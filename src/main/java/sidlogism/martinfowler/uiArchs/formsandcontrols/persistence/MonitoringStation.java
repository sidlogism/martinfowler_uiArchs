/*
 * Copyright 2025 Sidlogism
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
package sidlogism.martinfowler.uiArchs.formsandcontrols.persistence;

/**
 * Domain object holding data of a monitoring station record.
 * 
 * @see sidlogism.martinfowler.uiArchs.model2_passive_view.model.persistence.MonitoringStation
 */
public class MonitoringStation {
	private long id;
	private String stationExternalId;
	private String stationName;
	private int targetConcentration;
	
	MonitoringStation(final long id, final String stationExternalId, final String stationName, final int targetConcentration){
		this.id = id;
		this.stationExternalId = stationExternalId;
		this.stationName = stationName;
		this.targetConcentration = targetConcentration;
	}
	
	/**
	 * @return the id
	 */
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
	
	@Override
	public String toString() {
		return "MonitoringStation [id=" + id + ", stationExternalId=" + stationExternalId + ", stationName="
				+ stationName + ", targetConcentration=" + targetConcentration + "]";
	}
}
