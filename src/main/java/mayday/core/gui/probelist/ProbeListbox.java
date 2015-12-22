/*
 * Created on Feb 7, 2005
 *
 */
package mayday.core.gui.probelist;

import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.event.MouseInputAdapter;

import mayday.core.gui.ProbeCellRenderer;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;

/**
 * @author gehlenbo
 *
 */
@SuppressWarnings("serial")
public class ProbeListbox
extends JList
{
  public ProbeListbox()
  {
    super();
    
    init();
  }
  
  
  public ProbeListbox( Object[] objects )
  {
    super( objects );
// 070323 fb: this line gives enormous speedup - not all probe names have to be inspected for their height
    this.setPrototypeCellValue( "any string works here" );     
    init();
  }
  
  
  private void init()
  {
    this.setCellRenderer( new ProbeCellRenderer(true) );
    this.addMouseListener( new ProbeListboxMouseListener() );
  }
  
  
  protected class ProbeListboxMouseListener
  extends MouseInputAdapter
  {      
    public void mouseClicked( MouseEvent e )
    {        
      if ( e.getButton() == MouseEvent.BUTTON1 )
      {
        if ( e.getClickCount() == 2 )
        {
          if ( getSelectedValue() != null )
          {
        	  AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(getSelectedValues());
        	  apd.setVisible(true);
          }
        }
      }
    }
  }  
}
