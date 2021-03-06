package edu.umass.cs.contextservice.client.callback.implementations;

import edu.umass.cs.contextservice.client.callback.interfaces.CallBackInterface;
import edu.umass.cs.contextservice.client.callback.interfaces.UpdateReplyInterface;

public class PrivacyUpdateReply implements UpdateReplyInterface
{	
	private final UpdateReplyInterface userUpdReplyObj;
	private final CallBackInterface userCallback;
	private final int totalAnonymizedIDUpdated;
	
	private int numberCompletionRepliesSoFar;
	
	private final Object localLock = new Object();
	
	
	public PrivacyUpdateReply(UpdateReplyInterface userUpdReplyObj
			, CallBackInterface userCallback, int totalAnonymizedIDUpdated )
	{	
		this.userUpdReplyObj = userUpdReplyObj;
		this.userCallback = userCallback;
		this.totalAnonymizedIDUpdated = totalAnonymizedIDUpdated;
		numberCompletionRepliesSoFar = 0;
	}
	
	/**
	 * Returns true in case of completion
	 * @return
	 */
	public boolean incrementReplies()
	{
		synchronized(localLock)
		{
			numberCompletionRepliesSoFar++;
			
			// completion of all anonymized ID updates
			if( numberCompletionRepliesSoFar == totalAnonymizedIDUpdated )
			{
				userCallback.updateCompletion(userUpdReplyObj);
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}