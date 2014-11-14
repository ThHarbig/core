package mayday.core.settings.generic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringListSetting;

/** A list of plugin infos, subsettings (i.e. settings of the plugins in the list) can not be changed.
 * Probably you should rather use PluginInstanceListSetting.  
 * @author battke
 *
 */
public class PluginTypeListSetting extends StringListSetting {

	protected String[] MC; 
	
	public PluginTypeListSetting(String Name, String Description, List<PluginInfo> Default, String... MC) {
		super(Name, Description, null);
		this.MC = MC;
		if (Default!=null)
			setPluginList(Default);
	}
	
	public PluginTypeListSetting(String Name, String Description, String... MC) {
		this(Name, Description, null, MC);
	}
	
	public List<PluginInfo> getPluginList() {
		List<PluginInfo> ret = new LinkedList<PluginInfo>();
		for (String s : getStringListValue()) {
			PluginInfo pli = PluginManager.getInstance().getPluginFromID(s);
			if (pli!=null)
				ret.add(pli);
		}
		return ret;
	}
	
	public void setPluginList(List<PluginInfo> plis) {
		List<String> ret = new LinkedList<String>();
		for (PluginInfo s : plis) {
			ret.add(s.getIdentifier());
		}
		setStringListValue(ret);	
	}

	
	public SettingComponent getGUIElement() {
		return new PluginListSettingComponent(this);
	}
		
		
	protected static class PluginListSettingComponent extends AbstractMutableListSettingComponent<PluginTypeListSetting, PluginInfo> {

		public PluginListSettingComponent(PluginTypeListSetting s) {
			super(s);
		}

		@Override
		protected String elementToString(PluginInfo element) {
			return element.getIdentifier();
		}

		@Override
		protected Iterable<PluginInfo> elementsFromSetting(PluginTypeListSetting mySetting) {
			return mySetting.getPluginList();
		}

		@Override
		protected PluginInfo getElementToAdd(Collection<PluginInfo> alreadyPresent) {
			Map<String, PluginInfo> av = new TreeMap<String, PluginInfo>();
			for (String mc : mySetting.MC) {
				Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(mc);
				for (PluginInfo pli : plis)
					if (!alreadyPresent.contains(pli))
						av.put(pli.getName(), pli);
			}
			RestrictedStringSetting available = new RestrictedStringSetting("Select a plugin",null, 0, av.keySet().toArray(new String[0]));
			
			String[] tooltips = new String[av.size()];
			for (int i=0; i!=av.size(); ++i) {
				tooltips[i] = av.get(available.getPredefinedValues()[i]).getAbout();
			}
			available.setToolTips(tooltips);
			
			available.setLayoutStyle(LayoutStyle.LIST);
			SettingDialog sd = new SettingDialog(null, "Select a plugin to add", available);
			sd.showAsInputDialog();
			if (!sd.canceled()) 
				return av.get(available.getStringValue());
			return null;
		}

		@Override
		protected String renderListElement(PluginInfo element) {
			return element.getName();
		}

		@Override
		protected String renderToolTip(PluginInfo element) {
			return  element.getAbout();
		}
		
	}
	
	public PluginTypeListSetting clone() {
		return new PluginTypeListSetting(getName(),getDescription(),getPluginList(), MC);
	}


}
