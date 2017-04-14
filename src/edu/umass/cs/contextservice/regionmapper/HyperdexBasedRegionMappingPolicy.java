package edu.umass.cs.contextservice.regionmapper;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.List;

import edu.umass.cs.contextservice.attributeInfo.AttributeMetaInfo;
import edu.umass.cs.contextservice.common.CSNodeConfig;
import edu.umass.cs.contextservice.configurator.AbstractSubspaceConfigurator;
import edu.umass.cs.contextservice.configurator.BasicSubspaceConfigurator;
import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.contextservice.regionmapper.database.AbstractRegionMappingStorage;
import edu.umass.cs.contextservice.regionmapper.database.HyperdexSQLRegionMappingStorage;
import edu.umass.cs.contextservice.regionmapper.helper.AttributeValueRange;


/**
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class HyperdexBasedRegionMappingPolicy extends AbstractRegionMappingPolicy
{
	private final AbstractSubspaceConfigurator subspaceConfigurator;
	private final AbstractRegionMappingStorage regionMappingStorage;
	
	
	public HyperdexBasedRegionMappingPolicy( HashMap<String, AttributeMetaInfo> attributeMap, 
			CSNodeConfig csNodeConfig, int numberAttrsPerSubspace, AbstractDataSource dataSource )
	{
		super(attributeMap, csNodeConfig);
		
		subspaceConfigurator 
			= new BasicSubspaceConfigurator(csNodeConfig, numberAttrsPerSubspace);
		
		regionMappingStorage = new HyperdexSQLRegionMappingStorage(dataSource, 
				subspaceConfigurator.getSubspaceInfoMap() );
	}
	

	@Override
	public void computeRegionMapping() 
	{
	}
	
	
	public List<Integer> getNodeIDsForUpdate
			(String GUID, HashMap<String, AttributeValueRange> attrValRangeMap)
	{
		return null;
	}
	
	public List<Integer> getNodeIDsForSearch
							(HashMap<String, AttributeValueRange> attrValRangeMap)
	{	
		return null;
	}
	
	/**
	 * recursive function to generate all the
	 * subspace regions/partitions.
	 */
	public void generateAndStoreSubspaceRegions()
	{
	}
	
	public static void main(String[] args) throws PropertyVetoException
	{
	}
}
