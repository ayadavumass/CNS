/*
 *
 *  Copyright (c) 2015 University of Massachusetts
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you
 *  may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Initial developer(s): ayadav
 *
 */
package edu.umass.cs.contextservice.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import edu.umass.cs.contextservice.logging.ContextServiceLogger;

/**
 * Parses a properties file to get all info needed to run CS on hosts.
 * @author ayadav
 */
public class CSConfigFileLoader 
{
  /**
   * Creates an instance of InstallConfig.
   * @param filename
   */
  public CSConfigFileLoader(String filename) {
    try {
      loadPropertiesFile(filename);
    } catch (IOException e) {
      ContextServiceLogger.getLogger().severe("Problem loading installer config file: " + e);
    }
  }

  private void loadPropertiesFile(String filename) throws IOException 
  {
	  Properties properties = new Properties();

	  File f = new File(filename);
	  if (f.exists() == false) 
	  {
		  throw new FileNotFoundException("CS config file not found:" + filename);
	  }
	  
	  InputStream input = new FileInputStream(filename);
	  properties.load(input);
    
	  ContextServiceConfig.triggerEnabled = Boolean.parseBoolean(
    		properties.getProperty(ContextServiceConfig.TRIGGER_ENABLE_STRING, 
    				ContextServiceConfig.triggerEnabled+"") );
	  
	  ContextServiceConfig.numAttrsPerSubspace = Double.parseDouble(
			  properties.getProperty(ContextServiceConfig.NUM_ATTRS_PER_SUBSPACE_STRING, 
					  ContextServiceConfig.numAttrsPerSubspace+"") );
	  
	  
	  ContextServiceConfig.privacyEnabled = Boolean.parseBoolean(
	    		properties.getProperty(ContextServiceConfig.PRIVACY_ENABLED_STRING, 
	    				ContextServiceConfig.privacyEnabled+"") );
	  
	  ContextServiceConfig.queryAllEnabled = Boolean.parseBoolean(
	    		properties.getProperty(ContextServiceConfig.QUERY_ALL_ENABLED_STRING, 
	    				ContextServiceConfig.queryAllEnabled+"") );
	  
	  ContextServiceConfig.mysqlMaxConnections = Integer.parseInt(
	    		properties.getProperty(ContextServiceConfig.SQL_POOL_SIZE_STRING, 
	    				10+"") )  ;
	  
	  ContextServiceConfig.threadPoolSize = Integer.parseInt(
	    		properties.getProperty(ContextServiceConfig.THREAD_POOL_SIZE_STRING, 
	    				10+"") )  ;
	  
	  ContextServiceConfig.regionMappingPolicy = properties.getProperty
			  	(ContextServiceConfig.REGION_MAPPING_POLICY_STRING, 
			  			ContextServiceConfig.regionMappingPolicy);
	  
	  ContextServiceConfig.enableBulkLoading = Boolean.parseBoolean(
	    		properties.getProperty(ContextServiceConfig.BULK_LOADING_ENABLE_STRING, 
	    				ContextServiceConfig.enableBulkLoading+"") );
	  
	  ContextServiceConfig.bulkLoadingFilePath = properties.getProperty
			  	(ContextServiceConfig.BULK_LOADING_FILE_STRING, 
			  			ContextServiceConfig.bulkLoadingFilePath);
	  
	  
	  System.out.println(" ContextServiceConfig.TRIGGER_ENABLED "+ContextServiceConfig.triggerEnabled
    		+" ContextServiceConfig.numAttrsPerSubspace "+ContextServiceConfig.numAttrsPerSubspace 
    		+" ContextServiceConfig.PRIVACY_ENABLED "+ContextServiceConfig.privacyEnabled
    		+" ContextServiceConfig.MYSQL_MAX_CONNECTIONS "+ContextServiceConfig.mysqlMaxConnections 
    		+" ContextServiceConfig.THREAD_POOL_SIZE "+ContextServiceConfig.threadPoolSize
    		+" ContextServiceConfig.regionMappingPolicy "+ContextServiceConfig.regionMappingPolicy
    		+" ContextServiceConfig.enableBulkLoading "+ContextServiceConfig.enableBulkLoading
    		+" ContextServiceConfig.bulkLoadingFilePath "+ContextServiceConfig.bulkLoadingFilePath);
  }
  
  /**
   * The main routine. For testing only.
   * 
   * @param args
   */
  public static void main(String[] args) 
  {
  }
}