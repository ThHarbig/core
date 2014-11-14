package mayday.core.plugins;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.PluginTypeListSetting;

public class PluggableUIBuilder extends AbstractPlugin implements CorePlugin, SettingChangeListener {

	protected final static String[] allowedMC = new String[]{
		Constants.MC_PLUGGABLEVIEWS
	};
	
	protected static PluginTypeListSetting elements;
	
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.core.PluggableUIBuilder",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Fills Mayday's PluggableUI (right window pane) with a predefined list of elements.",
				"Additional view elements"
				);
		return pli;
	}
	
	public Setting getSetting() {
		if (elements==null) {
			elements = new PluginTypeListSetting("View Elements", null, null, allowedMC);
			elements.addChangeListener(this);
		}
		return elements;
	}
	
	public void run() {    	
        PluginInfo.loadDefaultSettings(getSetting(), "PAS.core.PluggableUIBuilder");
		fillPane();
		
	}
	
	protected void fillPane() {
		for (PluginInfo pli : elements.getPluginList()) {
			((GenericPlugin)pli.getInstance()).run();
		}
		
	}

	public void init() {
	}

	public void stateChanged(SettingChangeEvent e) {
		fillPane();
	}
}
