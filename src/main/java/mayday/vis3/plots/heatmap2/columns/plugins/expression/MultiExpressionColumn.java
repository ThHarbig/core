package mayday.vis3.plots.heatmap2.columns.plugins.expression;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.plots.heatmap2.columns.AbstractProbeSelectionColumn;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class MultiExpressionColumn extends AbstractProbeSelectionColumn implements SettingChangeListener {

	protected Integer col;
	protected ExpressionConfiguration config;
	
	public MultiExpressionColumn(HeatmapStructure struct, Integer column, ExpressionConfiguration sett) {
		super(struct);
		col=column;
		config = sett;
		config.getSetting().addChangeListener(this);		
	}
	
	@Override
	public String getName() {
		return config.getName(col);
	}

	@Override
	public void render(Graphics2D graphics, int row, int col, boolean isSelected) {
		
		Probe pb = data.getProbe(row);
		
		Color c = config.getColor(pb, this.col);			
		
		graphics.setColor(c);
		Rectangle bounds = graphics.getClipBounds();
		graphics.fill(bounds);
		
		if (isSelected && config.showSelection()) {
			graphics.setColor(Color.white);
			graphics.drawLine(0, 0, (int)data.getColWidth(col), (int)data.getRowHeight(row));
		}

	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
//		if (e.hasSource(config.getSetting().getChild("Sort Experiments")))
//			fireChange(UpdateEvent.SIZE_CHANGE);
//		else
			fireChange(UpdateEvent.REPAINT);
	}
	
	public ExpressionConfiguration getConfiguration() {
		return config;
	}

	@Override
	public void dispose() {
		config.dispose();
	}
	
}
