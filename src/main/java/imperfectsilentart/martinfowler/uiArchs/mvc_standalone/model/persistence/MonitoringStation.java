/*
 * Copyright 2021 Imperfect Silent Art
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Domain object holding data of a monitoring station record.
 */
@Entity
public class MonitoringStation {
	private long id;
	private String stationExternalId;
	private String stationName;
	private int targetConcentration;
	private ArrayList<ConcentrationReading> readings;
	
	MonitoringStation(){
		this.readings = new ArrayList<ConcentrationReading>();
	}
	
	MonitoringStation(final long id, final String stationExternalId, final String stationName, final int targetConcentration){
		this.id = id;
		this.stationExternalId = stationExternalId;
		this.stationName = stationName;
		this.targetConcentration = targetConcentration;
		this.readings = new ArrayList<ConcentrationReading>();
	}
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	
	/**
	 * @return the stationExternalId
	 */
	public String getStationExternalId() {
		return stationExternalId;
	}
	
	/**
	 * @return the stationName
	 */
	public String getStationName() {
		return stationName;
	}
	
	/**
	 * @return the targetConcentration
	 */
	public int getTargetConcentration() {
		return targetConcentration;
	}
	
	/**
	 * @return the readings
	 */
	@OneToMany(mappedBy = "stationForeignKey")
	public ArrayList<ConcentrationReading> getReadings() {
		return readings;
	}
	
//	public ArrayList<MonitoringStation> getAll() {
//		return readings;
//	}
	
	@Override
	public String toString() {
		return "MonitoringStation [id=" + id + ", stationExternalId=" + stationExternalId + ", stationName="
				+ stationName + ", targetConcentration=" + targetConcentration + "]";
	}
}
