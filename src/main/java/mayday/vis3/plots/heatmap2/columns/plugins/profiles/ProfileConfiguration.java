package mayday.vis3.plots.heatmap2.columns.plugins.profiles;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.SortedExperiments;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public class ProfileConfiguration {

	protected ViewModel vm;
	protected SortedExperiments expSorting;
	protected HierarchicalSetting setting;


	double[] cache;
	Probe cachedProbe=null;

	public ProfileConfiguration(final HeatmapStructure struct) {
		vm = struct.getViewModel();
		expSorting = new SortedExperiments(vm);
		expSorting.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				struct.triggerInvalidate(); 
			}
		});
		setting = new HierarchicalSetting("Expression profiles").addSetting(expSorting.getSetting());
	}

	public double[] getProbeValue(Probe pb) {
		if (pb!=cachedProbe) {
			cache = vm.getProbeValues(pb);
			cachedProbe = pb;
		}		
		return cache;

	}
	
	public HierarchicalSetting getSetting() {
		return setting;
	}
	
	public SortedExperiments getSortOrder() {
		return expSorting;
	}

	public void dispose() {
		expSorting.dispose();
	}


}
