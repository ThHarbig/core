package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;

@SuppressWarnings("serial")
public abstract class GoToProbeAction extends AbstractAction {
	
	public GoToProbeAction()	{
		super( "Find Probe..." );
	}

	public void actionPerformed( ActionEvent event ) {      
		String l_probeName = new String();

		do {
			l_probeName = (String)JOptionPane.showInputDialog( null,
					"Enter a probe identifier.",
					MaydayDefaults.Messages.INFORMATION_TITLE,
					JOptionPane.INFORMATION_MESSAGE,
					null,
					null,
					"" );
			if ( l_probeName == null )         
				return;

			if ( l_probeName.trim().equals( "" ) ) {
				JOptionPane.showMessageDialog( null,
						"Empty probe names do not exist.",                                           
						MaydayDefaults.Messages.ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE ); 
			}
		}
		while ( l_probeName.trim().equals( "" ) );

		if (!goToProbe( l_probeName )) {
			JOptionPane.showMessageDialog( null,
					"Probe \"" + l_probeName + "\" not contained in this view.",                                           
					MaydayDefaults.Messages.INFORMATION_TITLE,
					JOptionPane.INFORMATION_MESSAGE ); 
		}
	}  
	
	public abstract boolean goToProbe( String probeName );
	
}