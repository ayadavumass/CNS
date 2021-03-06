package edu.umass.cs.contextservice.bulkloading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.umass.cs.contextservice.attributeInfo.AttributeTypes;
import edu.umass.cs.contextservice.config.ContextServiceConfig;
import edu.umass.cs.contextservice.database.DBConstants;
import edu.umass.cs.contextservice.database.RegionMappingDataStorageDB;
import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
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
public class BulkLoadDataInMySQLFromAFile
{
	private final int myId;
	private final String allguidfilepath;
	private final AbstractDataSource dataSource;
	private final AbstractRegionMappingPolicy regionMappingPolicy;
	private final List<Integer> allNodeIDs;
	
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
	public BulkLoadDataInMySQLFromAFile(int myId, String allguidfilepath, 
				AbstractDataSource dataSource, AbstractRegionMappingPolicy regionMappingPolicy,
				List<Integer> allNodeIDs)
	{
		this.myId = myId;
		this.allguidfilepath = allguidfilepath;
		this.dataSource = dataSource;
		this.regionMappingPolicy = regionMappingPolicy;	
		this.allNodeIDs = allNodeIDs;
	}
	
	/**
	 * Bulk loads the data
	 */
	public void bulkLoadData()
	{
		List<String> attributeOrderList = writeNodeSpecificBulkLoadingFiles();
		loadDataFromFilesInMysql(attributeOrderList);
	}
	
	/**
	 * Writes node specific bulk loading files. There are two files.
	 * One is for guid hash index table and other is for attr index table.
	 * These files contain guids that have to be stored in tables on this node.
	 */
	private List<String> writeNodeSpecificBulkLoadingFiles()
	{
		BufferedReader br 			 = null;
		BufferedWriter attrIndexFile = null;
		BufferedWriter hashIndexFile = null;
		List<String> attributeOrderList = new LinkedList<String>();
		try
		{
			br = new BufferedReader(new FileReader(allguidfilepath));
			attrIndexFile = new BufferedWriter(new FileWriter
								(ContextServiceConfig.ATTR_INDEX_FILE_PREFIX+myId));
			hashIndexFile = new BufferedWriter(new FileWriter
								(ContextServiceConfig.HASH_INDEX_FILE_PREFIX+myId));
					
			String currLine;
			
			boolean firstline = true;
			
			while( (currLine = br.readLine()) != null )
			{
				if(firstline)
				{
					if( !currLine.startsWith("#") )
					{
						throw new IOException("Wrong file format: First line doesn't start with #");
						// first line contains the column order
					}
					
					// first column is guid and the attributes in order.
					String[] parsed = currLine.split(",");
					// ignoring fisr column, that should be guid
					for(int i=1; i<parsed.length; i++)
					{
						String attrName = parsed[i].trim();
						if(!AttributeTypes.attributeMap.containsKey(attrName))
						{
							throw new IOException("Attribute "+attrName+" not recongnized by CNS");
						}
						attributeOrderList.add(attrName);
					}
					attrIndexFile.write(currLine+"\n");
					hashIndexFile.write(currLine+"\n");
					
					firstline = false;
				}
				else
				{
					String[] parsed = currLine.split(",");
					
					if( !(parsed.length == (attributeOrderList.size()+1)) )
					{
						ContextServiceLogger.getLogger().warning("Bulkloading skipping line for "
								+ "mismatch in number of attributes"+currLine+"");
					}
					else
					{
						String guid = parsed[0];
						HashMap<String, AttributeValueRange> attrValRangeMap 
															= new HashMap<String, AttributeValueRange>();
						
						for(int i=1; i<parsed.length; i++)
						{
							String attrName = attributeOrderList.get(i-1);
							String attrVal = parsed[i];
							attrValRangeMap.put(attrName, new AttributeValueRange(attrVal, attrVal));
						}
						
						List<Integer> nodeList = regionMappingPolicy.getNodeIDsForUpdate
																			(guid, attrValRangeMap);
						
						if( ifListContainsMyId(nodeList) )
						{
							attrIndexFile.write(currLine+"\n");
						}
						
						if(Utils.getConsistentHashingNodeID(guid, allNodeIDs) == myId)
						{
							hashIndexFile.write(currLine+"\n");
						}
					}
				}
			}
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
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			
			if(attrIndexFile != null)
			{
				try
				{
					attrIndexFile.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			
			if(hashIndexFile != null)
			{
				try 
				{
					hashIndexFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return attributeOrderList;
	}
	
	
	/**
	 * bulk loads data from files into mysql.
	 */
	private void loadDataFromFilesInMysql(List<String> attributeOrderList)
	{
		String currentDir 		 = System.getProperty("user.dir");
		String attrIndexFilePath = currentDir+"/"
					+ContextServiceConfig.ATTR_INDEX_FILE_PREFIX+myId;
		
		String hashIndexFilePath = currentDir+"/"
					+ContextServiceConfig.HASH_INDEX_FILE_PREFIX+myId;
		
		
		String cmd = getMySqlLoadCommand(attrIndexFilePath, 
				DBConstants.ATTR_INDEX_TABLE_NAME, 
				attributeOrderList);
		
		
		MySqlOperationThread attrIndexCmd = new MySqlOperationThread(cmd);
		
		
		cmd = getMySqlLoadCommand(hashIndexFilePath, 
				DBConstants.GUID_HASH_TABLE_NAME, 
				attributeOrderList);
		
		MySqlOperationThread hashIndexCmd = new MySqlOperationThread(cmd);
		
		Thread th1 = new Thread(attrIndexCmd);
		Thread th2 = new Thread(hashIndexCmd);
		
		long start = System.currentTimeMillis();
		
		try 
		{
			th1.start();
			th1.join();
			
			th2.start();
			th2.join();
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("Data loading for both attr and hash index finished "
							+(System.currentTimeMillis()-start));	
		
		//LOAD DATA INFILE '/proj/MobilityFirst/ayadavDir/contextServiceScripts/guidsInfoFile.txt' INTO TABLE testTable FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' (@hexGUID, attr0, attr1) SET nodeGUID=UNHEX(@hexGUID);
	}
	
	
	private String getMySqlLoadCommand(String inputFilePath, String tablename, 
								List<String> attributeOrderList)
	{
		String cmd = "LOAD DATA INFILE '"+inputFilePath+"' INTO TABLE "
				+ tablename 
				+ " FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' IGNORE 1 LINES "
				+ "(@hexGUID";//, attr0, attr1) SET nodeGUID=UNHEX(@hexGUID);

		for(int i=0; i< attributeOrderList.size(); i++)
		{
			cmd = cmd+" , "+attributeOrderList.get(i);
		}
		//FIXME: nodeGUID column name is harcoded, needs to be changed to a variable.
		cmd = cmd + " ) SET nodeGUID=UNHEX(@hexGUID)";
		return cmd;
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
	
	private class MySqlOperationThread implements Runnable
	{
		private final String command;
		
		public MySqlOperationThread(String cmd)
		{
			this.command = cmd;
		}
		
		@Override
		public void run()
		{
			Connection myConn  = null;
			Statement  stmt    = null;
			try
			{
				myConn = dataSource.getConnection();
				stmt   = myConn.createStatement();
				
				long start = System.currentTimeMillis();
				stmt.execute(command);
				long end = System.currentTimeMillis();
				System.out.println("Data loading ended "+(end-start));
			}
			catch( SQLException mysqlEx )
			{
				mysqlEx.printStackTrace();
			}
			finally
			{
				try
				{
					if( stmt != null )
						stmt.close();
					
					if( myConn != null )
						myConn.close();
				}
				catch( SQLException sqex )
				{
					sqex.printStackTrace();
				}
			}
		}
	}
	
	
	public static void main(String[] args)
	{
		
	}
}