package edu.umass.cs.contextservice.database.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import com.mchange.v2.c3p0.ComboPooledDataSource;


import edu.umass.cs.contextservice.profilers.CNSProfiler;

/**
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class MySQLDataSource extends AbstractDataSource
{
	private final HashMap<Integer, SQLNodeInfo> sqlNodeInfoMap;
	
    private ComboPooledDataSource dataSource;
    private final int myNodeId;
    
    private final CNSProfiler profStats;
    
    
    public MySQLDataSource(Integer myNodeID, CNSProfiler profStats)  
    {
    	myNodeId = myNodeID;
    	this.profStats = profStats;
    	sqlNodeInfoMap = null;
    }

    public Connection getConnection() throws SQLException 
    {
    	return null;
    }
    
    
    @Override
	public String getCmdLineConnString() 
    {
    	String str = "mysql --"+sqlNodeInfoMap.get(myNodeId).arguments+" -u "+
    			sqlNodeInfoMap.get(myNodeId).username+" --password="+sqlNodeInfoMap.get(myNodeId).password;
    			
		return str;
	}
    
    public void dropLocalDB()
    {
    }
	
	private class SQLNodeInfo
	{
		public int portNum;
		public String databaseName;
		public String username;
		public String password;
		public String arguments;	
	}
}