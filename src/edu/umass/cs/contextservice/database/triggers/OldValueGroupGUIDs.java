package edu.umass.cs.contextservice.database.triggers;

import java.util.HashMap;

import org.json.JSONObject;


import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;

/**
 * 
 * Class created to parallelize old and new guids fetching
 * @author adipc
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class OldValueGroupGUIDs implements Runnable
{
	private JSONObject oldValJSON;
	private JSONObject updateValJSON;
	private JSONObject newUnsetAttrs;
	private HashMap<String, GroupGUIDInfoClass> oldValGroupGUIDMap;
	private final AbstractDataSource dataSource;
	
	
	public OldValueGroupGUIDs(JSONObject oldValJSON, 
			JSONObject updateValJSON, JSONObject newUnsetAttrs,
			HashMap<String, GroupGUIDInfoClass> oldValGroupGUIDMap,
			AbstractDataSource dataSource )
	{
		this.oldValJSON = oldValJSON;
		this.updateValJSON = updateValJSON;
		this.newUnsetAttrs = newUnsetAttrs;
		this.oldValGroupGUIDMap = oldValGroupGUIDMap;
		this.dataSource = dataSource;
	}
	@Override
	public void run() 
	{
		
	}
}