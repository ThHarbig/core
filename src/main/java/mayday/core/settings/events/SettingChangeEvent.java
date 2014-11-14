package mayday.core.settings.events;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;

/** Settings fire events when they are changed. HierarchicalSettings pass the events of 
 * their children settings on to their own listeners, but they add themselves to the 
 * additional sources of the event.  
 * @author battke
 */
@SuppressWarnings("serial")
public class SettingChangeEvent extends ChangeEvent {

	protected LinkedList<Object> additionalSources;
	
	public SettingChangeEvent(Object source) {
		super(source);
	}
	
	/** adds another higher-level event source. */ 
	public void addSource(Object source) {
		if (additionalSources==null)
			additionalSources = new LinkedList<Object>();
		additionalSources.add(source);
	}
	
	public List<Object> getAdditionalSources() {
		if (additionalSources==null)
			return Collections.emptyList();
		return Collections.unmodifiableList(additionalSources);
	}
	
	public boolean hasSource(Object source) {
		return this.source==source || getAdditionalSources().contains(source);
	}
	
}
