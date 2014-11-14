package mayday.vis3.plots.genomeviz.genomeheatmap.additional;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JScrollPane;

import mayday.core.Probe;
import mayday.genetics.basic.Species;
import mayday.vis3.ColorProvider;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.genomeviz.Organiser;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ActionMode;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTable;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.RangeModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.MenuManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.StyleSetting;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.windows.RangeSelectionSettings;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.zoom.ZoomComputations;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.ComboObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.TranslatedKey;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.MyColorProvider;
import mayday.vis3.plots.genomeviz.genomeorganisation.Chrome;
import mayday.vis3.plots.genomeviz.genomeorganisation.GenomeDAO;

public class MasterManager {
	 
	// table
	protected JScrollPane scrollPaneForHeatMap;
	protected GenomeHeatMapTable heatMapTable;
	protected GenomeHeatMapTableModel tableModel;
	protected RangeModel rangeModel;
	protected Organiser org;
	
	protected Controller c = null;
	// menues
	public MenuManager menuManager;

	protected ZoomComputations zoomComputor;

	// access to data set
	protected ViewModel viewModel;					
	protected ColorProvider colorprovider;
	

	public MasterManager(){
		this.zoomComputor = new ZoomComputations(this);

	}
	
	/**
	 * Sets the JTable.
	 * @param heatMapTable
	 */
	public void setChromeHeatMapTable(GenomeHeatMapTable heatMapTable){
		
		this.heatMapTable = heatMapTable;
	}
	
	public GenomeHeatMapTable getTable(){
		return this.heatMapTable;
	}
	
	public GenomeDAO getDAO() {
		return org.getChromeManager();
	}
	
	/**
	 * sets the table model in master manager.
	 * @param tableModel
	 */
	public void setTableModel(GenomeHeatMapTableModel tableModel) {
		this.tableModel = tableModel;
		setRangeModel(this.tableModel.getRangeModel());
	}
	
	public GenomeHeatMapTableModel getTableModel(){
		return this.tableModel;
	}
	
	protected void setRangeModel(RangeModel rangeModel){
		this.rangeModel = rangeModel;
	}
	
	public RangeModel getRangeModel(){
		return this.rangeModel;
	}
	/**
	 * Sets the JScrollPane which contains the JTable.
	 * @param scrollPaneForHeatMap
	 */
	public void setScrollPane(JScrollPane scrollPaneForHeatMap){
		this.scrollPaneForHeatMap = scrollPaneForHeatMap;
	}
	
	public void setController(Controller c){
		this.c = c;
	}
	
	public Controller getController(){
		return this.c;
	}

	public RangeSelectionSettings getRs(){
		if(menuManager!=null){
			return menuManager.getRss();
		}
		return null;
	}


	//#############################################################
	/*
	 * Operations on ScrollPane
	 *  
	 */
	//#############################################################

	
	//#############################################################
	/*
	 * Executed methods on computationClass
	 * 
	 */
	//#############################################################
	
	/**
	 * returns the cellnumber for row and column, lowest cellnumber is 1.
	 * @param row
	 * @param col
	 * @return cellnumber for row,column
	 */
	
	//#############################################################
	//						ACCESS TO DATA
	//#############################################################
	/*                     
	 * 
	 * Operations on ActualChromeData
	 * 					
	 */
	//#############################################################

	/**
	 * returns all Probes contained in cellNumber, no matter if forward or backward probe and no
	 * matter which strand.
	 * @param cellnumber
	 * @return LinkedList of all probes both strands contained in cell/cellrange
	 */
	public List<Probe> getAllProbes(int cellnumber) {
		return tableModel.getAllProbes(cellnumber);
	}
	
	
	public long getStartPosition(Probe probe){
		return tableModel.getStartPosition(probe);
	}

	public long getEndPosition(Probe probe){
		return tableModel.getEndPosition(probe);
	}
	
	//#############################################################
	/*
	 * Operations on ToolTipManager
	 * 
	 */
	//#############################################################
	
	//#############################################################
	/*
	 * Operations on ZoomManager
	 * 
	 */
	//#############################################################
	public TranslatedKey getTranslatedKey(int key ,int originalNumberOfCells){

		return zoomComputor.getTranslatedKeyPair(key, originalNumberOfCells);
	}

	//#############################################################
	/*
	 * Operations on TableOrganiser
	 * 
	 */
	//#############################################################

	public int getNumberOfColumns(){
		return tableModel.getColumnCount();
	}
	
	public int getNumberOfUnusedColumns(){
		return tableModel.getNumberOfUnusedColumns();	
	}
	
	public int getNumberOfFrontUnusedColumns(){
		return tableModel.getNumberOfFrontUnusedColumns();
	}
	
	public int getNumberOfBackUnusedColumns(){
		return tableModel.getNumberOfBackUnusedColumns();
	}

	public int getNumberOfRows(){
		return tableModel.getRowCount();
	}
	
	public SplitView getSplitView(){
		return tableModel.getSplitView();
	}

	public Integer getZoomMultiplikator(){
		return tableModel.getZoomMultiplikator();
	}
	
	// get and set the zoom level
	public ZoomLevel getZoomLevel(){
		return tableModel.getZoomLevel();
	}
	
	public KindOfChromeView getKindOfChromeView(){
		return this.tableModel.getKindOfChromeView();
	}
	
	public void setKindOfChromeView(KindOfChromeView view){
		this.tableModel.setKindOfChromeView(view);
	}

	public String getSpeciesAndChromeName() {
		return "Species: " + tableModel.getSelectedSpecies().getName() + " Chromosome: " + tableModel.getSelectedChrome().getId();
	}

	public long getHighestChromosomePosition(){
		return tableModel.getOriginalSizeOfChromosome_Whole();
	}
	
	//#############################################################
	/*
	 * Operations on HeatMap Table
	 * 
	 */
	//#############################################################
	
	public int getColumnCount(){
		return heatMapTable.getColumnCount();
	}
	
	public void applyBoxSize(){
		heatMapTable.applyBoxSize();
	}
	public int rowAtPoint(Point p){
		return heatMapTable.rowAtPoint(p);
	}
	
	public int columnAtPoint(Point p){
		return heatMapTable.columnAtPoint(p);
	}
	
	public void setIntercellSpacingFromTable(int dim){
		heatMapTable.setIntercellSpacing(new Dimension(dim,dim));
	}
	
	public void repaintTable(){
		heatMapTable.repaint();
	}
	//#############################################################
	/*
	 * Operations on ViewModel
	 * 
	 */
	//#############################################################

	
	public ViewModel getViewModel(){
		return this.viewModel;
	}
	
	//#############################################################
	/*
	 * Operations on ChromeManager
	 * 
	 */
	//#############################################################
//	public ChromosomeSetContainer getChromeSetContainer(){
//		return org.getChromeManager().getChromeSetContainer();
//	}
	
	public Species getSelectedSpecies(){
		
		return tableModel.getSelectedSpecies();
		
	}
	
	public Chrome getSelectedChrome(){
		return tableModel.getSelectedChrome();
	}
	
	public void setTempSelectedChrome(Chrome chrome){
		tableModel.setTempSelectedChrome(chrome);
	}
	
	public Chrome getTempSelectedChrome(){
		return tableModel.getTempSelectedChrome();
	}
	
	public Species getTempSelectedSpecies(){
		return tableModel.getTempSelectedSpecies();
	}
	
//	public void setSelectedChromePosition(int chromePosition) {
//		tableModel.setSelectedChromePosition(chromePosition);
//	}
	
	public int getSelectedChromePosition() {
		return tableModel.getSelectedChromePosition();
	}

	public void setProbeToFind(Probe probe) {
		tableModel.setProbeToFind(probe);
	}

	public Probe getProbeToFind() {
		return tableModel.getProbeToFind();
	}
	
	//#############################################################
	/*
	 * Operations on MenuManager
	 * 
	 */
	//#############################################################
	public JMenu setChromeViewMenu(){
		return menuManager.setChromeViewMenu();
	}
	
	public JMenu setSpecChrSelMenu(){
		return menuManager.setSpecChrSelMenu();
	}
	
	public JMenu findProbe_window(){
		return menuManager.findProbe_window();
	}
	
	public JMenu selectRangeOfProbesForNewWindow(){
		return menuManager.selectRangeOfProbesForNewWindow();
	}
	
	public void setZoomViewButtons(ZoomLevel level){
		menuManager.setZoomLevelButtons(level);
	}
	
	public void enableZoomView_FitButton(){
		menuManager.enableZoomView_FitButton();
	}
	
	public void disableZoomView_FitButton(){
		menuManager.disableZoomView_FitButton();
	}
	
	public void setFitLabel(Integer zoomMultiplikator) {
		menuManager.setFitLabel(zoomMultiplikator);
		
	}

	public void setFromPosition_RangeSelection(int val) {
		this.rangeModel.setFromPosition_RangeSelection(val);
	}

	public long getToPosition_RangeSelection() {
		return rangeModel.getToPosition_RangeSelection();
	}

	public long getFromPosition_RangeSelection() {
		return rangeModel.getFromPosition_RangeSelection();
	}

	public void setToPosition_RangeSelection(int val) {
		rangeModel.setToPosition_RangeSelection(val);
	}

	public long getEndPositionOfChromosome() {
		return tableModel.getViewEnd();
	}

	public long getStartPositionOfChromosome() {
		return tableModel.getViewStart();
	}

	public TreeMap<Integer,ComboObject> getWindowRanges() {
		return tableModel.getWindowRanges();
	}

	public void initializeRangeOfWindow() {
		this.menuManager.initializeRangeOfWindow();
	}

	public void enableCondensedView(boolean b) {
		menuManager.enableCondensedView(b);
	}

	public boolean isCondensedViewAvailable() {
		return tableModel.isCondensedViewAvailable();
	}

//	public ZoomingSetting getZoomingSetting() {
//		return menuManager.getZoomingSetting();
//	}

	public StyleSetting getStyleSetting(){
		if(menuManager!=null){
			return menuManager.getStyleSetting();
		}
		return null;
	}
	
	public void setOrganiser(Organiser Org) {
		org = Org;
		c.setOrganiser(org);
	}

	public void init(Organiser Org, ViewModel ViewModel,
			MyColorProvider Coloring) {
		setOrganiser(Org);
		viewModel = ViewModel;
		colorprovider = Coloring;
		menuManager = new MenuManager(this, this.c);
		tableModel.performAction(ActionMode.MODE_D, this.c);
	}
}
