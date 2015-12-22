package mayday.vis3.model.subset;


import java.util.HashMap;
import java.util.List;

import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.gui.ExperimentWithDisplayNameInToString;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.MultiselectObjectListSetting;
import mayday.core.settings.generic.ObjectOrderSetting;
import mayday.vis3.gui.Layouter;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.wrapped.WrappedMasterTable;
import mayday.vis3.model.wrapped.WrappedProbeList;
import mayday.vis3.tables.ExpressionTableWindow;

public class SubsetVisualizer extends AbstractPlugin implements ProbelistPlugin {

	public final static String PLID = "PAS.vis3.visualizer.subset";
	
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				PLID,
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a new visualizer showing only a subset of the experiments",
				"Experiment subset"
		);
		pli.addCategory("Visualization (derived data)");
		return pli;	
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		List<ExperimentWithDisplayNameInToString> le = ExperimentWithDisplayNameInToString.convert(masterTable.getExperiments());
		
		MultiselectObjectListSetting<ExperimentWithDisplayNameInToString> oss = new MultiselectObjectListSetting<ExperimentWithDisplayNameInToString>(
				"Experiment subset", null, le);
		SettingDialog sd = new SettingDialog(null, "Select experiments", oss);
		sd.showAsInputDialog();
		if (sd.canceled() || oss.getSelection().size()<1)
			return null;
		
		createVisualizer(ExperimentWithDisplayNameInToString.convertBack(oss.getSelection()), masterTable, probeLists);
		
		return null;
	}
	
	public static void createVisualizer(List<Experiment> experiments, MasterTable masterTable, List<ProbeList> probeLists) {

		List<ExperimentWithDisplayNameInToString> le = ExperimentWithDisplayNameInToString.convert(experiments);
		
		ObjectOrderSetting<ExperimentWithDisplayNameInToString> oos = new ObjectOrderSetting<ExperimentWithDisplayNameInToString>("Experiment order", null, le)
		.setLayoutStyle(ObjectOrderSetting.LayoutStyle.LIST_WITH_BUTTONS);
		
		SettingDialog sd = new SettingDialog(null, "Define new experiment order", oos);
		sd.showAsInputDialog();
		if (sd.canceled())
			return;

		le  = oos.getOrderedElements();
		
		experiments = ExperimentWithDisplayNameInToString.convertBack(le);
		
		int[] idx = new int[experiments.size()];
		for (int i=0; i!=idx.length; ++i)
			idx[i] = experiments.get(i).getIndex();
		
		// Create the subset 
		WrappedMasterTable smt  = new SubsetMasterTable(masterTable, idx);
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
