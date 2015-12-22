/*
 * Created on Feb 14, 2005
 *
 */
package mayday.core.meta;

import java.util.EventListener;

/**
 * @author battke
 *
 */
public interface MIGroupListener
extends EventListener
{
	public void miGroupChanged( MIGroupEvent event );  

	/** This method determines which events will trigger this listener.
	 * @return null if you want to be notified whenever ANY object is added or removed from the group,
	 * or an instance of Object to only be notified of changes applying to this specific object instance.
	 */
	public Object getWatchedObject(); //return null to be notified of all objects

}
