package sidlogism.martinfowler.uiArchs.model2_passive_view.view;

/**
 * Variance of the Model2-pattern or MVP-pattern with a 'passive view' (see https://www.martinfowler.com/eaaDev/PassiveScreen.html) of assessment form-UI from https://www.martinfowler.com/eaaDev/uiArchs.html .
 */
public interface IAssessmentFormView {
	/**
	 * Launches a new UI-instance on the screen.
	 * 
	 * @param args
	 */
	public void launchUi(final String[] args);

}