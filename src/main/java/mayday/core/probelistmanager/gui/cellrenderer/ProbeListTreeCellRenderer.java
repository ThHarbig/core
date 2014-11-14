package mayday.core.probelistmanager.gui.cellrenderer;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import mayday.core.ProbeList;
import mayday.core.probelistmanager.UnionProbeList;
import mayday.core.probelistmanager.gui.ProbeListNode;


@SuppressWarnings("serial")
public class ProbeListTreeCellRenderer
extends JLabel
implements TreeCellRenderer
{
	private Color color;
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) 
	{
	
		if (!(value instanceof ProbeListNode))
			return this;
		
		ProbeList pl = ((ProbeListNode)value).getProbeList();
		
		if (pl==null)
			return this;
		
		String  s = "<html><nobr>"; 
		s += pl.getName();

		s += "<small><font color=#888888>";
	    if (pl instanceof UnionProbeList && ((UnionProbeList)pl).getNode()!=null) 
	    	s+="&nbsp;&nbsp;L="+((UnionProbeList)pl).getNode().getChildCount();	    	
		s += "&nbsp;&nbsp;S=" + pl.getNumberOfProbes();
		s += "&nbsp;&nbsp;M=" + pl.getDataSet().getMIManager().getGroupsForObject(pl).size();
		s += "</nobr></small>";
		s += "</html>"; 
		setText( s );

		color = pl.getColor();

		if ( selected )
		{
			setForeground( color );
			setBackground( UIManager.getColor("List.selectionBackground") ); 
		}
		else
		{
			setForeground( color );
			setBackground( Color.WHITE);
		}	

		setEnabled( tree.isEnabled() );
		setFont( tree.getFont() );
		setOpaque( true );

		setToolTipText( pl.getAnnotation().getQuickInfo() );

		return ( this );
	}
	
}
