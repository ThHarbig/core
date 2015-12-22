/**
 *  File DistanceMeasureType.java 
 *  Created on 05.04.2005
 *  As part of the package MathObjects.DistanceMeasures
 *  By Janko Dietzsch
 *  
 */
package mayday.core.math.scoring;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import mayday.core.math.stattest.StatTestPlugin;
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
public class ScoringManager extends AbstractPlugin implements CorePlugin {
	
	private static TreeMap<String, PluginInfo> values = new TreeMap<String, PluginInfo>();
	
	public void init() {
	}
	
	public void run() {
		values.clear();
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(StatTestPlugin.MC)) {
			values.put(pli.getName(),pli);
		}
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(ScoringPlugin.MC)) {
			values.put(pli.getName(),pli);
		}

	};
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.ScoringMethods",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all available scoring methods",
				"Scoring methods"
				);
		return pli;
	}
	
	public static Collection<PluginInfo> plivalues() {
		return Collections.<PluginInfo>unmodifiableCollection(values.values()); 
	}

	public static List<ScoringPlugin> values() {
		LinkedList<ScoringPlugin> ret = new LinkedList<ScoringPlugin>();
		for (PluginInfo pli : values.values())
			ret.add((ScoringPlugin)pli.newInstance());
		return ret; 
	}

	
	public static ScoringPlugin get(String Name) {
		PluginInfo pli = values.get(Name);
		if (pli!=null)
			return (ScoringPlugin)pli.newInstance();
		return null;
	}
	
	public ScoringManager() {
		// empty for pluma
	}
	

}
