package mayday.vis3.plots.heatmap2.interaction;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public abstract class PassthroughMouseListener implements MouseListener, MouseWheelListener, MouseMotionListener {

	protected abstract MouseListener coordinateToListener(MouseEvent e);
	protected abstract MouseMotionListener coordinateToMotionListener(MouseEvent e);
	protected abstract MouseWheelListener coordinateToWheelListener(MouseEvent e);

	@Override
	public void mouseClicked(MouseEvent e) {
		MouseListener ml = coordinateToListener(e);
		if (ml!=null)
			ml.mouseClicked(e);

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		MouseListener ml = coordinateToListener(e);
		if (ml!=null)
			ml.mouseEntered(e);

	}

	@Override
	public void mouseExited(MouseEvent e) {
		MouseListener ml = coordinateToListener(e);
		if (ml!=null)
			ml.mouseExited(e);

	}

	@Override
	public void mousePressed(MouseEvent e) {
		MouseListener ml = coordinateToListener(e);
		if (ml!=null)
			ml.mousePressed(e);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		MouseListener ml = coordinateToListener(e);
		if (ml!=null)
			ml.mouseReleased(e);

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		MouseWheelListener ml = coordinateToWheelListener(e);
		if (ml!=null)
			ml.mouseWheelMoved(e);

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		MouseMotionListener ml = coordinateToMotionListener(e);
		if (ml!=null)
			ml.mouseDragged(e);

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		MouseMotionListener ml = coordinateToMotionListener(e);
		if (ml!=null)
			ml.mouseMoved(e);

	}

}