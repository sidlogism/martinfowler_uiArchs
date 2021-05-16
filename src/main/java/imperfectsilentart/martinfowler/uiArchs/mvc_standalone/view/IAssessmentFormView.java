package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;

/**
 * "Standalone MVC" version of assessment form-UI from https://www.martinfowler.com/eaaDev/uiArchs.html .
 */
public interface IAssessmentFormView {
	/**
	 * Launches a new UI-instance on the screen.
	 * 
	 * @param args
	 */
	public void launchUi(final String[] args);

}