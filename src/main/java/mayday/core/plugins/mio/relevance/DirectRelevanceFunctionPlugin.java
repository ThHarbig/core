/*
 * Created on Dec 8, 2004
 *
 */
package mayday.core.plugins.mio.relevance;

import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author gehlenbo
 *
 */
public class DirectRelevanceFunctionPlugin
extends RelevanceFunctionPlugin
{
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.relevance.direct",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Transforms input values into relevance values.",
				"Direct Mapping");
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_RELEVANCE);
		return pli;
	}
  
	protected String getMIODescription() {
		return "(direct mapping)";
	}

	protected double transformValue(double oldvalue) {
		return oldvalue;
	}
	
}
