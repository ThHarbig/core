package mayday.core.plugins.probe.lookup;


public class StepDBLookup extends GenericLookupPlugin
{

	public StepDBLookup() 
	{
		this.name="StrepDB";
		this.urlPrefix="http://strepdb.streptomyces.org.uk/cgi-bin/search.pl?string=";
		this.urlSuffix="&width=900&accession=AL645882&anno_or_gene=gene";
		this.multipleProbesPerRequest=false;
		idSeperator="";

	}
	
}
