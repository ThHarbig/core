package mayday.core;

public class XMLTools {

	public static String xmlize(String s) {
		return s.replace("&","&amp;").replace(">", "&gt;").replace("<","&lt;")
		        .replace("'","&apos;").replace("\"","&quot;")
		        ;
		// as per xml spec 
	}
	
	public static String unxmlize(String s) {
		return s.replace("&gt;",">").replace("&lt;","<")
        .replace("&apos;","'").replace("&quot;","\"")
        .replace("&amp;","&");
	}
	
		
	// functions for simple xml parsing
	
	public static Object[] nextSubstring(String master, String startmark, String endmark, int startPosition) {
		int elementstart = master.indexOf(startmark, startPosition);
		if (elementstart<0) 
			return new Object[]{null,-1};
		elementstart+=startmark.length();
		int elementend = master.indexOf(endmark, elementstart);		
		if (elementend<0)
			return new Object[]{null,-1};
		return new Object[]{master.substring(elementstart,elementend),elementend+endmark.length()};
	}
}
