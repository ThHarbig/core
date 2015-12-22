package mayday.vis3.plots.genomeviz.genomeheatmap.menues;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.genetics.basic.Species;
import mayday.vis3.gui.actions.GoToProbeAction;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.SearchOperations;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.ChromosomeViewing.ChromosomeViewingSetting;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.windows.RangeSelectionSettings;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.zoom.ZoomViewMenu;
import mayday.vis3.plots.genomeviz.genomeheatmap.usergestures.UserGestures;
import mayday.vis3.plots.genomeviz.genomeorganisation.Chrome;

public class MenuManager implements SettingChangeListener {
 
	protected MasterManager master;
	protected Controller c;
	
	protected ZoomViewMenu zoomViewMenu;
	protected ChromeSelectionMenu chromeSelectionMenu;
	protected ListSelectionModel probeInformationSelectionModel;	// needed for JList's to sync selected entries
	
	protected DefaultListModel modelForProbesInCell;
	
	protected JTextPane styledTextArea;
	protected JTextPane chromeStyledTextArea;
	protected EventListenerList eventListenerList = new EventListenerList();
	protected ChromosomeViewing chromeViewing;
	protected ChromosomeViewingSetting chromeViewingSetting;
	protected StyleSetting styset;
//	protected Zooming zooming;
	protected RangeSelectionSettings rss;

	public MenuManager(MasterManager Master, Controller controller){
		master = Master;
		c = controller;
		chromeViewing = new ChromosomeViewing(master, this);
		chromeViewingSetting = chromeViewing.getSetting();
		styset = new StyleSetting("Style", "StyleSelection",c, master);

		zoomViewMenu = new ZoomViewMenu(this, this.master, this.c);
		chromeSelectionMenu = new ChromeSelectionMenu(this, this.master, this.c);
//		zooming = new Zooming(this);
		rss = new RangeSelectionSettings(this.c, master);
	}
	
	public void addChangeListener(ChangeListener cl) {
		eventListenerList.add(ChangeListener.class, cl);		
	}
	
	public void removeChangeListener(ChangeListener cl) {
		eventListenerList.remove(ChangeListener.class, cl);
	}
	
	public void fireChanged() {
		
		Object[] l_listeners = this.eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;
		
		ChangeEvent event = new ChangeEvent(this);

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ChangeListener.class )  {
				ChangeListener list = ((ChangeListener)l_listeners[i+1]);
				list.stateChanged(event);
			}
		}
	}
	
	/**
	 * Creates the Menu entries "Chrome View " with submenus (like SplitView, ChromeDetail, Zoom, 
	 * Species/Chrome Selection).
	 * @return JMenu which contains all JMenus to select different kind of chromosome views
	 */
	public JMenu setChromeViewMenu() {
		JMenu menu = new JMenu( "Zoomlevel" );
		menu.setMnemonic( KeyEvent.VK_V );

		//menu.add(new ZoomLevelEditorPanel(this, "Zoomlevel"));
		//menu.add(zoomingSetting.getMenuItem(null));
		return ( menu );
	}
	
//	public ZoomingSetting getZoomingSetting() {
//		if(zooming!=null){
//			return zooming.getSetting();
//		}
//		return null;
//	}
	
	public JMenu setSpecChrSelMenu() {

		return (chromeSelectionMenu.speciesAndChromeSelectionMenu());

	}
	
	
	//#############################################################
	/*
	 * Menu entries
	 * 
	 */
	//#############################################################


	//#############################################################
	/*
	 * Operations on MasterManager
	 * 
	 */
	//#############################################################
	
	protected Species getSelectedSpecies(){
		return master.getSelectedSpecies();
	}
	
	protected Chrome getSelectedChrome(){
		return master.getSelectedChrome();
	}
	
//	protected ChromosomeSetContainer getChromeSetContainer(){
//		return master.getChromeSetContainer();
//	}

	//#############################################################
	/*
	 * Operations on KindOfChromeView
	 * 
	 */
	//#############################################################

	/**
	 * sets the button from KindOfChromeView selected.
	 * @param view
	 */
//	public void setKindOfChromeViewButtons(KindOfChromeView view){ 
//		kindOfChromeViewMenu.setKindOfChromeViewButtons(view);
//	}
//	
//	public void setSplitViewButtons(SplitView view){
//		splitViewMenu.setSplitViewButtons(view);
//	}
	
	public void setZoomLevelButtons(ZoomLevel level){
		zoomViewMenu.setZoomViewButtons(level);
	}
	
	public void initializeRangeOfWindow(){
		zoomViewMenu.initializeComboBox();
	}

	public void enableZoomView_FitButton() {

		zoomViewMenu.enableZoomView_FitButton();
	}

	public void disableZoomView_FitButton() {
		zoomViewMenu.disableZoomView_FitButton();
		
	}

	public void setFitLabel(Integer zoomMultiplikator) {
		zoomViewMenu.setFitLabel(zoomMultiplikator);
	}

	@SuppressWarnings("serial")
	public JMenu findProbe_window() {
		JMenu menu = new JMenu( "Find" );
		
		// Find a single probe
		menu.add(new GoToProbeAction() {
			public boolean goToProbe(String probeIdentifier) {
				return SearchOperations.searchProbe(probeIdentifier, master.getTable(), master.getTableModel(), master.getController());
			}
		});
		return ( menu );
	}
	
	/**
	 * select range of probes which are shown in new chromeHeatMapTable.
	 * @return JMenu
	 */
	public JMenu selectRangeOfProbesForNewWindow() {
		JMenu menu = new JMenu( "Select range" );
		

		JMenuItem menuItem = new JMenuItem("by selected probes");
		menuItem.addActionListener(this.master.getController().getC_dt());
		menuItem.setActionCommand(UserGestures.PROBE_RANGE_SELECTION);
		menu.add(menuItem);

//		menu.add(new ChooseRange("by positions...", master));
		
		JMenuItem selectRange = new JMenuItem("by positions...");
		selectRange.addActionListener(new ActionListener(){

			
			public void actionPerformed(ActionEvent e) {
				if(master.menuManager.getRss() !=null){
					RangeSelectionSettings rss = master.menuManager.getRss();
					SettingsDialog sd = rss.getDialog();
					if(sd!=null){
						sd.setVisible(true);
					}
				}
			}
		});
		
		menu.add(selectRange);
		
		return ( menu );
	}
	
	protected class ProbeInformationsListSelectionManager implements ListSelectionListener{
		
		public void valueChanged(ListSelectionEvent event) {
			ListSelectionModel listSelectionModel = (ListSelectionModel)event.getSource();

			if(!listSelectionModel.isSelectionEmpty()){
				listSelectionModel.getMinSelectionIndex();
				int sel_index = listSelectionModel.getLeadSelectionIndex();
				

				String text = master.getTableModel().getInformationAboutProbe((Probe)modelForProbesInCell.get(sel_index));
				chromeStyledTextArea.setText(text);
			}
		}
	}

	public ZoomViewMenu getZoomViewMenu() {
		return this.zoomViewMenu;
	}

	public ChromeSelectionMenu getChromeSelectionMenu() {
		return this.chromeSelectionMenu;
	}

	public void enableCondensedView(boolean b) {
		chromeViewingSetting.enableCondensedViewButtons(b);
	}
	
	public Setting getViewingSetting(){
		return chromeViewingSetting;
	}

	public void stateChanged(SettingChangeEvent e) {

		this.c.setChromeViewSettings(chromeViewingSetting.getProbelistColoring(), 
				chromeViewingSetting.getRepresentation(),chromeViewingSetting.getKindOfChromeView());

	}

	public StyleSetting getStyleSetting() {
		return this.styset;
	}

	public RangeSelectionSettings getRss() {
		return rss;
	}
}
