/*
 * Created on Dec 8, 2004
 *
 */
package mayday.core.plugins.mio.relevance;

import java.util.HashMap;

import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class ExponentialMappingRelevanceFunctionPlugin
extends RelevanceFunctionPlugin
{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.relevance.exponential",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Maps input values to relevance values using an exponential function." +
				" Minimum input value will be mapped to relevance 0, maximum input value will be mapped to relevance 1.",
				"Exponential Mapping");
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_RELEVANCE);

		return pli;
	}

	protected double l_sf = 1.0;


	protected double transformValue(double oldvalue) {
		return ( Math.exp( l_sf*oldvalue - l_sf*l_min ) ) / ( Math.exp( l_sf*l_max - l_sf*l_min ) );
	}

	protected void prepare() {
		computeMinMax();
		l_sf = new Double( JOptionPane.showInputDialog( "Enter the scaling factor.", l_sf ) );  
	}

	@Override
	protected String getMIODescription() {
		return "(exponential mapping, scaling = " + l_sf + ")";
	}
}
