package edu.umass.cs.contextservice.messages;

import org.json.JSONException;
import org.json.JSONObject;

public class GetReplyMessage extends ContextServicePacket
{
	private enum Keys {GetReqID, GUIDsToGet, GUIDObject};
	
	private final long getReqID;
	private final String guidToGet;
	private final JSONObject guidObject;
	
	public GetReplyMessage(Integer initiator, long getReqID, String guidToGet, JSONObject guidObject)
	{
		super(initiator, ContextServicePacket.PacketType.GET_REPLY_MESSAGE);
		this.getReqID = getReqID;
		this.guidToGet = guidToGet;
		this.guidObject = guidObject;
	}
	
	public GetReplyMessage(JSONObject json) throws JSONException
	{
		super(json);
		this.getReqID = json.getLong(Keys.GetReqID.toString());
		this.guidToGet = json.getString(Keys.GUIDsToGet.toString());
		this.guidObject = json.getJSONObject(Keys.GUIDObject.toString());
	}
	
	public JSONObject toJSONObjectImpl() throws JSONException
	{
		JSONObject json = super.toJSONObjectImpl();
		json.put(Keys.GetReqID.toString(), this.getReqID);
		json.put(Keys.GUIDsToGet.toString(), guidToGet);
		json.put(Keys.GUIDObject.toString(), this.guidObject);
		return json;
	}
	
	public String getGUIDsToGet()
	{
		return this.guidToGet;
	}
	
	public long getReqID()
	{
		return this.getReqID;
	}
	
	public JSONObject getGUIDObject()
	{
		return this.guidObject;
	}
	
	public static void main(String[] args)
	{
	}
}