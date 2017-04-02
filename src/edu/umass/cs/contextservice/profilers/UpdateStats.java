package edu.umass.cs.contextservice.profilers;

/**
 * This class stores various Update stats for a single 
 * update request. It helps in profiling various steps in 
 * a single update operation.
 * @author ayadav
 *
 */
public class UpdateStats
{
	// stores the time for the update operation in the CNS, from the time the request
	// arrived and the response was sent. Time is in ms.
	private long totalCNSTime;
	
	// stores the time to determine which regions/nodes this update request should 
	// go to. Time is in ms.
	private long totalRegionCompTime;
	
	private long updateStartTime;
	
	private long getGuidHashIndexTime;
	
	private long hashIndexReadTime;
	
	private long hashIndexWriteTime;
	
	public UpdateStats()
	{
		updateStartTime = System.currentTimeMillis();
	}
	
	public void setHashIndexReadTime(long timeTaken)
	{
		hashIndexReadTime = timeTaken;
	}
	
	public void setHashIndexWriteTime(long timeTaken)
	{
		hashIndexWriteTime = timeTaken;
	}
	
	public void setRegionCompTime(long timeTaken)
	{
		this.totalRegionCompTime = timeTaken;
	}
	
	public void setUpdateFinishTime()
	{
		totalCNSTime = System.currentTimeMillis()-updateStartTime;
	}
	
	public long getTotalCNSTime()
	{
		return totalCNSTime;
	}
}