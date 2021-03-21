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

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Test;



/**
 * Simple test for connecting to MySQL DB instance, run sample read-only query and output result.
 * 
 * TODO merge all DbConnectionTest* classes and initialize with different parameters for each test run (avoid redundant code).
 */
public class DbConnectionTestMySQL {
	private final String driverName = "com.mysql.jdbc.Driver";
	private final String connectionUrl = "jdbc:mysql://localhost:3306/martinfowler_uiArchs?useUnicode=true&characterEncoding=UTF8";
	private Connection connection = null;

	/**
	 * @throws java.sql.SQLException
	 */
	@After
	public void tearDown() throws SQLException {
		this.connection.close();
	}

	/**
	 * Test connecting to DB instance, run sample read-only query and output result.
	 */
	@Test
	public final void test() {
		System.out.println ("Loading JDBC driver "+this.driverName);
		try {
			Class.forName(this.driverName);
		} catch (ClassNotFoundException e){
			fail("Could not load the driver "+this.driverName+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}

		System.out.println ("Connecting to database: "+connectionUrl);
		try {
			this.connection = DriverManager.getConnection(connectionUrl, "XXX", "XXX");
			final Statement stmt = this.connection.createStatement();
			final String queryText = "select station_name from monitoring_station";
			
			final ResultSet result = stmt.executeQuery(queryText);
			while (result.next()) {
				System.out.println(result.getString(1));
			}
		} catch (SQLException e) {
			fail("Error while accessing database: "+connectionUrl+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}

	}

}
