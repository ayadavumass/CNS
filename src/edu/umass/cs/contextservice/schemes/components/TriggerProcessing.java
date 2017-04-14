package edu.umass.cs.contextservice.schemes.components;


import java.util.HashMap;

import org.json.JSONException;

import edu.umass.cs.contextservice.database.AbstractDataStorageDB;
import edu.umass.cs.contextservice.database.triggers.GroupGUIDInfoClass;
import edu.umass.cs.contextservice.messages.QueryMesgToSubspaceRegion;
import edu.umass.cs.contextservice.messages.ValueUpdateToSubspaceRegionMessage;
import edu.umass.cs.contextservice.queryparsing.QueryInfo;
import edu.umass.cs.contextservice.regionmapper.AbstractRegionMappingPolicy;
import edu.umass.cs.nio.JSONMessenger;

/**
 * Implements trigger processing interface.
 * Implements hyperspace trigger processing interface.
 * @author adipc
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class TriggerProcessing implements 
								TriggerProcessingInterface
{
	private final AbstractDataStorageDB regionMappingDataStorageDB;
	
	private final Integer myID;
	
	private final JSONMessenger<Integer> messenger;
	
	public TriggerProcessing(Integer myID, 
				AbstractRegionMappingPolicy regionMappingPolicy, 
						AbstractDataStorageDB regionMappingDataStorageDB, 
						JSONMessenger<Integer> messenger )
	{
		this.myID = myID;
		this.messenger = messenger;
		this.regionMappingDataStorageDB = regionMappingDataStorageDB;

		new Thread( new DeleteExpiredSearchesThread( regionMappingDataStorageDB) ).start();
	}
	
	public boolean processTriggerOnQueryMsgFromUser( QueryInfo currReq)
	{
		boolean found = false;	
		return found;
	}
	
	public void processQuerySubspaceRegionMessageForTrigger
				( QueryMesgToSubspaceRegion queryMesgToSubspaceRegion )
	{
	}
	
	public void processTriggerForValueUpdateToSubspaceRegion
		( ValueUpdateToSubspaceRegionMessage 
		valueUpdateToSubspaceRegionMessage, HashMap<String, GroupGUIDInfoClass> removedGroups, 
		HashMap<String, GroupGUIDInfoClass> addedGroups ) throws InterruptedException
	{
	}
	
	public void sendOutAggregatedRefreshTrigger
				( HashMap<String, GroupGUIDInfoClass> removedGroups, 
				HashMap<String, GroupGUIDInfoClass> addedGroups, String updateGUID, 
				long versionNum,
				long updateStartTime) throws JSONException
	{
		
	}
}