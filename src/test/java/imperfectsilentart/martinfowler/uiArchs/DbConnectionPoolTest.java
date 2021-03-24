/* Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
Licensed under the Universal Permissive License v 1.0 
as shown at http://oss.oracle.com/licenses/upl
 * 
 * Based on https://github.com/oracle/oracle-db-examples/blob/master/java/jdbc/ConnectionSamples/ADBQuickStart.java
 */
package imperfectsilentart.martinfowler.uiArchs;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;




/**
 * Simple tests for DB connectivity via connection pool.
 */
public class DbConnectionPoolTest {
	/**
	 * @throws org.json.JSONException
	 */
	@BeforeAll
	public static void setup() throws IOException, URISyntaxException{
		ConfigParser.getInstance().parseConfig();
	}

	
	/**
	 * Test connecting to DB instance via connection pool, run test query and print result.
	 * 
	 * @param dbsName    configuration identifier of the tested DBS
	 */
	@ParameterizedTest
	@ValueSource(strings = { "oracleXE" })
	public final void testQueryExecution(final String dbsName) {
		JSONObject dbParameters = null;
		JSONArray testQueries = null;
		String queryText = null;
		try {
			dbParameters = ConfigParser.getInstance().getRootNode().getJSONObject("dbParameters").getJSONObject(dbsName);
			testQueries = ConfigParser.getInstance().getRootNode().getJSONObject("testQueries").getJSONArray(dbsName);
			queryText = testQueries.getString(0);
		}catch(JSONException e) {
			fail("Failed reading configuration: Could not get connection parameters or test queries for DBS "+dbsName+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
		
		// create connection via conneciton pool
		Connection conn = null;
		switch(dbsName) {
		default:
		case "oracleXE": 
			// Get the PoolDataSource for Oracle's UCP (Universal Connection Pool)
			final PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();
			try {
				// Set the connection factory first before all other properties
				pds.setConnectionFactoryClassName(dbParameters.getString("connectionFactoryClassName"));
				pds.setURL(dbParameters.getString("connectionUrl"));
				pds.setUser(dbParameters.getString("user"));
				pds.setPassword(dbParameters.getString("password"));
				pds.setConnectionPoolName("JDBC_UCP_POOL");
				
				// Default is 0. Set the initial number of connections to be created
				// when UCP is started.
				pds.setInitialPoolSize(5);
				
				// Default is 0. Set the minimum number of connections
				// that is maintained by UCP at runtime.
				pds.setMinPoolSize(5);
				
				// Default is Integer.MAX_VALUE (2147483647). Set the maximum number of
				// connections allowed on the connection pool.
				pds.setMaxPoolSize(20);
				
				
				// Get the database connection from UCP.
				System.out.println ("Connecting to database: "+dbParameters.getString("connectionUrl"));
				conn = pds.getConnection();
			} catch (SQLException e) {
				fail("Error while accessing database: "+dbParameters.getString("connectionUrl")+".\n"+e.getCause()+"\n"+e.getStackTrace());
			}
		}
		
		
		if(null == conn) fail("Failed initializing connection to database: "+dbParameters.getString("connectionUrl"));
		// execute test query
		try {
			executeQuery(conn, queryText);
			conn.close();
		} catch (SQLException e) {
			fail("Error while accessing database: "+dbParameters.getString("connectionUrl")+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
		
		
		// just for better separation of tests outputs
		System.out.println("\n\n\n\n");
	}

	/**
	 * Connect to DB instance, run test query and print result.
	 * 
	 * @param dbParameters    configuration holding DB connection parameters
	 * @param testQueries    configuration holding texts of test queries
	 */
	private void executeQuery(final Connection connection, final String queryText) {
		try(
			final Statement stmt = connection.createStatement();
		){
			connection.setAutoCommit(false);
			
			
			System.out.println ("Executing query: "+queryText);
			final ResultSet result = stmt.executeQuery(queryText);
			
			
			while (result.next()) {
				System.out.println(result.getString(1));
			}
			result.close();
			connection.close();
		} catch (SQLException e) {
			fail("Error while creating, executing or evaluating query: \""+queryText+"\".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
	}

}
