package mayday.mushell.dispatch;

import javax.swing.SwingUtilities;

import mayday.core.EventFirer;
import mayday.mushell.OutputField;

public abstract class AbstractDispatcher implements Dispatcher {

	protected EventFirer<DispatchEvent, DispatchListener> eventfirer = new EventFirer<DispatchEvent, DispatchListener>() {
		protected void dispatchEvent(DispatchEvent event, DispatchListener listener) {
			listener.dispatching(event);
		}
	};
	
	private boolean isReady = true;
	protected OutputField outputField;
	
	public void connect(OutputField of) {
		outputField = of;
	}
	
	public final Boolean dispatchCommand(String command) {
		fireEvent(new DispatchEvent(this, DispatchEvent.event.BEFORE_DISPATCH, command, null));
		Boolean ret = dispatchCommandImpl(command);
		fireEvent(new DispatchEvent(this, DispatchEvent.event.AFTER_DISPATCH, command, ret));
		return ret;
	}
	
	public void addDispatchListener(DispatchListener l) {
		eventfirer.addListener(l);
	}
	
	public void removeDispatchListener(DispatchListener l) {
		eventfirer.removeListener(l);
	}
	
	public abstract Boolean dispatchCommandImpl( String Command );

	protected void setReady( boolean ready ) {
		DispatchEvent.event readyChange = null;
		if (isReady && !ready) 
			readyChange = DispatchEvent.event.NOW_BUSY;
		if (!isReady && ready)
			readyChange = DispatchEvent.event.NOW_READY;		
		isReady = ready;
		if (readyChange!=null)
			fireEvent(new DispatchEvent(this, readyChange, null, null));
	}
	
	public final boolean ready() {
		return isReady;
	}
	
	public synchronized void print(String s) {
		if (outputField!=null)
			outputField.print(s);
	}
	
	public void println(String s) {
		print(s);
		print("\n");
	}
	
	public void print(Object o) {
		print(o.toString());
	}
	
	protected void fireEvent(final DispatchEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				eventfirer.fireEvent(e);
			}
		});
	}
}
