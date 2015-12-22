package mayday.core.plugins.probe.lookup;

//http://metacyc.org/META/substring-search?type=NIL&object=GAPDH

public class MetaCycLookup extends GenericLookupPlugin
{
	public MetaCycLookup() 
	{
		this.name="MetaCyc";
		this.urlPrefix="http://metacyc.org/META/substring-search?type=NIL&object=";
		this.urlSuffix="";
		this.multipleProbesPerRequest=false;
		idSeperator="";
	}
}
