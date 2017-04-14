package edu.umass.cs.contextservice.nodeApp;


import java.io.IOException;
import java.net.UnknownHostException;


import org.apache.commons.cli.ParseException;

import edu.umass.cs.contextservice.ContextServiceNode;
import edu.umass.cs.contextservice.common.CSNodeConfig;


/**
 * This is the entry point for the CNS code. 
 * Calling main method in this class starts CNS on a node.
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class StartContextServiceNode extends ContextServiceNode
{
	public static final int HYPERSPACE_HASHING							= 1;
	
	private static CSNodeConfig csNodeConfig							= null;
	
	public StartContextServiceNode(Integer id, CSNodeConfig nc)
			throws Exception
	{
		super(id, nc);
	}
	
	/**
	 * Entry point to CNS.
	 * Reads configuration files and sets config variables.
	 * Makes object of this class to start CNS. 
	 * @param args
	 * @throws NumberFormatException
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws NumberFormatException, 
									UnknownHostException, IOException, ParseException
	{
	}
}