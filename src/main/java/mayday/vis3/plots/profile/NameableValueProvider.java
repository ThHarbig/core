package mayday.vis3.plots.profile;

import mayday.vis3.ValueProvider;
import mayday.vis3.model.ViewModel;

public class NameableValueProvider extends ValueProvider {

	public NameableValueProvider(ViewModel vm, String menuTitle) {
		super(vm, menuTitle);
	}

	protected void makeSetting() {
		setting = new NameableValueProviderSetting(title, null, this, viewModel);
	}

	public void setMenuTitle(String string) {
		if (!title.equals(string)) {
			title = string;	
			fireChanged();
		}
	}
	

}
