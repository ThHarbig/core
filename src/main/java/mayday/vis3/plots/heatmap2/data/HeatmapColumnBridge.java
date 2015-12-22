package mayday.vis3.plots.heatmap2.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting.ElementBridge;
import mayday.vis3.plots.heatmap2.columns.HeatmapColumn;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;

public class HeatmapColumnBridge implements ElementBridge<AbstractColumnGroupPlugin> {

	protected HeatmapStructure data;
	
	public HeatmapColumnBridge(HeatmapStructure struct) {
		data =struct;
	}
	
	@Override
	public Collection<AbstractColumnGroupPlugin> availableElementsForAddition(
			Collection<AbstractColumnGroupPlugin> alreadyInList) {
		List<AbstractColumnGroupPlugin> hcgs = new LinkedList<AbstractColumnGroupPlugin>();
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(AbstractColumnGroupPlugin.MC);
		for (PluginInfo pli : plis)
			hcgs.add(((AbstractColumnGroupPlugin)pli.getInstance()).init(data));
		return hcgs;
	}

	@Override
	public AbstractColumnGroupPlugin createElementFromIdentifier(String identifier) {
		return ((AbstractColumnGroupPlugin)PluginManager.getInstance().getPluginFromID(identifier).getInstance()).init(data);
	}

	@Override
	public String createIdentifierFromElement(AbstractColumnGroupPlugin element) {
		return element.getPluginID();
	}

	@Override
	public String getDisplayName(AbstractColumnGroupPlugin element) {
		return element.getName();
	}

	@Override
	public Setting getSettingForElement(AbstractColumnGroupPlugin element) {
		return element.getSetting();
	}

	@Override
	public String getTooltip(AbstractColumnGroupPlugin element) {
		return PluginManager.getInstance().getPluginFromID(element.getPluginID()).getAbout();
	}

	@Override
	public void disposeElement(AbstractColumnGroupPlugin element) {
		for (HeatmapColumn hmc : element.getColumns())
			hmc.dispose();
	}

}
