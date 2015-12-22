package mayday.vis3.plots.boxplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.ColorProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.Shape;
import wsi.ra.chart2d.GraphicsModifier;

/* No mouse/key listeners here, because we don't support selection in this plot
 */

@SuppressWarnings("serial")
public class BoxPlotComponent extends ChartComponent implements ViewModelListener, ProbeListListener {
	
	protected DataSeries selectionLayer;

	protected RestrictedStringSetting probelisthandling;
	protected final static String PL_GROUP_LISTS ="Group by probelist";
	protected final static String PL_GROUP_EXPERIMENTS ="Group by experiment";
	protected final static String PL_OVERLAY_BOXES="Overlay boxes";
	
	public BoxPlotComponent() {
		setName("Box Plot");
		setGrid(0.0, 0.2);
		setGridEmphasize(0.0, 1.0);
		probelisthandling = new RestrictedStringSetting("Arrangement", null, 0, 
				PL_GROUP_LISTS, PL_GROUP_EXPERIMENTS, PL_OVERLAY_BOXES);
		probelisthandling.addChangeListener(new SettingChangeListener() {

			public void stateChanged(SettingChangeEvent e) {
				updatePlot();
			}
			
		});
	}
	
	public class BoxShape extends Shape
	{
		double[] quartiles;
		Color c;
		ProbeList pl;
		
		public BoxShape(double[] quartiles, ProbeList pl)
		{
			this.quartiles = quartiles;
			this.pl = pl;
		}
		
		public void paint(Graphics2D g)
		{
			double[] ypos = new double[5];
			for (int i=0; i!=quartiles.length; ++i) 
				ypos[i]=(quartiles[i]-quartiles[0]); //dist to min
			
			double width = .2;
			
			g.setColor(pl.getColor());
			
			Line2D.Double line = new Line2D.Double();
			Rectangle2D.Double rect = new Rectangle2D.Double();
			
			// min-max line
			Graphics2D g2 = (Graphics2D)g;
			
			line.setLine(-width, ypos[4], width, ypos[4]); //top whisker
			g2.draw(line);
			line.setLine(0, ypos[4], 0, ypos[0]); // vertical line
			g2.draw(line);
			
			line.setLine(-width, ypos[0], width, ypos[0]); // bottom whisker
			g2.draw(line);
			
			// the box
			g.setColor(Color.WHITE);			
			rect.setRect(-width, ypos[1], 2*width, ypos[3]-ypos[1]);
			g2.fill(rect);
			g.setColor(pl.getColor());
			rect.setRect(-width, ypos[1], 2*width, ypos[3]-ypos[1]);
			g2.draw(rect);
			
			// the median
			line.setLine(-width, ypos[2], width, ypos[2]);
			g2.draw(line);
			
		}
	}
	
	
	public void createView() 
	{

		double maxY = 0;
		double minX=Double.POSITIVE_INFINITY, maxX=Double.NEGATIVE_INFINITY;		

		int h=0;
		List<ProbeList> pls = viewModel.getProbeLists(false);
		int probelists = pls.size();
		int experiments = viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		DataSeries series_arr[] = new DataSeries[probelists*experiments];
		
		for(int iPL=0; iPL!=pls.size(); ++iPL) {
			
			ProbeList pl = pls.get(iPL);
			ProbeList.Statistics plStat = viewModel.getStatistics(pl.getAllProbes()); 
			// create quartiles for this probe list
			
			for(int iEX=0; iEX < experiments; iEX++) {
				
				int x = h++;
				double plotx = getPosition(iPL, iEX);
				
				minX = Math.min(plotx, minX);
				maxX = Math.max(plotx, maxX);
				
				series_arr[x] = new DataSeries();				
				
				double[] quartiles = new double[5];
				
				if (pl.getNumberOfProbes()>0) {
					quartiles[4] = viewModel.getMaximum(iEX, pl.getAllProbes());
					quartiles[3] = plStat.getQ1().getValues()[iEX];
					quartiles[2] = plStat.getMedian().getValues()[iEX];
					quartiles[1] = plStat.getQ3().getValues()[iEX];
					quartiles[0] = viewModel.getMinimum(iEX, pl.getAllProbes());					
				}
				
				if (Double.isNaN(quartiles[0]) || Double.isNaN(quartiles[1]) || Double.isNaN(quartiles[2]) ||
						Double.isNaN(quartiles[3]) || Double.isNaN(quartiles[4])) {
					// nothing to paint
				} else {				
					series_arr[x].setShape(new BoxShape(quartiles, pl));	
					series_arr[x].addPoint(plotx, quartiles[0], null);
					series_arr[x].setConnected(true);
					addDataSeries(series_arr[x]);
					maxY = Math.max(maxY, quartiles[4]);
				}								
			}
			
		}
		
		// draw these invisible points to get a good grid
		DataSeries beauty = new DataSeries();
		beauty.addPoint(minX-.5, maxY+.1, null);
		beauty.addPoint(maxX+.5, maxY+.1, null);
		beauty.setColor(Color.WHITE);
		beauty.setConnected(true);			
		addDataSeries(beauty);
		
		setScalingUnitX(1.0);
		select(Color.RED);
				
		setXLabeling(makeLabelMap());
	}
	
	protected int getPosition(int probelistindex, int experimentindex) {
		switch (probelisthandling.getSelectedIndex()) {
		case 0: // group by probelist
			return experimentindex + probelistindex*viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		case 1: // group by experiment
			return experimentindex*viewModel.getProbeLists(true).size() + probelistindex;
		case 2: // overlay
			return experimentindex;
		}
		throw new RuntimeException("Undefined ordering method");
	}
	
	@SuppressWarnings("unused")
	protected Map<Double, String> makeLabelMap() {
		HashMap<Double,String> exLabels = new HashMap<Double, String>();
		int pos=0;
		
		switch (probelisthandling.getSelectedIndex()) {
		case 0: // group by probelist
			for (ProbeList pl : viewModel.getProbeLists(false))
				for (Experiment e : viewModel.getDataSet().getMasterTable().getExperiments())
					exLabels.put((double)pos++, e.getName());
			break;
		case 1: // group by experiment
			for (Experiment e : viewModel.getDataSet().getMasterTable().getExperiments()) {
				exLabels.put((double)pos++, e.getName());
				for (ProbeList pl : viewModel.getProbeLists(false))
					exLabels.put((double)pos++, "");
				pos--;
			}
			break;
		case 2: // overlay
			for (Experiment e : viewModel.getDataSet().getMasterTable().getExperiments())
				exLabels.put((double)pos++, e.getName());
			break;
		default : 
			throw new RuntimeException("Undefined ordering method");
		}
		return exLabels;
	}
	
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		plotContainer.setPreferredTitle("Boxplot", this);
		viewModel.addViewModelListener(this);
		viewModel.addRefreshingListenerToAllProbeLists(this,false);
		coloring = new ColorProvider(viewModel);
		
		plotContainer.addViewSetting(probelisthandling, this);
		
		useExperimentNamesAsXLabels();	
		
		updatePlot();
	}

	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			select(Color.red);
			break;
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED: // fallthrough
		case ViewModelEvent.DATA_MANIPULATION_CHANGED:
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED:
		case ViewModelEvent.TOTAL_PROBES_CHANGED:
			updatePlot();
			break;			
		}
	}

	public void probeListChanged(ProbeListEvent event) {
		switch (event.getChange()) {
		case ProbeListEvent.LAYOUT_CHANGE: //fall
		case ProbeListEvent.CONTENT_CHANGE:
			updatePlot();
			break;
		}
	}
	
	public void removeNotify() {
		super.removeNotify();
		if (coloring!=null)
			coloring.removeNotify();
		viewModel.removeViewModelListener(this);
		viewModel.removeRefreshingListenerToAllProbeLists(this);
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
	
	
	// Added profiles
	
	protected ColorProvider coloring;
	
	protected DataSeries viewProfile(Collection<Probe> pl) {
		DataSeries series = new DataSeries();
		series.setShape(new Shape() {
			public void paint(Graphics2D g) {				
				g.fillRect(-1,-1,3,3);
			}
			public boolean wantDeviceCoordinates() {
				return true;
			}
		});			

		List<ProbeList> pls = viewModel.getProbeLists(false);

		for(Probe p : pl) {			

			for (int iPL=0; iPL!=pls.size(); ++iPL) {
				ProbeList plist = pls.get(iPL);
				
				if (plist.contains(p)) {
					double[] vals = viewModel.getProbeValues(p);
					
					for(int iEX=0; iEX < p.getNumberOfExperiments(); iEX++) {
						
						double plotx = getPosition(iPL, iEX);
						
						Double v = vals[iEX];
						if (!Double.isNaN(v))
							series.addPoint(plotx, v, p);
						else
							series.jump();
					}
					series.jump();					
				}

			}
		}
		series.setConnected(true);
		return series;
	}
	
	private class ProbeColorSetter implements GraphicsModifier {
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
		
		selectionLayer = viewProfile(s);
		
		selectionLayer.setAfterJumpModifier(new ProbeColorSetter());
		selectionLayer.setStroke(new BasicStroke(3));
		addDataSeries(selectionLayer);
		clearBuffer();
		repaint();
	}

	
	@Override
	public String getAutoTitleY(String ytitle) {
		String manip = viewModel.getDataManipulator().getManipulation().getDataDescription();
		if (manip.length()>0)
			manip = ", "+manip;
		return "Expression value"+manip;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		return "Experiment";
	}
	
	
}

