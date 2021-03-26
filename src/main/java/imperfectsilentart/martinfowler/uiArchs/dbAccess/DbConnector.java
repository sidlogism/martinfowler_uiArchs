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

import imperfectsilentart.martinfowler.uiArchs.ConfigParser;
/**
 * Beschreibung
 *
 * @author Imperfect Silent Art
 *
 */
public class DbConnector {
	private DbConnector(){}
	
    private static HikariDataSource connectionPool;
    /**
     * Provides connection pool initialized with default parameters
     * 
     * @return HikariDataSource    connection pool initialized with default parameters
     * @note    Returning HikariDataSource instead of javax.sql.DataSource because the latter cannot be closed and shutdown explicitly while a HikariDataSource can. 
     * @throws DbAccessException
     */
    public static HikariDataSource getConnectionPool() throws DbAccessException{
    	JSONObject dbParameters = null;
		try {
			ConfigParser.getInstance().parseConfig();
			final String activeDbs = ConfigParser.getInstance().getRootNode().getString("activeDbs");
			dbParameters = ConfigParser.getInstance().getRootNode().getJSONObject("dbParameters").getJSONObject(activeDbs);
		}catch(JSONException | IOException | URISyntaxException e) {
			throw new DbAccessException("Failed reading configuration: Could not get connection parameters.", e);
		}
        if(null == connectionPool){
            HikariConfig config = new HikariConfig();
                 
            config.setJdbcUrl(dbParameters.getString("connectionUrl"));
            config.setUsername(dbParameters.getString("user"));
            config.setPassword(dbParameters.getString("password"));

            config.setMaximumPoolSize(10);
            config.setAutoCommit(false);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
         
            connectionPool = new HikariDataSource(config);
        }
        return connectionPool;
    }
}
