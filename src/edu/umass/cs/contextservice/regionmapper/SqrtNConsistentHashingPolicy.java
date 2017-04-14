package edu.umass.cs.contextservice.regionmapper;


import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.umass.cs.contextservice.attributeInfo.AttributeMetaInfo;
import edu.umass.cs.contextservice.common.CSNodeConfig;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;

/**
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class SqrtNConsistentHashingPolicy extends AbstractRegionMappingPolicy
{
	private HashMap<Integer, List<Integer>> nodePartitionMap;
	private final Random rand;
	private final int numPartitions;
	
	public SqrtNConsistentHashingPolicy(HashMap<String, AttributeMetaInfo> attributeMap, 
									CSNodeConfig nodeConfig) 
	{
		super(attributeMap, nodeConfig);
		
		nodePartitionMap = new HashMap<Integer, List<Integer>>();
		rand = new Random();
		numPartitions = (int) Math.floor(Math.sqrt(nodeConfig.getNodes().size()));
	}

	@Override
	public List<Integer> getNodeIDsForSearch(HashMap<String, AttributeValueRange> attrValRangeMap) 
	{
		return null;
	}

	@Override
	public List<Integer> getNodeIDsForUpdate(String GUID, 
			HashMap<String, AttributeValueRange> attrValRangeMap) 
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