package mayday.vis3;

import java.awt.Component;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.vis3.model.Visualizer;

public abstract class PlotPlugin extends AbstractPlugin implements ProbelistPlugin {
	
	public abstract Component getComponent();

	
	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) {
		Visualizer.createWithPlot(masterTable.getDataSet(), probeLists, getComponent());
		return null;
	}
	
	protected final static String IS_MAJOR_PLOT="vis3_major_plot"; 
	
	protected static void setIsMajorPlot(PluginInfo pli) {
		pli.getProperties().put(IS_MAJOR_PLOT, Boolean.TRUE);
	}
	
	public static boolean isMajorPlot(PluginInfo pli) {
		Object o = pli.getProperties().get(IS_MAJOR_PLOT);
		return (o!=null && Boolean.TRUE.equals(o));
	}
	
}
