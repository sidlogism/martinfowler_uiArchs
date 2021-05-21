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
package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller.IReadingController;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.IReadingModelListener;
import imperfectsilentart.martinfowler.uiArchs.util.TimeProcessingException;
import imperfectsilentart.martinfowler.uiArchs.util.TimeTools;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;



/**
 * View handling UI elements related to reading view.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.ReadingDataSheet
 */
public class ReadingView implements Initializable, IReadingView, IReadingModelListener {
	private static final Logger logger = Logger.getLogger(ReadingView.class.getName());
	/**
	 * Flag indicating whether the current UI change is induced by user input (external) or by view code itself (internal).
	 * 
	 * REALLY DIRTY workaround which is not thread safe.
	 * Needed because overwriting the text property of text fields internally triggers on<Property>Change-event handler for text property.
	 * TODO coulnd't yet figure out how to disable on<Property>Change-event handler for text property programmatically.
	 * 
	 * @see #overwriteUIActualConcentration
	 */
	private volatile boolean currentlyOverwritingActualConcentration = false;
	/**
	 * Flag indicating whether the current UI change is induced by user input (external) or by view code itself (internal).
	 * 
	 * REALLY DIRTY workaround which is not thread safe.
	 * Needed because overwriting the text property of text fields internally triggers on<Property>Change-event handler for text property.
	 * TODO coulnd't yet figure out how to disable on<Property>Change-event handler for text property programmatically.
	 * 
	 * @see #overwriteUIStationExternalId
	 */
	private volatile boolean currentlyOverwritingStationExtId = false;
	private IReadingController controller = null;

	// data depending on current monitoring station
	@FXML
	public TextField tfStationExternalId;
	@FXML
	public TextField tfTargetConcentration;
	// data depending on current reading record
	@FXML
	public TextField tfReadingTimestamp;
	@FXML
	public TextField tfActualConcentration;
	@FXML
	public TextField tfVariance;
	/**
	 * ID of currently displayed concentration reading record
	 * 
	 * @note: like the selection state and text contents this is part of the presentation model: i. e. the portion of the application data representing the current state of the view.
	 */
	private long currentReadingId = -1;

	public ReadingView() {
		logger.log(Level.INFO, "reading view ctor");
	}
	
	/**
	 * Called to initialize a controller after its root element has been
	 * completely processed.
	 * @note    in this subproject "fx:controller" references view objects!
	 *
	 * @param location
	 * The location used to resolve relative paths for the root object, or
	 * {@code null} if the location is not known.
	 *
	 * @param resources
	 * The resources used to localize the root object, or {@code null} if
	 * the root object was not localized.
	 */
	// TODO superseded => @FXML + remove initializable ?
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.log(Level.INFO, "reading view init");
	}
	
	/**
	 * @param controller the controller to set
	 */
	@Override
	public void setReadingController(IReadingController controller) {
		this.controller = controller;
	}
	
	/**
	 * @param currentReadingId the currentReadingId to set
	 */
	@Override
	public void setCurrentReadingId(long currentReadingId) {
		this.currentReadingId = currentReadingId;
	}

	/**
	 * @return the stationExternalId
	 */
	@Override
	public String getStationExternalId() {
		return tfStationExternalId.getText();
	}
	
	/**
	 * @param stationExternalId the stationExternalId to set
	 */
	@Override
	public void overwriteUIStationExternalId(String stationExternalId) {
		this.currentlyOverwritingStationExtId = true;
		this.tfStationExternalId.setText( stationExternalId );
		this.currentlyOverwritingStationExtId = false;
	}

	/**
	 * @return the targetConcentration
	 */
	@Override
	public int getTargetConcentration() {
		try {
			return Integer.parseInt( this.tfTargetConcentration.getText() );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			logger.log(Level.WARNING, "Target concentration has invalid value \""+ this.tfTargetConcentration.getText() +"\". Returning default value.");
			return -1;
		}
	}
	/**
	 * @param targetConcentration the targetConcentration to set
	 */
	@Override
	public void overwriteUITargetConcentration(int targetConcentration) {
		this.tfTargetConcentration.setText( Integer.valueOf(targetConcentration).toString() );
		try {
			overwriteUIVariance( Integer.valueOf(this.tfActualConcentration.getText()).intValue(), targetConcentration );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			logger.log(Level.WARNING, "Couldn't compute variance because actual concentration has invalid value \""+ this.tfActualConcentration.getText() +"\". Returning default value.");
		}
	}
	
	/**
	 * @return the readingTimestamp
	 * @throws TimeProcessingException 
	 */
	@Override
	public LocalDateTime getReadingTimestamp() {
		try {
			return TimeTools.parseReadingTimestamp( this.tfReadingTimestamp.getText() );
		}catch(TimeProcessingException e) {
			// not logging exception because of verbosity
			logger.log(Level.WARNING, "Reading timestamp has invalid value \""+ this.tfReadingTimestamp.getText() +"\". Returning default value.");
			return LocalDateTime.now();
		}
	}
	/**
	 * @param readingTimestamp the readingTimestamp to set
	 * @throws TimeProcessingException 
	 */
	@Override
	public void overwriteUIReadingTimestamp(LocalDateTime readingTimestamp){
		this.tfReadingTimestamp.setText( readingTimestamp.format( TimeTools.getReadingTimestampFormat() ) );
	}
	
	/**
	 * @return the actualConcentration
	 */
	@Override
	public int getActualConcentration() {
		try {
			return Integer.parseInt( this.tfActualConcentration.getText() );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			logger.log(Level.WARNING, "Actual concentration has invalid value \""+ this.tfActualConcentration.getText() +"\". Returning default value.");
			return -1;
		}
	}
	
	/**
	 * @param actualConcentration the actualConcentration to set
	 */
	@Override
	public void overwriteUIActualConcentration(final int actualConcentration) {
		logger.log(Level.FINE, "Overwriting text field with new actual concentration: "+actualConcentration);
		this.currentlyOverwritingActualConcentration = true;
		this.tfActualConcentration.setText( Integer.valueOf(actualConcentration).toString() );
		this.currentlyOverwritingActualConcentration = false;
		try {
			overwriteUIVariance( actualConcentration, Integer.valueOf(this.tfTargetConcentration.getText()).intValue() );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			logger.log(Level.WARNING, "Couldn't compute variance because target concentration has invalid value \""+ this.tfTargetConcentration.getText() +"\". Returning default value.");
		}
	}
	
	/**
	 * Recomputes the concentration variance based on the given values.
	 * @note    This implicitly overwrites current selection status in UI.
	 * @note: like the selection state and text contents this is part of the presentation model: i. e. the portion of the application data representing the current state of the view.
	 * 
	 * @param actualConcentration    value of the actual concentration measured
	 * @param targetConcentration    value of the target concentration to be reached
	 */
	//@Override
	private void overwriteUIVariance(final int actualConcentration, final int targetConcentration) {
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
		 * NOTE like the selection state and text contents this is part of the presentation model: i. e. the portion of the application data representing the current state of the view.
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
	@Override
	public void wipeAllDependentTextFields() {
		this.tfTargetConcentration.clear();
		wipeReadingDependentTextFields();
	}
	
	/**
	 * Wipes all text field which depend on the current concentration reading record.
	 */
	@Override
	public void wipeReadingDependentTextFields() {
		this.tfReadingTimestamp.clear();
		this.tfActualConcentration.clear();
		this.tfVariance.clear();
	}
	
	@Override
	public void actualConcentrationChanged(final int newValue) {
		logger.log(Level.FINE, "Observer notification: Model was updated to new actual concentration: "+newValue);
		overwriteUIActualConcentration(newValue);
	}
	
	/**
	 * Handle change of text field for station external ID in UI.
	 * 
	 * @param event    UI event that triggered the handler callback
	 */
	@FXML
	public void handleUserChangedStationExtId(final Event event) {
		if(this.currentlyOverwritingStationExtId) return;
		logger.log(Level.FINE, "User changed value of "+event.getSource());
		this.controller.handleUserChangedStationExtId( this.tfStationExternalId.getText() );
	}
	
	/**
	 * Handle change of text field for actual concentration in UI.
	 * 
	 * @param event    UI event that triggered the handler callback
	 */
	@FXML
	public void handleUserChangedActualConcentration(final Event event) {
		if(this.currentlyOverwritingActualConcentration) return;
		logger.log(Level.FINE, "User changed value of "+event.getSource());
		this.controller.handleUserChangedActualConcentration( this.tfActualConcentration.getText() , this.currentReadingId );
	}
}
