package mayday.core.plugins.probe.lookup;

//http://www.ncbi.nlm.nih.gov/sites/entrez?db=pubmed&orig_db=pubmed&term=actinorhodin%20SCO7036&cmd=search&src=

public class PubmedLookup extends GenericLookupPlugin
{
	public PubmedLookup() 
	{		
		this.name="Pubmed";
		this.urlPrefix="http://www.ncbi.nlm.nih.gov/sites/entrez?db=pubmed&orig_db=pubmed&term=";
		this.urlSuffix="&cmd=search&src=";
		this.multipleProbesPerRequest=true;
		idSeperator="%20";
	}
}
