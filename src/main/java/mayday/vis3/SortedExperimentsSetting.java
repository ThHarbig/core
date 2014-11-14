package mayday.vis3;

import java.awt.Component;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.clustering.hierarchical.TreeInfo;
import mayday.clustering.hierarchical.TreeMIO;
import mayday.core.ProbeList;
import mayday.core.meta.ComparableMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

public class SortedExperimentsSetting extends HierarchicalSetting implements ViewModelListener, ChangeListener {
	
	public static final int SORT_ASCENDING = 1;
	public static final int SORT_DESCENDING = 2;
	public static final int SORT_BY_INDEX = 1;
	public static final int SORT_BY_NAME = 2;
	public static final int SORT_BY_TREE = 3;
	public static final int SORT_BY_MIO_GROUP = 4;
	public static final int SORT_BY_DISPLAYNAME = 5;
		
	protected final static String EXP_NAME = "by experiment name";
	protected final static String EXP_INDEX = "by experiment index";
	protected final static String EXP_DISPLAYNAME = "by experiment display name";

	protected final static String[] ORDERS = new String[]{"Descending","Ascending"};
	
	protected RestrictedStringSetting direction;
	protected SelectableHierarchicalSetting source;
	protected ViewModel viewModel;
	protected SortedExperiments target;
	
	protected MIGroupSetting mioGroupPath;
	protected ExtendableObjectSelectionSetting<TreeInfo> tree;
	protected TreeInfo[] trees;
	
	public SortedExperimentsSetting(String Name, String Description, SortedExperiments Target, ViewModel vm) {
		super(Name);
		description = Description;
		viewModel = vm;
		target=Target;
		addSetting(direction = new RestrictedStringSetting("Sort Order",null,1,ORDERS));
		addSetting(makeSource());
		setChildrenAsSubmenus(true);
		Target.addChangeListener(this);
		viewModel.addViewModelListener(this);
		stateChanged(new SettingChangeEvent(this));
	}

	protected Setting makeSource() {	
		source = new SelectableHierarchicalSetting("Source",null,0, new Object[]{
				EXP_INDEX,
				EXP_NAME,
				EXP_DISPLAYNAME,
				mioGroupPath = new MIGroupSetting("meta information",null, null, viewModel.getDataSet().getMIManager(), true)
				.setAcceptableClass(ComparableMIO.class),
				tree = new ExtendableObjectSelectionSetting<TreeInfo>("by clustering tree",null,0,trees = new TreeInfo[0])
		});
		updateTrees(true);
		return source;
	}
	
	protected int getMode() {
		if (source.getObjectValue()==EXP_NAME)
			return SORT_BY_NAME;
		if (source.getObjectValue()==EXP_DISPLAYNAME)
			return SORT_BY_DISPLAYNAME;
		if (source.getObjectValue()==mioGroupPath)
			return SORT_BY_MIO_GROUP;
		if (source.getObjectValue()==tree)
			return SORT_BY_TREE;
		return SORT_BY_INDEX;
	}
	
	public void setMode(int mode) {
		switch(mode) {
		case SORT_BY_INDEX:
			source.setSelectedIndex(0);
			break;
		case SORT_BY_NAME:
			source.setSelectedIndex(1);
			break;			
		case SORT_BY_MIO_GROUP:
			source.setSelectedIndex(3);
			break;
		case SORT_BY_TREE: 
			source.setSelectedIndex(4);
			break;
		case SORT_BY_DISPLAYNAME:
			source.setSelectedIndex(2);
			break;
		}
	}
	
	protected int getOrder() {
		if (direction.getObjectValue()==ORDERS[0])
			return SORT_DESCENDING;
		return SORT_ASCENDING;
	}
	
	public void setOrder(int order) {
		if (order==SORT_DESCENDING)
			direction.setObjectValue(ORDERS[0]);
		else
			direction.setObjectValue(ORDERS[1]);
	}

	protected MIGroup getMIGroup() {
		return mioGroupPath.getMIGroup();
	}
	
	protected TreeInfo getTreeInfo() {
		return tree.getObjectValue();
	}
	
	public void stateChanged(SettingChangeEvent e) {
		if ((getMode()==SORT_BY_TREE && target.getMode()!=SORT_BY_TREE) || e.getSource()==tree) { 
			target.setTreeInfo(tree.getObjectValue());
		}
		if ((getMode()==SORT_BY_MIO_GROUP && target.getMode()!=SORT_BY_MIO_GROUP) || e.getSource()==mioGroupPath) {
			MIGroup mg = getMIGroup();
			if (mg!=null) {
				target.setMISelection(mg);
			}
		}
		target.setMode(getMode());
		target.setOrder(getOrder());
		target.fireIfNeeded();
		fireChanged(e);
	}
	

	public SortedExperimentsSetting clone() {
		SortedExperimentsSetting cp = new SortedExperimentsSetting(name, description, target, viewModel);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
	}
	
	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==target) {
			switch(target.getMode()) {
			case SORT_BY_TREE:
				source.setObjectValue(tree);
				tree.setObjectValue(target.getTreeInfo());
				break;				
			case SORT_BY_NAME:
				source.setObjectValue(EXP_NAME);
				break;
			case SORT_BY_DISPLAYNAME:
				source.setObjectValue(EXP_DISPLAYNAME);
				break;
			case SORT_BY_MIO_GROUP:
				source.setObjectValue(mioGroupPath);
				MIGroup mg = target.getMIGroup();
				mioGroupPath.setMIGroup(mg);
				break;
			case SORT_BY_INDEX:
				source.setObjectValue(EXP_INDEX);
				break;
			}
			direction.setObjectValue(target.getOrder()==SORT_ASCENDING?ORDERS[1]:ORDERS[0]);
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
		HashMap<String,TreeInfo> candidates = new HashMap<String,TreeInfo>();

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
				if (ti.getSettings().isMatrixTransposed()) {
					candidates.put(pl.getName(), ti);	
				}				
			}
		}

		return candidates;
	}
	
	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED)
			updateTrees(false);
	}
	
	public Component getMenuItem( Window parent ) {
		JMenu subMenu = new JMenu(getName());
		JMenu sourceM = (JMenu)source.getMenuItem(parent);
		for (Component cm : sourceM.getMenuComponents())
			subMenu.add(cm);
		subMenu.addSeparator();
		JMenu dirM = (JMenu)direction.getMenuItem(parent);
		for (Component cm : dirM.getMenuComponents())
			subMenu.add(cm);
		return subMenu;
	}

	public void dispose() {
		viewModel.removeViewModelListener(this);
	}
	
}
