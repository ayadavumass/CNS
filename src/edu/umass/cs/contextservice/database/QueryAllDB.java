package edu.umass.cs.contextservice.database;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;

/**
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class QueryAllDB
{
	public static final int UPDATE_REC 								= 1;
	public static final int INSERT_REC 								= 2;
	
	// maximum query length of 1000bytes
	public static final int MAX_QUERY_LENGTH						= 1000;
	
	public static final String groupGUID 							= "groupGUID";
	public static final String userIP 								= "userIP";
	public static final String userPort 							= "userPort";
	
	private AbstractDataSource dataSource;
	
	public QueryAllDB( Integer myNodeID )
			throws Exception
	{
	}
	
	public int processSearchQueryInSubspaceRegion( String query, 
			JSONArray resultArray)
	{		
		return -1;
	}
	
	/**
	 * Returns the search query, it doesn't execute. This
	 * is done so that this can be executed as a nested query in privacy case.
	 * HyperspaceMySQLDB calling this function has more info.
	 * @param subspaceId
	 * @param query
	 * @param resultArray
	 * @return
	 */
	public String getMySQLQueryForProcessSearchQueryInSubspaceRegion
										( String query)
	{
		return null;
	}
	
	public JSONObject getGUIDStoredInPrimarySubspace( String guid )
	{
		return null;
	}
	
	/**
     * Stores GUID in a subspace. The decision to store a guid on this node
     * in this subspace is not made in this function.
     * @param subspaceNum
     * @param nodeGUID
     * @param attrValuePairs
     * @return
     * @throws JSONException
     */
    public void storeGUIDInPrimarySubspace(String tableName, String nodeGUID, 
    		JSONObject updatedAttrValJSON, int updateOrInsert) throws JSONException
    {
    }
}