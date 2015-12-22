package mayday.core.plugins.probe.lookup;

import java.util.Collection;
import java.util.HashMap;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.plugins.probe.ProbeWebLookupPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public abstract class GenericLookupPlugin extends ProbeWebLookupPlugin
{
	protected String name;
	protected String urlPrefix;
	protected String urlSuffix;
	protected String idSeperator;
	protected boolean multipleProbesPerRequest;
	
	public GenericLookupPlugin() 
	{
		name="";
	}
	
	public GenericLookupPlugin(String name, String urlPrefix, String urlSuffix, boolean multiple) 
	{
		this.name=name;
		this.urlPrefix=urlPrefix;
		this.urlSuffix=urlSuffix;
		this.multipleProbesPerRequest=multiple;
		idSeperator=" or ";
	}

	@Override
	protected void processProbes(Collection<Probe> probes,
			MasterTable masterTable) 
	{
		StringBuilder url = new StringBuilder();
		url.append(urlPrefix);
		boolean first=true;
		if(multipleProbesPerRequest)
		{
			for (Probe pb : probes) {
				if(!first)
					url.append(idSeperator);
				url.append(pb.getDisplayName());
				
				first=false;
			}
			url.append(urlSuffix);
			runWithURL(url.toString());
		}else
		{
			for(Probe p:probes)
			{
				url = new StringBuilder(urlPrefix);
				url.append(p.getDisplayName());
				url.append(urlSuffix);
				if(!runWithURL(url.toString()))
					break;
			}
		}
		
	}

	@Override
	public void init() 	{} // do nothing. 


	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.core.Lookup"+name,
				new String[]{},
				Constants.MC_PROBE,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Retrieves Probe Information from "+name,
				name
		);
		pli.addCategory("Lookup");
		return pli;		
	}
	

}
