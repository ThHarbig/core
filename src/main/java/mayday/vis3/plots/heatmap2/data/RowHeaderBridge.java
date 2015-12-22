package mayday.vis3.plots.heatmap2.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting.ElementBridge;
import mayday.vis3.plots.heatmap2.headers.AbstractHeaderPlugin;
import mayday.vis3.plots.heatmap2.headers.RowHeaderElement;

public class RowHeaderBridge implements ElementBridge<RowHeaderElement> {

	protected HeatmapStructure data;
	
	public RowHeaderBridge(HeatmapStructure struct) {
		data =struct;
	}
	
	@Override
	public Collection<RowHeaderElement> availableElementsForAddition(
			Collection<RowHeaderElement> alreadyInList) {
		List<RowHeaderElement> hes = new LinkedList<RowHeaderElement>();
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(AbstractHeaderPlugin.MC_ROW);
		for (PluginInfo pli : plis)
			
			hes.add(((RowHeaderElement)((AbstractHeaderPlugin)pli.getInstance())).init(data));
		return hes;
	}

	@Override
	public RowHeaderElement createElementFromIdentifier(String identifier) {
		return ((RowHeaderElement)((AbstractHeaderPlugin)PluginManager.getInstance().getPluginFromID(identifier).getInstance())).init(data);
	}

	@Override
	public String createIdentifierFromElement(RowHeaderElement element) {
		return element.getPluginID();
	}

	@Override
	public String getDisplayName(RowHeaderElement element) {
		return element.getName();
	}

	@Override
	public Setting getSettingForElement(RowHeaderElement element) {
		return element.getSetting();
	}

	@Override
	public String getTooltip(RowHeaderElement element) {
		return PluginManager.getInstance().getPluginFromID(element.getPluginID()).getAbout();
	}

	@Override
	public void disposeElement(RowHeaderElement element) {
		element.dispose();		
	}

}
