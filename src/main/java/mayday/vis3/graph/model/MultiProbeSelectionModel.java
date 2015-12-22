package mayday.vis3.graph.model;

import java.util.ArrayList;
import java.util.List;

import mayday.core.Probe;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.listener.CanvasComponentListener;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

public class MultiProbeSelectionModel extends SelectionModel implements ViewModelListener, CanvasComponentListener
{
	private ViewModel viewModel;
	private static int instanceCounter=0;
	public MultiProbeSelectionModel(GraphModel model, ViewModel viewModel) 
	{
		super(model);
		instanceCounter++;
		this.viewModel=viewModel;
	}

	public void componentSelectionChanged(CanvasComponent comp2) 
	{
		
		super.componentSelectionChanged(comp2);
		MultiProbeComponent comp=(MultiProbeComponent)comp2;
		if(comp.isSelected())
		{
			for(Probe p:comp.getProbes())
			{
				viewModel.selectProbe(p);
			}
		}else
		{
			for(Probe p:comp.getProbes())
			{
				viewModel.unselectProbe(p);
			}
		}		
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
	public void toggleComponentSelection(MultiProbeComponent comp)
	{
		super.toggleComponentSelection(comp);		
		if(isSelected(comp))
		{			
			for(Probe p:comp.getProbes())
			{
				viewModel.selectProbe(p);
			}			
		}else
		{
			for(Probe p:comp.getProbes())
			{
				viewModel.unselectProbe(p);
			}	
		}		
	}
	
	/**
	 * Select the component
	 * @param comp
	 */
	public void select(MultiProbeComponent comp)
	{
		super.select(comp);
		for(Probe p:comp.getProbes())
		{
			viewModel.selectProbe(p);
		}
	}
	
	/**
	 * Unselect the component
	 * @param comp
	 */
	public void unselect(MultiProbeComponent comp)
	{
		super.unselect(comp);
		for(Probe p:comp.getProbes())
		{
			viewModel.unselectProbe(p);
		}
	}
	
	public void clearSelection()
	{
		for(CanvasComponent comp:new ArrayList<CanvasComponent>(getSelectedComponents()))
		{
			for(Probe p:((MultiProbeComponent)comp).getProbes())
			{
				viewModel.unselectProbe(p);
			}
		}	
		super.clearSelection();
	}
	
	public void selectAll()
	{
		super.selectAll();		
		for(CanvasComponent comp:getSelectedComponents())
		{
			for(Probe p:((MultiProbeComponent)comp).getProbes())
			{
				viewModel.selectProbe(p);
			}
		}
	}
	
	public void clearSelectionSilent()
	{
		super.clearSelection();
	}

	/**
	 * @return the viewModel
	 */
	public ViewModel getViewModel() {
		return viewModel;
	}

	/**
	 * @param viewModel the viewModel to set
	 */
	public void setViewModel(ViewModel viewModel) {
		this.viewModel = viewModel;
	}

	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED)
		{
			GraphWithProbeModel pmodel=(GraphWithProbeModel)model;
			clearSelectionSilent();
			for(Probe p:viewModel.getSelectedProbes())
			{
				for(CanvasComponent c:pmodel.getComponents(p))
				{
//					c.setSelected(true);
					super.select(c);
				}
			}
		}
	}
	
	
	
	

}
