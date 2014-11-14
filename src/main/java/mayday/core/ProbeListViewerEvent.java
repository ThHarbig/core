package mayday.core;
import java.util.EventObject;

/*
 * Created on Apr 15, 2003
 *
 */

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ProbeListViewerEvent
extends EventObject
{
  public final static int CONTENT_CHANGE = 1;
  public final static int ORDER_CHANGE = 2;
  public final static int SELECTION_CHANGE = 4;
  public final static int LAYOUT_CHANGE = 8;
  public final static int ANNOTATION_CHANGE = 16;
  public final static int LAYER_CHANGE = 32;
  public final static int OVERALL_CHANGE = CONTENT_CHANGE |
                                           ORDER_CHANGE |
                                           SELECTION_CHANGE |
                                           LAYOUT_CHANGE |
                                           ANNOTATION_CHANGE |
                                           LAYER_CHANGE;
                                           
  private int change;
  private ProbeList object;

  public ProbeListViewerEvent( Object source, int change, ProbeList object )
  {
    super( source );
    
    this.change = change;
    this.object = object;
  }

  public ProbeListViewerEvent( Object source, int change )
  {
    super( source );
    
    this.change = change;
    this.object = null;
  }
  
  
  public int getChange()
  {
    return ( this.change ); 
  }
  
  
  public void setChange( int change )
  {
    this.change = change;
  }  
  
  
  public ProbeList getObject()
  {
    return ( this.object );
  }
  
  
  public void setObject( ProbeList object )
  {
    this.object = object;
  }
}
