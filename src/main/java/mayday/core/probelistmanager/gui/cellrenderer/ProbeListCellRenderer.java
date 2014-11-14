package mayday.core.probelistmanager.gui.cellrenderer;
import javax.swing.*;

import mayday.core.ProbeList;

import java.awt.*;
//import java.awt.geom.*;

/*
 * Created on Apr 5, 2003
 *
 */

/**
 * @author neil
 * @version 
 */
@SuppressWarnings("serial")
public class ProbeListCellRenderer
extends JLabel
implements ListCellRenderer
{
	// This is the only method defined by ListCellRenderer.
	// We just reconfigure the JLabel each time we're called.

	private Color color;

	public Component getListCellRendererComponent (
			JList list,
			Object value,            // value to display
			int index,               // cell index
			boolean isSelected,      // is the cell selected
			boolean cellHasFocus )    // the list and the cell have the focus
	{

		//if ( !value.getClass().equals( ProbeList.class ) )  //W-T-F ??
		if (!(value instanceof ProbeList)) {
			setText( value.toString() );

			return ( this ); 
		}

		String  s = "<html><nobr>"; 
		s += ((ProbeList)value).getName();

		s += "<small><font color=#888888>";
		s += "&nbsp;&nbsp;S=" + ((ProbeList)value).getNumberOfProbes();
		s += "&nbsp;&nbsp;M=" + ((ProbeList)value).getDataSet().getMIManager().getGroupsForObject(value).size();
		s += "</nobr></small>";
		s += "</html>"; 
		setText( s );

		color = ((ProbeList)value).getColor();

		if ( isSelected )
		{
			setForeground( color );
			setBackground( list.getSelectionBackground() );
		}
		else
		{
			setForeground( color );
			setBackground( list.getBackground() );
		}	

		setEnabled( list.isEnabled() );
		setFont( list.getFont() );
		setOpaque( true );

		setToolTipText( ((ProbeList)value).getAnnotation().getQuickInfo() );

		return ( this );
	}	
}
