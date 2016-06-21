package edu.umass.cs.contextservice.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.client.common.ACLEntry;
import edu.umass.cs.contextservice.client.anonymizedID.AnonymizedIDCreationInterface;
import edu.umass.cs.contextservice.client.anonymizedID.GUIDBasedAnonymizedIDCreator;
import edu.umass.cs.contextservice.client.anonymizedID.HyperspaceBasedAnonymizedIDCreator;
import edu.umass.cs.contextservice.client.callback.implementations.BlockingCallBack;
import edu.umass.cs.contextservice.client.callback.implementations.BlockingSearchReply;
import edu.umass.cs.contextservice.client.callback.implementations.BlockingUpdateReply;
import edu.umass.cs.contextservice.client.callback.implementations.PrivacyCallBack;
import edu.umass.cs.contextservice.client.callback.implementations.PrivacyUpdateReply;
import edu.umass.cs.contextservice.client.callback.implementations.SampleCallBack;
import edu.umass.cs.contextservice.client.callback.implementations.SampleSearchReply;
import edu.umass.cs.contextservice.client.callback.implementations.SampleUpdateReply;
import edu.umass.cs.contextservice.client.callback.interfaces.CallBackInterface;
import edu.umass.cs.contextservice.client.callback.interfaces.SearchReplyInterface;
import edu.umass.cs.contextservice.client.callback.interfaces.UpdateReplyInterface;
import edu.umass.cs.contextservice.client.common.AnonymizedIDEntry;
import edu.umass.cs.contextservice.client.csprivacytransform.CSPrivacyTransformInterface;
import edu.umass.cs.contextservice.client.csprivacytransform.CSSearchReplyTransformedMessage;
import edu.umass.cs.contextservice.client.csprivacytransform.CSUpdateTransformedMessage;
import edu.umass.cs.contextservice.client.csprivacytransform.HyperspaceBasedCSTransform;
import edu.umass.cs.contextservice.client.gnsprivacytransform.EncryptionBasedGNSPrivacyTransform;
import edu.umass.cs.contextservice.client.gnsprivacytransform.GNSPrivacyTransformInterface;
import edu.umass.cs.contextservice.client.gnsprivacytransform.GNSTransformedMessage;
import edu.umass.cs.contextservice.client.storage.GetStorage;
import edu.umass.cs.contextservice.client.storage.SearchQueryStorage;
import edu.umass.cs.contextservice.client.storage.UpdateStorage;
import edu.umass.cs.contextservice.config.ContextServiceConfig;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.contextservice.messages.ClientConfigReply;
import edu.umass.cs.contextservice.messages.ClientConfigRequest;
import edu.umass.cs.contextservice.messages.ContextServicePacket;
import edu.umass.cs.contextservice.messages.GetMessage;
import edu.umass.cs.contextservice.messages.GetReplyMessage;
import edu.umass.cs.contextservice.messages.QueryMsgFromUser;
import edu.umass.cs.contextservice.messages.QueryMsgFromUserReply;
import edu.umass.cs.contextservice.messages.RefreshTrigger;
import edu.umass.cs.contextservice.messages.ValueUpdateFromGNS;
import edu.umass.cs.contextservice.messages.ValueUpdateFromGNSReply;
import edu.umass.cs.contextservice.messages.dataformat.SearchReplyGUIDRepresentationJSON;
import edu.umass.cs.contextservice.utils.Utils;
import edu.umass.cs.gnsclient.client.GNSClientCommands;
import edu.umass.cs.gnsclient.client.GuidEntry;
import edu.umass.cs.gnscommon.exceptions.client.ClientException;

/**
 * ContextService client.
 * It is used to send and recv replies from context service.
 * It knows context service node addresses from a file in the conf folder.
 * It is thread safe, means same client can be used by multiple threads without any 
 * synchronization problems.
 * @author adipc
 * @param <NodeIDType>
 */
public class ContextServiceClient<NodeIDType> extends AbstractContextServiceClient<NodeIDType> 
						implements ContextClientInterfaceWithPrivacy, ContextServiceClientInterfaceWithoutPrivacy
{
	public static final int SUBSPACE_BASED_CS_TRANSFORM				= 1;
	public static final int HYPERSPACE_BASED_CS_TRANSFORM			= 2;
	public static final int GUID_BASED_CS_TRANSFORM					= 3;
	
	
	private Queue<JSONObject> refreshTriggerQueue;
	
	private final Object refreshTriggerClientWaitLock 				= new Object();
	
	private final GNSClientCommands gnsClient;
	
	// for anonymized ID
	private AnonymizedIDCreationInterface anonymizedIDCreation;
	
	//gns transform
	private GNSPrivacyTransformInterface gnsPrivacyTransform;
	
	// for cs transform
	private CSPrivacyTransformInterface csPrivacyTransform;
	
	private PrivacyCallBack privacyCallBack;
	
	private BlockingCallBack blockingCallBack;
	
	private long blockingReqID 										= 0;
	private final Object blockingReqIDLock 							= new Object();
	
	
	// indicates the transform type.
	private final int transformType;
	/**
	 * Use this constructor if you want to directly communicate with CS, bypassing GNS.
	 * @param csHostName
	 * @param csPortNum
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	public ContextServiceClient(String csHostName, int csPortNum, int transformType)
			throws IOException, NoSuchAlgorithmException
	{
		super( csHostName, csPortNum );
		this.transformType = transformType;
		gnsClient = null;
		privacyCallBack = new PrivacyCallBack();
		blockingCallBack = new BlockingCallBack();
		//executorService = Executors.newCachedThreadPool();
		initializeClient();
	}
	
	/**
	 * Use this constructor when CS and GNS are used.
	 * @param csHostName
	 * @param csPortNum
	 * @param gnsHostName
	 * @param gnsPort
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	public ContextServiceClient( String csHostName, int csPortNum, 
			String gnsHostName, int gnsPort, int transformType ) 
			throws IOException, NoSuchAlgorithmException
	{
		super( csHostName, csPortNum );
		
		// just setting some gns properties.
		Properties props = System.getProperties();
		props.setProperty("gigapaxosConfig", "conf/gnsClientConf/gigapaxos.client.local.properties");
		props.setProperty("javax.net.ssl.trustStorePassword", "qwerty");
		props.setProperty("javax.net.ssl.trustStore", "conf/gnsClientConf/trustStore/node100.jks");
		props.setProperty("javax.net.ssl.keyStorePassword", "qwerty");
		props.setProperty("javax.net.ssl.keyStore", "conf/gnsClientConf/keyStore/node100.jks");
		
		privacyCallBack = new PrivacyCallBack();
		blockingCallBack = new BlockingCallBack();
		
		this.transformType = transformType;
		
		gnsClient = new GNSClientCommands();
		
		initializeClient();
	}
	
//	public void sendUpdate( String GUID, GuidEntry myGuidEntry, JSONObject gnsAttrValuePairs, 
//			long versionNum)
//	{
//		//Note: gnsAttrValuePairs, key is attrName, value is attrValue
//		ContextServiceLogger.getLogger().fine("ContextClient sendUpdate enter "+GUID+" json "+
//				gnsAttrValuePairs);
//		try
//		{
//			if(gnsClient != null)
//			{
//				sendUpdateToGNS(myGuidEntry, gnsAttrValuePairs);
//			}
//			
//			JSONObject csAttrValuePairs 
//						= filterCSAttributes(gnsAttrValuePairs);
//			
//			// no context service attribute matching.
//			if( csAttrValuePairs.length() <= 0 )
//			{
//				return;
//			}
//			sendUpdateToCS(GUID, 
//					csAttrValuePairs, null, versionNum, blocking );
//		}
//		catch (Exception | Error e)
//		{
//			e.printStackTrace();
//		}
//		// no waiting in update
//	}
	
	public void sendUpdateWithCallBack
		( String GUID, GuidEntry myGuidEntry, JSONObject gnsAttrValuePairs, 
		long versionNum, UpdateReplyInterface updReplyObj, CallBackInterface callback )
	{
		//Note: gnsAttrValuePairs, key is attrName, value is attrValue
		ContextServiceLogger.getLogger().fine("ContextClient sendUpdate enter "+GUID
				+" json "+gnsAttrValuePairs);
		
		try
		{	
			if(gnsClient != null)
			{
				sendUpdateToGNS(myGuidEntry, gnsAttrValuePairs);
			}
			
			JSONObject csAttrValuePairs 
						= filterCSAttributes(gnsAttrValuePairs);
			
			// no context service attribute matching.
			if( csAttrValuePairs.length() <= 0 )
			{
				return;
			}
			sendUpdateToCS(GUID, 
					csAttrValuePairs, null, versionNum, updReplyObj, callback );
		}
		catch ( Exception | Error e )
		{
			e.printStackTrace();
		}
		// no waiting in update	
	}
	
	public void sendSearchQueryWithCallBack(String searchQuery, 
			long expiryTime, SearchReplyInterface searchRep, CallBackInterface callback)
	{
//		if( searchRep.getSearchReplyArray() == null )
//		{
//			ContextServiceLogger.getLogger().warning("null passed "
//					+ "as replyArray in sendSearchQuery");
//			return -1;
//		}
		sendSearchQueryToCS(searchQuery, expiryTime, 
				searchRep, callback);
	}
	
	public JSONObject sendGetRequest(String GUID)
	{
		long currId;
		synchronized(this.getIdLock)
		{
			currId = this.getReqId++;
		}
		
		GetMessage<NodeIDType> getmesgU 
			= new GetMessage<NodeIDType>(this.nodeid, currId, GUID, sourceIP, sourcePort);
		
		GetStorage<NodeIDType> getQ = new GetStorage<NodeIDType>();
		getQ.requestID = currId;
		getQ.getMessage = getmesgU;
		getQ.getReplyMessage = null;
		
		this.pendingGet.put(currId, getQ);
		InetSocketAddress sockAddr = this.csNodeAddresses.get(rand.nextInt(csNodeAddresses.size()));
		//ContextServiceLogger.getLogger().fine("Sending query to "+sockAddr);
		
		try 
		{
			niot.sendToAddress(sockAddr, getmesgU.toJSONObject());
		} catch (IOException e) 
		{
			e.printStackTrace();
		} catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		synchronized( getQ )
		{
			while( getQ.getReplyMessage == null )
			{
				try
				{
					getQ.wait();
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		JSONObject result = pendingGet.get(currId).getReplyMessage.getGUIDObject();
		pendingGet.remove(currId);
		return result;
	}
	
	/**
	 * Blocking call to return the current triggers.
	 * This call is also thread safe, only one thread will be notified though.
	 */
	public void getQueryUpdateTriggers(JSONArray triggerArray)
	{
		if(triggerArray == null)
		{
			assert(false);
			return;
		}
		
		synchronized( refreshTriggerClientWaitLock )
		{
			while( refreshTriggerQueue.size() == 0 )
			{
				try
				{
					refreshTriggerClientWaitLock.wait();
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			
			while( !refreshTriggerQueue.isEmpty() )
			{
				triggerArray.put(refreshTriggerQueue.poll());
			}
		}
	}
	
	public void sendUpdateSecureWithCallback( String GUID, GuidEntry myGUIDInfo, 
			JSONObject attrValuePairs, long versionNum, 
			HashMap<String, List<ACLEntry>> aclmap, List<AnonymizedIDEntry> anonymizedIDList,
			UpdateReplyInterface updReplyObj, CallBackInterface callback )
	{
		try
		{
			if(gnsClient != null)
			{
				GNSTransformedMessage gnsTransformMesg = 
						this.gnsPrivacyTransform.transformUpdateForGNSPrivacy
						(attrValuePairs, aclmap);
				
				sendUpdateToGNS( myGUIDInfo, 
						gnsTransformMesg.getEncryptedAttrValuePair() );
			}
			
			JSONObject csAttrValuePairs = filterCSAttributes(attrValuePairs);
			// no context service attribute matching.
			if( csAttrValuePairs.length() <= 0 )
			{
				return;
			}
			
			long start1 = System.currentTimeMillis();
			
			List<CSUpdateTransformedMessage> transformedMesgList 
				= this.csPrivacyTransform.transformUpdateForCSPrivacy
				(myGUIDInfo.getGuid(), attrValuePairs, aclmap, anonymizedIDList);
			
			long end1 = System.currentTimeMillis();
			
			assert(transformedMesgList.size() > 0);
			
			long currId;
			
			synchronized( this.privacyUpdateIdLock )
			{
				currId = this.privacyReqId++;
			}
			
			UpdateReplyInterface privacyUpdRep 
					= new PrivacyUpdateReply( currId, updReplyObj, callback, 
							transformedMesgList.size() );
			
//			PrivacyUpdateReplyTracker privacyUpdRepTracker 
//				= new PrivacyUpdateReplyTracker( updReplyObj, 
//						callback, transformedMesgList.size() );
			
			//privacyCallBack.addUpdateReply(currId, privacyUpdRepTracker);
			
			// now all the anonymized IDs and the attributes that needs to be updated
			// are calculated, 
			// just need to send out updates
			
			//List<Long> finishTimeList = new LinkedList<Long>();
			
//			ParallelUpdateStateStorage updateState 
//								= new ParallelUpdateStateStorage(transformedMesgList, 
//										finishTimeList);
			
			for( int i=0; i<transformedMesgList.size(); i++ )
			{
				CSUpdateTransformedMessage csTransformedMessage 
									= transformedMesgList.get(i);
			
//				UpdateOperationThread updateThread = new UpdateOperationThread(GUID, 
//						csTransformedMessage, versionNum, blocking, 
//						updateState );
//				this.executorService.execute(updateThread);
				
				String IDString 
					= Utils.bytArrayToHex(csTransformedMessage.getAnonymizedID());
				
				sendUpdateToCS( IDString, csTransformedMessage.getAttrValJSON(), 
						csTransformedMessage.getAnonymizedIDToGuidMapping(), 
						versionNum, privacyUpdRep, privacyCallBack );
			}
			
			long end2 = System.currentTimeMillis();
			
			if( transformedMesgList.size() > 0 )
			{
				System.out.println
				("sendUpdateSecure complete GUID "+GUID+" transform time "+(end1-start1)
					+" total time "+(end2-start1)+
					" length "+attrValuePairs.length()+" transformedMesgList size "
					+transformedMesgList.size());
			}
		}
		catch( JSONException jsoEx )
		{
			jsoEx.printStackTrace();
		}
	}
	
	public void sendSearchQuerySecureWithCallBack
		( String searchQuery, 
			long expiryTime, GuidEntry myGUIDInfo, 
			SearchReplyInterface searchRep, CallBackInterface callback )
	{
//		if( replyArray == null )
//		{
//			ContextServiceLogger.getLogger().warning("null passsed "
//					+ "as replyArray in sendSearchQuery");
//			return -1;
//		}
		
		//long start 		= System.currentTimeMillis();
		sendSearchQueryToCS(searchQuery, expiryTime, searchRep, callback);
		
//		List<CSSearchReplyTransformedMessage> searchRepTransformList 
//						= new LinkedList<CSSearchReplyTransformedMessage>();
//		
//		for(int i=0; i<searchAnswer.resultArray.length(); i++)
//		{
//			try
//			{
//				JSONArray jsoArr1 = searchAnswer.resultArray.getJSONArray(i);
//				for( int j=0; j<jsoArr1.length(); j++ )
//				{
//					JSONObject searchRepJSON = jsoArr1.getJSONObject(j);
//
//					if( ContextServiceConfig.DECRYPTIONS_ON_SEARCH_REPLY_ENABLED )
//					{
//						SearchReplyGUIDRepresentationJSON searchRepObj 
//						= SearchReplyGUIDRepresentationJSON.fromJSONObject(searchRepJSON);
//						CSSearchReplyTransformedMessage csSearchRepTransform 
//										= new CSSearchReplyTransformedMessage(searchRepObj);
//						searchRepTransformList.add(csSearchRepTransform);
//					}
//					else
//					{
//						// just adding the whole JSON here for the user to decrypt later on.
//						replyArray.put(searchRepJSON);
//					}
//				}
//			} catch ( JSONException e )
//			{
//				e.printStackTrace();
//			}
//		}
//		
//		long end1 = System.currentTimeMillis();
//		
//		if( ContextServiceConfig.DECRYPTIONS_ON_SEARCH_REPLY_ENABLED )
//		{
//			this.csPrivacyTransform.unTransformSearchReply( myGUIDInfo,
//				searchRepTransformList, replyArray );
//			long end2 = System.currentTimeMillis();
//			
//			System.out.println("SendSearchQuerySecure search reply from CS time "+ 
//						(end1-start)+" reply decryption time "+(end2-end1)+" fromCS reply size "
//						+ searchRepTransformList.size()+" final reply size "+replyArray.length() );
//			
//			return replyArray.length();
//		}
//		else
//		{
//			long end2 = System.currentTimeMillis();
//			
//			System.out.println("SendSearchQuerySecure search reply from CS time "+ 
//						(end1-start)+" reply decryption time "+(end2-end1)+" fromCS reply size "
//						+ searchRepTransformList.size()+" final reply size "+replyArray.length() );
//			
//			return searchAnswer.resultSize;
//		}
	}
	
	
	public JSONObject sendGetRequestSecure(String GUID, GuidEntry myGUIDInfo) throws Exception
	{
		// fetch it form GNS
		if(gnsClient != null)
		{
			JSONObject encryptedJSON = gnsClient.read(GUID, myGUIDInfo);
			GNSTransformedMessage gnsTransformedMesg 
							= new GNSTransformedMessage(encryptedJSON);
			
			return this.gnsPrivacyTransform.unTransformGetReply(gnsTransformedMesg, myGUIDInfo);
		}
		return null;
	}
	
	/**
	 * assumption is that ACL always fits in memory.
	 * @throws JSONException 
	 */
	public List<AnonymizedIDEntry> computeAnonymizedIDs( GuidEntry myGuidEntry,
			HashMap<String, List<ACLEntry>> aclMap ) throws JSONException
	{
		return this.anonymizedIDCreation.computeAnonymizedIDs(myGuidEntry, aclMap);
	}
	
	@Override
	public void sendUpdate(String GUID, GuidEntry myGuidEntry, 
			JSONObject attrValuePairs, long versionNum) 
	{
		//Note: gnsAttrValuePairs, key is attrName, value is attrValue
		ContextServiceLogger.getLogger().fine( "ContextClient sendUpdate enter "+GUID
				+" json "+attrValuePairs );
		
		try
		{
			if( gnsClient != null )
			{
				sendUpdateToGNS(myGuidEntry, attrValuePairs);
			}
			
			JSONObject csAttrValuePairs 
						= filterCSAttributes(attrValuePairs);
			
			// no context service attribute matching.
			if( csAttrValuePairs.length() <= 0 )
			{
				return;
			}
			
			long currReqId;
			
			synchronized( this.blockingReqIDLock )
			{
				currReqId = this.blockingReqID++;
			}
			
			BlockingUpdateReply blockingUpd = new BlockingUpdateReply(currReqId);
			sendUpdateToCS(GUID, 
					csAttrValuePairs, null, versionNum, blockingUpd, blockingCallBack );
			
			blockingUpd.waitForCompletion();
		}
		catch ( Exception | Error e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public int sendSearchQuery(String searchQuery, JSONArray replyArray, long expiryTime) 
	{	
		if( replyArray == null )
		{
			ContextServiceLogger.getLogger().warning("null passsed "
					+ "as replyArray in sendSearchQuery");
			return -1;
		}
		
		long currBlockingId;
		synchronized( this.blockingReqIDLock )
		{
			currBlockingId = this.blockingReqID++;
		}
		
		BlockingSearchReply blockingSearch = new BlockingSearchReply(currBlockingId);	
		
		sendSearchQueryToCS(searchQuery, expiryTime, 
				blockingSearch, this.blockingCallBack);
		
		blockingSearch.waitForCompletion();
		
		assert(blockingSearch.getSearchReplyArray() != null);
		for(int i=0; i<blockingSearch.getSearchReplyArray().length(); i++)
		{
			try
			{
				JSONArray jsoArr1 = blockingSearch.getSearchReplyArray().getJSONArray(i);
				for(int j=0; j<jsoArr1.length(); j++)
				{
					JSONObject searchRepJSON = jsoArr1.getJSONObject(j);
					SearchReplyGUIDRepresentationJSON searchRepObj 
							= SearchReplyGUIDRepresentationJSON.fromJSONObject(searchRepJSON);
					replyArray.put(searchRepObj.getID());
				}
			} catch ( JSONException e )
			{
				e.printStackTrace();
			}
		}
		return blockingSearch.getReplySize();		
	}

	@Override
	public void sendUpdateSecure(String GUID, GuidEntry myGUIDInfo, 
			JSONObject attrValuePairs, long versionNum,
			HashMap<String, List<ACLEntry>> aclmap, List<AnonymizedIDEntry> anonymizedIDList) 
	{
		try
		{
			if(gnsClient != null)
			{
				GNSTransformedMessage gnsTransformMesg = 
						this.gnsPrivacyTransform.transformUpdateForGNSPrivacy
						(attrValuePairs, aclmap);
				
				sendUpdateToGNS( myGUIDInfo, 
						gnsTransformMesg.getEncryptedAttrValuePair() );
			}
			
			JSONObject csAttrValuePairs = filterCSAttributes(attrValuePairs);
			// no context service attribute matching.
			if( csAttrValuePairs.length() <= 0 )
			{
				return;
			}
			
			long start1 = System.currentTimeMillis();
			
			List<CSUpdateTransformedMessage> transformedMesgList 
				= this.csPrivacyTransform.transformUpdateForCSPrivacy
				(myGUIDInfo.getGuid(), attrValuePairs, aclmap, anonymizedIDList);
			
			long end1 = System.currentTimeMillis();
			
			assert(transformedMesgList.size() > 0);
			
			long currId;
			
			synchronized( this.privacyUpdateIdLock )
			{
				currId = this.privacyReqId++;
			}
			
			long currblockReqId;
			
			synchronized( this.blockingReqIDLock )
			{
				currblockReqId = this.blockingReqID++;
			}
			
			BlockingUpdateReply blockingUpd = new BlockingUpdateReply(currblockReqId);
			
			
			UpdateReplyInterface privacyUpdRep = new PrivacyUpdateReply(currId, 
					blockingUpd, this.blockingCallBack, transformedMesgList.size());
			
//			PrivacyUpdateReplyTracker privacyUpdRepTracker 
//				= new PrivacyUpdateReplyTracker(, 
//						);
//			
//			privacyCallBack.addUpdateReply(currId, privacyUpdRepTracker);
			
			// now all the anonymized IDs and the attributes that needs to be updated
			// are calculated, 
			// just need to send out updates
			
			//List<Long> finishTimeList = new LinkedList<Long>();
			
//			ParallelUpdateStateStorage updateState 
//								= new ParallelUpdateStateStorage(transformedMesgList, 
//										finishTimeList);
			
			for( int i=0; i<transformedMesgList.size(); i++ )
			{
				CSUpdateTransformedMessage csTransformedMessage 
									= transformedMesgList.get(i);
			
//				UpdateOperationThread updateThread = new UpdateOperationThread(GUID, 
//						csTransformedMessage, versionNum, blocking, 
//						updateState );
//				this.executorService.execute(updateThread);
				
				String IDString 
					= Utils.bytArrayToHex(csTransformedMessage.getAnonymizedID());
				
				sendUpdateToCS( IDString, csTransformedMessage.getAttrValJSON(), 
						csTransformedMessage.getAnonymizedIDToGuidMapping(), 
						versionNum, privacyUpdRep, privacyCallBack );
			}
			
			long end2 = System.currentTimeMillis();
			
			if( transformedMesgList.size() > 0 )
			{
				System.out.println
				("sendUpdateSecure complete GUID "+GUID+" transform time "+(end1-start1)
					+" total time "+(end2-start1)+
					" length "+attrValuePairs.length()+" transformedMesgList size "
					+transformedMesgList.size() );
			}
			
			blockingUpd.waitForCompletion();
		}
		catch( JSONException jsoEx )
		{
			jsoEx.printStackTrace();
		}
	}

	@Override
	public int sendSearchQuerySecure(String searchQuery, JSONArray replyArray, 
				long expiryTime, GuidEntry myGUIDInfo)
	{
		if( replyArray == null )
		{
			ContextServiceLogger.getLogger().warning("null passsed "
					+ "as replyArray in sendSearchQuery");
			return -1;
		}
		
		long currBlockingId;
		synchronized( this.blockingReqIDLock )
		{
			currBlockingId = this.blockingReqID++;
		}
		
		long start 		= System.currentTimeMillis();
		BlockingSearchReply blockingSearch = new BlockingSearchReply(currBlockingId);
		
		sendSearchQueryToCS(searchQuery, expiryTime, blockingSearch, blockingCallBack);
		
		blockingSearch.waitForCompletion();
		
		List<CSSearchReplyTransformedMessage> searchRepTransformList 
						= new LinkedList<CSSearchReplyTransformedMessage>();
		
		for(int i=0; i<blockingSearch.getSearchReplyArray().length(); i++)
		{
			try
			{
				JSONArray jsoArr1 = blockingSearch.getSearchReplyArray().getJSONArray(i);
				for( int j=0; j<jsoArr1.length(); j++ )
				{
					JSONObject searchRepJSON = jsoArr1.getJSONObject(j);

					if( ContextServiceConfig.DECRYPTIONS_ON_SEARCH_REPLY_ENABLED )
					{
						SearchReplyGUIDRepresentationJSON searchRepObj 
						= SearchReplyGUIDRepresentationJSON.fromJSONObject(searchRepJSON);
						CSSearchReplyTransformedMessage csSearchRepTransform 
										= new CSSearchReplyTransformedMessage(searchRepObj);
						searchRepTransformList.add(csSearchRepTransform);
					}
					else
					{
						// just adding the whole JSON here for the user to decrypt later on.
						replyArray.put(searchRepJSON);
					}
				}
			} 
			catch ( JSONException e )
			{
				e.printStackTrace();
			}
		}
		
		long end1 = System.currentTimeMillis();
		
		if( ContextServiceConfig.DECRYPTIONS_ON_SEARCH_REPLY_ENABLED )
		{
			this.csPrivacyTransform.unTransformSearchReply( myGUIDInfo,
				searchRepTransformList, replyArray );
			long end2 = System.currentTimeMillis();
			
			System.out.println("SendSearchQuerySecure search reply from CS time "+ 
						(end1-start)+" reply decryption time "+(end2-end1)+" fromCS reply size "
						+ searchRepTransformList.size()+" final reply size "+replyArray.length() );
			
			return replyArray.length();
		}
		else
		{
			long end2 = System.currentTimeMillis();
			
			System.out.println("SendSearchQuerySecure search reply from CS time "+ 
						(end1-start)+" reply decryption time "+(end2-end1)+" fromCS reply size "
						+ searchRepTransformList.size()+" final reply size "+replyArray.length() );
			
			return blockingSearch.getReplySize();
		}
	}
	
	
	@Override
	public boolean handleMessage(JSONObject jsonObject)
	{
		try
		{
			if( jsonObject.getInt(ContextServicePacket.PACKET_TYPE) 
					== ContextServicePacket.PacketType.QUERY_MSG_FROM_USER_REPLY.getInt() )
			{
				handleQueryReply(jsonObject);
			} else if( jsonObject.getInt(ContextServicePacket.PACKET_TYPE)
					== ContextServicePacket.PacketType.VALUE_UPDATE_MSG_FROM_GNS_REPLY.getInt() )
			{
				handleUpdateReply(jsonObject);
			} else if( jsonObject.getInt(ContextServicePacket.PACKET_TYPE)
					== ContextServicePacket.PacketType.REFRESH_TRIGGER.getInt() )
			{
				handleRefreshTrigger(jsonObject);
			} else if( jsonObject.getInt(ContextServicePacket.PACKET_TYPE)
					== ContextServicePacket.PacketType.GET_REPLY_MESSAGE.getInt() )
			{
				handleGetReply(jsonObject);
			} else if(jsonObject.getInt(ContextServicePacket.PACKET_TYPE)
					== ContextServicePacket.PacketType.CONFIG_REPLY.getInt() )
			{
				handleConfigReply(jsonObject);
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Sends update to CS in both privacy and non privacy case.
	 * filtering of CS attributes has already happened before this function call.
	 * It assumes all attributes in csAttrValMap are CS attributes.
	 */
	private void sendUpdateToCS( String GUID, 
			JSONObject csAttrValPair, JSONArray anonymizedIDToGuidMappingArray, 
			long versionNum, UpdateReplyInterface updReplyObj, CallBackInterface callback )
	{
		try
		{
			long currId;
			
			synchronized( this.updateIdLock )
			{
				currId = this.updateReqId++;
			}
			
			long requestID = currId;
			
//			ValueUpdateFromGNS<NodeIDType> valUpdFromGNS = 
//				new ValueUpdateFromGNS<NodeIDType>(null, versionNum, GUID, 
//						jsonObject, reqeustID, sourceIP, sourcePort, 
//							System.currentTimeMillis() );
			
			ValueUpdateFromGNS<NodeIDType> valUpdFromGNS = new
					ValueUpdateFromGNS<NodeIDType>( null, versionNum, GUID, 
							csAttrValPair, requestID, sourceIP, sourcePort, 
							System.currentTimeMillis(), anonymizedIDToGuidMappingArray );
			
			UpdateStorage<NodeIDType> updateQ = new UpdateStorage<NodeIDType>();
			updateQ.requestID = currId;
			updateQ.valUpdFromGNS = valUpdFromGNS;
			updateQ.valUpdFromGNSReply = null;
			updateQ.updReplyObj = updReplyObj;
			updateQ.callback = callback;
			//updateQ.blocking = blocking;
			
			this.pendingUpdate.put(currId, updateQ);
			
			InetSocketAddress sockAddr = this.csNodeAddresses.get(rand.nextInt(csNodeAddresses.size()));
			
			ContextServiceLogger.getLogger().fine("ContextClient sending update requestID "+currId+" to "+sockAddr+" json "+
					valUpdFromGNS);
			niot.sendToAddress(sockAddr, valUpdFromGNS.toJSONObject());
			
//			if( blocking )
//			{
//				synchronized( updateQ )
//				{
//					while( updateQ.valUpdFromGNSReply == null )
//					{
//						try 
//						{
//							updateQ.wait();
//						} catch (InterruptedException e) 
//						{
//							e.printStackTrace();
//						}
//					}
//				}
//				pendingUpdate.remove(currId);
//			}
		}
		catch ( Exception | Error e )
		{
			e.printStackTrace();
		}
	}
	
	private void sendUpdateToGNS(GuidEntry writingGuid, 
			JSONObject attrValuePair)
	{
		if(gnsClient != null)
		{
			try 
			{
				gnsClient.update(writingGuid, attrValuePair);
			} catch (IOException e) 
			{
				e.printStackTrace();
			} catch (ClientException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	private void sendSearchQueryToCS(String searchQuery, 
			long expiryTime, SearchReplyInterface searchRep, 
			CallBackInterface callback)
	{
		long currId;
		synchronized( this.searchIdLock )
		{
			currId = this.searchReqId++;
		}
		
		QueryMsgFromUser<NodeIDType> qmesgU 
			= new QueryMsgFromUser<NodeIDType>(this.nodeid, searchQuery, 
					currId, expiryTime, sourceIP, sourcePort);
		
		SearchQueryStorage<NodeIDType> searchQ = new SearchQueryStorage<NodeIDType>();
		searchQ.requestID = currId;
		searchQ.queryMsgFromUser = qmesgU;
		searchQ.queryMsgFromUserReply = null; 
		searchQ.searchRep = searchRep;
		searchQ.callback = callback;
		
		this.pendingSearches.put(currId, searchQ);
		InetSocketAddress sockAddr 
				= this.csNodeAddresses.get(rand.nextInt(csNodeAddresses.size()));
		
		try 
		{
			niot.sendToAddress(sockAddr, qmesgU.toJSONObject());
		} catch (IOException e) 
		{
			e.printStackTrace();
		} catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
//		synchronized( searchQ )
//		{
//			while( searchQ.queryMsgFromUserReply == null )
//			{
//				try 
//				{
//					searchQ.wait();
//				} catch (InterruptedException e) 
//				{
//					e.printStackTrace();
//				}
//			}
//		}
		
//		JSONArray result = searchQ.queryMsgFromUserReply.getResultGUIDs();
//		int resultSize = searchQ.queryMsgFromUserReply.getReplySize();
//		pendingSearches.remove(currId);
//		
//		SearchReplyAnswer searchAnswer = new SearchReplyAnswer();
//		searchAnswer.resultArray = result;
//		searchAnswer.resultSize = resultSize;
//		return searchAnswer;
	}
	
	/**
	 * Just used to return the search reply answer from
	 * private method to public method
	 * @author adipc
	 *
	 */
//	private class SearchReplyAnswer 
//	{
//		JSONArray resultArray;
//		int resultSize;
//	}
	
	private void handleUpdateReply(JSONObject jso)
	{
		ValueUpdateFromGNSReply<NodeIDType> vur = null;
		try
		{
			vur = new ValueUpdateFromGNSReply<NodeIDType>(jso);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		long currReqID = vur.getUserReqNum();
		ContextServiceLogger.getLogger().fine("Update reply recvd "+currReqID);
		UpdateStorage<NodeIDType> replyUpdObj = this.pendingUpdate.get(currReqID);
		
		if( replyUpdObj != null )
		{
			replyUpdObj.valUpdFromGNSReply = vur;
			replyUpdObj.callback.updateCompletion(replyUpdObj.updReplyObj);
			
			this.pendingUpdate.remove(currReqID);
		}
		else
		{
			ContextServiceLogger.getLogger().fine("Update reply recvd "
											+currReqID+" update Obj null");
			assert(false);
		}
	}
	
	private void handleGetReply(JSONObject jso)
	{
		try
		{
			GetReplyMessage<NodeIDType> getReply;
			getReply = new GetReplyMessage<NodeIDType>(jso);
			
			long reqID = getReply.getReqID();
			GetStorage<NodeIDType> replyGetObj = this.pendingGet.get(reqID);
			replyGetObj.getReplyMessage = getReply;
			
			synchronized(replyGetObj)
			{
				replyGetObj.notify();
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleConfigReply(JSONObject jso)
	{
		try
		{
			ClientConfigReply<NodeIDType> configReply
									= new ClientConfigReply<NodeIDType>(jso);
			
			JSONArray nodeIPArray 		= configReply.getNodeConfigArray();
			JSONArray attrInfoArray 	= configReply.getAttributeArray();
			JSONArray subspaceInfoArray = configReply.getSubspaceInfoArray();
			
			for( int i=0;i<nodeIPArray.length();i++ )
			{
				String ipPort = nodeIPArray.getString(i);
				String[] parsed = ipPort.split(":");
				csNodeAddresses.add(new InetSocketAddress(parsed[0], Integer.parseInt(parsed[1])));
			}
			
			for(int i=0;i<attrInfoArray.length();i++)
			{
				String attrName = attrInfoArray.getString(i);
				attributeHashMap.put(attrName, true);	
			}
			
			for( int i=0; i<subspaceInfoArray.length(); i++ )
			{
				JSONArray subspaceAttrJSON = subspaceInfoArray.getJSONArray(i);
				subspaceAttrMap.put(i, subspaceAttrJSON);
			}
			
			synchronized( this.configLock )
			{
				configLock.notify();
			}
		} catch ( JSONException e )
		{
			e.printStackTrace();
		}
	}
	
	private void handleQueryReply(JSONObject jso)
	{
		try
		{
			QueryMsgFromUserReply<NodeIDType> qmur;
			qmur = new QueryMsgFromUserReply<NodeIDType>(jso);
			
			long reqID = qmur.getUserReqNum();
			SearchQueryStorage<NodeIDType> replySearchObj 
									= this.pendingSearches.get(reqID);
			replySearchObj.queryMsgFromUserReply = qmur;
			
			JSONArray result = qmur.getResultGUIDs();
			int resultSize = qmur.getReplySize();
			
			replySearchObj.searchRep.setSearchReplyArray(result);
			replySearchObj.searchRep.setReplySize(resultSize);
			
			replySearchObj.callback.searchCompletion(replySearchObj.searchRep);
			
			pendingSearches.remove(reqID);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	private void sendConfigRequest()
	{	
		ClientConfigRequest<NodeIDType> clientConfigReq 
			= new ClientConfigRequest<NodeIDType>(this.nodeid, sourceIP, sourcePort);
		
		InetSocketAddress sockAddr = new InetSocketAddress(configHost, configPort);
		
		try 
		{
			niot.sendToAddress(sockAddr, clientConfigReq.toJSONObject());
		} catch (IOException e) 
		{
			e.printStackTrace();
		} catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		synchronized( this.configLock )
		{
			while( csNodeAddresses.size() == 0 )
			{
				try 
				{
					this.configLock.wait();
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private void handleRefreshTrigger(JSONObject jso)
	{
		try
		{
			RefreshTrigger<NodeIDType> qmur 
						= new RefreshTrigger<NodeIDType>(jso);
			
			synchronized(refreshTriggerClientWaitLock)
			{
				refreshTriggerQueue.add(qmur.toJSONObject());
				refreshTriggerClientWaitLock.notify();
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * filters attributes, returns only attributes value pairs 
	 * that are supported by CS
	 * @param gnsAttrValuePairs
	 * @return
	 * @throws JSONException
	 */
	private JSONObject filterCSAttributes(JSONObject gnsAttrValuePairs) throws JSONException
	{
		//filetering out CS attrbutes.
		JSONObject csAttrValuePairs = new JSONObject();
		Iterator gnsIter = gnsAttrValuePairs.keys();
		
		while( gnsIter.hasNext() )
		{
			String gnsAttrName = (String) gnsIter.next();
			if( attributeHashMap.containsKey(gnsAttrName) )
			{
				csAttrValuePairs.put(gnsAttrName, gnsAttrValuePairs.get(gnsAttrName));
			}
		}
		return csAttrValuePairs;
	}
	
	private void initializeClient() throws NoSuchAlgorithmException
	{	
		refreshTriggerQueue = new LinkedList<JSONObject>();
		// FIXME: add a timeout mechanism here.
		sendConfigRequest();
		
		// for anonymized ID
		if( this.transformType == SUBSPACE_BASED_CS_TRANSFORM )
		{
//			anonymizedIDCreation 
//				= new SubspaceBasedAnonymizedIDCreator(subspaceAttrMap);
//			
//			// for cs transform
//			csPrivacyTransform = new SubspaceBasedCSTransform(executorService);
		}
		else if( this.transformType == HYPERSPACE_BASED_CS_TRANSFORM )
		{
			anonymizedIDCreation = new HyperspaceBasedAnonymizedIDCreator();
			
			// for cs transform
			csPrivacyTransform = new HyperspaceBasedCSTransform();
		}
		else if( this.transformType == GUID_BASED_CS_TRANSFORM )
		{
			anonymizedIDCreation = new GUIDBasedAnonymizedIDCreator();
			// for cs transform
			csPrivacyTransform = new HyperspaceBasedCSTransform();
		}
		
		// for gnsTransform
		gnsPrivacyTransform = new EncryptionBasedGNSPrivacyTransform();
	}
	
	// testing secure client code
	// TODO: test the scheme with the example given in the draft.
	public static void main(String[] args) 
			throws Exception
	{
		// testing based on the example in the draft.
		// more testing of each method in secure interface.
		// test with the example in the draft.
		//GUIDEntryStoringClass myGUIDInfo, JSONArray ACLArray
		//String guid = GuidUtils.createGuidFromPublicKey(keyPair.getPublic().getEncoded());
//		Properties props = System.getProperties();
//		props.setProperty("gigapaxosConfig", "conf/gnsClientConf/gigapaxos.client.local.properties");
//		props.setProperty("javax.net.ssl.trustStorePassword", "qwerty");
//		props.setProperty("javax.net.ssl.trustStore", "conf/gnsClientConf/trustStore/node100.jks");
//		props.setProperty("javax.net.ssl.keyStorePassword", "qwerty");
//		props.setProperty("javax.net.ssl.keyStore", "conf/gnsClientConf/keyStore/node100.jks");
//		
//		InetSocketAddress address 
//			= new InetSocketAddress("127.0.0.1", GNSClientConfig.LNS_PORT);
//		UniversalTcpClientExtended gnsClient = new GNSClient(null, address, true);
//	
//		GuidEntry masterGuid = GuidUtils.lookupOrCreateAccountGuid(gnsClient,
//            "ayadav@cs.umass.edu", "password", true);
	
		KeyPairGenerator kpg;
		kpg = KeyPairGenerator.getInstance("RSA");
		KeyPair kp0 = kpg.genKeyPair();
		PublicKey publicKey0 = kp0.getPublic();
		PrivateKey privateKey0 = kp0.getPrivate();
		
		System.out.println("privateKey0 "+((RSAPrivateKey)privateKey0).getModulus().bitLength());
		byte[] publicKeyByteArray0 = publicKey0.getEncoded();
		byte[] privateKeyByteArray0 = privateKey0.getEncoded();
		
		String guid0 = Utils.convertPublicKeyToGUIDString(publicKeyByteArray0);
		GuidEntry myGUID = new GuidEntry("Guid0", guid0, publicKey0, privateKey0);
		
//		PublicKey publicKey0 = masterGuid.getPublicKey();
//		PrivateKey privateKey0 = masterGuid.getPrivateKey();
//		byte[] publicKeyByteArray0 = publicKey0.getEncoded();
//		byte[] privateKeyByteArray0 = privateKey0.getEncoded();
//		String guid0 = GuidUtils.createGuidFromPublicKey(publicKeyByteArray0);
//		GuidEntry myGUID = masterGuid;
		
		
		Vector<GuidEntry> guidsVector = new Vector<GuidEntry>();
		

		
		guidsVector.add(myGUID);
		
		// draft example has 7 guids
		for(int i=1; i <= 7; i++)
		{
			KeyPair kp = kpg.genKeyPair();
			PublicKey publicKey = kp.getPublic();
			PrivateKey privateKey = kp.getPrivate();
			byte[] publicKeyByteArray = publicKey.getEncoded();
			byte[] privateKeyByteArray = privateKey.getEncoded();
			
			String guid = Utils.convertPublicKeyToGUIDString(publicKeyByteArray);
			
			GuidEntry currGUID = new GuidEntry("Guid"+i, guid, 
					publicKey, privateKey);
			
			guidsVector.add(currGUID);
		}
		
		HashMap<String, List<ACLEntry>> aclMap = new HashMap<String, List<ACLEntry>>();
		
		List<ACLEntry> acl0 = new LinkedList<ACLEntry>();
		
		acl0.add(new ACLEntry(guidsVector.get(1).getGuid(), guidsVector.get(1).getPublicKey()));
		acl0.add(new ACLEntry(guidsVector.get(2).getGuid(), guidsVector.get(2).getPublicKey()));
		acl0.add(new ACLEntry(guidsVector.get(3).getGuid(), guidsVector.get(3).getPublicKey()));
		aclMap.put("attr1", acl0);
		
		
		List<ACLEntry> acl1 = new LinkedList<ACLEntry>();
		acl1.add(new ACLEntry(guidsVector.get(4).getGuid(), guidsVector.get(4).getPublicKey()));
		acl1.add(new ACLEntry(guidsVector.get(5).getGuid(), guidsVector.get(5).getPublicKey()));
		acl1.add(new ACLEntry(guidsVector.get(3).getGuid(), guidsVector.get(3).getPublicKey()));
		aclMap.put("attr2", acl1);
		
		
		List<ACLEntry> acl2 = new LinkedList<ACLEntry>();
		acl2.add(new ACLEntry(guidsVector.get(1).getGuid(), guidsVector.get(1).getPublicKey()));
		acl2.add(new ACLEntry(guidsVector.get(2).getGuid(), guidsVector.get(2).getPublicKey()));
		aclMap.put("attr3", acl2);
		
		
		List<ACLEntry> acl3 = new LinkedList<ACLEntry>();
		acl3.add(new ACLEntry(guidsVector.get(1).getGuid(), guidsVector.get(1).getPublicKey()));
		acl3.add(new ACLEntry(guidsVector.get(2).getGuid(), guidsVector.get(2).getPublicKey()));
		acl3.add(new ACLEntry(guidsVector.get(3).getGuid(), guidsVector.get(3).getPublicKey()));
		aclMap.put("attr4", acl3);
		
		
		List<ACLEntry> acl4 = new LinkedList<ACLEntry>();
		acl4.add(new ACLEntry(guidsVector.get(6).getGuid(), guidsVector.get(6).getPublicKey()));
		acl4.add(new ACLEntry(guidsVector.get(7).getGuid(), guidsVector.get(7).getPublicKey()));
		aclMap.put("attr5", acl4);
		
		
		List<ACLEntry> acl5 = new LinkedList<ACLEntry>();
		acl5.add(new ACLEntry(guidsVector.get(4).getGuid(), guidsVector.get(4).getPublicKey()));
		acl5.add(new ACLEntry(guidsVector.get(5).getGuid(), guidsVector.get(5).getPublicKey()));
		acl5.add(new ACLEntry(guidsVector.get(1).getGuid(), guidsVector.get(1).getPublicKey()));
		
		aclMap.put("attr6", acl5);
		
		
		ContextServiceClient<Integer> csClient = new ContextServiceClient<Integer>("127.0.0.1", 8000, 
				ContextServiceClient.SUBSPACE_BASED_CS_TRANSFORM);
		
		CallBackInterface callback = new SampleCallBack();
		
		
		List<AnonymizedIDEntry> anonymizedIdList = csClient.computeAnonymizedIDs
					(myGUID, aclMap);
		JSONObject attrValPair = new JSONObject();
		attrValPair.put("attr1", 10+"");
		
		attrValPair.put("attr2", 15+"");
		
		UpdateReplyInterface updateRep = new SampleUpdateReply(1);
		
		csClient.sendUpdateSecureWithCallback(guid0, 
				myGUID, attrValPair, -1, aclMap, anonymizedIdList, updateRep, callback);
		
		Thread.sleep(2000);
		
		String searchQuery = "SELECT GUID_TABLE.guid FROM GUID_TABLE WHERE attr1 >= 5 AND attr1 <= 15";
		JSONArray replyArray = new JSONArray();
		GuidEntry queryingGuid = guidsVector.get(3);
		
		SearchReplyInterface searchRep = new SampleSearchReply(2);
		
		csClient.sendSearchQuerySecureWithCallBack
					(searchQuery, 300000, queryingGuid, searchRep, callback);
		
		System.out.println("Query for attr1 querying GUID "+ queryingGuid.getGuid()+
				" Real GUID "+guid0+" reply Arr "+replyArray);
		
		
		searchQuery = "SELECT GUID_TABLE.guid FROM GUID_TABLE WHERE attr1 >= 5 AND attr1 <= 15"
				+ " AND attr2 >= 10 AND attr2 <= 20";
		replyArray = new JSONArray();
		queryingGuid = guidsVector.get(1);
		
		searchRep = new SampleSearchReply(3);
		
		csClient.sendSearchQuerySecureWithCallBack(searchQuery, 300000, queryingGuid,
				searchRep, callback);
		
		System.out.println("Query for att1 and attr4 querying GUID "+ queryingGuid.getGuid()+
				" Real GUID "+guid0+" reply Arr "+replyArray);
		
//		queryingGuid = guidsVector.get(1);
//		JSONObject getObj = csClient.sendGetRequestSecure(guid0, queryingGuid);
//		System.out.println("recvd Obj "+getObj);
	}
	
	/**
	 * Converts attr value JSON given in the api calls to
	 * attr AttrValueRepresentationJSON map
	 * @return
	 * @throws JSONException 
	 */
	/*private HashMap<String, AttrValueRepresentationJSON> convertAttrValueToSystemFormat
								(JSONObject attrValuePairs) throws JSONException
	{
		HashMap<String, AttrValueRepresentationJSON> map 
							= new HashMap<String, AttrValueRepresentationJSON>();
		
		Iterator<String> jsonIter = attrValuePairs.keys();
		while(jsonIter.hasNext())
		{
			String attrName = jsonIter.next();
			String value = attrValuePairs.getString(attrName);
			AttrValueRepresentationJSON valRep = new AttrValueRepresentationJSON(value);
			
			map.put(attrName, valRep);
		}
		return map;
	}*/
	
//	@Override
//	public void sendUpdateSecure( String GUID, GuidEntry myGUIDInfo, 
//			JSONObject attrValuePairs, long versionNum, boolean blocking,
//			HashMap<String, List<ACLEntry>> aclmap, List<AnonymizedIDEntry> anonymizedIDList )
//	{
//		try
//		{
//			if(gnsClient != null)
//			{
//				GNSTransformedMessage gnsTransformMesg = 
//						this.gnsPrivacyTransform.transformUpdateForGNSPrivacy
//						(attrValuePairs, aclmap);
//				
//				sendUpdateToGNS( myGUIDInfo, 
//						gnsTransformMesg.getEncryptedAttrValuePair() );
//			}
//			
//			JSONObject csAttrValuePairs = filterCSAttributes(attrValuePairs);
//			// no context service attribute matching.
//			if( csAttrValuePairs.length() <= 0 )
//			{
//				return;
//			}
//			
////			HashMap<String, AttrValueRepresentationJSON> attrValueMap =
////					convertAttrValueToSystemFormat(csAttrValuePairs);
//			
//			// TODO: Different CSUpdateTransformedMessage messages can be computed
//			// in parallel.
//			
//			long start1 = System.currentTimeMillis();
//			
//			List<CSUpdateTransformedMessage> transformedMesgList 
//				= this.csPrivacyTransform.transformUpdateForCSPrivacy
//				(myGUIDInfo.getGuid(), attrValuePairs, aclmap, anonymizedIDList);
//			
//			long end1 = System.currentTimeMillis();
//			
//			// now all the anonymized IDs and the attributes that needs to be updated
//			// are calculated, 
//			// just need to send out updates
//			
//			List<Long> finishTimeList = new LinkedList<Long>();
//			
//			ParallelUpdateStateStorage updateState 
//								= new ParallelUpdateStateStorage(transformedMesgList, 
//										finishTimeList);
//			
//			for( int i=0; i<transformedMesgList.size(); i++ )
//			{
//				CSUpdateTransformedMessage csTransformedMessage = transformedMesgList.get(i);
//			
//				UpdateOperationThread updateThread = new UpdateOperationThread(GUID, 
//						csTransformedMessage, versionNum, blocking, 
//						updateState );
//				this.executorService.execute(updateThread);
//				
//				//String IDString = Utils.bytArrayToHex(csTransformedMessage.getAnonymizedID());
//				//sendUpdateToCS( IDString, csTransformedMessage.getAttrValMap() , 
//				//		versionNum, blocking );
//			}
//			updateState.waitForCompletion();
//			
//			long end2 = System.currentTimeMillis();
//			
//			finishTimeList.sort(null);
//			
//			if(transformedMesgList.size() > 0)
//			{
//				System.out.println
//				("sendUpdateSecure complete GUID "+GUID+" transform time "+(end1-start1)
//					+" total time "+(end2-start1)+
//					" length "+attrValuePairs.length()+" transformedMesgList size "
//					+transformedMesgList.size() +" first anonymized ID finish time "
//					+finishTimeList.get(0) );
//			}
//		}
//		catch( JSONException jsoEx )
//		{
//			jsoEx.printStackTrace();
//		}
//	}
	
//	public int sendSearchQuery(String searchQuery, JSONArray replyArray, 
//			long expiryTime)
//	{
//		if( replyArray == null )
//		{
//			ContextServiceLogger.getLogger().warning("null passsed "
//					+ "as replyArray in sendSearchQuery");
//			return -1;
//		}
//		
//		SearchReplyAnswer searchAnswer = sendSearchQueryToCS(searchQuery, expiryTime);
//		
//		// no untransformation needed in no privacy case.
//		// context service returns an array of JSONArrays.
//		// Each JSONArray contains the SearchReplyGUIDRepresentationJSON JSONObjects 
//		for(int i=0; i<searchAnswer.resultArray.length(); i++)
//		{
//			try
//			{
//				JSONArray jsoArr1 = searchAnswer.resultArray.getJSONArray(i);
//				for(int j=0; j<jsoArr1.length(); j++)
//				{
//					//resultGUIDMap.put(jsoArr1.getString(j), true);
//					JSONObject searchRepJSON = jsoArr1.getJSONObject(j);
//					SearchReplyGUIDRepresentationJSON searchRepObj 
//							= SearchReplyGUIDRepresentationJSON.fromJSONObject(searchRepJSON);
//					replyArray.put(searchRepObj.getID());
//				}
//			} catch ( JSONException e )
//			{
//				e.printStackTrace();
//			}
//		}
//		return searchAnswer.resultSize;
//	}
	
	/**
	 * Thread tha performs the update.
	 * @author adipc
	 */
	/*private class UpdateOperationThread implements Runnable
	{
		private final String guid;
		private final CSUpdateTransformedMessage csTransformedMessage;
		private final long versionNum;
		private final boolean blocking;
		private final ParallelUpdateStateStorage updateState;
		private final long startTime;
		
		
		public UpdateOperationThread(String guid, CSUpdateTransformedMessage csTransformedMessage
				, long versionNum, boolean blocking, ParallelUpdateStateStorage updateState)
		{
			this.guid = guid;
			this.csTransformedMessage = csTransformedMessage;
			this.versionNum = versionNum;
			this.blocking = blocking;
			this.updateState = updateState;
			startTime = System.currentTimeMillis();
		}
		
		@Override
		public void run() 
		{
			String IDString = Utils.bytArrayToHex(csTransformedMessage.getAnonymizedID());
			
			sendUpdateToCS( IDString, csTransformedMessage.getAttrValJSON(), 
					csTransformedMessage.getAnonymizedIDToGuidMapping(), 
					versionNum, blocking );
			
			long endTime = System.currentTimeMillis();
//			if(ContextServiceConfig.DEBUG_MODE )
//			{
//				System.out.println("GUID "+guid+" AnonymizedID "+IDString+" update finished at "
//							+(endTime-startTime)+" full mesg "+csTransformedMessage.toString());
//			}
			updateState.incrementNumCompleted((endTime-startTime));
		}
	}*/
}