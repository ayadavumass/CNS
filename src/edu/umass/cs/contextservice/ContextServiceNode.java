package edu.umass.cs.contextservice;

import edu.umass.cs.contextservice.common.CSNodeConfig;
import edu.umass.cs.contextservice.schemes.AbstractScheme;

/**
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public abstract class ContextServiceNode
{
	protected final Integer myID;
	protected final AbstractScheme cnsScheme;
	private boolean started = false;
	
	private final Object startMonitor = new Object();
	
	public ContextServiceNode(Integer id, CSNodeConfig nc) throws Exception
	{
		this.myID = id;
		cnsScheme = null;
	}
	
	/**
	 * returns the context service
	 * @return
	 */
	public AbstractScheme getContextService()
	{
		return this.cnsScheme;
	}
	
	/**
	 * waits till the current node has started
	 */
	public void waitToFinishStart()
	{
	}
}