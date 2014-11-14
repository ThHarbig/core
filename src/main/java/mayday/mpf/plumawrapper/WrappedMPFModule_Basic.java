package mayday.mpf.plumawrapper;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.SurrogatePluginInfo;
import mayday.mpf.FilterBase;


public class WrappedMPFModule_Basic extends WrappedMPFModule<FilterBase> {

	private FilterBase base;

	public WrappedMPFModule_Basic() {
	}

	public FilterBase getMPFModule() {
		return base;
	}

	
	public void initializeWithObject(FilterBase surrogateObject, PluginInfo pli) {
		FilterBase instance = surrogateObject;
		try {
			// try to properly create a new instance
			instance = instance.getClass().newInstance();
		} catch (Exception e) {
			System.err.println("FilterBase could not create a new instance: "+e.getMessage());
			e.printStackTrace();
		}
		base = instance;		
	}

	public static SurrogatePluginInfo<FilterBase, WrappedMPFModule_Basic> producePluginInfo(FilterBase i) throws PluginManagerException {
		SurrogatePluginInfo<FilterBase, WrappedMPFModule_Basic> spli = new SurrogatePluginInfo<FilterBase, WrappedMPFModule_Basic>(
				WrappedMPFModule_Basic.class,
				i,
				"fill-in", 
				new String[]{"PAS.mpf"}, 
				mayday.mpf.Constants.masterComponent, 
				new HashMap<String,Object>(),
				"fill-in",
				"fill-in",
				"fill-in",
				"fill-in"
			);
		spli.addCategory("Basic Modules");
		return spli;
	}
}
