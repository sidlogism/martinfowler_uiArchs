/* Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
Licensed under the Universal Permissive License v 1.0 
as shown at http://oss.oracle.com/licenses/upl */

/*
 DESCRIPTION
 The code sample demonstrates establishing a connection to Autonomous Database (ATP/ADW) using
 Oracle JDBC driver and Universal Connection Pool (UCP). It does the following.  
 
 (a) Set the connection factory class name to 
 oracle.jdbc.pool.OracleDataSource before getting a connection.   
 (b) Set the connection pool properties(e.g.,minPoolSize, maxPoolSize). 
 (c) Get the connection and perform some database operations. 
 For a quick test, the sample retrieves 20 records from the Sales History (SH) schema 
 that is accessible to any DB users on autonomous Database.  
 
 Step 1: Enter the Database details DB_URL and DB_USER. 
 You will need to enter the DB_PASSWORD of your Autonomous Database through console
 while running the sample.  
 Step 2: Download the latest Oracle JDBC driver(ojdbc8.jar) and UCP (ucp.jar) 
 along with oraclepki.jar, osdt_core.jar and osdt_cert.jar and add to your classpath.  
 Refer to https://www.oracle.com/database/technologies/maven-central-guide.html
 Step 3: Compile and Run the sample. 
 
 SH Schema: 
 This sample uses the Sales History (SH) sample schema. SH is a data set suited for 
 online transaction processing operations. The Star Schema Benchmark (SSB) sample schema 
 is available for data warehousing operations. Both schemas are available 
 with your shared ADB instance and do not count towards your storage. 
 ou can use any ADB user account to access these schemas.
 
 NOTES
 Use JDK 1.8 and above 
  
 MODIFIED	(MM/DD/YY)
 nbsundar	11/09/2020 - Creation 
 */
package imperfectsilentart.martinfowler.uiArchs;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/*
 * Based on https://github.com/oracle/oracle-db-examples/blob/master/java/jdbc/ConnectionSamples/ADBQuickStart.java
 * 
 * The sample demonstrates connecting to Autonomous Database using 
 * Oracle JDBC driver and UCP as a client side connection pool.
 */
public class DbConnectionPoolTestOracleXE {  
	@Test
	public final void test() {
		final String DB_URL="jdbc:oracle:thin:@localhost:1521:XE";
		// Update the Database Username and Password to point to your Autonomous Database
		final String DB_USER = "XXX";
		final String DB_PASSWORD = "XXX";
		final String CONN_FACTORY_CLASS_NAME="oracle.jdbc.pool.OracleDataSource";
		
		// Get the PoolDataSource for UCP
		final PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();
	
		try {
			// Set the connection factory first before all other properties
			pds.setConnectionFactoryClassName(CONN_FACTORY_CLASS_NAME);
			pds.setURL(DB_URL);
			pds.setUser(DB_USER);
			pds.setPassword(DB_PASSWORD);
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
			try (Connection conn = pds.getConnection()) {
				System.out.println("Available connections after checkout: " + pds.getAvailableConnectionsCount());
				System.out.println("Borrowed connections after checkout: " + pds.getBorrowedConnectionsCount());		 
				// Perform a database operation
				queryAllDbUsers(conn);
			} catch (SQLException e) {
				fail("Error while accessing database: "+DB_URL+".\n"+e.getCause()+"\n"+e.getStackTrace());
			} 
			
			System.out.println("Available connections after checkin: "
				+ pds.getAvailableConnectionsCount());
			System.out.println("Borrowed connections after checkin: "
				+ pds.getBorrowedConnectionsCount());
		} catch (SQLException e) {
			fail("Error while accessing database: "+DB_URL+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
	}
	
	/*
	 * Select user name of all db users and print them to System.out.
	 */
	private static void queryAllDbUsers(Connection conn) throws SQLException {
		final String queryStatement = "SELECT username FROM dba_users";
		System.out.println("\n Query is " + queryStatement);
		
		conn.setAutoCommit(false);
		// Prepare a statement to execute the SQL Queries.
		try (
			final Statement statement = conn.createStatement(); 
			final ResultSet resultSet = statement.executeQuery(queryStatement)
		) {
			while (resultSet.next()) {
				System.out.println(resultSet.getString(1));
			}
		} catch (SQLException e) {
			fail("Error while creating, executing or evaluation query: \""+queryStatement+"\".\n"+e.getCause()+"\n"+e.getStackTrace());
		}
	}

  
}
