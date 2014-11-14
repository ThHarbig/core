package mayday.vis3.model.summarize;


import java.util.HashMap;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.math.average.IAverage;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectOrderSetting;
import mayday.core.settings.typed.AveragingSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.gui.Layouter;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.wrapped.WrappedMasterTable;
import mayday.vis3.model.wrapped.WrappedProbeList;
import mayday.vis3.tables.ExpressionTableWindow;

public class SummaryVisualizer extends AbstractPlugin implements ProbelistPlugin {

	public final static String PLID = "PAS.vis3.visualizer.summary";
	
	public void init() {
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				PLID,
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a new visualizer showing summarized experiments",
				"Combined experiments (e.g. replicates)"
		);
		pli.addCategory("Visualization (derived data)");
		return pli;	
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		ClassSelectionModel csm = new ClassSelectionModel(masterTable);
		
		ClassSelectionSetting css = new ClassSelectionSetting("Select experiment groups", null, csm , 1, masterTable.getNumberOfExperiments(), masterTable.getDataSet());
		css.setLayoutStyle(ClassSelectionSetting.LayoutStyle.FULL);
		AveragingSetting avs = new AveragingSetting();
		HierarchicalSetting hs = new HierarchicalSetting("Experiment combination").addSetting(avs).addSetting(css);
		
		SettingDialog sd = new SettingDialog(null, "Group experiments", hs);
		sd.showAsInputDialog();
		if (sd.canceled())
			return null;
		
		createVisualizer(css.getModel(), avs.getSummaryFunction(), masterTable, probeLists);
		
		return null;
	}
	
	public static void createVisualizer(ClassSelectionModel csm, IAverage summary, MasterTable masterTable, List<ProbeList> probeLists) {

		List<String> le = csm.getClassesLabels();
		
		ObjectOrderSetting<String> oos = new ObjectOrderSetting<String>("Experiment order", null, le)
		.setLayoutStyle(ObjectOrderSetting.LayoutStyle.LIST_WITH_BUTTONS);
		
		SettingDialog sd = new SettingDialog(null, "Define new experiment order", oos);
		sd.showAsInputDialog();
		if (sd.canceled())
			return;

		le  = oos.getOrderedElements();
		
		MultiHashMap<Integer, Integer> mapping = new MultiHashMap<Integer, Integer>();
		
		for (int i=0; i!=le.size(); ++i) {
			String className = le.get(i);
			List<Integer> li = csm.toIndexList(className);
			for (Integer k : li)
				mapping.put(i, k);
		}

		// Create the subset 
		WrappedMasterTable smt  = new SummaryMasterTable(masterTable, le, mapping, summary);
		for (int i=0; i!=probeLists.size(); ++i) 
			probeLists.set(i, new WrappedProbeList(probeLists.get(i), smt));
		
		// Create the visualizer
		Visualizer viz = new Visualizer(smt.getDataSet(),probeLists);
		ExpressionTableWindow tw = new ExpressionTableWindow(viz);
		tw.setVisible(true);
		viz.addPlot(tw);
		Layouter l = new Layouter(2,1);
		l.nextElement().placeWindow(tw);

	}

}
