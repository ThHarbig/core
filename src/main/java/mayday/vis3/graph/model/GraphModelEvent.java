package mayday.vis3.graph.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import mayday.vis3.graph.components.CanvasComponent;

/**
 * Event Class that is used to communicate events between GraphModels and GraphModelListeners
 * @author Stephan Symons
 */
public class GraphModelEvent 
{
	private GraphModelChange change;
	
	private Collection<CanvasComponent> affectedComponents;
	
	public GraphModelEvent(GraphModelChange change) 
	{
		this.change=change;
		affectedComponents=new HashSet<CanvasComponent>();
	}
	
	public GraphModelEvent(GraphModelChange change, Collection<CanvasComponent> affectedCanvasComponents) 
	{
		this.change=change;
		affectedComponents=affectedCanvasComponents;
	}
	
	public GraphModelEvent(GraphModelChange change, CanvasComponent affectedCanvasComponent) 
	{
		this.change=change;
		affectedComponents=new HashSet<CanvasComponent>();
		affectedComponents.add(affectedCanvasComponent);
	}
	
	/**
	 * @return the kind of event that occured. 
	 */
	public GraphModelChange getChange() {
		return change;
	}



	/**
	 * Set the kind of event that has occured. 
	 * @param change
	 */
	public void setChange(GraphModelChange change) {
		this.change = change;
	}



	/**
	 * The set of affected components. If change is AllComponentsChanged, this may be not defined.
	 * @return The set of affected components
	 */
	public Collection<CanvasComponent> getAffectedComponents() {
		return affectedComponents;
	}



	/**
	 * @param affectedComponents
	 */
	public void setAffectedComponents(Set<CanvasComponent> affectedComponents) {
		this.affectedComponents = affectedComponents;
	}



	/**
	 * This enum contains the different change events that can occur within 
	 * a graph model:
	 * <ul>
	 * <li>ComponentsAdded means that one or several components were added to the model </li>
	 * <li>ComponentsRemoved means that one or several components were removed from the model </li>
	 * <li>AllComponentsChanged means that the entire model changed </li>
	 * </ul>
	 * @author Stephan Symons
	 *
	 */
	public static enum GraphModelChange
	{
		ComponentsAdded,
		ComponentsRemoved,
		AllComponentsChanged,
		EdgeAdded,
		EdgeRemoved
	}
}
