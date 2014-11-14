package mayday.vis3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.event.ChangeEvent;

import mayday.clustering.hierarchical.TreeInfo;
import mayday.clustering.hierarchical.TreeMIO;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.ComparableMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.ExperimentSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.genemining2.cng.Bipartition;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

public class HierarchicalSortedProbeListSetting extends SortedProbeListSetting implements ViewModelListener {
	
	public static final int SORT_BY_TREE = 6;
	
	protected ExtendableObjectSelectionSetting<TreeInfo> tree;
	protected TreeInfo[] trees;
	
	public HierarchicalSortedProbeListSetting(String Name, String Description, HierarchicalSortedProbeList Target, ViewModel vm) {
		super(Name, Description, Target, vm);
		viewModel.addViewModelListener(this);
		stateChanged(new SettingChangeEvent(this));
	}

	protected Setting makeSource() {	
		source = new SelectableHierarchicalSetting("Source",null,0, new Object[]{
				TOP_PROBELIST,
				PROBE_IDENTIFIER,
				PROBE_DISPLAYNAME,
				experiment =new ExperimentSetting("by experiment",null,viewModel.getDataSet().getMasterTable()),
				mioGroupPath = new MIGroupSetting("by meta information",null, null, viewModel.getDataSet().getMIManager(), true)
								.setAcceptableClass(ComparableMIO.class),
				tree = new ExtendableObjectSelectionSetting<TreeInfo>("by clustering tree",null,0,trees = new TreeInfo[0])
		});
		updateTrees(true);
		return source;
	}
	

	protected int getMode() {
		if (source.getObjectValue()==tree)
			return SORT_BY_TREE;
		return super.getMode();
	}

	protected TreeInfo getTreeInfo() {
		return tree.getObjectValue();
	}
	
	public void stateChanged(SettingChangeEvent e) {
		if ((getMode()==SORT_BY_TREE && target.getMode()!=SORT_BY_TREE) || e.getSource()==tree) { 
			((HierarchicalSortedProbeList)target).setTreeInfo(tree.getObjectValue());
		}
		super.stateChanged(e);	
	}
	

	public HierarchicalSortedProbeListSetting clone() {
		HierarchicalSortedProbeListSetting cp = new HierarchicalSortedProbeListSetting(getName(), getDescription(), (HierarchicalSortedProbeList)target, viewModel);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
	}
	
	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==target) {
			switch(target.getMode()) {
			case SORT_BY_TREE:
				source.setObjectValue(tree);
				tree.setObjectValue(((HierarchicalSortedProbeList)target).getTreeInfo());
				break;			
			}
			
			super.stateChanged(e);
		}
	}	

	protected void updateTrees(boolean selectTree) {
		Map<String, TreeInfo> candidates = findUsableTrees();
		for (Entry<String, TreeInfo> cand : candidates.entrySet())
			cand.getValue().setName(cand.getKey());
		tree.updatePredefined(candidates.values());
		if (selectTree && !candidates.isEmpty()) {
			tree.setObjectValue(candidates.values().iterator().next());
			source.setObjectValue(tree);
		}
	}
	
	protected Map<String, TreeInfo> findUsableTrees() {
		// find out how many trees are present in the viewmodel
		final HashMap<String,TreeInfo> candidates = new HashMap<String,TreeInfo>();

		HashSet<String> allProbeNames = new HashSet<String>();
		for (Probe pb : viewModel.getProbes())
			allProbeNames.add(pb.getName()); 
		
		for (ProbeList pl : viewModel.getProbeLists(false)) {
			MIGroupSelection<MIType> mgs = pl.getDataSet().getMIManager().getGroupsForType("PAS.MIO.HierarchicalClusteringTree");
			MIType mt=null;
			for (MIGroup mg : mgs) {
				mt = mg.getMIO(pl);
				if (mt!=null)
					break;
			}
			if (mt!=null) {				
				TreeInfo ti = ((TreeMIO)mt).getValue();
				if (!ti.getSettings().isMatrixTransposed()) {
				// 	check if all probes are there
					Bipartition bp = new Bipartition();
					bp.set(ti.getTree());						
					HashSet<String> orderedProbeNameList = new HashSet<String>(bp.getLeafList1());
					if (allProbeNames.containsAll(orderedProbeNameList) && orderedProbeNameList.containsAll(allProbeNames)) {
						candidates.put(pl.getName(), ti);	
					}
				}
			}
		}
		
		return candidates;
	}
	
	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED)
			updateTrees(false);
	}
	
	public void dispose() {
		viewModel.removeViewModelListener(this);
	}

}
