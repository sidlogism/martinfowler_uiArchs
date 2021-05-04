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
package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.IceCreamAssessmentFormView;

/**
 * Beschreibung
 *
 * @author Imperfect Silent Art
 */
public class IceCreamAssessmentFormController {
	//TODO model
	//TODO interface
	private IceCreamAssessmentFormView view = new IceCreamAssessmentFormView();
	
	public void launchUi(final String[] args) {
		this.view.launchUi(args);
	}
}
