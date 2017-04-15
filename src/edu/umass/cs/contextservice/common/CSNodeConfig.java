package edu.umass.cs.contextservice.common;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.nio.interfaces.NodeConfig;

/**
 * @author ayadav
 *
 * @param <Integer>
 */
public class CSNodeConfig implements NodeConfig<Integer> 
{
	private boolean local = false;
	private HashMap<Integer,InetSocketAddress> nmap 
							= new HashMap<Integer,InetSocketAddress>();
	private int defaultPort=2000;
	
	public CSNodeConfig(int dp)
	{
		defaultPort = dp;
	}
	
	public CSNodeConfig() {}
	
	public void localSetup(Set<Integer> members)
	{
		local = true;
		for(Integer i : members)
		{
			this.add(i, getLocalAddress());
		}
	}
	
	/* The caller can either specify the number of nodes, nNodes,
	 * or specify a set of integer node IDs explicitly. In the former
	 * case, nNodes from 0 to nNodes-1 will the node IDs. In the 
	 * latter case, the explicit set of node IDs will be used.
	 */
	public void localSetup(Integer nNodes) 
	{
		local = true;
		for(Integer i=0; i<nNodes; i++)
		{
			this.add(i, getLocalAddress());
		}
	}
	
	private InetAddress getLocalAddress()
	{
		InetAddress localAddr=null;
		try 
		{
			localAddr = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		return localAddr;
	}

	@Override
	public boolean nodeExists(Integer ID) 
	{
		return nmap.containsKey(ID);
	}

	@Override
	public Set<Integer> getNodeIDs() 
	{
		//throw  new UnsupportedOperationException();
		return this.nmap.keySet();
	}

	@Override
	public InetAddress getNodeAddress(Integer ID) 
	{
		InetSocketAddress addr = nmap.get(ID);
		return addr!=null ? addr.getAddress() : (local ? getLocalAddress() : null);
	}

	public int getNodePort(Integer ID) 
	{
		int maxPort = 65536;
		int port = (defaultPort + ID.hashCode()) % maxPort;
		if(port < 0) port = (port + maxPort) % maxPort;
		//return port;
		
		InetSocketAddress addr = nmap.get(ID);
		return addr!=null ? addr.getPort() : port;
	}

	public Set<Integer> getNodes()
	{
		return nmap.keySet();
	}
	
	public void add(Integer id, InetAddress IP)
	{
		nmap.put(id, new InetSocketAddress(IP, defaultPort));
	}
	
	
	public void add(Integer id, InetSocketAddress socketAddr) 
	{
		nmap.put(id, socketAddr);
	}
	
	public void addLocal(Integer id) 
	{
		local = true;
		nmap.put(id, new InetSocketAddress(getLocalAddress(), defaultPort));
	}

	public String toString()
	{
		String s="";
		for(Integer i : nmap.keySet())
		{
			s += i + " : " + getNodeAddress(i) + ":" + getNodePort(i) + "\n";
		}
		return s;
	}

	public static void main(String[] args)
	{
		int dp = (args.length>0 ? Integer.valueOf(args[0]) : 2222);
		CSNodeConfig snc = new CSNodeConfig(dp);
		
		ContextServiceLogger.getLogger().fine("Adding node 0, printing nodes 0 and 1");
		try
		{
			snc.add(0, InetAddress.getByName("localhost"));
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		
		ContextServiceLogger.getLogger().fine("0 : " + snc.getNodeAddress(0) + ":" + snc.getNodePort(0));
		ContextServiceLogger.getLogger().fine("1 : " + snc.getNodeAddress(1) + ":" + snc.getNodePort(1));
	}
	
	@Override
	public Set<Integer> getValuesFromJSONArray(JSONArray arg0)
			throws JSONException 
	{
		return null;
	}
	
	@Override
	public Set<Integer> getValuesFromStringSet(Set<String> arg0) 
	{
		return null;
	}
	
	@Override
	public Integer valueOf(String arg0)
	{
		return null;
	}
	
	@Override
	public InetAddress getBindAddress(Integer myID) 
	{
		return nmap.get(myID).getAddress();
	}
}