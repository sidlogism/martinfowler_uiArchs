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
package imperfectsilentart.martinfowler.uiArchs.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */
public class FileTools {
	private static final Logger logger = Logger.getLogger(FileTools.class.getName());
	
	/**
	 * Reads the content of a file as text interpreted in UTF-8 encoding.
	 * 
	 * @param relativePath    absolute path to the UTF-8 file to be read  (may also reference resources from a nested file system e. g. a JAR-file)
	 *
	 * @return content of a file as text interpreted in UTF-8 encoding
	 * @throws IOException
	 * @throws FileSystemAccessException 
	 * @throws URISyntaxException 
	 */
	public static String getFileContent(final String relativePath) throws IOException, URISyntaxException, FileSystemAccessException {
		return getFileContent(relativePath, StandardCharsets.UTF_8);
	}
	
	/**
	 * Reads the content of a file as text interpreted in the given encoding.
	 * 
	 * @param relativePath    relative path to the file to be read (may also reference resources from a nested file system e. g. a JAR-file)
	 * @param encoding     encoding for interpreting the content of a file as text    
	 * 
	 * @return content of a file as text interpreted in the given encoding. null on error.
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws FileSystemAccessException 
	 */
	private static String getFileContent(final String relativePath, final Charset encoding) throws IOException, URISyntaxException, FileSystemAccessException {
		logger.log(Level.FINE, "Reading content of given relative path:\""+relativePath+"\".");
		String result = null;
		
		/*
		 * Resolve given relative path to runtime resource.
		 */
		final URI pathUri = FileTools.class.getResource(relativePath).toURI();
		logger.log(Level.FINE, "Resolved URI: \""+pathUri+"\".");
		final String uriText = pathUri.toString();		
		if( uriText.contains("!") ) {
			logger.log(Level.FINE, "The resolved URI from the given relative path contains reference to a nested file system.");
			/*
			 * case: path contains reference to files in a nested file system (e. g. "/path/to/some.jar!/pathTofile.txt")
			 * IMPORTANT: Use the overall path URI with all scheme-prefixes. The path pointing to the base of the nested file system must contain these scheme-prefixes (e. g. "jar:").
			 */
			final String[] nestedPaths = uriText.split("!");
			if( nestedPaths.length > 2 ) {
				throw new FileSystemAccessException("The resolved URI from the given relative path contains MORE THAN ONE reference to a nested file system. Found "+(nestedPaths.length-1)+" references to nested file systems.");
			}
			final String nestedFsBase = nestedPaths[0];
			final String subpath = nestedPaths[1];
			logger.log(Level.FINE, "Subpath \""+subpath+ "\" is relative to nested file system \""+nestedFsBase+"\".");
			/*
			 * IMPORTANT: Reading the content of the resolved path must be done while the object of the nested file system is still open!
			 */
			final Map<String, String> env = new HashMap<String, String>();
			try(
				final FileSystem fs = FileSystems.newFileSystem( URI.create(nestedFsBase), env );
			){
				final Path absolutePath = fs.getPath(subpath);
				result = Files.readString(absolutePath, encoding);
			}catch(final IOException e){
				throw new FileSystemAccessException("Failed to resolve subpath \""+subpath+ "\" in nested file system \""+nestedFsBase+"\".", e);
			}
		}else{
			/*
			 * case: path contains NO reference to any nested file system
			 * Resolve the given relative path to an absolute path.
			 */
			logger.log(Level.FINE, "Given relative path:\""+relativePath+"\" contains NO reference to any nested file system.");
			final Path absolutePath = Paths.get( pathUri );
			logger.log(Level.FINE, "Given relative path:\""+relativePath+"\". Resolved absolute path:\""+absolutePath+"\".");
			result = Files.readString(absolutePath, encoding);
		}
		return result;
	}
	
	/**
	 * Reads the content of a file as text interpreted in UTF-8 encoding.
	 * 
	 * @param absolutePath    absolute path to the UTF-8 file to be read
	 * 
	 * @return content of a file as text interpreted in UTF-8 encoding
	 * @throws IOException
	 */
	private static String getFileContent(final Path absolutePath) throws IOException {
		return getFileContent(absolutePath, StandardCharsets.UTF_8);
	}

	/**
	 * Reads the content of a file as text interpreted in the given encoding.
	 * 
	 * @param absolutePath    absolute path to the file to be read
	 * @param encoding     encoding for interpreting the content of a file as text    
	 * 
	 * @return content of a file as text interpreted in the given encoding
	 * @throws IOException
	 */
	private static String getFileContent(final Path absolutePath, final Charset encoding) throws IOException {
		logger.log(Level.FINE, "Reading content of file:\""+absolutePath+"\".");
		final String configContent = Files.readString(absolutePath, encoding);
		return configContent;
	}
}
