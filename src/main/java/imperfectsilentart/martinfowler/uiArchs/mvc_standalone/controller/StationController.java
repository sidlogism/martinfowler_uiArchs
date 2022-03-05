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

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.IStationModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.StationModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.IReadingView;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.IStationView;

/**
 * Controller handling user actions in views related to monitoring stations.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.MonitoringStationList
 */
public class StationController implements IStationController{
	private static final Logger logger = Logger.getLogger(StationController.class.getName());
	private IStationModel model = null;
	private IStationView view = null;
	/**
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 * 
	 * @see #initializeController
	 */
	private IReadingController readingController = null;
	

	public StationController() {
		logger.log(Level.INFO, "station controller ctor");
		this.model = new StationModel();
	}
	

	@Override
	public void initializeController(final IStationView stationView, final IReadingView readingView) {
		this.view = stationView;
		// Controllers are constructed in the same nested fashion as their views.
		this.readingController =  new ReadingController(this, readingView, model);
		
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
			logger.log(Level.SEVERE, "Failed to load station-related data from DB.", e);
		}
		this.view.overwriteUIStationList(stationIdentifyers);
	}
	
	@Override
	public void overwriteUISelection(final String newExternalId) {
		this.view.overwriteUISelection(newExternalId);
	}
	
	@Override
	public void wipeSelection() {
		this.view.wipeSelection();
	}
	
	@Override
	public void handleUserChangedSelection(final String newExternalId) {
		//IMPORTANT: For keeping station view and reading view in sync, also inform reading controller.
		this.readingController.handleUserChangedStationExtId( newExternalId );
	}
}
