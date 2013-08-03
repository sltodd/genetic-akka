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

import org.apache.commons.math3.random.SynchronizedRandomGenerator
import org.apache.commons.math3.random.Well44497a
import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.random.MersenneTwister

/**
 * Creates seeds and seeded random number generators.
 */
class SeedFactory(val seed : Int) {

  private val seedFactory = new SynchronizedRandomGenerator(new Well44497a(seed))
  
  /**
   * Returns the next N(0,1) generator from the factory.
   */
  def nextNormal() : NormalDistribution = new NormalDistribution(new MersenneTwister(next()), 0, 1, 0.000001)
  
  def next() = seedFactory.nextInt()
  
}