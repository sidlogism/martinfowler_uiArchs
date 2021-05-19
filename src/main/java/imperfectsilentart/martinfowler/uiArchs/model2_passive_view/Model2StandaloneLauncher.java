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
package imperfectsilentart.martinfowler.uiArchs.model2_passive_view;

import imperfectsilentart.martinfowler.uiArchs.model2_passive_view.controller.AssessmentFormController;
import imperfectsilentart.martinfowler.uiArchs.model2_passive_view.controller.IAssessmentFormController;

/**
 * Variance of the Model2-pattern or MVP-pattern with a 'passive view' (see https://www.martinfowler.com/eaaDev/PassiveScreen.html) of assessment application from https://www.martinfowler.com/eaaDev/uiArchs.html .
 */

public class Model2StandaloneLauncher {
	/**
	 * The controller initializes the internal application prerequirements and then launches the UI.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//TODO suffices strategy pattern?
		final IAssessmentFormController controller = new AssessmentFormController();
		controller.launchUi(args);
	}
}
