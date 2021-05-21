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

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ConcentrationReading;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;

public interface IReadingModel {
	/**
	 * Updates actual concentration value of current reading record.
	 * 
	 * @throws ModelPersistenceException 
	 */
	public void updateActualConcentration(final int newConcentrationValue, final long readingId) throws ModelPersistenceException;

	/**
	 * Loads the youngest concentration reading record belonging to the monitoring station with the given ID from persistence layer.
	 * 
	 * @param internalStationId    ID of relevant monitoring station
	 * @return domain object of relevant reading record. null if the query result is empty.
	 * @throws ModelPersistenceException
	 */
	public ConcentrationReading getLatestConcentrationReading(final long internalStationId) throws ModelPersistenceException;

}