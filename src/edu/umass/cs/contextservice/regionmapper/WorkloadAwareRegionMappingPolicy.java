package edu.umass.cs.contextservice.regionmapper;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.umass.cs.contextservice.attributeInfo.AttributeMetaInfo;
import edu.umass.cs.contextservice.common.CSNodeConfig;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;
import edu.umass.cs.contextservice.regionmapper.helper.RegionInfo;

/**
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class WorkloadAwareRegionMappingPolicy extends AbstractRegionMappingPolicy
{
	public static final double rho									= 0.5;
	// hyperplane moves with 10% of the total interval.
	private static final double PLANE_MOVING_PERCENTAGE				= 0.1;
	
	private static final String SEARCH_TRACE_FILE					= "traces/guassianTrace/searchFile.txt";
	private static final String UPDATE_TRACE_FILE					= "traces/guassianTrace/updateFile.txt";
	
	// this field is only for testing and will be removed later.
	//private static final double NUM_SEARCH_QUERIES				= 1000.0;
	// we create regions such that 0.98 threshold is achieved.
	//private static final double JAINS_FAIRNESS_THRESHOLD			= 0.90;
	private final LinkedList<RegionInfo> regionList;
	
	
	public WorkloadAwareRegionMappingPolicy(HashMap<String, AttributeMetaInfo> attributeMap, 
			CSNodeConfig nodeConfig)
	{
		super(attributeMap, nodeConfig);
		regionList = new LinkedList<RegionInfo>();
	}
	
	
	@Override
	public List<Integer> getNodeIDsForUpdate(
			String GUID, HashMap<String, AttributeValueRange> attrValRangeMap ) 
	{
		return null;
	}
	
	@Override
	public List<Integer> getNodeIDsForSearch
			(HashMap<String, AttributeValueRange> attrValRangeMap) 
	{
		return null;
	}
	
	
	@Override
	public void computeRegionMapping()
	{
	}
	
	public static void main(String[] args)
	{
	}	
}