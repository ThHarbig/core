package mayday.core.plugins.probe.lookup;

//http://www.ensembl.org/Search/Summary?species=all;idx=Gene;q=HSP90

public class EnsemblLookup extends GenericLookupPlugin
{
	public EnsemblLookup() 
	{
		this.name="Ensembl";
		this.urlPrefix="http://www.ensembl.org/Search/Summary?species=all;idx=Gene;q=";
		this.urlSuffix="";
		this.multipleProbesPerRequest=false;
		idSeperator="";
	}
}
