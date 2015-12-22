package mayday.core.pluma;

import mayday.core.gui.PreferencePane;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingPreferencePane;

public abstract class AbstractPlugin {

	/* Plugin classes should be as simple as possible
	 * Don't import anything, don't do anything. 
	 * The best method is to use execute to create a new thread and WITHIN that thread
	 * create an object of another class which contains the "real" plugin code.
	 * Every import in the AbstractPlugin descendant can break the pluginscanner process
	 */

	// on register, no dependencies are guaranteed. Wait for init before using dependencies
	// do NOT use dependencies in static codeblocks or global variable initializers
	public abstract PluginInfo register() throws PluginManagerException;

	// on init, plugins can assume that all their dependencies are met
	public abstract void init();

	// unload is called when Mayday shuts down.
	public void unload() {};


	// override this function to provide a settings dialog (accessible from Mayday->Plugins->"your plugin"->Preferences)
	// NEW plugins should override getSetting() instead since getPreferencesPanel is deprecated old API.
	public PreferencePane getPreferencesPanel() {
		final Setting s = getSetting();
		if (s!=null) {
			return new SettingPreferencePane(PluginManager.getInstance().getPluginFromClass(AbstractPlugin.this.getClass()));
		}
		return null;
	}

	// return any kind of setting (e.g. hierarchicalsetting) that stores all your 
	// plugin's current state, but don't create a new instance for every call of this function
	public Setting getSetting() {
		return null;
	}

	public PluginInfo getPluginInfo() {
		return PluginManager.getInstance().getPluginFromClass(getClass());
	}

	public static class AbstractPluginInstanceComparator implements java.util.Comparator<AbstractPlugin> {

		@Override
		public int compare(AbstractPlugin o1, AbstractPlugin o2) {
			int comp = 0;

			// sort order: Name, HashCode 
			String myName = o1.getPluginInfo().getName();
			String otherName = o2.getPluginInfo().getName();
			comp = myName.compareTo(otherName);

			if (comp==0) {
				comp = Integer.valueOf(o1.hashCode()).compareTo(o2.hashCode());
			}

			return comp;
		}

	}


}
