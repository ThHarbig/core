package mayday.core.meta.plugins;

import java.util.LinkedList;

import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;


public abstract class AbstractMetaInfoPlugin extends AbstractPlugin implements MetaInfoPlugin {
	
	protected PluginInfo pli;
	
	@SuppressWarnings("unchecked")
	protected void registerAcceptableClass(Class<? extends MIType> aClass) {
		LinkedList<Class<? extends MIType>> acceptable;
		acceptable = (LinkedList<Class<? extends MIType>>)pli.getProperties().get(MetaInfoPlugin.ACCEPTABLE_CLASSES);
		if (acceptable==null) {
			acceptable = new LinkedList<Class<? extends MIType>>();
			pli.getProperties().put(MetaInfoPlugin.ACCEPTABLE_CLASSES, acceptable);
		}
		acceptable.add(aClass);
	}
	
	protected void registerAcceptableType(String mioType) {
		registerAcceptableClass(MIManager.getMIOClass(mioType));
	}
	
	public String getMultiSelectHandling() {
		String multi=(String)pli.getProperties().get(MetaInfoPlugin.MULTISELECT_HANDLING);
		if (multi==null)
			multi = MetaInfoPlugin.MULTISELECT_HANDLE_DEFAULT;
		return multi;
	}


	

}
