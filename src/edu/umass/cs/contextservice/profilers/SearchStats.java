package edu.umass.cs.contextservice.profilers;

/**
 * This class stores various search stats for a single search operation.
 * It helps in profiling various steps in a single search operation.
 * @author ayadav
 */
public class SearchStats
{
	// stores the time for the update operation in the CNS, from the time the request
	// arrived and the response was sent. Time is in ms.
	private long totalCNSTime;
		
	// stores the time to determine which regions/nodes this update request should 
	// go to. Time is in ms.
	private long totalRegionCompTime;
	
	// timestamp when the search query arrived at the CNS.
	private long queryStartTime ;
	
	private int numNodesForProcessing;
	
	public SearchStats()
	{
		queryStartTime = System.currentTimeMillis();
	}
	
	public void setNodeInfoAndTime(int numNodes, long totalRegionCompTime)
	{
		this.numNodesForProcessing = numNodes;
		this.totalRegionCompTime = totalRegionCompTime;
	}
	
	public void setQueryEndTime()
	{
		totalCNSTime = System.currentTimeMillis() - queryStartTime;
	}
	
	public long getTotalCNSTime()
	{
		return totalCNSTime;
	}
	
	public int getNumNodesProcessing()
	{
		return numNodesForProcessing;
	}
	
	public long getRegionMappingCompTime()
	{
		return totalRegionCompTime;
	}
}