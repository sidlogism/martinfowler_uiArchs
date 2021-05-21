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
package imperfectsilentart.martinfowler.uiArchs.model2_passive_view.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.ConcentrationReadingModel;
import imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.IConcentrationReadingModel;
import imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.persistence.ConcentrationReading;
import imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.persistence.ModelPersistenceException;
import imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.util.TimeTools;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;



/**
 * Business logic for accessing and processing all data related to ice cream reading records.
 * 
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.ReadingDataSheet
 */
public class ReadingDataSheetController implements Initializable, ChangeListener<String>, IReadingDataSheetController {
	private static final Logger logger = Logger.getLogger(ReadingDataSheetController.class.getName());
	private IConcentrationReadingModel model = null;
	/**
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 * 
	 * TODO too much coupling between reading controller and station controller: cannot use interface IMonitoringStationController because of listener registration in {@link #setStationController}.
	 * Doesn't suffice strategy pattern?
	 */
	private MonitoringStationController stationController = null;

	// ID of currently displayed concentration reading record
	private long concentrationReadingId = -1;
	
	// data depending on current monitoring station
	@FXML
	public TextField tfStationExternalId;
	@FXML
	public TextField tfTargetConcentration;
	// data depending on current reading record
	@FXML
	public TextField tfDate;
	@FXML
	public TextField tfActualConcentration;
	@FXML
	public TextField tfVariance;

	public ReadingDataSheetController() {
		logger.log(Level.FINE, "reading ctor");
		this.model = new ConcentrationReadingModel();
	}

	/**
	 * Called to initialize a controller after its root element has been
	 * completely processed.
	 *
	 * @param location
	 * The location used to resolve relative paths for the root object, or
	 * {@code null} if the location is not known.
	 *
	 * @param resources
	 * The resources used to localize the root object, or {@code null} if
	 * the root object was not localized.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.log(Level.FINE, "reading init");
		/*
		 * IMPORTANT: Register this controller as listener to value changes of the actual concentration text field.
		 * NOTE: The event handler approach doesn't apply here because events on text fields are only fired on pressing Enter.
		 */
		this.tfActualConcentration.textProperty().addListener(this);
	}
	
	/**
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 * 
	 * @param stationController    StationController to be set at initialization.
	 * 
	 * TODO too much coupling between reading controller and station controller: cannot use interface IMonitoringStationController because of listener registration.
	 */
	@Override
	public void setStationController(MonitoringStationController stationController) {
		this.stationController = stationController;
		if(null == this.tfStationExternalId) {
			logger.log(Level.WARNING, "Text field of station external id is not initialized yet. Failed to register station list controller as change listener to text field of station external id.");
			return;
		}
		/*
		 * IMPORTANT: Register station controller as listener to value changes of the station external ID text field.
		 * NOTE: The event handler approach doesn't apply here because events on text fields are only fired on pressing Enter.
		 */
		this.tfStationExternalId.textProperty().addListener(stationController);
	}

	/**
	 * Handle switch in selected monitoring station. All contents of reading data sheet must be updated.
	 * 
	 * @param newExternalId    new value for "Station ID" text field. Null is ignored. Use empty string instead.
	 * @return    boolean value indicating whether changing the currently displayed reading record was successful. True = success, false = failure.
	 * @throws ModelPersistenceException 
	 */
	@Override
	public synchronized boolean switchContents(final String newExternalId) {
		if(null == newExternalId || null == stationController) {
			return false;
		}
		
		/*
		 * load and display data depending on current monitoring station
		 */
		MonitoringStation station = null;
		try {
			station = stationController.getStation(newExternalId);
			if( null == station ) {
				throw new ModelPersistenceException("There is no station with given external station ID \""+newExternalId+"\".");
			}
		} catch (ModelPersistenceException | PersistenceException e) {
			logger.log(Level.WARNING, "Failed to lookup station with given external station ID \""+newExternalId+"\".", e);
			// wipe text fields to indicate error
			wipeAllDependentTextFields();
			return false;
		}
		this.tfStationExternalId.setText(newExternalId);
		this.tfTargetConcentration.setText( Integer.toString(station.getTargetConcentration()) );
		
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
			wipeReadingDependentTextFields();
			return false;
		}
		this.tfDate.setText( newRecord.getReadingTimestamp().format( TimeTools.getReadingTimestampFormat() ) );
		/*
		 * To avoid redundant listener updates temporarily unregister from changes of the actual concentration text field.
		 */
		this.tfActualConcentration.textProperty().removeListener(this);
		this.tfActualConcentration.setText( Integer.toString(newRecord.getActualConcentration()) );
		this.tfActualConcentration.textProperty().addListener(this);
		// update ID of currently displayed concentration reading record
		this.concentrationReadingId = newRecord.getId();
		updateVariance( newRecord.getActualConcentration(), station.getTargetConcentration() );
		return true;
	}
	
	/**
	 * Recomputes the concentration variance based on the given values.
	 * 
	 * @param actualConcentration    value of the actual concentration measured
	 * @param targetConcentration    value of the target concentration to be reached
	 */
	private void updateVariance(final int actualConcentration, final int targetConcentration) {
		final double variance = actualConcentration - targetConcentration;
		this.tfVariance.setText( Double.toString(variance) );
		
		// Important: use double to avoid integer arithmetics (rounding of decimal digits)
		double variancePercentage = ( variance / targetConcentration )*100;
		// remove sign from percentage, since not needed
		if( variancePercentage < 0) { variancePercentage *= -1; }
		
		/*
		 * Apply color code to variance text field.
		 * 
		 * I. e. calculate necessity of changing font color of text field. First set font color back to normal.
		 */
		this.tfVariance.setStyle("-fx-text-inner-color: black");
		if( variance < 0 && variancePercentage >= 10) {
			this.tfVariance.setStyle("-fx-text-inner-color: red");
		}else if( variance > 0 && variancePercentage >= 5) {
			this.tfVariance.setStyle("-fx-text-inner-color: green");
		}
	}

	/**
	 * Wipes all text fields which depend on external station ID.
	 * The current concentration reading record indirectly depends on the current monitoring station record.
	 */
	private void wipeAllDependentTextFields() {
		this.tfTargetConcentration.clear();
		wipeReadingDependentTextFields();
	}
	
	/**
	 * Wipes all text field which depend on the current concentration reading record.
	 */
	private void wipeReadingDependentTextFields() {
		this.tfDate.clear();
		this.tfActualConcentration.clear();
		this.tfVariance.clear();
	}

	/**
	 * ChangeListener callback if text field "Actual" changed.
	 * The record entry "Actual" can be modified to change the entry "actual value" of the currently active ice cream concentration reading record.
	 */
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldActualValue, String newActualValue) {
		// don't propagate null or empty value
		if( null == newActualValue || newActualValue.isEmpty() || newActualValue.isBlank() ) return;
		
		int newValue = -1;
		try {
			newValue = Integer.parseInt(newActualValue);
		}catch(NumberFormatException e) {
			logger.log(Level.WARNING, "The given value \""+newActualValue+"\" is no integer number. Expecting integer value for the actual concentration value.", e);
		}
		
		try {
			model.updateActualConcentration(newValue, concentrationReadingId);
		} catch (ModelPersistenceException | PersistenceException e) {
			logger.log(Level.WARNING, "Failed to update the actual concentration value in the database. Given actual value was \""+newActualValue+"\".", e);
		}
		
		// finally update variance text field
		updateVariance( newValue, Integer.parseInt( tfTargetConcentration.getText() ) );
	}
}
