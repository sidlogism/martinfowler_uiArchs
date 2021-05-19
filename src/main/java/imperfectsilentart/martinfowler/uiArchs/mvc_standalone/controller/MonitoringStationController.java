/*
 * Copyright 2021 Imperfect Silent Art
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
package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.IMonitoringStationModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.MonitoringStationModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.IMonitoringStationView;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.IReadingDataSheetView;
import javafx.fxml.FXML;

/**
 * Controller handling user actions in views related to monitoring stations.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.MonitoringStationList
 */
public class MonitoringStationController implements IStationController{
	private static final Logger logger = Logger.getLogger(MonitoringStationController.class.getName());
	private IMonitoringStationModel model = null;
	private IMonitoringStationView view = null;
	/**
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 * 
	 * @see #initializeController
	 */
	private IReadingController readingController = null;
	

	public MonitoringStationController() {
		logger.log(Level.INFO, "station controller ctor");
		this.model = new MonitoringStationModel();
	}
	
	/**
	 * Initializes controllers (informs controllers about their corresponding views) and initializes station view content.
	 * 
	 * @param stationView    the corresponding view of this controller
	 * @param readingView    the corresponding view of reading controller. Since the reading view is nested in other views and cannot be accessed directly before construction, the reading controller is informed about its corresponding view by the STATION controller.
	 * 
	 * @note    In this current sample implementation the station view object itself calls this method on the station controller in a bootstrapping fashion.
	 */
	@Override
	public void initializeController(final IMonitoringStationView stationView, final IReadingDataSheetView readingView) {
		this.view = stationView;
		// Controllers are constructed in the same nested fashion as their views.
		this.readingController =  new ReadingDataSheetController(this, readingView, model);
		
		/*
		 * Initialize list of monitoring stations.
		 * Currently the list contains only one single string representing a monitoring station because there currently is no out-of-the-box list for multiple columns in JavaFX.
		 */
		List<String> stationIdentifyers = null;
		try {
			final List<MonitoringStation> stations = model.findAll();
			stationIdentifyers = stations.stream().map(MonitoringStation::getStationExternalId).collect( Collectors.toList() );
		} catch (ModelPersistenceException | PersistenceException e) {
			stationIdentifyers = new ArrayList<String>();
			stationIdentifyers.add("- data access error -");
			logger.log(Level.SEVERE, "Failed to load data from DB.", e);
		}
		this.view.overwriteUIStationList(stationIdentifyers);
	}
	
	/**
	 * Set new selection for monitoring station view.
	 * @note    This implicitly overwrites current selection status in UI.
	 * 
	 * @param newExternalId    new selection for monitoring station view.
	 */
	@Override
	public void overwriteUISelection(final String newExternalId) {
		this.view.overwriteUISelection(newExternalId);
	}
	
	/**
	 * Wipe any selection in station view.
	 */
	@Override
	public void wipeSelection() {
		this.view.wipeSelection();
	}
	
	/**
	 * Handle new selection in station view made by user.
	 * 
	 * @param newExternalId    new user selection in monitoring station view.
	 */
	@Override
	public void handleUserChangedSelection(final String newExternalId) {
		//IMPORTANT: For keeping station view and reading view in sync, also inform reading controller.
		// FIXME prevent endless update cycle
		this.readingController.handleUserChangedStationExtId( newExternalId );
	}
}
