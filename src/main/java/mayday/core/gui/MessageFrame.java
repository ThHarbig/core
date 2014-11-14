package mayday.core.gui;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")
public class MessageFrame extends MessageFrameBase
{
	public MessageFrame() {
		super("Mayday Messages");
		System.setErr(new PrintStream(addOutputStream(Color.RED),true));
		System.setOut(new PrintStream(addOutputStream(Color.BLACK),true));	
	}

	public void stateChanged(ChangeEvent e)
	{
		if( (e.getSource() instanceof JCheckBoxMenuItem) )
		{
			if(isVisible() != ((JCheckBoxMenuItem)e.getSource()).isSelected())
			{
				setVisible(!isVisible());
			}
		}		
	}

	public static class VisibilityWindowListener
	implements WindowListener
	{
		protected JCheckBoxMenuItem item;
		public VisibilityWindowListener(JCheckBoxMenuItem item)
		{
			this.item = item;
		}

		public void windowOpened(WindowEvent e)
		{}

		public void windowClosing(WindowEvent e)
		{
			item.setSelected(false);            
		}

		public void windowClosed(WindowEvent e)
		{}

		public void windowIconified(WindowEvent e)
		{}

		public void windowDeiconified(WindowEvent e)
		{}

		public void windowActivated(WindowEvent e)
		{}

		public void windowDeactivated(WindowEvent e)
		{}        
	}
}
