package mayday.vis3.plots.genomeviz.genomeoverview;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.Strand;
import mayday.vis3.gui.Layouter;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.genomeviz.AbstractLogixVizModel;
import mayday.vis3.plots.genomeviz.FindPositionSettings;
import mayday.vis3.plots.genomeviz.Organiser;
import mayday.vis3.plots.genomeviz.EnumManager.RANGE_OF_CHROMOSOME;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.EnumManagerGO.ActionModes;
import mayday.vis3.plots.genomeviz.EnumManagerGO.Fixed;
import mayday.vis3.plots.genomeviz.EnumManagerGO.MouseClickNumber;
import mayday.vis3.plots.genomeviz.EnumManagerGO.SizeMode;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapComponent;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.SelectedRange;
import mayday.vis3.plots.genomeviz.genomeorganisation.Chrome;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeSettings;
import mayday.vis3.plots.genomeviz.genomeorganisation.GenomeDAO;
import mayday.vis3.plots.genomeviz.genomeoverview.caching.TrackPaintManager;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.DataSetted;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.OperationsForScalaSelection;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.TrackPositioner;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;

@SuppressWarnings("serial")
public class GenomeOverviewModel extends AbstractLogixVizModel{

	protected TrackPositioner trackpositioner = null;
	protected SelectionsModel selModel = null;

	protected GenomeOverviewComponent component = null;
	protected GenomeOverviewLayeredPane layeredPane = null;
	protected MyPlotScrollPane scrollpane = null;
	protected int width_ChromeOverviewComponent = 0;
	protected int height_ChromeOverviewComponent = 0;
		
	protected int experimentNumber = ConstantData.INITIAL_EXPERIMENT_VALUE;
	
	protected Long fromPosition = null;
	protected boolean fromSelected = false;
	protected Long toPosition = null;
	protected boolean toSelected = false;
	private boolean rangeFlag = false;
	protected MouseClickNumber mcn = MouseClickNumber.ONE;
	
	protected boolean widthChanged = false;
	
	
	protected EventListenerList rangeListenerList = new EventListenerList();

	protected int actualWidthLayeredPane = 0;
	protected int actualHeightLayeredPane = 0;
	protected int newWidthLayeredPane = 0;
	
	protected SizeMode sizemode = SizeMode.SIZE_LAYER;

	
	private int left_margin = 5;		// left space
	private int right_margin = 5;		// right space
	    
	protected boolean component_BiggerWidth = false;
	protected int component_prev_width = 0;
	protected boolean component_BiggerHeight = false;
	protected int component_prev_height = 0;
	
	public TrackPaintManager paintManager = null;
	
	protected int MAXIMAL_TRACK_WIDTH = 0;
	protected VisibleRange_Object visRangeObject;
	protected boolean directpaint = false;
	
	protected FindPositionSettings fps;
	protected ChromosomeSettings chromeSettings;
	protected DataSetted ds = null;
	
	protected BooleanSetting showMarker;
	
	public GenomeOverviewModel(GenomeOverviewComponent chromeOverviewComponent, DataSetted Ds){
		super("ChromeOverviewModel");
		ds= Ds;
		component = chromeOverviewComponent;
		
		selModel = new SelectionsModel(this);
		visRangeObject = new VisibleRange_Object();
		paintManager = new TrackPaintManager(visRangeObject, this);
				
		showMarker = new BooleanSetting("Highlight mouse position",null,false);
	}
	
	//###################################################
	//
	//	EVENTS
	//
	//###################################################
	
	public void addRangeListener(ChangeListener cl) {
		rangeListenerList.add(ChangeListener.class, cl);		
	}
	
	public void removeRangeListener(ChangeListener cl) {
		rangeListenerList.remove(ChangeListener.class, cl);
	}
	
	public void fireRangeChanged() {
		
		Object[] l_listeners = rangeListenerList.getListenerList();

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
	
	//###################################################
	//
	//	initialization of data
	//
	//###################################################
	public void initialize(ViewModel ViewModel, Organiser organiser) {
		super.initAbstractModel(ViewModel, organiser);
	
		trackpositioner = new TrackPositioner(this);
		trackpositioner.createScalePlugin();
		scrollpane.initScrollPane();
		fps = new FindPositionSettings(getController(),this, RANGE_OF_CHROMOSOME.COMPLETE);
		
		
	}
	
	/**
	 * sets the data of this plot depending on chromosome, happens if new window for another chomosome is choosen or
	 * if another chomosome is choosen in the same window.
	 * @param actualChromeData
	 * @param viewModel 
	 */
	protected void internalSettingOfActulaData() {
		ds.dataSetted();
		if(trackpositioner!=null)trackpositioner.dataUpdated();
	}
	
	protected void internalActualDataInitialization(){

	}

	//#############################################################
	/*
	 * For Species selection
	 * 
	 */
	//#############################################################
	
	protected void internalSpeciesInitialization() {
		if(getLengthOfChromosome() < 2000){
			MAXIMAL_TRACK_WIDTH = 2000;
		} else{
			long mtw = 10l * (long)getLengthOfChromosome();
			if (mtw>Integer.MAX_VALUE)
				mtw = Integer.MAX_VALUE;
			MAXIMAL_TRACK_WIDTH = (int)mtw;
		}
	}
	
	
	//#############################################################
	/*
	 * Connection to panels also create panels
	 * 
	 */
	//#############################################################
	
	public TrackPositioner getPanelPositioner() {
		return trackpositioner;
	}
	
	public void removePanels(){
		trackpositioner.removeTracks();
	}
	
	public void actualizeForThread(AbstractTrackPlugin trackplugin){
		trackplugin.repaintTrack();
		fireChanged();
	}
	public void setLocationOfUserpanel(JPanel panel) {
		trackpositioner.setLocationOfUserpanel(panel);
	}
	
	public void setLocationOfUserpanel() {
		trackpositioner.setLocationOfUserpanel();
	}
	
	public void actualizeTracks() {
		trackpositioner.deleteImagesOfTracks();
		trackpositioner.resizeTracks();
		trackpositioner.updateTracks();
	}
	
	public void repaintTrackImage(ITrack tp){
		trackpositioner.updateTrack(tp);
	}
	
	
	public ITrack getPanelToAdd(){
		if(trackpositioner!=null){
			return trackpositioner.getTrackToAdd();
		}
		else return null;
		
	}
	

	public void checkPanels(ITrack changedPanel) {
		trackpositioner.checkTracks(changedPanel);
	}

	public void resetPanelToAdd() {
		trackpositioner.resetPanelToAdd();
	}

	//#############################################################
	/*
	 * Connection to data
	 * 
	 */
	//#############################################################
	


	public LogixVizColorProvider getNewColorProvider(){
		return new LogixVizColorProvider(viewModel);
	}
	


	public Double getFirst(int actualExperiment, Set<Probe> list) {
		Probe pb = null;
		long startPos = Long.MAX_VALUE;
		
		for(Probe p: list){
			if(startPos > getStartPosition(p)) pb = p;
		}
		if(pb != null){
			return pb.getValue(actualExperiment);
		}
		return null;
	}
	
	public Set<Probe> getAllForwardProbes(long pos) {
		return getProbes(pos, pos, Strand.PLUS);
	}
	
	public MultiTreeMap<Double,Probe> getAllForwardProbes_Abs(long pos, int exp) {
		return getProbeValues_Abs(pos , pos, Strand.PLUS, exp);
	}
	
	public Set<Probe> getAllBackwardProbes(long pos) {
		return getProbes(pos , pos, Strand.MINUS);
	}
	
	public Set<Probe> getBothProbes(long pos) {
		return getProbes(pos, pos, Strand.UNSPECIFIED);	}
	
	public MultiTreeMap<Double,Probe> getAllBackwardProbes_Abs(long pos, int exp) {
		return getProbeValues_Abs(pos, pos, Strand.MINUS, exp);
	}

	public Set<Probe> getAllForwardProbes(long first, long last) {
		return getProbes(first, last, Strand.PLUS);
	}
	
	public Double getExpValue_p(long from, long to, SplitView split, int exp) {
		return getExpValue(from, to, Strand.PLUS, split, exp);
	}
	
	public Double getExpValue_m(long from, long to, SplitView split, int exp) {
		return getExpValue(from, to, Strand.MINUS, split, exp);
	}
	
	/**
	 * returns an double array which contains the maximum, minimum or mean of values from colorprovider for all probes
	 * valArray[0] contains the (mean/max/min) value for forward strand, valArray[1] the respective value for the backward strand.
	 * @param first
	 * @param last
	 * @param split
	 * @param exp
	 * @return value (mean/max/min) or NaN.
	 */
//	public void getBothValues_exp_transp(long first, long last, SplitView split,int exp, ValueProvider transparencyProvider, double[] valArray) {
//		/* transparency for forward strand*/
//		double transparencyMeanPlus = Double.NaN;
//		double transparencyMinPlus = Double.NaN;
//		double transparencyMaxPlus = Double.NaN;
//		/* transparency for backward strand*/
//		double transparencyMeanMinus = Double.NaN;
//		double transparencyMinMinus = Double.NaN;
//		double transparencyMaxMinus = Double.NaN;
//		
//		/* experiment value for forward strand*/
//		double minPlus = Double.NaN;
//		double maxPlus = Double.NaN;
//		double meanPlus = Double.NaN;
//		
//		/* experiment value for backward strand*/
//		double minMinus=Double.NaN;
//		double maxMinus=Double.NaN;
//		double meanMinus=Double.NaN;
//		
//		for(long i = first; i <= last; i++){
//			if (!getData().get(i).isEmpty()) {
//				
//				transparencyMinPlus = Double.POSITIVE_INFINITY;
//				transparencyMaxPlus = Double.NEGATIVE_INFINITY;
//				transparencyMeanPlus=0.;
//				/* transparency for backward strand*/
//				transparencyMinMinus = Double.POSITIVE_INFINITY;
//				transparencyMaxMinus = Double.NEGATIVE_INFINITY;
//				transparencyMeanMinus=0.;
//				/* experiment value for forward strand*/
//				minPlus = Double.POSITIVE_INFINITY;
//				maxPlus = Double.NEGATIVE_INFINITY;
//				meanPlus=0.;
//				/* experiment value for backward strand*/
//				minMinus=Double.POSITIVE_INFINITY;
//				maxMinus=Double.NEGATIVE_INFINITY;
//				meanMinus=0.;
//				
//				int probeNumberPlus=0;
//				int probeNumberMinus=0;
//				
//				for (Probe pb : getData().get(i)) {
//					if (actualChromeData.getStrand(pb) == Strand.PLUS){
//						double expVal=pb.getValue(exp);
//						if(minPlus>expVal)minPlus=expVal;
//						if(maxPlus<expVal)maxPlus=expVal;
//						meanPlus+=expVal;
////						plusProbes.add(pb);
//						
//						double transparencyValue=transparencyProvider.getValue(pb);
//						transparencyMeanPlus+=transparencyValue;
//						if(transparencyMinPlus>transparencyValue)transparencyMinPlus=transparencyValue;
//						if(transparencyMaxPlus<transparencyValue)transparencyMaxPlus=transparencyValue;
//						probeNumberPlus++;
//					} else if(actualChromeData.getStrand(pb) == Strand.MINUS){
//						double expVal=pb.getValue(exp);
//						if(minMinus>expVal)minMinus=expVal;
//						if(maxMinus<expVal)maxMinus=expVal;
//						meanMinus+=expVal;
////						plusProbes.add(pb);
//						
//						double transparencyValue=transparencyProvider.getValue(pb);
//						transparencyMeanMinus+=transparencyValue;
//						if(transparencyMinMinus>transparencyValue)transparencyMinMinus=transparencyValue;
//						if(transparencyMaxMinus<transparencyValue)transparencyMaxMinus=transparencyValue;
//						probeNumberMinus++;
//					}
//				}
//				
//				if(probeNumberMinus>0){
//					transparencyMeanMinus=transparencyMeanMinus/(double)probeNumberMinus;
//					meanMinus=meanMinus/(double)probeNumberMinus;
//				} else {
//					transparencyMeanMinus=Double.NaN;
//					transparencyMinMinus=Double.NaN;
//					transparencyMaxMinus=Double.NaN;
//					meanMinus=Double.NaN;
//					maxMinus=Double.NaN;
//					minMinus=Double.NaN;
//				}
//				
//				if(probeNumberPlus>0){
//					transparencyMeanPlus=transparencyMeanPlus/(double)probeNumberPlus;
//					meanPlus=meanPlus/(double)probeNumberPlus;
//				} else {
//					transparencyMeanPlus=Double.NaN;
//					transparencyMinPlus=Double.NaN;
//					transparencyMaxPlus=Double.NaN;
//					meanPlus=Double.NaN;
//					maxPlus=Double.NaN;
//					minPlus=Double.NaN;
//				}
//				
//			}
//		}
//
//		if(split.equals(SplitView.mean)){
//			valArray[0]=meanPlus;
//			valArray[1]=meanMinus;
//			valArray[2]=transparencyMeanPlus;
//			valArray[3]=transparencyMeanMinus;
////			expVals[0]= getMean(exp,plusProbes);
//			// expVals[1]= getMean(exp,minusProbes);
//		} else if(split.equals(SplitView.min)){
//			valArray[0]=minPlus;
//			valArray[1]=minMinus;
//			valArray[2]=transparencyMinPlus;
//			valArray[3]=transparencyMinMinus;
////			expVals[0]= getMin(exp,plusProbes);
////			expVals[1]= getMin(exp,minusProbes);
//		} else if(split.equals(SplitView.max)){
//			valArray[0]=maxPlus;
//			valArray[1]=maxMinus;
//			valArray[2]=transparencyMaxPlus;
//			valArray[3]=transparencyMaxMinus;
////			expVals[0]= getMax(exp,plusProbes);
////			expVals[1]= getMax(exp,minusProbes);
//		}
////		return valArray;
//	}
	
	
	public Set<Probe> getBothProbes(long first, long last) {
		return getProbes(first, last, Strand.UNSPECIFIED);
	}
	
	public MultiTreeMap<Double,Probe> getAllForwardProbes_Abs(long first, long last, int exp) {
		
		MultiTreeMap<Double,Probe> plusProbes = new MultiTreeMap<Double,Probe>();
		
		for (LocusGeneticCoordinateObject<Probe> olgcp : getData().getProbes(first, last, Strand.PLUS)) {
			Probe pb = olgcp.getObject();
			double val = viewModel.getProbeValues(pb)[exp];
			plusProbes.put(Math.abs(val), pb);
		}
	
		return plusProbes;
	}
	
	public Set<Probe> getAllBackwardProbes(long first, long last) {
		
		Set<Probe> minusProbes = new HashSet<Probe>();
		
		for (LocusGeneticCoordinateObject<Probe> olgcp : getData().getProbes(first, last, Strand.MINUS)) {
			Probe pb = olgcp.getObject();
			minusProbes.add(pb);
		}

		return minusProbes;
	}
	
	public MultiTreeMap<Double,Probe> getAllBackwardProbes_Abs(long first, long last, int exp) {

		MultiTreeMap<Double,Probe> minusProbes = new MultiTreeMap<Double,Probe>();
		
		for (LocusGeneticCoordinateObject<Probe> olgcp : getData().getProbes(first, last, Strand.MINUS)) {
			Probe pb = olgcp.getObject();
			double val = viewModel.getProbeValues(pb)[exp];
			minusProbes.put(Math.abs(val), pb);
		}
	
		return minusProbes;
	}
	//#############################################################
	/*
	 * Connection to ChromeHeatMapTable Plugin
	 * 
	 */
	//#############################################################
	public void openChromeHeatMapComponent(KindOfData kindOfData) {

//    	Species tempSpecies = this.getSelectedSpecies();
    	Chrome tempChrome = this.getSelectedChrome();
    	
    	if(kindOfData.equals(KindOfData.BY_POSITION)){
    		if(isRangeValid()){
        		org.setKindOfData(kindOfData);
            	org.setRangeSelection_withPos(new SelectedRange(fromPosition,toPosition));
        	}
//    	} else if(kindOfData.equals(KindOfData.BY_PROBES)){
//    		org.setKindOfData(kindOfData);
//           	org.setRangeSelection_withProbes(this.getViewModel().getSelectedProbes());
    	}
    	
    	setColoringInOrganiser();
    	
    	org.setTempSpeciesAndChrome(tempChrome);
    	
		Visualizer viz = this.viewModel.getVisualizer();
		PlotWindow pw = new PlotWindow(new GenomeHeatMapComponent(), viz);
		pw.setVisible(true);
		Layouter l = new Layouter(2,1);
		l.nextElement().placeWindow(pw);
		
		org.setKindOfData(KindOfData.STANDARD);
	}

	private void setColoringInOrganiser() {
		if((Track.isScaleTrack(selModel.getSelectedTrack()))){
   			org.setColoring(((ITrack)selModel.getSelectedTrack()).getTrackPlugin().getTrackSettings().getColorProvider());
       	}
	}
	
	public void openChromeHeatMapComponent(KindOfData kindOfData, int from, int to) {

//		Species tempSpecies = this.getSelectedSpecies();
    	Chrome tempChrome = this.getSelectedChrome();
    	
    	if(kindOfData.equals(KindOfData.BY_POSITION)){
    		if(isRangeValid()){
        		org.setKindOfData(kindOfData);
            	org.setRangeSelection_withPos(new SelectedRange(from,to));
        	}
//    	} else if(kindOfData.equals(KindOfData.BY_PROBES)){
//    		org.setKindOfData(kindOfData);
//           	org.setRangeSelection_withProbes(this.getViewModel().getSelectedProbes());
    	}
    	
    	setColoringInOrganiser();
    	
    	org.setTempSpeciesAndChrome(tempChrome);
    	
		Visualizer viz = this.viewModel.getVisualizer();
		PlotWindow pw = new PlotWindow(new GenomeHeatMapComponent(), viz);
		pw.setVisible(true);
		Layouter l = new Layouter(2,1);
		l.nextElement().placeWindow(pw);
		
		org.setKindOfData(KindOfData.STANDARD);
	}
	
	public void openChromeHeatMapComponent(KindOfData kindOfData, double from, double to) {

//    	Species tempSpecies = this.getSelectedSpecies();
    	Chrome tempChrome = this.getSelectedChrome();
    	
   		org.setKindOfData(kindOfData);
       	org.setRangeSelection_withProbes(this.getViewModel().getSelectedProbes());
       	setColoringInOrganiser();
       
    	
    	org.setTempSpeciesAndChrome(tempChrome);
    	
		Visualizer viz = this.viewModel.getVisualizer();
		PlotWindow pw = new PlotWindow(new GenomeHeatMapComponent(), viz);
		pw.setVisible(true);
		Layouter l = new Layouter(2,1);
		l.nextElement().placeWindow(pw);
		
		org.setKindOfData(KindOfData.STANDARD);
	}

	//#############################################################
	/*
	 * Range selection
	 * 
	 */
	//#############################################################
	private boolean isRangeValid() {
		if(fromPosition != null && toPosition != null){
			if(fromPosition <= toPosition && fromPosition > 0 && toPosition <= getChromosomeEnd()){
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public void selectRange(long chromPos) {
		if(this.mcn == MouseClickNumber.ONE){
			mcn = MouseClickNumber.TWO;
			this.fromPosition = chromPos;
		} else if(this.mcn == MouseClickNumber.TWO){
			mcn = MouseClickNumber.NULL;
			this.toPosition = chromPos;

		} else if(this.mcn == MouseClickNumber.NULL){
			mcn = MouseClickNumber.ONE;
			fromPosition = null;
			fromPosition = chromPos;
			toPosition = null;
		}
	}

	public void setFromToPosition_RangeSelection(long from, long to){
		System.out.println("setFromToPosition_RangeSelection " + from + " - " + to);
		fromPosition = from;
		toPosition = to;
		this.fireRangeChanged();
	}
	
	public void setFromPosition_RangeSelection(long val) {
		fromPosition = val;
	}

	public void setToPosition_RangeSelection(long val) {
		toPosition = val;
	}

	public Long getToPosition_RangeSelection() {
		return toPosition;
	}

	public Long getFromPosition_RangeSelection() {
		return fromPosition;
	}

	public void setRangeSelectionButton(boolean fromPressed) {
		if(fromPressed){
			this.fromSelected = true;
			this.toSelected = false;
		} else {
			this.fromSelected = false;
			this.toSelected = true;
		}
	}
	
	public void setRangeFlag(boolean b) {
		rangeFlag  = b;
	}

	public boolean getRangeFlag() {
		return rangeFlag;
	}
	
	//#############################################################
	/*
	 * Operations on Layered Pane
	 * 
	 */
	//#############################################################

	public void setLayeredPane(GenomeOverviewLayeredPane LayeredPane) {
		layeredPane = LayeredPane;
		setActualSize_layeredpane();
		visRangeObject.init(this, layeredPane);
	}
	
	public void setActualSize_layeredpane(){
		// width
		actualWidthLayeredPane = layeredPane.getWidth();
		
		// height
		int newHeight = getMinimumHeightOfLayeredPane();
		actualHeightLayeredPane = newHeight;
	}
	
	public Rectangle getVisibleRectOfLayeredPane(){
		if(layeredPane != null){
			return layeredPane.getVisibleRect();
		}
		return null;
	}

	public void setSizeLayeredPane(int newWidth,SizeMode sizemode){
		prepareNewWidth_LayeredPane(newWidth);
		setSize_LayeredPane(sizemode);
		
		if(isWidthChanged()){
			actualizeTracks();
			fireChanged();
			computeVisiblePositions();
		}
	}
	
	public void setSizeLayeredPane(){
		layeredPane.resetMySize();
	}
	
	protected void setSize_LayeredPane(SizeMode sizemode) {
		widthChanged = false;
		
		if(org!=null && org.getChromeManager()!=null &&
				org.getChromeManager().containsLoci()){

			int prevWidth = actualWidthLayeredPane;
			
			if(sizemode.equals(SizeMode.SIZE_ZOOM)){
				resetWidth_layerdpane();
			}

			actualHeightLayeredPane = getMinimumHeightOfLayeredPane();
			actualWidthLayeredPane = layeredPane.getWidth();

			if(prevWidth != actualWidthLayeredPane){
				widthChanged = true;
				if(org!=null && org.getChromeManager()!=null &&
						org.getChromeManager().containsLoci()){
				}
			} 
		}
	}
	
	public Rectangle getViewRectOfJLayeredPane(){
		return ((JViewport) layeredPane.getParent()).getViewRect();
	}
	

	/**
	 * returns the minimum height of the layeredPane, depends on window size.
	 * @return
	 */
	private int getMinimumHeightOfLayeredPane() {
		int newHeight = 0;
		if(this.scrollpane != null){
			final Dimension minimumLayeredPaneSize = this.scrollpane.getViewport().getExtentSize();
			if(layeredPane.getHeight() <  minimumLayeredPaneSize.getHeight()){
				newHeight = (int)minimumLayeredPaneSize.getHeight();
			} else {
				newHeight = layeredPane.getHeight();
			}
		}
		return newHeight;
	}
	
	/**
	 * reset width of LayeredPane, when tracks been zoomed.
	 */
	public void resetWidth_layerdpane() {
		layeredPane.resetMySize(newWidthLayeredPane, 0);
	}
	
	/**
	 * reset the height of LayeredPane when tracks been dragged.
	 */
	public void resetHeight_layeredPane() {
		final Dimension minimumLayeredPaneSize = this.scrollpane.getViewport().getExtentSize();
		layeredPane.resetMySize(0, (int)minimumLayeredPaneSize.getHeight());
	}
	
	public void setFixSize_layeredPane(boolean b) {
		layeredPane.setFixHeight(b);
	}

	/**
	 * prepare new width of layered pane.
	 * @param newWidth
	 */
	public void prepareNewWidth_LayeredPane(int newWidth) {
		final Dimension minimumLayeredPaneSize = scrollpane.getViewport().getExtentSize();
		if(newWidth < minimumLayeredPaneSize.getWidth()){
			newWidth = (int)minimumLayeredPaneSize.getWidth();
		} else if(newWidth > getMaximalTrackWidth()){
			newWidth = getMaximalTrackWidth();
		}
		newWidthLayeredPane = newWidth;
//		System.out.println("newWidthLayeredPane " + newWidthLayeredPane);
	}
	
	public int getMaximalTrackWidth(){
		return MAXIMAL_TRACK_WIDTH;
	}
	
	public int getNewWidthLayeredPane(){
		return newWidthLayeredPane;
	}
	
	public int getHeightOfLayeredPane() {
		return actualHeightLayeredPane;
	}
	
	public int getWidth_LayeredPane(){
		return actualWidthLayeredPane;
	}
	
	//#############################################################
	/*
	 * Connection to objects
	 * 
	 */
	//#############################################################
	
	public SizeMode getResizeType() {
		return sizemode;
	}

	public void setScrollPane(MyPlotScrollPane scrollPane) {
		this.scrollpane = scrollPane;
	}

	public GenomeOverviewComponent getComponent() {
		return component;
	}

	public MyPlotScrollPane getScrollPane() {
		return scrollpane;
	}
	
	/*
	 * reposition chromeheaderpanel in header of scrollpane if scrollbar moved.
	 */
	public void repositionChromeHeaderInScrollPane() {
		scrollpane.repositionHeader();
	}
	
	public Controller getController() {
		return this.component.c;
	}
	
	//#############################################################
	/*
	 * Getting width of panels
	 * 
	 */
	//#############################################################
	public int getWidth_paintingpanel_reduced() {

		int width = getWidth_LayeredPane() - getLocation_paintingpanel_X() - ConstantData.RIGHT_MARGIN;
		return width;
	}
	
	public int getWidth_scala_pp(){
		int width = getWidth_LayeredPane() - (ConstantData.USER_PANEL_WIDTH) +3;
		return width;
	}

	public int getWidth_userpanel() {
		return ConstantData.USER_PANEL_WIDTH;
	}
	
	public int getWidth_chromeHeaderPanel(){
		return this.scrollpane.getColumnHeader().getWidth();
	}
	
	public int getWidth_dummypanel() {
		return ConstantData.DUMMY_PANEL_WIDTH;
	}

	
	//#############################################################
	/*
	 * Getting height of panels
	 * 
	 */
	//#############################################################
	public int getHeight_chromePanel() {
		return ConstantData.INITIAL_CHROME_HEIGHT;
	}
	
	public int getHeight_trackpanel(){
		return ConstantData.INITIAL_TRACK_HEIGHT;
	}
	

	public int getHeight_scalapaintingpanel(){
		return ConstantData.INITIAL_SCALA_HEIGHT-2;
	}
	
	public int getHeight_infopanel() {
		return ConstantData.INITIAL_TRACK_HEIGHT-2;
	}
	
	public int getHeight_userpanel() {
		return ConstantData.INITIAL_TRACK_HEIGHT-2;
	}

	//#############################################################
	/*
	 * Getting y positions
	 * 
	 */
	//#############################################################

	public int getUnusableSpace_y(){
		return ConstantData.INITIAL_UNUSABLE_SPACE_HEIGHT;
	}
	
	public int getLocation_chromepanel_Y() {
		return 0;
	}

	public int getLocation_paintingpanel_Y() {
		return ConstantData.INITIAL_TRACKPAINTING_YPOSITION;
	}
	
//	public int getLocation_paintingpanel_dhm_Y() {
//		return ConstantData.INITIAL_TRACKPAINTING_YPOSITION_DHM;
//	}
//	
//	public int getLocation_stempaintingpanel_Y() {
//		return ConstantData.INITIAL_TRACKPAINTING_STEM_YPOSITION;
//	}
//	
//	public int getLocation_stemfrontpaintingpanel_Y() {
//		return ConstantData.INITIAL_TRACKPAINTING_STEMFRONT_YPOSITION;
//	}
	
	public int getLocation_userpanel_Y() {
		return ConstantData.INITIAL_LOCATION_USERPANEL_Y;
	}
	
	public int getLocation_dummypanel_Y(){
		return getLocation_userpanel_Y();
	}
	
	public int getLocationForInfopanel_Y() {
		
		return 1;
	}

	public int getLocation_scalepaintingpanel_Y() {
		return 1;
	}
	//#############################################################
	/*
	 * Getting x positions
	 * 
	 */
	//#############################################################
	
	public int getLocation_chromepanel_X() {
		Rectangle rect = getVisibleRectOfLayeredPane();
		if(rect != null){
			int x = rect.x;
			int unused_x = this.getLocation_paintingpanel_X();
			if(x <= unused_x){
				return unused_x;
			} else {
				return rect.x;
			}
			
		} else {
			return this.getLocation_paintingpanel_X();
		}
	}
	
	public int getLocation_paintingpanel_X() {
		return getWidth_userpanel() + getLocation_userpanel_X();
	}
	
	public int getLocation_scalepaintingpanel_X(){
		return getLocation_paintingpanel_X() - ConstantData.LEFT_MARGIN;
	}
	
	public int getLocation_userpanel_X() {
		return ConstantData.INITIAL_LOCATION_USERPANEL_X;
	}
	
	//#############################################################
	/*
	 * Getting 
	 * 
	 */
	//#############################################################

	
	public double getNeededWidthLayeredPane(){
		long length_chrome = getLengthOfChromosome();
		Rectangle visibleRect = this.layeredPane.getVisibleRect();
		double widthVisRect = visibleRect.getWidth();

		long neededVisible_bp = this.selModel.getNeeded_bp();
		double newWidth_pp = Math.round(length_chrome*(widthVisRect/neededVisible_bp));

		double newWidth_layeredPane = newWidth_pp + (ConstantData.USER_PANEL_WIDTH)+ left_margin-right_margin;

		return newWidth_layeredPane;
	}	

	public void someActionWithPanel(ActionModes mode) {
		ITrack selPanel = selModel.getSelectedTrack();
		AbstractTrackPlugin plugin = selPanel.getTrackPlugin();
		selModel.setSelectedTrack(null);
		if(selPanel!=null){
			switch (mode) {
			case DELETE:
				trackpositioner.removeTrack(plugin);
				break;
			case MOVE_UP:
				trackpositioner.moveUp(selPanel);
				break;
			case MOVE_DOWN:
				trackpositioner.moveDown(selPanel);
				break;
			case MOVE_TO_TOP:
				trackpositioner.moveToTop(selPanel);
				break;
			case MOVE_TO_BOTTOM:
				trackpositioner.moveToBottom(selPanel);
				break;
			default:
				System.err
						.println("ChromeOverviewModel - someActionsWithPanel: ActionMode not available");
			}
		}
	}

	public int getLeft_margin() {
		return left_margin;
	}

	public void setLeft_margin(int left_margin) {
		this.left_margin = left_margin;
	}

	public int getRight_margin() {
		return right_margin;
	}

	public void setRight_margin(int right_margin) {
		this.right_margin = right_margin;
	}

	public boolean isComponent_widthGettingBigger() {
		return component_BiggerWidth;
	}

	public void setComponent_BiggerWidth(boolean component_widthChanged) {
		this.component_BiggerWidth = component_widthChanged;
	}

	public boolean isComponent_heightGettingBigger() {
		return component_BiggerHeight;
	}

	public void setComponent_BiggerHeight(boolean component_heightChanged) {
		this.component_BiggerHeight = component_heightChanged;
	}

	public int getComponent_prev_width() {
		return component_prev_width;
	}

	public void setComponent_prev_width(int component_prev_width) {
		this.component_prev_width = component_prev_width;
	}

	public int getComponent_prev_height() {
		return component_prev_height;
	}

	public void setComponent_prev_height(int component_prev_height) {
		this.component_prev_height = component_prev_height;
	}

	public boolean isComponentGettingBigger(GenomeOverviewComponent comp) {
		int prev_width = getComponent_prev_width();
		int prev_height = getComponent_prev_height();
		
		int act_width = comp.getWidth();
		int act_height = comp.getHeight();
		
		if(act_width > prev_width){
			setComponent_BiggerWidth(true);
		} else{
			setComponent_BiggerWidth(false);
		}
		
		if(act_height > prev_height){
			setComponent_BiggerHeight(true);
		} else{
			setComponent_BiggerHeight(false);
		}
		
		setComponent_prev_width(act_width);
		setComponent_prev_height(act_height);
		
		if(component_BiggerWidth || component_BiggerHeight){
			return true;
		} else{
			return false;
		}
		
	}

	public double[] getProbeValues(Probe probe) {
		return viewModel.getProbeValues(probe);
	}

	public ViewModel getViewModel() {
		return viewModel;
	}

	public void updateCache(AbstractTrackPlugin tp) {
		tp.getTrack().setLocationOfUserpanel();		
		paintManager.zoomChanged(); // this is needed for repaint when VM changes. However, we get two repaints. Why?
//		imageStorage.updateCache(tp.getTrack().getId(),tp);
	}

	public ITrack createNewTrack(AbstractTrackPlugin trackPlugin) {
		ITrack t = trackpositioner.addNewTrack(trackPlugin);
		paintManager.addTrack(trackPlugin.getTrack());
		widthChanged=false;
		fireChanged();
		return t;
	}

	public void repositionVisibleRect_mouseClicked(Fixed fixed) {
		layeredPane.scrollRectToVisible(OperationsForScalaSelection.repositionVisibleRect_MouseClicked(this,
				selModel.getWantedVisiblePosition_low(),selModel.getWantedVisiblePosition_high(), fixed));
	}
	
	public void scrollViewToRect(final Rectangle rect) {
		
//		final int curX = layeredPane.getVisibleRect().x;
//		final int tgtX = rect.x;
//		final Rectangle r = new Rectangle();
//		final int delay = 40; //40ms = 25fps
//		
//		final int steps = 100;
//		Thread mover = new Thread() {
//			public void run() {
//				for (int i=0; i!=steps; ++i) {
//					double sigmoidalBooster = 1/(1+Math.exp(5-(((double)i)/30*10)))*30;
//					//System.out.println(sigmoidalBooster);
//					int newX = (int)Math.round(curX + sigmoidalBooster*tgtX);
//					layeredPane.computeVisibleRect(r);
//					r.x = newX;
//					layeredPane.scrollRectToVisible(r);
//					try {
//						Thread.currentThread().sleep(delay);
//					} catch (InterruptedException e) {
//					}
//				}
//				// final moving step
//				layeredPane.scrollRectToVisible(rect);
//			}
//		};
//		mover.start();
//		
		
		this.layeredPane.scrollRectToVisible(rect);
	}

//	public Species getTempSelectedSpecies() {
//		return tempSelectedChrome.getSpecies();
//	}

	public Chrome getTempSelectedChrome() {
		return tempSelectedChrome;
	}

	public void setTempSelectedChrome(Chrome TempSelectedChrome) {
		tempSelectedChrome = TempSelectedChrome;
	}

	public Organiser getOrganiser() {
		return org;
	}

	public void clearData() {
		paintManager.destroy();
		
	}
	
	public void computeVisiblePositions(){
		setDirectpaint(visRangeObject.getScale()<ConstantData.DIRECTPAINT_BELOW_BP_PER_PIXELS);
		visRangeObject.update();
	}

	public int getCenterposition_x() {
		return visRangeObject.getCenterposition_x();
	}

	public long getCenterposition_bp() {
		return visRangeObject.getCenterposition_bp();
	}

	public long getVisPos_low_bp() {
		return visRangeObject.getVisPos_low_bp();
	}

	public long getVisPos_high_bp() {
		return visRangeObject.getVisPos_high_bp();
	}
	
	public int getVis_centerPos_x() {
		return visRangeObject.getVis_centerPos_x();
	}

	public int getVis_leftPos_x() {
		return visRangeObject.getVis_leftPos_x();
	}

	public int getVis_rightPos_x() {
		return visRangeObject.getVis_rightPos_x();
	}

	public double getWidth_usableSpace() {
		Rectangle visibleRect = getVisibleRectOfLayeredPane();
		if(visibleRect!=null){
			return visibleRect.getWidth()-getWidth_userpanel();
		}
		return 0;
	}

	/**
	 * centering visibleRect to center_bp.
	 * @param center_bp
	 * @param center_x
	 */
	public void centerView(long center_bp, int center_x) {
		if(layeredPane!=null){
			scrollViewToRect(OperationsForScalaSelection.repositionVisibleRect_Centering(this, center_bp));
		}
	}
	
	public void resizeLayeredPane(int visLowPos_new_bp, int visHighPos_new_bp,int newWidthLayeredPane,SizeMode sizemode) {
		selModel.setWantedVisiblePositions(visLowPos_new_bp,visHighPos_new_bp);
		setSizeLayeredPane((int)newWidthLayeredPane,SizeMode.SIZE_ZOOM);
	}

	/**
	 * Is true if renderer has to paint directly into painting area.
	 * @return
	 */
	public boolean isDirectpaint() {
		return directpaint;
	}

	/**
	 * Is true if renderer has to paint directly into painting area.
	 * @param directpaint
	 */
	public void setDirectpaint(boolean Directpaint) {
		directpaint = Directpaint;
	}

	public int getSearchedChromePosition() {
		return fps.getPosition();
	}

	public FindPositionSettings getFps() {
		return fps;
	}

	public boolean isWidthChanged() {
		return widthChanged;
	}

	public SelectionsModel getSelectionModel() {
		return selModel;
	}

	public void stateChanged(ChangeEvent e) {
		if(actualChromeData!=null){
			initializeSpeciesAndChrome();
//			tempSelectedChrome = actualChromeData.getActualChrome();
//			resetActualData();
			actualizeTracks();
		} 
	}

	public void removeTrack(AbstractTrackPlugin trackPlugin) {
		trackpositioner.removeTrack(trackPlugin);
		paintManager.removeTrack(trackPlugin.getTrack());
	}

	public void stateChanged(SettingChangeEvent e) {
		actualizeTracks();
	}

	//*******************************************************
	// Methods not necessary for GenomeOverviewModel.
	//*******************************************************
	public int getColumnCount() {
		return 0;
	}

	public int getRowCount() {
		return 0;
	}

	public Object getValueAt(int arg0, int arg1) {
		return null;
	}

	public Set<Probe> getSelectedProbes() {
		if(viewModel!=null)
			return viewModel.getSelectedProbes();
		return Collections.emptySet();
	}
	
	public GenomeDAO getDAO() {
		return layeredPane.christianHatGelogen();
	}

	
	// === MOUSE MARKER VERTICAL LINE
	
	/** draw a mouse line at position X after removing the previously drawn mouse line
	 * @param mouseposx screen coordinate of mouse x (MouseEvent.getLocationOnScreenX), or <0 to hide mouse marker.
	 */
	public void updateMouseLinePosition(int mouseposx) {
		int marker = (showMarker.getBooleanValue()) ? mouseposx-scrollpane.getLocationOnScreen().x : -1;
		scrollpane.setMarker(marker);
	}
	
	public Setting getShowMarkerSetting() {
		return showMarker;
	}


}
