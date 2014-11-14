package mayday.vis3.plots.multiprofile;

import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

import mayday.core.Preferences;
import mayday.core.ProbeList;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.legend.SimpleTitle;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.profile.ProfilePlotComponent;
import mayday.vis3.vis2base.DataSeries;


@SuppressWarnings("serial")
public class MultiProfilePlotComponent extends MultiPlotPanel implements ViewModelListener, PlotContainer {

	protected DataSeries selectionLayer;
	protected DataSeries[] Layers;
	protected ViewModel viewModel;
	
	protected MultiHashMap<String, Setting> sub_settings = new MultiHashMap<String, Setting>();
	protected boolean now_adding_master_setting = false;
	protected SettingChangeListener subsetting_updater = new SettingChangeListener() {
		public void stateChanged(SettingChangeEvent e) {
			if (now_adding_master_setting)
				return;
			// first go up until we find matching parent settings 			
			Setting s = ((Setting)e.getSource());
			Preferences newValues = s.toPrefNode();
			
			List<Setting> targets = sub_settings.get(s.getName());
			for (Object o : e.getAdditionalSources()) {
				if (targets!=null && targets.size()>0)
					break;
				targets = sub_settings.get(((Setting)o).getName());
			}
			if (targets==null || targets.size()==0)
				return;
			// now go down the hierarchy to search for the specific setting we want to change
			for (Setting sub : targets) {
				if (sub instanceof HierarchicalSetting) {
					sub = ((HierarchicalSetting) sub).getChild(s.getName(), true);
				}
				if (sub!=null && sub.getName().equals(s.getName()))
					sub.fromPrefNode(newValues);
			}
		}
	};

	public MultiProfilePlotComponent() {
	}

	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		viewModel = plotContainer.getViewModel();
		viewModel.addViewModelListener(this);
		plotContainer.setPreferredTitle("Multi Profile Plot", this);
		zoomController.setAllowXOnlyZooming(true);
		zoomController.setAllowYOnlyZooming(true);
		zoomController.setActive(true);
		//		setPreferredSize(new Dimension(640,480));

		//add one common profileplot setting to rule them all -- this should be done in a generic fashion in MultiPlotPanel
		ProfilePlotComponent fake_ppc = new ProfilePlotComponent() {
			public String getPreferredTitle() {
				return "Configure all plots";
			}
		};
		now_adding_master_setting = true;
		fake_ppc.setup(this);
		new SimpleTitle("",fake_ppc).setup(this);
		now_adding_master_setting = false;
		
		updatePlot();
	}


	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED: // fallthrouh
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED:
			updatePlot();
			break;
		case ViewModelEvent.PROBE_SELECTION_CHANGED: // ignore
			break;
		}	
	}

	public void updatePlot() {
		int oldNumber = plots.length;
		LinkedList<Component> pcs = new LinkedList<Component>();
		for (ProbeList pl : viewModel.getProbeLists(false)) {
			ProfilePlotComponentMulti ppcm = new ProfilePlotComponentMulti(pl);
			PlotWithLegendAndTitle pwlat = new PlotWithLegendAndTitle();
			pwlat.setPlot(ppcm);
			pwlat.setTitle(new SimpleTitle(pl.getName(),ppcm));			
			pwlat.setLegend(null);
			pcs.add((Component)pwlat);
		}

		sub_settings.clear(); // -- this should be done in a generic fashion in MultiPlotPanel

		if (oldNumber==pcs.size())
			setPlots(pcs, dimensions);
		else 
			setPlots(pcs);
	}

	public void addViewSetting(Setting s, PlotComponent askingObject) {
		if (!now_adding_master_setting)
			sub_settings.put(s.getName(), s);
		else
			s.addChangeListener(subsetting_updater);
		
		super.addViewSetting(s, askingObject);		
	}





}

