package mayday.vis3.plots.bars;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.vis3.ColorProvider;
import mayday.vis3.SortedProbeList;
import mayday.vis3.ValueProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.vis2base.DataSeries;

@SuppressWarnings("serial")
public class BarPlotComponent extends AbstractBarPlotComponent {

	protected ColorProvider coloring;
	protected ValueProvider V;
	protected SortedProbeList probes;
	double max=Double.NEGATIVE_INFINITY;
	double min=Double.POSITIVE_INFINITY;
	protected HashSet<Probe> selection = new HashSet<Probe>();

	HashMap<Probe, BarShape> bars = new HashMap<Probe, BarShape>();
	
	@Override
	public String getPreferredTitle() {
		return "Bar Plot";
	}
	
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				setXLabeling();
				updatePlot();
			}
		};

		V = initValueProvider();
		V.addChangeListener(cl);
		
		plotContainer.addViewSetting(V.getSetting(), this);
		
		coloring = new ColorProvider(viewModel);
		plotContainer.addViewSetting(coloring.getSetting(), this);
		coloring.addChangeListener(cl);
		
		probes = new SortedProbeList(viewModel, null);
		probes.addChangeListener(cl);
		updateProbes();
		plotContainer.addViewSetting(probes.getSetting(), this);

		
		setYLabeling(null);
		V.setProvider(V.new ExperimentProvider(0));
		
	}
	
	@Override
	protected void processMouseClick(int barIndex, MouseEvent evt) {
		if (barIndex<probes.size() && barIndex>=0) {
			Probe pb = probes.get(barIndex);
			if (evt.getClickCount()==2) {
				PropertiesDialogFactory.createDialog(pb).setVisible(true);
				return;
			}
		}

		super.processMouseClick(barIndex, evt);
	}

	
	@Override
	protected void selectBar(int barIndex, Boolean select) {
		if (barIndex<probes.size() && barIndex>=0) {
			Probe pb = probes.get(barIndex);
			if (select==null)
				viewModel.toggleProbeSelected(pb);
			else if (select)
				viewModel.selectProbe(pb);
			else
				viewModel.unselectProbe(pb);			
		}
	}
	
	protected void unselectAllBars() {
		viewModel.setProbeSelection(Collections.<Probe>emptySet());
	}

	
	protected void updateProbes() {
		probes.clear();
		probes.addAll(viewModel.getProbes());
		max = 0;
		min = 0;
		for (Probe pb : probes) {
			double d = V.getValue(pb);
			max = Math.max(max, d);
			min = Math.min(min, d);
		}
		
	}
	
	protected void setXLabeling() {
		HashMap<Double, String> xLabeling = new HashMap<Double, String>();
		for (int i=0; i!=probes.size(); ++i)
			xLabeling.put((double)i+1, probes.get(i).getDisplayName());
		setXLabeling(xLabeling);
	}
	
	protected ValueProvider initValueProvider() {
		return new ValueProvider(viewModel,"Values");
	}
	
	@Override
	public void select(Color selection_color)
	{
		// remove old selection
		if (selection!=null) {
			for (Probe pb : selection) {
				BarShape bs = bars.get(pb);
				if (bs!=null)
					bs.setColor(coloring.getColor(pb));
			}
		}
		
		HashSet<Probe> s = new HashSet<Probe>();
				
		for(ProbeList probe_list : viewModel.getProbeLists(true))
			s.addAll(probe_list.getAllProbes());
		
		s.retainAll(viewModel.getSelectedProbes());
		
		if (V==null)
			return;
				
		for (Probe pb : s) {
			BarShape bs = bars.get(pb);
			if (bs!=null)
				bs.setColor(selection_color);
		}
		
		selection = s;
		
		clearBuffer();
		repaint();
	}
	
	
	@Override
	public BarShape getBar(int i) {
		Probe pb = probes.get(i);
		BarShape bs = new BarShape(1, V.getValue(pb), coloring.getColor(pb)); 
		bars.put(pb, bs);		
		return bs;
	}

	@Override
	public int getNumberOfBars() {
		if (probes==null)
			return 0;
		return probes.size();
	}
	
	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			select(Color.RED);
			break;
		case ViewModelEvent.DATA_MANIPULATION_CHANGED: // fallthrough
			updatePlot();
			break;
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED: // fallthrough
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED:
			updateProbes();
			updatePlot();
			break;			
		}
	}

	@Override
	public DataSeries doSelect(Collection<Probe> probes) {
		// not called ever
		return null;
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		if (V!=null)
			return V.getSourceName();
		return ytitle;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		return "Probes";
	}	
	
}
