package mayday.vis3.plots.profile;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.settings.Setting;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting.ElementBridge;
import mayday.vis3.ValueProvider;
import mayday.vis3.model.ViewModel;

public class ProfilePlotExtraColumnsBridge implements ElementBridge<ValueProvider>, ChangeListener {

	protected ViewModel vm;
	protected ProfilePlotComponent ppc;
	
	public ProfilePlotExtraColumnsBridge(ViewModel vm, ProfilePlotComponent ppc) {
		this.vm = vm;
		this.ppc = ppc;
	}
	
	@Override
	public Collection<ValueProvider> availableElementsForAddition(
			Collection<ValueProvider> alreadyInList) {
		List<ValueProvider> l = new LinkedList<ValueProvider>();
		NameableValueProvider nvp = new NameableValueProvider(vm, "Extra Column");
		l.add(nvp);
		nvp.addChangeListener(this); // will be removed in dispose()
		return l;
	}

	@Override
	public ValueProvider createElementFromIdentifier(String identifier) {
		return new NameableValueProvider(vm, identifier);
	}

	@Override
	public String createIdentifierFromElement(ValueProvider element) {
		return element.getMenuTitle();
	}

	@Override
	public void disposeElement(ValueProvider element) {
		element.removeChangeListener(this);
	}

	@Override
	public String getDisplayName(ValueProvider element) {
		return element.getMenuTitle();
	}

	@Override
	public Setting getSettingForElement(ValueProvider element) {
		return element.getSetting();
	}

	@Override
	public String getTooltip(ValueProvider element) {
		return element.getSourceName();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		ppc.updatePlot();		
	}

}
