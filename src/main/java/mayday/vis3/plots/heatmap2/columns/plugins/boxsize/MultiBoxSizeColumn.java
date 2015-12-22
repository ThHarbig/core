package mayday.vis3.plots.heatmap2.columns.plugins.boxsize;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.plots.heatmap2.columns.AbstractProbeSelectionColumn;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class MultiBoxSizeColumn extends AbstractProbeSelectionColumn implements SettingChangeListener {

	protected Integer col;
	protected BoxSizeConfiguration config;
	protected Rectangle2D bounds = new Rectangle2D.Double();
	
	public MultiBoxSizeColumn(HeatmapStructure struct, Integer column, BoxSizeConfiguration sett) {
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
		
		if (isSelected && config.showSelection()) {
			graphics.setColor(Color.red);
		} else {
			graphics.setColor(Color.black);
		}
		
		data.getCellRectangle(row, col, bounds);
		
		double newW = bounds.getWidth()*perc;
		double newH = bounds.getHeight()*perc;
		double newX = (bounds.getWidth()-newW)/2;
		double newY = (bounds.getHeight()-newH)/2;
		
		bounds.setFrame(newX,newY,newW,newH);
		
		graphics.fill(bounds);
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
//		if (e.hasSource(config.getSetting().getChild("Sort Experiments")))
//			fireChange(UpdateEvent.SIZE_CHANGE);
//		else
			fireChange(UpdateEvent.REPAINT);
	}
	
	public BoxSizeConfiguration getConfiguration() {
		return config;
	}

	@Override
	public void dispose() {
		config.dispose();		
	}
	
}
