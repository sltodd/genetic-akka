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

package uk.co.sltodd.geneticakka;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class ChromosomeBackup {
	
	public ChromosomeBackup(long rank, List<Double> genes) {
		this.rank = rank;
		this.genes = genes;
	}
	
	public ChromosomeBackup() { }
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long id;
	
	@Column
	long rank;
	
	@ElementCollection
	List<Double> genes = new ArrayList<>();
	
}