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
public class LinearMappingRelevanceFunctionPlugin
extends RelevanceFunctionPlugin
{
	
	protected double l_range;
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.relevance.linear",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Maps input values to relevance values using a linear function.",
				"Linear Mapping");
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_RELEVANCE);

		return pli;
	}
  
 
	protected double transformValue(double oldvalue) {
		return ( oldvalue - l_min ) / l_range;
	}

	protected void prepare() {
		computeMinMax();
	    l_range = l_max - l_min;
	}

	@Override
	protected String getMIODescription() {
		return "(linear mapping)";
	}
}
