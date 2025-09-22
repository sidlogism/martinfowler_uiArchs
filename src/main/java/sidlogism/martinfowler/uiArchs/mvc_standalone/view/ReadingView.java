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
package sidlogism.martinfowler.uiArchs.mvc_standalone.view;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import sidlogism.martinfowler.uiArchs.mvc_standalone.controller.IReadingController;
import sidlogism.martinfowler.uiArchs.mvc_standalone.model.IReadingModelListener;
import sidlogism.martinfowler.uiArchs.util.TimeProcessingException;
import sidlogism.martinfowler.uiArchs.util.TimeTools;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;



/**
 * View handling UI elements related to reading view.
 * @see sidlogism.martinfowler.uiArchs.formsandcontrols.ReadingDataSheet
 */
public class ReadingView implements IReadingView, IReadingModelListener {
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
	 */
	@FXML
	public void initialize() {
		logger.log(Level.INFO, "reading view init");
	}
	
	@Override
	public void setReadingController(IReadingController controller) {
		this.controller = controller;
	}
	
	@Override
	public void setCurrentReadingId(long currentReadingId) {
		this.currentReadingId = currentReadingId;
	}

	@Override
	public String getStationExternalId() {
		return tfStationExternalId.getText();
	}
	
	@Override
	public void overwriteUIStationExternalId(String stationExternalId) {
		this.currentlyOverwritingStationExtId = true;
		this.tfStationExternalId.setText( stationExternalId );
		this.currentlyOverwritingStationExtId = false;
	}
	
	@Override
	public void markUIStationExternalIdErroneous() {
		this.tfStationExternalId.setStyle("-fx-text-inner-color: red");
		
	}

	@Override
	public void markUIStationExternalIdValid() {
		this.tfStationExternalId.setStyle("-fx-text-inner-color: black");
		
	}

	@Override
	public int getTargetConcentration() {
		final String text = this.tfTargetConcentration.getText();
		try {
			return Integer.parseInt( text );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			if( null == text || text.isEmpty() || text.isBlank() ) {
				logger.log(Level.FINE, "Target concentration is empty. Reading view is propably not initialized yet or was wiped before. Refill of reading view is propably currently in progress.");
			}else {
				logger.log(Level.WARNING, "Target concentration has invalid value \""+ text +"\". Returning default value.");
			}
			return -1;
		}
	}

	@Override
	public void overwriteUITargetConcentration(int targetConcentration) {
		this.tfTargetConcentration.setText( Integer.valueOf(targetConcentration).toString() );
		
		final String actualConcentrationText = this.tfActualConcentration.getText();
		try {
			overwriteUIVariance( Integer.valueOf(actualConcentrationText).intValue(), targetConcentration );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			if( null == actualConcentrationText || actualConcentrationText.isEmpty() || actualConcentrationText.isBlank() ) {
				logger.log(Level.FINE, "Couldn't compute variance because actual concentration is empty. Reading view is propably not initialized yet or was wiped before. Refill of reading view is propably currently in progress.");
			}else {
				logger.log(Level.WARNING, "Couldn't compute variance because actual concentration has invalid value \""+ actualConcentrationText +"\".");
			}
		}
	}
	
	@Override
	public LocalDateTime getReadingTimestamp() {
		final String text = this.tfReadingTimestamp.getText();
		try {
			return TimeTools.parseReadingTimestamp( text );
		}catch(TimeProcessingException e) {
			// not logging exception because of verbosity
			if( null == text || text.isEmpty() || text.isBlank() ) {
				logger.log(Level.FINE, "Reading timestamp is empty. Reading view is propably not initialized yet or was wiped before. Refill of reading view is propably currently in progress.");
			}else {
				logger.log(Level.WARNING, "Reading timestamp has invalid value \""+ text +"\". Returning default value.");
			}
			return LocalDateTime.now();
		}
	}

	@Override
	public void overwriteUIReadingTimestamp(LocalDateTime readingTimestamp){
		this.tfReadingTimestamp.setText( readingTimestamp.format( TimeTools.getReadingTimestampFormat() ) );
	}
	
	@Override
	public int getActualConcentration() {
		final String text = this.tfActualConcentration.getText();
		try {
			return Integer.parseInt( text );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			if( null == text || text.isEmpty() || text.isBlank() ) {
				logger.log(Level.FINE, "Actual concentration is empty. Reading view is propably not initialized yet or was wiped before. Refill of reading view is propably currently in progress.");
			}else {
				logger.log(Level.WARNING, "Actual concentration has invalid value \""+ text +"\". Returning default value.");
			}
			return -1;
		}
	}
	
	@Override
	public void overwriteUIActualConcentration(final int actualConcentration) {
		logger.log(Level.FINE, "Overwriting text field with new actual concentration: "+actualConcentration);
		this.currentlyOverwritingActualConcentration = true;
		this.tfActualConcentration.setText( Integer.valueOf(actualConcentration).toString() );
		// mark new content of actual concentration as VALID
		this.markUIActualConcentrationValid();
		this.currentlyOverwritingActualConcentration = false;
		
		final String targetConcentrationText = this.tfTargetConcentration.getText();
		try {
			overwriteUIVariance( actualConcentration, Integer.valueOf(targetConcentrationText).intValue() );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			if( null == targetConcentrationText || targetConcentrationText.isEmpty() || targetConcentrationText.isBlank() ) {
				logger.log(Level.FINE, "Couldn't compute variance because target concentration is empty. Reading view is propably not initialized yet or was wiped before. Refill of reading view is propably currently in progress.");
			}else {
				logger.log(Level.WARNING, "Couldn't compute variance because target concentration has invalid value \""+ targetConcentrationText +"\".");
			}
		}
	}
	

	@Override
	public void markUIActualConcentrationErroneous() {
		this.tfActualConcentration.setStyle("-fx-text-inner-color: red");
		
	}

	@Override
	public void markUIActualConcentrationValid() {
		this.tfActualConcentration.setStyle("-fx-text-inner-color: black");
		
	}
	
	/**
	 * Recomputes the concentration variance based on the given values.
	 * @note    This implicitly overwrites current variance in UI.
	 * @note: like the selection state and text contents this is part of the presentation model: i. e. the portion of the application data representing the current state of the view.
	 * 
	 * @param actualConcentration    value of the actual concentration measured
	 * @param targetConcentration    value of the target concentration to be reached
	 */
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

	@Override
	public void wipeAllDependentTextFields() {
		this.tfTargetConcentration.clear();
		wipeReadingDependentTextFields();
	}
	
	
	@Override
	public void restoreEditabilityOnAllDependentTextFields() {
		// Only affect those text fields, who are intended to be editable.
		restoreEditabilityOnReadingDependentTextFields();
	}
	
	@Override
	public void removeEditabilityFromAllDependentTextFields() {
		// Only affect those text fields, who are intended to be editable.
		// The text field of the station external ID is always editable, since all other text fields depend on its value.
		removeEditabilityFromReadingDependentTextFields();
	}
	
	
	@Override
	public void wipeReadingDependentTextFields() {
		this.tfReadingTimestamp.clear();
		this.tfActualConcentration.clear();
		this.tfVariance.clear();
	}
	
	@Override
	public void restoreEditabilityOnReadingDependentTextFields() {
		// Only affect those text fields, who are intended to be editable.
		this.tfActualConcentration.setEditable(true);
	}	
	
	@Override
	public void removeEditabilityFromReadingDependentTextFields() {
		// Only affect those text fields, who are intended to be editable.
		this.tfActualConcentration.setEditable(false);
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
