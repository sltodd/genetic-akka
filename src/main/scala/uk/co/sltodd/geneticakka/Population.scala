/*******************************************************************************
 * Copyright 2013 Simon Todd <simon@sltodd.co.uk>.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package uk.co.sltodd.geneticakka

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import org.apache.commons.math3.random.MersenneTwister
import org.apache.commons.math3.distribution.BetaDistribution
import org.apache.commons.math3.util.FastMath
import org.apache.commons.math3.stat.StatUtils
import scala.collection.mutable.ListBuffer
import com.typesafe.config.Config
import akka.dispatch.Dispatchers
import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer
import com.typesafe.config._
import akka.routing.BroadcastRouter
import scala.collection.mutable.SynchronizedQueue
import scala.reflect.ClassTag

/**
 * The central hub for managing evolution. Actor contains a a collection of candidate solutions and coordinates the
 * support Actors.
 * @constructor Creates a new population which will optimise solutions for the problem given by the type A.
 * @param fac A factory method to generate new instances of Actors of type A.
 * @param size The size of the population (fixed).
 * @param evolutionRate The steepness of the selection curve.
 * @param mutationRate The frequency that random mutations occur.
 */
class Population[A<:Host:ClassTag](
    fac: () => A, 
    size : Int, 
    evolutionRate : Double, 
    mutationRate : Double,
    seed : Int) extends Actor {
  
  def this(fac: () => A) = this(fac, 1000, 1.5, 0.1, 10000)

  val parallelism = ConfigFactory.load.getInt("akka.actor.host-dispatcher.workers")
  protected val population = new SynchronizedQueue[Result]
  protected val workers : List[ActorRef] = List.fill(parallelism)(context.actorOf(Props(fac()).withDispatcher("akka.actor.host-dispatcher")))
  protected val host = context.actorOf(Props().withRouter(BroadcastRouter(routees = workers)))
  protected val breeder = context.actorOf(Props[Breeder], "breeder")
  protected val rng = new SeedFactory(seed)
  protected var running = false
  
  def receive = {
    case Ping => sender ! Pong
    case Start => start
    case Stop => running = false
    case GetBest => sender ! sizeCheck(x => best)
    case GetPopulationStats => sender ! sizeCheck(x => populationStats)
    case GetAllSolutions => sender ! population.clone
    case ExportPopulation => sender ! export
    case InjectPopulation(bkp) => load(bkp)
    case ch : Chromosome => host ! ch
    case result : Result => processResult(result)
  }
  
  protected def processResult(result : Result) = {
    population += result
    while (population.size > size)
      population.dequeue()
    if (running)
    	breeder ! mkBreedingPair()
  }
  
  protected def start() = {
    running = true
    for (x <- 0 until size - population.size)
    	host ! Create(rng.next())
    if (population.size > 0)
      for (x <- 0 until population.size)
        breeder ! mkBreedingPair()
  }
  
  protected def sorted = population.toList.sortBy(_.fitness).reverse
  
  protected def sizeCheck(x : Any => Any) : Any = population.size match {
    case 0 => Failure
    case _ => x()
  }
  
  protected def best = sorted.head
  
  protected def meanFitness = StatUtils.mean(population.map(_.fitness).toArray)
  
  protected def varianceOfFitness = StatUtils.variance(population.map(_.fitness).toArray)

  protected def populationStats = PopulationStats(population.size, best, meanFitness, varianceOfFitness)
  
  protected def mkBreedingPair() : BreedingPair = {
    val dist = new BetaDistribution(new MersenneTwister(rng.next()), 1, evolutionRate, 0.000001)
    val srt = sorted
    val samples = List(dist.sample(), dist.sample()).map(x => srt(FastMath.round(x*(population.size-1d)).toInt).chromesome)
    BreedingPair(samples(0), samples(1), mutationRate, rng.next())
  }    
  
  protected def export() = {
    val bkp = new PopulationBackup()
    bkp.chromosomes = JavaConversions.seqAsJavaList(for (x <- 0 until population.size) 
      yield new ChromosomeBackup(x, JavaConversions.seqAsJavaList(population(x).chromesome.genes.map(_.asInstanceOf[java.lang.Double]))))
    bkp
  }
  
  protected def load(pop : PopulationBackup) = {
    running = true
    val shortfall = size - pop.chromosomes.size - population.size
    for (x <- 1 to shortfall)
      host ! Create(rng.next)
    val chr = JavaConversions.asScalaBuffer(pop.chromosomes).toList.sortBy(_.rank).map(x => JavaConversions.asScalaBuffer(x.genes).map(_.asInstanceOf[Double]).toList)
    chr.foreach(host ! Chromosome(_))
  }
  
}