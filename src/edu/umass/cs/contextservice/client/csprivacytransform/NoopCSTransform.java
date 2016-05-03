package edu.umass.cs.contextservice.client.csprivacytransform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;

import edu.umass.cs.contextservice.client.common.AnonymizedIDEntry;
import edu.umass.cs.contextservice.messages.dataformat.AttrValueRepresentationJSON;
import edu.umass.cs.contextservice.client.common.ACLEntry;
import edu.umass.cs.contextservice.utils.Utils;
import edu.umass.cs.gnsclient.client.GuidEntry;

public class NoopCSTransform implements CSPrivacyTransformInterface
{
	@Override
	public List<CSUpdateTransformedMessage> transformUpdateForCSPrivacy(String targetGuid,
			HashMap<String, AttrValueRepresentationJSON> attrValueMap, HashMap<String, List<ACLEntry>> aclMap,
			List<AnonymizedIDEntry> anonymizedIDList) 
	{
		CSUpdateTransformedMessage csTransformedMessage 
			= new CSUpdateTransformedMessage(Utils.hexStringToByteArray(targetGuid), 
					attrValueMap);
		List<CSUpdateTransformedMessage> returnList = new LinkedList<CSUpdateTransformedMessage>();
		returnList.add(csTransformedMessage);
		return returnList;
	}
	
	@Override
	public void unTransformSearchReply(GuidEntry myGuid, List<CSSearchReplyTransformedMessage> csTransformedList, 
			JSONArray replyArray) 
	{
		for(int i=0;i<csTransformedList.size(); i++)
		{
			replyArray.put(csTransformedList.get(i).getSearchGUIDObj().getID());
		}
	}

//	@Override
//	public List<String> transformSearchQueryForCSPrivacy(String userSearchQuery,
//			HashMap<Integer, JSONArray> subspaceAttrMap) {
//		return null;
//	}
}