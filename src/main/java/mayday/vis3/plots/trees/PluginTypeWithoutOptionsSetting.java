package mayday.vis3.plots.trees;

import java.util.HashMap;
import java.util.Set;

import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.ObjectSelectionSetting;

public class PluginTypeWithoutOptionsSetting<T extends AbstractPlugin> extends ObjectSelectionSetting<AbstractPlugin> {
	

	protected Set<PluginInfo> predef;
	protected HashMap<String, PluginInfo> mapping = new HashMap<String, PluginInfo>();

	public PluginTypeWithoutOptionsSetting(String Name, String Description, T Default, Set<PluginInfo> plis) {
		super(Name,Description, 0, convert(plis));
		for (PluginInfo pli : plis)
			mapping.put(pli.getName(), pli);
		predef = plis;
		setInstance(Default);
	}
	
	protected static AbstractPlugin[] convert(Set<PluginInfo> plis) {
		AbstractPlugin[] ret = new AbstractPlugin[plis.size()];
		int i=0;
		for (PluginInfo pli : plis)
			ret[i++] = pli.getInstance();
		return ret;
	}

	public PluginTypeWithoutOptionsSetting(String Name, String Description, T Default, String MC) {
		this(Name, Description, Default, PluginManager.getInstance().getPluginsFor(MC));
	}

	public boolean isValidValue(String newVal) {
		return mapping.containsKey(newVal);
	}

	@SuppressWarnings("unchecked")
	public T getInstance() {
		T instance = (T)mapping.get(getStringValue()).newInstance();
		return instance;
	}

	public void setInstance(T instance) {
		String oldVal = getStringValue();
		String newValue = PluginManager.getInstance().getPluginFromClass(instance.getClass()).getName();
		if (!representative.deSerialize(MIType.SERIAL_TEXT, newValue))
			throw new RuntimeException("Invalid value \""+newValue+"\" for Setting of type "+getType());
		if (!oldVal.equals(getStringValue()))
			fireChanged();		
	}

	@SuppressWarnings("unchecked")
	public void setValueString(String newValue) {
		PluginInfo pli = mapping!=null?mapping.get(newValue):null;
		if (pli!=null)
			setInstance( (T)pli.getInstance() );
		else
			super.setValueString(newValue);
	}
	
	public PluginTypeWithoutOptionsSetting<T> clone() {
		PluginTypeWithoutOptionsSetting<T> pts = new PluginTypeWithoutOptionsSetting<T>(getName(),getDescription(),getInstance(),predef);
		return pts;
	}

	// since the plugins have no settings of their own, so we can replace the plugintypesetting-gui with 
	// the original guis offered by objectselectionsetting
	public SettingComponent getGUIElement() {
		switch(layout) {
		case COMBOBOX:	
			return new ObjectSelectionComboBoxSettingComponent(this);
		case RADIOBUTTONS:
			return new ObjectSelectionRadioButtonSettingComponent(this, false);
		case RADIOBUTTONS_HORIZONTAL:
			return new ObjectSelectionRadioButtonSettingComponent(this, true);
		case LIST:
			return new ObjectSelectionListSettingComponent(this);
		}		
		return null;
	}		
	
}
