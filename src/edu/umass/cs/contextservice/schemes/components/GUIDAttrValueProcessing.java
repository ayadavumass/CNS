package edu.umass.cs.contextservice.schemes.components;


import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;

import edu.umass.cs.contextservice.database.AbstractDataStorageDB;
import edu.umass.cs.contextservice.messages.QueryMesgToSubspaceRegion;
import edu.umass.cs.contextservice.messages.QueryMesgToSubspaceRegionReply;
import edu.umass.cs.contextservice.profilers.CNSProfiler;
import edu.umass.cs.contextservice.queryparsing.QueryInfo;
import edu.umass.cs.contextservice.regionmapper.AbstractRegionMappingPolicy;
import edu.umass.cs.contextservice.updates.UpdateInfo;
import edu.umass.cs.nio.JSONMessenger;

public class GUIDAttrValueProcessing
								extends AbstractGUIDAttrValueProcessing 
{
	public GUIDAttrValueProcessing( Integer myID, 
			AbstractRegionMappingPolicy regionMappingPolicy, 
			AbstractDataStorageDB hyperspaceDB, 
		JSONMessenger<Integer> messenger , 
		ConcurrentHashMap<Long, QueryInfo> pendingQueryRequests, 
		CNSProfiler profStats )
	{
		super(myID, regionMappingPolicy, 
				hyperspaceDB, messenger , 
				pendingQueryRequests,  profStats);
	}
	
	public void processQueryMsgFromUser
		( QueryInfo queryInfo, boolean storeQueryForTrigger )
	{
	}
	
	public int processQueryMesgToSubspaceRegion
					(QueryMesgToSubspaceRegion queryMesgToSubspaceRegion, 
							JSONArray resultGUIDs)
	{	
		return -1;
	}
	
	public void processQueryMesgToSubspaceRegionReply(
			QueryMesgToSubspaceRegionReply 
									queryMesgToSubspaceRegionReply)
	{
	}
	
	
	/**
	 * This function processes a request serially.
	 * when one outstanding request completes.
	 */
	public void processUpdateFromGNS(UpdateInfo updateReq)
	{
	}
}