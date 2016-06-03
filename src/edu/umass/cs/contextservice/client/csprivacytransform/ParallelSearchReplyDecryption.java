package edu.umass.cs.contextservice.client.csprivacytransform;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.json.JSONArray;
import org.json.JSONException;

import edu.umass.cs.contextservice.logging.ContextServiceLogger;
import edu.umass.cs.contextservice.messages.dataformat.SearchReplyGUIDRepresentationJSON;
import edu.umass.cs.contextservice.utils.Utils;
import edu.umass.cs.gnsclient.client.GuidEntry;

/**
 * Implements the parallel decryption of search reply.
 * @author adipc
 *
 */
public class ParallelSearchReplyDecryption 
{
	private final GuidEntry myGuid;
	private final List<CSSearchReplyTransformedMessage> csTransformedList;
	private final JSONArray replyArray;
	private final ExecutorService execService;
	
	//private long numStarted  				= 0;
	private long numFinished 				= 0;
	private final Object lock 				= new Object();
	
	private int totalDecryptionsOverall		= 0;
	
	public ParallelSearchReplyDecryption(GuidEntry myGuid , 
			List<CSSearchReplyTransformedMessage> csTransformedList
			, JSONArray replyArray, ExecutorService execService)
	{
		this.myGuid = myGuid;
		this.csTransformedList = csTransformedList;
		this.replyArray = replyArray;
		this.execService = execService;
	}
	
	/**
	 * Blocking call, does the decryption of the search 
	 * reply, in csTransformedList, and returns real GUIDs 
	 * in replyArray
	 */
	public void doDecryption()
	{
		for(int i=0; i<csTransformedList.size();i++)
		{
			CSSearchReplyTransformedMessage csSearchRepMessage 
							= csTransformedList.get(i);
			
			execService.execute( new SearchReplyDecryptionThread
					( myGuid, csSearchRepMessage.getSearchGUIDObj() ) );
		}
		
		synchronized( lock )
		{
			while( numFinished != csTransformedList.size() )
			{
				try 
				{
					lock.wait();
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public int getTotalDecryptionsOverall()
	{
		return this.totalDecryptionsOverall;
	}
	
	/**
	 * Decrypts the anonymized ID in the search reply.
	 * @author adipc
	 */
	private class SearchReplyDecryptionThread implements Runnable
	{
		private final GuidEntry myGUIDInfo;
		private final SearchReplyGUIDRepresentationJSON seachReply;
		
		private int totalDecryptionsThread = 0;
		
		public SearchReplyDecryptionThread( GuidEntry myGUIDInfo , 
				SearchReplyGUIDRepresentationJSON seachReply )
		{
			this.myGUIDInfo = myGUIDInfo;
			this.seachReply = seachReply;
		}
		
		@Override
		public void run()
		{
			try {
				byte[] plainTextBytes = decryptRealIDFromSearchRep( myGUIDInfo, seachReply );
				
				if( plainTextBytes != null )
				{
					synchronized(lock)
					{
						numFinished++;
						totalDecryptionsOverall = totalDecryptionsOverall + totalDecryptionsThread;
						replyArray.put( Utils.bytArrayToHex(plainTextBytes) );
						if( numFinished == csTransformedList.size() )
						{
							lock.notify();
						}
					}
				}
				else
				{
					synchronized(lock)
					{
						numFinished++;
						totalDecryptionsOverall = totalDecryptionsOverall + totalDecryptionsThread;
						if( numFinished == csTransformedList.size() )
						{
							lock.notify();
						}
					}
				}
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Decrypts the real ID from search reply using realID mapping info.
		 * Returns null if it cannot be decrypted.
		 * @param myGUIDInfo
		 * @param encryptedRealJsonArray
		 * @return
		 * @throws DecoderException 
		 * @throws JSONException 
		 */
		private byte[] decryptRealIDFromSearchRep( GuidEntry myGUIDInfo, 
				SearchReplyGUIDRepresentationJSON seachReply ) throws DecoderException 
		{
			byte[] privateKey = myGUIDInfo.getPrivateKey().getEncoded();
			byte[] plainText = null;
			JSONArray anonymizedIDToGuidMapping 
								= seachReply.getAnonymizedIDToGuidMapping();
			
			if( anonymizedIDToGuidMapping != null )
			{
				ContextServiceLogger.getLogger().fine("realIDMappingInfo JSONArray "
						+ anonymizedIDToGuidMapping.length() );
				String myGuidString = myGUIDInfo.getGuid();
				
				int indexToCheck = Utils.consistentHashAString(myGuidString, 
												anonymizedIDToGuidMapping.length());
				int numChecked = 0;
				
				while(numChecked < anonymizedIDToGuidMapping.length())
				{
					try
					{
//						System.out.println("indexToCheck "+indexToCheck
//								+" anonymizedIDToGuidMapping "
//								+anonymizedIDToGuidMapping.length());
						
						byte[] encryptedElement =  Utils.hexStringToByteArray(
								anonymizedIDToGuidMapping.getString(indexToCheck));
						totalDecryptionsThread++;
						
						plainText = Utils.doPrivateKeyDecryption(privateKey, encryptedElement);
						
						// non exception, just break;
						break;
					}
					catch(javax.crypto.BadPaddingException wrongKeyException)
					{
						// just catching this one, as this one results when wrong key is used 
						// to decrypt.
					} catch ( InvalidKeyException | NoSuchAlgorithmException
							| InvalidKeySpecException | NoSuchPaddingException
							| IllegalBlockSizeException | JSONException
							e )
					{
						e.printStackTrace();
					}
					numChecked++;
					indexToCheck++;
					indexToCheck = indexToCheck%anonymizedIDToGuidMapping.length();
				}
			}
			
			if(plainText != null)
			{
				ContextServiceLogger.getLogger().fine("Anonymized ID "+seachReply.getID()
										+ "realID "+Utils.bytArrayToHex(plainText) );
			}
			return plainText;
		}
	}
}