package edu.umass.cs.contextservice.regionmapper;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.umass.cs.contextservice.attributeInfo.AttributeMetaInfo;
import edu.umass.cs.contextservice.attributeInfo.AttributeTypes;
import edu.umass.cs.contextservice.common.CSNodeConfig;
import edu.umass.cs.contextservice.config.ContextServiceConfig;
import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.contextservice.database.datasource.SQLiteDataSource;
import edu.umass.cs.contextservice.regionmapper.database.AbstractRegionMappingStorage;
import edu.umass.cs.contextservice.regionmapper.database.SQLRegionMappingStorage;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;
import edu.umass.cs.contextservice.regionmapper.helper.RegionInfo;
import edu.umass.cs.contextservice.regionmapper.helper.ValueSpaceInfo;

/**
 * This policy reads regions from a file. 
 * The file should be in location ContextServiceConfig.configFileDirectory+
				"/"+ContextServiceConfig.REGION_INFO_FILENAME along with other 
				node , db and csConfig files.			
 * @author ayadav
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class FileBasedRegionMappingPolicyWithDB extends AbstractRegionMappingPolicy
{
	private final HashMap<Integer, RegionInfo> regionMap;
	private final Random randGen;
	
	private final AbstractRegionMappingStorage regionMappingStorage;
	
	public FileBasedRegionMappingPolicyWithDB( AbstractDataSource dataSource,
			HashMap<String, AttributeMetaInfo> attributeMap, 
			CSNodeConfig nodeConfig )
	{
		super(attributeMap, nodeConfig);
		regionMap = new HashMap<Integer, RegionInfo>();
		randGen = new Random();
		regionMappingStorage = new SQLRegionMappingStorage(dataSource, attributeMap);
		
		regionMappingStorage.createTables();
	}
	
	@Override
	public List<Integer> getNodeIDsForSearch(HashMap<String, AttributeValueRange> 
										attrValRangeMap)
	{
		return null;
	}
	
	@Override
	public List<Integer> getNodeIDsForUpdate(
			String GUID, HashMap<String, AttributeValueRange> attrValRangeMap)
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