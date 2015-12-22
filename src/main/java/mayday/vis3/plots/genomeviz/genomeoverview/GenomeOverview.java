package mayday.vis3.plots.genomeviz.genomeoverview;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class GenomeOverview extends PlotPlugin{

	
	public Component getComponent() {
		PlotWithLegendAndTitle myComponent;
		GenomeOverviewComponent goc = new GenomeOverviewComponent();
		myComponent = new PlotWithLegendAndTitle(goc);
		myComponent.setTitledComponent(goc.getComponentForTitling());
		return myComponent;
	}
	
	public void init() {
	}
	
	public PluginInfo register() throws PluginManagerException {

		PluginInfo pli = new PluginInfo(
				this.getClass(), 
				"PAS.zippi.GenomeBrowser", 
				new String[0], 
				MaydayDefaults.Plugins.CATEGORY_PLOT, 
				new HashMap<String, Object>(), 
				"Christian Zipplies", 
				"zipplies@informatik.uni-tuebingen.de", 
				"The overview of genome", 
				"Genome Browser"
		);
		pli.setIcon("mayday/vis3/genomeoverview128.png");
		pli.addCategory("Genomic");
		pli.setMenuName("Genome Browser");
		return pli;
	}

}
