package mayday.core.plugins.menu;

/*
 * There is only ever one ProbeList menu existing. It is created during Pluma init and added to mayday's menu.
 * When an action is started from the menu, the menu asks the datasetmanagerview for the currently selected
 * dataset and calls the action for the selected probelist in that dataset.
 * The context menu in the ProbeListManagerView is the same menu, fetched via the instance stored in the 
 * DataSetManagerView (via setProbeListMenu()). 
 */


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.gui.PluginMenu;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuMakingPlugin;
import mayday.core.pluma.prototypes.MenuPlugin;
import mayday.core.probelistmanager.ProbeListManager;

public class ProbeListMenu extends AbstractPlugin implements MenuPlugin {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.ProbeListMenu",
				new String[0],
				Constants.MC_MENUBAR,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all probelist related plugins and builds the \"Probelist\" menu.",
				"ProbeList Menu"
				);
		return pli;
	}
	
	public void init() {
	}

	@SuppressWarnings("serial")
	public JMenu getMenu() {
		
		PluginMenu<ProbeList> theMenu = new PluginMenu<ProbeList>("Probe List", Constants.MC_PROBELIST) {

			@Override
			public void callPlugin(PluginInfo pli, List<ProbeList> selection) {
				runProbeListPlugin(pli, selection);				
			}
			
			@Override
			protected List<ProbeList> getSelection() {
				return getCurrentSelection0();
			}
			
		};
	    theMenu.setMnemonic( KeyEvent.VK_P );
	    
	    // Part 0 - Creation of new Probelists
	    Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(Constants.MC_PROBELIST_CREATE);
	    JMenu subm = new JMenu("Create...");
		for (PluginInfo pli : plis)
			subm.add(theMenu.createPluginInfoMenuAction(pli));

       	theMenu.add(subm,true);
       	theMenu.addSeparator();
		
	    // Part 1 - import GUDI items
		PluginInfo gudipl = PluginManager.getInstance().getPluginFromID("PAS.core.GUDI.ProbeList");
		if (gudipl!=null) { 
			try {
				for (JMenuItem item : ((MenuMakingPlugin)(gudipl.getInstance())).createMenu()) {
					theMenu.add(item, true);					
				}
			} catch (Throwable t) {
				System.err.println("ProbeList menu builder: "+t.getMessage());
				t.printStackTrace();
			};		
			theMenu.addSeparator();
		}
		
		// Part 2 - import GUDE items
		PluginInfo gudepl = PluginManager.getInstance().getPluginFromID("PAS.core.GUDE.ProbeList");
		if (gudepl!=null) { 
			try {
				for (JMenuItem item : ((MenuMakingPlugin)(gudepl.getInstance())).createMenu()) {
					theMenu.add(item, false);
				}
			} catch (Throwable t) {
				System.err.println("ProbeList menu builder: "+t.getMessage());
				t.printStackTrace();
			};		
			theMenu.addSeparator();
		}
	    		
	    // Part 3 - Probe List Plugins
		theMenu.fill();

		theMenu.addSeparator();
    
	    // Part 4 - Close action
        theMenu.add(new CloseProbelistAction(), false);	    
        
        // make the menu known to the datasetmanager
        registerMenuWithDSMV(theMenu);
        
		return theMenu;
	}

	public int getPreferredPosition() {
		return 3; 
	}
	
	@SuppressWarnings("serial")
	protected class CloseProbelistAction extends AbstractAction {
		public CloseProbelistAction() {
			super("Remove");
		}
		public void actionPerformed( ActionEvent event ) {
			ProbeListManager plm = getCurrentPLM();
			plm.getProbeListManagerView().getRemoveSelectionAction().actionPerformed(event);
		}
	}
	
	
	public static void runProbeListPlugin(PluginInfo pli, List<ProbeList> selection) 
	{
		ProbeListPluginRunner plpr = new ProbeListPluginRunner(pli, selection, null);
		plpr.execute();
	}
	
	public static ProbeListManager getCurrentPLM() {
		if (DataSetManagerView.getInstance().getSelectedDataSets().size()==0)
			return null;
		DataSet selDS = DataSetManagerView.getInstance().getSelectedDataSets().get(0);
		if (selDS==null)
			return null;
		// close selected probelists
		ProbeListManager plm = selDS.getProbeListManager();
		return plm;
	}
	
	protected List<ProbeList> getCurrentSelection0() {
		return ProbeListMenu.getCurrentSelection();
	}
	
	protected void registerMenuWithDSMV(PluginMenu<ProbeList> theMenu) {
		DataSetManagerView.getInstance().setProbeListMenu(theMenu);  
	}

	
	public static List<ProbeList> getCurrentSelection() {
		ProbeListManager plm = getCurrentPLM();
		if (plm!=null && plm.getProbeListManagerView()!=null) {
			Object[] so = plm.getProbeListManagerView().getSelectedValues();
			LinkedList<ProbeList> sel = new LinkedList<ProbeList>();
			for (Object o : so) {
				sel.add((ProbeList)o);
			}
			return sel;
		}
		return Collections.emptyList();
	}


}
