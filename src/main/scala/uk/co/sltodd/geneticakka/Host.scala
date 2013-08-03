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
import org.apache.commons.math3.distribution.UniformRealDistribution
import org.apache.commons.math3.random.Well44497a

/**
 * Hosts candidates.  Once passed a <code>Chromosome</code> it will attempt to calculate the fitness and respond to the sender.
 */
trait Host extends Actor {

  /**
   * Entry point for communication. The host should be passed <code>Chromosome</code>s to evaluate.
   */
  def receive = {
    case x : Chromosome => sender ! Result(x, fitness(x))
    case x : Create => {
      val ch = create(x.seed)
      sender ! Result(ch, fitness(ch))
    }
    case _ => sender ! Failure
  }
  
  /**
   * Processes the <code>Chromosome</code> and returns a scalar value representing the fitness.
   */
  def fitness(chromesome : Chromosome) : Double
  
  /**
   * The length of the array underlying the genetic sequence.
   */
  val chromosomeSize : Int
  
  protected def create(seed : Int) : Chromosome = {
    val rng = new UniformRealDistribution(new Well44497a(seed), 0, 1, 0.000001)
    Chromosome(List.fill(chromosomeSize)(rng.sample))
  }
  
}
