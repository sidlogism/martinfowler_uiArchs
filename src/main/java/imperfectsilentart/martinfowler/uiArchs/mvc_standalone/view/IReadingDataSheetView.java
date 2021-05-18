package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;

import java.net.URL;
import java.util.ResourceBundle;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import javafx.beans.value.ObservableValue;

public interface IReadingDataSheetView {

	void initialize(URL location, ResourceBundle resources);

	/**
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 * 
	 * @param stationController    StationController to be set at initialization.
	 * TODO too much cohesion: cannot use interface IMonitoringStationController because of listener registration.
	 */
	public void setStationController(final MonitoringStationView stationController);

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