/*
 * Copyright 2025 Sidlogism
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
package sidlogism.martinfowler.uiArchs.formsandcontrols.persistence;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import sidlogism.martinfowler.uiArchs.util.ConfigParser;
import sidlogism.martinfowler.uiArchs.util.FileSystemAccessException;
/**
 * Simple utility wrapper for accessing database connection pool.
 * 
 * @see sidlogism.martinfowler.uiArchs.model2_passive_view.model.persistence.PersistenceTools
 */
public class DbConnector {
	/*
	 * No ctor needed. Class has only a static utility interface.
	 */
	private DbConnector(){}
	
	/**
	 * HikariDataSource	global connection pool initialized once using {@link #getConnectionPool()}
	 * 
	 * @note	pool is not closed at application shutdown or until a calling method closes it (TODO strategy for closing statically. finalize() is deprecated)
	 */
	private static HikariDataSource CONNECTION_POOL;
	
	/**
	 * Provides connection pool initialized with default parameters
	 * 
	 * @return HikariDataSource	connection pool initialized with default parameters.
	 * @note	returns HikariDataSource instead of javax.sql.DataSource to allow closing the pool externally. 
	 * 
	 * @throws DbAccessException
	 * @throws FileSystemAccessException 
	 */
	public static synchronized HikariDataSource getConnectionPool() throws DbAccessException{
		JSONObject dbParameters = null;
		String activeDbs = null;
		try {
			ConfigParser.getInstance().parseConfig();
			activeDbs = ConfigParser.getInstance().getRootNode().getString("activeDbs");
			dbParameters = ConfigParser.getInstance().getRootNode().getJSONObject("dbParameters").getJSONObject(activeDbs);
		}catch(IOException | JSONException | URISyntaxException | FileSystemAccessException e) {
			throw new DbAccessException("Failed reading configuration: Could not get connection parameters.", e);
		}
		if(null == CONNECTION_POOL || CONNECTION_POOL.isClosed()){
			final HikariConfig config = new HikariConfig();
				 
			config.setJdbcUrl(dbParameters.getString("connectionUrl"));
			config.setUsername(dbParameters.getString("user"));
			config.setPassword(dbParameters.getString("password"));

			config.setMaximumPoolSize(10);
			config.setAutoCommit(false);
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			/*
			 * set global transaction isolation level and query logging properties depending on used DBS
			 */
			switch(activeDbs) {
			case "oracleXE":
				config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");
				break;
			case "mysql":
				// REPEATABLE READ is default transaction isolation level in MySQL. Just assuring in case of using other DBS.
				config.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");
				config.addDataSourceProperty("logger", "com.mysql.cj.log.StandardLogger");
				config.addDataSourceProperty("logSlowQueries", "true");
				config.addDataSourceProperty("dumpQueriesOnException", "true");
				if( dbParameters.getBoolean("logAllDbOperations") ) config.addDataSourceProperty("autoGenerateTestcaseScript", "true");
				if( dbParameters.getBoolean("logDbOperationTimings") ) config.addDataSourceProperty("profileSQL", "true");
				if( dbParameters.getBoolean("logJdbcActionTrace") ) config.addDataSourceProperty("traceProtocol", "true");
				break;
			default:
			}
		 
			CONNECTION_POOL = new HikariDataSource(config);
		}
		return CONNECTION_POOL;
	}
}
