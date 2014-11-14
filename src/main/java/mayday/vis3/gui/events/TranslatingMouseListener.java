package mayday.vis3.gui.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TranslatingMouseListener implements MouseListener {

	protected MouseListener child;
	protected Integer dX,dY; // class "Integer" so that external modification is instantly seen here (pointer-like)
	
	public TranslatingMouseListener(MouseListener ml, Integer deltaX, Integer deltaY) {
		child = ml;
		dX=deltaX;
		dY=deltaY;
	}
		
	public void mouseClicked(MouseEvent e) {
		MouseEvent me = EventTranslator.translate(e,dX,dY);
		if (me.getX()>=0 && me.getY()>=0)
			child.mouseClicked(me);
	}

	public void mouseEntered(MouseEvent e) {
		MouseEvent me = EventTranslator.translate(e,dX,dY);
		if (me.getX()>=0 && me.getY()>=0)
			child.mouseEntered(me);
	}

	public void mouseExited(MouseEvent e) {
		MouseEvent me = EventTranslator.translate(e,dX,dY);
		if (me.getX()>=0 && me.getY()>=0)
			child.mouseExited(me);
	}

	public void mousePressed(MouseEvent e) {
		MouseEvent me = EventTranslator.translate(e,dX,dY);
		if (me.getX()>=0 && me.getY()>=0)
			child.mousePressed(me);
	}

	public void mouseReleased(MouseEvent e) {
		MouseEvent me = EventTranslator.translate(e,dX,dY);
		if (me.getX()>=0 && me.getY()>=0)
			child.mouseReleased(me);
	}

}
