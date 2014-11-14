/*
 * Created on 29.11.2005
 */
package mayday.core.meta.io.probemio;

import java.util.HashMap;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.meta.io.tabular.AbstractCSVImportPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.maps.MultiHashMap;

/**
 * Text file import plugin.
 * 
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 29.11.2005
 *
 */
public class CSVImportPlugin
extends AbstractCSVImportPlugin 
{
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.mio.import.probe.csv",
				new String[]{},
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Read meta information from tabular text files.",
		"Import Table (CSV)");
		pli.addCategory("Probe information");
		pli.setIcon("mayday/images/table16.gif");
		return pli;  
	}

	@Override
	protected void fillDisplayNames(DataSet ds, MultiHashMap<String, Object> map) {
		for (Probe pb : ds.getMasterTable().getProbes().values())
			map.put_unambigous(pb.getDisplayName(), pb);	
	}

	@Override
	protected Object getObject(DataSet ds, String name) {
		return ds.getMasterTable().getProbe(name);
	}

}
