package edu.umass.cs.contextservice.schemes;


import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.common.CSNodeConfig;
import edu.umass.cs.contextservice.database.AbstractDataStorageDB;
import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.contextservice.messages.ContextServicePacket.PacketType;
import edu.umass.cs.contextservice.profilers.CNSProfiler;
import edu.umass.cs.contextservice.regionmapper.AbstractRegionMappingPolicy;
import edu.umass.cs.contextservice.schemes.components.AbstractGUIDAttrValueProcessing;
import edu.umass.cs.contextservice.schemes.components.TriggerProcessingInterface;
import edu.umass.cs.contextservice.updates.GUIDUpdateInfo;
import edu.umass.cs.nio.GenericMessagingTask;
import edu.umass.cs.nio.JSONMessenger;
import edu.umass.cs.protocoltask.ProtocolEvent;
import edu.umass.cs.protocoltask.ProtocolTask;

/**
 * 
 * @author ayadav
 */
// suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class RegionMappingBasedScheme extends AbstractScheme
{
	private  AbstractDataStorageDB hyperspaceDB 							= null;
	private final ExecutorService nodeES;
	
	private HashMap<String, GUIDUpdateInfo> guidUpdateInfoMap				= null;
	
	private final AbstractRegionMappingPolicy regionMappingPolicy;
	
	private final AbstractGUIDAttrValueProcessing guidAttrValProcessing;
	
	private TriggerProcessingInterface triggerProcessing;
	
	private CNSProfiler profStats;
	
	private AbstractDataSource dataSource;
	
	private HashMap<String, Boolean> groupGUIDSyncMap;
	public static final Logger log 											= ContextServiceLogger.getLogger();
	
	
	public RegionMappingBasedScheme(CSNodeConfig nc, 
			JSONMessenger<Integer> m) throws Exception
	{
		super(nc, m);
		nodeES = null;
		regionMappingPolicy = null;
		guidAttrValProcessing = null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleQueryMsgFromUser(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleValueUpdateFromGNS(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleQueryMesgToSubspaceRegion(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleQueryMesgToSubspaceRegionReply(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		return null;
	}

	@Override
	public GenericMessagingTask<Integer, ?>[] handleValueUpdateToSubspaceRegionMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleGetMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		return null;
	}
	
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleGetReplyMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleValueUpdateToSubspaceRegionReplyMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleQueryTriggerMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		return null;
	}

	@Override
	public GenericMessagingTask<Integer, ?>[] handleUpdateTriggerMessage(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleUpdateTriggerReply(
			ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleClientConfigRequest(ProtocolEvent<PacketType, String> event,
			ProtocolTask<Integer, PacketType, String>[] ptasks) 
	{
		return null;
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleACLUpdateToSubspaceRegionMessage(
			ProtocolEvent<PacketType, String> event, 
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		return null;
	}

	@Override
	public GenericMessagingTask<Integer, ?>[] handleACLUpdateToSubspaceRegionReplyMessage(
			ProtocolEvent<PacketType, String> event, 
			ProtocolTask<Integer, PacketType, String>[] ptasks)
	{
		return null;
	}
	
	public static JSONObject getUnsetAttrJSON(JSONObject attrValJSON)
	{
		JSONObject unsetAttrJSON = null;
		return unsetAttrJSON;
	}
	
	public static boolean checkIfAnonymizedIDToGuidInfoAlreadyStored(JSONObject oldValJSON) 
					throws JSONException
	{
		boolean alreadyStored = false;
		return alreadyStored;
	}
	
	public static void main(String[] args)
	{
	}
}
