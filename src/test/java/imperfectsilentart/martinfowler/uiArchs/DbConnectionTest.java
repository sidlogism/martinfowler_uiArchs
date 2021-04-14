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
package imperfectsilentart.martinfowler.uiArchs;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;



/**
 * Simple tests for DB connectivity.
 */
public class DbConnectionTest {
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
		System.out.println("\n\n\n\n");
	}
	
	/**
	 * Test connecting to DB instance, run test query and print result.
	 * 
	 * @param dbsName    configuration identifier of the tested DBS
	 */
	@ParameterizedTest
	@ValueSource(strings = { "mysql", "oracleXE" })
	public void testQueryExecution(final String dbsName) {
		JSONObject dbParameters = null;
		JSONArray testQueries = null;
		try {
			dbParameters = ConfigParser.getInstance().getRootNode().getJSONObject("dbParameters").getJSONObject(dbsName);
			testQueries = ConfigParser.getInstance().getRootNode().getJSONObject("testQueries").getJSONArray(dbsName);
		}catch(JSONException e) {
			fail("Failed reading configuration: Could not get connection parameters or test queries for DBS "+dbsName+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
		
		
		executeQuery(dbParameters, testQueries);
		// just for better separation of tests outputs
		System.out.println("\n\n\n\n");
	}
	
	/**
	 * Load DB-driver for given DBS.
	 * 
	 * @param dbParameters    configuration holding DB connection parameters
	 */
	private void loadDbDriver(final JSONObject dbParameters) {
		System.out.println ("Loading JDBC driver "+dbParameters.getString("driverName"));
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
	private void executeQuery(final JSONObject dbParameters, final JSONArray testQueries) {
		System.out.println ("Connecting to database: "+dbParameters.getString("connectionUrl"));
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
			
			
			System.out.println ("Executing query: "+queryText);
			try(
				final ResultSet result = stmt.executeQuery(queryText);
			){
				while (result.next()) {
					System.out.println(result.getString(1));
				}
			} catch (SQLException e2) {
				fail("Error while accessing database: "+dbParameters.getString("connectionUrl")+".\n"+e2.getCause()+"\n"+e2.getStackTrace());
			}
		} catch (SQLException e) {
			fail("Error while accessing database: "+dbParameters.getString("connectionUrl")+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
	}

}
