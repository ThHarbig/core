package mayday.vis3.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import mayday.core.gui.MaydayFrame;
import mayday.core.settings.Setting;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.gui.actions.ExportPlotAction;
import mayday.vis3.gui.actions.ExportVisibleAreaAction;
import mayday.vis3.gui.menu.MenuManager;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;

@SuppressWarnings("serial")
public class DetachablePlot extends DetachableComponent implements PlotContainer, VisualizerMember {

	protected JMenu menu;
	
	protected Visualizer viz;
	protected boolean multiPlot;
	protected Component earlyPlot;
	protected MenuManager menuManager;
	
	boolean menusFinished = false;
	
	
	public DetachablePlot(Component plot, Visualizer viz, String title, boolean multiplot) {
		super(plot, title);
		this.multiPlot = multiplot;
		this.viz = viz;
		initMenu();
		viz.addPlot(this);
	}
	
	public DetachablePlot(Visualizer viz, String title) {
		this(null, viz, title, false);
	}
	
	protected JMenu makeFileMenu() {
		JMenu plot = new JMenu(PlotContainer.FILE_MENU);
		plot.setMnemonic('P');
		plot.add(new DeferredPlotAction(new ExportPlotAction(null)));
		plot.add(new DeferredPlotAction(new ExportVisibleAreaAction(null)));
		return plot;
	}
	
	@Override
	protected void buildMenu() {		
		menubar.add(menu);		
		super.buildMenu();
	}
	
	protected void initMenu() {
		menu = new JMenu("Menu");
		
		// we need heavy popups to make jogl plots work with popups
		HeavyWeightWorkaround.forceHeavyWeightPopups(menubar);
		
		menuManager = new MenuManager(null, this, menu) {
			protected JMenu makeFileMenu() {
				return DetachablePlot.this.makeFileMenu();
			}
		};
	}
	
	public void attach() {
		initMenu();
		super.attach();
		viz.updateVisualizerMenus();
	}
	
	public void detach() {
		menusFinished = true;
		super.detach();		
		viz.updateVisualizerMenus();
	}
	
	protected Component createDetachableComponent(Component plot) {
		Component comp;
		if (multiPlot) {
			comp = new MultiPlotPanel(plot);
		} else {
			comp = new PlotWithLegendAndTitle(plot); 
			if (plot instanceof PlotComponent)
				((PlotWithLegendAndTitle)comp).setTitledComponent((PlotComponent)plot);
		}
		return comp;
	}
	
	protected MaydayFrame createExternalWindow() {
		detachedPlot = createDetachableComponent(plot);
		MaydayFrame mf = new PlotWindow(detachedPlot, viz) {
			public void initContent() {
				super.initContent();
				removeWindowListener(closingListener); // don't tell the visualizer we're gone, cause we're not.
			}
		};
		remove(plot);				
		mf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				attach();
			}
		});		
		mf.setVisible(true);
		return mf;
	}
	
	public void toFront() {
		// ignore
	}
	
	public void addMenu(JMenu jm) {
		menuManager.addMenu(jm);
	}
	
	public JMenu getMenu(String name, PlotComponent askingObject) {
		return menuManager.getMenu(name, askingObject);
	}

	public ViewModel getViewModel() {
		return viz.getViewModel();
	}

	public void setPreferredTitle(String preferredTitle, PlotComponent askingObject) {
		menuManager.setPreferredTitle(preferredTitle, askingObject);
	}
	
	public String getPreferredTitle() {
		return title.getText();
	}

	public String getTitle() {
		return title.getText();
	}

	public JMenu getVisualizerMenu() {
		return getMenu("Visualizer",null);
	}
	
	

	public void closePlot() {
		// ignore this
	}
	
	public void setTitle(String title) {
		// ignore this
	}
		
	public void setPlot(Component plot) {
		if (getParent()==null) {
			earlyPlot = plot;
		} else {
			super.setPlot(plot);
			earlyPlot = null;
		}
	}
	
	public void addNotify() {
		super.addNotify();
		if (earlyPlot!=null)
			setPlot(earlyPlot);
	}

	public void addViewSetting(Setting s, PlotComponent askingObject) {
		menuManager.addViewSetting(s, askingObject);		
	}
	
	
	protected class DeferredPlotAction extends AbstractAction {
		Class<? extends AbstractAction> clazz;
		public DeferredPlotAction(AbstractAction exportAction) {
			super((String)exportAction.getValue(NAME));
			clazz=exportAction.getClass();
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Constructor<? extends AbstractAction> c;
			try {
				c = clazz.getConstructor(Component.class);
				AbstractAction aa = c.newInstance(plot);
				aa.actionPerformed(e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
