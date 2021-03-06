package edu.umass.cs.contextservice.database.guidattributes;

import java.sql.Connection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.database.recordformat.HashIndexGUIDRecord;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;


/**
 * This interface defines the attributes and guid storage 
 * tables, methods to search and update those tables.
 * This interface defines the tables for context service
 * that supports updates and range queries.
 * @author adipc
 */
public interface GUIDStorageInterface 
{
	public void createDataStorageTables();
	
	public int processSearchQueryUsingAttrIndex
						( HashMap<String, AttributeValueRange> queryAttrValMap, 
								JSONArray resultArray);
	
	public HashIndexGUIDRecord getGUIDStoredUsingHashIndex
											( String guid, Connection myConn );
	
	public void storeGUIDUsingHashIndex(String nodeGUID, JSONObject jsonToWrite, 
    		int updateOrInsert, Connection myConn) throws JSONException;
	
	public void storeGUIDUsingAttrIndex( String tableName, String nodeGUID, 
    		JSONObject updatedAttrValMap, int updateOrInsert )
    					throws JSONException;
	
	public void deleteGUIDFromTable(String tableName, String nodeGUID);
	
	public String getAttrIndexTableCreationCmd();
	
	public String getHashIndexTableCreationCmd();
}