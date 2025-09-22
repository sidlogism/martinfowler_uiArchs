package sidlogism.martinfowler.uiArchs.mvc_standalone.controller;

/**
 * Business logic for accessing and initializing assessment UI main view.
 */
public interface IAssessmentFormController {
	/**
	 * Construct and launch assessment UI main view.
	 * @note    IMPORTANT: any internal initialization should be done in constructor and not here.
	 * 
	 * @param args    command line arguments
	 */
	public void launchUi(final String[] args);

}