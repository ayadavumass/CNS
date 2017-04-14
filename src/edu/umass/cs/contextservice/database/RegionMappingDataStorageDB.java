package edu.umass.cs.contextservice.database;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.database.guidattributes.SQLGUIDStorage;
import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.contextservice.database.guidattributes.GUIDStorageInterface;
import edu.umass.cs.contextservice.database.triggers.GroupGUIDInfoClass;
import edu.umass.cs.contextservice.database.triggers.TriggerInformationStorageInterface;
import edu.umass.cs.contextservice.profilers.CNSProfiler;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;


/**
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class RegionMappingDataStorageDB extends AbstractDataStorageDB
{
	public static final int UPDATE_REC 								= 1;
	public static final int INSERT_REC 								= 2;
	
	// maximum query length of 1000bytes
	public static final int MAX_QUERY_LENGTH						= 1000;
	
	// FIXME: move these macros to contextserviceconfig file. 
	//public static final String userQuery = "userQuery";
	public static final String groupGUID 							= "groupGUID";
	public static final String userIP 								= "userIP";
	public static final String userPort 							= "userPort";
	
	public static final String anonymizedIDToGUIDMappingColName     = "anonymizedIDToGUIDMapping";
	
	// this col stores attrs which are not set by the user.
	// this information is used in indexing scheme.
	public static final String unsetAttrsColName					= "unsetAttrs";
	
	public static final String GUID_HASH_TABLE_NAME					= "guidHashDataStorage";
	
	public static final String ATTR_INDEX_TABLE_NAME				= "attrIndexDataStorage";
	
	public static final String ATTR_INDEX_TRIGGER_TABLE_NAME		= "triggerDataStorage";
	
	public static final String HASH_INDEX_TRIGGER_TABLE_NAME		= "queryHashTriggerDataStorage";
	
	// FIXME: use this macro at every place and change it to GUID instead of
	// nodeGUID.
	public static final String GUID_COL_NAME						= "nodeGUID";
	
	
	//unsetAttrsColName is varchar type for now.
	// FIXME: currently JSONObject is stored as string, but in future
	// it should be changed to bitmap to save space and stringification overhead.
	public static final int varcharSizeForunsetAttrsCol				= 1000;
	
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
		return -1;
	}
	
	public JSONObject getGUIDStoredUsingHashIndex( String guid, Connection myConn )
	{
		return null;
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
	}
	
	/**
	 * Returns a JSONArray of JSONObjects denoting each row in the table
	 * @param subspaceNum
	 * @param hashCode
	 * @return
	 * @throws InterruptedException 
	 */
	public void getTriggerDataInfo( JSONObject oldValJSON, JSONObject updateAttrJSON, 
		HashMap<String, GroupGUIDInfoClass> oldValGroupGUIDMap, 
		HashMap<String, GroupGUIDInfoClass> newValGroupGUIDMap, 
		int requestType, JSONObject newUnsetAttrs, boolean firstTimeInsert)
				throws InterruptedException
	{
	}
	
	/**
	 * this function runs independently on every node 
	 * and deletes expired queries.
	 * @return
	 */
	public int deleteExpiredSearchQueries()
	{
		return -1;
	}
	
	
	public void storeGUIDUsingHashIndex( String nodeGUID, 
    		JSONObject jsonToWrite, int updateOrInsert, Connection myConn ) throws JSONException
	{
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
    }
    
	public void deleteGUIDFromTable(String tableName, String nodeGUID)
	{
	}
	
	public boolean checkAndInsertSearchQueryRecordFromPrimaryTriggerSubspace( String groupGUID, 
			String userIP, int userPort ) throws UnknownHostException
	{
		return false;
	}
}