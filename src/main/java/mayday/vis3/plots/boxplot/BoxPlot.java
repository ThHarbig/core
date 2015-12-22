package mayday.vis3.plots.boxplot;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class BoxPlot extends PlotPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.incubator.BoxPlot",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Philipp Bruns",
				"bruns@informatik.uni-tuebingen.de",
				"A box plot showing the characteristics of probe experiment value distributions",
				"Box Plot"
		);
		pli.setIcon("mayday/vis3/boxplot128.png");
		pli.addCategory("Distributions");
		setIsMajorPlot(pli);
		return pli;	
	}

	public Component getComponent() {
		Component myComponent;
		myComponent = new PlotWithLegendAndTitle(new BoxPlotComponent());
		return myComponent;
	}

}
