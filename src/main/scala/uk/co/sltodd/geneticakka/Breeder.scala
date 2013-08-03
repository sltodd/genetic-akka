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
import org.apache.commons.math3.distribution.BinomialDistribution
import org.apache.commons.math3.random.Well44497a
import org.apache.commons.math3.distribution.UniformRealDistribution

/**
 * Breeds parents together with an even probability of the child inheriting each gene.
 * Random numbers are generated from a binomial distribution with the given seed.
 */
class Breeder extends Actor {
  
  def receive = {
    case parents : BreedingPair => {
      require(parents.x.genes.size == parents.y.genes.size)
      val r = new Well44497a(parents.seed)
    	val rng = new BinomialDistribution(r, 1, 0.5)
      val rng2 = new BinomialDistribution(r, 1, parents.mutationRate)
    	val urng = new UniformRealDistribution(r, 0d, 1d)     
      val genes = for ((x,y) <- parents.x.genes.zip(parents.y.genes)) yield (rng.sample, rng2.sample()) match {
        case (0, 0) => x
        case (0, 1) => y
        case (1, _) => urng.sample()
      }
    }
  }

}