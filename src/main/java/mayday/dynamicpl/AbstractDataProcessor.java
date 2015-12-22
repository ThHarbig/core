package mayday.dynamicpl;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;

/* Derived classes can implement either of OptionPanelProvider and/or StorageNodeStorable */

@SuppressWarnings("unchecked")
public abstract class AbstractDataProcessor<InType,OutType> 
	extends AbstractPlugin 
	implements ProbeFilter {

	public final static String MC = "Dynamic Probe Lists/Data Processor";
	
	private DynamicProbeList dynamicProbeList;
	protected AbstractDataProcessor<OutType,?> nextInChain; 
	private EventListenerList eventListenerList = new EventListenerList();
	
	public final boolean isAcceptableSource(AbstractDataProcessor adp) {
		if (adp.getDataClass()==null) return false;
		return isAcceptableInput(adp.getDataClass());
	}
	
	public abstract boolean isAcceptableInput(Class<?>[] inputClass);
	
	public void linkTarget(AbstractDataProcessor<OutType,?> adp) {
		nextInChain = adp;
	}
	
	protected abstract OutType convert(InType value);
	
	public Boolean passesFilter(Probe pb) {
		return processChain((InType)pb); // should only be called if intype is probe
	}
	
	/* The chain is processed top-down: The first processor converts the data,
	 * calls the second and obtains the boolean pass-value from the second 
	 * (who gets it from the third and so on), i.e. (converted) values are passed 
	 * DOWN the chain and the result is passed UP.
	 * This allows for some otherwise tricky operations like collection unpacking.   
	 */
	
	public Boolean processChain(InType value) {
		OutType converted = convert(value);
		
		if (converted==null)
			return null;
		
		if (nextInChain==null)
			if (converted instanceof Boolean)
				return (Boolean)converted;
			else
				return null;
		else
			return nextInChain.processChain(converted);
	}
	
	public abstract Class<?>[] getDataClass();
	
	public void init() {}

	public abstract String toString();

	public void addChangeListener( ChangeListener listener ) {
		eventListenerList.add( ChangeListener.class, listener );
	}


	public void removeChangeListener( ChangeListener listener ) {
		eventListenerList.remove( ChangeListener.class, listener );
	}

	public void fireChanged(  )
	{
		Object[] l_listeners = this.eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;

		ChangeEvent ce = new ChangeEvent(this);

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ChangeListener.class )  {
				ChangeListener list = ((ChangeListener)l_listeners[i+1]);
				list.stateChanged(ce);
			}
		}
	}
	
	public void setDynamicProbeList(DynamicProbeList dynamicProbeList) {
		this.dynamicProbeList = dynamicProbeList;
	}

	public DynamicProbeList getDynamicProbeList() {
		return dynamicProbeList;
	}
		
	public String toDescription() {
		return toString();
	}
	
	public void dispose() {
		// REMOVE listeners in subclasses that use listeners
	}
}
