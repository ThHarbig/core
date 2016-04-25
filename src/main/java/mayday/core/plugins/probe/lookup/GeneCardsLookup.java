package mayday.core.plugins.probe.lookup;


public class GeneCardsLookup extends GenericLookupPlugin
{
	public GeneCardsLookup() 
	{
		this.name="Gene Cards";
		this.urlPrefix="http://www.genecards.org/cgi-bin/carddisp.pl?gene=";
		this.urlSuffix="";
		this.multipleProbesPerRequest=false;
		idSeperator=" ";
	}
}
