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
package imperfectsilentart.martinfowler.uiArchs.dbAccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Beschreibung
 *
 * @author Imperfect Silent Art
 *
 */
public class MonitoringStationDao {
	private static final Logger logger = Logger.getLogger(MonitoringStationDao.class.getName());
	
	public ArrayList<String> finaAll() throws DbAccessException {
    	try(
			final Connection connection = DbConnector.getConnectionPool().getConnection();
    	){
	    	
    	} catch (SQLException e) {
			throw new DbAccessException("Error while opening new database connection or while executing query.", e);
		}
    	return new ArrayList<String>();
	}
}
