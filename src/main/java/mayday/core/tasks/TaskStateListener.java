/**
 * 
 */
package mayday.core.tasks;


public interface TaskStateListener extends java.util.EventListener {
	
	public int getStateMask();
	
	public void processEvent(TaskStateEvent evt);
}