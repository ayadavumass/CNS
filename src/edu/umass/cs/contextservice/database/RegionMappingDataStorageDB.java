package edu.umass.cs.contextservice.database;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.config.ContextServiceConfig;
import edu.umass.cs.contextservice.database.guidattributes.SQLGUIDStorage;
import edu.umass.cs.contextservice.database.recordformat.HashIndexGUIDRecord;
import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.contextservice.database.guidattributes.GUIDStorageInterface;
import edu.umass.cs.contextservice.database.triggers.GroupGUIDInfoClass;
import edu.umass.cs.contextservice.database.triggers.TriggerInformationStorage;
import edu.umass.cs.contextservice.database.triggers.TriggerInformationStorageInterface;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.contextservice.profilers.CNSProfiler;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;


public class RegionMappingDataStorageDB extends AbstractDataStorageDB
{
	private final GUIDStorageInterface guidAttributesStorage;
	private  TriggerInformationStorageInterface triggerInformationStorage;
	
	private final CNSProfiler cnsprofiler;
	
	private AbstractDataSource dataSource;
	
	public RegionMappingDataStorageDB( Integer myNodeID, 
			AbstractDataSource abstractDataSource, CNSProfiler cnsProfiler)
			throws Exception
	{
		this.dataSource = abstractDataSource;
		this.cnsprofiler = cnsProfiler;
		
		guidAttributesStorage = new SQLGUIDStorage
							(myNodeID, abstractDataSource, cnsprofiler );
		
		if( ContextServiceConfig.triggerEnabled )
		{
			// Currently it is assumed that there are only conjunctive queries
			// DNF form queries can be added by inserting its multiple conjunctive 
			// components.
			ContextServiceLogger.getLogger().fine( "HyperspaceMySQLDB "
					+ " TRIGGER_ENABLED "+ContextServiceConfig.triggerEnabled );
			triggerInformationStorage = new TriggerInformationStorage
											(myNodeID , abstractDataSource);
		}
		
		createTables();
	}
	
	public AbstractDataSource getDataSource()
	{
		return this.dataSource;
	}
	
	public GUIDStorageInterface getGUIDStorageInterface()
	{
		return guidAttributesStorage;
	}
	
	/**
	 * Creates tables needed for 
	 * the database.
	 * @throws SQLException
	 */
	private void createTables()
	{	
		// slightly inefficient way of creating tables
		// as it loops through subspaces three times
		// instead of one, but it only happens in the beginning
		// so not a bottleneck.
		guidAttributesStorage.createDataStorageTables();
		
		if( ContextServiceConfig.triggerEnabled )
		{
			// currently it is assumed that there are only conjunctive queries
			// DNF form queries can be added by inserting its multiple conjunctive components.			
			triggerInformationStorage.createTriggerStorageTables();
		}
	}
	
	/**
	 * This function is implemented here as it involves 
	 * joining guidAttrValueStorage and privacy storage tables.
	 * @param subspaceId
	 * @param query
	 * @param resultArray
	 * @return
	 */
	public int processSearchQueryUsingAttrIndex( HashMap<String, AttributeValueRange> 
			queryAttrValRange, JSONArray resultArray )
	{
		int resultSize 
			= this.guidAttributesStorage.processSearchQueryUsingAttrIndex
												(queryAttrValRange, resultArray);
		
		return resultSize;	
	}
	
	
	public HashIndexGUIDRecord getGUIDStoredUsingHashIndex( String guid, Connection myConn )
	{
		HashIndexGUIDRecord guidRecord 
						= this.guidAttributesStorage.getGUIDStoredUsingHashIndex(guid, myConn);
		return guidRecord;
	}
	
	/**
	 * Inserts trigger info on a query into the table
	 * @param subspaceNum
	 * @param subspaceVector
	 */
	public void insertIntoTriggerDataStorage(  
			String userQuery, String groupGUID, String userIP, 
			int userPort, long expiryTimeFromNow )
	{
		this.triggerInformationStorage.insertIntoTriggerDataStorage
			(userQuery, groupGUID, userIP, userPort, expiryTimeFromNow);
	}
	
	/**
	 * Returns a JSONArray of JSONObjects denoting each row in the table
	 * @param subspaceNum
	 * @param hashCode
	 * @return
	 * @throws InterruptedException 
	 */
	public void getTriggerDataInfo( HashIndexGUIDRecord oldGuidRec, 
			JSONObject updateAttrJSON, HashMap<String, GroupGUIDInfoClass> oldValGroupGUIDMap, 
		HashMap<String, GroupGUIDInfoClass> newValGroupGUIDMap, 
		int requestType, JSONObject newUnsetAttrs, boolean firstTimeInsert)
				throws InterruptedException
	{
		this.triggerInformationStorage.getTriggerDataInfo
			( oldGuidRec, updateAttrJSON, oldValGroupGUIDMap, 
				newValGroupGUIDMap, requestType, newUnsetAttrs, firstTimeInsert );
	}
	
	/**
	 * this function runs independently on every node 
	 * and deletes expired queries.
	 * @return
	 */
	public int deleteExpiredSearchQueries()
	{
		return this.triggerInformationStorage.deleteExpiredSearchQueries();
	}
	
	public void storeGUIDUsingHashIndex( String nodeGUID, 
    		JSONObject jsonToWrite, int updateOrInsert, Connection myConn ) throws JSONException
	{
		this.guidAttributesStorage.storeGUIDUsingHashIndex
			( nodeGUID, jsonToWrite, updateOrInsert, myConn);
	}
	
	/**
     * Stores GUID in a subspace. The decision to store a guid on this node
     * in this subspace is not made in this function.
     * @param subspaceNum
     * @param nodeGUID
     * @param attrValuePairs
     * @param primaryOrSecondarySubspaces true if update is happening 
     * to primary subspace, false if update is for subspaces.
     * @return
     * @throws JSONException
     */
    public void storeGUIDUsingAttrIndex( String tableName, String nodeGUID, 
    		JSONObject jsonToWrite, int updateOrInsert) throws JSONException
    {	
		// no need to add realIDEntryption Info in primary subspaces.
		this.guidAttributesStorage.storeGUIDUsingAttrIndex
					(tableName, nodeGUID, jsonToWrite, updateOrInsert);
    }
	
	public void deleteGUIDFromTable(String tableName, String nodeGUID)
	{
		this.guidAttributesStorage.deleteGUIDFromTable(tableName, nodeGUID);
	}
	
	public boolean checkAndInsertSearchQueryRecordFromPrimaryTriggerSubspace( String groupGUID, 
			String userIP, int userPort ) throws UnknownHostException
	{
		return triggerInformationStorage.checkAndInsertSearchQueryRecordFromPrimaryTriggerSubspace
				(groupGUID, userIP, userPort);
	}
}