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
package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model;

import java.util.List;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;

/**
 * Business logic for accessing and processing all data related to monitoring stations.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.MonitoringStationDao
 */
public interface IStationModel {
	/**
	 * Loads the monitoring station with the given external ID from the database.
	 * 
	 * @param stationExternalId    external ID of relevant monitoring station
	 * @return domain object of relevant monitoring station. null if the query result is empty.
	 * @throws ModelPersistenceException
	 */
	public MonitoringStation getStation(final String stationExternalId) throws ModelPersistenceException;

	/**
	 * @return Container holding the String representation of every monitoring station record.
	 * @throws ModelPersistenceException
	 */
	public List<MonitoringStation> findAll() throws ModelPersistenceException;
}