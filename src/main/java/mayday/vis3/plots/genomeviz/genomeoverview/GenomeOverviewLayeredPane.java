package mayday.vis3.plots.genomeviz.genomeoverview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.io.nativeformat.FileLoadDialog;
import mayday.core.io.nativeformat.FileStoreDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.genomeviz.NoLocusMIO_Panel;
import mayday.vis3.plots.genomeviz.Organiser;
import mayday.vis3.plots.genomeviz.genomeorganisation.GenomeDAO;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.UserGestures;
import mayday.vis3.plots.genomeviz.genomeoverview.menues.ChooseRange;
import mayday.vis3.plots.genomeviz.genomeoverview.menues.FitViewAction;
import mayday.vis3.plots.genomeviz.genomeoverview.menues.MenuModel;
import mayday.vis3.plots.genomeviz.genomeoverview.menues.SingleBaseViewAction;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;

@SuppressWarnings("serial")
public class GenomeOverviewLayeredPane extends JLayeredPane implements
		PlotComponent, ViewModelListener, ProbeListListener, Scrollable, ChangeListener {

	protected Organiser org = null;
	protected ViewModel viewModel = null;
	protected GenomeOverviewModel chromeModel = null;
	protected Controller c = null;

	private static int MAX_PREFERRED_SCROLLABLE_HEIGHT = 0;
	private static int MAX_PREFERRED_SCROLLABLE_WIDTH = 0;
	
	protected boolean fix_height = false;

	public GenomeOverviewLayeredPane(GenomeOverviewModel ChromeModel, Controller C) {
		
		chromeModel = ChromeModel;
			
		chromeModel.addChangeListener(this);
		c = C;

		setName("JLayeredPane");
		setVisible(true);
//		this.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
		addMouseListener(c);

		addMouseWheelListener(c);

		this.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				GenomeOverviewLayeredPane.this.chromeModel.selModel.setSelectedTrack(null);
			}

			public void mousePressed(MouseEvent arg0) {
				GenomeOverviewLayeredPane.this.chromeModel
						.selModel.setSelectedTrack(null);
			}

		});

	}

	public GenomeDAO christianHatGelogen() {
		return org.getChromeManager();
	}
	
	protected void addPanel_dragLayer() {
		if (chromeModel.getPanelToAdd() != null) {
			ITrack panel = chromeModel.getPanelToAdd();
			chromeModel.resetPanelToAdd();
			if (panel instanceof ITrack) {
				panel.getTrackPlugin();
				chromeModel.repaintTrackImage(((ITrack) panel));
			}
			this.add((JPanel) panel, JLayeredPane.DRAG_LAYER);
		}
	}

	public void setup(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("Genome Browser",this);
		
		viewModel = plotContainer.getViewModel();
		org = Organiser.getInstance(viewModel);

		org.addPlotComponent(this);
		
		if (org.isDataSetted() && org.containsLoci()) {
			addPanel_dragLayer();
			resetMySize();
			chromeModel.initialize(viewModel,org);
			viewModel.addRefreshingListenerToAllProbeLists(this, true);
			viewModel.addViewModelListener(this);
			editViewMenu(plotContainer);
			editSelectionMenu(plotContainer);
			editPlotMenu(plotContainer);
			updatePlot();
		} else {
			createDefaultPanel();		
		}
	}
	
	private void createDefaultPanel() {
		try {
			Container oneAboveTheScrollPane = getParent().getParent().getParent();
			oneAboveTheScrollPane.add(new NoLocusMIO_Panel(this), BorderLayout.CENTER);
		} catch(NullPointerException npe) {
			System.err.println("No locus information found and furthermore: ");
			npe.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void editPlotMenu(PlotContainer pc) {
		JMenu plotMenu = pc.getMenu(PlotContainer.FILE_MENU, this);
		
		JMenuItem saveItem = new JMenuItem(new AbstractAction("Save track layout") {
			public void actionPerformed(ActionEvent e) {

				Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node( this.getClass().getName() );

				final JFileChooser fc = new JFileChooser(
			            prefs.get(FileStoreDialog.LAST_SAVE_DIR_KEY, System.getProperty("user.home"))
				);
				
				fc.setDialogType(JFileChooser.SAVE_DIALOG);

				// show dialog
				fc.setDialogTitle("Save Track Layout");
				
				int res = fc.showSaveDialog(null);
		        if (res!=JFileChooser.APPROVE_OPTION) {
		        	return;
		        }
		        
		        prefs.put(FileStoreDialog.LAST_SAVE_DIR_KEY, fc.getCurrentDirectory().getAbsolutePath());
				
		        AbstractTask at = new AbstractTask("Saving track layout") {

					protected void doWork() throws Exception {
						Preferences root = Preferences.createUnconnectedPrefTree("Saved Tracks", "");
						
						int i=0;
						
						for (ITrack t : chromeModel.getPanelPositioner().getTracks().values()) {
							Preferences tx = root.node(""+(i++));
							tx.Value = t.getTrackPlugin().getPluginInfo().getIdentifier();
							tx.node("Ypos").Value=""+t.getPositionInPane();
							tx.node("Height").Value=""+t.getHeight();
							tx.node("Setting").connectSubtree(t.getTrackPlugin().getTrackSettings().getRoot().toPrefNode());
						}
						
						root.connectSubtree( chromeModel.getPanelPositioner().getSetting().toPrefNode() );
						
						root.saveTo(new BufferedWriter(new FileWriter(fc.getSelectedFile())));
					}

					protected void initialize() {}		        	
		        };
		        at.start();

				
			}
		});
		
		JMenuItem loadItem = new JMenuItem(new AbstractAction("Load track layout") {
			public void actionPerformed(ActionEvent e) {
				
				Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node( this.getClass().getName() );
				
				final JFileChooser fc = new JFileChooser(
						prefs.get(FileLoadDialog.LAST_OPEN_DIR_KEY, System.getProperty("user.home"))
				);

				fc.setDialogType(JFileChooser.OPEN_DIALOG);
				fc.setMultiSelectionEnabled(false);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				// show dialog
				fc.setDialogTitle("Open Saved Track Layout");
				int res = fc.showOpenDialog(null);
				if (res!=JFileChooser.APPROVE_OPTION) {
					return; 
				}
				
				prefs.put(FileLoadDialog.LAST_OPEN_DIR_KEY, fc.getCurrentDirectory().getAbsolutePath());

				AbstractTask at = new AbstractTask("Loading track layout") {

					@Override
					protected void doWork() throws Exception {
						chromeModel.getPanelPositioner().removeTracks();
						
						Preferences root = Preferences.createUnconnectedPrefTree("Saved Tracks", "");
						root.loadFrom(new BufferedReader(new FileReader(fc.getSelectedFile())));
						Setting s = chromeModel.getPanelPositioner().getSetting();
						s.fromPrefNode(root.node(s.getName()));
						
						int i=0;
						while (true) {					
							if (root.getChild(""+i)==null)
								break;
							Preferences tx = root.node(""+i);
							++i;
							
							PluginInfo pli = PluginManager.getInstance().getPluginFromID(tx.Value);
							if (pli!=null) {
								AbstractTrackPlugin atp = (AbstractTrackPlugin)pli.newInstance();
								if (Track.isScaleTrack(atp))
									continue;
								int ypos = tx.getInt("Ypos",50*i);
								int height = tx.getInt("Height", 50);
								atp.init(chromeModel, c);
								AbstractTrackSettings ats = (AbstractTrackSettings)atp.getTrackSettings();
								ats.getRoot().fromPrefNode(tx.node("Setting").node(ats.getRoot().getName()));
								ITrack it = chromeModel.createNewTrack(atp);
								it.resizeTrackheight(height);
								atp.actualizeTrack();
								it.setLocationInPanel(ypos);
							}
						}
					}

					@Override
					protected void initialize() {}
				};
				at.start();
				at.waitFor();
			    
//				for (ITrack t : chromeModel.getPanelPositioner().getTracks().values()) 
//					System.out.println(t.getTrackPlugin().getPluginInfo().getIdentifier()+": "+t.getPositionInPane());
			}
		});
		
		plotMenu.add(loadItem);
		plotMenu.add(saveItem);
	}

	private void editSelectionMenu(PlotContainer plotContainer) {
	}
	
	@SuppressWarnings("deprecation")
	private void editViewMenu(PlotContainer plotContainer) {
		// get menu
		JMenu viewMenu = plotContainer.getMenu(PlotContainer.VIEW_MENU, this);
		
		MenuModel mm = chromeModel.getController().getMenuModel();
		viewMenu.add(mm.getSpecChrSelMenu());
		
		JMenu find = c.getMenuModel().findProbe_window();
		JMenuItem gotoPosition = new JMenuItem("Find position...");
		gotoPosition.addActionListener(chromeModel.getController());
		gotoPosition.setActionCommand(UserGestures.GOTO_POSITION);
		find.add(gotoPosition);
		
		viewMenu.add(find);
		
		plotContainer.addViewSetting(chromeModel.getPanelPositioner().getSetting(), this);
		
		plotContainer.addViewSetting(chromeModel.getShowMarkerSetting(), this);
		
//		sub.add(new RangeSelectionSettings(chromeModel));
		JMenu zoom = new JMenu("Zoom");
		
		zoom.add(new FitViewAction("Fit chromosome",chromeModel,c));
		zoom.add(new ChooseRange("Fit range", chromeModel, c, UserGestures.SELECT_RANGE_TO_VIEW));
		
		zoom.add(new SingleBaseViewAction("Use 1 pixel per base", chromeModel, c));
		
		viewMenu.add(zoom);
		
//		if (chromeModel.getChromosomeSettings()!=null)
//			plotContainer.addViewSetting(chromeModel.getChromosomeSettings().getRoot(), this);
	}
	
	// diese Methode wird aufgerufen um ein Objekt am Bildschirm darzustellen
	// also wenn b.add(f); dann ruft f addNotify() auf
	/* From Component */
	public void addNotify() {

		// calls addNotify() -> always when component is added to container
		super.addNotify();
		Component comp = this;

		// get outermost component and call setup for component
		while (comp != null && !(comp instanceof PlotContainer)) {
			comp = comp.getParent();
		}

		if (comp != null) {
			// call setup for outermost component
			setup((PlotContainer) comp);
		}
	}

	public void removeNotify() {
		super.removeNotify();
		
		viewModel.removeViewModelListener(this);
		if(chromeModel!=null){
			if (chromeModel.getPanelPositioner()!=null)
				chromeModel.getPanelPositioner().removeTracks(true);
			chromeModel.clearData();
			chromeModel = null;
		}
		
		if(org!=null){
			org.removePlotComponent(this);
			org = null;
		}
		
		c.removeNotify();
	}

	/**
	 * get outermost window.
	 * @return
	 */
	protected Window getOutermostJWindow() {
		Component comp = this;
		while (comp != null && !(comp instanceof Window)) {
			comp = comp.getParent();
		}
		return ((Window) comp);
	}

	public void updatePlot() {
		this.repaint();
	}

	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange() == ViewModelEvent.PROBE_SELECTION_CHANGED) {
			updatePlot();
		} else if(vme.getChange() == ViewModelEvent.TOTAL_PROBES_CHANGED){
			chromeModel.actualizeTracks();
			updatePlot();
		}
	}

	public void probeListChanged(ProbeListEvent event) {
		System.out
				.println("probeListChanged - catched from ChromeHeatMapTable");
		switch (event.getChange()) {
		case ProbeListEvent.LAYOUT_CHANGE:
			updatePlot();
			break;
		case ProbeListEvent.CONTENT_CHANGE:
			updatePlot();
			break;
		case ProbeListEvent.PROBELIST_CLOSED:
			updatePlot();
			break;
		}
	}

	public void removeTrack(ITrack epanel) {
		this.remove((JPanel)epanel);
		this.chromeModel.removeTrack(epanel.getTrackPlugin());
		updatePlot();
		this.repaint();
	}
	
	//#############################################
	//
	// Scrollable implementations
	//
	//#############################################
	
	public Dimension getPreferredSize() {
		final int count = getComponentCount();
		
		int w = 0;
		int h = 0;
		for (int i = 0; i < count; i++) {
			final Component c = getComponent(i);
			w = Math.max(w, c.getX() + c.getWidth());
			h = Math.max(h, c.getY() + c.getHeight());
		}
		
		final Insets insets = getInsets();

		PlotScrollPane scrollPane = chromeModel.getScrollPane();
		
		if (w<1) w=1;
		if (h<1) h=1;
	
		if(scrollPane != null){
//			if(h < scrollPane.getVisibleRect().height){
//				h = scrollPane.getVisibleRect().height - (scrollPane.getHorizontalScrollBar().getHeight());
//			}
//			
			if(this.fix_height){
				final Dimension minimumLayeredPaneSize = scrollPane.getViewport().getExtentSize();
				
				h = (int)minimumLayeredPaneSize.getHeight();
			}
			
			if(w < scrollPane.getVisibleRect().width){
				w = scrollPane.getVisibleRect().width-
				(scrollPane.getVerticalScrollBar().isVisible()?scrollPane.getVerticalScrollBar().getWidth():0);
			}
		}
		return new Dimension(w + insets.right, h + insets.bottom);
	}
	
	public void resetMySize(int newwidth, int newheight){
		Dimension dim = getPreferredSize();
		int height = dim.height;
		int width = dim.width;
		
		if(newwidth==0 && newheight >0){
			dim = new Dimension(width, newheight);
		} else if(newwidth>0 && newheight ==0){
			dim = new Dimension(newwidth, height);
		} else if(newwidth>0 && newheight >0){
			dim = new Dimension(newwidth, newheight);
		}

		this.setPreferredSize(dim);
		this.setSize(dim);
	}
	
	/**
	 * reset size of layeredPane.
	 */
	public void resetMySize(){
		Dimension dim = getPreferredSize();
		this.setPreferredSize(dim);
		this.setSize(dim);
	}

	public Dimension getPreferredScrollableViewportSize() {
		final Dimension dim = getPreferredSize();
		dim.height = Math.min(dim.height, MAX_PREFERRED_SCROLLABLE_HEIGHT);
		dim.width = Math.min(dim.width, MAX_PREFERRED_SCROLLABLE_WIDTH);
		return dim;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if(c.isCtrl_pressed()){
			return 0;
		}
		return 10;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 100;
	}

	public void stateChanged(ChangeEvent e) {
		addPanel_dragLayer();
		resetMySize();
		updatePlot();
	}

	public void setFixHeight(boolean b) {
		fix_height = b;
	}
}
