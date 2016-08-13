package cs276.pa4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
	
	public static ArrayList<String> parseUrlString(String url) {
		String urlTokenInterm = url.replace("http:",".").replace('/','.').replace('?','.')
							        .replace('=','.').replace("%20",".").replace("...",".").replace("..",".").toLowerCase();
	    urlTokenInterm = urlTokenInterm.replaceAll("[^a-z0-9]+", "\\.");
	    String[] urlarr = urlTokenInterm.split("\\.");
	    ArrayList<String> urlTokens = new ArrayList<String>(Arrays.asList(urlarr));

	    //remove initial blank
	    if (urlTokens.get(0).equals(""))
	      urlTokens.remove(0);

	    return urlTokens;
	}
	
	public static List<String> parseTitle(String title){
		String[] titleArr = title.split(" ");
		List<String> titleTokens = Arrays.asList(titleArr);
		return titleTokens;
	}
	
	public static List<String> parseHeaders(List<String> headers) {
		ArrayList<String> headerTokens = new ArrayList<String>();
		if (headers!=null)
		{
			for (String header : headers)
			{
				String[] headerArr = header.split(" ");
				headerTokens.addAll(Arrays.asList(headerArr));
			}
		}
		return headerTokens;
	}
	
	public static Map<String,Integer> parseAnchors(Map<String, Integer> anchors) {
		Map<String,Integer> anchorCountMap = new HashMap<String,Integer>();
		if (anchors!=null)
		{
			for (String anchor : anchors.keySet())
			{
				int count = anchors.get(anchor);
				String[] anchorarr=anchor.split(" ");
				List<String> anchorTokens = Arrays.asList(anchorarr);
				
				for (String anchorToken : anchorTokens)
				{
					if (anchorCountMap.containsKey(anchorToken))
						anchorCountMap.put(anchorToken, anchorCountMap.get(anchorToken)+count);
					else
						anchorCountMap.put(anchorToken, count);
				}
			}
		}
		return anchorCountMap;
	}
	
	public static Map<String,Integer> parseBody(Map<String, List<Integer>> bodyHits) {
		Map<String,Integer> bodyCountMap = new HashMap<String,Integer>();
		if (bodyHits!=null) {
			for (String bodyHit : bodyHits.keySet())
			{
				bodyCountMap.put(bodyHit, bodyHits.get(bodyHit).size());
			}
		}
		return bodyCountMap;
	}
}
