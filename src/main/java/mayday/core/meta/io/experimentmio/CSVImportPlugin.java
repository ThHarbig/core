/*
 * Created on 29.11.2005
 */
package mayday.core.meta.io.experimentmio;

import java.util.HashMap;

import mayday.core.DataSet;
import mayday.core.Experiment;
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
				"PAS.mio.import.experiment.csv",
				new String[]{},
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Read meta information from tabular text files.",
		"Import Table (CSV)");
		pli.addCategory("Experiment information");
		pli.setIcon("mayday/images/table16.gif");
		return pli;  
	}

	@Override
	protected void fillDisplayNames(DataSet ds, MultiHashMap<String, Object> map) {
		for (Experiment e : ds.getMasterTable().getExperiments())
			map.put_unambigous(e.getDisplayName(), e);	
	}

	@Override
	protected Object getObject(DataSet ds, String name) {
		return ds.getMasterTable().getExperiment(name);
	}

}
