package edu.umass.cs.contextservice.database.triggers;

import java.net.UnknownHostException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;

/**
 * Implements the trigger storage table creation
 * and search and update trigger storage.
 * @author adipc
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class TriggerInformationStorage implements 
										TriggerInformationStorageInterface
{
	private final AbstractDataSource dataSource;
	
	public TriggerInformationStorage( Integer myNodeID, 
			AbstractDataSource dataSource )
	{
		this.dataSource = dataSource;
	}
	
	
	@Override
	public void createTriggerStorageTables()
	{
	}
	
	/**
	 * Inserts trigger info on a query into the table
	 * @param subspaceNum
	 * @param subspaceVector
	 */
	public void insertIntoTriggerDataStorage( String userQuery, 
			String groupGUID, String userIP, int userPort, 
			long expiryTimeFromNow )
	{
	}
	
	/**
	 * returns a JSONArray of JSONObjects denoting each row in the table
	 * @param subspaceNum
	 * @param hashCode
	 * @return
	 * @throws InterruptedException 
	 */
	public void getTriggerDataInfo( JSONObject oldValJSON, JSONObject onlyUpdateAttrValJSON, 
		HashMap<String, GroupGUIDInfoClass> removedGroupGUIDMap, 
		HashMap<String, GroupGUIDInfoClass> addedGroupGUIDMap, 
		int requestType, JSONObject newUnsetAttrs,
		boolean firstTimeInsert )
					throws InterruptedException
	{
	}
	
	/**
	 * Returns search queries that contain attributes of an update, 
	 * as only those search queries can be affected.
	 * This helps in reducing the size of the search queries that needs to be checked
	 * further if the GUID in update satisfies that or not.
	 * @param attrsInUpdate
	 * @return
	 */
	public static String getQueriesThatContainAttrsInUpdate( JSONObject attrsInUpdate )
	{
		return null;
	}
	
	public static String getQueryToGetOldValueGroups(JSONObject oldValJSON) 
			throws JSONException
	{
		return null;
	}
	
	public static String getQueryToGetNewValueGroups
				( JSONObject oldValJSON, JSONObject newJSONToWrite, 
						JSONObject newUnsetAttrs )
								throws JSONException
	{
		return null;
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
	
	public boolean checkAndInsertSearchQueryRecordFromPrimaryTriggerSubspace(String groupGUID, 
			String userIP, int userPort) 
					throws UnknownHostException
	{
		return false;
	}
}