package mayday.vis3.vis2base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.vis3.ZoomController;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import wsi.ra.chart2d.DElement;
import wsi.ra.chart2d.DPoint;
import wsi.ra.chart2d.DPointSet;
import wsi.ra.chart2d.ScaledBorder;
import wsi.ra.plotting.FunctionArea;

/**
 * Purpose: General class interface for chart plotting
 * @author Philipp Bruns
 * @version 080502
 */

@SuppressWarnings("serial")
public abstract class ChartComponent extends JPanel implements PlotComponent{
	
	protected static final double ZOOM_STEP = 0.1;

	// view elements
	protected FunctionArea farea;
	protected FAreaJPanel fareapanel;
	protected ViewModel viewModel;	
	protected Dimension scrdim;
	protected ZoomController zoomController = new ZoomController();
	
	protected ChartSetting chartSettings;
	
	protected boolean firstTime = true;
	
	public abstract void createView(); 

	public ChartComponent() 
	{
		super(new BorderLayout());
		farea = new FunctionArea("x", "y");
		farea.setBackground(Color.WHITE);
		farea.setPreferredSize(null); // size is determined by enclosing window
		
		// Set major and minor grid distances for x and y grid
		setGrid(1.0, .2);
		setGridEmphasize(1.0, 1.0);
		
		
		fareapanel = new FAreaJPanel(farea) {
			@Override
			public void setup(PlotContainer plotContainer) {
				ChartComponent.this.setup(plotContainer);
			}
//			public Setting getPlotSetting() {
//				return ChartComponent.this.getPlotSetting();
//			}
		};
		
		JScrollPane jsp = new PlotScrollPane(fareapanel) {
			protected void paintChildren(Graphics g) {
				/* FunctionArea likes painting on top of the Scrollbars
				 * so we need to change the clip rectangle if the Scrollbars are visible.
				 * (See also DMeasures.getGraphics())
				 */
//				if (getHorizontalScrollBar().isShowing() || getVerticalScrollBar().isShowing()) {
//					Rectangle previousClip = g.getClipBounds();
//					Rectangle r = this.getViewport().getVisibleRect();
//					g.setClip(r);
//					super.paintChildren(g);
//					g.setClip(previousClip);
//				} else {
					super.paintChildren(g);
//				}
				
			}
		};
		add(jsp, BorderLayout.CENTER);		
		
		chartSettings = new ChartSetting(this);
		new ChartSettingListener(this, chartSettings);
		
//		setPreferredSize(new Dimension(640,480));
		
	}

	protected void setAxisTitleDirectly(String xtitle, String ytitle) {
		farea.setAxisTitle(xtitle, ytitle);
	}	
	
	protected FunctionArea getArea() {
		return farea;
	}

	public void setBackgroundColor(Color c)	{
		farea.setBackground(c);
	}
	
//	public Setting getPlotSetting() {
//		return chartSettings;
//	}
	
	public Dimension getPreferredSize() {
		// if we don't have a preferred size return our current size instead
		Dimension p = super.getPreferredSize();
		if (p!=null && (p.width!=p.height || p.width+p.height>50))
			return p;
		p = getSize();
		return p;
	}
	
	/**
	 * apply a HashMap that associates Double values and Strings 
	 * for setting up a free customized labeling of the X axis
	 * @param map mapping Double => String for axis labeling
	 */
	public void setXLabeling(Map<Double, String> map)
	{
		if (map==null) {
			((ScaledBorder) farea.getBorder()).setXLabeling(map);
			((ScaledBorder) farea.getBorder()).setAutoScaleX();
			return;
		}
			
		
		// reduce the number of labels according to the skiplabels setting
		int skip = chartSettings.getExperimentLabelSkip().getIntValue()-1;
		if (skip>0) {
			TreeMap<Double, String> submap = new TreeMap<Double,String>(map);
			int i=skip; // always show first label (i.e. for i=0)
			Iterator<Entry<Double, String>> e = submap.entrySet().iterator();
			while (e.hasNext()) {
				e.next();
				if ((i++) != skip)  
					e.remove();
				else i=0;
			}
			map = submap;
		}
		
		((ScaledBorder) farea.getBorder()).setXLabeling(map);
	}
	
	/**
	 * apply a HashMap that associates Double values and Strings 
	 * for setting up a free customized labeling of the Y axis
	 * @param map mapping Double => String for axis labeling
	 */
	public void setYLabeling(HashMap<Double, String> map)
	{
		((ScaledBorder) farea.getBorder()).setYLabeling(map);
	}
	
	private HashMap<Double, String> getProbeLabeling()
	{
		HashMap<Double, String> probe_labeling = new HashMap<Double, String>();
		int i=0;
		
		for(Probe p : viewModel.getProbes())
				probe_labeling.put(new Double(i++), p.getName());
			
		return probe_labeling;
	}
	
	protected HashMap<Double, String> getExperimentLabeling()
	{
		HashMap<Double, String> exp_labeling = new HashMap<Double, String>();
		
		List<Experiment> le = viewModel.getDataSet().getMasterTable().getExperiments();
		int noe = le.size();
		int max = viewModel.getProbeLists(false).size() * noe;
		
		for(int i=0; i < max; i++)
			exp_labeling.put(new Double(i), le.get(i%noe).getDisplayName());
		
		return exp_labeling;
	}
		
	/**
	 * use the probe names for labeling the x axis
	 */
	public void useProbeNamesAsXLabels()
	{
		setXLabeling(getProbeLabeling());
		setScalingUnitX(1.0);
	}
	
	/**
	 * use the probe names for labeling the y axis
	 */
	public void useProbeNamesAsYLabels()
	{
		setYLabeling(getProbeLabeling());
		setScalingUnitY(1.0);
	}
	
	/**
	 * use the experiment names for labeling the x axis
	 */
	public void useExperimentNamesAsXLabels()
	{
		setXLabeling(getExperimentLabeling());
		setScalingUnitX(1.0);
	}
	
	/**
	 * use the experiment names for labeling the y axis
	 */
	public void useExperimentNamesAsYLabels()
	{
		setYLabeling(getExperimentLabeling());
		setScalingUnitY(1.0);
	}
	
	/**
	 * set the interval for drawing the values on the scaled x axis
	 * @param unit axis interval
	 */
	public void setScalingUnitX(double unit)
	{
		((ScaledBorder) farea.getBorder()).setSrcdX(unit);
	}
	
	/**
	 * set the interval for drawing the values on the scaled y axis
	 * @param unit axis interval
	 */
	public void setScalingUnitY(double unit)
	{
		((ScaledBorder) farea.getBorder()).setSrcdY(unit);
	}
	
	public void setFont(Font font) {
		super.setFont(font);
		if (farea!=null)
			farea.setFont(font);
	}
	
	/**
	 * set the intervals for drawing the grid lines 
	 * @param xunit interval on the x axis
	 * @param yunit interval on the y axis
	 */
	public void setGrid(double xunit, double yunit)
	{
		boolean b = farea.isGridVisible();
		farea.setGrid(xunit, yunit);
		farea.setGridVisible(b);
	}
	
	/**
	 * set the intervals for drawing the emphasized grid lines 
	 * @param xunit interval on the x axis
	 * @param yunit interval on the y axis
	 */
	public void setGridEmphasize(double xunit, double yunit)
	{
		boolean b = farea.isGridVisible();
		farea.setGridEmphasize(xunit, yunit);
		farea.setGridVisible(b);
	}
	
	/**
	 * remove add series
	 * @param ds DataSeries object to be added
	 */
	public void addDataSeries(DataSeries ds)
	{
		farea.addDElement(ds);
	}

	
	/**
	 * remove data series
	 * @param ds DataSeries object to be removed
	 */
	public void removeDataSeries(DataSeries ds)
	{
		farea.removeDElement(ds);
	}
	
	public void clear()
	{
		for(DElement de : farea.getDElements())
		{
			if(de instanceof DPointSet)
				farea.removeDElement(de);
		}
	}
	
	/**
	 * returns the grid coordinate for the given pixel (screen) coordinate
	 * @param screen_x X pixel coordinate
	 * @param screen_y Y pixel coordinate
	 * @return grid coordinate as double array, [0]: x, [1]: y
	 */
	public double[] getPoint(int screen_x, int screen_y)
	{
		DPoint p = farea.getDMeasures().getDPoint(screen_x, screen_y);
		return new double[] {p.x, p.y};
	}
	
	/**
	 * set the min/max values to be drawn on the x axis
	 * @param min minimal value
	 * @param max maximal value
	 */
	public void setScalingRangeX(double min, double max)
	{
		((ScaledBorder)farea.getBorder()).setXScalingRange(min, max);
	}
	
	/**
	 * set the min/max values to be drawn on the y axis
	 * @param min minimal value
	 * @param max maximal value
	 */
	public void setScalingRangeY(double min, double max)
	{
		((ScaledBorder)farea.getBorder()).setYScalingRange(min, max);
	}
	
	/**
	 * Get the pixel x-distance of two points with x-distance 1.0
	 */
	public int getXUnitInPixels()
	{
		return Math.abs(farea.getDMeasures().getPoint(1, 0).x - farea.getDMeasures().getPoint(0, 0).x);
	}
	
	/**
	 * Get the pixel y-distance of two points with y-distance 1.0
	 */
	public int getYUnitInPixels()
	{
		return Math.abs(farea.getDMeasures().getPoint(0, 1).y - farea.getDMeasures().getPoint(0, 0).y);
	}
	
	public void setGridVisible(boolean b){
		farea.setGridVisible(b);
	}
	
	public void setGridToFront(boolean b) {
		farea.setGridToFront(b);
	}
	
	public boolean getGridFront()  {
		return farea.getGridFront();
	}
	
	public void setAutoGrid(boolean b) {
		farea.setAutoGrid(b);
	}
	

	 public double getGridX()
	  {
		  return farea.getGridX();
	  }
	  
	  public double getGridY()
	  {
		  return farea.getGridY();
	  }
	  
	  public double getGridEmphX()
	  {
		  return farea.getGridEmphX();
	  }
	  
	  public double getGridEmphY()
	  {
		  return farea.getGridEmphY();
	  }


	
	public void setup(PlotContainer plotContainer) {		
		if (firstTime) {
			viewModel = plotContainer.getViewModel();
			
			getZoomController().setTarget(fareapanel);
			getZoomController().setAllowXOnlyZooming(true);
			getZoomController().setAllowYOnlyZooming(true);
		}
		plotContainer.addViewSetting(chartSettings, this);
		
	}


	public void setAutoAxisTitles(String autoXtitle, String autoYtitle) {
		// update the titles in the setting ONLY if user requested this
		VisibleRectSetting vsr = chartSettings.getVisibleRect();
		if (!vsr.usersetXtitle.getBooleanValue())
			vsr.xtitle.setStringValue(autoXtitle);
		if (!vsr.usersetYtitle.getBooleanValue())
			vsr.ytitle.setStringValue(autoYtitle);
		// the titles are _really_ set via chartsettinglistener which calls into farea.setAxisTitle()
	}
	
	public void updatePlot() {
		clear();
		// update auto-defined titles 
 		VisibleRectSetting vsr = chartSettings.getVisibleRect();
		setAutoAxisTitles(getAutoTitleX(vsr.xtitle.getStringValue()), getAutoTitleY(vsr.ytitle.getStringValue()));
		
		createView();
		clearBuffer();
	}
	
	public void clearBuffer() {
		fareapanel.updatePlot();
	}

	public void addMouseListener(MouseListener l) {
		farea.addMouseListener(l);
	}
	public void removeMouseListener(MouseListener l) {
		farea.removeMouseListener(l);
	}

	public ZoomController getZoomController() {
		return zoomController;
	}

	/** Return an automatically created title for the y axis
 	 * @param ytitle the user defined y axis title
	 * @return the automatically created y axid title, which may take the user defined title into account
	 */
	public abstract String getAutoTitleY(String ytitle) ;
	
	/** Return an automatically created title for the x axis
	 * @param xtitle the user defined x axis title
	 * @return the automatically created x axid title, which may take the user defined title into account
	 */
	public abstract String getAutoTitleX(String xtitle) ;
	
}

