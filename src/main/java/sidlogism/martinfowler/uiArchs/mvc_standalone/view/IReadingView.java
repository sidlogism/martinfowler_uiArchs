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
package sidlogism.martinfowler.uiArchs.mvc_standalone.view;

import java.time.LocalDateTime;

import sidlogism.martinfowler.uiArchs.mvc_standalone.controller.IReadingController;
import sidlogism.martinfowler.uiArchs.util.TimeProcessingException;

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
	 * Visually mark the content of actualConcentration field as ERRONEOUS in UI.
	 */
	void markUIStationExternalIdErroneous();
	
	/**
	 * Visually mark the content of actualConcentration field as VALID in UI.
	 */
	void markUIStationExternalIdValid();

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
	 * Visually mark the content of actualConcentration field as ERRONEOUS in UI.
	 */
	void markUIActualConcentrationErroneous();
	
	/**
	 * Visually mark the content of actualConcentration field as VALID in UI.
	 */
	void markUIActualConcentrationValid();

	/**
	 * Wipes all text fields which depend on external station ID.
	 * @note	The current concentration reading record indirectly depends on the current monitoring station record.
	 */
	void wipeAllDependentTextFields();
	
	/**
	 * Change editability in UI: give back editability in UI to all text fields which depend on external station ID, which were originally editable.
	 * @note	The current concentration reading record indirectly depends on the current monitoring station record.
	 */
	void restoreEditabilityOnAllDependentTextFields();
	
	/**
	 * Change editability in UI: remove editability in UI from all text fields which depend on external station ID, which are originally editable.
	 * @note	The current concentration reading record indirectly depends on the current monitoring station record.
	 */
	void removeEditabilityFromAllDependentTextFields();
	
	/**
	 * Wipes all text field which depend on the current concentration reading record.
	 */
	void wipeReadingDependentTextFields();
	
	/**
	 * Change editability in UI: give back editability in UI to all text field which depend on the current concentration reading record, which were originally editable.
	 */
	void restoreEditabilityOnReadingDependentTextFields();
	
	/**
	 * Change editability in UI: remove editability in UI from all text field which depend on the current concentration reading record, which are originally editable.
	 */
	void removeEditabilityFromReadingDependentTextFields();
}