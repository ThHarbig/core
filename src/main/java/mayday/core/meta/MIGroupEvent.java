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
public class MIGroupEvent
extends EventObject
{
  public final static int MIO_ADDED = 0x01;
  public final static int MIO_REMOVED = 0x02;
  public final static int MIO_REPLACED = 0x04;
  public final static int OVERALL_CHANGE = MIO_ADDED | MIO_REMOVED | MIO_REPLACED;

  private int change;
  private Object mioExtendable;

  public MIGroupEvent( MIGroup source, int change, Object mioextendable )  {
    super( source );    
    this.change = change;
    this.mioExtendable=mioextendable;
  }
    
  public int getChange()  {
    return ( this.change ); 
  }  
  
  public void setChange( int change )  {
    this.change = change;
  }

  public Object getMioExtendable() {
	return mioExtendable;
  }

  public void setMioExtendable(Object mioExtendable) {
	this.mioExtendable = mioExtendable;
  }  
  
  public boolean equals(Object evt) {
		if (evt instanceof MIGroupEvent)  
			return ((MIGroupEvent)evt).getSource()==source && ((MIGroupEvent)evt).getChange()==change && ((MIGroupEvent)evt).getMioExtendable()==mioExtendable;
		return super.equals(evt);
  }
}
