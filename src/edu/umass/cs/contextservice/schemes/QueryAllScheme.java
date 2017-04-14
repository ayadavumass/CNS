package edu.umass.cs.contextservice.schemes;


import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;


import edu.umass.cs.contextservice.database.QueryAllDB;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.contextservice.messages.ContextServicePacket.PacketType;
import edu.umass.cs.contextservice.messages.QueryMesgToSubspaceRegion;
import edu.umass.cs.contextservice.messages.QueryMesgToSubspaceRegionReply;
import edu.umass.cs.contextservice.updates.GUIDUpdateInfo;
import edu.umass.cs.nio.GenericMessagingTask;
import edu.umass.cs.nio.JSONMessenger;
import edu.umass.cs.nio.interfaces.NodeConfig;
import edu.umass.cs.protocoltask.ProtocolEvent;
import edu.umass.cs.protocoltask.ProtocolTask;


/**
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class QueryAllScheme extends AbstractScheme
{
	private  QueryAllDB queryAllDB 													= null;
	
	private final ExecutorService nodeES;
	
	private HashMap<String, GUIDUpdateInfo> guidUpdateInfoMap						= null;
	
	private final Object pendingQueryLock											= new Object();
	private long queryIdCounter														= 0;
	public static final Logger log 													= ContextServiceLogger.getLogger();
	
	public QueryAllScheme(NodeConfig<Integer> nc, 
			JSONMessenger<Integer> m) throws Exception
	{
		super(nc, m);
		nodeES = null;
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
	public GenericMessagingTask<Integer, ?>[] handleClientConfigRequest
				(ProtocolEvent<PacketType, String> event, 
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
	
	public void processQueryMesgToSubspaceRegionReply
		( QueryMesgToSubspaceRegionReply queryMesgToSubspaceRegionReply )
	{
	}
	
	public void processQueryMesgToSubspaceRegion(QueryMesgToSubspaceRegion
									queryMesgToSubspaceRegion)
	{
	}
	
	public static void main(String[] args)
	{
	}
}