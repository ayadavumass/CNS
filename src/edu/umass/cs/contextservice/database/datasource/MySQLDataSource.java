package edu.umass.cs.contextservice.database.datasource;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import edu.umass.cs.contextservice.config.ContextServiceConfig;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;

public class MySQLDataSource extends AbstractDataSource
{
	private final HashMap<Integer, SQLNodeInfo> sqlNodeInfoMap;
	
    private ComboPooledDataSource dataSource;
	//private BasicDataSource dataSource;
    private final int myNodeId;

    // Commented to test which connection pool is better, apache dbcp or ComboPooledDataSource
    public MySQLDataSource(Integer myNodeID) throws IOException, SQLException, PropertyVetoException 
    {
    	myNodeId = myNodeID;
    	this.sqlNodeInfoMap = new HashMap<Integer, SQLNodeInfo>();
    	readDBNodeSetup();
    	
    	int portNum = sqlNodeInfoMap.get(myNodeID).portNum;
    	String dbName = sqlNodeInfoMap.get(myNodeID).databaseName;
    	String username = sqlNodeInfoMap.get(myNodeID).username;
    	String password = sqlNodeInfoMap.get(myNodeID).password;
    	String arguments = sqlNodeInfoMap.get(myNodeID).arguments;
    	
    	dropDB(sqlNodeInfoMap.get(myNodeID));
    	createDB(sqlNodeInfoMap.get(myNodeID));
    	
    	
    	dataSource = new ComboPooledDataSource();
    	dataSource.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
        if(arguments.length() > 0)
        {
        	dataSource.setJdbcUrl("jdbc:mysql://localhost:"+portNum+"/"+dbName+"?"+arguments);
        }
        else
        {
        	dataSource.setJdbcUrl("jdbc:mysql://localhost:"+portNum+"/"+dbName);
        }
        
        
        dataSource.setUser(username);
        dataSource.setPassword(password);

        //the settings below are optional -- c3p0 can work with defaults
        //cpds.setMinPoolSize(5);
        //cpds.setAcquireIncrement(5);
        // NOTE: max pool size of DB affects the performance alot
        // definitely set it, don't leave it for default. default gives very bad
        // update performance. 
        // TODO: need to find its optimal value.
        // on d710 cluster 150 gives the best performance, after that performance remains same.
        // should be at least same as the hyperspace hashing pool size.
        // actually default mysql server max connection is 151. So this should be
        // set in conjuction with that. and also the hyperpsace hashing thread pool
        // size should be set greater than that. These things affect system performance a lot.
        dataSource.setMinPoolSize(ContextServiceConfig.MYSQL_MAX_CONNECTIONS);
        dataSource.setMaxPoolSize(ContextServiceConfig.MYSQL_MAX_CONNECTIONS);
        System.out.println("Max idle time before "+dataSource.getMaxIdleTime());
        
        //dataSource.setMaxIdleTime(0);
        
        dataSource.setAutoCommitOnClose(false);
        
        //cpds.setMaxStatements(180);
        ContextServiceLogger.getLogger().fine("HyperspaceMySQLDB datasource "
        		+ "max pool size "+dataSource.getMaxPoolSize());
    }
    
    /*public MySQLDataSource(Integer myNodeID) 
    			throws IOException, SQLException, PropertyVetoException 
    {
    	myNodeId = myNodeID;
    	this.sqlNodeInfoMap = new HashMap<Integer, SQLNodeInfo>();
    	readDBNodeSetup();
    	
    	int portNum = sqlNodeInfoMap.get(myNodeID).portNum;
    	String dbName = sqlNodeInfoMap.get(myNodeID).databaseName;
    	String username = sqlNodeInfoMap.get(myNodeID).username;
    	String password = sqlNodeInfoMap.get(myNodeID).password;
    	String arguments = sqlNodeInfoMap.get(myNodeID).arguments;
    	
    	dropDB(sqlNodeInfoMap.get(myNodeID));
    	createDB(sqlNodeInfoMap.get(myNodeID));
    	
    	dataSource = new BasicDataSource();
        //cpds = new ComboPooledDataSource();
    	//dataSource.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
        if(arguments.length() > 0)
        {
        	dataSource.setUrl("jdbc:mysql://localhost:"+portNum+"/"+dbName+"?"+arguments);
        }
        else
        {
        	dataSource.setUrl("jdbc:mysql://localhost:"+portNum+"/"+dbName);
        }
        
        
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        //the settings below are optional -- c3p0 can work with defaults
        //cpds.setMinPoolSize(5);
        //cpds.setAcquireIncrement(5);
        // NOTE: max pool size of DB affects the performance alot
        // definitely set it, don't leave it for default. default gives very bad
        // update performance. 
        // TODO: need to find its optimal value.
        // on d710 cluster 150 gives the best performance, after that performance remains same.
        // should be at least same as the hyperspace hashing pool size.
        // actually default mysql server max connection is 151. So this should be
        // set in conjuction with that. and also the hyperpsace hashing thread pool
        // size should be set greater than that. These things affect system performance a lot.
       
        dataSource.setMaxTotal(ContextServiceConfig.MYSQL_MAX_CONNECTIONS);
        dataSource.setMaxIdle(ContextServiceConfig.MYSQL_MAX_CONNECTIONS);
        
        dataSource.setEnableAutoCommitOnReturn(false);
    }*/
    

    public Connection getConnection(DB_REQUEST_TYPE dbReqType) throws SQLException 
    {
    	long start = System.currentTimeMillis();
    	Connection conn = this.dataSource.getConnection();
    	if(ContextServiceConfig.DEBUG_MODE)
    	{
    		System.out.println("TIME_DEBUG getConnection "
    							+(System.currentTimeMillis()-start));	
    	}
        return conn;
    }
    
    @Override
	public String getCmdLineConnString() 
    {
    	String str = "mysql --"+sqlNodeInfoMap.get(myNodeId).arguments+" -u "+
    			sqlNodeInfoMap.get(myNodeId).username+" --password="+sqlNodeInfoMap.get(myNodeId).password;
    			
		return str;
	}
    
	private void readDBNodeSetup() throws NumberFormatException, IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(
				ContextServiceConfig.configFileDirectory+"/"+ContextServiceConfig.DB_SETUP_FILENAME));
		String line = null;
		
		while ( (line = reader.readLine()) != null )
		{
			// ignore comments
			if(line.startsWith("#"))
				continue;
			
			String [] parsed = line.split(" ");
			Integer readNodeId = Integer.parseInt(parsed[0]);
			int readPort = Integer.parseInt(parsed[1]);
			String dbName = parsed[2];
			String username = parsed[3];
			String password = parsed[4];
			String arguments = "";
			
			if(parsed.length == 6)
			{
				arguments = parsed[5];
			}
			
			SQLNodeInfo sqlNodeInfo = new SQLNodeInfo();
			
			sqlNodeInfo.portNum = readPort;
			sqlNodeInfo.databaseName = dbName;
			sqlNodeInfo.username = username;
			sqlNodeInfo.password = password;
			sqlNodeInfo.arguments = arguments;
			
					
			this.sqlNodeInfoMap.put((Integer)readNodeId, sqlNodeInfo);
			
			//csNodeConfig.add(readNodeId, new InetSocketAddress(readIPAddress, readPort));
		}
		reader.close();
	}
	
	private void createDB(SQLNodeInfo sqlInfo)
    {
    	Connection conn = null;
    	Statement stmt = null;
    	try
    	{
    		Class.forName("com.mysql.jdbc.Driver");
    		
    		String jdbcURL = "";
    		if(sqlInfo.arguments.length() > 0)
    		{
    			jdbcURL = "jdbc:mysql://localhost:"+sqlInfo.portNum+"?"+sqlInfo.arguments;
    		}
    		else
    		{
    			jdbcURL = "jdbc:mysql://localhost:"+sqlInfo.portNum;
    		}
    		
    	    conn = DriverManager.getConnection(jdbcURL, sqlInfo.username, sqlInfo.password);

		    stmt = conn.createStatement();
		    
		    String sql = "CREATE DATABASE "+sqlInfo.databaseName;
		    stmt.executeUpdate(sql);
		    
    	}
    	catch(SQLException se)
    	{
    		se.printStackTrace();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    	    try
    	    {
    	    	if(stmt!=null)
    	    		stmt.close();
    	    }
    	    catch(SQLException se2)
    	    {
    	    	se2.printStackTrace();
    	    }
    	    try
    	    {
    	    	if(conn!=null)
    	    		conn.close();
    	    }
    	    catch(SQLException se)
    	    {
    	    	se.printStackTrace();
    	    }
    	}
    }
    
    
    private void dropDB(SQLNodeInfo sqlInfo)
    {
    	Connection conn = null;
    	Statement stmt = null;
    	try
    	{
    		Class.forName("com.mysql.jdbc.Driver");
    		
    		String jdbcURL = "";
    		if(sqlInfo.arguments.length() > 0)
    		{
    			jdbcURL = "jdbc:mysql://localhost:"+sqlInfo.portNum+"?"+sqlInfo.arguments;
    		}
    		else
    		{
    			jdbcURL = "jdbc:mysql://localhost:"+sqlInfo.portNum;
    		}
    		
    	    conn = DriverManager.getConnection(jdbcURL, sqlInfo.username, sqlInfo.password);
    	    
		    stmt = conn.createStatement();
		    String sql = "drop DATABASE "+sqlInfo.databaseName;
		    stmt.executeUpdate(sql);
    	}
    	catch(SQLException se)
    	{
    		ContextServiceLogger.getLogger().info("Couldn't drop the database "+sqlInfo.databaseName);
    	    se.printStackTrace();
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		//finally block used to close resources
    		try
    		{
    			if(stmt!=null)
    				stmt.close();
    	    }
    		catch(SQLException se2)
    		{
    			se2.printStackTrace();
    		}
    		
    		try
    		{
    			if(conn!=null)
    				conn.close();
    	    }
    		catch(SQLException se)
    		{
    	    	se.printStackTrace();
    	    }
    	}
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