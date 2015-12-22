package mayday.vis3.plots.star;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.settings.Setting;
import mayday.vis3.ColorProvider;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.DelayedPlotUpdater;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.PointIterator;
import mayday.vis3.vis2base.Shape;
import wsi.ra.chart2d.DPoint;
import wsi.ra.chart2d.GraphicsModifier;

@SuppressWarnings("serial")
public class StarPlotComponent extends ChartComponent implements ViewModelListener, ProbeListListener {

	protected DataSeries[] Layers;

	// Selection
	protected DataSeries selectionLayer;
	protected Set<Probe> currentSelection = new HashSet<Probe>();

	// Members added for nice probe coloring
	protected ColorProvider coloring;
	protected ProbeColorSetter probeColorSetter = new ProbeColorSetter();

	protected StarPlotSetting settings;

	protected Rectangle selRect;
	
	protected double min, max, range;

	public StarPlotComponent() {
		farea.getBorder().setVisible(false);
		chartSettings.getGrid().getVisible().setBooleanValue(false);
	}

	public String getPreferredTitle() {
		return "Star Plot";
	}

	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);

		plotContainer.setPreferredTitle(getPreferredTitle(), this);

		if (coloring==null) { // don't loose old CP
			coloring = new ColorProvider(viewModel);
		} else {
			coloring.addNotify();
		}

		coloring.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				GraphicsModifier gm = null;
				if (coloring.getColoringMode()!=ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST)
					gm = probeColorSetter;
				for (DataSeries ds : Layers)
					if (ds!=null)
						ds.setAfterJumpModifier(gm);
				clearBuffer();  // remove antialiased image
				repaint(); // redraw plot with new coloring 
			}

		});

		if (settings==null) {
			settings = new StarPlotSetting(viewModel.getDataSet());
			settings.addSetting(coloring.getSetting());
			settings.addChangeListener(new DelayedPlotUpdater(this));
		}

		for (Setting s : settings.getChildren()) // add children without a subcategory
			plotContainer.addViewSetting(s, this);

		viewModel.addViewModelListener(this);
		viewModel.addRefreshingListenerToAllProbeLists(this, true);

		if (Layers==null) { // don't loose previous settings
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					switch (e.getButton()) {
					case MouseEvent.BUTTON1:
//						Probe pb = getClickedProbe(e.getPoint());
//						if (pb!=null) {
							if (e.getClickCount()==2) {
								PropertiesDialogFactory.createDialog(viewModel.getSelectedProbes().toArray(new Probe[0])).setVisible(true);
							} else {
								Rectangle r = new Rectangle(e.getX()-1, e.getY()-1, 3,3);
								int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
								selectByRectangle(r, (e.getModifiers()&CONTROLMASK) == CONTROLMASK, false);

//								if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
//									// 			toggle selection of the clicked probe
//									viewModel.toggleProbeSelected(pb);
//								} else {
//									// 			select only one probe
//									viewModel.setProbeSelection(pb);
//								}
							}
//						}
						break;
					case MouseEvent.BUTTON3:
						ProbeMenu pm = new ProbeMenu(viewModel.getSelectedProbes(), viewModel.getDataSet().getMasterTable());
						pm.getPopupMenu().show(StarPlotComponent.this, e.getX(), e.getY());
						break;
					}
				}
				public void mouseReleased(MouseEvent e) {
					if (selRect!=null) {
						//						System.out.println("Selecting profiles intersecting: "+selRect);
						Graphics2D g = ((Graphics2D)farea.getGraphics());
						drawSelectionRectangle(g);
						int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
						boolean control = ((e.getModifiers()&CONTROLMASK) == CONTROLMASK);
						boolean alt = e.isAltDown();
						selectByRectangle(selRect, control, alt);
						selRect = null;
					}
				}
			});

			farea.addMouseMotionListener(new MouseMotionListener() {

				protected Point dragPoint;
				protected Point targPoint;

				public void mouseDragged(MouseEvent e) {					
					Graphics2D g = ((Graphics2D)farea.getGraphics());
					if (selRect==null) {
						dragPoint = e.getPoint();
					} else {
						drawSelectionRectangle(g);
					}
					targPoint = e.getPoint();
					selRect = new Rectangle(dragPoint, new Dimension(1,1));
					selRect.add(targPoint);					
					drawSelectionRectangle(g);					
				}

				public void mouseMoved(MouseEvent e) {
				}
			});

			useExperimentNamesAsXLabels();
		}

		updatePlot();
	}

	protected void drawSelectionRectangle(Graphics2D g) {
		if (selRect==null)
			return;
		g.setXORMode(getBackground());
		g.setColor(Color.RED);
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.draw(selRect);
		g.setStroke(oldStroke);
		g.setPaintMode();
	}


	protected void selectByRectangle(Rectangle r, boolean control, boolean alt) {
		Set<Probe> newSelection = new HashSet<Probe>();
		double[] clicked1 = getPoint(r.x, r.y);
		double[] clicked2 = getPoint(r.x+r.width, r.y+r.height);
		Rectangle2D sel = new Rectangle2D.Double(clicked1[0], clicked2[1], clicked2[0]-clicked1[0], clicked1[1]-clicked2[1]);

		PointIterator<Probe> it = new PointIterator<Probe>(Layers);
		Double[] lastpoint = null;
		Line2D l = new Line2D.Double(0,0,0,0);

		while(it.hasNext()) {
			Double[] point = it.next();
			double px = point[0];
			double py = point[1];
			// fast evaluation of easy hits
			if (sel.contains(px, py))
				newSelection.add(it.getObject());
			else {
				// now check slopes 
			}
			double pslope = point[2];
			if (Double.isNaN(pslope))
				lastpoint=null;

			if (lastpoint!=null) {
				// construct a line now
				l.setLine(lastpoint[0], lastpoint[1], px, py);
				if (l.intersects(sel))
					newSelection.add(it.getObject());
			}

			lastpoint = point;
		}

		/*
		 * selection modes: 
		 * - no modifier: replace
		 * - ctrl: union with previous selection
		 * - alt: intersect with previous selection
		 * - ctrl+alt: remove from previous selection
		 */

		Set<Probe> previousSelection = viewModel.getSelectedProbes();
		if (control && alt) {
			previousSelection = new HashSet<Probe>(previousSelection);
			previousSelection.removeAll(newSelection);
			newSelection = previousSelection;
		} else if (control) {
			newSelection.addAll(previousSelection);
		} else if (alt) {
			newSelection.retainAll(previousSelection);
		} else {
			// nothing to do with prev selection
		}
		viewModel.setProbeSelection(newSelection);
	}

	protected DataSeries view(Collection<Probe> pl) {
		DataSeries series = new DataSeries();

		if (settings.getShowDots().getBooleanValue()) {
			series.setShape(new Shape() {
				public void paint(Graphics2D g) {				
					g.fillRect(-1,-1,3,3);
				}
				public boolean wantDeviceCoordinates() {
					return true;
				}
			});			
		}
		
		for(Probe p : pl) {			
			double[] probeValues;
			
			if (p.isImplicitProbe())
				probeValues = p.getValues();
			else 
				probeValues = viewModel.getProbeValues(p);
			
			for(int j=0; j < p.getNumberOfExperiments(); j++) {
				double alpha = ((double)j+.5) * 2.0 * Math.PI / (double)probeValues.length;
				double sinAlpha = Math.sin( alpha );
				double cosAlpha = Math.cos( alpha );
				
				double theValue = probeValues[j];
				
				if (Double.isNaN(theValue)) {
					if (!settings.getInferData().getBooleanValue())
						series.jump();
				} else {
					// scale value according to [min,max] into the range [0,1]
					theValue -= min;
					theValue /= range;

					double finalX = sinAlpha * theValue;
					double finalY = cosAlpha * theValue;
					series.addPoint(finalX, finalY, p);
				}
			}
			series.jump();	
		}
		series.setConnected(true);
		return series;
	}

	public List<ProbeList> getProbeLists() {
		return viewModel.getProbeLists(true);
	}

	public void createView()
	{
		if (viewModel==null)
			return;

		selRect = null;

		// Set distances for scaling (axis)
		setScalingUnitX(1.0);

		// compute minimum and maximum
		if (settings.getUseGlobalMax().getBooleanValue()) {
			Collection<Probe> allTheProbes = viewModel.getDataSet().getMasterTable().getProbes().values();
			min=viewModel.getDataManipulator().getMinimum(null, allTheProbes);
			max=viewModel.getDataManipulator().getMaximum(null, allTheProbes);
			range = max-min;
		} else {
			max = viewModel.getMaximum(null, null);
			min = viewModel.getMinimum(null, null);
			range = max-min;
		}
		
		// define plot range
		DataSeries pretty = new DataSeries();
		pretty.addDPoint(new DPoint(-1.1,-1.1));
		pretty.addDPoint(new DPoint(-1.1, 1.1));
		pretty.addDPoint(new DPoint( 1.1,-1.1));
		pretty.addDPoint(new DPoint( 1.1, 1.1));
		pretty.setColor(new Color(255,255,255,255));
		addDataSeries(pretty);

		// plot "grid"
		DataSeries axes = new DataSeries();
		axes.setColor(Color.gray);
		axes.setConnected(true);
		int noe = viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		// draw "rings"
		if (settings.getShowGuides().getBooleanValue()) {
			for (int k=1; k<=10; ++k) { // ring at zero is invisible anyhow
				for(int j=0; j < noe; j++) {
					double alpha = ((double)j+.5) * 2.0 * Math.PI / (double)noe;
					double sinAlpha = Math.sin( alpha );
					double cosAlpha = Math.cos( alpha );
					axes.addDPoint(sinAlpha*(double)k/10, cosAlpha*(double)k/10); // 10% axis overscale
				}
				axes.jump();
			}
		}
		if (settings.getShowAxes().getBooleanValue()) {
			// draw axes
			for(int j=0; j < noe; j++) {
				double alpha = ((double)j+.5) * 2.0 * Math.PI / (double)noe;
				double sinAlpha = Math.sin( alpha );
				double cosAlpha = Math.cos( alpha );
				axes.addDPoint(0,0);
				axes.addDPoint(sinAlpha*1.05, cosAlpha*1.05); //  axis overscale
				axes.jump();
			}
		}
		addDataSeries(axes);
			
		// iterate from below for optimal plotting
		if (!settings.getHideProfiles().getBooleanValue()) {
			List<ProbeList> pls = getProbeLists();

			Layers = new DataSeries[pls.size()];
			int h=0;

			for(int i = pls.size(); i!=0; --i) {
				ProbeList pl = pls.get(i-1);
				DataSeries ds = view(pl.getAllProbes());		
				ds.setColor(pl.getColor());
				ds.setAfterJumpModifier(probeColorSetter);
				Layers[h++] = ds;
				addDataSeries(ds);
			}
		}

		// add global centroid if selected
		if (settings.getShowCentroid().getBooleanValue()) {
			HashSet<Probe> probes = new HashSet<Probe>();
			for (ProbeList pl : getProbeLists())
				probes.addAll(pl.toCollection());			
			Probe[] allProbes = probes.toArray(new Probe[0]);
			for (int i=0; i!=allProbes.length; ++i) {
				Probe in = allProbes[i];
				Probe out = new Probe(viewModel.getDataSet().getMasterTable());
				out.setValues(viewModel.getProbeValues(in));
				allProbes[i] = out;
			}
			Probe centroidProbe = ProbeList.getMean(allProbes, viewModel.getDataSet());
			centroidProbe.setImplicitProbe(true); // this prevents a second round of zscoring in view()

			LinkedList<Probe> cpl = new LinkedList<Probe>();
			cpl.add(centroidProbe);
			DataSeries ds = view(cpl);
			BasicStroke stroke = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.f,
					new float[]{4.0f,4.0f}, 0);
			ds.setStroke(stroke);
			ds.setColor(Color.BLUE);						
			addDataSeries(ds);
		}

		// add individual centroids if selected
		if (settings.getShowCentroidForEachPL().getBooleanValue()) {
			for (ProbeList pl : getProbeLists()) {
				Probe[] allProbes = pl.getAllProbes().toArray(new Probe[0]);
				for (int i=0; i!=allProbes.length; ++i) {
					Probe in = allProbes[i];
					Probe out = new Probe(viewModel.getDataSet().getMasterTable());
					out.setValues(viewModel.getProbeValues(in));
					allProbes[i] = out;
				}
				Probe centroidProbe = ProbeList.getMean(allProbes, viewModel.getDataSet());
				centroidProbe.setImplicitProbe(true); // this prevents a second round of zscoring in view()
				LinkedList<Probe> cpl = new LinkedList<Probe>();
				cpl.add(centroidProbe);

				DataSeries ds2 = view(cpl);
				BasicStroke stroke = new BasicStroke(4);
				ds2.setStroke(stroke);
				ds2.setColor(Color.black);						
				addDataSeries(ds2);

				DataSeries ds = view(cpl);
				BasicStroke stroke2 = new BasicStroke(2);
				ds.setStroke(stroke2);
				ds.setColor(pl.getColor());						
				addDataSeries(ds);

			}
		}


		select(settings.getSelectionColor().getColorValue());


		//		System.out.println("t = "+(System.nanoTime()-start)/1000000+" ms");
	}


	public void select(Color selection_color)
	{
		Set<Probe> s = new HashSet<Probe>();		

		for(ProbeList probe_list : getProbeLists())
			s.addAll(probe_list.getAllProbes());

		s.retainAll(viewModel.getSelectedProbes());

		//		if (currentSelection.containsAll(s) && s.containsAll(currentSelection))
		//			return; // nothing changed for this plot

		currentSelection = s;

		if (selectionLayer!=null)
			removeDataSeries(selectionLayer);

		selectionLayer = view(s);

		selectionLayer.setColor(selection_color);
		selectionLayer.setStroke(new BasicStroke(3));
		if (settings.getShowDots().getBooleanValue()) {
			selectionLayer.setShape(new Shape() {
				public void paint(Graphics2D g) {				
					g.fillRect(-4,-4,7,7);
				}
				public boolean wantDeviceCoordinates() {
					return true;
				}
			});			
		}
		addDataSeries(selectionLayer);
		clearBuffer();
		repaint();
	}

	/**
	 * Compute the index of the probe to be colored
	 * @param clicked point (transformed into real coordinates)
	 * @return arrayList of the probes that fit with the click point
	 */
//	public Probe getClickedProbe(Point coordinates)	{		
//		double[] clicked = getPoint(coordinates.x, coordinates.y);
//
//		if (clicked==null) 
//			return null;
//		
//		Probe best_plid = null;			// probe that matches best with the clicked point
//		double min_imprecision = 1;	// maximal imprecision that is allowed
//		double cutoff_imprecision = .1; // here we stop looking for a better hit		
//
//		double cx = clicked[0];
//		double cy = clicked[1];
//
//		PointIterator it = new PointIterator(Layers);
//		while(it.hasNext()) {
//			Double[] point = it.next();
//			double px = point[0];
//			double py = point[1];
//			// fast evaluation of perfect hits
//			if (cx==px && cy==py)
//				return it.getProbe();
//			// only check the point if we are left of it (slope only applies there)
//			// symmetry to DataSeries.addPoint: clicked[] is the predecessor of point[]
//			if ( cx<=px ) {
//				double pslope = point[2];
//				double cslope = (py-cy)/(px-cx); 
//				// compute how far we are from the line defined by point and slope
//				double cgamma = Math.atan(cslope);
//				double pgamma = Math.atan(pslope);
//				//double angle_1 = 90;
//				double angle = Math.abs(pgamma-cgamma);
//				//double angle_3 = 180-angle_1-angle_2;
//				double hyp= Math.sqrt((cx-px)*(cx-px)+(cy-py)*(cy-py));
//				double dist = hyp * Math.sin(angle);
//				if (dist < min_imprecision && (px-cx)<=it.getStepSize()) {
//					best_plid = it.getProbe();
//					min_imprecision = dist;
//					//					xdist=(px-cx);
//					if (dist<cutoff_imprecision)
//						break;
//				}
//			}
//
//		}
//		return best_plid;
//	}

	public void setSelectionColor(Color c) {
		settings.getSelectionColor().setColorValue(c);
		if (viewModel!=null && viewModel.getSelectedProbes().size()>0)
			updatePlot();
	}

	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED: // fallthrouh
		case ViewModelEvent.DATA_MANIPULATION_CHANGED:
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED:
			updatePlot();
			break;
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			select(settings.getSelectionColor().getColorValue());
			break;
		}	
	}

	public void probeListChanged(ProbeListEvent event) {
		switch(event.getChange()) {
		case ProbeListEvent.LAYOUT_CHANGE: //fallthrough
		case ProbeListEvent.CONTENT_CHANGE:
			updatePlot();
			break;
		}
	}


	protected class ProbeColorSetter implements GraphicsModifier {
		public void modify(Graphics2D g, Object o) {
			if (o instanceof Probe) {
				Color c = coloring.getColor((Probe)o);
				if (settings.useRelevance()) {
					double rel = settings.getRelevanceSetting().getRelevance(o);
					c = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(rel*255));
				}
				g.setColor(c);
			}
		}		
	}

	public void removeNotify() {
		super.removeNotify();
		if (coloring!=null)
			coloring.removeNotify();
		viewModel.removeViewModelListener(this);
		viewModel.removeRefreshingListenerToAllProbeLists(this);
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		return "";
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		return "";
	}

}

