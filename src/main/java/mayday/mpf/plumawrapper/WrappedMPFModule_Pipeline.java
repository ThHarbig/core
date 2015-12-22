package mayday.mpf.plumawrapper;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.SurrogatePluginInfo;
import mayday.mpf.Constants;
import mayday.mpf.FilterBase;
import mayday.mpf.FilterClassList;
import mayday.mpf.FilterClassList.Item;


public class WrappedMPFModule_Pipeline extends WrappedMPFModule<FilterClassList.Item> {

	private FilterClassList.Item pipeline;

	public FilterBase getMPFModule() {
		try {
			return pipeline.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("This module can't be started.\n"+e.getMessage());
		}
	}

	public WrappedMPFModule_Pipeline() {
	}

	@Override
	public void initializeWithObject(Item surrogateObject, PluginInfo pli) {
		pipeline = surrogateObject;		
	}
	
	public static SurrogatePluginInfo<FilterClassList.Item, WrappedMPFModule_Pipeline> producePluginInfo(FilterClassList.Item i) throws PluginManagerException {
		SurrogatePluginInfo<FilterClassList.Item, WrappedMPFModule_Pipeline> spli = new SurrogatePluginInfo<FilterClassList.Item, WrappedMPFModule_Pipeline>(
				WrappedMPFModule_Pipeline.class,
				i,
				"PAS.mpf.pipeline."+i.toString().replace(" ", ""),
				new String[0],
				Constants.masterComponent,
				new HashMap<String, Object>(),
				"()",
				"(none)",
				i.getDescription().replace("\n", "<br>"),
				i.toString()
			);
		spli.addCategory(i.getCategory());
		return spli;
	}
	
}