package mayday.core.probelistmanager.gui.cellrenderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.gui.ProbeListImage;
import mayday.core.gui.ProbeListImageStorage;
import mayday.core.probelistmanager.UnionProbeList;
import mayday.core.probelistmanager.gui.ProbeListNode;

@SuppressWarnings("serial")
public class GraphicProbeListTreeCellRenderer 
extends GraphicProbeListRenderComponent
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
	   
	    setName(pl.getName());
	    
	    if (pl instanceof UnionProbeList) {
	    	ProbeListNode pln = ((UnionProbeList)pl).getNode();
	    	if (pln!=null)
	    		setNumProbes("["+pln.getChildCount()+"]  "+pl.getNumberOfProbes());	    	
	    } else {
	    	setNumProbes(pl.getNumberOfProbes());
	    }
	    	    
	    setMI(pl);
	    
	    
		if ( selected )
		{
			setForeground( color );
			setBackground( UIManager.getColor("List.selectionBackground") );
		}
		else
		{
			setForeground( color );
			setBackground( Color.WHITE);//list.getBackground() );
		}
		
		ProbeListImage img=ProbeListImageStorage.singleInstance().getImage(pl);
		if( img!=null) 
			setImage(new ImageIcon(img));
		
        boolean b2=MaydayDefaults.Prefs.showPLToolTips.getBooleanValue();
        if (pl.getAnnotation()!=null)
        	setToolTipText( pl.getAnnotation().getQuickInfo().equals("")||!b2?null:pl.getAnnotation().getQuickInfo() );
	            
		return this;
	}
	
}

