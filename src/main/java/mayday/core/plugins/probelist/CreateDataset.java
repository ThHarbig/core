package mayday.core.plugins.probelist;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.ProbelistExportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.miotree.Directory;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;

public class CreateDataset extends AbstractPlugin implements ProbelistExportPlugin {

	public void init() {}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.CreateDatasetFromProbeList",
				new String[0],
				Constants.MC_PROBELIST_EXPORT,
				new HashMap<String, Object>(),
				"Günter Jäger",
				"jaeger@informatik.uni-tuebingen.de",
				"Creates a new DataSet containing the selected ProbeLists.",
				"Create DataSet"
		);
		pli.setMenuName("Create new DataSet");
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_OTHER);
		return pli;
	}

	private HashMap<MIGroup, MIGroup> miGroupMapping = new HashMap<MIGroup, MIGroup>();
	
	public void run(List<ProbeList> probelists) {
		//some settings for the new dataset
		StringSetting dataSetNameSetting = new StringSetting("DataSet Name", "Define a name for the new dataset.", "New_DataSet");
		BooleanSetting cloneMIOsSetting = new BooleanSetting("Clone meta information", "If selected meta-iformation for probes and probe-lists will be cloned as well", true);
		BooleanSetting stripNamesSetting = new BooleanSetting("Remove MPF name modifiers", "Mayday Processing Framework name modifiers in bracket notation: [Modifier] will be removed", true);
		
		HierarchicalSetting setting = new HierarchicalSetting("Create new DataSet ...");
		setting.addSetting(dataSetNameSetting);
		setting.addSetting(cloneMIOsSetting);
		setting.addSetting(stripNamesSetting);
		
		SettingDialog sd = new SettingDialog(null, "Create new DataSet ...", setting);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;	
		
		//get values from settings and start dataset creation
		boolean cloneMIOs = cloneMIOsSetting.getBooleanValue();
		boolean stripNames = stripNamesSetting.getBooleanValue();
		String dsName = dataSetNameSetting.getStringValue();
		
		//empty names are not allowed
		if(dsName.length() == 0) {
			dsName = "New_DataSet";
		}
		
		DataSet InputData = probelists.get(0).getDataSet();

		DataSet ds = new DataSet(dsName);
		MasterTable mt = new MasterTable(ds);

		mt.setNumberOfExperiments(InputData.getMasterTable().getNumberOfExperiments());

		for (int i = 0; i != mt.getNumberOfExperiments(); ++i) {
			mt.setExperimentName(i, InputData.getMasterTable().getExperimentName(i));
		}

		Collection<Probe> union = ProbeList.mergeProbeLists(probelists, probelists.get(0).getDataSet().getMasterTable());
		
		HashMap<Probe, Probe> inoutprobes = new HashMap<Probe, Probe>();

		for (Probe pb: union) {
			Probe newPb = new Probe(mt);
			inoutprobes.put(pb,newPb);
			
			//cone experiment values for each probe
			for(int i = 0; i != pb.getNumberOfExperiments(); ++i)
				newPb.addExperiment(pb.getValue(i));
			
			//set the name of the new probe
			String PbName = pb.getName();

			int bracketIndex = PbName.indexOf("[");
			if (stripNames && bracketIndex >= 0) {
				PbName = PbName.substring(0,bracketIndex);
				PbName.trim();
			}
			
			newPb.setName(PbName);
			
			if (cloneMIOs)
				cloneMIOs(InputData, pb, ds, newPb);

			mt.addProbe(newPb);
		}
		
		ds.setMasterTable(mt);
		//create global probe list
		ProbeList global = ds.getMasterTable().createGlobalProbeList(true);
		ds.getProbeListManager().addObject(global);

		//clone the probe lists
		for (ProbeList inputProbeList : probelists) {
			//clone probe list (name and color)
			ProbeList newPL = new ProbeList(ds, inputProbeList.isSticky());
			newPL.setName(inputProbeList.getName());
			newPL.setColor(inputProbeList.getColor());
			
			for (Probe pb : inputProbeList.getAllProbes()) {
				newPL.addProbe(inoutprobes.get(pb));
			}
			
			if (cloneMIOs)
				cloneMIOs(InputData, inputProbeList, ds, newPL);
			
			ds.getProbeListManager().addObjectAtBottom(newPL);
		}

		if(cloneMIOs) {
			recreateMIGroupHierarchy(InputData, ds);
		}
		
		DataSetManager.singleInstance.addObjectAtTop(ds);
	}

	public void recreateMIGroupHierarchy(DataSet oldDS, DataSet newDS) {
		MIManager newManager = newDS.getMIManager();
		MIManager oldManager = oldDS.getMIManager();
		
		Set<MIGroup> oldGroups = miGroupMapping.keySet();
		
		for(MIGroup g : oldGroups) {
			Directory parent = oldManager.getTreeRoot().getDirectory(oldManager.getTreeRoot().getPathFor(g), false).getParent();
			MIGroup parentGroup = parent.getGroup();
			
			//check if there is a parent for this group in the new dataset
			if(miGroupMapping.containsKey(parentGroup)) {
				MIGroup newParentGroup = miGroupMapping.get(parentGroup);
				//move the group to its parent
				newManager.moveGroupInTree(miGroupMapping.get(g), newParentGroup.getPath() + parent.getName() + "/");
			}
		}
	}

	public void cloneMIOs(DataSet oldDS, Object oldMIOE, DataSet newDS, Object newMIOE) {
		MIManager newManager = newDS.getMIManager();
		MIManager oldManager = oldDS.getMIManager();

		for (MIGroup mg : oldManager.getGroupsForObject(oldMIOE)) {
			MIType mt = mg.getMIO(oldMIOE);
			/* go over all mios in this mioextendable object. clone the mio. if the miogroup has to be cloned as well,
			 * do that. find previously cloned miogroups by name */

			// 1) Clone the MIO
			MIType newMIO = mt.clone();

			// 2) find the corresponding group or create a new one
			MIGroup targetMG = null;
			
			/*
			 * changed to hashmap instead of list since group names don't need to be unique! <- this can lead to wrongly placed mios
			 */
			if(miGroupMapping.containsKey(mg)) {
				targetMG = miGroupMapping.get(mg);
			}
			
			if (targetMG == null) {
				targetMG = newManager.newGroup(mg.getMIOType(), mg.getName()); 
				miGroupMapping.put(mg, targetMG);
			}
			
			targetMG.add(newMIOE, newMIO);
		}
	}
}
