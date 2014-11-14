package mayday.vis3.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JPanel;

import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.ZoomController;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;

/** 
 * Framework for combining other plots into one NxM multi plot.
 */

@SuppressWarnings("serial")
public class MultiPlotPanel extends JPanel 
implements PlotComponent, PlotContainer, SettingChangeListener {

	protected PlotContainer parent;

	protected static final int GAP=15;
	protected int[] dimensions = new int[]{0,0};
	
	protected IntSetting rowCount = new IntSetting("Number of rows","The number of columns will be computed automatically.",1,0,null,false,true);
	
	protected Component[] plots = new Component[0];
	
	protected ZoomController zoomController = new ZoomController();

	public MultiPlotPanel() {
		rowCount.addChangeListener(this);
	}	
	
	public MultiPlotPanel(Component... thePlots) {
		this();
		plots = thePlots;
	}
	
	public void setPlots(Component[] thePlots, int[] dims) {
		if (plots!=null)
			removeChildrenAsSources(plots);
		
		removeAll();
		// clear background
		Graphics2D g = (Graphics2D)getGraphics();
		if (g!=null) {
			g.setBackground(Color.WHITE);
			g.clearRect(0,0,getWidth(),getHeight());
		}
		// start from scratch
		setLayout(new GridLayout(dims[0],dims[1],GAP,GAP));
		this.setBackground(Color.WHITE);
		for(Component pc : thePlots) {
			zoomController.addEventSource(pc);
			add(pc);
		}
		dimensions = dims;
		plots = thePlots;
		rowCount.setIntValue(dims[0]);
		
		if (plots!=null)
			addChildrenAsSources(plots);
		
		invalidate();
		revalidate();
	}
	
	protected void addChildrenAsSources(Component[] children) {
		for (Component c : children) {
			zoomController.addEventSource(c);
			if (c instanceof Container) {
				addChildrenAsSources(((Container)c).getComponents());
			}
		}
	}
	
	protected void removeChildrenAsSources(Component[] children) {
		for (Component c : children) {
			zoomController.removeEventSource(c);
			if (c instanceof Container) {
				removeChildrenAsSources(((Container)c).getComponents());
			}
		}
	}
	
	public void setPlots(Component... thePlots) {
		setPlots(thePlots, findBestRC(thePlots.length));
	}

	public void setPlots(List<Component> thePlots, int[] dims) {
		setPlots(thePlots.toArray(new Component[0]), dims);
	}

	public void setPlots(List<Component> thePlots) {
		setPlots(thePlots, findBestRC(thePlots.size()));
	}
	
	public void reLayout(int[] dims) {
		setPlots(plots, dims);
	}

	public static int[] findBestRC(int numberOfPlots) {
		int r=0,c=0;
		int waste=Integer.MAX_VALUE;
		for (int deltaC = -1; deltaC <= 1; ++deltaC ) {
			int xc = (int)Math.ceil(Math.sqrt(numberOfPlots))+deltaC;
			if (xc==0)
				continue;
			int xr = (int)Math.ceil((double)numberOfPlots/(double)xc);
			if (xc<0 || xr<0)
				continue;
			int xpenalty = Math.abs(xr-xc);
			int xwaste = 100 * ((xc*xr) - numberOfPlots)  + xpenalty;
			if (xwaste<waste) {
				c=xc;
				r=xr;
				waste = xwaste;
			}
			if (waste==0) break;
		}
		return new int[]{r,c};
	}

	public void setup(PlotContainer parent) {
		this.parent = parent;
		parent.addViewSetting(rowCount, null);
		zoomController.setTarget(this);
		zoomController.setActive(false);
		if (plots.length>0)
			setPlots(plots);
	}
	
	protected Window getOutermostJWindow() {
		Component comp = this;
		while (comp!=null && !(comp instanceof Window)) {
			comp=comp.getParent();
		}
		return((Window)comp);
	}
	
	@Override
	public void addKeyListener(KeyListener l) {
		Window w = getOutermostJWindow();
		if (w!=null) 
			w.addKeyListener(l);
	}
	
	@Override  
	public void removeKeyListener(KeyListener l) {
		Window w = getOutermostJWindow();
		if (w!=null) 
			w.removeKeyListener(l);		
	}


	public void addMenu(JMenu jm) {
		parent.addMenu(jm);
	}

	public ViewModel getViewModel() {
		return parent.getViewModel();
	}

	public void addNotify() {
		super.addNotify();
		Component comp = getParent();
		while (comp!=null && !(comp instanceof PlotContainer)) {
			comp=comp.getParent();
		}
		if (comp!=null) {
			setup((PlotContainer)comp);
		}
	};	

	@Override
	public void paint(Graphics g) {
		for (Component c : plots)
			c.setPreferredSize(c.getSize());
		super.paint(g);
	}
	
	public void updatePlot() {
	}

	public void addViewSetting(Setting s, PlotComponent askingObject) {
		parent.addViewSetting(s, askingObject);		
	}

		
	@Deprecated
	public JMenu getMenu(String name, PlotComponent askingObject) {
		return parent.getMenu(name, askingObject);
	}

	public void setPreferredTitle(String preferredTitle,
			PlotComponent askingObject) {
		parent.setPreferredTitle(preferredTitle, askingObject);		
	}

	public void stateChanged(SettingChangeEvent e) {
		int rows = rowCount.getIntValue();
		int cols = plots.length / rows;
		reLayout(new int[]{rows,cols});
	}

}
