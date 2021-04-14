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
package imperfectsilentart.martinfowler.uiArchs;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Parser of JSON config file for read-only access.
 */

public class ConfigParser {
	/**
	 * Relative path to default config file in classpath.
	 */
	final static String configRelativePath = "/config.json";
	/*
	 * static members for singleton pattern
	 */
	private static ConfigParser instance = new ConfigParser();
	public static ConfigParser getInstance() {
		return ConfigParser.instance;
	}
	/*
	 * dynamic members
	 */
	private JSONObject rootNode = null;
	private ConfigParser(){}
	
	public JSONObject getRootNode() {
		return this.rootNode;
	}
	/**
	 * Parses the default JSON config file.
	 * 
	 * @return    unmarshalled content of the default JSON config file
	 * @throws FileSystemAccessException 
	 */
	public JSONObject parseConfig() throws IOException, JSONException, URISyntaxException, FileSystemAccessException {
		final String configContent = FileTools.getFileContent(configRelativePath);
		
		this.rootNode = new JSONObject(configContent);
		return getRootNode();
	}
}

