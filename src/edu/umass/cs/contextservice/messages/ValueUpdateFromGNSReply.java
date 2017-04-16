package edu.umass.cs.contextservice.messages;

import org.json.JSONException;
import org.json.JSONObject;

public class ValueUpdateFromGNSReply extends ContextServicePacket
{
	// start time is the time when update started,
	// context time is the time at which context service recvd query
	// send time is the time when context service sends the ValueUpdateFromGNSReply
	private enum Keys {VERSION_NUM, USER_REQ_NUM};
	
	private final long versionNum;
	private final long userReqNum;
	
	public ValueUpdateFromGNSReply(Integer initiator, long versionNum, long userReqNum)
	{
		super(initiator, ContextServicePacket.PacketType.VALUE_UPDATE_MSG_FROM_GNS_REPLY);
		this.versionNum = versionNum;
		this.userReqNum = userReqNum;
	}
	
	public ValueUpdateFromGNSReply(JSONObject json) throws JSONException
	{
		super(json);
		this.versionNum = json.getLong(Keys.VERSION_NUM.toString());
		this.userReqNum = json.getLong(Keys.USER_REQ_NUM.toString());
	}
	
	public JSONObject toJSONObjectImpl() throws JSONException
	{
		JSONObject json = super.toJSONObjectImpl();
		json.put(Keys.VERSION_NUM.toString(), this.versionNum);
		json.put(Keys.USER_REQ_NUM.toString(), this.userReqNum);
		
		return json;
	}
	
	public long getVersionNum()
	{
		return this.versionNum;
	}
	
	public long getUserReqNum()
	{
		return this.userReqNum;
	}
	
	public static void main(String[] args)
	{
	}
}