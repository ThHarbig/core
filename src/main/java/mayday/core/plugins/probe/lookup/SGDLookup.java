package mayday.core.plugins.probe.lookup;


public class SGDLookup extends GenericLookupPlugin
{

	public SGDLookup() 
	{
		this.name="SGD";
		this.urlPrefix="http://db.yeastgenome.org/cgi-bin/SGD/singlepageformat?locus=";
		this.urlSuffix="";
		this.multipleProbesPerRequest=false;
		idSeperator="";

	}
	
}
