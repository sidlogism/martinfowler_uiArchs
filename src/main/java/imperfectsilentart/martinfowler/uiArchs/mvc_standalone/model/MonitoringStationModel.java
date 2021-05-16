/*
 * Copyright 2021 Imperfect Silent Art
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *	 http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PersistenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PersistenceTools;
/**
 * Business logic for accessing and processing all data related to monitoring stations.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.MonitoringStationDao
 */
public class MonitoringStationModel implements IMonitoringStationModel {
	private static final Logger logger = Logger.getLogger(MonitoringStationModel.class.getName());
	
	/**
	 * Loads the monitoring station with the given external ID from the database.
	 * 
	 * @param stationExternalId    external ID of relevant monitoring station
	 * @return domain object of relevant monitoring station. null if the query result is empty.
	 * @throws PersistenceException
	 */
	@Override
	public synchronized MonitoringStation getStation(final String stationExternalId) throws PersistenceException {
		// TODO prepared statements?
		final String query = "FROM monitoring_station WHERE station_external_id = "+stationExternalId;

		MonitoringStation result = null;
		EntityManager em = null;
		try {
			em = PersistenceTools.getEntityManager();
			em.getTransaction().begin();
			result = em.createQuery( query, MonitoringStation.class ).getSingleResult();
			em.getTransaction().commit();
		} catch (PersistenceException e) {
			throw new PersistenceException("Error while accessing or processing "+MonitoringStation.class.getName()+" with external ID: "+stationExternalId+". Query\n"+query, e);
		}finally {
			if(null != em) em.close();
		}

		return result;
	}
	
	/**
	 * @return Container holding the String representation of every monitoring station record.
	 * @throws PersistenceException
	 */
	@Override
	public synchronized List<MonitoringStation> findAll() throws PersistenceException {
		// TODO prepared statements?
		final String query = "FROM monitoring_station ORDER BY id ASC";
		
		List<MonitoringStation> result = null;
		EntityManager em = null;
		try {
			em = PersistenceTools.getEntityManager();
			em.getTransaction().begin();
			result = em.createQuery( query, MonitoringStation.class ).getResultList();
			em.getTransaction().commit();
		} catch (PersistenceException e) {
			throw new PersistenceException("Error while accessing or processing all "+MonitoringStation.class.getName()+". Query\n"+query, e);
		}finally {
			if(null != em) em.close();
		}
		return result;
	}
}
