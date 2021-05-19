package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;

import java.util.List;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller.IStationController;

public interface IMonitoringStationView {
	/**
	 * Inform station view about its corresponding controller.
	 * 
	 * @param controller the controller to set
	 */
	public void setStationController(final IStationController controller);
	
	/**
	 * Initialize list of monitoring stations.
	 * Currently the list contains only one string representing a monitoring station because there currently is no out-of-the-box list for multiple columns in JavaFX.
	 * 
	 * @param    stationIdentifyers    list of single strings representing a monitoring station each
	 */
	public void overwriteUIStationList(final List<String> stationIdentifyers);

	/**
	 * Set new selection for station view.
	 * 
	 * @param newExternalId    new selection for station view. Null is ignored. Use empty string instead.
	 */
	public void overwriteUISelection(final String newExternalId);

	/**
	 * Wipe any selection in station view.
	 */
	public void wipeSelection();
}