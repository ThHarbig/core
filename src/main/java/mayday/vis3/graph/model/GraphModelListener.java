package mayday.vis3.graph.model;

import mayday.vis3.graph.GraphCanvas;

/**
 * Interface for all graph model listeners. 
 * @see GraphCanvas
 * @author Stephan Symons
 *
 */
public interface GraphModelListener 
{
	/**
	 * Invoked if the GraphModel has changed.
	 * @param event
	 */
	public void graphModelChanged(GraphModelEvent event);
}
