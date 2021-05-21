package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;

/**
 * Simple observer type for changes in actual concentration value.
 */
public interface IActualConcentrationListener {
	/**
	 * Handle change in actual concentration value.
	 * @param newValue    changed actual concentration value
	 */
	void updateActualConcentration(int newValue);
}