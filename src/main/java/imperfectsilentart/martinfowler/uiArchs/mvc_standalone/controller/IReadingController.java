package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;

public interface IReadingController {

	/**
	 * Handle change of UI element for station external ID in UI..
	 * 
	 * @param newStationExternalId    new value for "Station ID" text field. Null is ignored. Use empty string instead.
	 * @return    boolean value indicating whether changing the currently displayed reading record was successful. True = success, false = failure.
	 * @throws ModelPersistenceException
	 */
	public void handleUserChangedStationExtId(String newStationExternalId);

	/**
	 * Handle change of UI element for actual concentration.
	 * The record entry "Actual" can be modified to change the entry "actual concentration" of the currently active ice cream concentration reading record.
	 * 
	 * @param newActualValue    new value for "actual concentration" entry of current reading record
	 * @param currentReadingId    ID of currently displayed reading record
	 */
	public void handleUserChangedActualConcentration(String newActualValue, long currentReadingId);

}