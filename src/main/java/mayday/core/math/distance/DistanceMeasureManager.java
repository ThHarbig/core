package mayday.core.math.distance;

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

public class DistanceMeasureManager extends AbstractPlugin implements CorePlugin {
	
	private static TreeMap<String, PluginInfo> values = new TreeMap<String, PluginInfo>();
	
	public void init() {
	}
	
	public void run() {
		values.clear();
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(DistanceMeasurePlugin.MC)) {
			values.put(pli.getName(),pli);
		}
	};
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DistanceMeasures",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all available distance measures",
				"Distance Measures"
				);
		return pli;
	}
	
	public static Collection<PluginInfo> plivalues() {
		return Collections.<PluginInfo>unmodifiableCollection(values.values()); 
	}

	public static List<DistanceMeasurePlugin> values() {
		LinkedList<DistanceMeasurePlugin> ret = new LinkedList<DistanceMeasurePlugin>();
		for (PluginInfo pli : values.values())
			ret.add((DistanceMeasurePlugin)pli.newInstance());
		return ret; 
	}

	
	public static DistanceMeasurePlugin get(String Name) {
		PluginInfo pli = values.get(Name);
		if (pli!=null)
			return (DistanceMeasurePlugin)pli.newInstance();
		return null;
	}
	
	public DistanceMeasureManager() {
		// empty for pluma
	}
	

}
