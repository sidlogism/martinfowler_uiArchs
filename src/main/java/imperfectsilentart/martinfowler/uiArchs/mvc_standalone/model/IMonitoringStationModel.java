package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model;

import java.util.List;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PeristenceException;

/**
 * Business logic for accessing and processing all data related to monitoring stations.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.MonitoringStationDao
 */
public interface IMonitoringStationModel {

	/**
	 * Loads the monitoring station with the given external ID from the database.
	 * 
	 * @param stationExternalId    external ID of relevant monitoring station
	 * @return domain object of relevant monitoring station. null if the query result is empty.
	 * @throws PeristenceException
	 */
	MonitoringStation getStation(String stationExternalId) throws PeristenceException;

	/**
	 * @return Container holding the String representation of every monitoring station record.
	 * @throws PeristenceException
	 */
	List<MonitoringStation> findAll() throws PeristenceException;
}