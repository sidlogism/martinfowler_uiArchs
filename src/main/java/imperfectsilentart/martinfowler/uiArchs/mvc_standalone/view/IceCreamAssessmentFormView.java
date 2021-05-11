package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;


import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ConcentrationReading;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PeristenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PersistenceTools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * "Standalone MVC" version of assessment form-UI from https://www.martinfowler.com/eaaDev/uiArchs.html .
 */
public class IceCreamAssessmentFormView extends Application{
	private static final Logger logger = Logger.getLogger(IceCreamAssessmentFormView.class.getName());
	//TODO model
	//TODO interface
	
	
	/**
	 * Launches a new UI-instance on the screen.
	 * 
	 * @param args
	 */
	public void launchUi(final String[] args){
		launch(args);
	}

	@Override
	public void start(final Stage stage) throws IOException
	{
		final Parent root = FXMLLoader.load(getClass().getResource("icecream_assessment.fxml"));
		stage.setScene(new Scene(root, 650, 200));
		
		
		
		//FIXME JPA test
		EntityManager em = null;
		try {
			em = PersistenceTools.getEntityManager();
			em.getTransaction().begin();
			List<MonitoringStation> result = em.createQuery( "from monitoring_station", MonitoringStation.class ).getResultList();
			for ( MonitoringStation station : result ) {
				System.out.println( "Station: " + station.getStationName() );
			}
			final MonitoringStation firstStation = result.get(0);
			System.out.println( "1st station: " + firstStation );
			final Collection<ConcentrationReading> readings = firstStation.getReadings();
			for ( ConcentrationReading r : readings ) {
				System.out.println( "1st station reading: " + r );
			}
			
			em.getTransaction().commit();
		} catch (PeristenceException e) {
			logger.log(Level.WARNING, "Failed to execute query ... ", e);
		}finally {
			if(null != em) em.close();
		}
		

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
