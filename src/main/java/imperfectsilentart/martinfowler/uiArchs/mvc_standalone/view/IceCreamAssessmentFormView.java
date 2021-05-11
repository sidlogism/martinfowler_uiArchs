package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
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
		final Map<String, Object> configOverrides = new HashMap<String, Object>();
		configOverrides.put("javax.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
		configOverrides.put("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/martinfowler_uiArchs?useUnicode=true&characterEncoding=UTF8");
		configOverrides.put("javax.persistence.jdbc.user", "XXX");
		configOverrides.put("javax.persistence.jdbc.password", "XXX");
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("martinfowler_uiArchs_pu", configOverrides);
		final EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		List<MonitoringStation> result = em.createQuery( "from monitoring_station", MonitoringStation.class ).getResultList();
		for ( MonitoringStation station : result ) {
			System.out.println( "Station: " + station.getStationName() );
		}
		em.getTransaction().commit();
		em.close();
		
		

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
