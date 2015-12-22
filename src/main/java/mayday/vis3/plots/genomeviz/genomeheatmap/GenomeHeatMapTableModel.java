package mayday.vis3.plots.genomeviz.genomeheatmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.event.ChangeEvent;

import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.genetics.basic.Strand;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.genomeviz.AbstractLogixVizModel;
import mayday.vis3.plots.genomeviz.FindPositionSettings;
import mayday.vis3.plots.genomeviz.Organiser;
import mayday.vis3.plots.genomeviz.EnumManager.RANGE_OF_CHROMOSOME;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ActionMode;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ClickedSelection;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.GHMStyle;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ModelEvents;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ProbeListColoring;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.datathreads.DataComputationStorage;
import mayday.vis3.plots.genomeviz.genomeheatmap.datathreads.ThreadManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.datathreads.WorkOnData;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.CheckTranslatedKeys_Delegate;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableComputations;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableMapper;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.StyleSetting;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.ClickedProbes;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.ComboObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.TranslatedKey;
import mayday.vis3.plots.genomeviz.genomeheatmap.probeinformation.ProbeInformation;
import mayday.vis3.plots.genomeviz.genomeheatmap.scrollpane.ChromosomeScrollPane;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.ImageModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.MyColorProvider;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.BackwardPanel;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.ForwardPanel;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.ScalaBufferedImage;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeDataSet;
import mayday.vis3.plots.genomeviz.genomeoverview.ConstantData;
 
@SuppressWarnings("serial")
public class GenomeHeatMapTableModel extends AbstractLogixVizModel{

	protected MasterManager master;
	protected ThreadManager threadManager;
	
	protected ImageModel imageModel = null;
	protected RangeModel rangeModel = null;
	protected DataComputationStorage dcs = null;

	protected FindPositionSettings fps = null;

	protected boolean settedChromeData = false;

	protected ProbeInformation probeInformation = null;

	// for zooming
	protected final int MINZOOM=4;
	protected final int MAXZOOM=45;
	protected int necessaryRowsLongestStrand = 0;
	protected boolean necessaryRowsChanged = false;

	protected KindOfChromeView kindOfChromeView;
	protected boolean chromeViewChanged = false;
	
	protected SplitView splitView;
	
	protected ZoomLevel zoomLevel;

	protected boolean gridVisible = false;
	
	protected ClickedProbes cell = null;
	protected ClickedSelection selection = ClickedSelection.ALL;
	public int clickedProbeNumber = 0;
	public int clickedProbeIndex = 0;

	protected ModelEvents modelEvent = null;

	protected Integer zoomMultiplikator = null;
	protected boolean zoomMultiplikatorChanged = false;
	
	protected boolean actualFitMultChanged_Condensed = false;	
	protected Integer actualFitMultiplikator_Condensed = -1;	// actual computed fitMultiplikator for condensed chromosome view
	
	protected boolean actualFitMultChanged_Whole = false;
	protected Integer actualFitMultiplikator_Whole = -1;		// actual computed fitMultiplikator for whole chromosome view 
																// depending on window size for this multiplikator actual thread is running
	
	protected boolean searchProbe = false;
	protected Probe foundProbe = null;
	//protected int selectedChromePosition = 0;				// position in chromosome to find
	protected int searchedCellnumber = 0;
	protected boolean searchChromePosition = false;

	protected boolean fit_WholePrbSetted = false;
	protected boolean fit_CondensedPrbSetted = false;
	protected boolean waitForWholeFit = true;
	protected boolean waitForCondensedFit = true;
	
	protected boolean condensedViewAvailable = true;
	public boolean startThread = true;

	protected int previousExperimentNumber = 0;
	protected int actualExperimentNumber = 0;

	protected TreeMap<Integer,ComboObject> rangesOfWindow = null;
	LinkedList<ZoomLevel> zoomMultiplikators = new LinkedList<ZoomLevel>();
	
	protected ProbeListColoring pbListColoring = ProbeListColoring.COLOR_HIGHEST_PROBELIST;


	protected TableSettings ts;
	private ChromosomeScrollPane scrollPane;
	private boolean initialized = false;
	
	public GenomeHeatMapTableModel(MasterManager Master){
		super("GenomeHeatMapTableModel");
		ts = new TableSettings(this);
		master = Master;
		imageModel = new ImageModel(this);
		rangeModel = new RangeModel(this,master);
		probeInformation = new ProbeInformation(master);
		System.out.println("ChromeHeatMapTableModel Constructor");
		
		kindOfChromeView = KindOfChromeView.WHOLE;
		splitView = SplitView.mean;
		zoomLevel = ZoomLevel.one;
		zoomMultiplikator = 1;
		
		threadManager = new ThreadManager(this);
		
		fillZoomMultiplikatorsList();

		dcs = new DataComputationStorage(this);
	}

	private void fillZoomMultiplikatorsList() {
		zoomMultiplikators.add(ZoomLevel.one);
		zoomMultiplikators.add(ZoomLevel.two);
		zoomMultiplikators.add(ZoomLevel.five);
		zoomMultiplikators.add(ZoomLevel.ten);
		zoomMultiplikators.add(ZoomLevel.fifteen);
		zoomMultiplikators.add(ZoomLevel.twenty);
		zoomMultiplikators.add(ZoomLevel.twentyfive);
		zoomMultiplikators.add(ZoomLevel.fifty);
		zoomMultiplikators.add(ZoomLevel.hundred);
		zoomMultiplikators.add(ZoomLevel.twohundred);
		zoomMultiplikators.add(ZoomLevel.thousand);
		zoomMultiplikators.add(ZoomLevel.twothousand);
		zoomMultiplikators.add(ZoomLevel.fivethousand);
		//zoomMultiplikators.add(ZoomLevel.fit);
	}	
	
	public Organiser getOrganiser(){
		return org;
	}

	/**
	 * data is setted in ChromeManager, only if data note yet setted.
	 * @param Org 
	 * @param coloring 
	 * @param probes
	 * @param viewModel
	 */
	public void initializeData(ViewModel ViewModel, Organiser Org, MyColorProvider coloring) {
		super.initAbstractModel(ViewModel, Org);
		rangeModel.setOrganiser(org);
		scrollPane.initScrollPane();
		
		master.init(org, viewModel, coloring);
		setInitialized(true);
	}
	
	public void setFoundProbe(Probe probe, int row, int column) {
		this.foundProbe = probe;
	}
	
	public Probe getFoundProbe(){
		if(foundProbe != null)return this.foundProbe;
		else return null;
	}
	
	public void searchForProbe(boolean val){
		this.searchProbe = val;
	}
	
	public boolean searchForProbe(){
		return this.searchProbe;
	}
	
	public void setModelEvent(ModelEvents event){
		this.modelEvent = event;
	}
	
	public ModelEvents getModelEvent(){
		return this.modelEvent;
	}
	
	public void resetClickedSelection(){
		selection = ClickedSelection.ALL;
	}
	
	public void setClickedSelection(){

		switch(this.selection){
		case ALL:
			this.selection = ClickedSelection.NONE;
			break;
		case NONE:
			this.selection = ClickedSelection.SINGLE;
			this.clickedProbeNumber = cell.getProbes().size();
			this.clickedProbeIndex = 0;
			break;
		case PENDING:
			this.selection = ClickedSelection.ALL;
			break;
		}
	}
	
	public void updateClickedProbeIndex(){
		clickedProbeIndex++;
	}
	
	public void setClickedSelection(ClickedSelection sel){
		selection = sel;
	}
	
	public ClickedSelection getClickedSelection(){
		return selection;
	}
	
	public void setPreviousClickedCell(ClickedProbes pair){
		cell = pair;
	}
	
	public ClickedProbes getPreviousClickedCell(){
		return this.cell;
	}
	
	public void setGridVisible(boolean val){
		gridVisible = val;
	}
	
	public boolean getGridVisible(){
		return gridVisible;
	}
	
	// define number of columns 
	// columns of probes AND unused columns
	public int getColumnCount() {
		ts.numberOfColumns = ts.getNumberOfBoxesEachRow() + getNumberOfUnusedColumns() ;
		return ts.numberOfColumns;
	}

	// defines the number of rows
	public int getRowCount() {
		ts.numberOfRows = ts.getNecessaryRowsAllStrands() + getNumberOfBackUnusedRows();
		return ts.numberOfRows;
	}

	// get value for each cell
	public CellObject getValueAt(int row, int col) {
		//
		// get strand of actual row/column
		StrandInformation strand = TableMapper.getStrand(row, getRowCount());
		int predecessorRowsOfStrand = TableComputations.computePredecessorsRowsOfStrand(row, strand);
		// get cellNumber
		int cellNumber = TableMapper.getCellNumber(row, col, this,predecessorRowsOfStrand);

		List<Probe> probeList = new LinkedList<Probe>();
		
		if (col == 0){
			
			return new CellObject(probeList,strand,cellNumber);
		}
		
		probeList = getAllProbes_DependingOnStrand(strand, cellNumber);

		CellObject probesInf = new CellObject(probeList,strand,cellNumber);
		return (probesInf == null) ? null : probesInf;
	}
	
	/**
	 * fires if data from table changed.
	 */
	public void tableStructureChanged() {
		fireTableStructureChanged();
	}
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public void setValueAt(Object value, int row, int col) {
		return;
	}
	
	public void setOriginalNumberOfCells(){
		ts.setOriginalNumberOfCells();
	}
	
	public int getNumberOfFrontUnusedColumns(){
		return Const.FRONT_UNUSEDCOLUMNS;
	}
	
	public int getNumberOfBackUnusedColumns(){
		return Const.BACK_UNUSEDCOLUMNS;
	}
	
	public int getNumberOfUnusedColumns(){
		return Const.UNUSEDCOLUMNS;
	}

	public int getNumberOfFrontUnusedRows(){
		return Const.FRONT_UNUSEDROWS;
	}
	
	public int getNumberOfBackUnusedRows(){
		return Const.BACK_UNUSEDROWS;
	}
	
	public int getNumberOfUnusedRows(){
		return Const.UNUSEDROWS;
	}
	
//	public int getNumberOfNecessaryCells(){
//		return ts.numberOfNecessaryCells;
//	}
	
	public int computeMultiplikator(int heightOfViewport){
		int multiplikator = -1;
		multiplikator = TableComputations.getZoomMultiplikator(zoomLevel);
		return multiplikator;
	}

	public int computeFittedMultiplikator_Whole(int heightOfViewport) {
		
		int multiplikator;
		int windowFittedRowsOneStrand = TableComputations.computeFittedRowsOneStrand(heightOfViewport, ts.getBoxSizeY());

		multiplikator = TableComputations.computeFitMultiplikator(windowFittedRowsOneStrand,
				ts.numberOfBoxesEachRow, getOriginalSizeOfChromosome_Whole());
		return multiplikator;
	}
	
	public int computeFittedMultiplikator_Condensed(int heightOfViewport) {
		int multiplikator;
		int windowFittedRowsOneStrand = TableComputations.computeFittedRowsOneStrand(heightOfViewport, ts.getBoxSizeY());

		multiplikator = TableComputations.computeFitMultiplikator(windowFittedRowsOneStrand,
				ts.numberOfBoxesEachRow, getOriginalSizeOfChromosome_Condensed());
		return multiplikator;
	}

	public void setZoomMultiplikator(Integer val){
		if(this.zoomMultiplikator != val){
			this.zoomMultiplikator = val;
			zoomMultiplikatorChanged = true;
		} else {
			zoomMultiplikatorChanged = false;
		}
	}
	
	public Integer getZoomMultiplikator(){
		return zoomMultiplikator;
	}
	
	public void setActualFitMultiplikator_Whole(Integer val){
		if(this.actualFitMultiplikator_Whole != val){
			this.actualFitMultiplikator_Whole = val;
			actualFitMultChanged_Whole = true;
		} else{
			actualFitMultChanged_Whole = false;
		}
	}
	
	public int getActualFitMultiplikator_Whole(){
		return this.actualFitMultiplikator_Whole;
	}
	
	public void setActualFitMultiplikator_Condensed(Integer val){
		if(this.actualFitMultiplikator_Condensed != val){
			this.actualFitMultiplikator_Condensed = val;
			this.actualFitMultChanged_Condensed = true;
		} else{
			this.actualFitMultChanged_Condensed = false;
		}
	}
	
	public int getActualFitMultiplikator_Condensed(){
		return this.actualFitMultiplikator_Condensed;
	}
	
	public int getMinZoom(){
		return this.MINZOOM;
	}
	
	public int getMaxZoom(){
		return this.MAXZOOM;
	}

	public KindOfChromeView getKindOfChromeView(){
		return kindOfChromeView;
	}
	
	public void setKindOfChromeView(KindOfChromeView view){
		if(this.kindOfChromeView != view){
			this.chromeViewChanged = true;
			this.kindOfChromeView = view;
		} else {
			this.chromeViewChanged = false;
		}
	}
	
	/**
	 * Sets the actual splitView.
	 * @param split
	 */
	public boolean setSplitView(SplitView split){
		if(this.splitView != split){
			this.splitView = split;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * returns the actual splitView.
	 * @return
	 */
	public SplitView getSplitView(){
		return this.splitView;
	}
	
	/**
	 * 
	 * @param level
	 */
	public boolean setZoomLevel(ZoomLevel level){
		System.out.println("SET ZOOMLEVEL " + level);
		if(zoomLevel != level){
			zoomLevel = level;
			this.master.setZoomViewButtons(zoomLevel);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ZoomLevel getZoomLevel(){
		return this.zoomLevel;
	}

	//#############################################################
	/*
	 * Connection to Data
	 * 
	 */
	//#############################################################
	/**
	 * returns all probes depending on strand for cellnumber, first cellnumber is translated depending on zoomlevel
	 * and then all probes for translated cellnumbers is returned.
	 * @param strand
	 * @param cellNumber
	 * @return
	 */
	public List<Probe> getAllProbes_DependingOnStrand(StrandInformation strand, int cellNumber) {
		
		if (cellNumber <= 0) {
			System.err
					.println("ChromeHeatMapTable: getTheCorrectProbe - probePosition has not been setted. probePosition: "
							+ cellNumber);
		}
		// check if cellNumber is higher then number of necessary cells
		else if (cellNumber > ts.getNumberOfNecessaryCells()) {
			return Collections.emptyList();
		}

		if(strand == StrandInformation.PLUS){
			return actualChromeData.getProbes(cellNumber, ts.originalNumberOfCells, Strand.PLUS, zoomLevel, master, kindOfChromeView);
		} else if(strand == StrandInformation.MINUS){
			return actualChromeData.getProbes(cellNumber, ts.originalNumberOfCells, Strand.MINUS, zoomLevel, master, kindOfChromeView);
		} 
		else return Collections.emptyList();
	}


	/**
	 * returns all Probes contained in cellNumber, no matter if forward or backward probe and no
	 * matter which strand.
	 * @param cellnumber
	 * @return LinkedList of all probes both strands contained in cell/cellrange
	 */
	public List<Probe> getAllProbes(int cellnumber) {
		
		TranslatedKey transKey = master.getTranslatedKey(cellnumber,ts.getOriginalNumberOfCells());
		
		if(transKey == null) return Collections.emptyList();
		
		long translatedKeyLow = transKey.getTranslatedKeyFirst();
		long translatedKeyHigh = transKey.getTranslatedKeyLast();
		translatedKeyHigh = CheckTranslatedKeys_Delegate.execute(translatedKeyLow,
				translatedKeyHigh, ts.getOriginalNumberOfCells());

		if(kindOfChromeView == KindOfChromeView.WHOLE){
				return actualChromeData.getAllProbes_Whole(transKey);
		} else{
				return actualChromeData.getAllProbes_Condensed(transKey);
		}
	}
	
	
	
	/**
	 * return the size of the actual shown data, if complete chromosome is shown, so length of chromosome is used
	 * else (for range selected data) the length is computed by start-/endposition of first/last probe.
	 * @return length of actual shown data
	 */
	public int getChromosomeSize() {
		int size = 0;
		if(this.actualChromeData != null){
			size = actualChromeData.getViewLength(getKindOfChromeView());
		} else {
			System.err.println("MasterManager:getSizeWholeChrome - ChromeData not initialized");
		}
		return size;
	}
	
	public int getOriginalSizeOfChromosome_Whole(){
		if(this.actualChromeData == null){
			System.err.println("MasterManager:getSizeWholeChrome - ChromeData not initialized");
			return 0;
		}
		return (int)this.actualChromeData.getViewLength(KindOfChromeView.WHOLE);
	}
	
	public int getOriginalSizeOfChromosome_Condensed(){
		return actualChromeData.getViewLength(KindOfChromeView.CONDENSED);
	}
	
	//#############################################################
	/*
	 * For Species selection
	 * 
	 */
	//#############################################################
	
	/**
	 * 
	 * @param b
	 */
	public void actualChromeDataSetted(boolean b) {
		this.settedChromeData = b;
	}
	
	/**
	 * 
	 * @param fitMultiplikator
	 */
	public void computeFittedWholeData(int fitMultiplikator){
		
		if (settedChromeData) {
			// set zoomLevel button disabled
			waitForWholeFit(true);

			this.threadManager.computeFittedWholeData(this.actualChromeData,fitMultiplikator);
		}
	}
	
	/**
	 * 
	 * @param fitMultiplikator
	 */
	public void computeFitteCondensedData(int fitMultiplikator){
		
		if (settedChromeData) {

			// set zoomLevel button disabled
			waitForCondensedFit(true);
			
			this.threadManager.computeFittedCondensedData(actualChromeData, fitMultiplikator);
		}
	}
	
	/**
	 * sets the data of this plot depending on chromosome, happens if new window for another chomosome is choosen or
	 * if another chomosome is choosen in the same window.
	 * @param actualChromeData
	 */
	protected void internalSettingOfActulaData() {
		
		if(actualChromeData.getViewLength(KindOfChromeView.CONDENSED) == 0){
			condensedViewAvailable = false;
		} else {
			condensedViewAvailable = true;
		}
		fps = new FindPositionSettings(master.getController(),this, RANGE_OF_CHROMOSOME.REDUCED);
	}

	/**
	 * set the initial chromosome for this plot, called if initial ChromeHeatMapTable is created.
	 */
	protected void internalSpeciesInitialization() {
		
	}
	
	/**
	 * 
	 */
	protected void internalActualDataInitialization(){
		actualChromeDataSetted(false);
		setActualData(org.getActualData(getSelectedChrome()));
		actualChromeDataSetted(true);
	
		fps = new FindPositionSettings(master.getController(),this, RANGE_OF_CHROMOSOME.REDUCED);
		
		threadManager.runWholeThreads(actualChromeData);
		threadManager.runCondensedThreads(actualChromeData);
	}
	
	/**
	 * 
	 * @param val
	 */
	public void fittedProbesSetted_Whole(boolean val){
		this.fit_WholePrbSetted = val;
	}

	/**
	 * 
	 * @param val
	 */
	public void fittedProbesSetted_Condensed(boolean val){
		this.fit_CondensedPrbSetted = val;
	}

	
	/**
	 * Sets the zoomlevel fit button enabled if boolean is false, else set disabled
	 * zoomLevel Fit button can only be pressed if computations are finished.
	 * @param val
	 */
	public void waitForCondensedFit(boolean val){
		waitForCondensedFit = val;
		if(waitForCondensedFit == false && waitForWholeFit == false){
			master.enableZoomView_FitButton();
		} else {
			master.disableZoomView_FitButton();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean waitForCondensedFit(){
		return this.waitForCondensedFit;
	}
	
	/**
	 * Sets the zoomlevel fit button enabled if boolean is false, else set disabled
	 * zoomLevel Fit button can only be pressed if computations are finished.
	 * @param val
	 */
	public void waitForWholeFit(boolean val){
		waitForWholeFit = val;
		if(waitForCondensedFit == false && waitForWholeFit == false){
			master.enableZoomView_FitButton();
		} else {
			master.disableZoomView_FitButton();
		}
	}

	/**
	 * 
	 */
	public void setFittedCondensedData() {
		this.threadManager.setFittedCondensedData();
	}

	/**
	 * 
	 */
	public void setFittedWholeData() {
		this.threadManager.setFittedWholeData();
	}

	/**
	 * 
	 */
	public void clearAllPreviousComputedFittedData() {
		fittedProbesSetted_Whole(false);
		fittedProbesSetted_Condensed(false);
		clearFittedData();
	}

	/**
	 * 
	 * @param fitted
	 */
	public void setFittedCondensedProbes(ArrayList<List<Probe>> fitted){
		actualChromeData.setFittedCondensedProbes(fitted);
	}
	
	/**
	 * 
	 * @param fitted
	 */
	public void setFittedWholeProbes(MultiTreeMap<Long,Probe> fitted){
		actualChromeData.setFittedWholeProbes(fitted);
	}
	
	/**
	 * clear fitted data because size of window or zoomlevel changed.
	 */
	public void clearFittedData() {
		actualChromeData.clearFittedData();
	}
	
	//#############################################################
	/*
	 * Connection to Fitted Data
	 * 
	 */
	//#############################################################
	
	public void clearBufferedImages(){
		imageModel.clearBufferedImages();
	}
	public void updateScalaTicks(){
		imageModel.updateScalaTicks();
	}
	
	/**
	 * 
	 */
	public void createScalaImagesForRange(){
		int firstVisibleRow = ts.getFirstVisibleRow();
		int lastVisibleRow = ts.getLastVisibleRow();
		//getSi_model().updateScalaTicks();
//		setBufferedImage(getSi_model().paintScalaImage());
		if(firstVisibleRow >= 0 && lastVisibleRow >= 0){
			for(int row = firstVisibleRow; row <= lastVisibleRow; row++){
				StrandInformation strand = TableMapper.getStrand(row, getRowCount());
				if(strand == StrandInformation.PLACEHOLDER){
					if(getBufferedImage(row) == null){
						setBufferedImage(row, getSi_model().paintScalaImage(row, this, strand));
					}	
				}
			}
		}
	}
	
	public ForwardPanel getForwardPanel(){
		return imageModel.getForwardPanel();
	}
	
	public BackwardPanel getBackwardPanel(){
		return imageModel.getBackwardPanel();
	}
	
	public ScalaBufferedImage getBufferedImage(int rowNumber){
		return imageModel.getBufferedImage(rowNumber);
	}
	
	public ScalaBufferedImage getBufferedImage(){
		return imageModel.getBufferedImage();
	}
	
	public void setBufferedImage(int row, ScalaBufferedImage scalaImage){
		imageModel.setBufferedImage(row, scalaImage);
	}
	
	public void setBufferedImage(ScalaBufferedImage scalaImage){
		imageModel.setBufferedImage(scalaImage);
	}
	
	/**
	 * returns if available information about actual searched of clicked Probe.
	 * @param name: display name of probe
	 * @return information about probe
	 */
	public String getInformationAboutProbe(Probe pb){

		return probeInformation.getInformationAboutProbe(pb);
	}

	public ChromosomeDataSet getActualChromeData() {
		
		return this.actualChromeData;
	}

	public int getNumberOfRowsOneStrand() {
		return ts.necessaryRowsOneStrand;
	}

	public void setNumberOfRowsAllStrands() {
		ts.setNumberOfRowsAllStrands(1);
	}

	public int getSelectedChromePosition() {
		//return selectedChromePosition;
		if(fps != null){
			return fps.getPosition();
		}
		return -1;
	}

	public void setProbeToFind(Probe probe) {
		foundProbe = probe;
	}

	public Probe getProbeToFind() {
		return foundProbe;
	}

	protected void setSearchedCellnumber(int cellnumber) {
		searchedCellnumber = cellnumber;
		
	}
	
	public int getSearchedCellnumber(){
		return this.searchedCellnumber;
	}

	public boolean chromePositionSearched() {
		return this.searchChromePosition;
	}

	public void searchForChromePosition(boolean b) {
		this.searchChromePosition = b;
	}

	public void setSearchedCell(int cellnumber, boolean b) {
		setSearchedCellnumber(cellnumber);
		searchForChromePosition(true);
	}

	/**
	 * set new experiment number if changed.
	 * @param exp 
	 */
	public void setExperimentNumber(int exp) {
		if(actualExperimentNumber != exp){
			previousExperimentNumber = actualExperimentNumber;
			actualExperimentNumber = exp;
		}
	}

	/**
	 * get number of actual selected experiment.
	 * @return
	 */
	public int getActualExperimentNumber() {
		return actualExperimentNumber;
	}
	
	public int getPreviousExperimentNumber(){
		return previousExperimentNumber;
	}
	
	public void setWindowRanges(){
		rangesOfWindow = new TreeMap<Integer,ComboObject>();
		for(ZoomLevel zoomlevel : this.zoomMultiplikators){
			int multiplikator = 0;
			String text = "";
			if(!zoomlevel.equals(ZoomLevel.fit)){
				multiplikator = TableComputations.getZoomMultiplikator(zoomlevel);
				//text = "show " + new ComputeWindowRangeForZoomLevel_Delegate().computeRange(this, master.getTable(),multiplikator) + " bp";
				text = "1:" + multiplikator;
			} else if(zoomlevel.equals(ZoomLevel.fit)){
				text = "1:" + this.getActualFitMultiplikator_Whole();
			}
			
			ComboObject obj = new ComboObject(text,zoomlevel);
			rangesOfWindow.put(multiplikator, obj);
		}
	}
	
	public TreeMap<Integer,ComboObject> getWindowRanges(){
		return rangesOfWindow;
	}

	public ProbeListColoring getPbListColoring() {
		return pbListColoring;
	}

	/**
	 * sets the kind of probelist coloring, if coloring is "ProbeListColoring.COLOR_HIGHEST_PROBELIST" only color from
	 * probelist with highest occurrence of probes is painted, else color of each probelist is painted.
	 * @param pbListColoring
	 */
	public void setPbListColoring(ProbeListColoring pbListColoring) {
		if(this.pbListColoring  != pbListColoring){
			this.pbListColoring = pbListColoring;
			this.fireChanged();
		}
	}

	public void setFirstAndLastVisiblePositions(int chromePositionFirst,
			int chromePositionLast) {
		ts.setFirstAndLastVisiblePositions(chromePositionFirst,chromePositionLast);
	}

	public int getFirstVisiblePosition() {
		return ts.firstVisiblePosition;
	}

	public int getLastVisiblePosition() {
		return ts.lastVisiblePosition;
	}

	public RangeModel getRangeModel() {
		return this.rangeModel;
	}

	public KindOfData getKindOfData() {
		return this.actualChromeData.getKindOfData();
	}

	public boolean isCondensedViewAvailable() {
		return condensedViewAvailable;
	}

	public void setCondensedViewAvailable(boolean condensedViewAvailable) {
		this.condensedViewAvailable = condensedViewAvailable;
	}
	
	public void updateCache(double workId, WorkOnData work) {
		dcs.updateCache(workId, work);
	}

	public void setScrollPane(ChromosomeScrollPane scrollPaneForHeatMap) {
		scrollPane = scrollPaneForHeatMap;
	}

	protected ChromosomeScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setWidthOrBoxSizeChanged(boolean b) {
		ts.widthOrBoxSizeChanged = b;
	}

	public void setVisibleRows() {
		ts.setVisibleRows(this.master.getTable());
	}

	public void setVisiblePositions() {
		ts.setVisiblePositions();
	}

	public void performAction(ActionMode mode_b, Controller controller) {
		switch(mode_b){
		case MODE_B:
			ts.setNecessaryCellnumber();
			ts.computeNumberOfBoxesEachRow();
			//model.setNumberOfBoxesEachRow(model.computeNumberOfBoxesEachRow());
			ts.setNumberOfRowsOneStrand();
			setNumberOfRowsAllStrands();
			
			setWindowRanges();
			master.initializeRangeOfWindow();
			
			fireTableStructureChanged();
			
			controller.scalaOperations(true);
			fireChanged();
			break;
		case MODE_A:
			ts.setNecessaryCellnumber();
			ts.setNumberOfRowsOneStrand();
			setNumberOfRowsAllStrands();
			setWindowRanges();
			
			controller.scalaOperations(true);
			fireChanged();
			break;
		case MODE_C:
			ts.computeNumberOfBoxesEachRow();
			ts.setNumberOfRowsOneStrand();
			setNumberOfRowsAllStrands();

			setWindowRanges();
			master.initializeRangeOfWindow();
			
			fireTableStructureChanged();
					
			controller.scalaOperations(true);
			fireChanged();
			break;
		case MODE_D:
			setOriginalNumberOfCells();
			ts.setNecessaryCellnumber();
			ts.computeNumberOfBoxesEachRow();
			
			fireTableStructureChanged();
			break;
		case MODE_E:
			setOriginalNumberOfCells();
			ts.setNecessaryCellnumber();
			ts.setNumberOfRowsOneStrand();
			setNumberOfRowsAllStrands();

			controller.scalaOperations(true);
			fireChanged();
			break;
		}
		if(chromePositionSearched()){
			master.getController().searchPositionInChromosome();
			fireChanged();
		}
	}

	public GHMStyle getStyle() {
		StyleSetting ss = master.getStyleSetting();
		if(ss!=null){
			return ss.getStyle();
		}
		return GHMStyle.MODERN;
	}

	public void removeNotify() {
	
		if(threadManager!=null){
			threadManager.killThreads();
			threadManager = null;
		}
		dcs =null;
	}
	
	public ScaleImageModel getSi_model() {
		return imageModel.getSi_model();
	}

	public void setStyle(GHMStyle Style, Controller c) {
			fireTableStructureChanged();
			c.scalaOperations(true);
			fireChanged();
	}

	public FindPositionSettings getFps() {
		return fps;
	}

	public void clearData() {
		if(actualChromeData != null){
			actualChromeData = null;
		}
		removeNotify();
	}

	public boolean isInitialized() {
		return initialized;
	}

	protected void setInitialized(boolean Initialized) {
		initialized = Initialized;
	}

	public void repositionChromeHeaderInScrollPane() {
		scrollPane.repositionHeader();
	}

	public int getHeight_chromePanel() {
		return ConstantData.INITIAL_HEIGHT_CHROMEPANEL;
	}

	public void stateChanged(ChangeEvent e) {
		if(actualChromeData!=null){
			tempSelectedChrome = actualChromeData.getActualChrome();
			resetActualData();
			fireChanged();
		} 
	}

	public void stateChanged(SettingChangeEvent e) {
		System.out.println("GenomeHeatmapTableModel - Settings changed");
	}

	public TableSettings getTableSettings() {
		return ts;
	}

}
