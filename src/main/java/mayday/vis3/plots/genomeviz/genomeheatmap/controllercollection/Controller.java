package mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import mayday.core.Probe;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.ColorProvider;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.genomeviz.IController;
import mayday.vis3.plots.genomeviz.Organiser;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ActionMode;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ModelEvents;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ProbeListColoring;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTable;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.FindNearestProbe_Delegate;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.MouseZoom_Delegate;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.SearchOperations;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableComputations;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableMapper;
import mayday.vis3.plots.genomeviz.genomeheatmap.scrollpane.ChromosomeScrollPane;
import mayday.vis3.plots.genomeviz.genomeheatmap.usergestures.UserGestures;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.ChromosomeHeader;

public class Controller implements IController, ActionListener, ComponentListener, SettingChangeListener, ChangeListener, AdjustmentListener, 
MouseListener, MouseWheelListener, KeyListener, TableModelListener{
 
	protected MasterManager master;
	protected GenomeHeatMapTableModel model;
	protected JScrollPane scrollPaneForHeatMap;
	protected MouseClickingController mouseClickControll;
	protected Organiser organiser;
	protected GenomeHeatMapTable table;
	protected ChromosomeHeader headerpanel;
	
	protected boolean mousePressed = false;
	protected boolean key_I_pressed = false;
	protected Point point = null;
	
	protected Controller_hp c_hp;
	protected Controller_data c_dt;
	protected Controller_cb c_cb;
	protected Controller_zl c_zl;
//	protected Controller_settings c_set;
	protected Controller_rs c_rs;
	
	public Controller(MasterManager master, GenomeHeatMapTableModel model) {
		super();
		this.master = master;
		this.model = model;
		this.mouseClickControll = new MouseClickingController(this.master, this.model);
		c_hp = new Controller_hp(this, model);
		c_dt = new Controller_data(this, model);
		c_cb = new Controller_cb(model);
		c_zl = new Controller_zl(model, this);
//		c_set = new Controller_settings(this,(ILogixVizModel)model);
		c_rs = new Controller_rs(this,model);
	}

	protected void setScrollPane(JScrollPane scrollPaneForHeatMap){
		this.scrollPaneForHeatMap = scrollPaneForHeatMap;
	}

	protected void setTable(GenomeHeatMapTable heatMapTable) {
		this.table = heatMapTable;
	}

	public Visualizer getVisualizerFromController(){
		if(this.master!=null){
			return master.getViewModel().getVisualizer();
		}
		return null;
	}
	
	public ViewModel getViewModelFromController(){
		if(this.master!=null){
			return master.getViewModel();
		}
		return null;
	}
	
	public void actionPerformed(ActionEvent e) {

		// ZOOM Events
		if(UserGestures.ZOOM_ONE.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.one;
			zoomLevelChangedOperations(zoomLevel);
		} else if(UserGestures.ZOOM_TWO.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.two;
			zoomLevelChangedOperations(zoomLevel);
		} else if(UserGestures.ZOOM_FIVE.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.five;
			zoomLevelChangedOperations(zoomLevel);
		} else if(UserGestures.ZOOM_TEN.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.ten;
			zoomLevelChangedOperations(zoomLevel);
		} else if(UserGestures.ZOOM_FIFTEEN.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.fifteen;
			zoomLevelChangedOperations(zoomLevel);
		} else if(UserGestures.ZOOM_TWENTY.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.twenty;
			zoomLevelChangedOperations(zoomLevel);
		} else if(UserGestures.ZOOM_TWENTYFIVE.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.twentyfive;
			zoomLevelChangedOperations(zoomLevel);
		} else if(UserGestures.ZOOM_FIFTY.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.fifty;
			zoomLevelChangedOperations(zoomLevel);
		}  else if(UserGestures.ZOOM_HUNDRED.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.hundred;
			zoomLevelChangedOperations(zoomLevel);
		}  else if(UserGestures.ZOOM_TWOHUNDRED.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.twohundred;
			zoomLevelChangedOperations(zoomLevel);
		}  else if(UserGestures.ZOOM_THOUSAND.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.thousand;
			zoomLevelChangedOperations(zoomLevel);
		}  else if(UserGestures.ZOOM_TWOTHOUSAND.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.twothousand;
			zoomLevelChangedOperations(zoomLevel);
		}  else if(UserGestures.ZOOM_FIVETHOUSAND.equals(e.getActionCommand())){
			ZoomLevel zoomLevel = ZoomLevel.fivethousand;
			zoomLevelChangedOperations(zoomLevel);
		}  else if(UserGestures.ZOOM_FIT.equals(e.getActionCommand())){

			ZoomLevel zoomLevel = ZoomLevel.fit;
			zoomLevelChangedOperations_fitted(zoomLevel);
		}
		// Grid selection Event
//		else if(UserGestures.GRID.equals(e.getActionCommand())){
//			gridOperations();
//		} 
			
		else if(UserGestures.PROBEINFORMATION_FIND_WINDOW.equals(e.getActionCommand())){
			Probe pb = model.getProbeToFind();
			if(pb!=null){
				Set<Probe> set = new HashSet<Probe>();
				set.add(pb);
				if(!set.isEmpty()){
					createPropertiesWindow(set);
				}
			}
		}
		else if(UserGestures.GOTO_POSITION.equals(e.getActionCommand())){
			if(model.getFps()!=null){
				SettingsDialog sd = model.getFps().getDialog();
				if(sd != null){
					sd.showAsInputDialog();
					searchPositionInChromosome(); 
				}
			}
			//new FindChromosomePosition(master).openWindow();
		} 
		else if (UserGestures.SEARCH_CHROMOSOME_POSITION.equals(e
				.getActionCommand())) {

//			searchPositionInChromosome();
//			model.fireChanged();
		}
	}

	/**
	 * search position in chromosome and jump visible rect of table to this position.
	 */
	public void searchPositionInChromosome() {
		int chromePosition = (int) (model.getSelectedChromePosition());
		if (chromePosition != 0) {

			GenomeHeatMapTable table = master.getTable();

			// switch for whole and condensed chromosome view
			// for condensed view nearest probe is searched (nearest by startposition)
			
			int cellnumber = 0;
			
			switch(model.getKindOfChromeView()){
			case WHOLE:
				cellnumber = TableComputations.computeCellnumberOfChromePosition((int) chromePosition, model
								.getZoomMultiplikator(), (int)model.getSkipValue());
				break;
			case CONDENSED:
				// try to skip cells
				Probe pb = FindNearestProbe_Delegate.findNearestProbe(chromePosition, model);
				if(pb!= null){
					cellnumber = TableMapper.getCellnumberOfProbe(pb.getDisplayName(),model);
				}
				break;
			}
			
			if(cellnumber > 0){
				// search the cellnumber and scroll rect to visible
				model.setSearchedCell(cellnumber, true);
				SearchOperations.searchCellnumber(cellnumber, model, table);
			} else {
				System.err.println("Controller:searchPositionInChromosome - Cellnumber " + cellnumber + " not valid");
			}
		}
	}

	/**
	 * creates a new window with the properties of the selected probes.
	 * @param set
	 */
	private void createPropertiesWindow(Set<Probe> set) {
		AbstractPropertiesDialog dlg;
		dlg = PropertiesDialogFactory.createDialog(set.toArray());
		dlg.setVisible(true);
	}

	/**
	 * creates necessary buffered images and delete if necessary previous painted images.
	 * @param clear if previous painted images should be deleted
	 */
	public void scalaOperations(boolean clear) {
		switch (model.getKindOfChromeView()) {
		case WHOLE:
			model.setVisibleRows();
			if(clear){
				model.clearBufferedImages();
				model.updateScalaTicks();
			}
			model.createScalaImagesForRange();
			break;
		default:
			model.clearBufferedImages();
		}
	}

	/**
	 * 
	 * @param level
	 */
	public void zoomLevelChangedOperations(ZoomLevel level){
		boolean changed = model.setZoomLevel(level);
		
		if(changed){
			model.setZoomMultiplikator(model.computeMultiplikator(scrollPaneForHeatMap.getViewport().getHeight()));
			model.performAction(ActionMode.MODE_A, this);
			repaintVisPosInHeader();
		}
	}
	
	public void zoomLevelChangedOperations(ZoomLevel level, boolean val){
		model.setZoomLevel(level);
		
		if(val){
			model.setZoomMultiplikator(model.computeMultiplikator(scrollPaneForHeatMap.getViewport().getHeight()));
			model.performAction(ActionMode.MODE_A, this);
			repaintVisPosInHeader();
		}
	}
	
	public void zoomLevelChangedOperations_fitted(ZoomLevel level){

		if(model.getKindOfChromeView().equals(KindOfChromeView.WHOLE)){
			int multiplikator = model.getActualFitMultiplikator_Whole();
			model.setZoomMultiplikator(multiplikator);
		} else if (model.getKindOfChromeView().equals(KindOfChromeView.CONDENSED)){
			int multiplikator = model.getActualFitMultiplikator_Condensed();
			model.setZoomMultiplikator(multiplikator);
		}

		model.setZoomLevel(level);
		// For Threads
		model.setFittedCondensedData();
		model.setFittedWholeData();

		model.performAction(ActionMode.MODE_A, this);
	}
	
	private void splitViewChangedOperations(SplitView view) {

		boolean changed = model.setSplitView(view);
		if(changed){
			model.fireChanged();
		}
	}

	public void chromeViewChangedOperations(KindOfChromeView view) {

		model.setKindOfChromeView(view);
		model.performAction(ActionMode.MODE_E, this);
	}

	//#############################################################
	/*
	 * Events from ComponentListener
	 * 
	 */
	//#############################################################

	public void componentHidden(ComponentEvent arg0) {

	}

	public void componentMoved(ComponentEvent arg0) {

	}

	public void componentResized(ComponentEvent arg0) {
		if(this.organiser!=null
				&& organiser.containsLoci()){
			model.performAction(ActionMode.MODE_B, this);

			if(model.getOrganiser().isDataSetted()){
				newThreads();
				setFitLabel();
			}
			
			if(headerpanel!=null){
				headerpanel.repositionButtons();
			}
		}
	}

	/**
	 * sets the fit label.
	 */
	private void setFitLabel() {
		KindOfChromeView view = model.getKindOfChromeView();
		if(view.equals(KindOfChromeView.WHOLE))
			master.setFitLabel(model.getActualFitMultiplikator_Whole());
		else if(view.equals(KindOfChromeView.CONDENSED))
			master.setFitLabel(model.getActualFitMultiplikator_Condensed());
	}

/**
 *  compute fit-multiplikator for whole and condensed chromosome view, check if fit-multiplikators changed, if so
 *  stop all active threads and start new threads for new fit-multiplikator.
 */
	private void newThreads() {
		int fitMultiplikator_Whole = model.computeFittedMultiplikator_Whole(this.scrollPaneForHeatMap.getViewport().getHeight());
		int fitMultiplikator_Condensed = model.computeFittedMultiplikator_Condensed(this.scrollPaneForHeatMap.getViewport().getHeight());
		
		if(model.getActualFitMultiplikator_Whole() != fitMultiplikator_Whole){
			model.setActualFitMultiplikator_Whole(fitMultiplikator_Whole);
			model.computeFittedWholeData(model.getActualFitMultiplikator_Whole());
		}
		
		if(model.getActualFitMultiplikator_Condensed() != fitMultiplikator_Condensed){
			model.setActualFitMultiplikator_Condensed(fitMultiplikator_Condensed);
			model.computeFitteCondensedData(model.getActualFitMultiplikator_Condensed());
		}
	}

	public void componentShown(ComponentEvent arg0) {

	}

	//#############################################################
	/*
	 * Events from ChangeListener
	 * change event occurs when colorprovider fires event
	 * 
	 */
	//#############################################################
	/**
	 * event occurs if colorprovider fires event.
	 */
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() instanceof ColorProvider){
			ColorProvider cp = (ColorProvider)e.getSource();
			model.setExperimentNumber(cp.getExperiment());
			model.setModelEvent(ModelEvents.UPDATE);
			model.fireChanged();
			master.getTable().repaint();				
		} 
	}
	
	public void stateChanged(SettingChangeEvent e) {
		model.setStyle(master.getStyleSetting().getStyle(),this);
	}
	
	//#############################################################
	/*
	 * Events from Adjustment Listener from vertical scroll bar
	 * 
	 */
	//#############################################################
	/**
	 * Event occurs if vertical scrollbar is moved.
	 * 
	 */
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if(this.organiser!=null
				&& organiser.containsLoci()){
			switch (e.getAdjustmentType()) {
			case AdjustmentEvent.UNIT_INCREMENT:
				break;
			case AdjustmentEvent.UNIT_DECREMENT:
				break;
			case AdjustmentEvent.BLOCK_DECREMENT:
				break;
			case AdjustmentEvent.BLOCK_INCREMENT:
				break;
			case AdjustmentEvent.TRACK:
				break;
			}
			
			model.repositionChromeHeaderInScrollPane();
			repaintVisPosInHeader();
			scalaOperations(false);
		}
	}

	private void repaintVisPosInHeader() {
		model.setVisiblePositions();
		if(headerpanel!=null){
			headerpanel.repaint();
		}
	}

	
	//#############################################################
	/*
	 * Events from Mouse Listener
	 * 
	 */
	//#############################################################
	public void mouseClicked(MouseEvent e) {
		// deselect searched probes
		model.searchForProbe(false);
		model.searchForChromePosition(false);

		if (e.getButton() == MouseEvent.BUTTON1) {
			int row = master.rowAtPoint(e.getPoint());
			int column = master.columnAtPoint(e.getPoint());
			this.mouseClickControll.mouseSelection(row, column, e);
		} 
		else if (e.getButton() == MouseEvent.BUTTON3) {

			openPropertiesWindow(e);
		}
	}


	private void openPropertiesWindow(MouseEvent e) {
		int row = master.rowAtPoint(e.getPoint());
		int column = master.columnAtPoint(e.getPoint());

		// get strand of actual row/column
		StrandInformation strand = TableMapper.getStrand(row,
				model.getRowCount());
		if (strand.equals(StrandInformation.MINUS)
				|| strand.equals(StrandInformation.PLUS)) {
			int predecessorRowsOfStrand = TableComputations.computePredecessorsRowsOfStrand(row, strand);

			// get cellNumber
			int cellnumber = TableMapper.getCellNumber(row,
					column, model, predecessorRowsOfStrand);

			Set<Probe> actualClickedProbes = new HashSet<Probe>();

			if (column != model.getColumnCount() - 1 && column != 0) {
				// get probes in cell
				List<Probe> list = model
						.getAllProbes_DependingOnStrand(strand, cellnumber);

				if (list != null) {
					actualClickedProbes.addAll(list);
				}
			}
			if (!actualClickedProbes.isEmpty()) {

				createPropertiesWindow(actualClickedProbes);
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {

	}

	public void mouseExited(MouseEvent arg0) {
		
	}

	public void mousePressed(MouseEvent e) {
		mousePressed = true;
		point = e.getPoint();
	}

	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
	}

	//#############################################################
	/*
	 * Events from Mouse Wheel Listener
	 * 
	 */
	//#############################################################

	public void mouseWheelMoved(MouseWheelEvent e) {

		// check if CTRL is being pressed
		if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) {
			// check if height must be changed, resizeWidth is false if shift is being pressed
			boolean resizeWidth = !((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK);
			// get number of "clicks"
			if (e.getWheelRotation() > 0) {
				// in/out-zoom | change width | change height
				mouseZoom(false, resizeWidth, true);
			} else {
				mouseZoom(true, resizeWidth, true);
			}
			model.performAction(ActionMode.MODE_B, this);
			
			if(model.getOrganiser().isDataSetted()){
				newThreads();
				setFitLabel();
			}
		}
	}
	
	/** 
	 * For zooming of boxes.
	 * @param inZoom in-zoom or out-zoom
	 * @param resizeWidth if width is resized
	 * @param resizeHeight if height is resized
	 */
	public void mouseZoom(boolean inZoom, boolean resizeWidth, boolean resizeHeight){
		
		int boxSizeX = MouseZoom_Delegate.execute_boxSizeX(inZoom, resizeWidth, resizeHeight, 
				model.getTableSettings().getBoxSizeX(), model.getMinZoom(), model.getMaxZoom());
		
		int boxSizeY = MouseZoom_Delegate.execute_boxSizeY(inZoom, resizeWidth, resizeHeight, 
				model.getTableSettings().getBoxSizeY(), model.getMinZoom(), model.getMaxZoom());;
		
		model.getTableSettings().setBoxSizeXY(boxSizeX, boxSizeY);
	}

	//#############################################################
	/*
	 * Events from Key Listener
	 * 
	 */
	//#############################################################
	public void keyPressed(KeyEvent e) {

		// is ControlDown being pressed?
		if (e.isControlDown()) {
			// check if change height, resizeWidth is false
			// if shift pressed
			boolean resizeWidth = !e.isShiftDown();
			if (e.getKeyCode() == KeyEvent.VK_PLUS) {
				mouseZoom(true, resizeWidth, true);
			}
			if (e.getKeyCode() == KeyEvent.VK_MINUS) {
				mouseZoom(false, resizeWidth, true);
			}
		}

		if(e.getKeyCode() == KeyEvent.VK_I){
			key_I_pressed = true;
		}
	}

	public void keyReleased(KeyEvent arg0) {
		key_I_pressed = false;
	}

	public void keyTyped(KeyEvent e) {
		
	}


	//#############################################################
	/*
	 * Events from Item Listener
	 * 
	 */
	//#############################################################

	public void probeSearched(boolean b, Probe probe, int row, int column) {
		model.searchForProbe(true);
		model.setFoundProbe(probe, row, column);
		model.fireChanged();
	}

	protected void setHeaderPanel(ChromosomeHeader chromeHeader) {
		headerpanel = chromeHeader;
	}

	public Controller_hp getC_hp() {
		return c_hp;
	}

	public Controller_data getC_dt() {
		return c_dt;
	}

	public Controller_cb getC_cb() {
		return this.c_cb;
	}

	public Controller_zl getC_zl() {
		return this.c_zl;
	}

//	public Controller_settings getC_set() {
//		return c_set;
//	}

	public Controller_rs getC_rs() {
		return c_rs;
	}
	
	public void jumpToData(long pos) {
		c_dt.jumpToData(pos);
		model.repositionChromeHeaderInScrollPane();
		repaintVisPosInHeader();
	}

	// TableModel listener
	public void tableChanged(TableModelEvent e) {
		this.master.enableCondensedView(model.isCondensedViewAvailable());
	}

	public void setChromeViewSettings(ProbeListColoring probeListColoring, SplitView splitView, KindOfChromeView kindOfChromeView){
		if(!this.model.getKindOfChromeView().equals(kindOfChromeView)){
			chromeViewChangedOperations(kindOfChromeView);
			setFitLabel();
		}
		
		if(!this.model.getSplitView().equals(splitView)){
			splitViewChangedOperations(splitView);
		}
		
		if(!model.getPbListColoring().equals(probeListColoring)){
			model.setPbListColoring(probeListColoring);
		}
	}

	public void setOrganiser(Organiser Org) {
		organiser = Org;
	}

	public void init(ChromosomeScrollPane scrollPaneForHeatMap, GenomeHeatMapTable heatMapTable, ChromosomeHeader chromosomeHeader) {
		setScrollPane(scrollPaneForHeatMap);
		setTable(heatMapTable);
		setHeaderPanel(chromosomeHeader);
	}
}
