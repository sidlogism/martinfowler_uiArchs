package sidlogism.martinfowler.uiArchs.model2_passive_view.controller;

import java.net.URL;
import java.util.ResourceBundle;

import sidlogism.martinfowler.uiArchs.model2_passive_view.model.persistence.ModelPersistenceException;
import javafx.beans.value.ObservableValue;

public interface IReadingDataSheetController {

	void initialize(URL location, ResourceBundle resources);

	/**
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 * 
	 * @param stationController    StationController to be set at initialization.
	 * TODO too much coupling between reading controller and station controller: cannot use interface IMonitoringStationController because of listener registration.
	 */
	public void setStationController(final MonitoringStationController stationController);

	/**
	 * Set new value for "Station ID" text field.
	 * 
	 * @param newExternalId    new value for "Station ID" text field. Null is ignored. Use empty string instead.
	 * @return    boolean value indicating whether changing the currently displayed reading record was successful. True = success, false = failure.
	 * @throws ModelPersistenceException 
	 */
	public boolean switchContents(final String newExternalId);

	/**
	 * ChangeListener callback if text field "Actual" changed.
	 * The record entry "Actual" can be modified to change the entry "actual value" of the currently active ice cream concentration reading record.
	 */
	public void changed(ObservableValue<? extends String> observable, String oldActualValue, String newActualValue);

}