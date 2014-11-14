package mayday.core;
import java.util.EventObject;

/*
 * Created on June 22, 2003
 */

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
@SuppressWarnings("serial")
public class MasterTableEvent
extends EventObject
{

  public final static int USER_PROBE_ADDED_CHANGE = 1;
  public final static int USER_PROBE_REMOVED_CHANGE = 2;
  public final static int SYSTEM_PROBE_ADDED_CHANGE = 4;
  public final static int SYSTEM_PROBE_REMOVED_CHANGE = 8;
  public final static int EXPERIMENT_ORDERING_CHANGED = 16;
  
  public final static int OVERALL_CHANGE = USER_PROBE_ADDED_CHANGE |
                                           USER_PROBE_REMOVED_CHANGE |
                                           SYSTEM_PROBE_ADDED_CHANGE |
                                           SYSTEM_PROBE_REMOVED_CHANGE |
                                           EXPERIMENT_ORDERING_CHANGED ;
  private int change;

  public MasterTableEvent( Object source, int change )
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
		if (evt instanceof MasterTableEvent)
			return ((MasterTableEvent)evt).getSource()==source && ((MasterTableEvent)evt).getChange()==change;
		return super.equals(evt);
	}
  
}
