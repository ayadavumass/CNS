package edu.umass.cs.contextservice.configurator;


import edu.umass.cs.contextservice.database.datasource.AbstractDataSource;
import edu.umass.cs.nio.interfaces.NodeConfig;

/**
 * Used to configure subspace configuration 
 * based on number of nodes, attributes.
 * It follows sqrt(N) model while configuring subspaces.
 * It partitions each attribute into two partitions at least.
 * It also decides subspace configuration like which attributes 
 * go into which subspaces.
 * @author adipc
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class ReplicatedSubspaceConfigurator extends AbstractSubspaceConfigurator
{
	// each domain is at least partitioned into two.
	
	private final double optimalH;
	public ReplicatedSubspaceConfigurator(NodeConfig<Integer> nodeConfig, 
			int optimalH, AbstractDataSource dataSource)
	{
		super(nodeConfig);
		this.optimalH = optimalH;
	}
	
	@Override
	public void configureSubspaceInfo()
	{
	}
	
	
	// test the above class. 
	public static void main(String[] args)
	{
	}
}