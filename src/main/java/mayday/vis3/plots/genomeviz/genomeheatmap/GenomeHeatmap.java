package mayday.vis3.plots.genomeviz.genomeheatmap;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;
 
public class GenomeHeatmap extends PlotPlugin {
	
	public Component getComponent() {
		PlotWithLegendAndTitle myComponent;
		GenomeHeatMapComponent ghc = new GenomeHeatMapComponent();
		myComponent = new PlotWithLegendAndTitle(ghc);
		myComponent.setTitledComponent(ghc.getTable());
		return myComponent;
	}


	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {

		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(), 
				"PAS.zippi.HeatStream", 
				new String[0], 
				MaydayDefaults.Plugins.CATEGORY_PLOT, 
				new HashMap<String, Object>(), 
				"Christian Zipplies", 
				"zipplies@informatik.uni-tuebingen.de", 
				"The expression image plot", 
				"Genome HeatStream"
		);
		pli.setIcon("mayday/vis3/chromeheatmap128.png");
		pli.addCategory("Genomic");
		pli.setMenuName("Genome HeatStream");
		return pli;
	}

}
