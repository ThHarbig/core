package mayday.vis3.plots.genomeviz.genomeoverview.controllercollection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DataSetted {

	protected EventListenerList eventListenerList = new EventListenerList();
	
	public DataSetted(){
		
	}
	
	public void dataSetted(){
		this.fireChanged();
	}
	
	public void addChangeListener(ChangeListener cl) {
		eventListenerList.add(ChangeListener.class, cl);		
	}
	
	public void removeChangeListener(ChangeListener cl) {
		eventListenerList.remove(ChangeListener.class, cl);
	}
	
	public void fireChanged() {
		
		Object[] l_listeners = this.eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;
		
		ChangeEvent event = new ChangeEvent(this);

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ChangeListener.class )  {
				ChangeListener list = ((ChangeListener)l_listeners[i+1]);
				list.stateChanged(event);
			}
		}
	}
}
