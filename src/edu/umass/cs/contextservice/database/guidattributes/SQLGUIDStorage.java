package edu.umass.cs.contextservice.database.guidattributes;

import java.sql.Connection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.contextservice.profilers.CNSProfiler;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;

/**
 * This class implements GUIDAttributeStorageInterface.
 * This class has methods for table creation, updates and searches of 
 * context service.
 * @author adipc
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class SQLGUIDStorage implements GUIDStorageInterface
{
	private final int myNodeID;
	
	private final AbstractDataSource dataSource;
	private final CNSProfiler cnsProfiler;
	
	public SQLGUIDStorage( int myNodeID, AbstractDataSource dataSource ,
			CNSProfiler cnsProfiler )
	{
		this.myNodeID = myNodeID;
		this.dataSource = dataSource;
		this.cnsProfiler = cnsProfiler;
	}
	
	@Override
	public void createDataStorageTables() 
	{
	}
	
	/**
	 * returns attribute index table creation command. 
	 * @return
	 */
	public String getAttrIndexTableCreationCmd()
	{	
		return null;
	}
	
	
	public String getHashIndexTableCreationCmd()
	{
		return null;
	}
	
	
	public int processSearchQueryUsingAttrIndex
			(HashMap<String, AttributeValueRange> queryAttrValRange, 
					JSONArray resultArray)
	{
		return -1;
	}
	
	public JSONObject getGUIDStoredUsingHashIndex( String guid, Connection myConn )
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
    public void storeGUIDUsingHashIndex( String nodeGUID, 
    		JSONObject jsonToWrite, int updateOrInsert , Connection myConn) throws JSONException
    {
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
    public void storeGUIDUsingAttrIndex( String tableName, String nodeGUID, 
    		JSONObject updatedAttrValJSON, int updateOrInsert ) throws JSONException
    {
    }
    
    public void deleteGUIDFromTable(String tableName, String nodeGUID)
	{
	}
}