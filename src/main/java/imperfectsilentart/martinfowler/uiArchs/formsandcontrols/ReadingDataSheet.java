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
package imperfectsilentart.martinfowler.uiArchs.formsandcontrols;

import java.util.logging.Level;
import java.util.logging.Logger;

import imperfectsilentart.martinfowler.uiArchs.dbAccess.ConcentrationReading;
import imperfectsilentart.martinfowler.uiArchs.dbAccess.ConcentrationReadingDao;
import imperfectsilentart.martinfowler.uiArchs.dbAccess.DbAccessException;
import imperfectsilentart.martinfowler.uiArchs.dbAccess.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.dbAccess.MonitoringStationDao;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;



/**
 * This wrapper class encapsulates a pane used as an interface for showing, searching and modifying ice cream reading records.
 * The pane contains all data fields (read only) and record entries of one ice cream reading record.
 * Each field or entry has a label with corresponding text field.
 * 
 * The record entries "Station ID" and "Date" can be modified to initiate an internal search for the corresponding ice cream reading record.
 * The record entry "Actual" can be modified to change the entry "actual value" of the currently active ice cream reading record.
 * All other entries and data fields are calculated from external sources and thus the corresponding text fields are read only.
 */
public class ReadingDataSheet {
	private static final Logger logger = Logger.getLogger(ReadingDataSheet.class.getName());
	/*
	 * static members for singleton pattern
	 */
	private static ReadingDataSheet instance = new ReadingDataSheet();
	public static ReadingDataSheet getInstance() {
		return ReadingDataSheet.instance;
	}
	
	/*
	 * dynamic members
	 */
	private GridPane dataSheetPane = null;
	// ID of currently displayed concentration reading record
	private long concentrationReadingId = -1;

	// data depending on current monitoring station
	private Label lblStationExternalId = null;
	private Label lblTargetConcentration = null;
	// data depending on current reading record
	private Label lblDate = null;
	private Label lblActualConcentration = null;
	private Label lblVariance = null;
	
	// data depending on current monitoring station
	private TextField tfStationExternalId = null;
	private TextField tfTargetConcentration = null;
	// data depending on current reading record
	private TextField tfDate = null;
	private TextField tfActualConcentration = null;
	private TextField tfVariance = null;
	
	private final ChangeListener<String> actualValueChangeListener = new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> ov, String oldActualValue, String newActualValue) {
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
					readingDao.updateActualConcentration(newValue, concentrationReadingId);
				} catch (DbAccessException e) {
					logger.log(Level.WARNING, "Failed to update the actual concentration value in the database. Given actual value was \""+newActualValue+"\".", e);
				}
				
				// finally update variance text field
				updateVariance( newValue, Integer.parseInt( tfTargetConcentration.getText() ) );
			}
		};
	
	/**
	 * private default ctor for singleton pattern
	 * 
	 * Initializes the internal pane used as an interface for showing, searching and modifying ice cream reading records.
	 */
	private ReadingDataSheet() {
		this.dataSheetPane = new GridPane();
		this.dataSheetPane.setPadding(new Insets(10,10,10,10));
		this.dataSheetPane.setHgap(7);
		this.dataSheetPane.setVgap(7);
		
		// data depending on current monitoring station
		this.lblStationExternalId = new Label("Station ID");
		this.dataSheetPane.add(this.lblStationExternalId, 0, 0);
		GridPane.setHalignment(lblStationExternalId, HPos.LEFT);
		this.tfStationExternalId = new TextField();
		this.dataSheetPane.add(this.tfStationExternalId, 1, 0);
		
		this.lblTargetConcentration = new Label("Target");
		this.dataSheetPane.add(this.lblTargetConcentration, 0, 1);
		GridPane.setHalignment(lblTargetConcentration, HPos.LEFT);
		this.tfTargetConcentration = new TextField();
		this.tfTargetConcentration.setEditable(false);
		this.tfTargetConcentration.setDisable(true);
		this.dataSheetPane.add(this.tfTargetConcentration, 1, 1);
		
		// data depending on current reading record
		this.lblDate = new Label("Date");
		this.dataSheetPane.add(this.lblDate, 0, 2);
		GridPane.setHalignment(lblDate, HPos.LEFT);
		this.tfDate = new TextField();
		this.tfDate.setEditable(false);
		this.tfDate.setDisable(true);
		this.dataSheetPane.add(this.tfDate, 1, 2);

		this.lblActualConcentration = new Label("Actual");
		this.dataSheetPane.add(this.lblActualConcentration, 0, 3);
		GridPane.setHalignment(lblActualConcentration, HPos.LEFT);
		this.tfActualConcentration = new TextField();
		this.dataSheetPane.add(this.tfActualConcentration, 1, 3);
		
		this.lblVariance = new Label("Variance");
		this.dataSheetPane.add(this.lblVariance, 0, 4);
		GridPane.setHalignment(lblVariance, HPos.LEFT);
		this.tfVariance = new TextField();
		this.tfVariance.setEditable(false);
		this.tfVariance.setDisable(true);
		this.dataSheetPane.add(this.tfVariance, 1, 4);
	}

	/**
	 * Set new value for "Station ID" text field.
	 * 
	 * @param newExternalId    new value for "Station ID" text field. Null is ignored. Use empty string instead.
	 * @throws DbAccessException 
	 */
	public synchronized void changeReadingRecord(final String newExternalId) {
		if(null == newExternalId) {
			return;
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
			return;
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
			return;
		}
		this.tfDate.setText( newRecord.getReadingTimestamp().format( ConcentrationReadingDao.getReadingTimestampFormat() ) );
		// temporarily disable change listener
		this.unregisterActualConcentratinChangeListener();
		this.tfActualConcentration.setText( Integer.toString(newRecord.getActualConcentration()) );
		this.registerActualConcentrationChangeListener();
		// update ID of currently displayed concentration reading record
		this.concentrationReadingId = newRecord.getId();
		updateVariance( newRecord.getActualConcentration(), station.getTargetConcentration() );
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
		
		// Important: use double to avoid integer arithmetics (rounding down decimal digits)
		double variancePercentage = ( variance / targetConcentration )*100;
		// remove sign from percentage, since not needed
		if( variancePercentage < 0) { variancePercentage = (variancePercentage*-1); }
		
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
	 * Registers the given listener to the text field for the external station ID.
	 * 
	 * @param changeListener
	 */
	public void registerStationChangeListener(final ChangeListener<String> changeListener) {
		this.tfStationExternalId.textProperty().addListener(changeListener);
	}
	
	
	/**
	 * Unregisters the listener from the text field for the actual concentration value.
	 * 
	 * @param changeListener
	 */
	public void unregisterActualConcentratinChangeListener() {
		this.tfActualConcentration.textProperty().removeListener(this.actualValueChangeListener);
	}
	
	/**
	 * Registers the listener to the text field for the actual concentration value.
	 * 
	 * @param changeListener
	 */
	public void registerActualConcentrationChangeListener() {
		this.tfActualConcentration.textProperty().addListener(this.actualValueChangeListener);
	}
	
	/**
	 * Integrates the node of this pane into the given parent node.
	 * 
	 * @param parentPane    given parent node
	 */
	public void integrateIntoPane(final Pane parentPane) {
		parentPane.getChildren().add(this.dataSheetPane);
	}
	
	/**
	 * @return node created by this class
	 */
	public GridPane getDataSheetPane() {
		return this.dataSheetPane;
	}
	
	/**
	 * Wipes all text fields which depend on external station ID.
	 * The current concentration reading record indirectly depends on the current monitoring station record.
	 */
	private void wipeAllDependentTextFields() {
		this.tfTargetConcentration.setText("");
		wipeReadingDependentTextFields();
	}
	
	/**
	 * Wipes all text field which depend on the current concentration reading record.
	 */
	private void wipeReadingDependentTextFields() {
		this.tfDate.setText("");
		this.tfActualConcentration.setText("");
		this.tfVariance.setText("");
	}


}
