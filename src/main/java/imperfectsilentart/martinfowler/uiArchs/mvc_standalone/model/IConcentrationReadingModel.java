package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ConcentrationReading;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;

public interface IConcentrationReadingModel {
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