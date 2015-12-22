package mayday.core.plugins.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mayday.core.DataSet;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.gui.PluginMenu;
import mayday.core.pluginrunner.DataSetPluginRunner;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuMakingPlugin;
import mayday.core.pluma.prototypes.MenuPlugin;

public class DataSetMenu extends AbstractPlugin implements MenuPlugin {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DataSetMenu",
				new String[0],
				Constants.MC_MENUBAR,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all dataset related plugins and builds the \"DataSet\" menu.",
				"DataSet Menu"
				);
		return pli;
	}
	
	public void init() {
	}

	@SuppressWarnings({ "serial" })
	public JMenu getMenu() {
		
		PluginMenu<DataSet> theMenu = new PluginMenu<DataSet>("Data Set", Constants.MC_DATASET) {

			public void callPlugin(PluginInfo pli, List<DataSet> selection) {
				runDataSetPlugin(pli, selection);
			}
			
			@Override
			protected List<DataSet> getSelection() {
				return DataSetManagerView.getInstance().getSelectedDataSets();
			}
			
		};
		
	    theMenu.setMnemonic( KeyEvent.VK_S );

	    // Part 1 - import GUDI items
		PluginInfo gudipl = PluginManager.getInstance().getPluginFromID("PAS.core.GUDI.DataSet");
		if (gudipl!=null) { 
			try {
				for (JMenuItem item : ((MenuMakingPlugin)(gudipl.getInstance())).createMenu()) {
					theMenu.add(item, true);					
				}
			} catch (Throwable t) {
				System.err.println("DataSet menu builder: "+t.getMessage());
				t.printStackTrace();
			};		
			theMenu.addSeparator();
		}
		
		// Part 2 - import GUDE items
		PluginInfo gudepl = PluginManager.getInstance().getPluginFromID("PAS.core.GUDE.DataSet");
		if (gudepl!=null) { 
			try {
				for (JMenuItem item : ((MenuMakingPlugin)(gudepl.getInstance())).createMenu()) {
					theMenu.add(item, false);
				}
			} catch (Throwable t) {
				System.err.println("DataSet menu builder: "+t.getMessage());
				t.printStackTrace();
			};		
			theMenu.addSeparator();
		}
		
		theMenu.fill();
		
       	theMenu.addSeparator();
        
        // Part 4 - Close Action
        theMenu.add(new AbstractAction("Remove") {
			public void actionPerformed(ActionEvent e) {
        		LinkedList<DataSet> removeCand = new LinkedList<DataSet>(DataSetManagerView.getInstance().getSelectedDataSets());
        		for (DataSet ds : removeCand)
        			DataSetManagerView.getInstance().closeDataSet(ds);        		
        	}
        }, false);
        
        // make ourselves known to the datasetmanager
        DataSetManagerView.getInstance().setDataSetMenu(theMenu);
              
		return theMenu;
	}

	public int getPreferredPosition() {
		return 2; 
	}
	
	public static void runDataSetPlugin(PluginInfo pli, List<DataSet> selection) 
	{
		DataSetPluginRunner plpr = new DataSetPluginRunner(pli, selection);
		plpr.execute();
	}
}
