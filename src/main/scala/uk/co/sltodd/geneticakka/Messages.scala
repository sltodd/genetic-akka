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

/**
 * Instructs a <code>Host</code> to create a new random <code>Chromosome</code>.
 */
case class Create(seed : Int)

/**
* Retrieves all of the current candidate solutions. 
*/
case object GetAllSolutions

/**
 * Asks the Population to return the latest best solution.
 */
case object GetBest 

/**
 * Asks the Population to return key statistics on itself.
 */
case object GetPopulationStats

/**
 * Asks the Population to return a <code>PopulationBackup</code> 
 */
case object ExportPopulation

/**
 * Returned by <code>Host</code> instances when they are unable to recognise messages.
 * Its only purposes is to provide type safety.
 */
case object Failure

/**
 * Can be used be actors to ping each other. 
 */
case object Ping

/**
 * Response to a <code>Ping</code>.
 */
case object Pong

/**
 * Instructs the Population to begin evolving.
 */
case object Start

/**
 * Instructs the Population to halt.
 */
case object Stop 