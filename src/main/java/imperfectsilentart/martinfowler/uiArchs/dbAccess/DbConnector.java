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

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import imperfectsilentart.martinfowler.uiArchs.util.ConfigParser;
import imperfectsilentart.martinfowler.uiArchs.util.FileSystemAccessException;
/**
 * Simple utility wrapper for accessing database connection pool.
 */
public class DbConnector {
	/*
	 * No ctor needed. Class has only a static utility interface.
	 */
	private DbConnector(){}
	
	/**
	 * HikariDataSource    global connection pool initialized once using {@link #getConnectionPool()}
	 * 
	 * @note    pool is not closed at application shutdown or until a calling method closes it (TODO strategy for closing statically. finalize() is deprecated)
	 */
    private static HikariDataSource connectionPool;
    
    /**
     * Provides connection pool initialized with default parameters
     * 
     * @return HikariDataSource    connection pool initialized with default parameters.
     * @note    HikariDataSource instead of javax.sql.DataSource to allow closing the pool externally. 
     * 
     * @throws DbAccessException
     * @throws FileSystemAccessException 
     */
    public static synchronized HikariDataSource getConnectionPool() throws DbAccessException{
    	JSONObject dbParameters = null;
		try {
			ConfigParser.getInstance().parseConfig();
			final String activeDbs = ConfigParser.getInstance().getRootNode().getString("activeDbs");
			dbParameters = ConfigParser.getInstance().getRootNode().getJSONObject("dbParameters").getJSONObject(activeDbs);
		}catch(IOException | JSONException | URISyntaxException | FileSystemAccessException e) {
			throw new DbAccessException("Failed reading configuration: Could not get connection parameters.", e);
		}
        if(null == connectionPool || connectionPool.isClosed()){
            HikariConfig config = new HikariConfig();
                 
            config.setJdbcUrl(dbParameters.getString("connectionUrl"));
            config.setUsername(dbParameters.getString("user"));
            config.setPassword(dbParameters.getString("password"));

            config.setMaximumPoolSize(10);
            config.setAutoCommit(false);
            // REPEATABLE READ is default transaction isolation level in MySQL. Just assuring in case of using other DBS.
            config.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
         
            connectionPool = new HikariDataSource(config);
        }
        return connectionPool;
    }
}
