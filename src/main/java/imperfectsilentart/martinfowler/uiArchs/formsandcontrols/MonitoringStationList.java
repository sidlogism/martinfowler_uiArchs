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
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper class for scrollable list of monitoring stations.
 */
public class MonitoringStationList{
	/*
	 * static members for singleton pattern
	 */
	private static MonitoringStationList instance = new MonitoringStationList();
	public static MonitoringStationList getInstance() {
		return MonitoringStationList.instance;
	}
	
	/*
	 * dynamic members
	 */
	final ListView<String> stationList = new ListView<String>();

	private MonitoringStationList() {
		// TODO sample data
		final ObservableList<String> stationListData = FXCollections.observableArrayList(
				"chocolate", "salmon", "gold", "coral", "darkorchid",
				"darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
				"blueviolet", "brown");
		stationList.setItems(stationListData);
	}
	
	public void registerStationChangeListener(final ChangeListener<String> changeListener) {
		this.stationList.getSelectionModel().selectedItemProperty().addListener(changeListener);
	}
	public void integrateIntoPane(final Pane parentPane) {
		parentPane.getChildren().add(this.stationList);
	}
	
	/**
	 * Set new selection for monitoring station list.
	 * 
	 * @param newStationName    new selection for monitoring station list. Null is ignored. Use empty string instead.
	 */
	public void changeSelection(final String newStationName) {
		if(null == newStationName) {
			return;
		}
		this.stationList.getSelectionModel().select(newStationName);
		//FIXME handle incomplete partial names or missing hits
	}


}
