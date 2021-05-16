package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller;

/**
 * Business logic for accessing and initializing assessment UI main view.
 */
public interface IAssessmentFormController {
	/**
	 * Construct and display assessment UI main view.
	 * 
	 * @param args    command line arguments
	 */
	public void launchUi(final String[] args);

}