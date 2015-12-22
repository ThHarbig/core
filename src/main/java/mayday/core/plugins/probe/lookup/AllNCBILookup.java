package mayday.core.plugins.probe.lookup;

//http://www.ncbi.nlm.nih.gov/sites/gquery?term=SCO5555

public class AllNCBILookup extends GenericLookupPlugin
{
	public AllNCBILookup() {
		
		this.name="NCBI - All Databases";
		this.urlPrefix="http://www.ncbi.nlm.nih.gov/sites/gquery?term=";
		this.urlSuffix="&cmd=search&src=";
		this.multipleProbesPerRequest=true;
		idSeperator="%20";
	}
}
