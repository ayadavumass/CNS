package edu.umass.cs.contextservice.schemes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import com.google.common.hash.Hashing;

import edu.umass.cs.contextservice.attributeInfo.AttributeTypes;
import edu.umass.cs.contextservice.config.ContextServiceConfig;
import edu.umass.cs.contextservice.config.ContextServiceConfig.PrivacySchemes;
import edu.umass.cs.contextservice.database.HyperspaceDB;
import edu.umass.cs.contextservice.database.QueryAllDB;
import edu.umass.cs.contextservice.gns.GNSCalls;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.contextservice.messages.ClientConfigReply;
import edu.umass.cs.contextservice.messages.ClientConfigRequest;
import edu.umass.cs.contextservice.messages.ContextServicePacket.PacketType;
import edu.umass.cs.contextservice.messages.GetMessage;
import edu.umass.cs.contextservice.messages.GetReplyMessage;
import edu.umass.cs.contextservice.messages.QueryMesgToSubspaceRegion;
import edu.umass.cs.contextservice.messages.QueryMesgToSubspaceRegionReply;
import edu.umass.cs.contextservice.messages.QueryMsgFromUser;
import edu.umass.cs.contextservice.messages.QueryMsgFromUserReply;
import edu.umass.cs.contextservice.messages.ValueUpdateFromGNS;
import edu.umass.cs.contextservice.messages.ValueUpdateFromGNSReply;
import edu.umass.cs.contextservice.queryparsing.QueryInfo;
import edu.umass.cs.contextservice.schemes.helperclasses.RegionInfoClass;
import edu.umass.cs.contextservice.schemes.helperclasses.SubspaceSearchReplyInfo;
import edu.umass.cs.contextservice.updates.GUIDUpdateInfo;
import edu.umass.cs.contextservice.updates.UpdateInfo;
import edu.umass.cs.nio.GenericMessagingTask;
import edu.umass.cs.nio.JSONMessenger;
import edu.umass.cs.nio.interfaces.NodeConfig;
import edu.umass.cs.protocoltask.ProtocolEvent;
import edu.umass.cs.protocoltask.ProtocolTask;

public class QueryAllScheme extends AbstractScheme
{
	private  QueryAllDB queryAllDB 								= null;
	
	//TODO: make the trigger handling part in separate interfaces and classes.
	// also the privacy stuff. 
	// this files is getting very big.
	private final ExecutorService nodeES;
	
	private HashMap<String, GUIDUpdateInfo> guidUpdateInfoMap			= null;
	
	private final Object pendingQueryLock											= new Object();
	private long queryIdCounter														= 0;
	public static final Logger log 													= ContextServiceLogger.getLogger();
	
	public QueryAllScheme(NodeConfig<Integer> nc,
			JSONMessenger<Integer> m) throws Exception
	{
		super(nc, m);
		
		nodeES = Executors.newFixedThreadPool(ContextServiceConfig.HYPERSPACE_THREAD_POOL_SIZE);
		
		guidUpdateInfoMap = new HashMap<String, GUIDUpdateInfo>();
		
		ContextServiceLogger.getLogger().fine("configure subspace completed");
		
		queryAllDB = new QueryAllDB(this.getMyID());
		
		ContextServiceLogger.getLogger().fine("HyperspaceMySQLDB completed");
	}
	
	//TODO not sure what is the overhead of synchronizig exectutor service
	// but as we are accessing same executor service from many threads, so may be good to synchronize
	@Override
	public GenericMessagingTask<Integer, ?>[] handleQueryMsgFromUser(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleValueUpdateFromGNS(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleQueryMesgToSubspaceRegion(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleQueryMesgToSubspaceRegionReply(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}

	@Override
	public GenericMessagingTask<Integer, ?>[] handleValueUpdateToSubspaceRegionMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleGetMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}

	@Override
	public GenericMessagingTask<Integer, ?>[] handleGetReplyMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleValueUpdateToSubspaceRegionReplyMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleQueryTriggerMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}

	@Override
	public GenericMessagingTask<Integer, ?>[] handleUpdateTriggerMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleUpdateTriggerReply(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleClientConfigRequest(ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleACLUpdateToSubspaceRegionMessage(
			ProtocolEvent<PacketType, String> event, 
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}

	@Override
	public GenericMessagingTask<Integer, ?>[] handleACLUpdateToSubspaceRegionReplyMessage(
			ProtocolEvent<PacketType, String> event, 
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		nodeES.execute(new HandleEventThread(event));
		return null;
	}
	
	@Override
	public Integer getConsistentHashingNodeID( String stringToHash , 
			Vector<Integer> listOfNodesToConsistentlyHash )
	{
		int numNodes = listOfNodesToConsistentlyHash.size();
		int mapIndex = Hashing.consistentHash(stringToHash.hashCode(), numNodes);
		return listOfNodesToConsistentlyHash.get(mapIndex);
	}
	
	private void processQueryMsgFromUser
				(QueryMsgFromUser queryMsgFromUser)
	{
		String query;
		
		
		query   = queryMsgFromUser.getQuery();
		
		ContextServiceLogger.getLogger().fine("QUERY RECVD: QUERY_MSG recvd query recvd "+query);
		
		// create the empty group in GNS
		String grpGUID = GNSCalls.createQueryGroup(query);
		
		if( grpGUID.length() <= 0 )
		{
			ContextServiceLogger.getLogger().fine
				("Query request failed at the recieving node "+queryMsgFromUser);
			return;
		}
		
		guidProcessingOfQueryMsgFromUser(queryMsgFromUser);
	}
	
	
	private QueryInfo guidProcessingOfQueryMsgFromUser
								(QueryMsgFromUser queryMsgFromUser)
	{
		String query;
		long userReqID;
		String userIP;
		int userPort;
		
		query   = queryMsgFromUser.getQuery();
		userReqID = queryMsgFromUser.getUserReqNum();
		userIP  = queryMsgFromUser.getSourceIP();
		userPort   = queryMsgFromUser.getSourcePort();
		
		ContextServiceLogger.getLogger().fine("QUERY RECVD: QUERY_MSG recvd query recvd "+query);
		
		// create the empty group in GNS
		String grpGUID = GNSCalls.createQueryGroup(query);
		
		if( grpGUID.length() <= 0 )
		{
			ContextServiceLogger.getLogger().fine
			("Query request failed at the recieving node "+queryMsgFromUser);
			return null;
		}
		
		
		//Vector<QueryComponent> qcomponents = QueryParser.parseQueryNew(query);
		//FIXME: for conflicting queries , need to be handled sometime
		/*Vector<QueryComponent> matchingQueryComponents = new Vector<QueryComponent>();
		int maxMatchingSubspaceNum = getMaxOverlapSubspace(qcomponents, matchingQueryComponents);
		ContextServiceLogger.getLogger().fine("userReqID "+userReqID+" maxMatchingSubspaceNum "+maxMatchingSubspaceNum+" matchingQueryComponents "
				+matchingQueryComponents.size()+" query "+query);
		
		// get number of nodes/or regions to send to in that subspace.
	    HashMap<Integer, OverlappingInfoClass> respNodeIdList 
	    		= this.hyperspaceDB.getOverlappingRegionsInSubspace(maxMatchingSubspaceNum, matchingQueryComponents);
	    
	    // query is conflicting, like same attribute has conflicting ranges
	    // in conjunction. 1 <= contextATT0 <= 5 && 10 <= contextATT0 <= 15,
	    // in current query patterns this query can be generated.
	    if( respNodeIdList.size() == 0 )
	    {
	    	QueryMsgFromUserReply<Integer> queryMsgFromUserReply = new QueryMsgFromUserReply<Integer>(this.getMyID(),
					query, grpGUID, new JSONArray(), userReqID, 0);
			try
			{
				this.messenger.sendToAddress(new InetSocketAddress( userIP, userPort), 
						queryMsgFromUserReply.toJSONObject() );
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			return;
	    }*/
		
		QueryInfo currReq  
			= new QueryInfo( query, this.getMyID(), grpGUID, userReqID, 
					userIP, userPort, queryMsgFromUser.getExpiryTime() );
		
		Vector<Integer> allNodeIDs = this.allNodeIDs;
		
		HashMap<Integer, RegionInfoClass> overlapInfoMap
							= new HashMap<Integer, RegionInfoClass>();
		for(int i=0; i<allNodeIDs.size(); i++)
		{
			int respNodeId = Integer.parseInt(allNodeIDs.get(i).toString());
			RegionInfoClass overlapObj = new RegionInfoClass();
	    	
	    	//overlapObj.hashCode = hashCode;
	    	overlapObj.respNodeId = respNodeId;
	    	overlapObj.replyArray = null;
	    	overlapInfoMap.put(respNodeId, overlapObj);
		}
		
		synchronized(this.pendingQueryLock)
		{
			currReq.setQueryRequestID(queryIdCounter++);
		}
		
		
		pendingQueryRequests.put(currReq.getRequestId(), currReq);
		
		HashMap<Integer, SubspaceSearchReplyInfo> searchQueryReplyInfo 
															= new HashMap<Integer, SubspaceSearchReplyInfo>();
		
		SubspaceSearchReplyInfo subspaceSearchInfo = new SubspaceSearchReplyInfo();
		subspaceSearchInfo.overlappingRegionsMap = overlapInfoMap;
		searchQueryReplyInfo.put(0, subspaceSearchInfo);
		
	    currReq.initializeSearchQueryReplyInfo(searchQueryReplyInfo);
		
	    Iterator<Integer> respNodeIdIter = overlapInfoMap.keySet().iterator();
	    
	    while( respNodeIdIter.hasNext() )
	    {
	    	Integer respNodeId = respNodeIdIter.next();
	    	
	    	QueryMesgToSubspaceRegion queryMesgToSubspaceRegion = 
					new QueryMesgToSubspaceRegion
	    			(this.getMyID(), currReq.getRequestId(), query, grpGUID, -1, userIP, userPort, 
	    						false, queryMsgFromUser.getExpiryTime(), 
	    						PrivacySchemes.NO_PRIVACY.ordinal());
	    	
			try
			{
				this.messenger.sendToID( (Integer)respNodeId, queryMesgToSubspaceRegion.toJSONObject() );
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			ContextServiceLogger.getLogger().info("Sending QueryMesgToSubspaceRegion mesg from " 
					+ this.getMyID() +" to node "+respNodeId);
	    }
	    return currReq;
	}
	
	private void processValueUpdateFromGNS( ValueUpdateFromGNS valueUpdateFromGNS )
	{
		String GUID 			  		= valueUpdateFromGNS.getGUID();
		Integer respNodeId 	  			= this.getConsistentHashingNodeID
													(GUID, this.allNodeIDs);
		
		// just forward the request to the node that has 
		// guid stored in primary subspace.
		if( this.getMyID() != respNodeId )
		{
			ContextServiceLogger.getLogger().fine("not primary node case souceIp "
													+valueUpdateFromGNS.getSourceIP()
													+" sourcePort "+valueUpdateFromGNS.getSourcePort());
			try
			{
				this.messenger.sendToID( respNodeId, valueUpdateFromGNS.toJSONObject() );
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			ContextServiceLogger.getLogger().fine("primary node case souceIp "
								+valueUpdateFromGNS.getSourceIP()
								+" sourcePort "+valueUpdateFromGNS.getSourcePort());
			
			UpdateInfo updReq  	= null;
			long requestID 					= -1;
			// if no outstanding request then it is set to true
			boolean sendOutRequest 			= false;
			
			synchronized( this.pendingUpdateLock )
			{
				updReq = new UpdateInfo(valueUpdateFromGNS, updateIdCounter++, 
						null);
				pendingUpdateRequests.put(updReq.getRequestId(), updReq);
				requestID = updReq.getRequestId();
				
				GUIDUpdateInfo guidUpdateInfo = this.guidUpdateInfoMap.get(GUID);
				
				if(guidUpdateInfo == null)
				{
					guidUpdateInfo = new GUIDUpdateInfo(GUID);
					guidUpdateInfo.addUpdateReqNumToQueue(requestID);
					this.guidUpdateInfoMap.put(GUID, guidUpdateInfo);
					sendOutRequest = true;
				}
				else
				{
					guidUpdateInfo.addUpdateReqNumToQueue(requestID);
					// no need to send out request. it will be sent once the current
					// outstanding gets completed
				}
			}
			
			if( sendOutRequest )
			{
				processUpdateSerially(updReq);
			}
		}
	}
	
	/**
	 * This function processes a request serially.
	 * when one outstanding request completes.
	 */
	private void processUpdateSerially(UpdateInfo updateReq)
	{
		assert( updateReq != null );
		try
		{
			ContextServiceLogger.getLogger().fine
					( "processUpdateSerially called "+updateReq.getRequestId() +
					" JSON"+updateReq.getValueUpdateFromGNS().toJSONObject().toString() );
		}
		catch(JSONException jso)
		{
			jso.printStackTrace();
		}
		
		String GUID 	 		= updateReq.getValueUpdateFromGNS().getGUID();
		JSONObject attrValuePairs 
						 		= updateReq.getValueUpdateFromGNS().getAttrValuePairs();
		long requestID 	 		= updateReq.getRequestId();
		long updateStartTime	= updateReq.getValueUpdateFromGNS().getUpdateStartTime();
		
		
		// get the old value and process the update in primary subspace and other subspaces.
		String tableName = "primarySubspaceDataStorage";
		
		try
		{
			long start 	 = System.currentTimeMillis();
			// FIXME: fetch only those attributes which are specified in the updated attrs.
			JSONObject oldValueJSON 	
						 = this.queryAllDB.getGUIDStoredInPrimarySubspace(GUID);
			
			long end 	 = System.currentTimeMillis();
			
			if(ContextServiceConfig.DEBUG_MODE)
			{
				System.out.println("getGUIDStoredInPrimarySubspace time "+(end-start)
							+" since upd start"+(end-updateStartTime));
			}
			
			int updateOrInsert 			= -1;
			
			if( oldValueJSON.length() == 0 )
			{
				updateOrInsert = HyperspaceDB.INSERT_REC;
			}
			else
			{
				updateOrInsert = HyperspaceDB.UPDATE_REC;
			}
			
			// default values are set for all attributes for hyperspace indexing.
			//setDefaultAttrValuesInJSON(oldValueJSON);
			
			// sending null means anonymizedIDToGuidMapping will not be inserted again.
			this.queryAllDB.storeGUIDInPrimarySubspace
			( tableName, GUID, attrValuePairs, updateOrInsert);
			
			if(ContextServiceConfig.DEBUG_MODE)
			{
				long now = System.currentTimeMillis();
				System.out.println("primary subspace update complete "+(now-updateStartTime));
			}
			
			ValueUpdateFromGNSReply valueUpdateFromGNSReply
				= new ValueUpdateFromGNSReply
				(this.getMyID(), updateReq.getValueUpdateFromGNS().getVersionNum(), 
						updateReq.getValueUpdateFromGNS().getUserRequestID());
			
			ContextServiceLogger.getLogger().fine("reply IP Port "
					+updateReq.getValueUpdateFromGNS().getSourceIP()
					+":"+updateReq.getValueUpdateFromGNS().getSourcePort()
					+ " ValueUpdateFromGNSReply for requestId "+requestID);
			try
			{
				this.messenger.sendToAddress( new InetSocketAddress
						(updateReq.getValueUpdateFromGNS().getSourceIP()
						, updateReq.getValueUpdateFromGNS().getSourcePort()), 
						valueUpdateFromGNSReply.toJSONObject() );
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			
			
			UpdateInfo removedUpdate = pendingUpdateRequests.remove(requestID);
			
			// starts the queues serialized updates for that guid
			// null is checked becuase it can also be remove on
			// update completion. So only one can start the new update
			if( removedUpdate != null )
			{
				startANewUpdate(removedUpdate, requestID);
			}
			
			
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}
	}
	
	private void processGetMessage(GetMessage getMessage)
	{
		String GUID 			  = getMessage.getGUIDsToGet();
		Integer respNodeId 	  = this.getConsistentHashingNodeID(GUID, this.allNodeIDs);
		
		// just forward the request to the node that has 
		// guid stored in primary subspace.
		if( this.getMyID() != respNodeId )
		{
			try
			{
				this.messenger.sendToID( respNodeId, getMessage.toJSONObject() );
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			//String tableName = "primarySubspaceDataStorage";
			JSONObject valueJSON= this.queryAllDB.getGUIDStoredInPrimarySubspace(GUID);
			
			
			GetReplyMessage getReplyMessage = new GetReplyMessage(this.getMyID(),
					getMessage.getUserReqID(), GUID, valueJSON);
			
			try
			{
				this.messenger.sendToAddress( new InetSocketAddress(getMessage.getSourceIP(), getMessage.getSourcePort()), 
						getReplyMessage.toJSONObject() );
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void processQueryMesgToSubspaceRegionReply
		( QueryMesgToSubspaceRegionReply queryMesgToSubspaceRegionReply )
	{
		Integer senderID = queryMesgToSubspaceRegionReply.getSender();
		long requestId = queryMesgToSubspaceRegionReply.getRequestId();

		QueryInfo queryInfo = pendingQueryRequests.get(requestId);

		boolean allRepRecvd = 
				queryInfo.addReplyFromARegionOfASubspace(0,(Integer)senderID, queryMesgToSubspaceRegionReply);

		if( allRepRecvd )
		{
			JSONArray concatResult 							 = new JSONArray();
			
			int totalNumReplies 							 = 0;
			
			if( ContextServiceConfig.sendFullRepliesWithinCS )
			{
				HashMap<Integer, RegionInfoClass> repliesHashMap 
					= queryInfo.getSearchReplyMap().get(0).overlappingRegionsMap;

				Iterator<Integer> nodeIdIter 				 = repliesHashMap.keySet().iterator();

				while( nodeIdIter.hasNext() )
				{
					RegionInfoClass regInfo = repliesHashMap.get(nodeIdIter.next());
					concatResult.put(regInfo.replyArray);
					totalNumReplies = totalNumReplies + regInfo.replyArray.length();
				}
			}
			else
			{
				HashMap<Integer, RegionInfoClass> repliesHashMap  
								= queryInfo.getSearchReplyMap().get(0).overlappingRegionsMap;
				Iterator<Integer> nodeIdIter = repliesHashMap.keySet().iterator();

				while( nodeIdIter.hasNext() )
				{
					RegionInfoClass regInfo = repliesHashMap.get(nodeIdIter.next());
					int currRepSize = regInfo.numReplies;
					totalNumReplies = totalNumReplies + currRepSize;
					//concatResult.put(currArray);
				}
			}
			
			
			QueryMsgFromUserReply queryMsgFromUserReply 
				= new QueryMsgFromUserReply(this.getMyID(), 
						queryInfo.getQuery(), queryInfo.getGroupGUID(), concatResult, 
						queryInfo.getUserReqID(), totalNumReplies, PrivacySchemes.NO_PRIVACY.ordinal());
			try
			{
				this.messenger.sendToAddress(new InetSocketAddress(queryInfo.getUserIP(), 
						queryInfo.getUserPort()), queryMsgFromUserReply.toJSONObject());
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			ContextServiceLogger.getLogger().info("Sending queryMsgFromUserReply mesg from " 
					+ this.getMyID() +" to node "+new InetSocketAddress(queryInfo.getUserIP(), queryInfo.getUserPort()));

			pendingQueryRequests.remove(requestId);
		}
	}
	
	
	private void startANewUpdate(UpdateInfo removedUpdate, long requestID)
	{
		boolean startANewUpdate = false;
		Long nextRequestID = null;
		synchronized( this.pendingUpdateLock )
		{
			// remove from guidUpdateInfo
			GUIDUpdateInfo guidUpdateInfo = 
					this.guidUpdateInfoMap.get(removedUpdate.getValueUpdateFromGNS().getGUID());
			
			assert(guidUpdateInfo!=null);
			Long currRequestID = guidUpdateInfo.removeFromQueue();
			// it must not be null
			assert(currRequestID != null);
			// it should be same as current requestID
			assert(requestID == currRequestID);
			
			// get the next requestID
			nextRequestID = guidUpdateInfo.getNextRequestID();
			if(nextRequestID == null)
			{
				// remove the guidUpdateInfo, there are no more updates for this GUID
				this.guidUpdateInfoMap.remove(removedUpdate.getValueUpdateFromGNS().getGUID());
			}
			else
			{
				// start a new update serially outside the lock
				startANewUpdate = true;
			}
		}
		
		if(startANewUpdate)
		{
			assert(nextRequestID != null);
			this.processUpdateSerially(pendingUpdateRequests.get(nextRequestID));
		}
	}
	
	public void processQueryMesgToSubspaceRegion(QueryMesgToSubspaceRegion 
	queryMesgToSubspaceRegion)
	{
		String query 			= queryMesgToSubspaceRegion.getQuery();
		String groupGUID 		= queryMesgToSubspaceRegion.getGroupGUID();
		JSONArray resultGUIDs = new JSONArray();
		
		int resultSize = this.queryAllDB.processSearchQueryInSubspaceRegion
													(query, resultGUIDs);
		
		QueryMesgToSubspaceRegionReply queryMesgToSubspaceRegionReply = 
		new QueryMesgToSubspaceRegionReply( this.getMyID(), 
				queryMesgToSubspaceRegion.getRequestId(), 
						groupGUID, resultGUIDs, resultSize, 
						PrivacySchemes.NO_PRIVACY.ordinal(), 0);
		
		try
		{
			this.messenger.sendToID(queryMesgToSubspaceRegion.getSender(), queryMesgToSubspaceRegionReply.toJSONObject());
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		ContextServiceLogger.getLogger().info("Sending queryMesgToSubspaceRegionReply mesg from " 
												+ this.getMyID() +" to node "+queryMesgToSubspaceRegion.getSender());
	}
	
	private void processClientConfigRequest(ClientConfigRequest clientConfigRequest)
	{
		JSONArray nodeConfigArray 		= new JSONArray();
		JSONArray attributeArray  		= new JSONArray();
		// Each element is a JSONArray of attrbutes for a subspace
		JSONArray subspaceConfigArray   = new JSONArray();
		
		Iterator<Integer> nodeIDIter = this.allNodeIDs.iterator();
		
		while( nodeIDIter.hasNext() )
		{
			Integer nodeId = nodeIDIter.next();
			InetAddress nodeAddress = this.messenger.getNodeConfig().getNodeAddress(nodeId);
			int nodePort = this.messenger.getNodeConfig().getNodePort(nodeId);
			String ipPortString = nodeAddress.getHostAddress()+":"+nodePort;
			nodeConfigArray.put(ipPortString);
		}
		
		Iterator<String> attrIter = AttributeTypes.attributeMap.keySet().iterator();
		
		while(attrIter.hasNext())
		{
			String attrName = attrIter.next();
			attributeArray.put(attrName);
		}
		
		InetSocketAddress sourceSocketAddr = new InetSocketAddress(clientConfigRequest.getSourceIP(),
				clientConfigRequest.getSourcePort());
		ClientConfigReply configReply 
					= new ClientConfigReply( this.getMyID(), nodeConfigArray,
							attributeArray, subspaceConfigArray );
		try
		{
			this.messenger.sendToAddress( sourceSocketAddr, configReply.toJSONObject() );
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	
	private class HandleEventThread implements Runnable
	{
		private final ProtocolEvent<PacketType, String> event;
		
		public HandleEventThread(ProtocolEvent<PacketType, String> event)
		{
			this.event = event;
		}
		
		@Override
		public void run()
		{
			// this try catch is very important.
			// otherwise exception from these methods are not at all printed by executor service
			// and debugging gets very time consuming
			try
			{
				switch(event.getType())
				{
					case  QUERY_MSG_FROM_USER:
					{
						QueryMsgFromUser queryMsgFromUser 
												= (QueryMsgFromUser)event;
						
						processQueryMsgFromUser(queryMsgFromUser);
						
						break;
					}
					case QUERY_MESG_TO_SUBSPACE_REGION:
					{
						//long t0 = System.currentTimeMillis();
						QueryMesgToSubspaceRegion queryMesgToSubspaceRegion = 
								(QueryMesgToSubspaceRegion) event;
						
						log.fine("CS"+getMyID()+" received " + event.getType() + ": " + event);
						
						processQueryMesgToSubspaceRegion(queryMesgToSubspaceRegion);
						//processQueryMsgToMetadataNode(queryMsgToMetaNode);
						
						//DelayProfiler.updateDelay("handleQueryMsgToMetadataNode", t0);
						break;
					}
					case QUERY_MESG_TO_SUBSPACE_REGION_REPLY:
					{
						//long t0 = System.currentTimeMillis();
						QueryMesgToSubspaceRegionReply queryMesgToSubspaceRegionReply = 
								(QueryMesgToSubspaceRegionReply)event;
						
						log.fine("CS"+getMyID()+" received " + event.getType() + ": " + queryMesgToSubspaceRegionReply);
						
						processQueryMesgToSubspaceRegionReply(queryMesgToSubspaceRegionReply);
						
						//DelayProfiler.updateDelay("handleQueryMsgToValuenode", t0);
						break;
					}
					case VALUE_UPDATE_MSG_FROM_GNS:
					{
						//long t0 = System.currentTimeMillis();
						ValueUpdateFromGNS valUpdMsgFromGNS = (ValueUpdateFromGNS)event;
						//MSocketLogger.getLogger().fine("CS"+getMyID()+" received " + event.getType() + ": " + valUpdMsgFromGNS);
						ContextServiceLogger.getLogger().fine("CS"+getMyID()+" received " + event.getType() + ": " + valUpdMsgFromGNS);
						
						processValueUpdateFromGNS(valUpdMsgFromGNS);
						break;
					}
					case VALUEUPDATE_TO_SUBSPACE_REGION_MESSAGE:
					{
						assert(false);
						break;
					}
					case GET_MESSAGE:
					
					{
						GetMessage getMessage 
									= (GetMessage)event;
						//log.fine("CS"+getMyID()+" received " + event.getType() + ": " + valueUpdateToSubspaceRegionMessage);
						ContextServiceLogger.getLogger().fine("CS"+getMyID()+" received " + event.getType() + ": " 
										+ getMessage);
						
						processGetMessage(getMessage);
						break;
					}
					
					case VALUEUPDATE_TO_SUBSPACE_REGION_REPLY_MESSAGE:
					{
						assert(false);
						break;
					}
					
					case CONFIG_REQUEST:
					{
						ClientConfigRequest configRequest 
									= (ClientConfigRequest)event;
						
						ContextServiceLogger.getLogger().fine("CS"+getMyID()+" received " + event.getType() + ": " 
								+ configRequest);
						processClientConfigRequest(configRequest);
						break;
					}
					default:
					{
						assert(false);
						break;
					}
				}
			}
			catch (Exception | Error ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args)
	{
		double numPartitions = Math.ceil(Math.pow(16, 1.0/4));
		ContextServiceLogger.getLogger().fine("numPartitions "+numPartitions);
		
		double numAttr  = 5;
		//double numNodes = nodesOfSubspace.size();
		
		Integer[] partitionNumArray = new Integer[2];
		for( int j = 0; j<2; j++ )
		{
			partitionNumArray[j] = j;
			ContextServiceLogger.getLogger().fine("partitionNumArray[j] "+j+" "+partitionNumArray[j]);
		}
		
		// Create the initial vector of 2 elements (apple, orange)
		ICombinatoricsVector<Integer> originalVector = Factory.createVector(partitionNumArray);
		
	    //ICombinatoricsVector<Integer> originalVector = Factory.createVector(new String[] { "apple", "orange" });

		// Create the generator by calling the appropriate method in the Factory class. 
		// Set the second parameter as 3, since we will generate 3-elemets permutations
		Generator<Integer> gen = Factory.createPermutationWithRepetitionGenerator(originalVector, (int)numAttr);
		
		// Print the result
		for( ICombinatoricsVector<Integer> perm : gen )
		{
			ContextServiceLogger.getLogger().fine("perm.getVector() "+perm.getVector());
			ContextServiceLogger.getLogger().fine("hyperspaceDB."
					+ "insertIntoSubspacePartitionInfo complete");
		}
	}
}