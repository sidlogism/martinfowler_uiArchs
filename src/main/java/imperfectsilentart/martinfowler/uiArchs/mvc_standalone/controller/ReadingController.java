/*
 * Copyright 2021 Imperfect Silent Art
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.IReadingModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.IReadingModelDataProvider;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.IStationModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.ReadingModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ConcentrationReading;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.IReadingView;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.ReadingView;



/**
 * Controller handling user actions in views related to readings.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.ReadingDataSheet
 */
public class ReadingController implements IReadingController {
	private static final Logger logger = Logger.getLogger(ReadingController.class.getName());
	private IReadingModel model = null;
	private IStationModel stationModel = null;
	private IReadingView view;
	/**
	 * see constructor
	 */
	private IStationController stationController;
	
	/**
	 * Constructor for reading controller
	 * 
	 * @param view    the corresponding view of this controller
	 * @param stationController    For keeping station view and reading view in sync, the corresponding controllers must know each other. This link is established here. Controllers are constructed in the same nested fashion as their views.
	 * @param model    station model. Reading view also contains contents about monitoring stations.
	 * 
	 * @see #handleUserChangedStationExtId
	 */
	public ReadingController(final IStationController stationController, final IReadingView view, final IStationModel stationModel) {
		logger.log(Level.INFO, "reading controller ctor");
		this.model = new ReadingModel();
		this.view = view;
		if(this.model instanceof IReadingModelDataProvider && this.view instanceof ReadingView) {
			// down-casting here in order to avoid with MVC interfaces inheriting from observer interfaces
			( (ReadingModel)this.model ).addReadingModelListener( (ReadingView)this.view );
		}
		/*
		 * Inform reading view about its corresponding controller:
		 * Since the reading view is nested in other views and cannot be accessed directly before construction, the reading controller reference is handed to it here.
		 */
		this.view.setReadingController( this );
		this.stationController = stationController;
		this.stationModel = stationModel;
	}

	@Override
	public void handleUserChangedStationExtId(final String newStationExternalId) {
		/*
		 * Changing the station external ID implies a switch of the selected monitoring station.
		 * Thus all contents of reading data sheet must be updated.
		 */
		if(null == newStationExternalId || null == this.stationModel) {
			return;
		}
		
		/*
		 * load and display data depending on current monitoring station
		 */
		MonitoringStation station = null;
		try {
			station = this.stationModel.getStation(newStationExternalId);
			if( null == station ) {
				throw new ModelPersistenceException("There is no station with given external station ID \""+newStationExternalId+"\".");
			}
		} catch (ModelPersistenceException | PersistenceException e) {
			logger.log(Level.WARNING, "Failed to lookup station with given external station ID \""+newStationExternalId+"\".", e);
			/*
			 * If there is a problem with the new station, wipe alls dependennt text fields and selections to indicate error.
			 */
			this.view.wipeAllDependentTextFields();
			this.stationController.wipeSelection();
			return;
		}
		if( newStationExternalId != this.view.getStationExternalId() ) {
			/*
			 * Hand new data to reading view if necessary.
			 * In order to avoid redundant handler calls and listener notifications, only update reading view if the value really changed.
			 * Also update station external ID because this update can also be triggered by station view instead of reading view.
			 */
			this.view.overwriteUIStationExternalId( newStationExternalId );
		}
		if( station.getTargetConcentration() != this.view.getTargetConcentration() ) {
			this.view.overwriteUITargetConcentration( station.getTargetConcentration() );
		}
		//IMPORTANT: For keeping station view and reading view in sync, also change station view.
		this.stationController.overwriteUISelection(newStationExternalId);
		
		/*
		 * load and display data depending on current reading record
		 */
		ConcentrationReading newRecord = null;
		try {
			newRecord = model.getLatestConcentrationReading( station.getId() );
			if( null == newRecord ) {
				throw new ModelPersistenceException("There doesn't exist any concentration reading for given station yet. Station: "+station);
			}
		} catch (ModelPersistenceException | PersistenceException e) {
			logger.log(Level.WARNING, "Failed to lookup concentration readings for given station. Station: "+station, e);
			// wipe text fields to indicate error
			this.view.wipeReadingDependentTextFields();
			return;
		}
		if( newRecord.getReadingTimestamp() != this.view.getReadingTimestamp() ) {
			this.view.overwriteUIReadingTimestamp( newRecord.getReadingTimestamp() );
		}
		if( newRecord.getActualConcentration() != this.view.getActualConcentration() ) {
			this.view.overwriteUIActualConcentration( newRecord.getActualConcentration() );
		}
		// update ID of currently displayed concentration reading record
		this.view.setCurrentReadingId( newRecord.getId() );
	}
	
	@Override
	public void handleUserChangedActualConcentration(final String newActualValue, final long currentReadingId) {
		// don't propagate null or empty value
		if( null == newActualValue || newActualValue.isEmpty() || newActualValue.isBlank() ) return;
		
		int newValue = -1;
		try {
			newValue = Integer.parseInt(newActualValue);
		}catch(NumberFormatException e) {
			logger.log(Level.WARNING, "The given value \""+newActualValue+"\" is no integer number. Expecting integer value for the actual concentration value.", e);
		}
		
		try {
			model.updateActualConcentration(newValue, currentReadingId);
		} catch (ModelPersistenceException | PersistenceException e) {
			logger.log(Level.WARNING, "Failed to update the actual concentration value in the database. Given actual value was \""+newValue+"\".", e);
		}
	}
}
