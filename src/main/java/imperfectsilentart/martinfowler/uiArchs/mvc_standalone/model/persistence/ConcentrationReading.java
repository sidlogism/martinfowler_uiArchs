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

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Domain object holding data of a concentration reading record.
 */
@Entity(name = "concentration_reading")
public class ConcentrationReading {
	private long id;
	private MonitoringStation station;
	private transient LocalDateTime readingTimestamp;
	private int actualConcentration;
	
	ConcentrationReading(){}
	
	ConcentrationReading(final long id, final MonitoringStation station, final LocalDateTime readingTimestamp, final int actualConcentration){
		this.id = id;
		this.station = station;
		this.readingTimestamp = readingTimestamp;
		this.actualConcentration = actualConcentration;
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
	 * @return the station
	 */
	@ManyToOne(targetEntity = MonitoringStation.class, optional = false)
	@JoinColumn(name="fk_station_id", columnDefinition = "BIGINT UNSIGNED", nullable=false, referencedColumnName = "id")
	public MonitoringStation getStation() {
		return station;
	}
	/**
	 * @param station the station to set
	 */
	public void setStation(MonitoringStation station) {
		this.station = station;
	}
	
	/**
	 * @return the readingTimestamp
	 */
	@Transient
	@Column(name="reading_timestamp", columnDefinition = "TIMESTAMP", nullable=false)
	public LocalDateTime getReadingTimestamp() {
		return readingTimestamp;
	}
	/**
	 * @param readingTimestamp the readingTimestamp to set
	 */
	public void setReadingTimestamp(LocalDateTime readingTimestamp) {
		this.readingTimestamp = readingTimestamp;
	}
	
	/**
	 * @return the actualConcentration
	 */
	@Column(name="actual_concentration", columnDefinition = "SMALLINT", nullable=false)
	public int getActualConcentration() {
		return actualConcentration;
	}
	/**
	 * @param actualConcentration the actualConcentration to set
	 */
	public void setActualConcentration(int actualConcentration) {
		this.actualConcentration = actualConcentration;
	}
	
	@Override
	public String toString() {
		return "ConcentrationReading [id=" + id + ", station=" + station + ", readingTimestamp="
				+ readingTimestamp + ", actualConcentration=" + actualConcentration + "]";
	}
}
