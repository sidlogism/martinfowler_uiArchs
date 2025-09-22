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
package sidlogism.martinfowler.uiArchs.model2_passive_view.controller;

import sidlogism.martinfowler.uiArchs.model2_passive_view.view.AssessmentFormView;
import sidlogism.martinfowler.uiArchs.model2_passive_view.view.IAssessmentFormView;

/**
 * Business logic for accessing and initializing assessment UI main view.
 */
public class AssessmentFormController implements IAssessmentFormController {
	private IAssessmentFormView view = new AssessmentFormView();
	
	/**
	 * Construct and display assessment UI main view.
	 * 
	 * @param args    command line arguments
	 */
	@Override
	public void launchUi(final String[] args) {
		this.view.launchUi(args);
	}
}
