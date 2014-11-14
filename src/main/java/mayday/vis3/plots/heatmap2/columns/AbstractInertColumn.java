package mayday.vis3.plots.heatmap2.columns;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public abstract class AbstractInertColumn extends AbstractColumn {

	@Override
	public MouseListener getMouseListener() {
		return null;
	}

	@Override
	public MouseMotionListener getMouseMotionListener() {
		return null;
	}

	@Override
	public MouseWheelListener getMouseWheelListener() {
		return null;
	}
	
}
