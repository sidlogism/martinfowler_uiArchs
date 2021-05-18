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
package imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence;

import java.time.LocalDateTime;

/**
 * Domain object holding data of a concentration reading record.
 * 
 * @see imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.persistence.ConcentrationReading
 */
public class ConcentrationReading {
	private long id;
	private long stationForeignKey;
	private LocalDateTime readingTimestamp;
	private int actualConcentration;
	
	ConcentrationReading(final long id, final long stationForeignKey, final LocalDateTime readingTimestamp, final int actualConcentration){
		this.id = id;
		this.stationForeignKey = stationForeignKey;
		this.readingTimestamp = readingTimestamp;
		this.actualConcentration = actualConcentration;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @return the stationForeignKey
	 */
	public long getStationForeignKey() {
		return stationForeignKey;
	}
	
	/**
	 * @return the readingTimestamp
	 */
	public LocalDateTime getReadingTimestamp() {
		return readingTimestamp;
	}
	
	/**
	 * @return the actualConcentration
	 */
	public int getActualConcentration() {
		return actualConcentration;
	}
	
	@Override
	public String toString() {
		return "ConcentrationReading [id=" + id + ", stationForeignKey=" + stationForeignKey + ", readingTimestamp="
				+ readingTimestamp + ", actualConcentration=" + actualConcentration + "]";
	}
}
