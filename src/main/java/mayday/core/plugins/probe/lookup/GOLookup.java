package mayday.core.plugins.probe.lookup;

public class GOLookup extends GenericLookupPlugin
{
	public GOLookup() 
	{
		this.name="Gene Ontology";
		this.urlPrefix="http://amigo.geneontology.org/amigo/medial_search?q=";
		this.urlSuffix=";search_constraint=gp;action=query;view=query";
		this.multipleProbesPerRequest=false;
		idSeperator=" ";
	}
}
