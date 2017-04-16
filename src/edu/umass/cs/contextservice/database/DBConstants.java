package edu.umass.cs.contextservice.database;

/**
 * The class contains all static final fields related to DB
 * constants such as table names, column sizes, operation types etc.
 * @author ayadav
 *
 */
public class DBConstants 
{
	public static final int UPDATE_REC 								= 1;
	public static final int INSERT_REC 								= 2;
	
	// maximum query length of 1000bytes
	public static final int MAX_QUERY_LENGTH						= 1000;
	
	public static final String GROUP_GUID 							= "groupGUID";
	public static final String USER_IP 								= "userIP";
	public static final String USER_PORT 							= "userPort";
	
	public static final String ANONYMIZEDID_TO_GUID_COLNAME     	= "anonymizedIDToGUIDMapping";
	
	// this col stores attrs which are not set by the user.
	// this information is used in indexing scheme.
	public static final String UNSET_ATTR_COLNAME					= "unsetAttrs";
	
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
	public static final int VARCHAR_SIZE_UNSET_ATTR_COL				= 1000;
}