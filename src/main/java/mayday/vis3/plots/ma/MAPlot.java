package mayday.vis3.plots.ma;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class MAPlot extends PlotPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.MAPlot",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"A 2D scatter plot of M (x1-x2) and A ((x1+x2)/2) values ",
				"MA Plot"
		);
		pli.setIcon("mayday/vis3/ma128.png");
		pli.addCategory("Scatter plots");
		return pli;	
	}

	public Component getComponent() {
		Component myComponent;
		myComponent = new PlotWithLegendAndTitle(new MAPlotComponent());	
		return myComponent;
	}

}
