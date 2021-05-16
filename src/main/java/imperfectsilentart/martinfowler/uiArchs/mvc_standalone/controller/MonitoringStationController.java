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

import javax.persistence.PersistenceException;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.IMonitoringStationModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.MonitoringStationModel;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ModelPersistenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputControl;

/**
 * Controller handling user actions in views related to monitoring stations.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.MonitoringStationList
 */
public class MonitoringStationController implements Initializable, ChangeListener<String>, IMonitoringStationController {
	private static final Logger logger = Logger.getLogger(MonitoringStationController.class.getName());
	private IMonitoringStationModel model = null;

	@FXML
	public ListView<String> stationList;
	/**
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 * 
	 * TODO suffices strategy pattern?
	 */
	@FXML
	public IReadingDataSheetController readingViewController;

	public MonitoringStationController() {
		logger.log(Level.FINE, "station ctor");
		this.model = new MonitoringStationModel();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		logger.log(Level.FINE, "station init");
		
		/*
		 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
		 * This link is established here.
		 */
		this.readingViewController.setStationController(this);
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
		} catch (ModelPersistenceException | PersistenceException e) {
			final List<String> data = new ArrayList<String>();
			data.add("- data access error -");
			stationListData = FXCollections.observableArrayList( data );
			logger.log(Level.SEVERE, "Failed to load data from DB.", e);
		}
		logger.log(Level.FINE, "Initializing station list with these entries: "+stationListData);
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
	@Override
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
	@Override
	public void wipeSelection() {
		this.stationList.getSelectionModel().clearSelection();
	}
	
	/**
	 * Forwards the monitoring station with the given external ID from the model.
	 * 
	 * @param stationExternalId    external ID of relevant monitoring station
	 * @return domain object of relevant monitoring station. null if the query result is empty.
	 * @throws ModelPersistenceException
	 */
	@Override
	public synchronized MonitoringStation getStation(final String stationExternalId) throws ModelPersistenceException{
		return this.model.getStation(stationExternalId);
	}

	/**
	 * ChangeListener callback
	 *     if selection in station list of station view changed
	 *     or if text field "Station ID" in reading view changed.
	 * The record entry "Station ID" of reading view can be modified initiate an internal search for the corresponding ice cream reading record.
	 */
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldStationValue, String newStationName) {
		if( observable instanceof ReadOnlyObjectProperty ) {
			/*
			 * ReadOnlyObjectProperty indicates a selection change in station list in station view.
			 * 
			 * don't propagate null or empty values
			 * if there is no selection (because of wrong or partial station name) or selection disappears, the new value is null, which must be ignored.
			 */
			if( null == newStationName || newStationName.isEmpty() || newStationName.isBlank() ) return;
			
			if(! this.readingViewController.changeReadingRecord(newStationName) ) {
				/*
				 * If there is a problem with the new station, wipe selection.
				 * To avoid redundant listener updates temporarily unregister from changes of the list selection.
				 */
				this.stationList.getSelectionModel().selectedItemProperty().removeListener(this);
				wipeSelection();
				this.stationList.getSelectionModel().selectedItemProperty().addListener(this);
			}
			return;
		}else if ( observable instanceof StringProperty || observable instanceof TextInputControl ) {
			/*
			 * StringProperty indicates a value change in external station ID in reading view (text field).
			 * 
			 * NOTE:
			 * The event handler approach doesn't apply here because events on text fields are only fired on pressing Enter.
			 * The exactly expected class would be the private nested class TextInputControl$TextProperty, which cannot be imported since it is private.
			 */
			changeSelection(newStationName);
			return;
		}
		logger.log(Level.WARNING, "Unknown class of observed object. The observed object has unknown type "+observable.getClass().getName()+".\nold value:"+oldStationValue+"\nnew value:"+newStationName);
	}
}
