package edu.umass.cs.contextservice.database.datasource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import edu.umass.cs.contextservice.config.ContextServiceConfig;


public class SQLiteDataSource extends AbstractDataSource
{	
    private ComboPooledDataSource searchPool;
    
    public SQLiteDataSource(int myNodeID) throws PropertyVetoException
    {	
    	searchPool = new ComboPooledDataSource();
    	searchPool.setDriverClass("org.sqlite.JDBC"); //loads the jdbc driver
        
    	//searchPool.setJdbcUrl("jdbc:sqlite:file:contextdb"+myNodeID+"?mode=memory&cache=shared");
    	searchPool.setJdbcUrl("jdbc:sqlite:file:contextdb"+myNodeID+"?mode=memory");
    	

    	searchPool.setMaxPoolSize(ContextServiceConfig.mysqlMaxConnections);
    	searchPool.setAutoCommitOnClose(true);
    }

    public Connection getConnection() throws SQLException 
    {
    	return searchPool.getConnection();
    }

	@Override
	public String getCmdLineConnString() 
	{
		return null;
	}
}