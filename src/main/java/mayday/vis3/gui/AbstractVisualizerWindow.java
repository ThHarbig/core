package mayday.vis3.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import mayday.core.MaydayDefaults;
import mayday.core.gui.MaydayFrame;
import mayday.core.settings.Setting;
import mayday.vis3.gui.menu.MenuManager;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;


@SuppressWarnings("serial")
public abstract class AbstractVisualizerWindow extends MaydayFrame implements VisualizerMember, PlotContainer {
	
	protected Visualizer visualizer;
	protected Component content; 
	protected WindowListener closingListener;
	
	protected MenuManager menuManager;
	
	public AbstractVisualizerWindow(Component windowContent, Visualizer vis, String title) {
		setTitle(title);
		closingListener = new ClosingListener();
		this.addWindowListener(closingListener);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		visualizer = vis;
		this.setJMenuBar(new JMenuBar());
		
		content = windowContent;
		
		initContent();
		
		buildMenu();

		buildToolbar();
		
		pack();
		
		computeSize();
		
		setIconImage(MaydayDefaults.createImage(MaydayDefaults.PROGRAM_ICON_IMAGE));
	}	
	
	protected abstract void initContent();
	
	
	protected abstract JMenu makeFileMenu(); 
	
	protected void computeSize() {
		int newWidth = 800;
		int newHeight = 600;
		setSize(newWidth, newHeight);
	}
	
	public Component getContent() {
		return content;
	}

	public final void buildMenu()  {
		menuManager = new MenuManager(this, this, getJMenuBar(), manageProbeSelection(), manageExperimentSelection()) {
			protected JMenu makeFileMenu() {
				return AbstractVisualizerWindow.this.makeFileMenu();
			}
		};
	}
	
	protected boolean manageExperimentSelection() {
		return true;
	}

	protected boolean manageProbeSelection() {
		return true;
	}

	public void addMenu(JMenu jm) {
		menuManager.addMenu(jm);
	}
	
	public JMenu getMenu(String name, PlotComponent askingObject) {
		return menuManager.getMenu(name, askingObject);
	}
	
	public void addViewSetting(Setting s, PlotComponent askingObject) {
		menuManager.addViewSetting(s, askingObject);
	}
	
	public void setPreferredTitle(String t, PlotComponent askingObject) {
		menuManager.setPreferredTitle(t, askingObject);
		setTitle(menuManager.getPreferredTitle());
	}


	public String getPreferredTitle() {
		return menuManager.getPreferredTitle();
	}
	
	protected void buildToolbar() {
		add(PluginSorter.createToolbar(visualizer), BorderLayout.PAGE_START);
	}
	
	public void closePlot() {
		dispose();
	}

	private class ClosingListener extends WindowAdapter {
		public void windowClosed(WindowEvent w) {
			visualizer.removePlot(AbstractVisualizerWindow.this); //will call closePlot();
		}
	}

	public ViewModel getViewModel() {
		return visualizer.getViewModel();
	}
	
	public Component getWindowContent() {
		return content;
	}

	public JMenu getVisualizerMenu() {
		return getMenu("Visualizer",null);
	}

	
}