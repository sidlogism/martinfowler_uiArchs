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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import imperfectsilentart.martinfowler.uiArchs.formsandcontrols.MonitoringStationList;
import imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.ConcentrationReading;
import imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.ConcentrationReadingDao;
import imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.DbAccessException;
import imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence.MonitoringStationDao;
import imperfectsilentart.martinfowler.uiArchs.util.TimeTools;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;



/**
 * Business logic for accessing and processing all data related to ice cream reading records.
 * The pane contains all data fields (read only) and record entries of one ice cream reading record.
 * Each field or entry has a label with corresponding text field.
 * 
 * The record entries "Station ID" and "Date" can be modified to initiate an internal search for the corresponding ice cream reading record.
 * The record entry "Actual" can be modified to change the entry "actual value" of the currently active ice cream reading record.
 * All other entries and data fields are calculated from external sources and thus the corresponding text fields are read only.
 * 
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.ReadingDataSheet
 */
public class ReadingDataSheetController implements Initializable, ChangeListener<String>{
	//FIXME extract interface
	private static final Logger logger = Logger.getLogger(ReadingDataSheetController.class.getName());
	// ID of currently displayed concentration reading record
	private long concentrationReadingId = -1;
	
	// data depending on current monitoring station
	private TextField tfStationExternalId = null;
	private TextField tfTargetConcentration = null;
	// data depending on current reading record
	private TextField tfDate = null;
	private TextField tfActualConcentration = null;
	private TextField tfVariance = null;

	public ReadingDataSheetController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//FIXME init text fields
		
		/*
		 * IMPORTANT: Register this controller as listener to value changes of the actual concentration text field.
		 */
		this.tfActualConcentration.textProperty().addListener(this);
	}
	
	
	/**
	 * Registers the given listener to the text field for the external station ID.
	 * 
	 * @param changeListener
	 */
	
	//@FXML
	private void handleButtonAction(ActionEvent event) {
		//FIXME better?
	}
	public void registerStationChangeListener(final ChangeListener<String> changeListener) {
		this.tfStationExternalId.textProperty().addListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov, String oldStationName, String newStationName) {
						// don't propagate null or empty values
						if( null == newStationName || newStationName.isEmpty() || newStationName.isBlank() ) return;
						
						MonitoringStationList.getInstance().changeSelection(newStationName);
					}
				}
			);
	}

	/**
	 * Set new value for "Station ID" text field.
	 * 
	 * @param newExternalId    new value for "Station ID" text field. Null is ignored. Use empty string instead.
	 * @return    boolean value indicating whether changing the currently displayed reading record was successful. True = success, false = failure.
	 * @throws DbAccessException 
	 */
	public synchronized boolean changeReadingRecord(final String newExternalId) {
		if(null == newExternalId) {
			return false;
		}
		
		/*
		 * load and display data depending on current monitoring station
		 */
		final MonitoringStationDao stationDao = new MonitoringStationDao();
		MonitoringStation station = null;
		try {
			station = stationDao.getStation(newExternalId);
			if( null == station ) {
				throw new DbAccessException("There is no station with given external station ID \""+newExternalId+"\".");
			}
		} catch (DbAccessException e) {
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
		final ConcentrationReadingDao readingDao = new ConcentrationReadingDao();
		ConcentrationReading newRecord = null;
		try {
			newRecord = readingDao.getLatestConcentrationReading( station.getId() );
			if( null == newRecord ) {
				throw new DbAccessException("There doesn't exist any concentration reading for given station yet. Station: "+station);
			}
		} catch (DbAccessException e) {
			logger.log(Level.WARNING, "Failed to lookup concentration readings for given station. Station: "+station, e);
			// wipe text fields to indicate error
			wipeReadingDependentTextFields();
			return false;
		}
		this.tfDate.setText( newRecord.getReadingTimestamp().format( TimeTools.getReadingTimestampFormat() ) );
		// temporarily disable change listener
		this.unregisterActualConcentratinChangeListener(); //FIXME unregister this
		this.tfActualConcentration.setText( Integer.toString(newRecord.getActualConcentration()) );
		this.registerActualConcentrationChangeListener(); //FIXME register this
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
		
		final ConcentrationReadingDao readingDao = new ConcentrationReadingDao();
		try {
			// TODO call reading model..updateActualConcentration 
			readingDao.updateActualConcentration(newValue, concentrationReadingId);
		} catch (DbAccessException e) {
			logger.log(Level.WARNING, "Failed to update the actual concentration value in the database. Given actual value was \""+newActualValue+"\".", e);
		}
		
		// finally update variance text field
		updateVariance( newValue, Integer.parseInt( tfTargetConcentration.getText() ) );
	}
}
