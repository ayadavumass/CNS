package edu.umass.cs.contextservice.config;

/**
 * Context service config file.
 * It contains all configuration parameters.
 */
public class ContextServiceConfig
{
	// properties name, these are read from properties file
	public static final String TRIGGER_ENABLE_STRING				= "triggerEnable";
	
	public static final String REGION_MAPPING_POLICY_STRING			= "regionMappingPolicy";
	public static final String NUM_ATTRS_PER_SUBSPACE_STRING		= "numAttrsPerSubspace";
	public static final String PRIVACY_ENABLED_STRING				= "privacyEnabled";
	public static final String QUERY_ALL_ENABLED_STRING				= "queryAllEnabled";
	public static final String SQL_POOL_SIZE_STRING					= "sqlPoolSize";
	public static final String THREAD_POOL_SIZE_STRING				= "threadPoolSize";
	public static final String BULK_LOADING_ENABLE_STRING			= "enableBulkLoading";
	public static final String BULK_LOADING_FILE_STRING				= "bulkLoadingFile";
	
	// region Mapping policies
	public static final String DEMAND_AWARE							= "DEMAND_AWARE";
	public static final String HYPERDEX								= "HYPERDEX";
	public static final String SQRT_N_HASH							= "SQRT_N_HASH";
	public static final String UNIFORM								= "UNIFORM";
	
	// path where all config files are stored, like node setup, attribute info, subspace info
	public static String configFileDirectory;
	
	// NO_PRIVACY 0 ordinal, HYPERSPACE_PRIVACY 1 ordinal, SUBSPACE_PRIVACY 2 ordinal
	public static enum PrivacySchemes {NO_PRIVACY, PRIVACY};
	
	
	//this flag indicates whether GNS is used or not.
	// In GNSCalls class, it bypasses GNS calls if set to false.
	public static final boolean USE_GNS								= false;
	
	
	public static final boolean PROFILER_THREAD						= true;
	
	// config files
	
	public static final String CS_CONF_FOLDERNAME  					= "contextServiceConf";
	public static final String ATTR_INFO_FILENAME  					= "attributeInfo.txt";
	public static final String NODE_SETUP_FILENAME 					= "contextServiceNodeSetup.txt";
	public static final String DB_SETUP_FILENAME   					= "dbNodeSetup.txt";
	public static final String CS_CONFIG_FILENAME   				= "csConfigFile.txt";
	public static final String REGION_INFO_FILENAME   				= "regionInfoFile.txt";
	
	//control if full guids are sent in the search query
	// reply, if false only sends the number of guids, not
	// the actual guids
	public static  boolean sendFullRepliesWithinCS					= false;
	
	// to check which one is bottleneck the client or CS in full replies. 
	public static  boolean sendFullRepliesToClient					= false;
	
	// if this is set to true, then mysql table selects
	// return results row by row. If set to false then
	// default mysql select semantics is used which fetches all
	// results in memory on a select, but on large result sizes can cause
	// memory overflow.
	public static final boolean ROW_BY_ROW_FETCHING_ENABLED			= true;
	
	
	// fetches only count of the result, select query is count(GUID)
	// used for debugging and also for results until we increse mysql default 
	// buffers.
	public static final boolean ONLY_RESULT_COUNT_ENABLE			= false;
	
	//if false, replies for any update messages will not be sent
	// just for measuring update throughout and time in experiments
	//public static final boolean sendUpdateReplies					= true;
	
	// if true group update trigger is enabled, not enabled if false
	public static boolean triggerEnabled							= false;
	
	
	// if set to true then there is a primary node for each groupGUID
	// and search always gores through that and doesn't update trigger info if its is repeated.
	public static boolean uniqueGroupGUIDEnabled					= false;
	// GroupGUID, UserIP and UserPort are primaries keys if this option is set false.
	// if set true then those are just hash index.
	public static boolean disableUniqueQueryStorage					= true;
	
	
	// circular query triggers makes the select queries 
	// very complicated, so there is an option to enable disable it.
	public static boolean disableCircularQueryTrigger				= false;
	
	
	
	// if set to true basic subspace config is enabled.
	//public static boolean basicSubspaceConfig						= false;
	
    // on d710 cluster 150 gives the best performance, after that performance remains same.
    // should be at least same as the hyperspace hashing pool size.
    // actually default mysql server max connection is 151. So this should be
    // set in conjuction with that. and also the hyperpsace hashing thread pool
    // size should be set greater than that. These things affect system performance a lot.
	// change back to 214 for experiments.
	public static  int mysqlMaxConnections							= 10;
	
	// it is also important to set this at least the size of the database connection pool.
	public static int threadPoolSize								= 10;
	
	// mysql result cursor fetches 1 guid at once and stores in memory
	// need this becuase otherwise in large guids case , all the result 
	// is stored in memory by default.
	//FIXED 1 below is wrong it should be Integer.MIN_VALUE , http://stackoverflow.com/questions/3443937/java-heap-memory-error
	public static final int MYSQL_CURSOR_FETCH_SIZE					= Integer.MIN_VALUE;
	
	
	// this gives minimum of 2^10 subspace partitions if there are 10 
	// attributes and each parititioned twice. 
	public static final int MAXIMUM_NUM_ATTRS_IN_SUBSPACE			= 10;
	
	
	public static boolean privacyEnabled							= false;
	
	
	
	public static boolean queryAllEnabled							= false;
	
	public static String regionMappingPolicy						= UNIFORM;
	
	
	// if this is set to true then the context service client will do decryptions 
	// on receiving the search reply.
	public static final boolean DECRYPTIONS_ON_SEARCH_REPLY_ENABLED	= true;
	
	// if true, no encryption happens. cipher text and plain text are same.
	public static final boolean NO_ENCRYPTION						= false;
	
	// if set to true then anonymized IDs will have random attr-val pairs.
	// only for testing.
	public static final boolean RAND_VAL_JSON						= false;
	
	
	// 20 bytes
	public static final int SIZE_OF_ANONYMIZED_ID					= 20;
	
	
	public static PrivacySchemes privacyScheme						= PrivacySchemes.NO_PRIVACY;
	
	
	public static final String ASSYMETRIC_ENC_ALGORITHM				= "RSA";
	public static final String SYMMETRIC_ENC_ALGORITHM				= "DES";
	public static final String STRING_ENCODING						= "UTF-8";
	
	// if true some debugging information will be computed and printed.
	public static final boolean DEBUG_MODE							= false;
	
	
	// numAttrsPerSubspace used in HyperDex
	public static double numAttrsPerSubspace						= 2.0;
	
	// security things
	public static final int KEY_LENGTH_SIZE							= 1024;
	
	
	// current encryption generated 128 bytes, if that changes then this has to change.
	//public static final int REAL_ID_ENCRYPTION_SIZE				= 128;
	// symmetric key of encryption of GUID gives 24 bytes of encrypted data.
	// FIXME: TEXT and BLOB is not supported in in-memory Mysql.
	// So, for Sigcomm deadline experiments just creating a harcoded 
	// column in mysql table of 24 bytes to store anonymizedIDToGUID mapping information.
	// will figure out later on a better way.
	// 4+ 24 bytes  First 4 bytes are integer indicating each encryption byte [] len in bytes.
	public static final int GUID_SYMM_KEY_ENC_LENGTH				= 28;
	
	
	// maximum length of an attribute name, used in varchar mysql table
	public static final int MAXIMUM_ATTRNAME_LENGTH					= 100;
	
	
	// if set to true then all mysql tables are created in in-memory.
	// used when virtualized nodes are used.
	public static boolean inMemoryMySQL								= false;
	
	
	// if true then mysql NULL is stored for unspecified
	// attributes to save space.
	public static final boolean NULL_DEFAULT_ENABLED				= true;
	
	// SQL database types
	public static enum SQL_DB_TYPE	{MYSQL, SQLITE};
	
	public static SQL_DB_TYPE sqlDBType								= SQL_DB_TYPE.MYSQL;
	
	public static final String REGION_INFO_TABLE_NAME 				= "regionInfoStorageTable";
	
	
	// if true CNS writes search and update traces of requests it receives.
	// we use traces in demand aware region creation.
	public static final boolean SEARCH_UPDATE_TRACE_ENABLE			= false;
	
	
	// used in case study to not to make client as bootllneck
	public static final boolean LIMITED_SEARCH_REPLY_ENABLE			= true;
	public static final int LIMITED_SEARCH_REPLY_SIZE				= 100;
	
	public static final String HASH_INDEX_FILE_PREFIX				= "hashIndexGuidsNodeId";
	public static final String ATTR_INDEX_FILE_PREFIX				= "attrIndexGuidsNodeId";
	public static final String BULK_LOAD_FILE						= "bulkLoadForNodeId";
	
	
	public static boolean enableBulkLoading							= false;
	public static String bulkLoadingFilePath						= "";
	
	// only set to true in running tests.
	// otherwise db should be dropped manually external to CNS
	public static boolean dropLocalDB								= false;
}