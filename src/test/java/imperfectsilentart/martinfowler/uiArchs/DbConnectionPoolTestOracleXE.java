/* Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
Licensed under the Universal Permissive License v 1.0 
as shown at http://oss.oracle.com/licenses/upl
 * 
 * Based on https://github.com/oracle/oracle-db-examples/blob/master/java/jdbc/ConnectionSamples/ADBQuickStart.java
 * Modification to ADBQuickStart.java by Imperfect Silent Art:
 * Description removed.
 * Namespace & class name & main-method name altered.
 * TNS-URL removed.
 * Reading fom System.in removed.
 * doSQLWork() method removed.
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
