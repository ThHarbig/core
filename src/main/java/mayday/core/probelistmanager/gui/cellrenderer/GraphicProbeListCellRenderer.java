package mayday.core.probelistmanager.gui.cellrenderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.gui.ProbeListImage;
import mayday.core.gui.ProbeListImageStorage;

@SuppressWarnings("serial")
public class GraphicProbeListCellRenderer 
extends GraphicProbeListRenderComponent
implements ListCellRenderer
{
	private Color color;
	
	
	public Component getListCellRendererComponent (
			 JList list,
			 Object value,            // value to display
			 int index,               // cell index
			 boolean isSelected,      // is the cell selected
			 boolean cellHasFocus )    // the list and the cell have the focus
	{
		
		if (!(value instanceof ProbeList))
			return this;
	   
	    setName(((ProbeList)value).getName());
	    setNumProbes(((ProbeList)value).getNumberOfProbes());
	    setMI(((ProbeList)value));
	    
	    
		if ( isSelected )
		{
			setForeground( color );
			setBackground( list.getSelectionBackground() );
		}
		else
		{
			setForeground( color );
			setBackground( Color.WHITE);//list.getBackground() );
		}
		
		ProbeListImage img=ProbeListImageStorage.singleInstance().getImage((ProbeList)value);
		if( img==null) System.out.println("Error");
		
		// this is quite slow because it's not using a background thread
		// i think we don't really need background color changes in the image
		/*
		if( !img.getBackgroundColor().equals(getBackground()) )
	    {
	    	//redraw due to selection change
			img=new ProbeListImage(((ProbeList)value),getBackground());
			ProbeListImageStorage.singleInstance().setImage((ProbeList)value,img);				
	    }
	    */
		
	    setImage(new ImageIcon(img));	    
        boolean b2=MaydayDefaults.Prefs.showPLToolTips.getBooleanValue();
        if (((ProbeList)value).getAnnotation()!=null)
        	setToolTipText( ((ProbeList)value).getAnnotation().getQuickInfo().equals("")||!b2?null:((ProbeList)value).getAnnotation().getQuickInfo() );
	    
        //System.out.print(value+" ");
        
		return this;
	}
}

