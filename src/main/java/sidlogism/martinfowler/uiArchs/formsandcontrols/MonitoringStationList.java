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
package sidlogism.martinfowler.uiArchs.formsandcontrols;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import sidlogism.martinfowler.uiArchs.formsandcontrols.persistence.DbAccessException;
import sidlogism.martinfowler.uiArchs.formsandcontrols.persistence.MonitoringStationDao;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

/**
 * Wrapper class for scrollable list of monitoring stations.
 * @see sidlogism.martinfowler.uiArchs.model2_passive_view.controller.MonitoringStationController
 */
public class MonitoringStationList{
	private static final Logger logger = Logger.getLogger(MonitoringStationList.class.getName());
	/*
	 * static members for singleton pattern
	 * NOTE: Enforcing singleton to the current implementation to avoid some multi-threading issues.
	 */
	private static MonitoringStationList instance = new MonitoringStationList();
	public static MonitoringStationList getInstance() {
		return MonitoringStationList.instance;
	}

	
	private final ListView<String> stationList = new ListView<String>();
	/**
	 * private default ctor for singleton pattern
	 * 
	 * Initializes the internal pane for scrollable list of monitoring stations.
	 */
	private MonitoringStationList() {
		/*
		 * Load data for scrollable list of monitoring stations.
		 */
		final MonitoringStationDao dao = new MonitoringStationDao();
		
		/*
		 * Currently the list contains only one string representing column monitoring_station.station_external_id.
		 * There currently is no out-of-the-box list for multiple columns in JavaFX.
		 */
		ObservableList<String> stationListData = null;
		try {
			stationListData = FXCollections.observableArrayList( dao.findAll() );
		} catch (DbAccessException e) {
			final ArrayList<String> data = new ArrayList<String>();
			data.add("- data access error -");
			stationListData = FXCollections.observableArrayList( data );
			logger.log(Level.SEVERE, "Failed to load data from DB.", e);
		}
		this.stationList.setItems(stationListData);
	}
	
	/**
	 * Registers the given listener to the list for selecting a monitoring station.
	 * 
	 * @param changeListener
	 */
	public void registerStationChangeListener(final ChangeListener<String> changeListener) {
		this.stationList.getSelectionModel().selectedItemProperty().addListener(changeListener);
	}
	
	/**
	 * Integrates the node of this pane into the given parent node.
	 * 
	 * @param parentPane    given parent node
	 */
	public void integrateIntoPane(final Pane parentPane) {
		parentPane.getChildren().add(this.stationList);
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
	 */
	public void wipeSelection() {
		this.stationList.getSelectionModel().clearSelection();
	}

}
