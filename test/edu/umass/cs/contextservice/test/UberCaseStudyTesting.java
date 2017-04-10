package edu.umass.cs.contextservice.test;

import edu.umass.cs.contextservice.client.ContextServiceClient;
import edu.umass.cs.contextservice.config.ContextServiceConfig;
import edu.umass.cs.contextservice.nodeApp.StartContextServiceNode;

public class UberCaseStudyTesting {
	
	
	public static void main(String[] args) throws Exception
	{
		ContextServiceConfig.configFileDirectory 
					= "conf/uberCaseStudyConf/contextServiceConf";
	
	
		// setting all config parameters for the test.
		ContextServiceConfig.sendFullRepliesToClient 	= true;
		ContextServiceConfig.sendFullRepliesWithinCS 	= true;
		ContextServiceConfig.triggerEnabled 			= false;
		ContextServiceConfig.uniqueGroupGUIDEnabled     = false;
		ContextServiceConfig.privacyEnabled				= false;
		ContextServiceConfig.inMemoryMySQL				= false;
		// false because in experiment mode full triggers are
		// not returned to client so they client doesn't become bottleneck.
		ContextServiceClient.EXPERIMENT_MODE            = false;
	
	
		// start context service.
		startFourNodeSetup();
	}
	
	private static void startFourNodeSetup() throws Exception
	{
		String[] args = new String[4];
		for( int i=0; i<4; i++ )
		{
			args[0] = "-id";
			args[1] = i+"";
			args[2] = "-csConfDir";
			args[3] = ContextServiceConfig.configFileDirectory;
			
			StartContextServiceNode.main(args);
		}
	}
}


