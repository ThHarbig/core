package mayday.vis3.plots.silhouette;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class SilhouettePlot extends PlotPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.SilhouettePlot",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"A silhouette plot to assess clustering quality",
				"Silhouette Plot"
		);
		pli.setIcon("mayday/vis3/silhouetteplot128.png");
		return pli;	
	}

	public Component getComponent() {
		Component myComponent;
		myComponent = new PlotWithLegendAndTitle(new SilhouettePlotComponent());
		return myComponent;
	}

}
