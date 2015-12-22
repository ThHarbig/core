package mayday.core;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


// fb 090126 - store now is a listmodel and only translates listmodelevents to the ancient storeevents for backwards compatibility

@SuppressWarnings({ "unchecked", "serial" })
public abstract class Store extends DefaultListModel implements ListDataListener
{
    protected EventListenerList storeListenerList = new EventListenerList();
	protected LinkedList last_objects = new LinkedList(); // always synchronous with the list content    
  
	public Store()	{
		this.addListDataListener(this); //to translate listmodelevents to storeevents
	}

	public List getObjects() {
		LinkedList<Object> newList = new LinkedList<Object>();
		Enumeration en = elements();
		while (en.hasMoreElements())
			newList.add(en.nextElement());
		return newList;
	}

	public void setObjects( List objects ) {
		clear();
		int i=0;
		for (Object o : objects)
			add(i++, o);
		last_objects.addAll(objects);
	}


	public int getNumberOfObjects()	{
		return size();
	}


	public void add(int index, Object element) {
		last_objects.add(index, element);
		super.add(index, element);
	}
	
	public void addObject( Object object ) {
		int i = size();
		add(i, object);
	}
	
	public void addObjectAtBottom( Object object ) {
		addObject( object );
	}

	public void addObjectAtTop( Object object ) {
		add( 0, object );
	}

	public Object remove(int index) {
		Object o = super.remove(index);
		last_objects.remove(index);
		return o;
	}
	
	public void removeObject( Object object ) {
		removeElement(object);
		last_objects.remove(object);
	}

	public void clear()	{
		super.clear();
		last_objects.clear();
	}  
  
	public boolean contains( String name ) {
		return contains(name,null);
	}
	
	public boolean contains( String name, Object unequalTo ) {
		for ( Object o : getObjects() ) {
			if ( ((Storable)o).getName().equals( name ) ) {
				if (unequalTo==null || unequalTo!=o)
					return true;
			}
		}  
		return false;
	}
  
    public void addStoreListener(StoreListener listener) {
        storeListenerList.add(StoreListener.class, listener);
    }
    
    public void removeStoreListener(StoreListener listener) {
        storeListenerList.remove(StoreListener.class, listener);
    }
    
    public void fireObjectAdded(Object obj) {
    	StoreEvent se = new StoreEvent(this, obj);
    	for(StoreListener l:storeListenerList.getListeners(StoreListener.class))
    		l.objectAdded(se);
    }
    
    public void fireObjectRemoved(Object obj) {
    	StoreEvent se = new StoreEvent(this, obj);
    	for(StoreListener l:storeListenerList.getListeners(StoreListener.class))
    		l.objectRemoved(se);
    }


	public void contentsChanged(ListDataEvent e) {
		// find out what has changed
		LinkedList<Object> newList = new LinkedList<Object>();
		Enumeration en = elements();
		while (en.hasMoreElements())
			newList.add(en.nextElement());
		LinkedList<Object> added = new LinkedList<Object>();
		added.addAll(newList);
		added.removeAll(last_objects);	
		LinkedList<Object> removed = new LinkedList<Object>();
		removed.addAll(last_objects);
		removed.removeAll(newList);
		for(Object o : removed)
			fireObjectRemoved(o);
		for(Object o : added)
			fireObjectAdded(o);
	}


	public void intervalAdded(ListDataEvent e) {
		for ( int i=e.getIndex0(); i<=e.getIndex1(); ++i ) {
			fireObjectAdded(getElementAt(i));
		}
	}


	public void intervalRemoved(ListDataEvent e) {
		for ( int i=e.getIndex0(); i<=e.getIndex1(); ++i ) {
			fireObjectRemoved(last_objects.get(i));
		}
	}    
}
