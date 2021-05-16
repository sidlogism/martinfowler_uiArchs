package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.MvcInitializationException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller.MonitoringStationController;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.controller.ReadingDataSheetController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * "Standalone MVC" version of assessment form-UI from https://www.martinfowler.com/eaaDev/uiArchs.html .
 */
public class AssessmentFormView extends Application implements IAssessmentFormView{
	private static final Logger logger = Logger.getLogger(AssessmentFormView.class.getName());
	//TODO model
	private MonitoringStationController stationController = null;
	/**
	 * Launches a new UI-instance on the screen.
	 * 
	 * @param args
	 */
	@Override
	public void launchUi(final String[] args){
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws IOException
	{
		final Parent root = FXMLLoader.load(getClass().getResource("icecream_assessment.fxml"));
		stage.setScene(new Scene(root, 650, 200));
		
		
		
		

		// set stage attributes
		stage.getScene().getStylesheets().add(getClass().getResource("icecream_assessment.css").toExternalForm());
		stage.setTitle("Assessment Record (\"Standalone MVC\" version)");
		stage.setResizable(true);
		stage.centerOnScreen();
		// make stage visible
		logger.log(Level.INFO, "Displaying and starting application "+this.getClass().getName() );
		stage.show();
	}


}
