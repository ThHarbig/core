package mayday.vis3.plots.ma;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.ma.MAValueProvider.ExperimentProvider;

public class MAValueProviderSetting extends RestrictedStringSetting implements ChangeListener, SettingChangeListener {
	
	protected ViewModel viewModel;
	protected MAValueProvider target;
	
	public MAValueProviderSetting(String Name, String Description, MAValueProvider Target, ViewModel vm) {
		super(Name,Description,0,vm.getDataSet().getMasterTable().getExperimentNames().toArray(new String[0]));		
		viewModel = vm;
		target=Target;
		Target.addChangeListener(this);
		addChangeListener(this);
	}
	
	protected int getExperimentIndex() {
		return viewModel.getDataSet().getMasterTable().getExperimentNames().indexOf(getStringValue());
	}
	

	public void stateChanged(SettingChangeEvent e) {
		target.setProvider(target.new ExperimentProvider(getExperimentIndex()));
	}
	
	public MAValueProviderSetting clone() {
		MAValueProviderSetting cp = new MAValueProviderSetting(getName(), getDescription(), new MAValueProvider(viewModel, target.getMenuTitle()), viewModel);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
	}
	
	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==target) {
			int exp = ((ExperimentProvider)target.getProvider()).getExperiment();
			setStringValue(viewModel.getDataSet().getMasterTable().getExperimentName(exp));
		}
	}	

}
