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
package imperfectsilentart.martinfowler.uiArchs.dbAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zaxxer.hikari.HikariDataSource;
/**
 * DAO for accessing monitoring_station table.
 *
 * Using no OR-mapper on purpose.
 * TODO pessimistic db-locking, thread synchronization
 */
public class MonitoringStationDao {
	
	/**
	 * Loads the monitoring station with the given external ID from the database.
	 * 
	 * @param stationExternalId    external ID of relevant monitoring station
	 * @return domain object of relevant monitoring station. null if the query result is empty.
	 * @throws DbAccessException
	 */
	public MonitoringStation getStation(final String stationExternalId) throws DbAccessException {
		final String query = "SELECT id, station_external_id, station_name, target_concentration FROM monitoring_station WHERE station_external_id = ?";

		long id = -1;
		String stationName = null;
		int targetConcentration = -1;
		try(
			final HikariDataSource connPool = DbConnector.getConnectionPool();
			final Connection connection = connPool.getConnection();
			final PreparedStatement stmt = connection.prepareStatement(query);
		){
			stmt.setString(1, stationExternalId);
			connection.setAutoCommit(false);
			try(
				final ResultSet resultSet = stmt.executeQuery();
			){
				if(! resultSet.next() ) {
					return null;
				}
				id = resultSet.getLong(1);
				stationName = resultSet.getString(3);
				targetConcentration = resultSet.getInt(4);
				
				if( resultSet.next() ) {
					throw new DbAccessException("Query result contains more tuples than expected. Expected one single tuple. Query:\n"+query);
				}
			}
		} catch (SQLException | DbAccessException e) {
			throw new DbAccessException("Error while opening database connection or executing query or processing query result. Query\n"+query, e);
		}
		
		final MonitoringStation result = new MonitoringStation(id, stationExternalId, stationName, targetConcentration);
		return result;
	}
	
	/**
	 * @return Container holding the String representation of every monitoring station record.
	 * @throws DbAccessException
	 */
	public ArrayList<String> findAll() throws DbAccessException {
		final String query = "SELECT id, station_external_id, station_name, target_concentration FROM monitoring_station ORDER BY id ASC";
		
		ArrayList<String> result = new ArrayList<String>();
		try(
			final HikariDataSource connPool = DbConnector.getConnectionPool();
			final Connection connection = connPool.getConnection();
			final PreparedStatement stmt = connection.prepareStatement(query);
			final ResultSet resultSet = stmt.executeQuery();
		){
			connection.setAutoCommit(false);
			/*
			 * Only use column station_external_id. See MonitoringStationList.
			 */
			while( resultSet.next() ) {
				result.add( resultSet.getString(2) );
			}
		} catch (SQLException e) {
			throw new DbAccessException("Error while opening database connection or executing query or processing query result. Query: "+query, e);
		}
		return result;
	}
}
