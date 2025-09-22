package sidlogism.martinfowler.uiArchs.formsandcontrols;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * "Forms and Controls" version of assessment form from https://www.martinfowler.com/eaaDev/uiArchs.html .
 * 
 * @note    For simplicity DB table locking is done pessimistic.
 * @see sidlogism.martinfowler.uiArchs.model2_passive_view.view.AssessmentFormView
 */
public class IceCreamAssessmentForm extends Application{
	private static final Logger logger = Logger.getLogger(IceCreamAssessmentForm.class.getName());

	@Override
	public void start(final Stage stage)
	{
		final HBox rootPane = new HBox(10);
		rootPane.setPadding(new Insets(7,7,7,7));
		rootPane.setAlignment(Pos.BASELINE_LEFT);
		
		/*
		 * Mutually register listener/event handler for changes of selected station either in the list or in the reading data sheet.
		 * 
		 * On change of selected station in list, the value of the text field "Station ID" is updated.
		 * On change of the text field "Station ID", the selected station in the list is updated as well.
		 * IMPORTANT: avoid circular listener update notifications by only changing "Station ID" text or list selection if the new value is different from the currently present value.
		 * 
		 * NOTE: elegant binding (e. g. bindBidirectional()) not easily possible, because SelectionModel.selectedItemProperty() returns ReadOnlyObjectProperty, which cannot be bound directly.
		 */
		MonitoringStationList.getInstance().registerStationChangeListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov, String oldStationName, String newStationName) {
						// don't propagate null or empty values
						// if there is no selection (because of wrong or partial station name) or selection disappears, the new value is null, which must be ignored.
						if( null == newStationName || newStationName.isEmpty() || newStationName.isBlank() ) return;
						
						if(! ReadingDataSheet.getInstance().switchContents(newStationName) ) {
							//if there is a problem with the new station, wipe selection
							MonitoringStationList.getInstance().wipeSelection();
						}
					}
				}
			);
		ReadingDataSheet.getInstance().registerStationChangeListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov, String oldStationName, String newStationName) {
						// don't propagate null or empty values
						if( null == newStationName || newStationName.isEmpty() || newStationName.isBlank() ) return;
						
						MonitoringStationList.getInstance().changeSelection(newStationName);
					}
				}
			);
		/*
		 * Register listener for updating the actual concentration value if the value was changed in the input form.
		 */
		ReadingDataSheet.getInstance().registerActualConcentrationChangeListener();

		/*
		 * Construct new scene consisting of two elements and link the scene to the stage.
		 * Leftmost element: a list of monitoring stations
		 * Rightmost element: fields & entries of an ice cream reading record and the corresponding monitoring station
		 */
		MonitoringStationList.getInstance().integrateIntoPane(rootPane);
		ReadingDataSheet.getInstance().integrateIntoPane(rootPane);
		ReadingDataSheet.getInstance().setHorizontalGrowthPolicy(Priority.ALWAYS);
		stage.setScene(new Scene(rootPane, 650, 200));
		// set stage attributes
		stage.setTitle("Assessment Record (\"Forms and Controls\" version)");
		stage.setResizable(true);  
		stage.centerOnScreen();
		// make stage visible
		logger.log(Level.INFO, "Displaying and starting application "+this.getClass().getName() );
		stage.show();
	}

	public static void main(final String[] args){
		launch(args);
	}

}
