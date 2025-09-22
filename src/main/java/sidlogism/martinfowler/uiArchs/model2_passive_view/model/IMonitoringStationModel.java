package sidlogism.martinfowler.uiArchs.model2_passive_view.model;

import java.util.List;

import sidlogism.martinfowler.uiArchs.model2_passive_view.model.persistence.ModelPersistenceException;
import sidlogism.martinfowler.uiArchs.model2_passive_view.model.persistence.MonitoringStation;

/**
 * Business logic for accessing and processing all data related to monitoring stations.
 * @see sidlogism.martinfowler.uiArchs.formsandcontrols.persistence.MonitoringStationDao
 */
public interface IMonitoringStationModel {
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