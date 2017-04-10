package edu.umass.cs.contextservice.updates;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import edu.umass.cs.contextservice.config.ContextServiceConfig;

import edu.umass.cs.contextservice.database.triggers.GroupGUIDInfoClass;
import edu.umass.cs.contextservice.messages.ValueUpdateFromGNS;
import edu.umass.cs.contextservice.profilers.UpdateStats;

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
		
		updateReqCompl = false;
		
		if( ContextServiceConfig.triggerEnabled )
		{
			toBeRemovedMap = new HashMap<String, GroupGUIDInfoClass>();
			toBeAddedMap = new HashMap<String, GroupGUIDInfoClass>();
		}
		
		if(ContextServiceConfig.PROFILER_ENABLED)
		{
			updateStat = new UpdateStats();
		}
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
		
		synchronized( this.repliesLock )
		{
			if( ContextServiceConfig.triggerEnabled )
			{
				for( int i=0; i<toBeRemovedGroups.length(); i++ )
				{
					try 
					{
						GroupGUIDInfoClass groupGUIDInfo 
								= new GroupGUIDInfoClass(toBeRemovedGroups.getJSONObject(i));
						
						String groupGUID = groupGUIDInfo.getGroupGUID();
						
						// doing duplicate elimination right here.
						// as a query can span multiple nodes in a subspace.
						toBeRemovedMap.put(groupGUID, groupGUIDInfo);
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
				
				
				for( int i=0; i<toBeAddedGroups.length(); i++ )
				{
					GroupGUIDInfoClass groupGUIDInfo;
					try 
					{
						groupGUIDInfo 
							= new GroupGUIDInfoClass(toBeAddedGroups.getJSONObject(i));
						
						String groupGUID = groupGUIDInfo.getGroupGUID();
						
						// doing duplicate elimination right here.
						// as a query can span multiple nodes in a subspace.
						toBeAddedMap.put(groupGUID, groupGUIDInfo);
					} catch (JSONException e) 
					{
						e.printStackTrace();
					}
				}
			}
			valueUpdateRepliesCounter++;
			
			if( valueUpdateRepliesCounter == this.totalExpectedValueUpdateReplies )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	public boolean checkAllUpdateReplyRecvd()
	{
		synchronized(this.repliesLock)
		{
			if( valueUpdateRepliesCounter == this.totalExpectedValueUpdateReplies )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
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