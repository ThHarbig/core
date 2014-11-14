package mayday.vis3.graph.model;

import java.util.ArrayList;
import java.util.List;

import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.listener.CanvasComponentListener;

public class SelectionModel implements CanvasComponentListener
{
	private List<CanvasComponent> selectedComponents;
	protected GraphModel model;
	private List<GraphModelSelectionListener> listeners;
	
	public SelectionModel(GraphModel model)
	{
		this.model=model;
		selectedComponents=new ArrayList<CanvasComponent>();
		listeners=new ArrayList<GraphModelSelectionListener>();
	}
	
	public void addSelectionListener(GraphModelSelectionListener listener)
	{
		listeners.add(listener);
	}
	
	protected void fireSelectionChanged()
	{
		for(GraphModelSelectionListener gmsl:listeners)
			gmsl.selectionChanged();
	}
	
	public void componentSelectionChanged(CanvasComponent comp) 
	{
		if(comp.isSelected()) 
			selectedComponents.add(comp);
		else 
			selectedComponents.remove(comp);		
		fireSelectionChanged();
	}
	
	public void componentMoved(CanvasComponent sender, int dx, int dy) {} // do nothing
		
	/**
	 * @param comp
	 * @return true, if the component is selected
	 */
	public boolean isSelected(CanvasComponent comp)
	{
		return selectedComponents.contains(comp);
	}
	
	/**
	 * @return the selectedComponents
	 */
	public List<CanvasComponent> getSelectedComponents() 
	{
		return selectedComponents;
	}
	
	public int numberOfSelectedComponents()
	{
		return selectedComponents.size();
	}

	/**
	 * @param selectedComponents the selectedComponents to set
	 */
	public void setSelectedComponents(List<CanvasComponent> selectedComponents) 
	{
		this.selectedComponents = selectedComponents;
	}
	
	/**
	 * Toggles component selection
	 * @param comp
	 */
	public void toggleComponentSelection(CanvasComponent comp)
	{
		if(isSelected(comp))
		{
			unselect(comp);
		}else
		{
			select(comp);
		}	
		fireSelectionChanged();
	}
	
	/**
	 * Select the component
	 * @param comp
	 */
	public void select(CanvasComponent comp)
	{
		selectedComponents.add(comp);
		comp.setSelected(true);	
		comp.repaint();
		fireSelectionChanged();
	}
	
	/**
	 * Select the component without notifying the listeners
	 * @param comp
	 */
	public void selectSilent(CanvasComponent comp)
	{
		selectedComponents.add(comp);
		comp.setSelected(true);	
		comp.repaint();
//		fireSelectionChanged();
	}
	
	/**
	 * Unselect the component
	 * @param comp
	 */
	public void unselect(CanvasComponent comp)
	{
		selectedComponents.remove(comp);
		comp.setSelected(false);	
		fireSelectionChanged();
	}
	
	public void clearSelection()
	{
		for(CanvasComponent c:selectedComponents)
		{
			c.setSelected(false);
		}
		selectedComponents.clear();	
		fireSelectionChanged();
	}
	
	public void selectAll()
	{
		selectedComponents.clear();
		for(CanvasComponent c:model.getComponents())
		{
			c.setSelected(true);
			selectedComponents.add(c);
		}
		fireSelectionChanged();
	}
	
	public void hideSelected()
	{
		for(CanvasComponent comp:model.getComponents())
		{
			if(comp.isSelected())
			{
				comp.setVisible(false);
			}
		}
	}
	
	public void showOnlySelected()
	{
		for(CanvasComponent comp:model.getComponents())
		{
			if(!comp.isSelected())
			{
				comp.setVisible(false);
			}
		}
	}

	/**
	 * @return the model
	 */
	public GraphModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(GraphModel model) {
		clearSelection();
		this.model = model;
	}

	@Override
	public void componentMoveFinished() {} // do nothing.

	public void removeSelectionListener(GraphModelSelectionListener listener) {
		listeners.add(listener);
	}
	
	
	
}
