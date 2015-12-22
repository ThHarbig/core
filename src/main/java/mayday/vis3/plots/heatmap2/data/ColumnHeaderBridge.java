package mayday.vis3.plots.heatmap2.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting.ElementBridge;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.headers.AbstractHeaderPlugin;
import mayday.vis3.plots.heatmap2.headers.ColumnHeaderElement;

public class ColumnHeaderBridge implements ElementBridge<ColumnHeaderElement> {

	protected HeatmapStructure data;
	protected AbstractColumnGroupPlugin group;
	
	public ColumnHeaderBridge(HeatmapStructure struct, AbstractColumnGroupPlugin group) {
		data =struct;
		this.group = group;
	}
	
	@Override
	public Collection<ColumnHeaderElement> availableElementsForAddition(
			Collection<ColumnHeaderElement> alreadyInList) {
		List<ColumnHeaderElement> hes = new LinkedList<ColumnHeaderElement>();
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(AbstractHeaderPlugin.MC_COL);
		for (PluginInfo pli : plis)
			hes.add(((ColumnHeaderElement)((AbstractHeaderPlugin)pli.getInstance())).init(data, group));
		return hes;
	}

	@Override
	public ColumnHeaderElement createElementFromIdentifier(String identifier) {
		return ((ColumnHeaderElement)((AbstractHeaderPlugin)PluginManager.getInstance().getPluginFromID(identifier).getInstance())).init(data, group);
	}

	@Override
	public String createIdentifierFromElement(ColumnHeaderElement element) {
		return element.getPluginID();
	}

	@Override
	public String getDisplayName(ColumnHeaderElement element) {
		return element.getName();
	}

	@Override
	public Setting getSettingForElement(ColumnHeaderElement element) {
		return element.getSetting();
	}

	@Override
	public String getTooltip(ColumnHeaderElement element) {
		return PluginManager.getInstance().getPluginFromID(element.getPluginID()).getAbout();
	}
	
	@Override
	public void disposeElement(ColumnHeaderElement element) {
		element.dispose();
	}


}
