package edu.umass.cs.contextservice.regionmapper;

import java.beans.PropertyVetoException;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.umass.cs.contextservice.attributeInfo.AttributeMetaInfo;
import edu.umass.cs.contextservice.common.CSNodeConfig;
import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.contextservice.regionmapper.database.AbstractRegionMappingStorage;
import edu.umass.cs.contextservice.regionmapper.database.SQLRegionMappingStorage;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;
import edu.umass.cs.contextservice.regionmapper.helper.RegionInfo;


/**
 * This class implements a uniform region mapping policy. 
 * 
 * This policy creates regions by keeping the volume of regions 
 * approximately same across the regions. 
 * @author ayadav
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class UniformGreedyRegionMappingPolicyWithDB extends AbstractRegionMappingPolicy
{
	private final HashMap<Integer, RegionInfo> regionMap;
	private final Random randGen;
	
	// attributes are partitioned in this order
	private final List<String> orderedAttrList;
	
	private final AbstractRegionMappingStorage regionMappingStorage;
	
	public UniformGreedyRegionMappingPolicyWithDB( AbstractDataSource dataSource,
			HashMap<String, AttributeMetaInfo> attributeMap, List<String> orderedAttrList,
			CSNodeConfig nodeConfig )
	{
		super(attributeMap, nodeConfig);
		this.orderedAttrList = orderedAttrList;
		regionMap = new HashMap<Integer, RegionInfo>();
		randGen = new Random();
		regionMappingStorage = new SQLRegionMappingStorage(dataSource, attributeMap);
		regionMappingStorage.createTables();
	}
	
	@Override
	public List<Integer> getNodeIDsForUpdate(
			String GUID, HashMap<String, AttributeValueRange> attrValRangeMap)
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
	
	public static void main(String[] args) throws PropertyVetoException
	{
	}
}