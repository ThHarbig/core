package mayday.vis3.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import mayday.core.MaydayDefaults;
import mayday.core.gui.PluginMenu;
import mayday.core.gui.components.MenuOverflowLayout;
import mayday.core.gui.components.ToolbarOverflowLayout;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.vis3.PlotPlugin;
import mayday.vis3.gui.actions.AddPlotAction;
import mayday.vis3.gui.actions.AddTableAction;
import mayday.vis3.model.Visualizer;
import mayday.vis3.tables.TablePlugin;

public class PluginSorter {

	@SuppressWarnings("unchecked")
	/** creates a sorted list of visualizer plugins. resulting list contains sorted abstractaction objects for adding the window to the visualizer
	 * and, if separateTables==true, the "null" value is used to separate plots and plugins
	 * and, if separateMajor==true, the "null" value is used to separate major plots from minor/categorized plots 
	 * and, if separateCategories==true, the Category Title (String) is used to separate plugin categories 
	 */
	public static List<Object> createActionList(Visualizer viz, boolean separateTables, boolean separateMajor, boolean separateCategories) {
		
		List<Object> res = new LinkedList<Object>();
		
		// first all the table plugins
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(TablePlugin.MC))
			res.add(new AddTableAction(pli, viz));
		
		if (separateTables && res.size()>0)
			res.add(null);
		
		// now sort all plot plugins
		// first priority: "MAJOR" plots (PluginInfo.getProperties().get("IS_MAJOR_PLOT")==TRUE);
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(MaydayDefaults.Plugins.CATEGORY_PLOT);
		MultiTreeMap<String, PluginInfo> sortedPli = new MultiTreeMap<String, PluginInfo>();
		
		for (PluginInfo pli : plis) {
			if (PlotPlugin.isMajorPlot(pli)) {
				res.add(new AddPlotAction(pli, viz));
			} else { 
				if (pli.getProperties().get(Constants.CATEGORIES)==null)
					pli.addCategory("Miscellaneous");
				Vector<String> plpath = (Vector<String>)(pli.getProperties().get(Constants.CATEGORIES));
				for (String cat : plpath)
					sortedPli.put(cat, pli);			
			}
		}
		
		if (separateMajor && res.size()>0)
			res.add(null);
		
		// add by category
		for (String cat : sortedPli.keySet()) {
			if (separateCategories && res.size()>0)
				res.add(cat);	
			for (PluginInfo pli : sortedPli.get(cat))
				res.add(new AddPlotAction(pli, viz));
		}
		
		return res;

	}
	
	/** creates a sorted list of visualizer plugins. resulting list contains sorted plugininfo objects 
	 * and, if separateTables==true, the "null" value is used to separate plots and plugins
	 * and, if separateMajor==true, the "null" value is used to separate major plots from minor/categorized plots 
	 * and, if separateCategories==true, the Category Title (String) is used to separate plugin categories 
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> createPluginInfoList(boolean separateTables, boolean separateMajor, boolean separateCategories) {
		
		List<Object> res = new LinkedList<Object>();
		
		// first all the table plugins
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(TablePlugin.MC))
			res.add(pli);
		
		if (separateTables && res.size()>0)
			res.add(null);
		
		// now sort all plot plugins
		// first priority: "MAJOR" plots (PluginInfo.getProperties().get("IS_MAJOR_PLOT")==TRUE);
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(MaydayDefaults.Plugins.CATEGORY_PLOT);
		MultiTreeMap<String, PluginInfo> sortedPli = new MultiTreeMap<String, PluginInfo>();
		
		for (PluginInfo pli : plis) {
			if (PlotPlugin.isMajorPlot(pli)) {
				res.add(pli);
			} else { 
				if (pli.getProperties().get(Constants.CATEGORIES)==null)
					pli.addCategory("Miscellaneous");
				Vector<String> plpath = (Vector<String>)(pli.getProperties().get(Constants.CATEGORIES));
				for (String cat : plpath)
					sortedPli.put(cat, pli);			
			}
		}
		
		if (separateMajor && res.size()>0)
			res.add(null);
		
		// add by category
		for (String cat : sortedPli.keySet()) {
			if (separateCategories && res.size()>0)
				res.add(cat);	
			for (PluginInfo pli : sortedPli.get(cat))
				res.add(pli);
		}
		
		return res;

	}
	
	public static JMenu createSubMenu(Visualizer viz) {
		JMenu m = new JMenu("Add window");
		m.setLayout(new MenuOverflowLayout());
		boolean firstCat = true;
		for (Object elem : createActionList(viz, true, true, true)) {
			if (elem == null)
				m.addSeparator();
			else if (elem instanceof String) {
				if (!firstCat)
					m.add(new PluginMenu.TitleMenuComponentAction(""));
				else
					firstCat = false;
				m.add(new PluginMenu.TitleMenuComponentAction((String)elem));
			} else if (elem instanceof AbstractAction) {
				m.add((AbstractAction)elem);
			}
		}
		return m;
	}
	
	public static JToolBar createToolbar(Visualizer viz) {
		JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
		tb.setLayout(new ToolbarOverflowLayout(true, 2));
		for (Object elem : createActionList(viz, true, true,false)) {
			if (elem == null)
				tb.addSeparator();
			else if (elem instanceof String) {
				tb.addSeparator();
			} else if (elem instanceof AbstractAction) {
				tb.add((AbstractAction)elem);
			}
		}
		
		return tb;
	}
	
}
