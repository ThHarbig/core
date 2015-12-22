package mayday.core.plugins.probe.lookup;


public class NCBIGeneLookup extends GenericLookupPlugin
{
	public NCBIGeneLookup() 
	{
		this.name="NCBI Gene";
		this.urlPrefix="http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=search&db=gene&term=";
		this.urlSuffix="";
		this.multipleProbesPerRequest=true;
		idSeperator=" or ";
	}


//	public void init() {
//	}
//
//	public PluginInfo register() throws PluginManagerException {
//		PluginInfo pli = new PluginInfo(
//				this.getClass(),
//				"PAS.core.NCBILookup",
//				new String[]{},
//				Constants.MC_PROBE,
//				new HashMap<String, Object>(),
//				"Stephan Symons",
//				"symons@informatik.uni-tuebingen.de",
//				"Retrieves Probe Information from the NCBI Gene Database",
//				"NCBI Lookup"
//		);
//		return pli;		
//	}
//
//	@Override
//	protected void processProbes(Collection<Probe> probes, MasterTable masterTable) {
//
//		StringBuilder url = new StringBuilder();
//
//		url.append("http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=search&db=gene&term=");//SCO6221[sym] or SCO4331[sym]");
//		boolean first=true;
//		// one window for each probe 
//		for (Probe pb : probes) {
//			if(!first)
//				url.append(" or ");
//			url.append(pb.getDisplayName());
//			url.append("[sym]");
//			first=false;
//		}
//		runWithURL(url.toString());
//
//	}

}



