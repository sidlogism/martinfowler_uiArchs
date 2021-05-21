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

import java.time.LocalDateTime;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller.IReadingController;
import imperfectsilentart.martinfowler.uiArchs.util.TimeProcessingException;

public interface IReadingView {

	/**
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 * Since the reading view is nested in or wrapped by other views and cannot be accessed directly
	 * 
	 * @param readingController    ReadingController reference to forwarded to at reading view at initialization.
	 */
	public void setReadingController(final IReadingController controller);

	/**
	 * @param currentReadingId the currentReadingId to set
	 */
	public void setCurrentReadingId(long currentReadingId);

	/**
	 * @return the stationExternalId
	 */
	String getStationExternalId();

	/**
	 * @param stationExternalId the stationExternalId to set
	 * @note    This implicitly overwrites current stationExternalId in UI.
	 */
	void overwriteUIStationExternalId(String stationExternalId);

	/**
	 * @return the targetConcentration
	 */
	int getTargetConcentration();

	/**
	 * @param targetConcentration the targetConcentration to set
	 * @note    This implicitly overwrites current targetConcentration in UI.
	 */
	void overwriteUITargetConcentration(int targetConcentration);

	/**
	 * @return the readingTimestamp
	 */
	LocalDateTime getReadingTimestamp();

	/**
	 * @param readingTimestamp the readingTimestamp to set
	 * @throws TimeProcessingException
	 * @note    This implicitly overwrites current readingTimestamp in UI. 
	 */
	void overwriteUIReadingTimestamp(LocalDateTime readingTimestamp);

	/**
	 * @return the actualConcentration
	 */
	int getActualConcentration();

	/**
	 * @param actualConcentration the actualConcentration to set
	 * @note    This implicitly overwrites current actualConcentration in UI.
	 */
	void overwriteUIActualConcentration(int actualConcentration);

	/**
	 * Wipes all text fields which depend on external station ID.
	 * The current concentration reading record indirectly depends on the current monitoring station record.
	 */
	void wipeAllDependentTextFields();
	
	/**
	 * Wipes all text field which depend on the current concentration reading record.
	 */
	void wipeReadingDependentTextFields();
}