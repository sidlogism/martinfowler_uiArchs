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
import imperfectsilentart.martinfowler.uiArchs.util.TimeProcessingException;
import imperfectsilentart.martinfowler.uiArchs.util.TimeTools;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;



/**
 * View handling UI elements related to reading view.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.ReadingDataSheet
 */
public class ReadingDataSheetView implements Initializable, IReadingDataSheetView {
	private static final Logger logger = Logger.getLogger(ReadingDataSheetView.class.getName());
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

	public ReadingDataSheetView() {
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
		//FIXME check if this triggers onTextChange or onAction!!
		this.tfStationExternalId.setText( stationExternalId );
	}

	/**
	 * @return the targetConcentration
	 */
	@Override
	public int getTargetConcentration() {
		try {
			return Integer.parseInt( tfTargetConcentration.getText() );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			logger.log(Level.WARNING, "Target concenctration has invalid value \""+ tfTargetConcentration.getText() +"\". Returning default value.");
			return -1;
		}
	}
	/**
	 * @param targetConcentration the targetConcentration to set
	 */
	@Override
	public void overwriteUITargetConcentration(int targetConcentration) {
		this.tfTargetConcentration.setText( Integer.valueOf(targetConcentration).toString() );
	}
	
	/**
	 * @return the readingTimestamp
	 * @throws TimeProcessingException 
	 */
	@Override
	public LocalDateTime getReadingTimestamp() {
		try {
			return TimeTools.parseReadingTimestamp( tfReadingTimestamp.getText() );
		}catch(TimeProcessingException e) {
			// not logging exception because of verbosity
			logger.log(Level.WARNING, "Reading timestamp has invalid value \""+ tfReadingTimestamp.getText() +"\". Returning default value.");
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
			return Integer.parseInt( tfActualConcentration.getText() );
		}catch(NumberFormatException e) {
			// not logging exception because of verbosity
			logger.log(Level.WARNING, "Actual concenctration has invalid value \""+ tfActualConcentration.getText() +"\". Returning default value.");
			return -1;
		}
	}
	
	/**
	 * @param actualConcentration the actualConcentration to set
	 */
	@Override
	public void overwriteUIActualConcentration(int actualConcentration) {
		/*
		 * Setting the text property triggers onTextChange-event.
		 * Thus we must temporarily disable any text change handlers.
		 */
		final EventHandler<? super InputMethodEvent> handler = this.tfActualConcentration.getOnInputMethodTextChanged();
		this.tfActualConcentration.setOnAction(null);
		this.tfActualConcentration.setText( Integer.valueOf(actualConcentration).toString() );
		this.tfActualConcentration.setOnInputMethodTextChanged(handler);
	}
	
	/**
	 * Recomputes the concentration variance based on the given values.
	 * @note    This implicitly overwrites current selection status in UI.
	 * @note: like the selection state and text contents this is part of the presentation model: i. e. the portion of the application data representing the current state of the view.
	 * 
	 * @param actualConcentration    value of the actual concentration measured
	 * @param targetConcentration    value of the target concentration to be reached
	 */
	@Override
	public void overwriteUIVariance(final int actualConcentration, final int targetConcentration) {
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

	/**
	 * ChangeListener callback if text field "Actual" changed.
	// FIXME entity listener callback
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldActualValue, String newActualValue) {
		// finally update variance text field
		updateVariance( newValue, Integer.parseInt( tfTargetConcentration.getText() ) );
	}
	 */
	
	/**
	 * Handle change of text field for station external ID in UI.
	 * 
	 * @param event    UI event that triggered the handler callback
	 */
	@FXML
	public void handleUserChangedStationExtId(final Event event) {
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
		logger.log(Level.INFO, "User changed value of "+event.getSource());
		this.controller.handleUserChangedActualConcentration( this.tfActualConcentration.getText() , this.currentReadingId );
	}
}
