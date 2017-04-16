package edu.umass.cs.contextservice.database.recordformat;


import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.database.DBConstants;

/**
 * This class defines the format for hash index guid record. 
 * @author ayadav
 *
 */
public class HashIndexGUIDRecord 
{	
	// attribute value json
	private final JSONObject attrValJSON;
	
	// unset attribute json. 
	// this json contains attributes that are not yet set by the users.
	private final JSONObject unsetAttrJSON;
	
	
	// anonymized ID to guid info is used in privacy scheme.
	private final JSONArray anonymizedIDToGUIDInfo;
	
	
	public HashIndexGUIDRecord(JSONObject attrValJSON, 
				JSONObject unsetAttrJSON, JSONArray anonymizedIDToGUIDInfo)
	{
		this.attrValJSON = attrValJSON;
		this.unsetAttrJSON = unsetAttrJSON;
		this.anonymizedIDToGUIDInfo = anonymizedIDToGUIDInfo;
	}
	
	
	public JSONObject getAttrValJSON()
	{
		return attrValJSON;
	}
	
	public JSONObject getUnsetAttrJSON()
	{
		return unsetAttrJSON;
	}
	
	public JSONArray getAnonymizedIDToGUIDInfo()
	{
		return anonymizedIDToGUIDInfo;
	}
	
	public JSONObject getUnsetAttrJSON(JSONObject attrValJSON)
	{
		JSONObject unsetAttrJSON = null;
		
		try
		{
			if( attrValJSON.has(DBConstants.UNSET_ATTR_COLNAME) )
			{
				String jsonString 
						= attrValJSON.getString( DBConstants.UNSET_ATTR_COLNAME );
				
				if( jsonString != null )
				{
					if( jsonString.length() > 0 )
					{
						unsetAttrJSON = new JSONObject(jsonString);
					}
				}
			}
		}
		catch(JSONException jsonException)
		{
			jsonException.printStackTrace();
		}
		return unsetAttrJSON;
	}
	
	
	public boolean checkIfAnonymizedIDToGuidInfoAlreadyStored() 
			throws JSONException
	{
		boolean alreadyStored = false;
		if( anonymizedIDToGUIDInfo != null )
		{
			
			// We don't need to compare the actual value,
			// because this information change causes whole
			// anonymized ID to change, so anonymizedIDToGUIDMapping
			// either inserted or deleted, but never updated for 
			// an anonymized ID.
			if(anonymizedIDToGUIDInfo.length() > 0)
			{
				alreadyStored = true;
				return alreadyStored;
			}
			return alreadyStored;
		}
		else
		{
			return alreadyStored;
		}
	}
	
	public JSONObject toJSONObject()
	{
		JSONObject json = new JSONObject();
		
		//copying attrVal JSON
		@SuppressWarnings("unchecked")
		Iterator<String> attrKey = attrValJSON.keys();
		
		while(attrKey.hasNext())
		{
			String attrName = attrKey.next();
			
			try 
			{
				json.put(attrName, attrValJSON.getString(attrName));
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		
		if(unsetAttrJSON != null)
		{
			try 
			{
				json.put(DBConstants.UNSET_ATTR_COLNAME, unsetAttrJSON);
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		
		if(anonymizedIDToGUIDInfo != null)
		{
			try 
			{
				json.put(DBConstants.ANONYMIZEDID_TO_GUID_COLNAME, anonymizedIDToGUIDInfo);
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		
		return json;
	}
	
	
	public static HashIndexGUIDRecord fromJSON(JSONObject json)
	{
		@SuppressWarnings("unchecked")
		Iterator<String> keyIter = json.keys();
		
		JSONArray anonymizedInfo = null;
		JSONObject unsetAttrs = null;
		JSONObject attrValJSON = new JSONObject();
		while(keyIter.hasNext())
		{
			String key = keyIter.next();
			
			if( key.equals(DBConstants.ANONYMIZEDID_TO_GUID_COLNAME) )
			{
				try 
				{
					anonymizedInfo = json.getJSONArray(key);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			else if(key.equals(DBConstants.UNSET_ATTR_COLNAME))
			{
				try
				{
					unsetAttrs = json.getJSONObject(key);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			else
			{
				try 
				{
					attrValJSON.put(key, json.get(key));
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		return new HashIndexGUIDRecord(attrValJSON, unsetAttrs, 
				anonymizedInfo);
	}
}