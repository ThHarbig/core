package mayday.vis3.gui;

import javax.swing.JMenu;

import mayday.core.settings.Setting;
import mayday.vis3.model.ViewModel;

/**
 * When a PlotComponent is added to a PlotContainer (e.g. a PlotWindow), it
 * gets the next surrounding PlotContainer passed to its setup() method.
 * "Between" Component and Container can be several other Swing/AWT components
 * such as ScrollPanes, JPanels etc.
 * The outermost Container is a PlotWindow which as instance of VisualizerMember
 * connects everything to the Visualizer.
 * Schema:  (pcm=PlotComponent, pct=PlotContainer, pw=PlotWindow, s=swing container)
 *          (pct+pcm=class fulfilling both interfaces)
 * 
 *    [BOTTOM]  pcm--(--(s)*--(pct+pcm)*--)*--pw  [TOP]
 *    
 *    Menu Items and preferred title are passed from left to right
 *    each intermediate pct can modify the title.
 *    
 *    The ViewModel is passed from right to left
 *      
 */
public interface PlotContainer {

	public static final String FILE_MENU = "Plot";
	public static final String VIEW_MENU = "View";
	public static final String ENHANCE_MENU = "Enhance";
	public static final String PROBE_SELECTION_MENU = "Selection";
	public static final String EXPERIMENT_SELECTION_MENU = "Experiments";
		
	public void addMenu(JMenu jm);
	
	/** @deprecated Use the settings framework to define plot settings and add them via addViewSettings */
	public JMenu getMenu(String name, PlotComponent askingObject);
	
	public void addViewSetting(Setting s, PlotComponent askingObject); 
	
	/* Tell the container what we think is a good name for it */
	public void setPreferredTitle(String preferredTitle, PlotComponent askingObject);
	
	public ViewModel getViewModel();
	
}
