package mayday.vis3.plots.multiprofile;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class MultiProfilePlot extends PlotPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.incubator.MultiProfilePlot",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"A profile plot for each selected probe list",
				"Multi Profile Plot"
		);
		pli.setIcon("mayday/vis3/multiprofile128.png");
		pli.addCategory("Expression Profiles");
		return pli;	
	}

	public Component getComponent() {
		PlotWithLegendAndTitle myComponent;
		MultiProfilePlotComponent mpp = new MultiProfilePlotComponent();
		myComponent = new PlotWithLegendAndTitle(new PlotScrollPane(mpp));
		myComponent.setTitledComponent(null);
		return myComponent;
	}

}
