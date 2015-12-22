package mayday.vis3.gui.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class TranslatingMouseMotionListener implements MouseMotionListener {

	protected MouseMotionListener child;
	protected Integer dX,dY; // class "Integer" so that external modification is instantly seen here (pointer-like)
	
	public TranslatingMouseMotionListener(MouseMotionListener ml, Integer deltaX, Integer deltaY) {
		child = ml;
		dX=deltaX;
		dY=deltaY;
	}
	
	
	public void mouseDragged(MouseEvent e) {
		MouseEvent me = EventTranslator.translate(e,dX,dY);
		if (me.getX()>=0 && me.getY()>=0)
			child.mouseDragged(me);
	}

	public void mouseMoved(MouseEvent e) {
		MouseEvent me = EventTranslator.translate(e,dX,dY);
		if (me.getX()>=0 && me.getY()>=0)
			child.mouseMoved(me);
	}

}
