/*
 * Copyright 2025 Sidlogism
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
package sidlogism.martinfowler.uiArchs.mvc_standalone.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.PersistenceException;

import sidlogism.martinfowler.uiArchs.mvc_standalone.model.IReadingModel;
import sidlogism.martinfowler.uiArchs.mvc_standalone.model.IReadingModelDataProvider;
import sidlogism.martinfowler.uiArchs.mvc_standalone.model.IStationModel;
import sidlogism.martinfowler.uiArchs.mvc_standalone.model.ReadingModel;
import sidlogism.martinfowler.uiArchs.mvc_standalone.model.persistence.ConcentrationReading;
import sidlogism.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import sidlogism.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import sidlogism.martinfowler.uiArchs.mvc_standalone.view.IReadingView;
import sidlogism.martinfowler.uiArchs.mvc_standalone.view.ReadingView;



/**
 * Controller handling user actions in views related to readings.
 * @see sidlogism.martinfowler.uiArchs.formsandcontrols.ReadingDataSheet
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
			// down-casting here in order to avoid MVC interfaces inheriting from observer interfaces
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

		/*
		 * After initial loading of station list selection may be empty => newStationExternalId may be NULL
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
			// mark new content of station external ID as VALID
			this.view.markUIStationExternalIdValid();
			this.view.restoreEditabilityOnAllDependentTextFields();
		} catch (ModelPersistenceException | PersistenceException e) {
			logger.log(Level.WARNING, "Failed to lookup station with given external station ID \""+newStationExternalId+"\".", e);
			/*
			 * If there is a problem with the new station, wipe all dependent text fields and selections to indicate error.
			 */
			this.view.wipeAllDependentTextFields();
			this.stationController.wipeSelection();
			this.view.removeEditabilityFromAllDependentTextFields();
			// mark new content of station external ID as INVALID
			this.view.markUIStationExternalIdErroneous();
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
		 * Load and display data depending on current reading record.
		 * If the current monitoring station has no corresponding concentration readings, the depending data fields remain empty.
		 * Insertion of new concentration readings is currently not supported by the UI.
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
			this.view.removeEditabilityFromReadingDependentTextFields();
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
			
			try {
				model.updateActualConcentration(newValue, currentReadingId);
				// mark new content of actual concentration as VALID
				this.view.markUIActualConcentrationValid();
			} catch (ModelPersistenceException | PersistenceException e) {
				logger.log(Level.WARNING, "Failed to update the actual concentration value in the database. Given actual value was \""+newValue+"\".", e);
				this.view.markUIActualConcentrationErroneous();
			}
		}catch(NumberFormatException e) {
			logger.log(Level.WARNING, "The given value \""+newActualValue+"\" is no integer number. Expecting integer value for the actual concentration value.", e);
			// mark new content of actual concentration as INVALID
			this.view.markUIActualConcentrationErroneous();
		}
		
	}
}
