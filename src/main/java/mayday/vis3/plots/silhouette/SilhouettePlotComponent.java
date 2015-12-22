package mayday.vis3.plots.silhouette;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.DelayedUpdateTask;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.EuclideanDistance;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.ColorProvider;
import mayday.vis3.MapValueProvider;
import mayday.vis3.ValueProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.VolatileProbeList;
import mayday.vis3.plots.bars.AbstractBarPlotComponent;
import mayday.vis3.vis2base.DataSeries;

@SuppressWarnings("serial")
public class SilhouettePlotComponent extends AbstractBarPlotComponent implements SettingChangeListener {

	protected ColorProvider coloring;
	protected ValueProvider V;
	double max=1;
	double min=-1;
	protected HashSet<Probe> selection = new HashSet<Probe>();
	protected List<Probe> probes = new ArrayList<Probe>();
	protected DistanceMeasureSetting dms;
	protected boolean isComputing = false;

	
	protected DelayedUpdateTask silhouetteComputer = new DelayedUpdateTask("Silhouette updater",500) {
		protected void performUpdate() {
			updateProbes0();
		}
		
		protected boolean needsUpdating() {
			return true;
		}
	};

	HashMap<Probe, BarShape> bars = new HashMap<Probe, BarShape>();
	
	@Override
	public String getPreferredTitle() {
		return "Silhouette Plot";
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
		
		dms = new DistanceMeasureSetting("Distance Measure", null, new EuclideanDistance());
		plotContainer.addViewSetting(dms, this);
		dms.addChangeListener(this);
		
		coloring = new ColorProvider(viewModel);
		plotContainer.addViewSetting(coloring.getSetting(), this);
		coloring.addChangeListener(cl);
		
		chartSettings.getGrid().visible.setBooleanValue(false);
		
		updateProbes();
		
		setYLabeling(null);
		

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

	protected void unselectAllBars() {
		viewModel.setProbeSelection(Collections.<Probe>emptySet());
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

	@Override
	public void paint(Graphics _g) {		
		Graphics2D g = (Graphics2D)_g;
		g.setBackground(Color.white);
		g.clearRect(0, 0, getWidth(), getHeight());	
		if (isComputing) {
			g.drawString("Computing silhouette values...", 30, 30);
		} else {
			super.paint(g);
		}
	}
	
	protected void updateProbes() {
		// delegate time-consuming task for later
		isComputing = true;
		lastSelected = -1;
		silhouetteComputer.trigger();
	}
	
	protected void updateProbes0() {
		probes.clear();
		// compute the silhouette values
		final Map<Probe,Double> silhouetteValues = ((MapValueProvider)V).getValuesMap();
		final DistanceMeasurePlugin dm = dms.getInstance();
		
		AbstractTask at = new AbstractTask("Computing Silhouette") {
			protected void doWork() throws Exception {
				// add probes sorted by probelists
				List<ProbeList> probelists = viewModel.getProbeLists(true);
				
				int total = viewModel.getProbes().size();
				int current=0;
				
				for (ProbeList pl : probelists) {
					for (Probe pb : pl.getAllProbes()) {
						
						double[] v1 = viewModel.getProbeValues(pb);
						
						// compute all distances within this cluster
						double meandistA = 0;

						if (pl.getNumberOfProbes()>1) {

							for (Probe pb2 : pl.getAllProbes()) {
								if (pb2!=pb) {								
									double[] v2 = viewModel.getProbeValues(pb2);
									double dist = dm.getDistance(v1,v2);
									meandistA+=dist;
								}
							}
							meandistA/=(double)(pl.getNumberOfProbes()-1);
							
						}
						
						
						// compute all distances to other clusters
						double mindistB = Double.MAX_VALUE;
						for (ProbeList pl2 : probelists) {
							if (pl2 != pl) {
								double meandistD = 0;
								for (Probe pb2 : pl2.getAllProbes()) {
									double[] v2 = viewModel.getProbeValues(pb2);
									double dist = dm.getDistance(v1,v2);
									meandistD+=dist;
								}
								meandistD/=(double)(pl2.getNumberOfProbes());
								mindistB = mindistB<meandistD ? mindistB : meandistD;
							}
						}
						
						// Compute silhouette value
						double s = (mindistB - meandistA) / Math.max(mindistB, meandistA);
						silhouetteValues.put(pb, s);
						++current;
						setProgress((10000*current)/total);
					}
					
					// now sort the probes in this probelist according to their silhouette values, decreasingly
					List<Probe> tmp = new ArrayList<Probe>(pl.getAllProbes());
					Collections.sort(tmp, new Comparator<Probe>() {
						public int compare(Probe o1, Probe o2) {
							return silhouetteValues.get(o2).compareTo(silhouetteValues.get(o1));
						}
					});
					
					probes.addAll(tmp);
				}
			}

			protected void initialize() {}
		};
		at.start();
		at.waitFor();
		
		
		// update the valueprovider to update the plot
		((MapValueProvider)V).setValues(silhouetteValues);
		isComputing = false;
		updatePlot();
	}
	
	protected void setXLabeling() {
		int last=0;
		HashMap<Double, String> xLabeling = new HashMap<Double, String>();
		for (ProbeList pl: viewModel.getProbeLists(true)) {
			xLabeling.put((double)last+(pl.getNumberOfProbes()/2), (((VolatileProbeList)pl).getOriginalName()));
			last+=pl.getNumberOfProbes();
		}
		setXLabeling(xLabeling);
	}
	
	protected ValueProvider initValueProvider() {
		return new MapValueProvider("Silhouette Values");
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
	public void stateChanged(SettingChangeEvent e) {
		// new distance measure
		updateProbes();
		updatePlot();		
	}

	public String getAutoTitleY(String ytitle) {
		if (V!=null)
			return  V.getSourceName();
		return ytitle;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		return "Clusters";
	}	
	
}
