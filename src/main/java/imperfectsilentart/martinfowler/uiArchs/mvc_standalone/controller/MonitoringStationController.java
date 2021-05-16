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


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import imperfectsilentart.martinfowler.uiArchs.formsandcontrols.MonitoringStationList;
import imperfectsilentart.martinfowler.uiArchs.formsandcontrols.ReadingDataSheet;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.IMonitoringStationModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.MonitoringStationModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PeristenceException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

/**
 * Controller handling user actions in views related to monitoring stations.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.MonitoringStationList
 */
public class MonitoringStationController implements Initializable, ChangeListener<String> {
	//FIXME extract interface
	private static final Logger logger = Logger.getLogger(MonitoringStationController.class.getName());
	private IMonitoringStationModel model = null;
	private ReadingDataSheetController readingController = null;

	@FXML
	public ListView<String> stationList = new ListView<String>();

	
	/**
	 * private default ctor for singleton pattern
	 * 
	 * Initializes the internal pane for scrollable list of monitoring stations.
	 */
	public MonitoringStationController(final ReadingDataSheetController readingController) {
		logger.log(Level.INFO, "gotcha");
		this.model = new MonitoringStationModel();
		//this.readingController = readingController;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// FIXME log url ,resources
		logger.log(Level.INFO, "gotcha");
		
		/*
		 * Initialize list of monitoring stations.
		 * Currently the list contains only one string representing column monitoring_station.station_external_id.
		 * There currently is no out-of-the-box list for multiple columns in JavaFX.
		 */
		ObservableList<String> stationListData = null;
		try {
			final List<MonitoringStation> stations = model.findAll();
			final List<String> externalIds = stations.stream().map(MonitoringStation::getStationExternalId).collect( Collectors.toList() );
			stationListData = FXCollections.observableArrayList( externalIds );
		} catch (PeristenceException e) {
			final List<String> data = new ArrayList<String>();
			data.add("- data access error -");
			stationListData = FXCollections.observableArrayList( data );
			logger.log(Level.SEVERE, "Failed to load data from DB.", e);
		}
		logger.log(Level.INFO, "Data: "+stationListData);
		this.stationList.setItems(stationListData);
		
		/*
		 * IMPORTANT: Register this controller as listener to changes of the selection in the list of monitoring stations.
		 */
		this.stationList.getSelectionModel().selectedItemProperty().addListener(this);
	}

	
	/**
	 * Set new selection for monitoring station list.
	 * 
	 * @param newExternalId    new selection for monitoring station list. Null is ignored. Use empty string instead.
	 */
	public void changeSelection(final String newExternalId) {
		if(null == newExternalId) {
			return;
		}
		/*
		 * Incomplete partial names or missing hits are handled implicitly:
		 * If the selection model doesn't find the given entry, the selection simply doesn't change.
		 */
		this.stationList.getSelectionModel().select(newExternalId);
	}

	/**
	 * Wipe any selection in monitoring station list.
	 * 
	 * @param newExternalId    new selection for monitoring station list. Null is ignored. Use empty string instead.
	 */
	public void wipeSelection() {
		this.stationList.getSelectionModel().clearSelection();
	}


	@Override
	public void changed(ObservableValue<? extends String> observable, String oldStationValue, String newStationName) {
		// call ReadingDataSheetController.changeReadingRecord !!!
		// don't propagate null or empty values
		// if there is no selection (because of wrong or partial station name) or selection disappears, the new value is null, which must be ignored.
		if( null == newStationName || newStationName.isEmpty() || newStationName.isBlank() ) return;
		
		if(! ReadingDataSheet.getInstance().changeReadingRecord(newStationName) ) {
			/*
			 * If there is a problem with the new station, wipe selection.
			 * To avoid redundant listener updates temporarily unregister from changes of the list selection.
			 */
			this.stationList.getSelectionModel().selectedItemProperty().removeListener(this);
			wipeSelection();
			this.stationList.getSelectionModel().selectedItemProperty().addListener(this);
		}
	}
}
