package mayday.vis3.plots.heatmap2;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.plots.heatmap2.component.HeatmapOuterComponent;

public class HeatMap extends PlotPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.Heatmap4",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"The Enhanced Heatmap",
				"HeatMap"
		);
		pli.setIcon("mayday/vis3/heatmap128.png");
		pli.addCategory("Expression Profiles");
		setIsMajorPlot(pli);
		return pli;	
	}

	public Component getComponent() {
		PlotWithLegendAndTitle myComponent;
		myComponent = new PlotWithLegendAndTitle();
		HeatmapOuterComponent hmc = new HeatmapOuterComponent(myComponent);
		myComponent.setPlot(hmc);
		return myComponent;
	}

}
