package mayday.vis3.plots.histogram;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class HistogramPlot extends PlotPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.HistogramPlot",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"A histogram of value frequencies",
				"Histogram"
		);
		pli.setIcon("mayday/vis3/histogram128.png");
		pli.addCategory("Distributions");
		setIsMajorPlot(pli);
		return pli;	
	}

	public Component getComponent() {
		Component myComponent;
		myComponent = new PlotWithLegendAndTitle(new HistogramPlotComponent());
		return myComponent;
	}

}
