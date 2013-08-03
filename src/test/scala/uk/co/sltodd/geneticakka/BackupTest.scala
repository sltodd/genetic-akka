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

import org.junit.Assert._
import org.junit.Before
import javax.persistence.Persistence
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import org.hibernate.cfg.AnnotationConfiguration
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.junit.Test
import org.junit.After

class BackupTest {
  
  var sf : SessionFactory = _
  var ses : Session = _
  
  @Before
  def before() = {
    val config = new AnnotationConfiguration()
    config.addAnnotatedClass(classOf[PopulationBackup])
    .addAnnotatedClass(classOf[ChromosomeBackup])
    .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
    .setProperty("hibernate.connection.driver_class","org.h2.Driver")
    .setProperty("hibernate.connection.url", "jdbc:h2:mem")
    .setProperty("hibernate.hbm2ddl.auto", "create")
    .setProperty("hibernate.show_sql", "false");
    sf = config.buildSessionFactory()
    ses = sf.openSession()
    
    val pp = new PopulationBackup()
    val c0 = new ChromosomeBackup()
    c0.rank = 0
    c0.genes.add(0.1)
    c0.genes.add(0.2)
//    c0.populationBackup = pp
    val c1 = new ChromosomeBackup()
    c1.rank = 1
    c1.genes.add(0.3)
    c1.genes.add(0.4)
//    c1.populationBackup = pp
    pp.chromosomes.add(c0)
    pp.chromosomes.add(c1)
    pp.label = "testing"
      
    val tx = ses.beginTransaction()
    ses.persist(pp)
    tx.commit()
  }
  
  @Test
  def dataAccess() = {
    val pp2 = ses.get(classOf[PopulationBackup], 1l).asInstanceOf[PopulationBackup]
    assertEquals("testing", pp2.label)
    
    val a = ses.createQuery("from PopulationBackup").list.toArray().toList.map(_.asInstanceOf[PopulationBackup])
    assertEquals(1, a.size)
    val b = a(0).chromosomes.get(1)
    assertEquals(1, b.rank)
    assertEquals(0.4, b.genes.get(1), 1e-10)
  }
  
  @After
  def after() = {
    ses.close()
    sf.close()
  }
  
}