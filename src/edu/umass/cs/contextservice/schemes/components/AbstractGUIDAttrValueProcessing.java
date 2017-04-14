package edu.umass.cs.contextservice.schemes.components;


import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;


import edu.umass.cs.contextservice.database.AbstractDataStorageDB;
import edu.umass.cs.contextservice.messages.QueryMesgToSubspaceRegion;
import edu.umass.cs.contextservice.messages.QueryMesgToSubspaceRegionReply;
import edu.umass.cs.contextservice.messages.ValueUpdateToSubspaceRegionMessage;
import edu.umass.cs.contextservice.profilers.CNSProfiler;
import edu.umass.cs.contextservice.queryparsing.QueryInfo;
import edu.umass.cs.contextservice.regionmapper.AbstractRegionMappingPolicy;
import edu.umass.cs.contextservice.updates.UpdateInfo;
import edu.umass.cs.nio.JSONMessenger;

public abstract class AbstractGUIDAttrValueProcessing
{	
	protected final AbstractRegionMappingPolicy regionMappingPolicy;
	protected final Random replicaChoosingRand;
	
	protected final Integer myID;
	
	protected final AbstractDataStorageDB hyperspaceDB;
	
	protected final JSONMessenger<Integer> messenger;
	
	protected final ConcurrentHashMap<Long, QueryInfo> pendingQueryRequests;
	
	protected final Object pendingQueryLock								= new Object();
	
	protected long queryIdCounter										= 0;
	
	protected final CNSProfiler profStats;
	
	protected final Random defaultAttrValGenerator;
	
	public AbstractGUIDAttrValueProcessing( Integer myID, 
			AbstractRegionMappingPolicy regionMappingPolicy, 
			AbstractDataStorageDB hyperspaceDB, JSONMessenger<Integer> messenger , 
		ConcurrentHashMap<Long, QueryInfo> pendingQueryRequests, 
		CNSProfiler profStats )
	{
		this.myID = myID;
		this.regionMappingPolicy = regionMappingPolicy;
		this.hyperspaceDB = hyperspaceDB;
		this.messenger = messenger;
		this.pendingQueryRequests = pendingQueryRequests;
		this.profStats = profStats;
		replicaChoosingRand = new Random(myID.hashCode());
		
		defaultAttrValGenerator = new Random(myID.hashCode());
	}
	
	
	public void processValueUpdateToSubspaceRegionMessage( 
			ValueUpdateToSubspaceRegionMessage valueUpdateToSubspaceRegionMessage )
	{
	}
	
	
	public abstract void processQueryMsgFromUser
		( QueryInfo queryInfo, boolean storeQueryForTrigger );
	
	public abstract void processQueryMesgToSubspaceRegionReply(QueryMesgToSubspaceRegionReply 
						queryMesgToSubspaceRegionReply);
	
	public abstract void processUpdateFromGNS( UpdateInfo updateReq );
	
	public abstract int processQueryMesgToSubspaceRegion(QueryMesgToSubspaceRegion 
														queryMesgToSubspaceRegion, JSONArray resultGUIDs);
}