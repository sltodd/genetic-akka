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

import java.util

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.apache.commons.math3.stat.StatUtils
import org.apache.commons.math3.util.FastMath
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class Cnd extends Host {
    def fitness(c : Chromosome) = {
      val e = StatUtils.mean(c.genes.toArray)
      val e2 = StatUtils.variance(c.genes.toArray)
      - FastMath.abs(e - 0.5) - e2
    }
    val chromosomeSize = 10
  }

@RunWith(classOf[JUnitRunner])
class PopulationTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike with MustMatchers with BeforeAndAfterAll {
 
  def this() = this(ActorSystem("test"))
 
  implicit val timeout = Timeout(1 second)
  
  override def afterAll() {
    system.shutdown()
  }  
  
  "Population" must {

    "return Pong following Ping" in {
    	val population = system.actorOf(Props(new Population(() => new Cnd())))
      val result = Await.result(population ? Ping, timeout.duration)
      result match {
        case Pong => assert(true)
        case _ => fail("Ping failed.")
      }
    
      population ! Ping
      expectMsg(Pong)
    }
    
    "find the best match correctly" in {
      val population = system.actorOf(Props(new Population[Cnd](() => new Cnd())))
      population ! Result(Chromosome(List(0, 1)), 1)
      population ! Result(Chromosome(List(1, 2)), 2)
      population ! GetBest
      expectMsg(Result(Chromosome(List(1, 2)), 2))
    }
    
    "retrieve the correct population stats" in {
      val population = system.actorOf(Props(new Population[Cnd](() => new Cnd())))
      population ! Result(Chromosome(List(0, 1)), 1)
      population ! Result(Chromosome(List(1, 2)), 2)
      population ! GetPopulationStats
      expectMsg(PopulationStats(2, Result(Chromosome(List(1, 2)), 2), 1.5, StatUtils.variance(Array(1, 2))))
    }
    
    "populate itself and improve solutions over time" in {
      val population = system.actorOf(Props(new Population[Cnd](() => new Cnd(), 50, 1.5, 0.01, 10000)))
      population ! Start
      Thread.sleep(100)
      val res = Await.result(population ? GetPopulationStats, timeout.duration)
      res match {
        case Failure => fail()
        case p : PopulationStats =>
          if (p.size == 0) fail("not populating")
      }
       
      Thread.sleep(100)
      val res2 = Await.result(population ? GetPopulationStats, timeout.duration).asInstanceOf[PopulationStats]
      if (res2.mean < res.asInstanceOf[PopulationStats].mean)
        fail("solutions not improving")
      Thread.sleep(50)
      val res3 = Await.result(population ? GetPopulationStats, timeout.duration).asInstanceOf[PopulationStats]
      if (res3.mean < res2.mean)
        fail("solutions not improving")
    }
    
    "return the full population" in {
      val population = system.actorOf(Props(new Population[Cnd](() => new Cnd(), 50, 1.5, 0.01, 10000)))
      population ! Start
      Thread.sleep(50)
      val res = Await.result(population ? GetAllSolutions, timeout.duration).asInstanceOf[mutable.Queue[Result]]
      assert(res.length == 50)
    }
    
    "import from backup" in {
      val population = system.actorOf(Props(new Population[Cnd](() => new Cnd(), 10, 1.5, 0.01, 10000)))
      
      val populationBackup = new PopulationBackup
      populationBackup.label = "temp"
      val chromosomeBackups : java.util.List[ChromosomeBackup] = new util.ArrayList[ChromosomeBackup]()
      for (x <- 1 until 10)
      chromosomeBackups.add({
        val c = new ChromosomeBackup
        c.rank = x; c.genes = new util.ArrayList[java.lang.Double]()
        val tmp = List.fill(10)(0.5d)
        for (y <- tmp)
          c.genes.add(new java.lang.Double(0.5))
        c 
        })
      population ! InjectPopulation(populationBackup)
      Thread.sleep(50)
      val res = Await.result(population ? GetPopulationStats, timeout.duration)
      population ! ExportPopulation
      val res2 = Await.result(population ? ExportPopulation, timeout.duration).asInstanceOf[PopulationBackup]
      assert(res2.chromosomes.size() == 10)      
    }
    
  }
    
}