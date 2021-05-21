package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;

import java.time.LocalDateTime;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller.IReadingController;
import imperfectsilentart.martinfowler.uiArchs.util.TimeProcessingException;

public interface IReadingView {

	/**
	 * IMPORTANT: For keeping station view and reading view in sync, the corresponding controllers must know each other.
	 * This link is established here.
	 * Since the reading view is nested in or wrapped by other views and cannot be accessed directly
	 * 
	 * @param readingController    ReadingController reference to forwarded to at reading view at initialization.
	 */
	public void setReadingController(final IReadingController controller);

	/**
	 * @param currentReadingId the currentReadingId to set
	 */
	public void setCurrentReadingId(long currentReadingId);

	/**
	 * @return the stationExternalId
	 */
	String getStationExternalId();

	/**
	 * @param stationExternalId the stationExternalId to set
	 */
	void overwriteUIStationExternalId(String stationExternalId);

	/**
	 * @return the targetConcentration
	 */
	int getTargetConcentration();

	/**
	 * @param targetConcentration the targetConcentration to set
	 */
	void overwriteUITargetConcentration(int targetConcentration);

	/**
	 * @return the readingTimestamp
	 */
	LocalDateTime getReadingTimestamp();

	/**
	 * @param readingTimestamp the readingTimestamp to set
	 * @throws TimeProcessingException 
	 */
	void overwriteUIReadingTimestamp(LocalDateTime readingTimestamp);

	/**
	 * @return the actualConcentration
	 */
	int getActualConcentration();

	/**
	 * @param actualConcentration the actualConcentration to set
	 */
	void overwriteUIActualConcentration(int actualConcentration);

	/**
	 * Recomputes the concentration variance based on the given values.
	 * @note    This implicitly overwrites current selection status in UI.
	 * @note: like the selection state and text contents this is part of the presentation model: i. e. the portion of the application data representing the current state of the view.
	 * 
	 * @param actualConcentration    value of the actual concentration measured
	 * @param targetConcentration    value of the target concentration to be reached
	 */
	//void overwriteUIVariance(int actualConcentration, int targetConcentration);

	/**
	 * Wipes all text fields which depend on external station ID.
	 * The current concentration reading record indirectly depends on the current monitoring station record.
	 */
	void wipeAllDependentTextFields();
	
	/**
	 * Wipes all text field which depend on the current concentration reading record.
	 */
	void wipeReadingDependentTextFields();
}