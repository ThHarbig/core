package mayday.vis3.plots.scatter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.vis3.ColorProvider;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.vis2base.DataSeries;
import wsi.ra.chart2d.GraphicsModifier;

/* No mouse/key listeners here, because we don't support selection in this plot
 */

@SuppressWarnings("serial")
public abstract class AbstractProbeListScatterPlotComponent extends AbstractScatterPlotComponent implements ViewModelListener, ProbeListListener {
	
	protected ProbeColoring probeColorSetter;
	protected ColorProvider coloring;
	
	public abstract DataSeries viewProbes(Collection<Probe> probes);
	
	public List<ProbeList> getProbeLists() {
		return viewModel.getProbeLists(true);
	}
	
	public void createView() {
	
		List<ProbeList> pls = getProbeLists();
		
		Layers = new DataSeries[pls.size()];
		int h=0;
		
		for(int i = pls.size(); i!=0; --i) {
			ProbeList pl = pls.get(i-1);
			DataSeries ds = viewProbes(pl.getAllProbes());		
			ds.setColor(pl.getColor());
			ds.setAfterJumpModifier(probeColorSetter);
			Layers[h++] = ds;
			addDataSeries(ds);
		}		select(Color.RED);
	}
		
	public void setup(PlotContainer plotContainer) {
		
		if (firstTime) {
			viewModel = plotContainer.getViewModel();
			coloring = new ColorProvider(viewModel);
			probeColorSetter = new ProbeColoring();
			coloring.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					GraphicsModifier gm = null;
					if (coloring.getColoringMode()!=ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST)
						gm = probeColorSetter;
					for (DataSeries ds : Layers)
						ds.setAfterJumpModifier(gm);
					clearBuffer();  // remove antialiased image
					repaint(); // redraw plot with new coloring 
				}
				
			});
		}

		plotContainer.addViewSetting(coloring.getSetting(), this);
		super.setup(plotContainer);
		
		firstTime = false;
	}

	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			select(Color.RED);
			break;
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED: // fallthrough
		case ViewModelEvent.DATA_MANIPULATION_CHANGED: 
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
	
	protected class IterableExperiment implements Iterable<Double>, Iterator<Double> {

		int experiment;
		Iterator<Probe> pb;
		
		public IterableExperiment(ProbeList pl, int experiment) {
			this.experiment=experiment;
			pb = pl.getAllProbes().iterator();
		}
		
		public Iterator<Double> iterator() {
			return this;
		}

		public boolean hasNext() {
			return pb.hasNext();
		}

		public Double next() {
			return pb.next().getValue(experiment);
		}

		public void remove() {
		}
		
	}
	

	private class ProbeColoring implements GraphicsModifier {
		public void modify(Graphics2D g, Object o) {
			if (o instanceof Probe)
				g.setColor(coloring.getColor((Probe)o));
		}		
	}
	
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
	
	public void removeNotify() {
		if (coloring!=null)
			coloring.removeNotify();
		super.removeNotify();
	}
		
	
}

