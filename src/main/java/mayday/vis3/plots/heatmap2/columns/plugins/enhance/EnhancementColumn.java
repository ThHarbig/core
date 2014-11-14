package mayday.vis3.plots.heatmap2.columns.plugins.enhance;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.ColorProvider;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.plots.heatmap2.columns.AbstractProbeSelectionColumn;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class EnhancementColumn extends AbstractProbeSelectionColumn implements SettingChangeListener {

	protected HierarchicalSetting setting;
	
	protected IntSetting pxwidth;
	protected DoubleSetting relwidth;
	protected SelectableHierarchicalSetting wtype;
	
	protected ColorProvider coloring;
	
	protected BooleanSetting showSelection;
	
	public EnhancementColumn(HeatmapStructure struct) {
		super(struct);
		
		pxwidth = new IntSetting("With in pixels",null, 3, 1, null, true, false);
		relwidth= new DoubleSetting("Relative width",null, 1d, .1, null, true, false);
		wtype = new SelectableHierarchicalSetting("Column width", null, 0, new Object[]{pxwidth, relwidth});
		
		coloring = new ColorProvider(struct.getViewModel());		
		
		showSelection = new BooleanSetting("Indicate selection",null, true);
		
		setting = new HierarchicalSetting("Enhancement Column").addSetting(wtype).addSetting(coloring.getSetting()).addSetting(showSelection);
		setting.addChangeListener(this);
	}
	
	@Override
	public String getName() {
		return coloring.getSourceName();
	}

	@Override
	public void render(Graphics2D graphics, int row, int col, boolean isSelected) {		
		graphics.setColor(coloring.getColor(data.getProbe(row)));
		Rectangle bounds = graphics.getClipBounds();
		graphics.fill(bounds);
		
		if (isSelected && showSelection.getBooleanValue()) {
			graphics.setColor(Color.white);
			graphics.drawLine(0, 0, (int)data.getColWidth(col), (int)data.getRowHeight(row));
		}
		
	}

	@Override
	public double getDesiredWidth() {
		if (wtype.getObjectValue()==pxwidth)
			return -pxwidth.getIntValue();
		else
			return relwidth.getDoubleValue();
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		if (e.hasSource(wtype))
			fireChange(UpdateEvent.SIZE_CHANGE);
		else
			fireChange(UpdateEvent.REPAINT);
	}

	protected ColorGradient getGradient() {
		return coloring.getGradient();
	}

	@Override
	public void dispose() { /*nada*/ }

}
