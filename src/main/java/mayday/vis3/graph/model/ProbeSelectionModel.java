package mayday.vis3.graph.model;

import java.util.List;

import mayday.core.Probe;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

public class ProbeSelectionModel extends SelectionModel implements ViewModelListener
{
	private ViewModel viewModel;

	private boolean busy=false;

	public ProbeSelectionModel(GraphModel model) 
	{
		super(model);		
	}

	public ProbeSelectionModel(GraphModel model, ViewModel viewModel)
	{
		super(model);
		this.viewModel=viewModel;	
		this.viewModel.addViewModelListener(this);
	}

	public void componentSelectionChanged(CanvasComponent comp) 
	{
		super.componentSelectionChanged(comp);
		busy=true;
		if(! (comp instanceof MultiProbeComponent) )
			return;
		if(comp.isSelected())
		{
			for(Probe p:((MultiProbeComponent)comp).getProbes() )
				viewModel.selectProbe(p);
		}else
		{
			for(Probe p:((MultiProbeComponent)comp).getProbes() )
				viewModel.unselectProbe(p);
		}
		busy=false;
	}

	/**
	 * @param selectedComponents the selectedComponents to set
	 */
	public void setSelectedComponents(List<CanvasComponent> selectedComponents) 
	{
		super.setSelectedComponents(selectedComponents);
		for(CanvasComponent comp:selectedComponents)
		{
			for(Probe p:((MultiProbeComponent)comp).getProbes())
			{
				viewModel.selectProbe(p);
			}
		}
	}

	/**
	 * Toggles component selection
	 * @param comp
	 */
	public void toggleComponentSelection(CanvasComponent comp)
	{
		super.toggleComponentSelection(comp);	
		busy=true;
		if(isSelected(comp))
		{			
			for(Probe p:((MultiProbeComponent)comp).getProbes() )
				viewModel.unselectProbe(p);
		}		
		else
		{
			for(Probe p:((MultiProbeComponent)comp).getProbes() )
				viewModel.selectProbe(p);	
		}
		busy=false;
	}

	/**
	 * Select the component
	 * @param comp
	 */
	public void select(CanvasComponent comp)
	{
		if(comp.isSelected()) return;
		super.select(comp); 
		if(comp instanceof MultiProbeComponent)
			for(Probe p:((MultiProbeComponent)comp).getProbes() )
				viewModel.selectProbe(p);
	}
	
	/**
	 * Unselect the component
	 * @param comp
	 */
	public void unselect(CanvasComponent comp)
	{
		super.unselect(comp);
		for(Probe p:((MultiProbeComponent)comp).getProbes() )
			viewModel.unselectProbe(p);
		
	}

	public synchronized void clearSelection()
	{
		busy=true;
		for(CanvasComponent comp:getSelectedComponents())
		{
			if(! (comp instanceof MultiProbeComponent)) continue;
			for(Probe p:((MultiProbeComponent)comp).getProbes() )
				viewModel.unselectProbe(p);			
		}
		busy=false;
		super.clearSelection();
	}

	public void clearSelectionSilent()
	{
		super.clearSelection();
	}

	public synchronized void selectAll()
	{
		super.selectAll();	
		busy=true;
		for(CanvasComponent comp:getSelectedComponents())
		{
			for(Probe p:((MultiProbeComponent)comp).getProbes() )
				viewModel.selectProbe(p);
		}
		busy=false;
	}

	public void viewModelChanged(ViewModelEvent vme)
	{
		if(busy) return;
		if(vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED)
		{	
			clearSelectionSilent();
			if(model instanceof ProbeGraphModel)
			{
				for(Probe p:viewModel.getSelectedProbes())
				{
					for(CanvasComponent cc:((ProbeGraphModel)model).getComponents(p))
						super.select(cc);
				}
			}		
		}		
	}






}
