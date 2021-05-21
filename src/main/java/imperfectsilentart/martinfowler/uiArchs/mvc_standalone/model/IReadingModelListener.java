package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model;

/**
 * Simple observer type for changes in reading model.
 */
public interface IReadingModelListener {
	/**
	 * Handle change in actual concentration value.
	 * @param newValue    changed actual concentration value
	 */
	void actualConcentrationChanged(int newValue);
}