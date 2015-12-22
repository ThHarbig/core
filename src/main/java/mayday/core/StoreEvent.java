/*
 * Created on 30.06.2005
 */
package mayday.core;

import java.util.EventObject;

/**
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 30.06.2005
 *
 */
@SuppressWarnings("serial")
public class StoreEvent
extends EventObject
{
    /*
     * the obj should be an instance of Storable!
     */
    private Object object;

    /**
     * @param source
     */
    public StoreEvent(Store source, Object object) 
    {
        super(source);
        this.object = object;
    }
    
    public Store getSource()
    {
        return (Store)getSource();
    }
    
    protected void setObject(Object obj)
    {
        this.object = obj;
    }
    
    public Object getObject()
    {
        return object;
    }
}
