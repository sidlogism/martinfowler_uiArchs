/*
 * Copyright 2021 Imperfect Silent Art
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *	 http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package imperfectsilentart.martinfowler.uiArchs.formsandcontrols.persistence;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;

import com.zaxxer.hikari.HikariDataSource;

import imperfectsilentart.martinfowler.uiArchs.util.ConfigParser;
import imperfectsilentart.martinfowler.uiArchs.util.FileSystemAccessException;
import imperfectsilentart.martinfowler.uiArchs.util.TimeProcessingException;
import imperfectsilentart.martinfowler.uiArchs.util.TimeTools;

/**
 * DAO for accessing concentration_reading table.
 *
 * NOTE: Using no OR-mapper on purpose.
 * @see imperfectsilentart.martinfowler.uiArchs.model2_passive_view.model.ConcentrationReadingModel
 */
public class ConcentrationReadingDao {
	private static final Logger logger = Logger.getLogger(ConcentrationReadingDao.class.getName());
	
	/**
	 * Updates actual concentration value of current reading record.
	 * 
	 * @throws DbAccessException 
	 */
	public synchronized void updateActualConcentration(final int newConcentrationValue, final long readingId) throws DbAccessException {
		if(readingId < 0) return;
		final String query = "UPDATE concentration_reading\n"
				+ "SET actual_concentration  = ?\n"
				+ "WHERE id = ?\n";
		/*
		 * Get name of currently used DBS. Need to handle pessimistic locking different for each DBS.
		 */
		String activeDbs = null;
		try {
			ConfigParser.getInstance().parseConfig();
			activeDbs = ConfigParser.getInstance().getRootNode().getString("activeDbs");
		}catch(IOException | JSONException | URISyntaxException | FileSystemAccessException e) {
			throw new DbAccessException("Failed reading configuration: Could not get name of currently used DBS.", e);
		}
		
		/*
		 * Pessimistic locking for update statement.
		 */
		try(
			final HikariDataSource connPool = DbConnector.getConnectionPool();
			final Connection connection = connPool.getConnection();
			final PreparedStatement stmt = connection.prepareStatement(query);
			final PreparedStatement lockStmtMysql = connection.prepareStatement("LOCK TABLES concentration_reading WRITE");
			final PreparedStatement lockStmtOracle = connection.prepareStatement("LOCK TABLE concentration_reading IN EXCLUSIVE MODE NOWAIT");
			final PreparedStatement unlockStmt = connection.prepareStatement("UNLOCK TABLES");
		){
			stmt.setLong(1, newConcentrationValue);
			stmt.setLong(2, readingId);
			connection.setAutoCommit(false);
			switch(activeDbs) {
				case "oracleXE":
					lockStmtOracle.execute();
					break;
				case "mysql":
				default:
					lockStmtMysql.execute();
			}
			
			stmt.executeUpdate();
			connection.commit();
			if( "mysql".equals(activeDbs)) unlockStmt.execute();
		} catch (SQLException | DbAccessException e) {
			throw new DbAccessException("Error while opening database connection or executing update query. Query:\n"+query, e);
		}
	}
	
	/**
	 * Loads the youngest concentration reading record belonging to the monitoring station with the given ID from the database.
	 * 
	 * @param internalStationId    ID of relevant monitoring station
	 * @return domain object of relevant reading record. null if the query result is empty.
	 * @throws DbAccessException
	 */
	public synchronized ConcentrationReading getLatestConcentrationReading(final long internalStationId) throws DbAccessException {
		/*
		 * TOP query compatible to standard SQL syntax.
		 * IMPORTANT: Don't filter by timestamp-value only because it is not always a unique value.
		 * 
		 * Other dialects allow elegant single query constructs in combination with ORDER BY:
		 *     MySQL: "LIMIT 1"
		 *     Oracle SQL: "FETCH FIRST 1 ROW ONLY"
		 */
		final String query = 
				"SELECT id, fk_station_id, reading_timestamp, actual_concentration\n"
				+ "FROM concentration_reading\n"
				+ "WHERE fk_station_id = ? AND reading_timestamp =\n"
				+ "(\n"
				+ "    SELECT MAX(reading_timestamp)\n"
				+ "    FROM concentration_reading\n"
				+ "    WHERE fk_station_id = ?\n"
				+ ")";
		
		long id = -1;
		long stationForeignKey = -1;
		LocalDateTime readingTimestamp = null;
		int actualConcentration = -1;
		try(
			final HikariDataSource connPool = DbConnector.getConnectionPool();
			final Connection connection = connPool.getConnection();
			final PreparedStatement stmt = connection.prepareStatement(query);
		){
			stmt.setLong(1, internalStationId);
			stmt.setLong(2, internalStationId);
			connection.setAutoCommit(false);
			
			try(
				final ResultSet resultSet = stmt.executeQuery();
			){
				if(! resultSet.next() ) {
					logger.log(Level.WARNING, "Query result is empty. Expected one single tuple as result. Query:\n"+query);
					return null;
				}
				id = resultSet.getLong(1);
				stationForeignKey = resultSet.getLong(2);
				readingTimestamp = TimeTools.parseReadingTimestamp( resultSet.getString(3) );
				actualConcentration = resultSet.getInt(4);
				
				if( resultSet.next() ) {
					throw new DbAccessException("Query result contains more tuples than expected. Expected one single tuple. Query:\n"+query);
				}
			}
		} catch (SQLException | DbAccessException | TimeProcessingException e) {
			throw new DbAccessException("Error while opening database connection or executing query or processing query result. Query:\n"+query, e);
		}
		
		final ConcentrationReading result = new ConcentrationReading(id, stationForeignKey, readingTimestamp, actualConcentration);
		return result;
	}

}
