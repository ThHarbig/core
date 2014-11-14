package mayday.vis3.gui.events;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class EventTranslator {

	public static MouseEvent translate(MouseEvent me, int x, int y) {
		return new MouseEvent((Component)me.getSource(),
				me.getID(),
				me.getWhen(),
				me.getModifiers(),
				me.getX()+x,
				me.getY()+y,
				me.getClickCount(), 
				me.isPopupTrigger());
	}
	
	public static MouseWheelEvent translate(MouseWheelEvent me, int x, int y) {
		return new MouseWheelEvent (
				(Component)me.getSource(),
				me.getID(),
				me.getWhen(),
				me.getModifiers(),
				me.getX()+x,
				me.getY()+y,
				me.getClickCount(), 
				me.isPopupTrigger(),
                me.getScrollType(),
                me.getScrollAmount(),
                me.getWheelRotation());
	}
	
	
}
