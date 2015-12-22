package mayday.vis3.plots.heatmap2.columns.plugins.bar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.plots.heatmap2.columns.AbstractProbeSelectionColumn;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class MultiBarColumn extends AbstractProbeSelectionColumn implements SettingChangeListener {

	protected Integer col;
	protected BarConfiguration config;
	protected Rectangle2D bounds = new Rectangle2D.Double();
	
	public MultiBarColumn(HeatmapStructure struct, Integer column, BarConfiguration sett) {
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
		
		double perc = config.getPercentage(pb, this.col);			
		
		boolean leftRight=config.ltr();
		
		if (isSelected && config.showSelection()) {
			graphics.setColor(Color.red);
		} else {
			graphics.setColor(Color.black);
		}
		
		data.getCellRectangle(row, col, bounds);
		
		if (leftRight) {
			bounds.setFrame(0,0,bounds.getWidth()*perc,bounds.getHeight());
		} else {
			bounds.setFrame(0,bounds.getHeight()*(1-perc),bounds.getWidth(),bounds.getHeight());
		}
		
		
		
		
		graphics.fill(bounds);
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
//		if (e.hasSource(config.getSetting().getChild("Sort Experiments")))
//			fireChange(UpdateEvent.SIZE_CHANGE);
//		else
			fireChange(UpdateEvent.REPAINT);
	}
	
	public BarConfiguration getConfiguration() {
		return config;
	}

	@Override
	public void dispose() {
		config.dispose();		
	}
	
}
