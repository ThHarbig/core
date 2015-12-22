package mayday.vis3.plots.heatmap2.columns.plugins.divider;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.plots.heatmap2.columns.AbstractInertColumn;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class DividerColumn extends AbstractInertColumn implements SettingChangeListener {

	protected HeatmapStructure data;
	protected HierarchicalSetting setting;
	
	protected IntSetting pxwidth;
	protected DoubleSetting relwidth;
	protected SelectableHierarchicalSetting wtype;
	
	protected ColorSetting color;
	
	public DividerColumn(HeatmapStructure struct) {
		data = struct;
		pxwidth = new IntSetting   ("With in pixels",null,  3,  1, null, true, false);
		relwidth= new DoubleSetting("Relative width",null, 1d, .1, null, true, false);
		wtype = new SelectableHierarchicalSetting("Column width", null, 0, new Object[]{pxwidth, relwidth});

		color = new ColorSetting("Color",null, Color.white);
		setting = new HierarchicalSetting("Divider Column").addSetting(wtype).addSetting(color);
		setting.addChangeListener(this);
	}
	
	@Override
	public String getName() {
		return "";
	}

	@Override
	public void render(Graphics2D graphics, int row, int col, boolean isSelected) {
		
		graphics.setColor(color.getColorValue());
		Rectangle bounds = graphics.getClipBounds();
		graphics.fill(bounds);
		
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

	@Override
	public void dispose() { /*nada*/ }
	
}
