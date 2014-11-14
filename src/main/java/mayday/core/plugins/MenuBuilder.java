package mayday.core.plugins;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import mayday.core.Mayday;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.pluma.prototypes.MenuPlugin;


public class MenuBuilder extends AbstractPlugin implements CorePlugin {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.MenuBarBuilder",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Builds Mayday's Menu bar using available plugins.",
				"Menubar Builder"
				);
		return pli;
	}

	public void run() {
		JMenuBar l_menuBar = Mayday.sharedInstance.getJMenuBar();
		l_menuBar.removeAll();
	    // build menu bar
		Set<PluginInfo> menuplugins = PluginManager.getInstance().getPluginsFor(Constants.MC_MENUBAR);
	    TreeMap<Integer, JMenu> sortedMenus = new TreeMap<Integer, JMenu>();
	    for (PluginInfo pli : menuplugins) {
	    	MenuPlugin mpl = (MenuPlugin)(pli.getInstance());
	    	int pos = mpl.getPreferredPosition();
	    	while(sortedMenus.containsKey(pos)) ++pos; //move further back
	    	sortedMenus.put(pos, mpl.getMenu());
	    }
	    for (Integer i : sortedMenus.keySet())
	    	l_menuBar.add(sortedMenus.get(i));
	    l_menuBar.repaint();
	}

	public void init() {
	}
}
