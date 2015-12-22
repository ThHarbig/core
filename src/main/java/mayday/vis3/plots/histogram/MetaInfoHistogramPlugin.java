/*
 * Created on Dec 8, 2004
 *
 */
package mayday.vis3.plots.histogram;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.NumericMIO;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.gui.Layouter;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.manipulators.None;

/**
 * @author gehlenbo
 *
 */
public class MetaInfoHistogramPlugin extends AbstractMetaInfoPlugin
{
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.MetaInfoHistogram",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Opens a histogram plot visualizing the distribution numeric mio values",
				"Visualize as Histogram");
		pli.setMenuName("as Histogram");
		pli.addCategory("Visualize");
		return pli;
	}
	
	protected Class<? extends MIType> getMIOClass() {
		return NumericMIO.class;
	}
	
	protected RestrictedStringSetting chooseProbeLists;
	protected HierarchicalSetting setting;
	
	private String[] probeSelectionMethods = {"Only selected ProbeLists", "All probes with MIOs"};
	
	public Setting getSetting() {
		if (setting == null) {
			chooseProbeLists = new RestrictedStringSetting("Probes for visualization", "Choose whether the MIO table should be restricted "
					+ "to selected probe lists or if it should display "
					+ "all probes for which mio values are available.", 0, probeSelectionMethods);
			setting = new HierarchicalSetting("Value ranking").addSetting(chooseProbeLists);
		}
		return setting;
	}

	@Override
	public void run(MIGroupSelection<MIType> input, MIManager miManager) {
		
		getSetting();
		SettingDialog sdl = new SettingDialog(null, "Value ranking", setting);
		sdl.showAsInputDialog();
		
		if (sdl.canceled()) {
			return;
		}
		
		String extras = (input.size()>1)?"...":"";
		
		ProbeList pl = new ProbeList(miManager.getDataSet(), false);
		MIGroup xmg = input.get(0);
		pl.setName("Probes contained in "+xmg.getPath()+"/"+xmg.getName()+extras);

		if(chooseProbeLists.getSelectedIndex() == 1) {
			for (MIGroup mg : input)
				for (Object o : mg.getObjects())
					if (o instanceof Probe && !pl.contains((Probe)o))
						pl.addProbe((Probe)o);
		} else {
			Object[] probeListObjects = miManager.getDataSet().getProbeListManager().getProbeListManagerView().getSelectedValues();
			List<ProbeList> allProbeLists = new ArrayList<ProbeList>();
			for(Object probeList : probeListObjects) {
				allProbeLists.add((ProbeList)probeList);
			}
			ProbeList unique = ProbeList.createUniqueProbeList(allProbeLists);
			
			for(Probe p : unique) {
				 for(MIGroup mg : input) {
					 if(mg.contains(p)) {
						 if(!pl.contains(p))
							 pl.addProbe(p);
					 }
				 }
			}
		}
		
		LinkedList<ProbeList> temporaryPL = new LinkedList<ProbeList>();
		temporaryPL.add(pl);
		
		int[] dims = MultiPlotPanel.findBestRC(input.size());
		
		Visualizer viz = new Visualizer(miManager.getDataSet(),temporaryPL);
		Layouter l = new Layouter(dims[0],dims[1]);
		
		for (MIGroup mg : input) {
			HistogramPlotComponent hpc = new HistogramPlotComponent();
			Component plotComponent = new PlotWithLegendAndTitle(hpc);
			PlotWindow pw = new PlotWindow(plotComponent, viz);
			viz.addPlot(pw);
			pw.setVisible(true);
			l.nextElement().placeWindow(pw);
			// set value provider of the histogram to use our meta info
			hpc.V.setProvider(hpc.V.new MIOProvider(mg, new None()));
		}
	}

	@Override
	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(getMIOClass());		
	}

	
}
