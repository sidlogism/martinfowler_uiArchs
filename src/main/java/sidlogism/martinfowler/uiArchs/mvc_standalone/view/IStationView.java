/*
 * Copyright 2025 Sidlogism
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
package sidlogism.martinfowler.uiArchs.mvc_standalone.view;

import java.util.List;

import sidlogism.martinfowler.uiArchs.mvc_standalone.controller.IStationController;

public interface IStationView {
	/**
	 * Inform station view about its corresponding controller.
	 * 
	 * @param controller the controller to set
	 */
	public void setStationController(final IStationController controller);
	
	/**
	 * Initialize list of monitoring stations.
	 * Currently the list contains only one string representing a monitoring station because there currently is no out-of-the-box list for multiple columns in JavaFX.
	 * 
	 * @param    stationIdentifiers    list of single strings representing a monitoring station each
	 * @note    This implicitly overwrites list of monitoring stations in UI.
	 */
	public void overwriteUIStationList(final List<String> stationIdentifiers);

	/**
	 * Set new selection for station view.
	 * 
	 * @param newExternalId    new selection for station view. Null is ignored. Use empty string instead.
	 * @note    This implicitly overwrites current selection status in UI.
	 */
	public void overwriteUISelection(final String newExternalId);

	/**
	 * Wipe any selection in station view.
	 */
	public void wipeSelection();
}