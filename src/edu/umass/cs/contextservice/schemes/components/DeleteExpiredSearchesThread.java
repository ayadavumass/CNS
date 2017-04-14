package edu.umass.cs.contextservice.schemes.components;


import edu.umass.cs.contextservice.database.AbstractDataStorageDB;
import edu.umass.cs.contextservice.logging.ContextServiceLogger;

/**
 * 
 * @author ayadav
 *
 */
//suppressing the warnings for unused fields as the design methods are empty.
@SuppressWarnings("unused")
public class DeleteExpiredSearchesThread implements Runnable
{
	private final AbstractDataStorageDB dataStorageDB;
	
	public DeleteExpiredSearchesThread(AbstractDataStorageDB dataStorageDB)
	{
		this.dataStorageDB = dataStorageDB;
	}
	
	@Override
	public void run() 
	{
		while( true )
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
			//TODO: the function to delete expired search queries.
		}
	}
}