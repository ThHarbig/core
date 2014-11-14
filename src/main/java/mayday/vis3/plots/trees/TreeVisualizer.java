package mayday.vis3.plots.trees;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class TreeVisualizer extends PlotPlugin {

	public void init() {
	}

		public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.clustering.TreeVisualizer2",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Visualizer for trees derived from clusterings",
				"Tree Visualizer"
		);
		pli.setIcon("mayday/vis3/tree128.png");
		return pli;	
	}

	public Component getComponent() {
		PlotWithLegendAndTitle myComponent;
		TreeVisualizerComponent tvc = new TreeVisualizerComponent();
		myComponent = new PlotWithLegendAndTitle(new PlotScrollPane(tvc));
		myComponent.setTitledComponent(tvc);
		return myComponent;
	}

}
