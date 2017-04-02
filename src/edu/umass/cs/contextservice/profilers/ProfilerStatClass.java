package edu.umass.cs.contextservice.profilers;


public class ProfilerStatClass implements Runnable
{		
	private long incomingUpdateRate					= 0;
	private long incomingSearchRate					= 0;
	
	// indicates the time from when the CNS receives a search query to when it sends a reply
	// back to the client.
	private long sumCNSSearchTime					= 0;
	private long numSearchReqs 						= 0;
	
	// indicates the time from when the CNS receives a update request to when it sends a reply
	// back to the client.
	private long sumCNSUpdateTime					= 0;
	private long numUpdateReqs 						= 0;
	
	private final Object lock 						= new Object();
	
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			if( (numSearchReqs > 0) )
			{	
				System.out.println("numSearchReqs "+numSearchReqs 
									+ " average search resp time "
									+(sumCNSSearchTime/numSearchReqs));
			}
			
			if( (numUpdateReqs > 0) )
			{	
				System.out.println("numUpdateReqs "+numUpdateReqs 
								+" average update resp time "
								+(sumCNSUpdateTime/numUpdateReqs));
			}
			
			double updrate = (incomingUpdateRate*1.0)/10.0;
			double searchrate = (incomingSearchRate*1.0)/10.0;
			
			System.out.println("Incoming update rate "+updrate+" requests/s"
								+ " search rate "+searchrate);
			
			synchronized(lock)
			{
				incomingSearchRate = 0;
				incomingUpdateRate = 0;
			}
			
		}
	}
	
	public void addUpdateStats(UpdateStats updateStat)
	{
		synchronized( lock )
		{
			sumCNSUpdateTime = sumCNSUpdateTime + updateStat.getTotalCNSTime();
			numUpdateReqs++;
		}
	}
	
	public void addSearchStats(SearchStats searchStat)
	{
		synchronized( lock )
		{
			sumCNSSearchTime = sumCNSSearchTime + searchStat.getTotalCNSTime();
			numSearchReqs++;
		}
	}
	
	/**
	 * Keeps track of the incoming search rate at the current node.
	 */
	public void incrementIncomingSearchRate()
	{
		synchronized( lock )
		{
			incomingSearchRate++;
		}
	}
	
	/**
	 * Keeps track of the incoming update rate at the node.
	 */
	public void incrementIncomingUpdateRate()
	{
		synchronized( lock )
		{
			incomingUpdateRate++;
		}
	}
	
	public void addSearchQueryProcessTime(long timeTaken, int resultSizePerNode)
	{
	}
}

// TO be deleted
/*public void incrementNumSearches(int currNumNodes, long regionMapTime)
{
	synchronized(lock)
	{
		numNodesForSearchQuery = numNodesForSearchQuery + currNumNodes;
		numSearchReqs++;
		sumRegionMapTime = sumRegionMapTime +regionMapTime;
	}
}

public void incrementNumSearchesAttrIndex(long time)
{
	synchronized(lock)
	{
		this.numSearchInAttrIndex++;
		sumAttrIndexTime = sumAttrIndexTime + time;
	}
}

public void incrementNumUpdatesAttrIndex(long time)
{
	synchronized(lock)
	{
		this.numUpdateInAttrIndex++;
	}
}


public void incrementNumUpdates(int currNumNodes)
{
	synchronized(lock)
	{
		numNodesForUpdateReqs = numNodesForUpdateReqs + currNumNodes;
		numUpdateReqs++;
	}
}


public void incrementIncomingForOverlap()
{
	synchronized( lock )
	{
		incomingORate++;
	}
}


public void incrementIncomingForData()
{
	synchronized( lock )
	{
		incomingDRate++;
	}
}*/