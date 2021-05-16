/*
 * Copyright 2021 Imperfect Silent Art
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package imperfectsilentart.martinfowler.uiArchs.persistence;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.ConcentrationReading;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.MonitoringStation;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PeristenceException;
import imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence.PersistenceTools;
import imperfectsilentart.martinfowler.uiArchs.util.ConfigParser;
import imperfectsilentart.martinfowler.uiArchs.util.FileSystemAccessException;



/**
 * Simple tests for DB connectivity.
 */
public class DbConnectionTest {
	private static final Logger logger = Logger.getLogger(DbConnectionTest.class.getName());
	/**
	 * @throws FileSystemAccessException 
	 * @throws org.json.JSONException
	 */
	@BeforeAll
	public static void setup() throws IOException, URISyntaxException, FileSystemAccessException{
		ConfigParser.getInstance().parseConfig();
	}

	/**
	 * Test loading DB-driver for given DBS.
	 * 
	 * @param dbsName    configuration identifier of the tested DBS
	 */
	@ParameterizedTest
	@ValueSource(strings = { "mysql", "oracleXE" })
	public void testLoadingDbDriver(final String dbsName) {
		JSONObject dbParameters = null;
		try {
			dbParameters = ConfigParser.getInstance().getRootNode().getJSONObject("dbParameters").getJSONObject(dbsName);
		}catch(JSONException e) {
			fail("Failed reading configuration: Could not get connection parameters for DBS "+dbsName+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
		
		
		loadDbDriver(dbParameters);
		// just for better separation of tests outputs
		logger.log(Level.INFO, "\n\n\n\n");
	}
	
	/**
	 * Test connecting to DB instance, run test query and print result.
	 * 
	 * @param dbsName    configuration identifier of the tested DBS
	 */
	@ParameterizedTest
	@ValueSource(strings = { "mysql", "oracleXE" })
	public void testRawQueryExecution(final String dbsName) {
		JSONObject dbParameters = null;
		JSONArray testQueries = null;
		try {
			dbParameters = ConfigParser.getInstance().getRootNode().getJSONObject("dbParameters").getJSONObject(dbsName);
			testQueries = ConfigParser.getInstance().getRootNode().getJSONObject("testQueries").getJSONArray(dbsName);
		}catch(JSONException e) {
			fail("Failed reading configuration: Could not get connection parameters or test queries for DBS "+dbsName+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
		
		
		executeRawQuery(dbParameters, testQueries);
		// just for better separation of tests outputs
		logger.log(Level.INFO, "\n\n\n\n");
	}
	
	/**
	 * Test connecting to DB instance, run test query and print result.
	 * 
	 * @param dbsName    configuration identifier of the tested DBS
	 */
	@Test
	public void testCreateEntityManager() {
		EntityManager em = null;
		try {
			em = PersistenceTools.getEntityManager();
		} catch (PeristenceException e) {
			logger.log(Level.WARNING, "Failed to execute query ... ", e);
		}finally {
			if(null != em) em.close();
		}
		// just for better separation of tests outputs
		logger.log(Level.INFO, "\n\n\n\n");
	}
	
	/**
	 * Test connecting to DB instance, run test query and print result.
	 * 
	 * @param dbsName    configuration identifier of the tested DBS
	 */
	@Test
	public void testJpaQueryExecution() {
		EntityManager em = null;
		try {
			em = PersistenceTools.getEntityManager();
			em.getTransaction().begin();
			
			//1st query
			List<MonitoringStation> result = em.createQuery( "from monitoring_station", MonitoringStation.class ).getResultList();
			for ( MonitoringStation station : result ) {
				logger.log(Level.INFO, "Station: " + station.getStationName() );
			}
			final MonitoringStation firstStation = result.get(0);
			logger.log(Level.INFO,  "1st station: " + firstStation );
			
			//second query
			final Collection<ConcentrationReading> readings = firstStation.getReadings();
			final List<ConcentrationReading> sortedReadings = readings.stream().collect( Collectors.toList());
			Collections.sort(sortedReadings);
			for ( ConcentrationReading r : sortedReadings ) {
				logger.log(Level.INFO, "1st station reading: " + r );
			}
			
			em.getTransaction().commit();
		} catch (PeristenceException e) {
			logger.log(Level.WARNING, "Failed to execute query ... ", e);
		}finally {
			if(null != em) em.close();
		}
		// just for better separation of tests outputs
		logger.log(Level.INFO, "\n\n\n\n");
	}
	
	/**
	 * Load DB-driver for given DBS.
	 * 
	 * @param dbParameters    configuration holding DB connection parameters
	 */
	private void loadDbDriver(final JSONObject dbParameters) {
		logger.log(Level.INFO, "Loading JDBC driver "+dbParameters.getString("driverName"));
		try {
			Class.forName(dbParameters.getString("driverName"));
		} catch (ClassNotFoundException e){
			fail("Could not load the driver "+dbParameters.getString("driverName")+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
	}

	/**
	 * Connect to DB instance, run test query and print result.
	 * 
	 * @param dbParameters    configuration holding DB connection parameters
	 * @param testQueries    configuration holding texts of test queries
	 */
	private void executeRawQuery(final JSONObject dbParameters, final JSONArray testQueries) {
		logger.log(Level.INFO, "Connecting to database: "+dbParameters.getString("connectionUrl"));
		String queryText = null;
		try {
			queryText = testQueries.getString(0);
		}catch(JSONException e) {
			fail("Failed reading configuration.\n"+e.getCause()+"\n"+e.getStackTrace());
		}
		try(
			final Connection connection = DriverManager.getConnection(dbParameters.getString("connectionUrl"), dbParameters.getString("user"), dbParameters.getString("password"));
			final Statement stmt = connection.createStatement();
		){
			connection.setAutoCommit(false);
			
			
			logger.log(Level.INFO, "Executing query: "+queryText);
			try(
				final ResultSet result = stmt.executeQuery(queryText);
			){
				while (result.next()) {
					logger.log(Level.INFO, result.getString(1));
				}
			} catch (SQLException e2) {
				fail("Error while accessing database: "+dbParameters.getString("connectionUrl")+".\n"+e2.getCause()+"\n"+e2.getStackTrace());
			}
		} catch (SQLException e) {
			fail("Error while accessing database: "+dbParameters.getString("connectionUrl")+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
	}

}
