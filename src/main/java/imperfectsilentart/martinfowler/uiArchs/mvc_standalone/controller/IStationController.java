package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.IMonitoringStationView;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.IReadingDataSheetView;

public interface IStationController {

	/**
	 * Initializes controllers (informs controllers about ther corresponding views) and initializes station view content.
	 * 
	 * @param stationViewController    the corresponding view of this controller
	 * @param stationViewController    the corresponding view of reading controller. Since the reading view is nested in other views and cannot be accessed directly before construction, the reading controller is informed about its corresponding view by the STATION controller.
	 * 
	 * @note    In this current sample implementation the station view object itself calls this method on the station controller in a bootstrapping fashion.
	 */
	public void initializeController(final IMonitoringStationView stationView, final IReadingDataSheetView readingView);

	/**
	 * Set new selection for monitoring station view.
	 * @note    This implicitly overwrites current selection status in UI.
	 * 
	 * @param newExternalId    new selection for monitoring station view.
	 */
	public void overwriteUISelection(String newExternalId);
	
	/**
	 * Wipe any selection in station view.
	 */
	public void wipeSelection();

	/**
	 * Handle new selection in station view made by user.
	 * 
	 * @param newExternalId    new user selection in monitoring station view.
	 */
	public void handleUserChangedSelection(String newExternalId);

}