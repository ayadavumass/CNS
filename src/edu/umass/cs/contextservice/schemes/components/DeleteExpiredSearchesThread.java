package edu.umass.cs.contextservice.schemes.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.umass.cs.contextservice.database.HyperspaceDB;
import edu.umass.cs.contextservice.hyperspace.storage.SubspaceInfo;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;

public class DeleteExpiredSearchesThread implements Runnable
{
	private final Integer myNodeId;
	private final HashMap<Integer, Vector<SubspaceInfo>> subspaceInfoMap;
	private final HyperspaceDB hyperspaceDB;
	
	public DeleteExpiredSearchesThread(HashMap<Integer, Vector<SubspaceInfo>> subspaceInfoMap ,
			Integer myNodeID, HyperspaceDB hyperspaceDB)
	{
		this.myNodeId = myNodeID;
		this.subspaceInfoMap = subspaceInfoMap;
		this.hyperspaceDB = hyperspaceDB;
	}
	
	@Override
	public void run() 
	{
		while( true )
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}					
			
			Iterator<Integer> subapceIdIter = subspaceInfoMap.keySet().iterator();
			while(subapceIdIter.hasNext())
			{
				int subspaceId = subapceIdIter.next();
				// at least one replica and all replica have same default value for each attribute.
				Vector<SubspaceInfo> subspaceInfoVect 
									= subspaceInfoMap.get(subspaceId);
				
				for(int i=0; i<subspaceInfoVect.size(); i++)
				{
					SubspaceInfo currSubspaceInfo 
										= subspaceInfoVect.get(i);
					
					int replicaNum = currSubspaceInfo.getReplicaNum();
					
					if( currSubspaceInfo.checkIfSubspaceHasMyID(myNodeId)
							 )
					{
						int numDeleted = hyperspaceDB.deleteExpiredSearchQueries
								(subspaceId);
								if(numDeleted > 0)
									ContextServiceLogger.getLogger().fine( "Group guids deleted "
										+ " for subspaceId "+subspaceId+" replicaNum "+replicaNum+
										" numDeleted "+numDeleted );
					}
				}
			}
		}
	}
}