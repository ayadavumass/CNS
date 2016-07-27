package edu.umass.cs.contextservice.database.mysqlpool;

public enum DatabaseRequestTypes
{
	// 
	OVERLAPPING_REGION_SUBSPACE,
	//
	OVERLAPPING_PARTITIONS_IN_TRIGGERS,
	//
	SEARCH_QUERY_IN_SUBSPACE_REGION,
	//
	INSERT_SUBSPACE_PARTITION_INFO,
	//
	INSERT_TRIGGER_PARITION_INFO,
	//
	GUID_GET_PRIMARY_SUBSPACE,
	//
	INSERT_SUBSPACE_TRIGGER_DATA_INFO,
	//
	GET_TRIGGER_DATAINFO,
	//
	DELETE_EXPIRED_SEARCH_QUERIES,
	//
	STORE_GUID_PRIMARY_SUBSPACE,
	//
	DELETE_GUID_FROM_SUBSPACE_REGION,
	//
	GET_SEARCH_QUERY_RECORD_FROM_PRIMARY_TRIGGER_SUBSPACE;
}