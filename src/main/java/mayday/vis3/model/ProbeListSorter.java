package mayday.vis3.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mayday.core.ProbeList;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectOrderSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;

public class ProbeListSorter implements ViewModelListener, SettingChangeListener {
	
	protected ViewModel vm;
	protected HierarchicalSetting setting;
	protected SelectableHierarchicalSetting orderingSource;
	protected ObjectOrderSetting<ProbeList> userDefinedOrder;
	protected BooleanSetting reverseOrder;
	protected final static String DSORDER = "Use order as established in the DataSet";
	
	public ProbeListSorter(ViewModel vm) {
		this.vm = vm;
		vm.addViewModelListener(this);
		userDefinedOrder = new ObjectOrderSetting<ProbeList>("User defined ordering",null,null).setLayoutStyle(
				ObjectOrderSetting.LayoutStyle.LIST_WITH_BUTTONS);
		orderingSource = new SelectableHierarchicalSetting("Ordering", null, 0, new Object[]{
			DSORDER,
			userDefinedOrder
		});
		reverseOrder = new BooleanSetting("Reverse ordering",null,false);
		setting = new HierarchicalSetting("ProbeList ordering");
		setting.addSetting(orderingSource);
		setting.addSetting(reverseOrder);
		setting.setChildrenAsSubmenus(false);
		fillOrderingList();
		setting.addChangeListener(this);		
	}
	
	public LinkedList<ProbeList> getSelectedProbeListOrdering(List<ProbeList> selectedLists) {
		return getSelectedProbeListOrdering(selectedLists, getOrderedList());
	}
	
	protected LinkedList<ProbeList> getSelectedProbeListOrdering(List<ProbeList> selectedLists, List<ProbeList> orderedList) {
		LinkedList<ProbeList> ret = new LinkedList<ProbeList>();
		for(ProbeList pl : orderedList) {
			if (selectedLists.contains(pl)) {
				ret.add(pl);
			}
		}
		return ret;
	}
	
	protected List<ProbeList> getOrderedList() {
		List<ProbeList> ret;
		if (orderingSource.getObjectValue()==DSORDER) 
			ret = vm.getDataSet().getProbeListManager().getProbeLists();
		else 
			ret = userDefinedOrder.getOrderedElements();
		if (reverseOrder.getBooleanValue())
			Collections.reverse(ret);
		return ret;
	}

	protected void fillOrderingList() {
		List<ProbeList> dsOrder = getSelectedProbeListOrdering(
				vm.getProbeLists(false), 
				vm.getDataSet().getProbeListManager().getProbeLists()
				);
		userDefinedOrder.setOrderedElements(dsOrder);
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED) {
			fillOrderingList();
		}		
	}
	
	public Setting getSetting() {
		return setting;
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		vm.translateProbeListOrderingChangedEvent_calledFromProbeListSorter();
	}
}
