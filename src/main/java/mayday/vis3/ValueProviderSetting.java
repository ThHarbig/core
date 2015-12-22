package mayday.vis3;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.meta.MIGroup;
import mayday.core.meta.NumericMIO;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.methods.ManipulationMethodSetting;
import mayday.core.settings.typed.ExperimentSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.vis3.ValueProvider.ExperimentProvider;
import mayday.vis3.ValueProvider.MIOProvider;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ManipulationMethodSingleValue;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.manipulators.None;

public class ValueProviderSetting extends HierarchicalSetting implements ChangeListener {
	
	public static final int EXPERIMENT_VALUE=1;
	public static final int MIO_VALUE=2;
	
	protected ExperimentSetting experiment;
	protected MIGroupSetting mioGroupPath;
	protected ManipulationMethodSetting mioManipulator;
	protected SelectableHierarchicalSetting source;
	protected ViewModel viewModel;
	protected ValueProvider target;
	
	public ValueProviderSetting(String Name, String Description, ValueProvider Target, ViewModel vm) {
		super(Name);
		description = Description;
		viewModel = vm;
		target=Target;
		initialSettings();
		if (vm!=null) {
			addSetting(source = new SelectableHierarchicalSetting("Source",null,0,new Object[]{					
					experiment = new ExperimentSetting("experiment",null,vm.getDataSet().getMasterTable()),
					mioGroupPath = new MIGroupSetting("meta information",null, null, vm.getDataSet().getMIManager(), true)
								.setAcceptableClass(NumericMIO.class)					
			})).
			addSetting(mioManipulator = new ManipulationMethodSetting("meta information manipulator", null, new None(), true));
		}
		setChildrenAsSubmenus(true);
		Target.addChangeListener(this);
	}

	protected void initialSettings() {} // for overriding;

	protected int getMode() {
		if (source.getObjectValue()==experiment)
			return EXPERIMENT_VALUE;
		else  
			return MIO_VALUE;
	}
	
	protected int getExperimentIndex() {
		return experiment.getExperiment().getIndex();
	}
	
	protected MIGroup getMIGroup() {
		return mioGroupPath.getMIGroup();
	}
	
	public void stateChanged(SettingChangeEvent e) {
		if ((getMode()==EXPERIMENT_VALUE && target.getSourceType()!=EXPERIMENT_VALUE) || e.getSource()==experiment)
			target.setProvider(target.new ExperimentProvider(getExperimentIndex()));
		if ((getMode()==MIO_VALUE && target.getSourceType()!=MIO_VALUE) 
				|| e.getSource()==mioGroupPath || e.hasSource(mioManipulator)) {
			MIGroup mg = getMIGroup();
			ManipulationMethodSingleValue manip = getManipulation();
			if (mg!=null) {
				target.setProvider(target.new MIOProvider(mg, manip));
			}
		}
		fireChanged(e);	
	}
	
	public ManipulationMethodSingleValue getManipulation() {
		return (ManipulationMethodSingleValue)mioManipulator.getInstance();
	}
	
	public ValueProviderSetting clone() {
		ValueProviderSetting cp = new ValueProviderSetting(getName(), getDescription(), new ValueProvider(viewModel, target.getMenuTitle()), viewModel);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
	}
	

	
	public Component getMenuItem( Window parent ) {
		JMenu subMenu = new JMenu(getName());
		JMenu sourceM = (JMenu)source.getMenuItem(parent);
		for (Component cm : sourceM.getMenuComponents())
			subMenu.add(cm);
		subMenu.add(mioManipulator.getMenuItem(parent));
		return subMenu;
	}


	public void stateChanged(ChangeEvent e) {
		if (viewModel==null)
			return;
		if (e.getSource()==target) {
			switch(target.getSourceType()) {
			case EXPERIMENT_VALUE:
				source.setObjectValue(experiment);
				int exp = ((ExperimentProvider)target.getProvider()).getExperiment();
				if (exp<viewModel.getDataSet().getMasterTable().getNumberOfExperiments())
					experiment.setStringValue(viewModel.getDataSet().getMasterTable().getExperimentName(exp));
				break;
			case MIO_VALUE:
				source.setObjectValue(mioGroupPath);
				MIGroup mg = ((MIOProvider)target.getProvider()).getMIGroup();
				mioGroupPath.setMIGroup(mg);
				mioManipulator.setInstance((ManipulationMethod)((MIOProvider)target.getProvider()).getManipulator());
				break;
			}
		}
	}	

}
