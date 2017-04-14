package edu.umass.cs.contextservice.updates;

import java.util.HashMap;

import org.json.JSONArray;

import edu.umass.cs.contextservice.database.triggers.GroupGUIDInfoClass;
import edu.umass.cs.contextservice.messages.ValueUpdateFromGNS;
import edu.umass.cs.contextservice.profilers.UpdateStats;

/**
 * 
 * @author ayadav
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class UpdateInfo
{	
	private final ValueUpdateFromGNS valUpdMsgFromGNS;
	
	private final long updateRequestId;
	
	private boolean updateReqCompl;
	
	private int totalExpectedValueUpdateReplies									= -1;
	private int valueUpdateRepliesCounter 										= 0;
	
	private HashMap<String, GroupGUIDInfoClass> toBeRemovedMap;
	private HashMap<String, GroupGUIDInfoClass> toBeAddedMap;
	
	private final Object repliesLock 											= new Object();
	
	private UpdateStats updateStat;
	
	public UpdateInfo( ValueUpdateFromGNS valUpdMsgFromGNS, long updateRequestId )
	{
		this.valUpdMsgFromGNS = valUpdMsgFromGNS;
		this.updateRequestId  = updateRequestId;
	}
	
	public long getRequestId()
	{
		return updateRequestId;
	}
	
	public ValueUpdateFromGNS getValueUpdateFromGNS()
	{
		return this.valUpdMsgFromGNS;
	}
	
	public boolean getUpdComl()
	{
		return this.updateReqCompl;
	}
	
	public void  setUpdCompl()
	{
		this.updateReqCompl = true;
	}
	
	public boolean setUpdateReply( JSONArray toBeRemovedGroups, JSONArray toBeAddedGroups )
	{
		assert( toBeRemovedGroups != null );
		assert( toBeAddedGroups != null );
		return false;
	}
	
	public boolean checkAllUpdateReplyRecvd()
	{
		return false;
	}
	
	public HashMap<String, GroupGUIDInfoClass> getToBeRemovedMap()
	{
		return this.toBeRemovedMap;
	}
	
	public HashMap<String, GroupGUIDInfoClass> getToBeAddedMap()
	{
		return this.toBeAddedMap;
	}
	
	public void setNumberOfExpectedReplies(int numberOfExpectedReplies)
	{
		totalExpectedValueUpdateReplies = numberOfExpectedReplies;
	}
	
	public UpdateStats getUpdateStats()
	{
		return this.updateStat;
	}
}