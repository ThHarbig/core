package mayday.core.plugins.probe.lookup;


public class AllEBILookup extends GenericLookupPlugin
{
	public AllEBILookup() 
	{
		this.name="EBI - All Databases";
		this.urlPrefix="http://www.ebi.ac.uk/ebisearch/search.ebi?db=allebi&query=(";
		this.urlSuffix=")&FormsButton3=Go";
		this.multipleProbesPerRequest=true;
		idSeperator="+OR+";
	}
}
