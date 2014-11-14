package mayday.core.datasetmanager.gui;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import mayday.core.DataSet;
import mayday.core.MasterTable;


@SuppressWarnings("serial")
public class DataSetCellRenderer
extends JLabel
implements ListCellRenderer
{
	// This is the only method defined by ListCellRenderer.
	// We just reconfigure the JLabel each time we're called.

	public Component getListCellRendererComponent (
			JList list,
			Object value,            // value to display
			int index,               // cell index
			boolean isSelected,      // is the cell selected
			boolean cellHasFocus )    // the list and the cell have the focus
	{
		
		if (!(value instanceof DataSet)) {
			setText( value.toString() );
			return ( this ); 
		}

		DataSet ds = (DataSet)value;
		MasterTable mt = ds.getMasterTable();
		
		String  s = "<html><nobr>"; 
		s += ds.getName();

		s += "<small><font color=#888888>";
		s += "&nbsp;&nbsp;" + mt.getNumberOfProbes();
		s += "x" + mt.getNumberOfExperiments();
		s += "</nobr></small>";
		s += "</html>"; 
		setText( s );

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

//		setToolTipText( ds.getAnnotation().getQuickInfo() );

		return ( this );
	}	
}
