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
package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller.IStationController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

/**
 * View handling UI elements related to monitoring station view.
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.MonitoringStationList
 */
public class MonitoringStationView implements Initializable, ChangeListener<String>, IMonitoringStationView {
	private static final Logger logger = Logger.getLogger(MonitoringStationView.class.getName());
	private IStationController controller = null;
	
	@FXML
	public ListView<String> stationList;
	/**
	 * Reference to nested reading view created and initialized externally.
	 * 
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 */
	@FXML
	public IReadingDataSheetView readingViewController;

	public MonitoringStationView(){
		logger.log(Level.INFO, "station view ctor");
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
	public void initialize(URL url, ResourceBundle resources) {
		logger.log(Level.INFO, "station view init");

		/*
		 * IMPORTANT: Register this object as listener to changes of the selection in the station view.
		 */
		this.stationList.getSelectionModel().selectedItemProperty().addListener(this);
		this.controller.initializeController(this, readingViewController);
	}
	
	/**
	 * Inform station view about its corresponding controller.
	 * 
	 * @param controller the controller to set
	 */
	@Override
	public void setStationController(final IStationController controller) {
		this.controller = controller;
	}
	
	/**
	 * Initialize list of monitoring stations.
	 * Currently the list contains only one string representing a monitoring station because there currently is no out-of-the-box list for multiple columns in JavaFX.
	 * 
	 * @param    stationIdentifyers    list of single strings representing a monitoring station each
	 */
	@Override
	public void overwriteUIStationList(final List<String> stationIdentifyers) {
		/*
		 * Initialize list of monitoring stations.
		 * Currently the list contains only one single string representing a monitoring station because there currently is no out-of-the-box list for multiple columns in JavaFX.
		 */
		final ObservableList<String> stationListData = FXCollections.observableArrayList( stationIdentifyers );
		logger.log(Level.FINE, "Initializing station list with these entries: "+stationListData);
		this.stationList.setItems(stationListData);
	}
	
	/**
	 * Set new selection for station view.
	 * @note    This implicitly overwrites current selection status in UI.
	 * 
	 * @param newExternalId    new selection for station view. Null is ignored. Use empty string instead.
	 */
	@Override
	public void overwriteUISelection(final String newExternalId) {
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
	 * Wipe any selection in station view.
	 */
	@Override
	public void wipeSelection() {
		this.stationList.getSelectionModel().clearSelection();
	}

	/**
	 * Handle selection change in station view.
	 * 
	 * @note: these changes are part of the presentation model: i. e. the portion of the application data representing the current state of the view.
	 */
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldStationValue, String newStationName) {
		/*
		 * don't propagate null or empty values
		 * if there is no selection (because of wrong or partial station name) or selection disappears, the new value is null, which must be ignored.
		 */
		if( null == newStationName || newStationName.isEmpty() || newStationName.isBlank() ) return;
		
		this.controller.handleUserChangedSelection(newStationName);
	}
}
