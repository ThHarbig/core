package mayday.core.gui;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import mayday.core.Experiment;

@SuppressWarnings("serial")
public class ExperimentCellRenderer
extends JLabel
implements ListCellRenderer
{

	protected boolean useDisplayNames;

	public ExperimentCellRenderer(boolean useDisplayNames) {
		this.useDisplayNames=useDisplayNames;
	}

	public ExperimentCellRenderer() {
		this(false);
	}

	// This is the only method defined by ListCellRenderer.
	// We just reconfigure the JLabel each time we're called.

	public Component getListCellRendererComponent (
			JList list,
			Object value,            // value to display
			int index,               // cell index
			boolean isSelected,      // is the cell selected
			boolean cellHasFocus )    // the list and the cell have the focus
	{
		
		if ( ! (value instanceof Experiment) ) {
			setText( value.toString() );
		} else {

			Experiment e = (Experiment)value;

			String  s = "<html>";

			if (useDisplayNames)
				s += e.getDisplayName();
			else
				s += e.getName();

			s += "<small><font color=#888888>";
			s += "&nbsp;&nbsp;M=" + e.getMasterTable().getDataSet().getMIManager().getGroupsForObject(e).size();

			s += "</font></small>";
			s += "</html>"; 
			setText( s );
			
			if (e.getAnnotation()!=null)
				setToolTipText( e.getAnnotation().getQuickInfo() );
		}

		if ( isSelected )
		{
			setForeground( list.getSelectionForeground() );
			setBackground( list.getSelectionBackground() );
		}
		else
		{
			setForeground( list.getForeground() );
			setBackground( list.getBackground() );
		}	

		setEnabled( list.isEnabled() );
		setFont( list.getFont() );
		setOpaque( true );

		return ( this );
	}	
}
