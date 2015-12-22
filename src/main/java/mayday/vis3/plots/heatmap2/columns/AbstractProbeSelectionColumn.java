package mayday.vis3.plots.heatmap2.columns;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.SelectionMouseListener;

public abstract class AbstractProbeSelectionColumn extends AbstractColumn  {

	protected MouseAdapter ml;	
	protected HeatmapStructure data;
	
	public AbstractProbeSelectionColumn(HeatmapStructure struct) {
		data = struct;
		ml = SelectionMouseListener.getListenerInstance(data);
	}
	
	@Override
	public MouseListener getMouseListener() {
		return ml;
	}

	@Override
	public MouseMotionListener getMouseMotionListener() {
		return ml;
	}

	@Override
	public MouseWheelListener getMouseWheelListener() {
		return null;
	}
	
	public double getDesiredWidth() {
		return 1;
	}
	
	
}
