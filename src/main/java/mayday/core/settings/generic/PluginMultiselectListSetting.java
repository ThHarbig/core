package mayday.core.settings.generic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.structures.maps.BidirectionalHashMap;
/**
 * Presents a choice of strings where several can be selected 
 * @author fb
 *
 */
public class PluginMultiselectListSetting<T extends AbstractPlugin> extends HierarchicalSetting {

	protected BidirectionalHashMap<PluginInfo, Setting> subsettings = new BidirectionalHashMap<PluginInfo, Setting>();
	protected List<PluginInfo> predef = new ArrayList<PluginInfo>();
	
	public PluginMultiselectListSetting(String Name, String Description, Collection<PluginInfo> plugins) {
		super(Name);
		this.setDescription(Description);
		predef.addAll(plugins);
		for (PluginInfo pli : predef) {
			BooleanHierarchicalSetting bhs = new BooleanHierarchicalSetting(pli.getName(), pli.getAbout(), false);
			Setting s = pli.newInstance().getSetting();
			if (s!=null) {
				bhs.addSetting(s);
				subsettings.put(pli,s);
			}
			bhs.setLayoutStyle(BooleanHierarchicalSetting.LayoutStyle.PANEL_FOLDUP);
			addSetting(bhs);
		}
	}	

	public PluginMultiselectListSetting(String Name, String Description, String... MCs) {
		this(Name, Description, PluginManager.getInstance().getPluginsFor(MCs));
	}
	
	public PluginMultiselectListSetting<T> clone() {
		PluginMultiselectListSetting<T> c = new PluginMultiselectListSetting<T>(getName(), getDescription(), predef);
		c.fromPrefNode(this.toPrefNode());
		for (PluginInfo pli : predef)
			c.subsettings.put(pli, (Setting)subsettings.get(pli));
		return c;
	}

	public SettingComponent getGUIElement() {
		HierarchicalSettingComponent_Panel hscp = new HierarchicalSettingComponent_Panel(this, true, false);
		hscp.getEditorComponent().setBackground(Color.white);
		hscp.getEditorComponent().setOpaque(true);
		hscp.setUseScrollPane(true);
		return hscp;
	}
	
	protected void setSettings(AbstractPlugin newPlug, PluginInfo pli) {
		Setting stored = subsettings.get(pli);
		Setting target = newPlug.getSetting();
		if (stored!=null && target!=null)
			target.fromPrefNode(stored.toPrefNode());
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getSelectedPlugins() {
		LinkedList<T> ret = new LinkedList<T>();
		for (int i=0; i!=predef.size(); ++i) {			
			if (((BooleanHierarchicalSetting)children.get(i)).getBooleanValue()) {
				T newPlug = (T)predef.get(i).newInstance();
				if (newPlug==null)
					continue; // happens when pluma cant instantiate the plugin
				setSettings(newPlug, predef.get(i));
				ret.add(newPlug);
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public void setSelectedPlugins(Collection sel) {		
		for (Setting s : children) {
			((BooleanHierarchicalSetting)s).setBooleanValue(false);
		}
		for (Object t : sel) {
			if (!(t instanceof PluginInfo) && (t instanceof AbstractPlugin))
				t = PluginManager.getInstance().getPluginFromClass(((AbstractPlugin)t).getClass());
			int index = predef.indexOf(t);
			((BooleanHierarchicalSetting)children.get(index)).setBooleanValue(true);
		}
	}
	
	
}
