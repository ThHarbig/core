/*
 * Created on Dec 8, 2004
 *
 */
package mayday.vis3.tables;

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
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.gui.Layouter;
import mayday.vis3.model.Visualizer;

/**
 * @author gehlenbo
 *
 */
public class MetaInfoTablePlugin extends AbstractMetaInfoPlugin
{
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.vis3.MetaInfoTable",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Opens a histogram plot visualizing the distribution numeric mio values",
				"Visualize as Table");
		pli.setMenuName("as Table");
		pli.addCategory("Visualize");
		return pli;
	}
	
	protected Class<? extends MIType> getMIOClass() {
		return MIType.class;
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

	protected ArrayList<MIGroup> myColumns;
	
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
		
		myColumns = new ArrayList<MIGroup>(input);
		
		Visualizer viz = new Visualizer(miManager.getDataSet(),temporaryPL);

		MIOTableWindow mtw = new RestrictedMIOTableWindow(viz);
		mtw.setVisible(true);
		viz.addPlot(mtw);
		Layouter l = new Layouter(2,1);
		l.nextElement().placeWindow(mtw);
		
	}

	@Override
	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(getMIOClass());		
	}

	
	@SuppressWarnings("serial")
	protected class RestrictedMIOTableWindow extends MIOTableWindow {
		public RestrictedMIOTableWindow(Visualizer pg) {
			super(pg);
		}
		protected MIOTableComponent createTableComponent() {
			return new RestrictedMIOTableComponent(visualizer);
		}
	}
	
	@SuppressWarnings("serial")
	protected class RestrictedMIOTableComponent extends MIOTableComponent {
		public RestrictedMIOTableComponent(Visualizer visualizer) {
			super(visualizer);
		}

		protected void findColumns() {
			columns = myColumns;
			// do nothing
		}
	}
	
	
	
}
