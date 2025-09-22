/*
 * Copyright 2025 Sidlogism
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
package sidlogism.martinfowler.uiArchs.persistence;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariDataSource;

import sidlogism.martinfowler.uiArchs.formsandcontrols.persistence.DbAccessException;
import sidlogism.martinfowler.uiArchs.formsandcontrols.persistence.DbConnector;



/**
 * Simple tests for DB connectivity.
 */
public class HikariConnectionPoolTest {
	/**
	 * Test connecting to DB instance.
	 */
	@Test
	public void testCreatingConnectionPool() {
		try (final HikariDataSource pool = DbConnector.getConnectionPool();){
			// nothing to do
		} catch (DbAccessException e) {
			fail("Error while creating connection pool.\n"+e.getCause()+"\n"+e.getStackTrace());
		}
		
		// just for better separation of tests outputs
		System.out.println("\n\n\n\n");
	}
	
	/**
	 * Test connecting to DB instance.
	 */
	@Test
	public void testOpeningConnection() {
		try(
			final HikariDataSource pool = DbConnector.getConnectionPool();
			final Connection connection = pool.getConnection();
		){
			// nothing to do
		} catch (SQLException | DbAccessException e) {
			fail("Error while connecting to DB instance.\n"+e.getCause()+"\n"+e.getStackTrace());
		}
		
		// just for better separation of tests outputs
		System.out.println("\n\n\n\n");
	}
}
