package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view.MonitoringStationView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for assessment UI main view.
 * 
 * @see imperfectsilentart.martinfowler.uiArchs.formsandcontrols.IceCreamAssessmentForm
 */
public class AssessmentFormController extends Application implements IAssessmentFormController{
	private static final Logger logger = Logger.getLogger(AssessmentFormController.class.getName());
	/**
	 * First create top most controller i. e. station controller in order to inform station view about its corresponding controller later.
	 * The nested reading controller is created internally by station controller.
	 * I. e. controllers are constructed in the same nested fashion as their views.
	 */
	private IStationController stationController = null;
	
	public AssessmentFormController() {
		this.stationController = new MonitoringStationController();
	}
	
	/**
	 * Construct and launch assessment UI main view.
	 * @note    IMPORTANT: any internal initialization should be done in constructor and not here.
	 * 
	 * @param args    command line arguments
	 */
	@Override
	public void launchUi(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws IOException
	{
		final FXMLLoader loader = new FXMLLoader( MonitoringStationView.class.getResource("icecream_assessment.fxml") );
		// create station view and inform it about its corresponding controller
		final MonitoringStationView stationView = new MonitoringStationView( );
		stationView.setStationController(this.stationController);
		// Hand station view to application loader. NOTE: In this subproject "fx:controller" references view objects!
		loader.setController(stationView);
		
		final Parent root = loader.load();
		stage.setScene(new Scene(root, 650, 200));
		
		// set stage attributes
		stage.getScene().getStylesheets().add( MonitoringStationView.class.getResource("icecream_assessment.css").toExternalForm() );
		stage.setTitle("Assessment Record (\"Standalone MVC\" version)");
		stage.setResizable(true);
		stage.centerOnScreen();
		// make stage visible
		logger.log(Level.INFO, "Displaying and starting application "+this.getClass().getName() );
		stage.show();
	}


}
