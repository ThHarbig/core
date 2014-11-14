package mayday.vis3.model;

import java.awt.Component;
import java.awt.Window;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import mayday.core.DataSet;
import mayday.core.DataSetEvent;
import mayday.core.DataSetListener;
import mayday.core.ProbeList;
import mayday.vis3.gui.Layouter;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.gui.PluginSorter;
import mayday.vis3.gui.actions.CloseAllAction;
import mayday.vis3.gui.actions.CloseOthersAction;
import mayday.vis3.gui.actions.CombineAllOpenPlotsAction;
import mayday.vis3.gui.actions.ShowAllAction;
import mayday.vis3.gui.actions.ShowPlotAction;
import mayday.vis3.tables.ExpressionTableWindow;

public class Visualizer implements DataSetListener {

	protected static int nextID=0;
	
	protected final int groupID = (++nextID);
	
	public final static HashMap<DataSet, LinkedList<Visualizer>> openVisualizers = new HashMap<DataSet,LinkedList<Visualizer>>();
	
	protected ViewModel viewModel;
	protected final LinkedList<VisualizerMember> openPlots = new LinkedList<VisualizerMember>();
	
 	protected Visualizer() {} // for internal use in derived classes
	
	public Visualizer(DataSet ds, List<ProbeList> initialSelection) {		
		viewModel = new ViewModel(this, ds, initialSelection);
		viewModel.addViewModelListener(new ViewModelListener() {
			public void viewModelChanged(ViewModelEvent vme) {
				if (viewModel.getProbeLists(false).size()==0) {
					viewModel.removeViewModelListener(this);
					while (openPlots.size()>0)
						removePlot(openPlots.get(0));
				}
			}
		});
		ds.addDataSetListener(this);		
		addVisualizer(ds, this);
	}
	
	public int getID() {
		return groupID;
	}
	
	public String getName() {
		return "Visualizer <"+getID()+">";
	}
	
	public void addPlot(VisualizerMember plot) {
		openPlots.add(plot);		
		updateTitles();
		updateVisualizerMenus();
	}
	
	public void removePlot(VisualizerMember plot) {
		if (openPlots.contains(plot)) {
			openPlots.remove(plot);
			plot.closePlot();
			updateTitles();
			updateVisualizerMenus();
		}
		// close complete visualizer
		if (openPlots.size()==0) {
			removeVisualizer(viewModel.getDataSet(), this);
			viewModel.dispose();
		}
	}
	
	public ViewModel getViewModel() {
		return viewModel;
	}


	public void dataSetChanged(DataSetEvent event) {
		if (event.getChange()==DataSetEvent.CLOSING_CHANGE) {
			while (openPlots.size()>0)
				removePlot(openPlots.get(0));
			openPlots.clear();
			viewModel.dispose();
			((DataSet)event.getSource()).removeDataSetListener(this);
		}
	}

	
	public void updateTitles() {
		HashSet<String> newTitles = new HashSet<String>();
		for (VisualizerMember pgm : openPlots) {
			String oldTitle = pgm.getPreferredTitle();
			
			String prefix = viewModel.getDataSet().getName()+" <"+this.groupID+"> ";
			
			String suffix = " (";
			for (ProbeList pl : viewModel.getProbeLists(false)) {
				suffix+=pl.getName()+", ";
			}
			if (suffix.length()>2) 
				suffix = suffix.substring(0, suffix.length()-2)+")";				
			else
				suffix="";
			
			String newTitle = oldTitle;
			int modifier = 1;
			while (newTitles.contains(newTitle)) {
				newTitle = oldTitle + " ("+(++modifier)+")";
			}
			newTitles.add(newTitle);
			pgm.setTitle(prefix+newTitle+suffix);
		}
	}
	
	public void updateVisualizerMenus() {
		TreeMap<String, VisualizerMember> sorted = new TreeMap<String, VisualizerMember>();		
		for(VisualizerMember pgm : openPlots)  {
			String title = pgm.getTitle();
			sorted.put(title, pgm);
		}
		for(VisualizerMember pgm : openPlots) {			
			JMenu menu = pgm.getVisualizerMenu();
			menu.removeAll();
			menu.add(getViewModel().getDataManipulator().getMenu());
			menu.add(getViewModel().getProbeListSorter().getSetting().getMenuItem( 
					(pgm instanceof Window ? (Window)pgm : null)
			));
			menu.add(new JSeparator());
			menu.add(new ShowAllAction(this));
			menu.add(new CloseAllAction(this));
			menu.add(new CloseOthersAction(this, pgm));
			menu.add(new JSeparator());			
			menu.add(new CombineAllOpenPlotsAction(this));
			menu.add(new JSeparator());
			
			JMenu addVis = PluginSorter.createSubMenu(this); 
				
//				new JMenu("Add plot");
//			for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(MaydayDefaults.Plugins.CATEGORY_PLOT)) 
//				addVis.add(new AddPlotAction(pli, this));
//
//			for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(TablePlugin.MC))
//				addVis.add(new AddTableAction(pli, this));
			
			menu.add(addVis);
			menu.add(new JSeparator());

			
			for (String title: sorted.keySet()) {
				VisualizerMember otherWindow = sorted.get(title);
				JMenuItem jmi;
				if (otherWindow==pgm) {
					jmi = new JMenuItem(pgm.getPreferredTitle());
					jmi.setEnabled(false);
				} else {
					jmi = new JMenuItem(new ShowPlotAction(title, otherWindow));
				}
				menu.add(jmi);
			}
		}
	}
	
	
	public Collection<VisualizerMember> getMembers() {
		return openPlots;
	}
	
	
	
	public static Visualizer createWithPlotAndTable(DataSet ds, List<ProbeList> selectedPL, Component plotComponent) {
		Visualizer viz = new Visualizer(ds,selectedPL);
		ExpressionTableWindow tw = new ExpressionTableWindow(viz);
		PlotWindow pw = new PlotWindow(plotComponent, viz);
		viz.addPlot(tw);
		viz.addPlot(pw);
		tw.setVisible(true);
		pw.setVisible(true);
		Layouter l = new Layouter(2,1);
		l.nextElement().placeWindow(tw);
		l.nextElement().placeWindow(pw);
		return viz;
	}
	
	public static Visualizer createWithPlot(DataSet ds, List<ProbeList> selectedPL, Component plotComponent) {
		Visualizer viz = new Visualizer(ds,selectedPL);
		if (plotComponent==null)
			return viz;
		PlotWindow pw = new PlotWindow(plotComponent, viz);
		viz.addPlot(pw);
		pw.setVisible(true);
		Layouter l = new Layouter(2,1);
		l.nextElement().placeWindow(pw);
		return viz;
	}
	
	
	protected static void addVisualizer(DataSet ds, Visualizer vs) {
		LinkedList<Visualizer> l = openVisualizers.get(ds);
		if (l==null)
			openVisualizers.put(ds, l=new LinkedList<Visualizer>());
		l.add(vs);
	}
	
	protected static void removeVisualizer(DataSet ds, Visualizer vs) {
		LinkedList<Visualizer> l = openVisualizers.get(ds);
		if (l!=null) {
			l.remove(vs);
			if (l.size()==0)
				openVisualizers.remove(ds);
		}
	}
	
}
