package mayday.dynamicpl;

import javax.swing.event.ChangeListener;

import mayday.core.Probe;

public interface ProbeFilter {

	public Boolean passesFilter(Probe pb);
	
	public void addChangeListener( ChangeListener listener );
	public void removeChangeListener( ChangeListener listener );

	public String toDescription();
	
	// call this method to make sure that the filter removes all listeners added to other objects *//
	public void dispose();
}
