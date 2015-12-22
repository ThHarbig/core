package mayday.vis3.tables;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.gui.AbstractTableWindow;
import mayday.vis3.model.Visualizer;

public class PercentileTable extends TablePlugin {

	public final static String PLID = "PAS.vis3.PercentileTable";

	public void init() {
	}
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.ProbeListPercentiles",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes the percentiles of a visualizer",
				"Percentile Table"
		);
		pli.setIcon("mayday/vis3/percentiletable128.png");
		pli.addCategory("Tables");
		return pli;
	}

	@SuppressWarnings("unchecked")
	public AbstractTableWindow getTableWindow(Visualizer viz) {
		return new PercentileTableWindow(viz);
	}


}
