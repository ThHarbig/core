package mayday.mpf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;

import mayday.core.ProbeList;
import mayday.core.gui.PluginMenu;
import mayday.core.plugins.menu.ProbeListMenu;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuPlugin;

@PluginManager.IGNORE_PLUGIN
public class MPFSubmenu extends AbstractPlugin implements MenuPlugin {

	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.mpf.ModuleMenu",
				new String[0],
				mayday.core.pluma.Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extends the Probelist menu with MPF modules.",
				"MPF Module Menu"
				);
		pli.addCategory("Data Processing");
		return pli;
	}
	
	
	@SuppressWarnings("serial")
	public JMenu getMenu() {
		
		PluginMenu<ProbeList> theMenu = new PluginMenu<ProbeList>("Data Processing", Constants.masterComponent) {

			@Override
			public void callPlugin(PluginInfo pli, List<ProbeList> selection) {
				ProbeListMenu.runProbeListPlugin(pli, selection);				
			}
			
			protected List<ProbeList> getSelection() {
				return ProbeListMenu.getCurrentSelection();
			}
			
			protected void filter(Set<PluginInfo> plis) {
				Set<PluginInfo> toRemove = new HashSet<PluginInfo>();
				for (PluginInfo pli : plis) {
					Boolean invisible = (Boolean)(pli.getProperties().get(Constants.NOT_IN_MENU));
					if (invisible!=null && invisible) {
						toRemove.add(pli);
					}			
				}
				plis.removeAll(toRemove);
			}
		};

		theMenu.fill();
		
		theMenu.add(theMenu.createPluginInfoMenuAction(PluginManager.getInstance().getPluginFromID("PAS.mpf")),false);
		
		return theMenu;
	}
	
	public int getPreferredPosition() {
		return 0;
	}


	@Override
	public void init() {
	}
	
	
}
