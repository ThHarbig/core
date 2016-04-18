package mayday.core.plugins.probe.lookup;


public class SGDLookup extends GenericLookupPlugin
{

	public SGDLookup() 
	{
		this.name="SGD";
		this.urlPrefix="http://www.yeastgenome.org/cgi-bin/search/luceneQS.fpl?query=";
		this.urlSuffix="";
		this.multipleProbesPerRequest=false;
		idSeperator="";

	}
	
}
