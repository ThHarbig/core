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
public class InvertRelevanceFunctionPlugin
extends RelevanceFunctionPlugin

{
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.relevance.inverse",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Maps input values to relevance values using an exponential function." +
				"Inverts the relevance values (1 becomes 0 and vice versa).",
				"Inverse Mapping");
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_RELEVANCE);

		return pli;
	}
  
	@Override
	protected String getMIODescription() {
		return "Relevance rating of \"" + selectedGroup.getName() + "\" (inverted)";
	}
	@Override
	protected double transformValue(double oldvalue) {
		return 1.0-oldvalue;
	}
	    
}
