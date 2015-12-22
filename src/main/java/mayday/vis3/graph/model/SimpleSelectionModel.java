package mayday.vis3.graph.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

public class SimpleSelectionModel extends SelectionModel implements ViewModelListener
{
	private boolean busy=false;
	private ViewModel viewModel;
	public SimpleSelectionModel(GraphModel model) 
	{
		super(model);		
	}
	
	public SimpleSelectionModel(GraphModel model, ViewModel viewModel)
	{
		super(model);
		this.viewModel=viewModel;	
		this.viewModel.addViewModelListener(this);
	}
	
	@Override
	public void componentSelectionChanged(CanvasComponent comp) 
	{
		super.componentSelectionChanged(comp);
		busy=true;
		if(! (comp instanceof MultiProbeComponent) )
			return;
		
		Set<Probe> selectedProbes=new HashSet<Probe>();
		for(CanvasComponent c:getSelectedComponents())
		{
			if(c instanceof MultiProbeComponent)
			{
				selectedProbes.addAll(((MultiProbeComponent) c).getProbes());
			}
		}
				
				
		if(comp.isSelected())
		{
			selectedProbes.addAll(((MultiProbeComponent)comp).getProbes());
		}else
		{
			selectedProbes.removeAll(((MultiProbeComponent)comp).getProbes());
		}
		viewModel.setProbeSelection(selectedProbes);
		busy=false;
	}
	
	public synchronized void clearSelection()
	{
		busy=true;
		viewModel.setProbeSelection(new HashSet<Probe>());
		busy=false;
		super.clearSelection();
	}
	
	/**
	 * @param selectedComponents the selectedComponents to set
	 */
	public void setSelectedComponents(List<CanvasComponent> selectedComponents) 
	{
		super.setSelectedComponents(selectedComponents);
		Set<Probe> probes =new HashSet<Probe>();
		for(CanvasComponent comp:selectedComponents)
		{
				probes.addAll(((MultiProbeComponent) comp).getProbes());			
		}
		viewModel.setProbeSelection(probes);
		fireSelectionChanged();
	}
	
	/**
	 * Select the component
	 * @param comp
	 */
	public void select(CanvasComponent comp)
	{
		super.select(comp); 		
		busy=true;
		if(! (comp instanceof MultiProbeComponent))
			return;		
		Set<Probe> selectedProbes=new HashSet<Probe>();
		for(CanvasComponent c:getSelectedComponents())
		{
			selectedProbes.addAll(((MultiProbeComponent) c).getProbes());
		}
		selectedProbes.addAll(((MultiProbeComponent)comp).getProbes());		
		viewModel.setProbeSelection(selectedProbes);
		busy=false;
	}
	
	/**
	 * Unselect the component
	 * @param comp
	 */
	public void unselect(CanvasComponent comp)
	{
		super.unselect(comp);
		busy=true;
		Set<Probe> selectedProbes=new HashSet<Probe>();
		for(CanvasComponent c:getSelectedComponents())
		{
			if(c instanceof MultiProbeComponent)
			{
				selectedProbes.addAll(((MultiProbeComponent) c).getProbes());
			}
		}
		selectedProbes.removeAll(((MultiProbeComponent)comp).getProbes());		
		viewModel.setProbeSelection(selectedProbes);
		busy=false;

	}
	
	public synchronized void selectAll()
	{
		super.selectAll();	
		busy=true;
		Set<Probe> selectedProbes=new HashSet<Probe>();
		for(CanvasComponent comp:getSelectedComponents())
		{
			selectedProbes.addAll(((MultiProbeComponent) comp).getProbes());
		}
		viewModel.setProbeSelection(selectedProbes);
		busy=false;
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(busy) return;
		if(vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED)
		{	
			for(CanvasComponent c:getSelectedComponents())
			{
				if(c==null) continue;
				c.setSelected(false);
			}
			getSelectedComponents().clear();		
			
			if(model instanceof ProbeGraphModel)
			{				
				for(Probe p:viewModel.getSelectedProbes())
				{
					if(((ProbeGraphModel)model).getComponents(p)!=null)
					{
						for(CanvasComponent cc:((ProbeGraphModel)model).getComponents(p) )
							super.select(cc);
					}
				}
			}else
			{
				for(Probe p:viewModel.getSelectedProbes())
				{
					for(CanvasComponent comp:model.getComponents())
					{
						if(!(comp instanceof MultiProbeComponent)) 
							continue;
						if( ((MultiProbeComponent)comp).getProbes().contains(p))
						{
							super.select(comp);
						}
					}
				}
			}
		}
	}
	
	
}
