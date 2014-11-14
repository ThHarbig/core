/*
 * Created on 30.06.2005
 */
package mayday.core;

import java.util.EventListener;


/**
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 30.06.2005
 *
 */
public interface StoreListener 
extends EventListener
{
    public void objectAdded(StoreEvent event);
    
    public void objectRemoved(StoreEvent event);
}
