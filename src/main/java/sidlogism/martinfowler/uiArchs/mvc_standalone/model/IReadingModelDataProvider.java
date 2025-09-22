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
package sidlogism.martinfowler.uiArchs.mvc_standalone.model;

import sidlogism.martinfowler.uiArchs.mvc_standalone.model.persistence.ConcentrationReading;

/**
 * Simple data provider (observable type) for reading model.
 */
public interface IReadingModelDataProvider {
	
	/**
	 * Adds new listener to this data provider's observer list.
	 * 
	 * @param listener
	 */
	public void addReadingModelListener(final IReadingModelListener listener);
	
	/**
	 * Removes given listener from this data provider's observer list.
	 * 
	 * @param listener
	 */
	public void removeReadingModelListener(final IReadingModelListener listener);
	
	/**
	 * Notify all listeners in this data provider's observer list about a data change.
	 * 
	 * @param listener
	 */
	public void notifyReadingModelListeners(final ConcentrationReading reading);

}