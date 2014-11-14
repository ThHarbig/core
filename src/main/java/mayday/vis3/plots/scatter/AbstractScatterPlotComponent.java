package mayday.vis3.plots.scatter;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.DataSeries;

/* No mouse/key listeners here, because we don't support selection in this plot
 */

@SuppressWarnings("serial")
public abstract class AbstractScatterPlotComponent extends ChartComponent 
  implements ViewModelListener, ProbeListListener {
	
	protected DataSeries selectionLayer;
	protected DataSeries[] Layers;
	
	public AbstractScatterPlotComponent() {}
	
	public abstract int getNumberOfComponents();

	public abstract DataSeries getPlotComponent(int i);
	
	public void createView() {
		
		// iterate from below for optimal plotting		
		int noC = getNumberOfComponents();
		
		Layers = new DataSeries[noC];
		int h=0;
		
		for(int i = 0; i!=noC; ++i) {
			DataSeries ds = getPlotComponent(i);		
			Layers[h++] = ds;
			addDataSeries(ds);
		}		
		select(Color.RED);
	}
		
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		plotContainer.setPreferredTitle(getPreferredTitle(), this);
		viewModel.addViewModelListener(this);
	}
	
	public abstract String getPreferredTitle();
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		viewModel.removeViewModelListener(this);
	}

	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			select(Color.RED);
			break;
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED: // fallthrough 
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED:
			updatePlot();
			break;			
		}
	}

	public void probeListChanged(ProbeListEvent event) {
		switch (event.getChange()) {
		case ProbeListEvent.LAYOUT_CHANGE:
			repaint();
			break;
		case ProbeListEvent.CONTENT_CHANGE:
			updatePlot();
			break;
		}
	}
	
	public abstract DataSeries doSelect(Collection<Probe> probes);	
	
	public void select(Color selection_color)
	{
		if (selectionLayer!=null)
			removeDataSeries(selectionLayer);
		
		Set<Probe> s = new HashSet<Probe>();
				
		for(ProbeList probe_list : viewModel.getProbeLists(true))
			s.addAll(probe_list.getAllProbes());
		
		s.retainAll(viewModel.getSelectedProbes());
		
		selectionLayer = doSelect(s);
		if (selectionLayer!=null) {
			selectionLayer.setColor(Color.RED);
			addDataSeries(selectionLayer);
		}
		clearBuffer();
		repaint();
	}
		
	
}

