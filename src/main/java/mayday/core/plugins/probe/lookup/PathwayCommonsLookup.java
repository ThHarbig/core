package mayday.core.plugins.probe.lookup;

//http://www.pathwaycommons.org/pc/webservice.do?version=3.0&snapshot_id=GLOBAL_FILTER_SETTINGS&entity_type=ALL_ENTITY_TYPE&q=SCO5088&format=html&cmd=get_by_keyword

public class PathwayCommonsLookup extends GenericLookupPlugin
{
	public PathwayCommonsLookup() 
	{
		this.name="Pathway Commons";
		this.urlPrefix="http://www.pathwaycommons.org/pc/webservice.do?version=3.0&snapshot_id=GLOBAL_FILTER_SETTINGS&entity_type=ALL_ENTITY_TYPE&q=";
		this.urlSuffix="&format=html&cmd=get_by_keyword";
		this.multipleProbesPerRequest=true;
		idSeperator="+";
	}
}
