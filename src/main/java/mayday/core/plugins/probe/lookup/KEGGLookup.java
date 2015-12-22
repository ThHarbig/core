package mayday.core.plugins.probe.lookup;


public class KEGGLookup extends GenericLookupPlugin {

	public KEGGLookup() 
	{
		this.name="KEGG";
		this.urlPrefix="http://www.genome.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&serv=gn&dbkey=all&keywords=";
		this.urlSuffix="";
		this.multipleProbesPerRequest=false;
		idSeperator="";
	}
	
//	public void init() {
//	}
//
//	public PluginInfo register() throws PluginManagerException {
//		PluginInfo pli = new PluginInfo(
//				this.getClass(),
//				"PAS.core.KEGGLookup",
//				new String[]{},
//				Constants.MC_PROBE,
//				new HashMap<String, Object>(),
//				"Stephan Symons",
//				"symons@informatik.uni-tuebingen.de",
//				"Retrieves Probe Information from the KEGG Website",
//				"KEGG Lookup"
//		);
//		return pli;		
//	}
//
//	@Override
//	protected void processProbes(Collection<Probe> probes, MasterTable masterTable) {
//
//		//		http://www.genome.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&serv=gn&dbkey=all&keywords=SCO4324&page=1
//		for(Probe p:probes)
//		{
//			StringBuilder url = new StringBuilder("http://www.genome.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&serv=gn&dbkey=all&keywords=");
//			url.append(p.getDisplayName());
//			if(!runWithURL(url.toString()))
//				break;
//		}
//
//
//	}

}
