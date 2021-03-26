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

import java.time.OffsetDateTime;

import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import java.util.logging.Level;
import java.util.logging.Logger;



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

	private Label lblStationExternalId = null;
	private Label lblDate = null;
	private Label lblTargetConcentration = null;
	private Label lblActualConcentration = null;
	private Label lblVariance = null;
	
	private TextField tfStationExternalId = null;
	private TextField tfDate = null;
	private TextField tfTargetConcentration = null;
	private TextField tfActualConcentration = null;
	private TextField tfVariance = null;
	
	
	/*
	 * private default ctor for singleton pattern
	 * 
	 * Initializes the internal pane used as an interface for showing, searching and modifying ice cream reading records.
	 */
	private ReadingDataSheet() {
		this.dataSheetPane = new GridPane();
		this.dataSheetPane.setPadding(new Insets(10,10,10,10));
		this.dataSheetPane.setHgap(7);
		this.dataSheetPane.setVgap(7);
		
		this.lblStationExternalId = new Label("Station ID");
		this.dataSheetPane.add(this.lblStationExternalId, 0, 0);
		this.dataSheetPane.setHalignment(lblStationExternalId, HPos.LEFT);
		this.tfStationExternalId = new TextField();
		this.dataSheetPane.add(this.tfStationExternalId, 1, 0);
		
		this.lblDate = new Label("Date");
		this.dataSheetPane.add(this.lblDate, 0, 1);
		this.dataSheetPane.setHalignment(lblDate, HPos.LEFT);
		this.tfDate = new TextField();
		this.dataSheetPane.add(this.tfDate, 1, 1);
		
		this.lblTargetConcentration = new Label("Target");
		this.dataSheetPane.add(this.lblTargetConcentration, 0, 2);
		this.dataSheetPane.setHalignment(lblTargetConcentration, HPos.LEFT);
		this.tfTargetConcentration = new TextField();
		this.dataSheetPane.add(this.tfTargetConcentration, 1, 2);
		
		this.lblActualConcentration = new Label("Actual");
		this.dataSheetPane.add(this.lblActualConcentration, 0, 3);
		this.dataSheetPane.setHalignment(lblActualConcentration, HPos.LEFT);
		this.tfActualConcentration = new TextField();
		this.dataSheetPane.add(this.tfActualConcentration, 1, 3);
		
		this.lblVariance = new Label("Variance");
		this.dataSheetPane.add(this.lblVariance, 0, 4);
		this.dataSheetPane.setHalignment(lblVariance, HPos.LEFT);
		this.tfVariance = new TextField();
		this.dataSheetPane.add(this.tfVariance, 1, 4);
	}
	
	
	public GridPane getDataSheetPane() {
		return this.dataSheetPane;
	}

	/**
	 * Set new value for "Station ID" text field.
	 * 
	 * @param newStationName    new value for "Station ID" text field. Null is ignored. Use empty string instead.
	 */
	public synchronized void changeReadingRecord(final String newStationName) {
		if(null == newStationName) {
			return;
		}
		this.tfStationExternalId.setText(newStationName);
		//FIXME handle incomplete partial names or missing hits
	}
	private synchronized void changeReadingRecord(final String stationName, final OffsetDateTime readingTimestamp) {
		// FIXME impl
	}
	
	public void registerStationChangeListener(final ChangeListener<String> changeListener) {
		this.tfStationExternalId.textProperty().addListener(changeListener);
	}
	public void integrateIntoPane(final Pane parentPane) {
		parentPane.getChildren().add(this.dataSheetPane);
	}
}
