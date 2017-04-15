package edu.umass.cs.contextservice.schemes;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.contextservice.messages.BasicContextServicePacket;
import edu.umass.cs.contextservice.messages.ContextServicePacket;
import edu.umass.cs.contextservice.queryparsing.QueryInfo;
import edu.umass.cs.contextservice.updates.UpdateInfo;
import edu.umass.cs.nio.GenericMessagingTask;
import edu.umass.cs.nio.JSONMessenger;
import edu.umass.cs.nio.interfaces.NodeConfig;
import edu.umass.cs.nio.interfaces.PacketDemultiplexer;
import edu.umass.cs.nio.nioutils.NIOHeader;
import edu.umass.cs.protocoltask.ProtocolEvent;
import edu.umass.cs.protocoltask.ProtocolExecutor;
import edu.umass.cs.protocoltask.ProtocolTask;


public abstract class AbstractScheme implements PacketDemultiplexer<JSONObject>, 
											ProtocolTask<Integer, ContextServicePacket.PacketType, String>
{
	protected final JSONMessenger<Integer> messenger;
	protected final ProtocolExecutor<Integer, ContextServicePacket.PacketType, String> protocolExecutor;
	
	protected final Object numMesgLock	;
	
	protected final List<Integer> allNodeIDs;
	
	// stores the pending queries
	protected ConcurrentHashMap<Long, QueryInfo> pendingQueryRequests		= null;
	
	protected ConcurrentHashMap<Long, UpdateInfo> pendingUpdateRequests		= null;
	
	protected long updateIdCounter											= 0;
	
	protected final Object pendingUpdateLock								= new Object();
	
	// lock for synchronizing number of msg update
	protected long numMessagesInSystem										= 0;
	
	private static final String HANDLER_METHOD_PREFIX 
																			= ContextServicePacket.HANDLER_METHOD_PREFIX; // could be any String as scope is local

	private static final List<ContextServicePacket.PacketType> types 
																			= ContextServicePacket.PacketType.getPacketTypes();
	
	private String key 														= "contextserviceKey";
	
	public static final Logger log = ContextServiceLogger.getLogger();
	
	public AbstractScheme(NodeConfig<Integer> nc, JSONMessenger<Integer> m)
	{
		messenger = m;
		protocolExecutor = null;
		numMesgLock = null;
		allNodeIDs = null;		
	}
	
	public int getMyID()
	{
		return this.messenger.getMyID();
	}
	
	/**
	 * returns all nodeIDs
	 * @return
	 */
	public List<Integer> getAllNodeIDs()
	{
		return this.allNodeIDs;
	}
	
	public JSONMessenger<Integer> getJSONMessenger()
	{
		return messenger;
	}
	
	@Override
	public boolean handleMessage(JSONObject jsonObject, NIOHeader nioHeader) 
	{
		BasicContextServicePacket csPacket = null;
		try
		{
			if( (csPacket = this.getContextServicePacket(jsonObject)) != null )
			{
				this.protocolExecutor.handleEvent(csPacket);
			}
		} catch(JSONException je)
		{
			je.printStackTrace();
		}
		return true;
	}
	
	public long getNumMesgInSystem()
	{
		return this.numMessagesInSystem;
	}
	
	/**
	 * spawns the protocol associated 
	 * spawning starts the start[] method
	 * of the protocol task
	 */
	public void spawnTheTask()
	{
		this.protocolExecutor.spawn(this);
	}
	
	@Override
	public String getKey()
	{
		return this.key;
	}
	
	@Override
	public Set<ContextServicePacket.PacketType> getEventTypes()
	{
		return new HashSet<ContextServicePacket.PacketType>(types);
	}
	
	
	@Override
	public GenericMessagingTask<Integer, ?>[] handleEvent(
		ProtocolEvent<ContextServicePacket.PacketType, String> event,
		ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks)
	{
		ContextServicePacket.PacketType type = event.getType();
		try
		{
			this.getClass().getMethod(HANDLER_METHOD_PREFIX+
				ContextServicePacket.getPacketTypeClassName(type), ProtocolEvent.class, 
				ProtocolTask[].class).invoke(this, 
					(BasicContextServicePacket)event, ptasks);
		} catch(NoSuchMethodException nsme)
		{
			nsme.printStackTrace();
		} catch(InvocationTargetException ite)
		{
			ite.printStackTrace();
		} catch(IllegalAccessException iae)
		{
			iae.printStackTrace();
		}
		// CNS doesn't send any task back to be executed by the protocol executor
		return null;
	}
	
	public BasicContextServicePacket getContextServicePacket(JSONObject json) throws JSONException
	{
		return (BasicContextServicePacket) ContextServicePacket.getContextServicePacket(json);
	}
	
	@Override
	public GenericMessagingTask<Integer, ?>[] start() 
	{
		return null;
	}
	
	// public abstract methods
	public abstract GenericMessagingTask<Integer,?>[] handleQueryMsgFromUser(
			ProtocolEvent<ContextServicePacket.PacketType, String> event,
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleValueUpdateFromGNS(
			ProtocolEvent<ContextServicePacket.PacketType, String> event,
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleQueryMesgToSubspaceRegion(
			ProtocolEvent<ContextServicePacket.PacketType, String> event,
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleQueryMesgToSubspaceRegionReply(
			ProtocolEvent<ContextServicePacket.PacketType, String> event,
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleValueUpdateToSubspaceRegionMessage(
			ProtocolEvent<ContextServicePacket.PacketType, String> event,
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleGetMessage(
			ProtocolEvent<ContextServicePacket.PacketType, String> event,
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleGetReplyMessage(
			ProtocolEvent<ContextServicePacket.PacketType, String> event,
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleValueUpdateToSubspaceRegionReplyMessage(
			ProtocolEvent<ContextServicePacket.PacketType, String> event,
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleQueryTriggerMessage(
			ProtocolEvent<ContextServicePacket.PacketType, String> event,
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleUpdateTriggerMessage(
			ProtocolEvent<ContextServicePacket.PacketType, String> event, 
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleUpdateTriggerReply(
			ProtocolEvent<ContextServicePacket.PacketType, String> event, 
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleClientConfigRequest(
			ProtocolEvent<ContextServicePacket.PacketType, String> event, 
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleACLUpdateToSubspaceRegionMessage(
			ProtocolEvent<ContextServicePacket.PacketType, String> event, 
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
	
	public abstract GenericMessagingTask<Integer,?>[] handleACLUpdateToSubspaceRegionReplyMessage(
			ProtocolEvent<ContextServicePacket.PacketType, String> event, 
			ProtocolTask<Integer, ContextServicePacket.PacketType, String>[] ptasks);
}