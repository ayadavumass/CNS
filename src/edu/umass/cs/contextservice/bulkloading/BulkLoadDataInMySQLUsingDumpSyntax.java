package edu.umass.cs.contextservice.bulkloading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import edu.umass.cs.contextservice.attributeInfo.AttributeMetaInfo;
import edu.umass.cs.contextservice.attributeInfo.AttributeTypes;
import edu.umass.cs.contextservice.config.ContextServiceConfig;
import edu.umass.cs.contextservice.database.RegionMappingDataStorageDB;
import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.contextservice.database.guidattributes.GUIDStorageInterface;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.contextservice.regionmapper.AbstractRegionMappingPolicy;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;
import edu.umass.cs.contextservice.utils.Utils;

/**
 * This class reads all guids from a file and loads only those guids into mysql 
 * that are mapped on this node by the region mapping mechanism.
 * There are no CNS messages sent for bulk loading. Each node in CNS has access 
 * to the file of guids and attribute value pairs and each node performs this operation 
 * locally. This procedure makes reduces the time to initializes CNS with millions of GUIDs.  
 * @author ayadav
 */
public class BulkLoadDataInMySQLUsingDumpSyntax
{
	//public static final String LATIN_ENCODING		= "ISO-8859-1";
	// maximum inserts batched into one in the mysql dump file format
	private static final int MAX_INSERT_BATCHING			= 1000;
	
	private final int myId;
	private final String allguidfilepath;
	private final AbstractDataSource dataSource;
	private final AbstractRegionMappingPolicy regionMappingPolicy;
	private final List<Integer> allNodeIDs;
	
	private final GUIDStorageInterface guidDBStorage;
	
	/**
	 * my id is the id of the cns node.
	 * allguidfilepath is the absolute path of the file that contains all 
	 * guids and their attr val pairs.
	 *  dataSource and regionMappingPolicy are db date source and region 
	 *  mapping scheme respectively,
	 * @param myId
	 * @param allguidfilepath
	 * @param dataSource
	 * @param regionMappingPolicy
	 */
	public BulkLoadDataInMySQLUsingDumpSyntax(int myId, String allguidfilepath, 
				AbstractDataSource dataSource, AbstractRegionMappingPolicy regionMappingPolicy,
				List<Integer> allNodeIDs, GUIDStorageInterface guidDBStorage)
	{
		this.myId = myId;
		this.allguidfilepath = allguidfilepath;
		this.dataSource = dataSource;
		this.regionMappingPolicy = regionMappingPolicy;
		this.allNodeIDs = allNodeIDs;
		this.guidDBStorage = guidDBStorage;
	}
	
	/**
	 * Bulk loads the data
	 */
	public void bulkLoadData()
	{
		long start = System.currentTimeMillis();
		//writeMySQLDumpFile();
		writeMySQLDumpFile();
		System.out.println("MySQL dump file writing ended in "
								+(System.currentTimeMillis()-start));
		
		//loadDataInMySQL();
		loadDataUsingMySQLScript();
		
		//List<String> attributeOrderList = writeNodeSpecificBulkLoadingFiles();
		//loadDataFromFilesInMysql(attributeOrderList);
	}
	
	
	/**
	 * writeMySQLDumpFile() function reads the all guids file and filters GUIDs that 
	 * needs to be stored at this node and writes a MySQL dump format file for that.
	 * This function also returns an attribute list in-order.
	 * @return
	 */
	private void writeMySQLDumpFile()
	{
		BufferedReader br 			 = null;
		BufferedWriter bw  		 	 = null;
		
		List<String> attributeOrderList = new LinkedList<String>();
		try
		{
			long attrIndexTime = System.currentTimeMillis();
			br = new BufferedReader(new FileReader(allguidfilepath));
			//bw = new BufferedWriter(new FileWriter(ContextServiceConfig.BULK_LOAD_FILE+myId) );
			
		    bw = new BufferedWriter(
		    		new OutputStreamWriter
		    			(new FileOutputStream(ContextServiceConfig.BULK_LOAD_FILE+myId+".sql")));
		    
		    String str = "/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;";
		    bw.write(str+"\n");
		    
			str = "/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;";
			bw.write(str+"\n");
			
			str = "/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;";
			bw.write(str+"\n");
			
			str = "/*!40101 SET NAMES utf8 */;";
			bw.write(str+"\n");
			
			str = "/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;";
			bw.write(str+"\n");
			
			str = "/*!40103 SET TIME_ZONE='+00:00' */;";
			bw.write(str+"\n");
			
			str = "/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;";
			bw.write(str+"\n");
			
			str = "/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;";
			bw.write(str+"\n");
			
			str = "/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;";
			bw.write(str+"\n");
			
			str = "/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;";
			bw.write(str+"\n");
		    
			str = "use contextDB;";
			bw.write(str+"\n");
			
			
			str = "DROP TABLE IF EXISTS `"+RegionMappingDataStorageDB.ATTR_INDEX_TABLE_NAME+"`;";
			bw.write(str+"\n");
			
			str = "/*!40101 SET @saved_cs_client     = @@character_set_client */;";
			bw.write(str+"\n");
			
			str = "/*!40101 SET character_set_client = utf8 */;";
			bw.write(str+"\n");
			
			// creates data storage tables.
			//guidDBStorage.createDataStorageTables();
			String attrIndexTableCmd = guidDBStorage.getAttrIndexTableCreationCmd();
			bw.write(attrIndexTableCmd+";"+"\n");
			
			
			str = "/*!40101 SET character_set_client = @saved_cs_client */;";
			bw.write(str+"\n");
			
			
			str = "LOCK TABLES `"+RegionMappingDataStorageDB.ATTR_INDEX_TABLE_NAME+"` WRITE;";
			bw.write(str+"\n");
			
			str = "/*!40000 ALTER TABLE `"+RegionMappingDataStorageDB.ATTR_INDEX_TABLE_NAME+"` DISABLE KEYS */;";
			bw.write(str+"\n");
			
			
			String attrInsertQuery = "INSERT INTO `"+RegionMappingDataStorageDB.ATTR_INDEX_TABLE_NAME
									+"` ";
			
			String currLine;
			boolean firstline  = true;
			boolean attrfirsttuple = true;
			
			long attrnumLinesBatched = 0;
			
			
			while( (currLine = br.readLine()) != null )
			{
				if(firstline)
				{
					if( !currLine.startsWith("#") )
					{
						throw new IOException("Wrong file format: First line doesn't start with #");
						// first line contains the column order
					}
					
					//attrInsertQuery = attrInsertQuery 
					//				+ "( "+RegionMappingDataStorageDB.GUID_COL_NAME;
					
					// first column is guid and the attributes in order.
					String[] parsed = currLine.split(",");
					// ignoring fisr column, that should be guid
					for(int i=1; i<parsed.length; i++)
					{
						String attrName = parsed[i].trim();
						if(!AttributeTypes.attributeMap.containsKey(attrName))
						{
							throw new IOException("Attribute "+attrName
													+" not recongnized by CNS");
						}
						attributeOrderList.add(attrName);
						
						//attrInsertQuery = attrInsertQuery + ","+attrName;
					}
					
					//attrInsertQuery = attrInsertQuery + ") VALUES ";
					attrInsertQuery = attrInsertQuery + " VALUES ";
					
					firstline = false;
				}
				else
				{
					String[] tupleParsed = currLine.split(",");
					String currTuple 
							= getATupleForInsertQuery(tupleParsed, attributeOrderList, false);
					
					boolean mapsOnNode 
							= checkIfTupleMapsToNode(tupleParsed, attributeOrderList);
					
					if( mapsOnNode )
					{
						if(attrfirsttuple)
						{
							attrInsertQuery = attrInsertQuery + currTuple;	
							attrfirsttuple = false;
						}
						else
						{
							attrInsertQuery = attrInsertQuery + ","+currTuple;
							attrfirsttuple = false;
						}
						attrnumLinesBatched++;
						
						
						if( (attrnumLinesBatched % MAX_INSERT_BATCHING) == 0 )
						{
							attrInsertQuery = attrInsertQuery + ";";
							bw.write(attrInsertQuery+"\n");
							attrInsertQuery = "INSERT INTO `"+RegionMappingDataStorageDB.ATTR_INDEX_TABLE_NAME
									+"` ";
							
							//attrInsertQuery = attrInsertQuery 
							//		+ "( "+RegionMappingDataStorageDB.GUID_COL_NAME;
							
//							for(int i=0; i<attributeOrderList.size(); i++)
//							{
//								String attrName = attributeOrderList.get(i);
//								//attrInsertQuery = attrInsertQuery + ","+attrName;
//							}
					
							//attrInsertQuery = attrInsertQuery + ") VALUES ";
							attrInsertQuery = attrInsertQuery + " VALUES ";
							attrnumLinesBatched = 0;
							attrfirsttuple = true;
						}
					}
					
				}
			}
			
			// do the remaining
			if(attrnumLinesBatched > 0)
			{
				attrInsertQuery = attrInsertQuery + ";";
				bw.write(attrInsertQuery+"\n");
			}
			
			System.out.println("Writing attrIndex dump completed "
					+(System.currentTimeMillis()-attrIndexTime));
			
			long hashIndexTime = System.currentTimeMillis();
			
			str = "/*!40000 ALTER TABLE `attrIndexDataStorage` ENABLE KEYS */;";
			bw.write(str+"\n");
			
			// release the locks.
			str = "UNLOCK TABLES;";
			bw.write(str+"\n");
			
			
			str = "DROP TABLE IF EXISTS `"+RegionMappingDataStorageDB.GUID_HASH_TABLE_NAME+"`;";
			bw.write(str+"\n");
			
			
			str = "/*!40101 SET @saved_cs_client     = @@character_set_client */;";
			bw.write(str+"\n");
			
			str = "/*!40101 SET character_set_client = utf8 */;";
			bw.write(str+"\n");
			
			
			String hashIndexTableCmd = guidDBStorage.getHashIndexTableCreationCmd();
			bw.write(hashIndexTableCmd+";"+"\n");
			
			
			str = "/*!40101 SET character_set_client = @saved_cs_client */;";
			bw.write(str+"\n");
			
			
			str = "LOCK TABLES `"+RegionMappingDataStorageDB.GUID_HASH_TABLE_NAME+"` WRITE;";
			bw.write(str+"\n");
			
			str = "/*!40000 ALTER TABLE `guidHashDataStorage` DISABLE KEYS */;";
			bw.write(str+"\n");
			
			
			String hashInsertQuery = "INSERT INTO `"+RegionMappingDataStorageDB.GUID_HASH_TABLE_NAME
					+"` ";
			
			br.close();
			
			
			
			firstline  = true;
			
			boolean hashfirsttuple = true;
			
			long hashnumLinesBatched = 0;
			br = new BufferedReader(new FileReader(allguidfilepath));
			
			while( (currLine = br.readLine()) != null )
			{
				if(firstline)
				{
					if( !currLine.startsWith("#") )
					{
						throw new IOException("Wrong file format: First line doesn't start with #");
						// first line contains the column order
					}
					
					//hashInsertQuery = hashInsertQuery 
					//		+ "( "+RegionMappingDataStorageDB.GUID_COL_NAME;
					
					// first column is guid and the attributes in order.
					//String[] parsed = currLine.split(",");
					// ignoring fisr column, that should be guid
//					for(int i=1; i<parsed.length; i++)
//					{
//						String attrName = parsed[i].trim();
//						
//						//hashInsertQuery = hashInsertQuery + ","+attrName;
//					}
					
					//hashInsertQuery = hashInsertQuery + ") VALUES ";
					hashInsertQuery = hashInsertQuery + " VALUES ";
					
					firstline = false;
				}
				else
				{
					String[] tupleParsed = currLine.split(",");
					String currTuple 
							= getATupleForInsertQuery(tupleParsed, attributeOrderList, true);
					
					
					// tupleParsed[0] is guid
					if(Utils.getConsistentHashingNodeID(tupleParsed[0], allNodeIDs) == myId)
					{
						if(hashfirsttuple)
						{	
							hashInsertQuery = hashInsertQuery + currTuple;
							hashfirsttuple = false;
						}
						else
						{	
							hashInsertQuery = hashInsertQuery + ","+currTuple;
							hashfirsttuple = false;
						}
						hashnumLinesBatched++;
						
						
						if( (hashnumLinesBatched % MAX_INSERT_BATCHING) == 0 )
						{
							hashInsertQuery = hashInsertQuery + ";";
							bw.write(hashInsertQuery+"\n");
							hashInsertQuery = "INSERT INTO `"+RegionMappingDataStorageDB.GUID_HASH_TABLE_NAME
									+"` ";
							
							//hashInsertQuery = hashInsertQuery 
							//		+ "( "+RegionMappingDataStorageDB.GUID_COL_NAME;
							
//							for(int i=0; i<attributeOrderList.size(); i++)
//							{
//								String attrName = attributeOrderList.get(i);
//								//hashInsertQuery = hashInsertQuery + ","+attrName;
//							}
					
							//hashInsertQuery = hashInsertQuery + ") VALUES ";
							hashInsertQuery = hashInsertQuery + " VALUES ";
							hashnumLinesBatched = 0;
							hashfirsttuple = true;
						}
					}	
				}
			}
			
			// do the remaining
			if(hashnumLinesBatched > 0)
			{
				hashInsertQuery = hashInsertQuery + ";";
				bw.write(hashInsertQuery+"\n");
			}
			
			System.out.println("Writing hashIndex dump completed "
					+(System.currentTimeMillis()-hashIndexTime));
			
			str = "/*!40000 ALTER TABLE `guidHashDataStorage` ENABLE KEYS */;";
			bw.write(str+"\n");
			
			// release the locks.
			str = "UNLOCK TABLES;";
			bw.write(str+"\n");
			
			str = "/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;";
			bw.write(str+"\n");
			
			str = "/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;";
			bw.write(str+"\n");
			
			str = "/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;";
			bw.write(str+"\n");
			
			str = "/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;";
			bw.write(str+"\n");
			
			str = "/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;";
			bw.write(str+"\n");
			
			str = "/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;";
			bw.write(str+"\n");
			
			str= "/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;";
			bw.write(str+"\n");
			
			str = "/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;";
			bw.write(str+"\n");
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			
			if(bw != null)
			{
				try
				{
					bw.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private void loadDataUsingMySQLScript()
	{
		String connStr = dataSource.getCmdLineConnString();
		
		String currentDir 		 = System.getProperty("user.dir");
		String bulkLoadFilePath = currentDir+"/"
					+ContextServiceConfig.BULK_LOAD_FILE+myId+".sql";
		
		BufferedWriter bw = null;
		String loadCmd = connStr +" -e \"source "+bulkLoadFilePath+"\"";
		
		System.out.println("loadCmd "+loadCmd);
		
		try
		{
			String scriptname = "bulkloadingScript"+myId+".sh";
			bw = new BufferedWriter(new FileWriter(scriptname));
			bw.write(loadCmd+"\n");
			bw.close();
			
			Runtime.getRuntime().exec("chmod +x "+scriptname).waitFor();
			
			long start = System.currentTimeMillis();
			Process proc = Runtime.getRuntime().exec("bash "+scriptname);
			BufferedReader read = new BufferedReader(
					new InputStreamReader(proc.getInputStream()));
			try 
			{
				proc.waitFor();
            } 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
            }
			
			while (read.ready()) 
			{
				System.out.println(read.readLine());
            }
			System.out.println("Data loading ended in "+(System.currentTimeMillis()-start));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
        } catch (InterruptedException e1) 
		{
			e1.printStackTrace();
		}
		finally
		{
			if(bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	
	private boolean checkIfTupleMapsToNode(String[] tupleParsed, 
												List<String> attributeOrderList)
	{
		HashMap<String, AttributeValueRange> attrValRangeMap 
											= new HashMap<String, AttributeValueRange>();
		
		String guid = tupleParsed[0];
		for(int i=1; i<tupleParsed.length; i++)
		{
			String attrName = attributeOrderList.get(i-1);
			String attrVal = tupleParsed[i];
			attrValRangeMap.put(attrName, new AttributeValueRange(attrVal, attrVal));
		}
		
		List<Integer> nodeList = regionMappingPolicy.getNodeIDsForUpdate
															(guid, attrValRangeMap);
		
		if( ifListContainsMyId(nodeList) )
		{
			return true;
		}
		
		return false;
//		if(Utils.getConsistentHashingNodeID(guid, allNodeIDs) == myId)
//		{
//			hashIndexFile.write(currLine+"\n");
//		}
	}
	
	
	private String getATupleForInsertQuery(String[] tupleParsed, 
				List<String> orderedAttrList, boolean hashIndex) throws UnsupportedEncodingException
	{
    	if( !(tupleParsed.length == (orderedAttrList.size()+1)) )
		{
			ContextServiceLogger.getLogger().warning("Bulkloading skipping line for "
					+ "mismatch in number of attributes"+tupleParsed.toString()+"");
		}
    	
    	String currTuple = "(";
    	// first column is always GUID.
    	//String latinCode = new String(Utils.hexStringToByteArray(tupleParsed[0]), LATIN_ENCODING);
    	
    	currTuple = currTuple + "0x"+tupleParsed[0]+"";
    	
    	
    	for(int i=1; i<tupleParsed.length; i++)
    	{
    		String attrName = orderedAttrList.get(i-1);
    		String attrValue = "";
    		
    		attrValue = tupleParsed[i];
			
			AttributeMetaInfo attrMetaInfo 
						= AttributeTypes.attributeMap.get(attrName);
			
			assert(attrMetaInfo != null);
		    String dataType = attrMetaInfo.getDataType();
			
		    attrValue = AttributeTypes.convertStringToDataTypeForMySQL
					(attrValue, dataType)+"";
		    
		    currTuple = currTuple +","+attrValue;
    	}
    	if(!hashIndex)
    	{
    		currTuple = currTuple +")";
    	}
    	else
    	{
    		// for unset attr
    		currTuple = currTuple +" , '{}' )";
    	}
    	return currTuple;
	}
	
	
	private boolean ifListContainsMyId(List<Integer> nodeList)
	{
		for(int i=0; i<nodeList.size(); i++)
		{
			if(nodeList.get(i) == myId)
			{
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args)
	{	
	}
}