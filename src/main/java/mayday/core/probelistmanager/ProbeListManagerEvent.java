package mayday.core.probelistmanager;
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
public class ProbeListManagerEvent
extends EventObject
{
  public final static int CONTENT_CHANGE = 1;
  public final static int ORDER_CHANGE = 2;
  public final static int LAYOUT_CHANGE = 4;
  public final static int ANNOTATION_CHANGE = 8;
  public final static int OVERALL_CHANGE = CONTENT_CHANGE |
                                           ORDER_CHANGE |
                                           LAYOUT_CHANGE |
                                           ORDER_CHANGE;
  private int change;

  public ProbeListManagerEvent( Object source, int change )
  {
  	super( source );
    
    this.change = change;
  }
  
  
  public int getChange()
  {
    return ( this.change ); 
  }
  
  
  public void setChange( int change )
  {
    this.change = change;
  }  
  
  
  public boolean equals(Object evt) {
		if (evt instanceof ProbeListManagerEvent)  
			return ((ProbeListManagerEvent)evt).getSource()==source && ((ProbeListManagerEvent)evt).getChange()==change;
		return super.equals(evt);
  }
}
