package mayday.vis3.gui.menu;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.PluginMenu;
import mayday.core.gui.WindowListWindow;
import mayday.core.plugins.menu.ProbeListMenu;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.settings.Setting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.components.HeavyWeightWorkaround;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.gui.actions.ExperimentSelectionClearAction;
import mayday.vis3.gui.actions.ExperimentSelectionCreateVisualizerAction;
import mayday.vis3.gui.actions.ExperimentSelectionInvertAction;
import mayday.vis3.gui.actions.ExperimentSelectionSelectionSendToVisualizerAction;
import mayday.vis3.gui.actions.ExperimentSelectionSynchronizeWithVisualizerAction;
import mayday.vis3.gui.actions.ProbeSelectionAddProbeAction;
import mayday.vis3.gui.actions.ProbeSelectionClearAction;
import mayday.vis3.gui.actions.ProbeSelectionCreateVisualizerAction;
import mayday.vis3.gui.actions.ProbeSelectionInvertAction;
import mayday.vis3.gui.actions.ProbeSelectionOpenProbeMover;
import mayday.vis3.gui.actions.ProbeSelectionRemoveFromProbeListAction;
import mayday.vis3.gui.actions.ProbeSelectionSendToVisualizerAction;
import mayday.vis3.gui.actions.ProbeSelectionSendToProbeList;
import mayday.vis3.gui.actions.ProbeSelectionSynchronizeWithVisualizerAction;
import mayday.vis3.gui.actions.ProbeSelectionToProbelistAction;
import mayday.vis3.gui.actions.ProbeSelectionToProbelistBipartitionAction;
import mayday.vis3.gui.actions.ProbelistAddAction;
import mayday.vis3.gui.actions.ProbelistRemoveAction;
import mayday.vis3.model.ViewModel;

public abstract class MenuManager {
	
	protected Window parent;
	protected PlotContainer container;
	
	protected Object menuBarEquivalent;
	protected Method additionMethod;
	
	protected boolean selectOnProbes;
	protected boolean selectOnExperiments;

	protected HashMap<String, JMenu> menu = new HashMap<String, JMenu>();
	
	protected HashMap<Object, String> titles = new HashMap<Object, String>();
	
	protected MenuMapAdapter view = new MenuMapAdapter();
	protected MenuMapAdapter enhance = new MenuMapAdapter();
	protected MultiHashMap<Object, Setting> viewSettings  = new MultiHashMap<Object, Setting>();
	
	protected ViewSettingsMenu specialViewMenu; 
	
	public MenuManager(Window parent, PlotContainer container, Object aMenuBar, boolean selectProbes, boolean selectExperiments) {
		this.parent = parent;
		this.container=container;
		menuBarEquivalent = aMenuBar;
		selectOnProbes = selectProbes;
		selectOnExperiments = selectExperiments;
		
		try {
			if (menuBarEquivalent instanceof JMenuBar)
				additionMethod = aMenuBar.getClass().getMethod("add", JMenu.class);
			else if (menuBarEquivalent instanceof JMenu)
				additionMethod = aMenuBar.getClass().getMethod("add", JMenuItem.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		createStandardMenus();	
	}
	
	public MenuManager(Window parent, PlotContainer container, Object aMenuBar) {
		this(parent, container, aMenuBar, true, false);
	}
	
	public void addMenu(JMenu jm) {
		menu.put(jm.getText(),jm);
		
		// we need heavy popups to make jogl plots work with popups
		HeavyWeightWorkaround.forceHeavyWeightPopups(jm);
		
		try {
			additionMethod.invoke(menuBarEquivalent, jm);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}	
	}
	
	public JMenu getMenu(String name, PlotComponent askingObject) {
		if (name.equals(PlotContainer.VIEW_MENU))
			return view.createCloneWithKnowledgeOf(askingObject);
		if (name.equals(PlotContainer.ENHANCE_MENU))
			return enhance.createCloneWithKnowledgeOf(askingObject);
		//asking object is only used for view and enhance menus
		return menu.get(name);
	}
	
	public void addViewSetting(Setting s, PlotComponent askingObject ) {
		viewSettings.put(askingObject, s);
	}
	
	public void setPreferredTitle(String s, Object askingObject) {
		titles.put(askingObject, s);
	}
	
	public String getPreferredTitle() {
		if (titles.containsKey(null))
			return titles.get(null); // the null title has precedence
		
		if (titles.size()==1) // use only available title if there is just one
			return titles.values().iterator().next();
		
		Set<Object> s = new HashSet<Object>(view.getMap().keySet());
		s.addAll(enhance.getMap().keySet());
		s.addAll(viewSettings.keySet());
		
		if (s.size()==1)
			return titles.get(s.iterator().next());	
		
		return "Multiplot";
	}

	protected void createStandardMenus() {
		addMenu(makeFileMenu());		
		addMenu(makeProbeListMenu());
		if (selectOnProbes)
			addMenu(makeProbeSelectionMenu());
		if (selectOnExperiments)
			addMenu(makeExperimentSelectionMenu());
		// add the SPECIAL view menu now
		addMenu(makeSettingsMenu());		
		// 	this is filled by the visualizer
		addMenu(makeVisualizerMenu());	
		
		JMenu windows = WindowListWindow.getMenuElement();
		addMenu(windows);
		
	}
	
	protected abstract JMenu makeFileMenu();
	
	protected JMenu makeProbeListMenu() {
		JMenu probeLists = new JMenu("ProbeLists");
		probeLists.setMnemonic('L');
		ViewModel vm = container.getViewModel();
		probeLists.add(new ProbelistAddAction(vm));
		probeLists.add(new ProbelistRemoveAction(vm));
		return probeLists;
	}
	
	protected JMenu makeProbeSelectionMenu() {
		JMenu selection = new JMenu(PlotContainer.PROBE_SELECTION_MENU);
		selection.setMnemonic('S');
		ViewModel vm = container.getViewModel();
		selection.add(new ProbeSelectionClearAction(vm));
		selection.add(new ProbeSelectionInvertAction(vm));
		selection.add(new ProbeSelectionAddProbeAction(vm));
		selection.add(new JSeparator());
		selection.add(new ProbeSelectionToProbelistAction(vm));
		selection.add(new ProbeSelectionToProbelistBipartitionAction(vm));
		selection.add(new ProbeSelectionRemoveFromProbeListAction(vm));
		selection.add(new ProbeSelectionSendToProbeList(vm));
		selection.add(new ProbeSelectionOpenProbeMover(vm));
		selection.add(new JSeparator());
		selection.add(new ProbeSelectionCreateVisualizerAction(vm));
		selection.add(new ProbeSelectionSendToVisualizerAction(vm));
		selection.add(new ProbeSelectionSynchronizeWithVisualizerAction(vm));
		selection.add(new JSeparator());
		
		ProbeListMenu myplmenu = new ProbeListMenu() {
			@Override
			protected List<ProbeList> getCurrentSelection0() {
				ViewModel vm = container.getViewModel();
				ProbeList tmp = new ProbeList(vm.getDataSet(), false);
				for (Probe pb : vm.getSelectedProbes())
					tmp.addProbe(pb);
				tmp.setName("Selected Probes from Visualizer "+vm.getVisualizer().getName());
				return(Arrays.asList(new ProbeList[]{tmp}));
			}
			@Override
			protected void registerMenuWithDSMV(PluginMenu<ProbeList> theMenu) {
				// do nothing, we do NOT want to replace the DSMV menu with this weird menu!
			}
		};		
		JMenu probeListActions = myplmenu.getMenu();
		probeListActions.setText("Run ProbeList plugin");		
		selection.add(probeListActions);
		
		selection.add(new JSeparator());
		JMenu probeActions = new ProbeMenu(
				vm.getSelectedProbes(), 
				vm.getDataSet().getMasterTable()).getMenu();
		
		selection.add(probeActions);		
		return selection;
	}
	
	protected JMenu makeExperimentSelectionMenu() {
		JMenu selection = new JMenu(PlotContainer.EXPERIMENT_SELECTION_MENU);
		selection.setMnemonic('E');
		ViewModel vm = container.getViewModel();
		selection.add(new ExperimentSelectionClearAction(vm));
		selection.add(new ExperimentSelectionInvertAction(vm));
		selection.add(new JSeparator());
		selection.add(new ExperimentSelectionSelectionSendToVisualizerAction(vm));
		selection.add(new ExperimentSelectionSynchronizeWithVisualizerAction(vm));
		selection.add(new ExperimentSelectionCreateVisualizerAction(vm));
		return selection;
	}
	
	protected JMenu makeSettingsMenu() {
		specialViewMenu = new ViewSettingsMenu(view, viewSettings, enhance, parent, titles, this);
		specialViewMenu.setMnemonic('V');
		return specialViewMenu;
	}
	
	protected JMenu makeVisualizerMenu() {
		JMenu visualizer = new JMenu("Visualizer"); 
		visualizer.setMnemonic('z');
		return visualizer;
	}
}
