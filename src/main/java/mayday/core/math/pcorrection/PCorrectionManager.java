/**
 *  File DistanceMeasureType.java 
 *  Created on 05.04.2005
 *  As part of the package MathObjects.DistanceMeasures
 *  By Janko Dietzsch
 *  
 */
package mayday.core.math.pcorrection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;

/**
 * This class implements type selector for the wanted distance measure.
 * 
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 */
public class PCorrectionManager extends AbstractPlugin implements CorePlugin {
	
	private static TreeMap<String, PluginInfo> values = new TreeMap<String, PluginInfo>();
	
	public void init() {
	}
	
	public void run() {
		values.clear();
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(PCorrectionPlugin.MC)) {
			values.put(pli.getName(),pli);
		}
	};
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.PCorrectionMethods",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all available p-value correction methods",
				"P-value correction methods"
				);
		return pli;
	}
	
	public static Collection<PluginInfo> plivalues() {
		return Collections.<PluginInfo>unmodifiableCollection(values.values()); 
	}

	public static List<PCorrectionPlugin> values() {
		LinkedList<PCorrectionPlugin> ret = new LinkedList<PCorrectionPlugin>();
		for (PluginInfo pli : values.values())
			ret.add((PCorrectionPlugin)pli.newInstance());
		return ret; 
	}

	
	public static PCorrectionPlugin get(String Name) {
		PluginInfo pli = values.get(Name);
		if (pli!=null)
			return (PCorrectionPlugin)pli.newInstance();
		return null;
	}
	
	public PCorrectionManager() {
		// empty for pluma
	}
	

}
