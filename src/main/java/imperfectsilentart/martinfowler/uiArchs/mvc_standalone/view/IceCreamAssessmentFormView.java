package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * "Standalone MVC" version of assessment form from https://www.martinfowler.com/eaaDev/uiArchs.html .
 */
public class IceCreamAssessmentFormView extends Application{
	private static final Logger logger = Logger.getLogger(IceCreamAssessmentFormView.class.getName());

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

	public static void main(final String[] args){
		launch(args);
	}

}
