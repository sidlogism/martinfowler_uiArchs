package imperfectsilentart.martinfowler.uiArchs.model2_passive_view.controller;

import java.net.URL;
import java.util.ResourceBundle;

import imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.persistence.ModelPersistenceException;
import imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.persistence.MonitoringStation;
import javafx.beans.value.ObservableValue;

public interface IMonitoringStationController {

	void initialize(URL url, ResourceBundle resources);

	/**
	 * Set new selection for monitoring station list.
	 * 
	 * @param newExternalId    new selection for monitoring station list. Null is ignored. Use empty string instead.
	 */
	public void changeSelection(final String newExternalId);

	/**
	 * Wipe any selection in monitoring station list.
	 * 
	 * @param newExternalId    new selection for monitoring station list. Null is ignored. Use empty string instead.
	 */
	public void wipeSelection();

	/**
	 * Forwards the monitoring station with the given external ID from the model.
	 * 
	 * @param stationExternalId    external ID of relevant monitoring station
	 * @return domain object of relevant monitoring station. null if the query result is empty.
	 * @throws ModelPersistenceException
	 */
	public MonitoringStation getStation(final String stationExternalId) throws ModelPersistenceException;

	/**
	 * ChangeListener callback
	 *     if selection in station list of station view changed
	 *     or if text field "Station ID" in reading view changed.
	 * The record entry "Station ID" of reading view can be modified initiate an internal search for the corresponding ice cream reading record.
	 */
	public void changed(ObservableValue<? extends String> observable, String oldStationValue, String newStationName);

}