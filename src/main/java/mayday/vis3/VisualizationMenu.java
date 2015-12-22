package mayday.vis3;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;

import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.gui.PluginMenu;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.plugins.menu.ProbeListMenu;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuPlugin;
import mayday.vis3.tables.TablePlugin;

public class VisualizationMenu extends AbstractPlugin implements MenuPlugin {

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.visualization.framework.menu",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all visualization plugins",
				"Visualizations"
		);		
		pli.addCategory(MaydayDefaults.Plugins.CATEGORY_VISUALIZATION);
		return pli;
	}

	@SuppressWarnings("serial")
	public JMenu getMenu() {
		
		// add category misc to all uncategorized plots
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(MaydayDefaults.Plugins.CATEGORY_PLOT);
		for (PluginInfo pli : plis) {
			if (pli.getProperties().get(Constants.CATEGORIES)==null)
				pli.addCategory("Miscellaneous");
		}
		
		PluginMenu<ProbeList> theMenu = new PluginMenu<ProbeList>("Visualization",MaydayDefaults.Plugins.CATEGORY_PLOT, TablePlugin.MC) {

			@Override
			public void callPlugin(PluginInfo pli, List<ProbeList> selection) {
				runVisPlugin(pli, selection);				
			}
			
			@Override
			protected List<ProbeList> getSelection() {
				return ProbeListMenu.getCurrentSelection();
			}
			
		};
		theMenu.setFlatStyle(true);
		theMenu.fill();
		
		return theMenu;
	}

	public int getPreferredPosition() {
		return 0;
	}
	
	public static void runVisPlugin(PluginInfo pli, List<ProbeList> selection) {
		ProbeListPluginRunner plpr = new ProbeListPluginRunner(pli, selection, null);
		plpr.execute();
	}
	
}
