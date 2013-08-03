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

import akka.testkit.TestActorRef
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.pattern.ask
import org.junit.Test
import akka.actor.ActorSystem
import akka.util.Timeout
import org.junit.Assert._

class HostTest {
	
	@Test
  def basicCandidate() = {
    
    class Cnd extends Host {
      def fitness(c : Chromosome) = {
        c.genes(0) - 10
      }
      
      val chromosomeSize = 1
    }
    
    implicit val system = ActorSystem()
    val worker = TestActorRef(new Cnd())
    val c = Chromosome(List(5))
    
    implicit val timeout = Timeout(1 second)
    val result = Await.result(worker ? c, timeout.duration).asInstanceOf[Result]
    val result2 = Await.result(worker ? (), timeout.duration)
    
    assertEquals("Incorrect fitness", -5.0, result.fitness, 1e-15)
    
    result2 match {
      case Failure => { }
      case _ => fail("Not receiving Failure class.")
    }
  }
  
}