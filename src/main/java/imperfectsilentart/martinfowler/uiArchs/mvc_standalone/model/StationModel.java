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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PersistenceTools;
/**
 * Business logic for accessing and processing all data related to monitoring stations.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.MonitoringStationDao
 */
public class StationModel implements IStationModel {
	private static final Logger logger = Logger.getLogger(StationModel.class.getName());
	
	@Override
	public synchronized MonitoringStation getStation(final String stationExternalId) throws ModelPersistenceException {
		final String query = "FROM MonitoringStation WHERE stationExternalId = :id";

		MonitoringStation result = null;
		EntityManager em = null;
		try {
			em = PersistenceTools.getEntityManager();
			em.getTransaction().begin();
			result = em.createQuery( query, MonitoringStation.class )
				.setParameter("id", stationExternalId)
				.getSingleResult();
			em.getTransaction().commit();
		} catch (ModelPersistenceException | PersistenceException e) {
			// TODO resource leak on exception: [JavaFX Application Thread] ERROR org.hibernate.orm.connections.pooling - Connection leak detected: there are 1 unclosed connections upon shutting down pool jdbc:...
			throw new ModelPersistenceException("Error while accessing or processing "+MonitoringStation.class.getName()+" with external ID: "+stationExternalId+". Query:\n"+query, e);
		}finally {
			if( null != em && em.getEntityManagerFactory().isOpen() ) em.getEntityManagerFactory().close();
		}

		return result;
	}
	
	@Override
	public synchronized List<MonitoringStation> findAll() throws ModelPersistenceException {
		final String query = "FROM MonitoringStation ORDER BY id ASC";
		
		List<MonitoringStation> result = null;
		EntityManager em = null;
		try {
			em = PersistenceTools.getEntityManager();
			em.getTransaction().begin();
			result = em.createQuery( query, MonitoringStation.class ).getResultList();
			em.getTransaction().commit();
		} catch (ModelPersistenceException | PersistenceException e) {
			throw new ModelPersistenceException("Error while accessing or processing all "+MonitoringStation.class.getName()+". Query\n"+query, e);
		}finally {
			if( null != em && em.getEntityManagerFactory().isOpen() ) em.getEntityManagerFactory().close();
		}
		return result;
	}
}
