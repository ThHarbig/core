package mayday.core.plugins.probe.lookup;


public class GeneCardsLookup extends GenericLookupPlugin
{
	public GeneCardsLookup() 
	{
		this.name="Gene Cards";
		this.urlPrefix="http://www.zbit.uni-tuebingen.de/cgi-bin/genecards/carddisp.pl?gene=";
		this.urlSuffix="";
		this.multipleProbesPerRequest=false;
		idSeperator=" ";
	}
}
