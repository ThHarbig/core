package mayday.vis3;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.meta.ComparableMIO;
import mayday.core.meta.MIGroup;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.ExperimentSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.model.ViewModel;

public class SortedProbeListSetting extends HierarchicalSetting implements ChangeListener {
	
	public static final int SORT_ASCENDING = 1;
	public static final int SORT_DESCENDING = 2;
	public static final int SORT_BY_PROBE_IDENTIFIER = 1;
	public static final int SORT_BY_TOP_PRIORITY_PROBE_LIST = 2;
	public static final int SORT_BY_EXPERIMENT = 3;
	public static final int SORT_BY_MIO_GROUP = 4;
	public static final int SORT_BY_DISPLAYNAME = 5;
	
	protected RestrictedStringSetting direction;
	protected ExperimentSetting experiment;
	protected MIGroupSetting mioGroupPath;
	protected SelectableHierarchicalSetting source;
	protected ViewModel viewModel;
	protected SortedProbeList target;
	
	protected final static String TOP_PROBELIST = "by top-priority probelist";
	protected final static String PROBE_IDENTIFIER = "by probe identifier";
	protected final static String PROBE_DISPLAYNAME = "by probe display name";
	
	protected final static String[] ORDERS = new String[]{"Descending","Ascending"};
	
	public SortedProbeListSetting(String Name, String Description, SortedProbeList Target, ViewModel vm) {
		super(Name);
		description = Description;
		viewModel = vm;
		target=Target;
		addSetting(direction = new RestrictedStringSetting("Sort Order",null,0,ORDERS));
		addSetting(makeSource());
		setChildrenAsSubmenus(true);
		Target.addChangeListener(this);
	}

	protected Setting makeSource() {
		source = new SelectableHierarchicalSetting("Source",null,0, new Object[]{
				TOP_PROBELIST,
				PROBE_IDENTIFIER,
				PROBE_DISPLAYNAME,
				experiment = new ExperimentSetting("experiment",null,viewModel.getDataSet().getMasterTable()),
				mioGroupPath = new MIGroupSetting("meta information",null, null, viewModel.getDataSet().getMIManager(), true)
								.setAcceptableClass(ComparableMIO.class)}
		);
		return source;
	}

	protected int getMode() {
		if (source.getObjectValue()==experiment)
			return SORT_BY_EXPERIMENT;
		if (source.getObjectValue()==mioGroupPath)
			return SORT_BY_MIO_GROUP;
		if (source.getObjectValue()==PROBE_IDENTIFIER)
			return SORT_BY_PROBE_IDENTIFIER;
		if (source.getObjectValue()==PROBE_DISPLAYNAME)
			return SORT_BY_DISPLAYNAME;
		return SORT_BY_TOP_PRIORITY_PROBE_LIST;
	}
	
	protected int getOrder() {
		if (direction.getObjectValue()==ORDERS[0])
			return SORT_DESCENDING;
		return SORT_ASCENDING;
	}
	
	protected int getExperimentIndex() {
		return experiment.getExperiment().getIndex();
	}
	
	protected MIGroup getMIGroup() {
		return mioGroupPath.getMIGroup();
	}
	
	public void stateChanged(SettingChangeEvent e) {
		if ((getMode()==SORT_BY_EXPERIMENT && target.getMode()!=SORT_BY_EXPERIMENT) || e.getSource()==experiment)
			target.setExperiment(experiment.getSelectedIndex());
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
	
	
	
	public SortedProbeListSetting clone() {
		SortedProbeListSetting cp = new SortedProbeListSetting(getName(), getDescription(), target, viewModel);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
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


	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==target) {
			switch(target.getMode()) {
			case SORT_BY_EXPERIMENT:
				source.setObjectValue(experiment);
				int exp = target.getExperiment();
				experiment.setExperiment(viewModel.getDataSet().getMasterTable().getExperiment(exp));
				break;
			case SORT_BY_MIO_GROUP:
				source.setObjectValue(mioGroupPath);
				MIGroup mg = target.getMIGroup();
				mioGroupPath.setMIGroup(mg);
				break;
			case SORT_BY_PROBE_IDENTIFIER:
				source.setObjectValue(PROBE_IDENTIFIER);
				break;
			case SORT_BY_TOP_PRIORITY_PROBE_LIST:
				source.setObjectValue(TOP_PROBELIST);
				break;
			case SORT_BY_DISPLAYNAME:
				source.setObjectValue(PROBE_DISPLAYNAME);
				break;
			}
			
			direction.setObjectValue(target.getOrder()==SORT_ASCENDING?ORDERS[1]:ORDERS[0]);
		}
	}	

}
