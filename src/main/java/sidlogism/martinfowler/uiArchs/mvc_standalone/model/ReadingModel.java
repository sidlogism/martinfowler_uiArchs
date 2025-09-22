/*
 * Copyright 2025 Sidlogism
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
package sidlogism.martinfowler.uiArchs.mvc_standalone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import sidlogism.martinfowler.uiArchs.mvc_standalone.model.persistence.ConcentrationReading;
import sidlogism.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import sidlogism.martinfowler.uiArchs.mvc_standalone.model.persistence.PersistenceTools;

/**
 * Business logic for accessing and processing all data related to concentration readings.
 * @see sidlogism.martinfowler.uiArchs.formsandcontrols.persistence.ConcentrationReadingDao
 */
public class ReadingModel implements IReadingModel, IReadingModelDataProvider {
	private static final Logger logger = Logger.getLogger(ReadingModel.class.getName());
	private List<IReadingModelListener> observers = null;
	
	public ReadingModel(){
		this.observers = new ArrayList<IReadingModelListener>();
	}
	
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
		
		/*
		 * rudimentary observer pattern: notify model observers about model change
		 */
		notifyReadingModelListeners(updatedReading);
	}
	
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
			"FROM ConcentrationReading\n"
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
			throw new ModelPersistenceException("Error while accessing or processing "+ConcentrationReading.class.getName()+" with station foreign key (station ID): "+internalStationId+". Query:\n"+queryText, e);
		}finally {
			if( null != em && em.getEntityManagerFactory().isOpen() ) em.getEntityManagerFactory().close();
		}
		return result;
	}

	@Override
	public void addReadingModelListener(IReadingModelListener listener) {
		logger.log(Level.INFO, "Adding new listener: "+listener);
		this.observers.add(listener);
	}

	@Override
	public void removeReadingModelListener(IReadingModelListener listener) {
		this.observers.remove(listener);
	}

	@Override
	public synchronized void notifyReadingModelListeners(final ConcentrationReading reading) {
		/*
		 * note: currently the only user-changeable field is the actual concentration.
		 * FIXME: hand over the entire updated object.
		 */
		logger.log(Level.FINE, "Reading entity was updated. Changed reading tuple: "+reading);
		for(final IReadingModelListener listener: this.observers) {
			listener.actualConcentrationChanged( reading.getActualConcentration() );
		}
	}
}
