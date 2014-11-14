/*
 * Created on Feb 14, 2005
 *
 */
package mayday.core.meta;

import java.util.EventObject;

/**
 * @author gehlenbo
 *
 */
@SuppressWarnings("serial")
public class MIManagerEvent
extends EventObject
{
  public final static int GROUP_DELETED = 0x01;
  public final static int GROUP_ADDED = 0x02;
  public final static int GROUP_RENAMED = 0x04;
  public final static int OVERALL_CHANGE = GROUP_DELETED |
                                           GROUP_ADDED | GROUP_RENAMED;

  private int change;

  public MIManagerEvent( MIManager source, int change )  {
    super( source );    
    this.change = change;
  }
    
  public int getChange()  {
    return ( this.change ); 
  }  
  
  public void setChange( int change )  {
    this.change = change;
  }  
  

	public boolean equals(Object evt) {
		if (evt instanceof MIManagerEvent)
			return ((MIManagerEvent)evt).getSource()==source && ((MIManagerEvent)evt).getChange()==change;
		return super.equals(evt);
	}

}
