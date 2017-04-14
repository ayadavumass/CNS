package edu.umass.cs.contextservice.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.client.common.ACLEntry;
import edu.umass.cs.contextservice.client.anonymizedID.AnonymizedIDCreationInterface;

import edu.umass.cs.contextservice.client.callback.implementations.BlockingCallBack;
import edu.umass.cs.contextservice.client.callback.implementations.PrivacyCallBack;
import edu.umass.cs.contextservice.client.callback.interfaces.CallBackInterface;
import edu.umass.cs.contextservice.client.callback.interfaces.SearchReplyInterface;
import edu.umass.cs.contextservice.client.callback.interfaces.UpdateReplyInterface;
import edu.umass.cs.contextservice.client.common.AnonymizedIDEntry;
import edu.umass.cs.contextservice.client.csprivacytransform.CSPrivacyTransformInterface;
import edu.umass.cs.contextservice.client.gnsprivacytransform.GNSPrivacyTransformInterface;
import edu.umass.cs.contextservice.client.profiler.ClientProfilerStatClass;
import edu.umass.cs.contextservice.config.ContextServiceConfig.PrivacySchemes;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.gnsclient.client.GNSClient;
import edu.umass.cs.gnsclient.client.util.GuidEntry;
import edu.umass.cs.nio.nioutils.NIOHeader;

/**
 * ContextService client.
 * It is used to send and recv replies from context service.
 * It knows context service node addresses from a file in the conf folder.
 * It is thread safe, means same client can be used by multiple threads without any 
 * synchronization problems.
 * @author adipc
 * @param <Integer>
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class ContextServiceClient extends AbstractContextServiceClient 
				implements ContextClientInterfaceWithPrivacy, 
				ContextServiceClientInterfaceWithoutPrivacy
{
	// if experiment mode is true then triggers are not stored in a queue.
	public static boolean EXPERIMENT_MODE								= false;
	
	// enables the profiler 
	private static final boolean PROFILER_ENABLE						= false;
	
	public static final String SYMMETRIC_KEY_EXCHANGE_FIELD_NAME		= "SYMMETRIC_KEY_EXCHANGE_FIELD";
	
	public static final String GNSCLIENT_CONF_FILE_NAME					= "gnsclient.contextservice.properties";
	
	
	private Queue<JSONObject> refreshTriggerQueue;
	
	private final Object refreshTriggerClientWaitLock 					= new Object();
	
	private GNSClient gnsClient;
	
	// asymmetric key id creation.
	private AnonymizedIDCreationInterface asymmetricAnonymizedIDCreation;
	
	// symmetric key id creation.
	private AnonymizedIDCreationInterface symmetricAnonymizedIDCreation;
	
	//gns transform
	private GNSPrivacyTransformInterface gnsPrivacyTransform;
	
	// asymmetric key privacy transform
	private CSPrivacyTransformInterface asymmetricCSPrivacyTransform;
	
	// symmetric key privacy transform.
	private CSPrivacyTransformInterface symmetricCSPrivacyTransform;
	
	
	private PrivacyCallBack privacyCallBack;
	
	private BlockingCallBack blockingCallBack;
	
	private long blockingReqID 											= 0;
	private final Object blockingReqIDLock 								= new Object();
	
	// used to get stats for experiment.
	private double sumNumAnonymizedIdsUpdated							= 0.0;
	private long totalPrivacyUpdateReqs									= 0;
	private Object printLocks											= new Object();
	
	private double sumAddedGroupGUIDsOnUpdate							= 0.0;
	private double sumRemovedGroupGUIDsOnUpdate							= 0.0;
	private long numTriggers											= 1;
	
	private long lastPrintTime;
	
	private final long startTime										= System.currentTimeMillis();
	
	private final ExecutorService execService;
	
	// PrivacySchemes is defined in context service config.
	private final PrivacySchemes privacyScheme;
	
	private ClientProfilerStatClass clientProf;
	
	/**
	 * Use this constructor if you want to directly communicate with CS, bypassing GNS.
	 * @param csHostName
	 * @param csPortNum
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	public ContextServiceClient( String csHostName, int csPortNum, 
			boolean useGNS, PrivacySchemes privacyScheme )
			throws IOException, NoSuchAlgorithmException
	{
		super( csHostName, csPortNum );
		this.privacyScheme = privacyScheme;
		gnsClient = null;
		execService = Executors.newFixedThreadPool
							(Runtime.getRuntime().availableProcessors());
	}
	
	public void sendUpdateWithCallBack
		( String GUID, GuidEntry myGuidEntry, JSONObject gnsAttrValuePairs, 
		long versionNum, UpdateReplyInterface updReplyObj, CallBackInterface callback )
	{
	}
	
	public void sendSearchQueryWithCallBack(String searchQuery, 
			long expiryTime, SearchReplyInterface searchRep, CallBackInterface callback)
	{
	}
	
	public JSONObject sendGetRequest(String GUID)
	{
		return null;
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
	}
	
	
	public void sendUpdateSecureWithCallback( String GUID, GuidEntry myGUIDInfo, 
			JSONObject attrValuePairs, long versionNum, 
			HashMap<String, List<ACLEntry>> aclmap, List<AnonymizedIDEntry> anonymizedIDList,
			UpdateReplyInterface updReplyObj, CallBackInterface callback )
	{
	}
	
	public void sendSearchQuerySecureWithCallBack
		( String searchQuery, 
			long expiryTime, GuidEntry myGUIDInfo, 
			SearchReplyInterface searchRep, CallBackInterface callback )
	{
	}
	
	
	public JSONObject sendGetRequestSecure(String GUID, GuidEntry myGUIDInfo) throws Exception
	{
		return null;
	}
	
	/**
	 * assumption is that ACL always fits in memory.
	 * If useSymmetricKeys is true then symmetric keys are used.
	 * If useSymmetricKeys is false then asymmetric keys are used.
	 * 
	 * @throws JSONException 
	 */
	public List<AnonymizedIDEntry> computeAnonymizedIDs( GuidEntry myGuidEntry,
			HashMap<String, List<ACLEntry>> aclMap, boolean useSymmetricKeys ) 
					throws JSONException
	{
		return null;
	}
	
	@Override
	public void sendUpdate(String GUID, GuidEntry myGuidEntry, 
			JSONObject attrValuePairs, long versionNum) 
	{
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
		return -1;
	}
	
	@Override
	public void sendUpdateSecure(String GUID, GuidEntry myGUIDInfo, 
			JSONObject attrValuePairs, long versionNum,
			HashMap<String, List<ACLEntry>> aclmap, List<AnonymizedIDEntry> anonymizedIDList) 
	{
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
		return -1; 
	}
	
	public int sendSearchQuerySecure(String searchQuery, JSONArray replyArray, 
				long expiryTime, HashMap<String, byte[]> anonymizedIDToSecretKeyMap)
	{
		if( replyArray == null )
		{
			ContextServiceLogger.getLogger().warning("null passsed "
					+ "as replyArray in sendSearchQuery");
			return -1;
		}
		return -1;
	}
	
	@Override
	public boolean handleMessage(JSONObject jsonObject, NIOHeader nioHeader)
	{
		return true;
	}
	
	
	/**
	 * Returns GNSClient if Context service client was created
	 * by setting useGNS to true.
	 * @return
	 */
	public GNSClient getGNSClient()
	{
		return this.gnsClient;
	}
	
	/**
	 * Reads the SYMMETRIC_KEY_EXCHANGE_FIELD_NAME from GNS 
	 * from the provided GuidEntry.
	 * Decrypts the field using the private key and 
	 * returns a map of anonymized ID and the corresponding symmetric key to use.
	 * Symmetric key is in byte[] format.
	 * @param guidEntry
	 */
	public HashMap<String, byte[]> getAnonymizedIDToSymmetricKeyMapFromGNS(GuidEntry guidEntry)
	{
		return null;
	}	
	
	public void printTriggerStats()
	{
		double avgAddTrigger     = this.sumAddedGroupGUIDsOnUpdate/this.numTriggers;
		double avgRemovedTrigger = this.sumRemovedGroupGUIDsOnUpdate/this.numTriggers;	
		System.out.println("avgAddTrigger "+avgAddTrigger
						+" avgRemovedTrigger "+avgRemovedTrigger);
	}
	
	// testing secure client code
	public static void main(String[] args) 
			throws Exception
	{	
	}
}