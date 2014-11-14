package mayday.vis3.tables;


import java.util.List;

import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.vis3.gui.Layouter;
import mayday.vis3.gui.AbstractTableWindow;
import mayday.vis3.model.Visualizer;

public abstract class TablePlugin extends AbstractPlugin implements ProbelistPlugin {

	public static String MC = MaydayDefaults.Plugins.CATEGORY_PLOT+"/Tables";
	
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public abstract AbstractTableWindow getTableWindow(Visualizer viz);
	
	@SuppressWarnings("unchecked")
	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) {
		Visualizer viz = new Visualizer(masterTable.getDataSet(),probeLists);
		AbstractTableWindow tw = getTableWindow(viz);
		tw.setVisible(true);
		viz.addPlot(tw);
		Layouter l = new Layouter(2,1);
		l.nextElement().placeWindow(tw);
		return null;
	}

}
