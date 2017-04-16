package edu.umass.cs.contextservice.database;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.contextservice.database.recordformat.HashIndexGUIDRecord;
import edu.umass.cs.contextservice.database.triggers.GroupGUIDInfoClass;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;

public abstract class AbstractDataStorageDB
{	
	public abstract int processSearchQueryUsingAttrIndex
			( HashMap<String, AttributeValueRange> 
					queryAttrValRange, JSONArray resultArray );
	
	public abstract HashIndexGUIDRecord getGUIDStoredUsingHashIndex
												( String guid, Connection myConn );
	
	public abstract void insertIntoTriggerDataStorage( String userQuery, 
			String groupGUID, String userIP, 
			int userPort, long expiryTimeFromNow );
	
	public abstract void getTriggerDataInfo( HashIndexGUIDRecord oldGuidRec, 
			JSONObject newJSONToWrite, 
			HashMap<String, GroupGUIDInfoClass> oldValGroupGUIDMap, 
			HashMap<String, GroupGUIDInfoClass> newValGroupGUIDMap, 
			int requestType, JSONObject newUnsetAttrs, boolean firstTimeInsert) 
					throws InterruptedException;
	
	public abstract int deleteExpiredSearchQueries();
	
	public abstract void storeGUIDUsingHashIndex( String nodeGUID, 
    		JSONObject jsonToWrite, int updateOrInsert, Connection myConn) 
    														throws JSONException;
	
	public abstract void storeGUIDUsingAttrIndex( String tableName, String nodeGUID, 
    		JSONObject jsonToWrite, int updateOrInsert) throws JSONException;
	
	public abstract void deleteGUIDFromTable(String tableName, String nodeGUID);
	
	public abstract boolean checkAndInsertSearchQueryRecordFromPrimaryTriggerSubspace
			( String groupGUID, String userIP, int userPort ) 
															throws UnknownHostException;
	
	public abstract AbstractDataSource getDataSource();
}