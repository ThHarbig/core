package mayday.core.io.nativeformat;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;

import mayday.core.Mayday;

@SuppressWarnings("serial")
public class ExitAction extends AbstractAction
{
	public ExitAction() {
		super( "Exit" );
	}

	public void actionPerformed( ActionEvent event ) {
		Mayday.sharedInstance.windowClosing(new WindowEvent(Mayday.sharedInstance,0));
	}
	
}