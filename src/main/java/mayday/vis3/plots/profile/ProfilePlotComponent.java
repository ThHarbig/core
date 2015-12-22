package mayday.vis3.plots.profile;

import static mayday.vis3.plots.profile.BreakSetting.BREAK_IGNORE;
import static mayday.vis3.plots.profile.BreakSetting.BREAK_START_LEFT;
import static mayday.vis3.plots.profile.BreakSetting.BREAK_START_LEFT_SHIFTED;
import static mayday.vis3.plots.profile.BreakSetting.BREAK_UNCONNECTED;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.vis3.ColorProvider;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.DelayedPlotUpdater;
import mayday.vis3.SortedExperiments;
import mayday.vis3.ValueProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.PointIterator;
import mayday.vis3.vis2base.Shape;
import wsi.ra.chart2d.DArea.TopMostChild;
import wsi.ra.chart2d.DPoint;
import wsi.ra.chart2d.GraphicsModifier;

@SuppressWarnings("serial")
public class ProfilePlotComponent extends ChartComponent implements ViewModelListener, ProbeListListener {

	protected DataSeries[] Layers;
	protected DataSeries[] centroids;

	// Selection
	protected DataSeries selectionLayer;
	protected Set<Probe> currentSelection = new HashSet<Probe>();

	// Members added for nice probe coloring
	protected ColorProvider coloring;
	protected ProbeColorSetter probeColorSetter = new ProbeColorSetter();

	protected ProfilePlotSetting settings;

	protected double[] experimentTimepoints = new double[0];

	protected Rectangle selRect;
	
	protected DelayedPlotUpdater delayedUpdater = new DelayedPlotUpdater(this);

	public ProfilePlotComponent() {}

	public String getPreferredTitle() {
		return "Profile Plot";
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
			settings = new ProfilePlotSetting(viewModel, this);
			//			settings.setDataSet(viewModel.getDataSet());
			settings.addSetting(coloring.getSetting());
			settings.addChangeListener(delayedUpdater);
			experimentTimepoints = settings.getTimepoints().getExperimentTimpoints();
		}

		for (Setting s : settings.getChildren()) // add children without a subcategory
			plotContainer.addViewSetting(s, this);

		viewModel.addViewModelListener(this);
		viewModel.addRefreshingListenerToAllProbeLists(this, true);

		if (Layers==null) { // don't loose previous settings
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();					
					
					Probe pb = getClickedProbe(e.getPoint());
					
					switch (e.getButton()) {
					case MouseEvent.BUTTON1:
						if (pb!=null) {
							if (e.getClickCount()==2) {
								PropertiesDialogFactory.createDialog(pb).setVisible(true);
							} else {
								
								if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
									// 			toggle selection of the clicked probe
									viewModel.toggleProbeSelected(pb);
								} else {
									// 			select only one probe
									viewModel.setProbeSelection(pb);
								}
							}
						}
						break;
					case MouseEvent.BUTTON3:
						if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
							if (pb==null)
								pb = getClickedProbe(e.getPoint(), centroids);
							if (pb!=null) {
								MoveableLabel ml = new MoveableLabel(pb);
								ml.computeLineTarget(e.getPoint().x);
								ml.setRelativeLocation(e.getPoint().x, e.getPoint().y);
								farea.add(ml);
								updatePlot();
							}
						} else {
							ProbeMenu pm = new ProbeMenu(viewModel.getSelectedProbes(), viewModel.getDataSet().getMasterTable());
							pm.getPopupMenu().add("( Use CTRL+Right click to add a label )");
							pm.getPopupMenu().show(ProfilePlotComponent.this, e.getX(), e.getY());
						}
						break;
					}
				}
				public void mouseReleased(MouseEvent e) {
					if (selRect!=null) {
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
		
		farea.setLayout(null);
				
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
		TreeSet<Integer> breaks = settings.getBreakPositions();
		int break_type = settings.getBreakType();
		double yshift= 
			break_type==BREAK_START_LEFT_SHIFTED
			?
			Math.ceil(viewModel.getMaximum(null, null)-viewModel.getMinimum(null, null)+1)
			:
			0;
					
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

		applyChangedTimepointSettings();

		boolean useTimepoints = settings.getTimepoints().useTimepoints();

		ValueProvider[] extraCols = settings.getExtraColumns().toArray(new ValueProvider[0]);
		
		int numberOfColumns = viewModel.getDataSet().getMasterTable().getNumberOfExperiments() + extraCols.length;
		
		SortedExperiments experimentOrder = settings.getExperimentOrder();
		
		for(Probe p : pl) {			
			int xmodifier = 0;
			double ymodifier = 0;
			double[] probeValues;
			if (p.isImplicitProbe())
				probeValues = p.getValues();
			else 
				probeValues = viewModel.getProbeValues(p);
			
			for(int j=0; j < numberOfColumns; j++) {
				double xposition = j;
				if (useTimepoints) {
					if (j<experimentTimepoints.length)
						xposition = experimentTimepoints[(int)xposition];
					else 
						xposition = experimentTimepoints[experimentTimepoints.length-1] + j-experimentTimepoints.length +1;
				}

				xposition = xposition+xmodifier;
				
				double theValue;
				if (j<probeValues.length) 
					theValue = probeValues[experimentOrder.mapColumn(j)];
				else
					theValue = extraCols[j-probeValues.length].getValue(p);
				
				if (Double.isNaN(theValue)) {
					if (!settings.getInferData().getBooleanValue())
						series.jump();
				} else { 
					series.addPoint(xposition, theValue+ymodifier, p);
				}
				if (break_type!=BREAK_IGNORE && breaks.contains(j)) {
					switch (break_type) {
					case BREAK_START_LEFT_SHIFTED:
						ymodifier+=yshift;
					case BREAK_START_LEFT:
						xmodifier = (useTimepoints? -j : -(j+1));  // fall through
					case BREAK_UNCONNECTED:
						series.jump();
					}
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

		if (settings.getUseGlobalMax().getBooleanValue()) {
			Collection<Probe> allTheProbes = viewModel.getDataSet().getMasterTable().getProbes().values();
			double globalMin=viewModel.getDataManipulator().getMinimum(null, allTheProbes);
			double globalMax=viewModel.getDataManipulator().getMaximum(null, allTheProbes);
			DataSeries pretty = new DataSeries();
			double prettyXcoordinate = 0;
			if (settings.getTimepoints().useTimepoints() && viewModel.getDataSet().getMasterTable().getNumberOfExperiments()>0) {
				prettyXcoordinate = settings.getTimepoints().getExperimentTimpoints()[0];
			}
			pretty.addDPoint(new DPoint(prettyXcoordinate,globalMin));
			pretty.addDPoint(new DPoint(prettyXcoordinate,globalMax));
			pretty.setColor(new Color(255,255,255,255));
			addDataSeries(pretty);
		}

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

		ArrayList<DataSeries> centroids = new ArrayList<DataSeries>();
		
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
			centroidProbe.setName("Centroid");

			LinkedList<Probe> cpl = new LinkedList<Probe>();
			cpl.add(centroidProbe);
			DataSeries ds = view(cpl);
			BasicStroke stroke = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.f,
					new float[]{4.0f,4.0f}, 0);
			ds.setStroke(stroke);
			ds.setColor(Color.BLUE);
			ds.setFixedAlpha(1.0f);
			addDataSeries(ds);
			centroids.add(ds);
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
				centroidProbe.setName("Centroid for "+pl.getName().replaceAll("\\(optimized\\)",""));

				DataSeries ds2 = view(cpl);
				BasicStroke stroke = new BasicStroke(4);
				ds2.setStroke(stroke);
				ds2.setColor(Color.black);						
				addDataSeries(ds2);
				centroids.add(ds2);

				DataSeries ds = view(cpl);
				BasicStroke stroke2 = new BasicStroke(2);
				ds.setStroke(stroke2);
				ds.setColor(pl.getColor());				
				ds.setFixedAlpha(1.0f);
				addDataSeries(ds);			
			}
		}

		this.centroids = centroids.toArray(new DataSeries[centroids.size()]);

		select(settings.getSelectionColor().getColorValue());
	}
	
	public void select(Color selection_color)
	{
		Set<Probe> s = new HashSet<Probe>();		

		for(ProbeList probe_list : getProbeLists())
			s.addAll(probe_list.getAllProbes());

		s.retainAll(viewModel.getSelectedProbes());

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
	public Probe getClickedProbe(Point coordinates)	{
		if (settings.getHideProfiles().getBooleanValue())
			return null;
		return getClickedProbe(coordinates, Layers);
	}
		
	public Probe getClickedProbe(Point coordinates, DataSeries[] dataseries) {
		double[] clicked = getPoint(coordinates.x, coordinates.y);

		if (clicked==null) 
			return null;

		Probe best_plid = null;			// probe that matches best with the clicked point
		double min_imprecision = 1;	// maximal imprecision that is allowed
		double cutoff_imprecision = .1; // here we stop looking for a better hit		

		double cx = clicked[0];
		double cy = clicked[1];

		PointIterator<Probe> it = new PointIterator<Probe>(dataseries);
		while(it.hasNext()) {
			Double[] point = it.next();
			double px = point[0];
			double py = point[1];
			// fast evaluation of perfect hits
			if (cx==px && cy==py)
				return it.getObject();
			// only check the point if we are left of it (slope only applies there)
			// symmetry to DataSeries.addPoint: clicked[] is the predecessor of point[]
			if ( cx<=px ) {
				double pslope = point[2];
				double cslope = (py-cy)/(px-cx); 
				// compute how far we are from the line defined by point and slope
				double cgamma = Math.atan(cslope);
				double pgamma = Math.atan(pslope);
				//double angle_1 = 90;
				double angle = Math.abs(pgamma-cgamma);
				//double angle_3 = 180-angle_1-angle_2;
				double hyp= Math.sqrt((cx-px)*(cx-px)+(cy-py)*(cy-py));
				double dist = hyp * Math.sin(angle);
				if (dist < min_imprecision && (px-cx)<=it.getStepSize()) {
					best_plid = it.getObject();
					min_imprecision = dist;
					//					xdist=(px-cx);
					if (dist<cutoff_imprecision)
						break;
				}
			}

		}
		return best_plid;
	}

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
			delayedUpdater.trigger();
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
			delayedUpdater.trigger();
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

	protected void applyChangedTimepointSettings() {

		if (settings.getTimepoints().useTimepoints()) {
			if (settings.getTimepoints().getExperimentTimepoints(experimentTimepoints)) {
				HashMap<Double,String> map = new HashMap<Double, String>();
				for (int i=0; i!=experimentTimepoints.length; ++i)
					map.put(experimentTimepoints[i], viewModel.getDataSet().getMasterTable().getExperimentDisplayName(i));
				// ADD extra column labels
				double max = new DoubleVector(experimentTimepoints).max();
				int i=0;
				for (ValueProvider vp : settings.getExtraColumns()) {
					++i;				
					map.put(new Double(max+i), vp.getMenuTitle());
				}
				setXLabeling(map);
			}
		} else {
			useExperimentNamesAsXLabels();				
		}

	}
	
	@Override
	protected HashMap<Double, String> getExperimentLabeling() {
		HashMap<Double, String> exp_labeling = new HashMap<Double, String>();
		List<Experiment> le = viewModel.getDataSet().getMasterTable().getExperiments();
		int noe = le.size();
		
		SortedExperiments se = settings.getExperimentOrder();
		
		for(int i=0; i < noe; i++)
			exp_labeling.put(new Double(i), le.get(se.mapColumn(i%noe)).getDisplayName());
		// add extra column labels
		for (ValueProvider vp : settings.getExtraColumns()) {
			exp_labeling.put(new Double(noe++), vp.getMenuTitle());
		}		
		return exp_labeling;
	}

	public void removeNotify() {
		super.removeNotify();
		if (coloring!=null)
			coloring.removeNotify();
		viewModel.removeViewModelListener(this);
		viewModel.removeRefreshingListenerToAllProbeLists(this);
	}
	
	public void updatePlotLater() {
		delayedUpdater.trigger();
	}
	
	
	public void setBounds(int x, int y, int width, int height) {
		// move all MoveableLabels to (0,0) to ensure they are updated by PaintChildren next time
		for (Component c: farea.getComponents())
			if (c instanceof MoveableLabel) 
				c.setLocation(0, 0);
			
		super.setBounds(x, y, width, height);
	}
	
	public class MoveableLabel extends JLabel implements SettingChangeListener, TopMostChild {
		
		protected boolean dragging = false;
		
		// target of the probe anchor line (in farea coordinates)
		protected double probex = 0;
		protected double probey = 0;
		protected Color lineCol;
		protected int lineWidth;
		protected int fontSize;
				
		// position of this element in farea coordinates
		protected DPoint labelPosition;
		protected Probe labelledProbe;
		
		protected ColorSetting fgColor, bgColor, lineColor;
		protected IntSetting thickness, font;
		protected StringSetting label;
		protected BooleanSetting bold;
		protected HierarchicalSetting setting;
		
		public MoveableLabel(Probe p) {
			super(" "+p.getDisplayName()+" ");
			labelledProbe = p;			
			setOpaque(true);
			setBorder(BorderFactory.createLineBorder(Color.white,3));
			
			label = new StringSetting("Label",null, getText());
			font = new IntSetting("Font size", null, 9);
			bold = new BooleanSetting("Bold font", null, false);
			fgColor = new ColorSetting("Label Color",null, Color.white);
			bgColor = new ColorSetting("Background Color",null, Color.black);
			lineColor = new ColorSetting("Line color", null, Color.black);
			thickness = new IntSetting("Line thickness", null, 1);
			setting = new HierarchicalSetting("Label properties")
				.addSetting(new HierarchicalSetting("Label text").addSetting(label).addSetting(font).addSetting(bold))
				.addSetting(new HierarchicalSetting("Label Box").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_HORIZONTAL)
					.addSetting(fgColor).addSetting(bgColor)
				)
				.addSetting(
						new HierarchicalSetting("Anchor line").addSetting(lineColor).addSetting(thickness)
				);
			
			setting.addChangeListener(this);
			
			setToolTipText("<html>Label interactions:<br><br>" +
					"Double click to edit properties<br>" +
					"Drag to change label position<br>" +
					"Ctrl-drag to change anchor position<br>" +
					"Ctrl-right click to delete.");
			
			fromSettings();
			
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent evt) {
					int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
					if (evt.getButton()==MouseEvent.BUTTON3) {
						if ((evt.getModifiers()&CONTROLMASK) == CONTROLMASK) {
							farea.remove(MoveableLabel.this);
							delayedUpdater.trigger();
						}						
					} else if (evt.getButton()==MouseEvent.BUTTON1 && evt.getClickCount()>1) {
						SettingDialog sd = new SettingDialog(null, setting.getName(), setting);
						sd.setVisible(true);
					}
				}
				public void mouseReleased(MouseEvent evt) {
					if (dragging) {
						delayedUpdater.trigger();
						dragging = false;
					}
				}
			});
			
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent evt) {
					int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
					if ((evt.getModifiers()&CONTROLMASK) != CONTROLMASK) {
						dragging = true; 
						fastPaint(); // remove previous line
						Point movePoint = evt.getLocationOnScreen();
						movePoint.x-=farea.getLocationOnScreen().x+getWidth()/2;
						movePoint.y-=farea.getLocationOnScreen().y+getHeight()/2;
						setRelativeLocation(movePoint.x, movePoint.y);						
						fastPaint(); // show new line
					} else {
						dragging = true;
						fastPaint(); // remove previous line
						computeLineTarget(evt.getLocationOnScreen().x - farea.getLocationOnScreen().x);
						fastPaint(); // show new line					
					}
				}
			});
			
		}
		
		private void setRelativeLocation(int x, int y) {
			labelPosition = farea.getDMeasures().getDPoint(x,y);
			setLocation(x,y);
		}
		
		public void fastPaint() {
			Graphics2D g2 = (Graphics2D)farea.getGraphics();
			g2.setXORMode(Color.cyan);
			AffineTransform at = g2.getTransform();
			g2.translate(getX(), getY());
			update(g2);
			g2.setPaintMode();
			g2.setTransform(at);
		}
		
		public void computeLineTarget(int xcoord) {
			probex = farea.getDMeasures().getDPoint(xcoord, 0).x;
			//interpolate from left to right, using the same code as the view() function

			TreeSet<Integer> breaks = settings.getBreakPositions();
			int break_type = settings.getBreakType();
			double yshift=break_type==BREAK_START_LEFT_SHIFTED?Math.ceil(viewModel.getMaximum(null, null)-viewModel.getMinimum(null, null)+1):0;
			applyChangedTimepointSettings();
			boolean useTimepoints = settings.getTimepoints().useTimepoints();

			double lefty = 0;
			double righty;
			double lastx = 0;
			
			int xmodifier = 0;
			double ymodifier = 0;
			double[] probeValues;
			if (labelledProbe.isImplicitProbe())
				probeValues = labelledProbe.getValues();
			else 
				probeValues = viewModel.getProbeValues(labelledProbe);
			
			ValueProvider[] extraCols = settings.getExtraColumns().toArray(new ValueProvider[0]);			
			int numberOfColumns = viewModel.getDataSet().getMasterTable().getNumberOfExperiments() + extraCols.length;		
			
			SortedExperiments se = settings.getExperimentOrder();
			
			double[] allValues = new double[numberOfColumns];
			for (int j=0; j!=probeValues.length; ++j) {
				allValues[j] = probeValues[se.mapColumn(j)];
			}
			for (int j=probeValues.length; j!=numberOfColumns; ++j) {
				allValues[j] = extraCols[j-probeValues.length].getValue(labelledProbe);
			}
			
			for(int j=0; j < numberOfColumns; j++) {
				double xposition = j;
				if (useTimepoints) {
					if (j<experimentTimepoints.length)
						xposition = experimentTimepoints[(int)xposition];
					else 
						xposition = experimentTimepoints[experimentTimepoints.length-1] + j-experimentTimepoints.length +1;
				}					
				xposition = xposition+xmodifier;

				if (!Double.isNaN(allValues[j])) {

					// best case
					if (xposition==probex) {
						probey = allValues[j];
						return;					
					}

					if (xposition<probex) { // find left interpolation anchor
						lefty = allValues[j]+ymodifier;
						lastx = xposition;
					} else { // xpos>probex for the first time
						righty = allValues[j]+ymodifier;
						double deltaY = righty-lefty;
						double deltaX = xposition-lastx;
						double percentageX = ((probex-lastx)/deltaX);
						probey = percentageX*deltaY + lefty; // already includes ymodifier
						return;
					}
					
				}
							
				if (break_type!=BREAK_IGNORE && breaks.contains(j)) {
					switch (break_type) {
					case BREAK_START_LEFT_SHIFTED:
						ymodifier+=yshift;
					case BREAK_START_LEFT:
						xmodifier = (useTimepoints? -j : -(j+1));  
					}
				}
			}
			
		}
		
		public void paint(Graphics g) {
			// remove graphics shift and cliprect
			Graphics2D g2d = ((Graphics2D)g);
			java.awt.Shape clip = g2d.getClip();
			AffineTransform at = g2d.getTransform();
			Stroke stroke = g2d.getStroke();
			g2d.translate(-getX(), -getY());
			g2d.setClip(null);
			// get positions in device coordinates
			Point labelPos = farea.getDMeasures().getPoint(labelPosition);
			Point lineTarget = farea.getDMeasures().getPoint(probex, probey);
			// draw line
			g2d.setColor(lineCol);
			g2d.setStroke(new BasicStroke(lineWidth));
			g2d.drawLine(labelPos.x+getWidth()/2, labelPos.y+getHeight()/2, lineTarget.x, lineTarget.y);
			// paint label
			g.translate(labelPos.x, labelPos.y);
			paintComponent(g);
			// restore graphics
			g2d.setTransform(at);
			g2d.setClip(clip);
			g2d.setStroke(stroke);
			// update location
			setLocation(labelPos.x, labelPos.y);
		}

		@Override
		public void stateChanged(SettingChangeEvent e) {
			if (e.hasSource(thickness))
				lineWidth = thickness.getIntValue();
			else if (e.hasSource(lineColor))
				lineCol = lineColor.getColorValue();
			else if (e.hasSource(fgColor))
				setForeground(fgColor.getColorValue());
			else if (e.hasSource(bgColor))
				setBackground(bgColor.getColorValue());
			else if (e.hasSource(label)) {
				setText(label.getStringValue());
				setSize(getPreferredSize());
			} else if (e.hasSource(font)) {
				setFont(getFont().deriveFont((float)font.getIntValue()));
				setSize(getPreferredSize());
			} else if (e.hasSource(bold)) {
				setFont(getFont().deriveFont(bold.getBooleanValue()?Font.BOLD:Font.PLAIN));
				setSize(getPreferredSize());
			}
			delayedUpdater.trigger();
			
		}
		
		public void fromSettings() {
			lineWidth = thickness.getIntValue();
			lineCol = lineColor.getColorValue();
			setForeground(fgColor.getColorValue());
			setBackground(bgColor.getColorValue());
			setText(label.getStringValue());
			setFont(getFont().deriveFont((float)font.getIntValue()));
			setFont(getFont().deriveFont(bold.getBooleanValue()?Font.BOLD:Font.PLAIN));
			setSize(getPreferredSize());
		}
		
		
	}

	@Override
	public String getAutoTitleX(String xtitle) { 
		if (settings.getTimepoints().useTimepoints())
			return "Time point";
		return "Experiment";
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		String manip = viewModel.getDataManipulator().getManipulation().getDataDescription();
		if (manip.length()>0)
			manip = ", "+manip;
		return "Expression value"+manip;
	}

}

