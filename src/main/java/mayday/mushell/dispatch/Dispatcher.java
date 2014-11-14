package mayday.mushell.dispatch;

import mayday.mushell.OutputField;

public interface Dispatcher {
	
	/** Dispatch a given command with the respective shell interpreter
	 * @param command what to do
	 * @return true if dispatching was successfull, false if the command produced an error, null if this is unknown
	 */
	public Boolean dispatchCommand( String command );

	public String getName();
	
	public boolean ready();
	
	public void addDispatchListener(DispatchListener l);
	
	public void removeDispatchListener(DispatchListener l);
	
	public void connect(OutputField outputField);
}
