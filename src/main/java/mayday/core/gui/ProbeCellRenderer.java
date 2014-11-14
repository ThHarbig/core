package mayday.core.gui;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import mayday.core.Probe;

/*
 * Created on Feb 4, 2005
 *
 */

/**
 * @author neil
 * @version 
 */
@SuppressWarnings("serial")
public class ProbeCellRenderer
extends JLabel
implements ListCellRenderer
{

	protected boolean useDisplayNames;

	public ProbeCellRenderer(boolean useDisplayNames) {
		this.useDisplayNames=useDisplayNames;
	}

	public ProbeCellRenderer() {
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
		
		if ( ! ( value instanceof Probe ) ) {
			setText( value.toString() );
		} else {

			Probe pb = (Probe)value;

			String  s = "<html>";

			if (useDisplayNames)
				s += pb.getDisplayName();
			else
				s += pb.getName();

			s += "<small><font color=#888888>";
			s += "&nbsp;&nbsp;P=" + pb.getNumberOfProbeLists();			
			s += "&nbsp;&nbsp;M=" + pb.getMasterTable().getDataSet().getMIManager().getGroupsForObject(pb).size();

			s += "</font></small>";
			s += "</html>"; 
			setText( s );
			
			if (pb.getAnnotation()!=null)
				setToolTipText( pb.getAnnotation().getQuickInfo() );
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
