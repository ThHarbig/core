package mayday.core.plugins.probe.lookup;

public class GoogleLookup  extends GenericLookupPlugin
{
	public GoogleLookup() 
	{		
		this.name="Google";
		this.urlPrefix="http://www.google.com/search?q=";
		this.urlSuffix="";
		this.multipleProbesPerRequest=true;
		idSeperator="+";
	}
}
