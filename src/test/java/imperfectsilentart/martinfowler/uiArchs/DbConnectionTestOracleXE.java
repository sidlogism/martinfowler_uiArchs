/*
 * TODO copyright
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
 * Simple test for connecting to Oracle XE DB instance, run sample read-only query and output result.
 * 
 * TODO merge all DbConnectionTest* classes and initialize with different parameters for each test run (avoid redundant code).
 */
public class DbConnectionTestOracleXE {
	private final String driverName = "oracle.jdbc.OracleDriver";
	private final String connUrl = "jdbc:oracle:thin:@localhost:1521:XE";
	private Connection conn = null;

	/**
	 * @throws java.sql.SQLException
	 */
	@After
	public void tearDown() throws SQLException {
		this.conn.close();
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

		System.out.println ("Connecting to database: "+connUrl);
		try {
			this.conn = DriverManager.getConnection(connUrl, "XXX", "XXX");
			final Statement stmt = this.conn.createStatement();
			final String query = "SELECT username FROM dba_users";
			
			final ResultSet rset = stmt.executeQuery(query);
			while (rset.next()) {
				System.out.println(rset.getString(1));
			}
		} catch (SQLException e) {
			fail("Error while accessing database: "+connUrl+".\n"+e.getCause()+"\n"+e.getStackTrace());
		}

	}

}
