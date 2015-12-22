package mayday.vis3.plots.heatmap2.columns.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.plots.heatmap2.columns.HeatmapColumn;
import mayday.vis3.plots.heatmap2.data.ColumnHeaderBridge;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.ColumnHeaderElement;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;
import mayday.vis3.plots.heatmap2.interaction.UpdateListener;

public abstract class AbstractColumnGroupPlugin extends AbstractPlugin {
	
	public final static String MC = "Visualization/Heatmap/Column Type";
	
	protected ArrayList<HeatmapColumn> columns;
	protected ArrayList<ColumnHeaderElement> colHeaders;
	
	protected HierarchicalSetting columnSetting;
	protected SortedExtendableConfigurableObjectListSetting<ColumnHeaderElement> columnHeaderSetting;
	
	public List<HeatmapColumn> getColumns() {
		return Collections.unmodifiableList(columns);
	}
	
	public Setting getSetting() {
		return columnSetting;
	}
	
	protected void init(HeatmapStructure struct, Collection<HeatmapColumn> cols, HierarchicalSetting setting, ColumnHeaderElement... defaultHeaders) {
		init(struct, setting, defaultHeaders);
		columns.addAll(cols);
	}
	
	protected void init(HeatmapStructure struct, HeatmapColumn column, HierarchicalSetting setting, ColumnHeaderElement... defaultHeaders) {
		init(struct, setting, defaultHeaders);
		columns.add(column);
	}
	
	private void init(final HeatmapStructure struct, HierarchicalSetting setting, ColumnHeaderElement[] defaultHeaders) {
		columns = new ArrayList<HeatmapColumn>();
		colHeaders = new ArrayList<ColumnHeaderElement>();
		columnSetting = setting;
		
		List<ColumnHeaderElement> chtypes = Arrays.asList(defaultHeaders);
		
		final UpdateListener sizeChangeListener = new UpdateListener() {
			public void elementNeedsUpdating(UpdateEvent evt) {
				switch(evt.getChange()) {
				case UpdateEvent.SIZE_CHANGE:
					struct.triggerInvalidate();
					break;
				case UpdateEvent.REPAINT:
					((PlotComponent)struct.getColumnHeaderComponent()).updatePlot();
					break;
				}
			}
		};
		
		columnHeaderSetting = new SortedExtendableConfigurableObjectListSetting<ColumnHeaderElement>("Column headers", null, new ColumnHeaderBridge(struct, this));
		columnHeaderSetting.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				
				LinkedList<ColumnHeaderElement> oldHeaders = new LinkedList<ColumnHeaderElement>(colHeaders);
				
				colHeaders.clear(); 
				for (ColumnHeaderElement he : columnHeaderSetting.getElements()) {
					colHeaders.add(he);
					he.addUpdateListener(sizeChangeListener);
				}

				for (ColumnHeaderElement rhe : oldHeaders)
					if (!colHeaders.contains(rhe))
						rhe.dispose();
				
				struct.triggerInvalidate();
			}
		});
		columnHeaderSetting.setElements(chtypes);

		columnSetting.setLayoutStyle(HierarchicalSetting.LayoutStyle.TABBED);
		columnSetting.addSetting(columnHeaderSetting);
		
		for (HeatmapColumn col : columns)
			col.setGroup(this);

	}
	
	public List<ColumnHeaderElement> getColumnHeaderElements() {
		return colHeaders;
	}
	
	public String getPluginID() {
		return PluginManager.getInstance().getPluginFromClass(getClass()).getIdentifier();
	}
	
	public String getName() {
		return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
	}
	
	public void init() { /*for PLUMA */	}
	
	public abstract AbstractColumnGroupPlugin init(HeatmapStructure struct);
	
}
