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
package imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.persistence;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Domain object holding data of a monitoring station record.
 * 
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.MonitoringStation
 */
@Entity(name = "Model2MonitoringStation")
@Table(name = "monitoring_station")
public class MonitoringStation implements Comparable< MonitoringStation >{
	private long id;
	private String stationExternalId;
	private String stationName;
	private int targetConcentration;
	private Collection<ConcentrationReading> readings;
	
	public MonitoringStation(){
		this.readings = new HashSet<ConcentrationReading>();
	}
	
	public MonitoringStation(final long id, final String stationExternalId, final String stationName, final int targetConcentration){
		this.id = id;
		this.stationExternalId = stationExternalId;
		this.stationName = stationName;
		this.targetConcentration = targetConcentration;
		this.readings = new HashSet<ConcentrationReading>();
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
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @return the stationExternalId
	 */
	@Column(name="station_external_id", nullable=false, unique = true)
	public String getStationExternalId() {
		return stationExternalId;
	}
	/**
	 * @param stationExternalId the stationExternalId to set
	 */
	public void setStationExternalId(String stationExternalId) {
		this.stationExternalId = stationExternalId;
	}

	/**
	 * @return the stationName
	 */
	@Column(name="station_name")
	public String getStationName() {
		return stationName;
	}
	
	/**
	 * @param stationName the stationName to set
	 */
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
	/**
	 * @return the targetConcentration
	 */
	@Column(name="target_concentration", nullable=false)
	public int getTargetConcentration() {
		return targetConcentration;
	}
	/**
	 * @param targetConcentration the targetConcentration to set
	 */
	public void setTargetConcentration(int targetConcentration) {
		this.targetConcentration = targetConcentration;
	}
	
	/**
	 * @return the readings
	 */
	@OneToMany(mappedBy = "station")
	public Collection<ConcentrationReading> getReadings() {
		return readings;
	}
	
	/**
	 * @param readings the readings to set
	 */
	public void setReadings(Collection<ConcentrationReading> readings) {
		this.readings = readings;
	}
	
	@Override
	public String toString() {
		return "MonitoringStation [id=" + id + ", stationExternalId=" + stationExternalId + ", stationName="
				+ stationName + ", targetConcentration=" + targetConcentration + "]";
	}

	@Override
	public int compareTo(MonitoringStation o) {
		return Long.valueOf( this.id - o.getId() ).intValue();
	}
}
