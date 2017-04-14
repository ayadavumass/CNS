package edu.umass.cs.contextservice.configurator;

import edu.umass.cs.nio.interfaces.NodeConfig;

/**
 * Basic subspace configurator partitions attributes into
 * subspaces and assigns each subspace to all available nodes.
 * @author adipc
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class BasicSubspaceConfigurator
					extends AbstractSubspaceConfigurator
{
	private final double numAttrsPerSubspace;
	
	public BasicSubspaceConfigurator( NodeConfig<Integer> nodeConfig, int numAttrsPerSubspace)
	{
		super(nodeConfig);
		this.numAttrsPerSubspace = numAttrsPerSubspace;
	}
	
	@Override
	public void configureSubspaceInfo()
	{
	}
	
	public static void main(String[] args)
	{
	}
}