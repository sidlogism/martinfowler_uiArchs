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
package sidlogism.martinfowler.uiArchs.mvc_standalone.model.persistence;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.json.JSONException;
import org.json.JSONObject;

import sidlogism.martinfowler.uiArchs.util.ConfigParser;
import sidlogism.martinfowler.uiArchs.util.FileSystemAccessException;
/**
 * Simple utility wrapper for accessing persistence layer.
 * 
 * @see sidlogism.martinfowler.uiArchs.formsandcontrols.persistence.DbConnector
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
	 * @throws ModelPersistenceException
	 * @throws FileSystemAccessException 
	 */
	public static synchronized EntityManager getEntityManager() throws ModelPersistenceException{
		JSONObject dbParameters = null;
		String activeDbs = null;
		String persistenceUnitName = null;
		try {
			ConfigParser.getInstance().parseConfig();
			activeDbs = ConfigParser.getInstance().getRootNode().getString("activeDbs");
			persistenceUnitName = ConfigParser.getInstance().getRootNode().getString("defaultPersistenceUnit");
			dbParameters = ConfigParser.getInstance().getRootNode().getJSONObject("dbParameters").getJSONObject(activeDbs);
		}catch(IOException | JSONException | URISyntaxException | FileSystemAccessException e) {
			throw new ModelPersistenceException("Failed reading configuration: Could not get connection parameters.", e);
		}
		if(null == ENTITY_MANAGER || !ENTITY_MANAGER.isOpen() ){
			final Map<String, Object> configOverrides = new HashMap<String, Object>();
			configOverrides.put("jakarta.persistence.jdbc.driver", dbParameters.getString("driverName"));
			configOverrides.put("jakarta.persistence.jdbc.url", dbParameters.getString("connectionUrl") );
			configOverrides.put("jakarta.persistence.jdbc.user", dbParameters.getString("user") );
			configOverrides.put("jakarta.persistence.jdbc.password", dbParameters.getString("password") );
			configOverrides.put("hibernate.format_sql", "true" );
			configOverrides.put("hibernate.use_sql_comments", "true" );
			/*
			 * set global transaction isolation level and other properties depending on used DBS
			 */
			switch(activeDbs) {
			case "oracleXE":
				configOverrides.put("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect" );
				configOverrides.put("jakarta.persistence.jdbc.transactionIsolation", "TRANSACTION_SERIALIZABLE" );
				//TODO test setting a schema: configOverrides.put("jakarta.persistence.jdbc.schema", dbParameters.getString("schema") );
				break;
			case "mysql":
				configOverrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect" );
				// REPEATABLE READ is default transaction isolation level in MySQL. Just assuring in case of using other DBS.
				configOverrides.put("jakarta.persistence.jdbc.transactionIsolation", "TRANSACTION_REPEATABLE_READ" );
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
