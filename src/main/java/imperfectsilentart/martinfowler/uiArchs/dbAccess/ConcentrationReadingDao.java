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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;

import com.zaxxer.hikari.HikariDataSource;

/**
 * DAO for accessing concentration_reading table.
 *
 * NOTE: Using no OR-mapper on purpose.
 * TODO pessimistic db-locking, thread synchronization
 */
public class ConcentrationReadingDao {
	/**
	 * @return DateTimeFormatter    Formatter for generating a date format compatible to MySQL timestamp data type.
	 * TODO untested whether the time zone offset works
	 */
	public final static DateTimeFormatter getReadingTimestampFormat() {
		final DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
		final DateTimeFormatter result = b.appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD).appendPattern("-MM-dd' 'HH:mm:ss[.SSS][Z]").toFormatter();
		return result;
	}
	
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
		
		try(
			final HikariDataSource connPool = DbConnector.getConnectionPool();
			final Connection connection = connPool.getConnection();
			final PreparedStatement stmt = connection.prepareStatement(query);
		){
			stmt.setLong(1, newConcentrationValue);
			stmt.setLong(2, readingId);
			connection.setAutoCommit(false);
			
			stmt.executeUpdate();
			connection.commit();
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
		final String query = "SELECT cr.id, cr.fk_station_id, cr.reading_timestamp, cr.actual_concentration\n"
				+ "FROM concentration_reading cr JOIN monitoring_station ms on (cr.fk_station_id = ms.id)\n"
				+ "WHERE ms.id = ?\n"
				+ "ORDER BY cr.reading_timestamp DESC\n"
				+ "LIMIT 1";
		
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
			connection.setAutoCommit(false);
			
			try(
				final ResultSet resultSet = stmt.executeQuery();
			){
				if(! resultSet.next() ) {
					return null;
				}
				id = resultSet.getLong(1);
				stationForeignKey = resultSet.getLong(2);
				readingTimestamp = parseReadingTimestamp( resultSet.getString(3) );
				actualConcentration = resultSet.getInt(4);
				
				if( resultSet.next() ) {
					throw new DbAccessException("Query result contains more tuples than expected. Expected one single tuple. Query:\n"+query);
				}
			}
		} catch (SQLException | DbAccessException e) {
			throw new DbAccessException("Error while opening database connection or executing query or processing query result. Query:\n"+query, e);
		}
		
		final ConcentrationReading result = new ConcentrationReading(id, stationForeignKey, readingTimestamp, actualConcentration);
		return result;
	}
	
	/**
	 * @param readingTimestamp
	 * @return
	 * @throws DbAccessException
	 */
	private LocalDateTime parseReadingTimestamp(final String readingTimestamp) throws DbAccessException {
		LocalDateTime result = null;
		
		try {
			result = LocalDateTime.parse(readingTimestamp, ConcentrationReadingDao.getReadingTimestampFormat());
		}catch(DateTimeParseException e) {
			throw new DbAccessException("Given timestamp \""+readingTimestamp+"\" doesn't have the required format \""+ConcentrationReadingDao.getReadingTimestampFormat()+"\"", e);
		}
		return result;
	}
}
