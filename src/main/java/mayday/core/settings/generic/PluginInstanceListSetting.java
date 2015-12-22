package mayday.core.settings.generic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringListSetting;
import mayday.core.settings.typed.StringSetting;

/** A list of plugin complete with their own settings, which can be changed.
 * @author battke
 *
 */

public class PluginInstanceListSetting<T extends AbstractPlugin> extends HierarchicalSetting {

	protected String[] MC;
	protected StringListSetting theList;
	protected List<T> cachedList;
	
	public PluginInstanceListSetting(String Name, String Description, List<T> Default, String... MC) {
		super(Name);
		setDescription(Description);
		theList = new StringListSetting(Name, Description, null);
		addSetting(theList);
		this.MC = MC;
		if (Default!=null)
			setPluginList(Default);
	}
	
	public PluginInstanceListSetting(String Name, String Description, String[] MC) {
		this(Name, Description, null, MC);
	}
	
	public PluginInstanceListSetting(String Name, String Description, String MC) {
		this(Name, Description, null, new String[]{MC});
	}

	
	@SuppressWarnings("unchecked")
	public List<T> getPluginList() {
		if (cachedList==null) {
			cachedList = new LinkedList<T>();
			List<String> thePLIDList = theList.getStringListValue();
			for (int i=0; i!=children.size()-1; ++i) {
				// create instance
				String plID = thePLIDList.get(i);
				PluginInfo pli = PluginManager.getInstance().getPluginFromID(plID);
				T instance = (T)pli.newInstance();
				// configure
				Setting s = children.get(i+1);
				if (instance.getSetting()!=null) {
					instance.getSetting().fromPrefNode(s.toPrefNode());
				}
				cachedList.add(instance);
			}
		}
		return cachedList;
	}
	
	public void setPluginList(List<T> plis) {
		List<String> ret = new LinkedList<String>();
		for (Setting s : children)
			s.removeChangeListener(this);
		children.clear();
		childrenMap.clear();
		addSetting(theList);
		for (T s : plis) {
			addPlugin(s);
			ret.add(PluginManager.getInstance().getPluginFromClass(s.getClass()).getIdentifier());
		}
		theList.setStringListValue(ret);	
		cachedList = null;
		fireChanged();
	}
	
	protected void addPlugin(T s) {
		if (s.getSetting()!=null) {
			addSetting(s.getSetting());
		} else {
			addSetting(new StringSetting("CHILD~"+new Random().nextInt(), null, ""));
		}
	}

	
	public SettingComponent getGUIElement() {
		return new PluginListSettingComponent2(this);
	}
		
		
	protected class PluginListSettingComponent2 extends AbstractMutableListSettingComponent<PluginInstanceListSetting<T>, T> {

		public PluginListSettingComponent2(PluginInstanceListSetting<T> s) {
			super(s);
		}

		@Override
		protected String elementToString(T element) {
			return PluginManager.getInstance().getPluginFromClass(element.getClass()).getIdentifier();
		}

		@Override
		protected Iterable<T> elementsFromSetting(PluginInstanceListSetting<T> mySetting) {
			LinkedList<T> clonedElements = new LinkedList<T>();
			for (T orig : mySetting.getPluginList()) {
				T clone = PluginManager.getInstance().getInitializedClone(orig);
				clonedElements.add(clone);
			}
			return clonedElements;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected T getElementToAdd(Collection<T> alreadyPresent) {
			Map<String, PluginInfo> av = new TreeMap<String, PluginInfo>();
			for (String mc : mySetting.MC) {
				Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(mc);
				for (PluginInfo pli : plis)
					av.put(pli.getName(), pli);
			}

			RestrictedStringSetting available = new RestrictedStringSetting("Select a plugin",null, 0, av.keySet().toArray(new String[0]));
			
			String[] tooltips = new String[av.size()];
			for (int i=0; i!=av.size(); ++i) {
				tooltips[i] = av.get(available.getPredefinedValues()[i]).getAbout();
			}
			available.setToolTips(tooltips);			
			available.setLayoutStyle(RestrictedStringSetting.LayoutStyle.LIST);
			
			SettingDialog sd = new SettingDialog(null, "Select a plugin to add", available);
			sd.showAsInputDialog();
			if (sd.canceled())
				return null;
			T plugin = (T)av.get(available.getStringValue()).newInstance();
			if (plugin.getSetting()!=null) {
				if (new SettingDialog(null, plugin.getSetting().getName(), plugin.getSetting()).showAsInputDialog().canceled())
					return null;
			}
			return plugin;
		}

		@Override
		protected String renderListElement(T element) {
			return PluginManager.getInstance().getPluginFromClass(element.getClass()).getName();
		}

		@Override
		protected String renderToolTip(T element) {
			return  PluginManager.getInstance().getPluginFromClass(element.getClass()).getAbout();
		}
		
		protected void handleDoubleClickOnElement(T element) {
			Setting s = element.getSetting();
			if (s!=null) {
				SettingDialog sd = new SettingDialog(null,s.getName(),s);
				sd.showAsInputDialog();
			}
		}
		
		// override method in AbstractSettingComponent
		@SuppressWarnings("unchecked")
		public boolean updateSettingFromEditor(boolean failSilently) {
			if (theList==null)
				return true;
			
			List<T> ret = new LinkedList<T>();
			for (int i=0; i!=theList.getModel().getSize(); ++i) {
				T element = (T)theList.getModel().getElementAt(i);
				ret.add(element);
			}
				
			mySetting.setPluginList(ret);
			return true;
		}
		
	}
	
	public PluginInstanceListSetting<T> clone() {
		return new PluginInstanceListSetting<T>(getName(),getDescription(),getPluginList(), MC);
	}
	
}
