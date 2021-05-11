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
package imperfectsilentart.martinfowler.uiArchs.mvc_standalone.model.persistence;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.json.JSONException;
import org.json.JSONObject;

import imperfectsilentart.martinfowler.uiArchs.util.ConfigParser;
import imperfectsilentart.martinfowler.uiArchs.util.FileSystemAccessException;
/**
 * Simple utility wrapper for accessing persistence layer.
 */
public class PersistenceTools {
	/*
	 * No ctor needed. Class has only a static utility interface.
	 */
	private PersistenceTools(){}
	
	/**
	 * EntityManager	global entity manager initialized once using {@link #getEntityManager()}
	 * 
	 * @note	pool is not closed at application shutdown or until a calling method closes it (TODO strategy for closing statically. finalize() is deprecated)
	 */
	private static EntityManager ENTITY_MANAGER;
	
	/**
	 * Provides entity manager initialized with default parameters for data source
	 * 
	 * @return EntityManager	entity manager initialized with default parameters for data source.
	 * @note	Caller is responsible for closing the returned entity manager.
	 * 
	 * @throws PeristenceException
	 * @throws FileSystemAccessException 
	 */
	public static synchronized EntityManager getEntityManager() throws PeristenceException{
		JSONObject dbParameters = null;
		String activeDbs = null;
		String persistenceUnitName = null;
		try {
			ConfigParser.getInstance().parseConfig();
			activeDbs = ConfigParser.getInstance().getRootNode().getString("activeDbs");
			persistenceUnitName = ConfigParser.getInstance().getRootNode().getString("defaultPersistenceUnit");
			dbParameters = ConfigParser.getInstance().getRootNode().getJSONObject("dbParameters").getJSONObject(activeDbs);
		}catch(IOException | JSONException | URISyntaxException | FileSystemAccessException e) {
			throw new PeristenceException("Failed reading configuration: Could not get connection parameters.", e);
		}
		if(null == ENTITY_MANAGER || !ENTITY_MANAGER.isOpen() ){
			final Map<String, Object> configOverrides = new HashMap<String, Object>();
			configOverrides.put("javax.persistence.jdbc.driver", dbParameters.getString("driverName"));
			configOverrides.put("javax.persistence.jdbc.url", dbParameters.getString("connectionUrl") );
			configOverrides.put("javax.persistence.jdbc.user", dbParameters.getString("user") );
			configOverrides.put("javax.persistence.jdbc.password", dbParameters.getString("password") );
			/*
			 * set global transaction isolation level and query logging properties depending on used DBS
			 */
			switch(activeDbs) {
			case "oracleXE":
				configOverrides.put("javax.persistence.jdbc.transactionIsolation", "TRANSACTION_SERIALIZABLE" );
				configOverrides.put("hibernate.dialect", "org.hibernate.dialect.Oracle18cDialect" );
				break;
			case "mysql":
				configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect" );
				// REPEATABLE READ is default transaction isolation level in MySQL. Just assuring in case of using other DBS.
				configOverrides.put("javax.persistence.jdbc.transactionIsolation", "TRANSACTION_REPEATABLE_READ" );
				/*
				 * java.util.Properties
				final Properties mysqlProperties = new Properties();
				mysqlProperties.setProperty("logger", "com.mysql.cj.log.StandardLogger");
				mysqlProperties.setProperty("logSlowQueries", "true");
				mysqlProperties.setProperty("dumpQueriesOnException", "true");
				configOverrides.put("hibernate.hikari.dataSourceProperties", mysqlProperties.toString() );
				*/
				configOverrides.put("hibernate.format_sql", "true" );
				configOverrides.put("hibernate.use_sql_comments", "true" );
				if( dbParameters.getBoolean("logAllDbOperations") ) configOverrides.put("hibernate.show_sql", "true" );
				if( dbParameters.getBoolean("logDbOperationTimings") ) configOverrides.put("hibernate.generate_statistics", "true" );
				break;
			default:
			}
			
			final EntityManagerFactory emf = Persistence.createEntityManagerFactory( persistenceUnitName , configOverrides);
			ENTITY_MANAGER = emf.createEntityManager();
		}
		return ENTITY_MANAGER;
	}
}
