package mayday.vis3.gui.events;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class TranslatingMouseWheelListener implements MouseWheelListener {

	protected MouseWheelListener child;
	protected Integer dX,dY; // class "Integer" so that external modification is instantly seen here (pointer-like)
	
	public TranslatingMouseWheelListener(MouseWheelListener ml, Integer deltaX, Integer deltaY) {
		child = ml;
		dX=deltaX;
		dY=deltaY;
	}
	
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		MouseWheelEvent me = EventTranslator.translate(e,dX,dY);
		if (me.getX()>=0 && me.getY()>=0)
			child.mouseWheelMoved(me);
	}

}
