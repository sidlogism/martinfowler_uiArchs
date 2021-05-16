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

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ConcentrationReading;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PersistenceTools;

/**
 * Business logic for accessing and processing all data related to concentration readings.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.ConcentrationReadingDao
 */
public class ConcentrationReadingModel implements IConcentrationReadingModel {
	private static final Logger logger = Logger.getLogger(ConcentrationReadingModel.class.getName());
	/**
	 * Updates actual concentration value of current reading record.
	 * 
	 * @throws ModelPersistenceException 
	 */
	@Override
	public synchronized void updateActualConcentration(final int newConcentrationValue, final long readingId) throws ModelPersistenceException {
		if(readingId < 0) return;

		EntityManager em = null;
		ConcentrationReading updatedReading = null;
		try {
			em = PersistenceTools.getEntityManager();
			em.getTransaction().begin();
			updatedReading = em.find( ConcentrationReading.class, Long.valueOf(readingId) );
			updatedReading.setActualConcentration(newConcentrationValue);
			// FIXME update timestamp
			em.persist(updatedReading);
			em.getTransaction().commit();
		} catch (ModelPersistenceException | PersistenceException e) {
			if(null != em && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}else {
				throw new ModelPersistenceException("Could not roll back failed update of "+ConcentrationReading.class.getName()+" record with ID: "+readingId+". Persistence manager or transaction is in invalid state.", e);
			}
			throw new ModelPersistenceException("Error while updating "+ConcentrationReading.class.getName()+" with ID: "+readingId+".", e);
		}finally {
			if( null != em && em.getEntityManagerFactory().isOpen() ) em.getEntityManagerFactory().close();
		}
	}
	
	/**
	 * Loads the youngest concentration reading record belonging to the monitoring station with the given ID from persistence layer.
	 * 
	 * @param internalStationId    ID of relevant monitoring station
	 * @return domain object of relevant reading record. null if the query result is empty.
	 * @throws ModelPersistenceException
	 */
	@Override
	public synchronized ConcentrationReading getLatestConcentrationReading(final long internalStationId) throws ModelPersistenceException {
		/*
		 * IMPORTANT: Don't filter by timestamp-value only because it is not always a unique value.
		 * 
		 * Retrieving all reading records related to the given station ID, then order descending by timestamp and get the first record.
		 * Equivalent constructs in other SQL dialects:
		 *     MySQL: "LIMIT 1"
		 *     Oracle SQL: "FETCH FIRST 1 ROW ONLY"
		 */
		final String queryText = 
			"FROM concentration_reading\n"
			+ "WHERE station = :station\n"
			+ "ORDER BY readingTimestamp DESC";

		ConcentrationReading result = null;
		EntityManager em = null;
		try {
			em = PersistenceTools.getEntityManager();
			em.getTransaction().begin();
			final TypedQuery<ConcentrationReading> query = em.createQuery( queryText, ConcentrationReading.class );
			query.setParameter("station", Long.valueOf(internalStationId) );
			query.setFirstResult(0);
			query.setMaxResults(1);
			result = query.getSingleResult();
			em.getTransaction().commit();
		} catch (ModelPersistenceException | PersistenceException e) {
			throw new ModelPersistenceException("Error while accessing or processing "+ConcentrationReading.class.getName()+" with station foreign key (station ID): "+internalStationId+". Query\n"+queryText, e);
		}finally {
			if( null != em && em.getEntityManagerFactory().isOpen() ) em.getEntityManagerFactory().close();
		}
		return result;
	}

}
