/*
 * Created on Feb 4, 2005
 *
 */
package mayday.core.gui.abstractdialogs;


import java.util.*;

import javax.swing.*;

/**
 * @author gehlenbo
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractStandardDialogComponent extends Box {
	
	public AbstractStandardDialogComponent( int direction )	{
		super( direction );
	}

	public abstract ArrayList< Action > getOkActions();

	public static AbstractStandardDialogComponent createFromPanel(JPanel panel) {
		return new PanelWrappingASD(panel);
	}

	private static class PanelWrappingASD extends AbstractStandardDialogComponent {
		public PanelWrappingASD(JPanel panel) {
			super(BoxLayout.Y_AXIS);
			add(panel);
		}
		public ArrayList<Action> getOkActions() {
			return null;
		}
	}
	
}
