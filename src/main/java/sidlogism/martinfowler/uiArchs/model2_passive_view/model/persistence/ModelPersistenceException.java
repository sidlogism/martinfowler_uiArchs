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
package sidlogism.martinfowler.uiArchs.model2_passive_view.model.persistence;

/**
 * Custom exception for database access problems.
 * @note    Class name was chosen to avoid clashes with jakarta.persistence.PersistenceException.
 */
public class ModelPersistenceException extends Exception {
	private static final long serialVersionUID = -2945303167253987142L;

	/**
	 * @param message
	 */
	public ModelPersistenceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ModelPersistenceException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ModelPersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ModelPersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
