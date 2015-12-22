package mayday.vis3.plots.scatter;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class ScatterPlot extends PlotPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.incubator.ScatterPlot",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"A 2D scatter plot comparing experiments or meta information",
				"Scatter Plot"
		);
		pli.setIcon("mayday/vis3/scatter128.png");
		pli.addCategory("Scatter plots");
		pli.setMenuName("\0Scatter Plot");
		setIsMajorPlot(pli);
		return pli;	
	}

	public Component getComponent() {
		Component myComponent;
		myComponent = new PlotWithLegendAndTitle(new ScatterPlotComponent());
		return myComponent;
	}

}
