package mayday.core.plugins.probe.lookup;


public class HGNClookup extends GenericLookupPlugin
{

	public HGNClookup()
	{
		this.name="HGNC";
		this.urlPrefix="http://www.genenames.org/cgi-bin/search?search_type=all&search=";
		this.urlSuffix="&submit=Submit";
		this.multipleProbesPerRequest=false;
		idSeperator="";

	}
	
}
