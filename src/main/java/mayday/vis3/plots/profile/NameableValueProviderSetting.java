package mayday.vis3.plots.profile;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.ValueProviderSetting;
import mayday.vis3.model.ViewModel;

public class NameableValueProviderSetting extends ValueProviderSetting {

	protected StringSetting menuName;
	
	public NameableValueProviderSetting(String Name, String Description,
			NameableValueProvider Target, ViewModel vm) {
		super(Name, Description, Target, vm);
	}
	
	protected void initialSettings() {
		addSetting(menuName = new StringSetting("Name","Enter a name for this data source, e.g. for labelling", target.getMenuTitle()));
	} 
	
	public void stateChanged(SettingChangeEvent e) {
		if (e.getSource()==menuName)
			((NameableValueProvider)target).setMenuTitle(menuName.getValueString());
		super.stateChanged(e);
	}
	
	public NameableValueProviderSetting clone() {
		NameableValueProviderSetting cp = new NameableValueProviderSetting(getName(), getDescription(), new NameableValueProvider(viewModel, target.getMenuTitle()), viewModel);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
	}

}
