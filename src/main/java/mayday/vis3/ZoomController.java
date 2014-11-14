package mayday.vis3;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JViewport;

/** Adds mouse and keyboard zoom interactivity to any kind of plot. Just put this into your PlotComponent's setup() function:
 * new ZoomController(this);
 * @author battke
 *
 */
public class ZoomController {
	
	private boolean Active = true;
	private boolean allowXOnlyZooming = false;
	private boolean allowYOnlyZooming = false;
	private ZoomKeyListener zkl = new ZoomKeyListener();
	private ZoomMouseWheelListener zmwl = new ZoomMouseWheelListener();
	
	protected JComponent target;

	public ZoomController() {		
	}
	
	public void setTarget(JComponent targetComponent) {
		if (target!=null) {
			removeEventSource(target);
		}
		this.target=targetComponent;
		if (target!=null) {
			addEventSource(target);
		}
	}
	
	public void addEventSource(Component source) {
		removeEventSource(source);
		source.addKeyListener(zkl);
		source.addMouseWheelListener(zmwl);		
	}
	
	public void removeEventSource(Component source) {
		source.removeKeyListener(zkl);
		source.removeMouseWheelListener(zmwl);		
	}
	
	protected void zoom(boolean zoomIn, boolean zoomX, boolean zoomY) {
		double zoomFac = zoomIn?1.2:0.8;
		double zX = zoomX?zoomFac:1.0;
		double zY = zoomY?zoomFac:1.0;		
		Dimension newSize = new Dimension((int)(target.getWidth()*zX), (int)(target.getHeight()*zY));
		target.setPreferredSize(newSize);
		target.setMaximumSize(newSize);
		target.setMinimumSize(newSize);
		target.revalidate();
	}
	
	private void zoomWithEvent(boolean zoomIn, InputEvent e) {
		boolean zoomX = true;
		boolean zoomY = true;
		if (allowXOnlyZooming && e.isShiftDown())
			zoomY=false;
		if (allowYOnlyZooming && e.isAltDown())
			zoomX=false;
		if (!zoomY & !zoomX)
			zoomX=zoomY=true;

		double vX=0, vY=0;
		double mX=0, mY=0;
		double dX=0, dY=0;
		Rectangle vpRect = null;
		
		// Collect information on the current mouse position within the plot
		Component p = target;
		while (p!=null && !(p instanceof JViewport))
			p = p.getParent();
		
		if (p!=null) {
			vpRect = ((JViewport)p).getViewRect();
			vX = vpRect.x;
			vY = vpRect.y;
			if (e instanceof MouseEvent) {
				Point mousePos = ((MouseEvent)e).getPoint();
				mX = mousePos.x;
				mY = mousePos.y;
				dX = mX - vX;
				dY = mY - vY;
			}
		}		
		
		// Do the zooming		
		zoom(zoomIn,zoomX,zoomY);
		
		// Move the plot so that the mouse position remains the same in plot coordinate spaec
		if (p!=null && vpRect!=null)  {
			Dimension newSize = target.getPreferredSize();
			Dimension oldSize = target.getSize();
			double scaleX = newSize.getWidth()/oldSize.getWidth();
			double scaleY = newSize.getHeight()/oldSize.getHeight();
			double mX_ = mX * scaleX;
			double mY_ = mY * scaleY;		
			double vX_ = mX_-dX - vX;
			double vY_ = mY_-dY - vY;
			vpRect.x = (int)vX_;
			vpRect.y = (int)vY_;
			((JViewport)p).scrollRectToVisible(vpRect);		
		}
	}
	
	public boolean isActive() {
		return Active;
	}

	public void setActive(boolean active) {
		Active = active;
	}

	public boolean doesAllowXOnlyZooming() {
		return allowXOnlyZooming;
	}

	public void setAllowXOnlyZooming(boolean allowXOnlyZooming) {
		this.allowXOnlyZooming = allowXOnlyZooming;
	}

	public boolean doesAllowYOnlyZooming() {
		return allowYOnlyZooming;
	}

	public void setAllowYOnlyZooming(boolean allowYOnlyZooming) {
		this.allowYOnlyZooming = allowYOnlyZooming;
	}
	
	private class ZoomKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (!Active) 
				return;
			int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
				if (e.getKeyCode()==KeyEvent.VK_MINUS || e.getKeyCode()==KeyEvent.VK_SUBTRACT || e.getKeyChar()=='-') 
					zoomWithEvent(false,e);
				if (e.getKeyCode()==KeyEvent.VK_PLUS || e.getKeyCode()==KeyEvent.VK_ADD || e.getKeyChar()=='+')
					zoomWithEvent(true,e);
			}
		}
	}
	
	private class ZoomMouseWheelListener implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (!Active)
				return;
			int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
				zoomWithEvent(e.getWheelRotation()<0,e);
			}
		}		
	}
	
	
}
