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

import imperfectsilentart.martinfowler.uiArchs.util.TimeProcessingException;

/**
 * Domain object holding data of a concentration reading record.
 */
@Entity(name = "concentration_reading")
public class ConcentrationReading implements Comparable< ConcentrationReading >  {
	private long id;
	private MonitoringStation station;
	private LocalDateTime readingTimestamp;
	private int actualConcentration;
	
	public ConcentrationReading(){}
	
	public ConcentrationReading(final long id, final MonitoringStation station, final LocalDateTime readingTimestamp, final int actualConcentration){
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
	@Column(name="reading_timestamp", columnDefinition = "TIMESTAMP", nullable=false)
	public LocalDateTime getReadingTimestamp() {
		return readingTimestamp;
	}
	/**
	 * @param readingTimestamp the readingTimestamp to set
	 * @throws TimeProcessingException 
	 */
	public void setReadingTimestamp(LocalDateTime readingTimestamp){
		this.readingTimestamp = readingTimestamp;
	}
	// FIXME Check if "T"-separator with default formatter causes Problem when writing back to DB.
// Possible workaround if there's a problem: make readingTimestamp transient again and Type String. Use jata.time.* Types only within application, not in JPA-DB connection.
//	public void setReadingTimestamp(String readingTimestampText) throws TimeProcessingException {
//		this.readingTimestamp = TimeTools.parseReadingTimestamp(readingTimestampText);
//	}
	
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

	@Override
	public int compareTo(final ConcentrationReading o) {
		if( null == o.getReadingTimestamp() || null == this.readingTimestamp) return +1;
		return this.readingTimestamp.compareTo( o.getReadingTimestamp() );
	}
}
