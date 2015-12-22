package mayday.vis3.plots.genomeviz;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.Strand;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.genomeorganisation.Chrome;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeDataSet;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeSettings;
import mayday.vis3.plots.genomeviz.genomeorganisation.GenomeDAO;

@SuppressWarnings("serial")
public abstract class AbstractLogixVizModel extends AbstractTableModel implements ILogixVizModel, SettingChangeListener, ChangeListener{
	
	protected ViewModel viewModel = null;
	protected ChromosomeSettings chromeSettings;
	protected EventListenerList eventListenerList = new EventListenerList();
	
	protected Organiser org = null;
	protected GenomeDAO gdao = null;
	protected ChromosomeDataSet actualChromeData = null;
	
	protected Chrome initialChrome = null;
	protected Chrome tempSelectedChrome = null;
	
	protected String name = ""; 
	protected boolean settedChromeData = false;
	
	public AbstractLogixVizModel(String Name){
		name = Name;

	}
	
	public void initAbstractModel(ViewModel ViewModel, Organiser organiser) {
		viewModel = ViewModel;
		org = organiser;
		gdao = org.getChromeManager();
		org.addListenerToGDAO(this);
		initializeSpeciesAndChrome();
	}
	
	protected void initializeSpeciesAndChrome(){
		Species species = null;
		Chrome chrome = null;

		chrome = org.getTransferredChrome();

		if (chrome != null) {
			actualChromeDataSetted(false);
			initializeActualData();
			actualChromeDataSetted(true);
		} else {
			if (org.getChromeManager() != null) {
				species = gdao.getContainer().keySet().iterator().next();
				chrome = (Chrome)gdao.getContainer().getChromosomes(species).getFirstChromosome();
				actualChromeDataSetted(false);
				if (species != null && chrome != null) {
					setSelectedSpeciesAndChrome(chrome);
					initializeActualData();
					actualChromeDataSetted(true);
				}

			} else {
				System.err.println("ChromeManager - no SpeciesContainer were set");
				System.exit(0);
			}

		}

		internalSpeciesInitialization();
	}

	protected abstract void internalSpeciesInitialization();
	
	private void initializeActualData(){
		actualChromeDataSetted(false);
		setActualData(org.getActualData(getSelectedChrome()));
		actualChromeDataSetted(true);
		
		internalActualDataInitialization();
	}
	
	/**
	 * sets the data of this plot depending on chromosome, happens if new window for another chomosome is choosen or
	 * if another chomosome is choosen in the same window.
	 * @param actualChromeData
	 */
	public void setActualData(ChromosomeDataSet ActualChromeData) {
		dataInitialization(ActualChromeData);
		internalSettingOfActulaData();
	}
	
	protected abstract void internalSettingOfActulaData();

	protected abstract void internalActualDataInitialization();

	public void actualChromeDataSetted(boolean b) {
		settedChromeData = b;
	}
	protected void dataInitialization(ChromosomeDataSet ActualChromeData){
		if(chromeSettings!=null)chromeSettings.removeListener(this);
		actualChromeData = ActualChromeData;
		chromeSettings = actualChromeData.getChromosomeSettings(this);
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
	
	public DataSet getDataSet(){
		return viewModel.getDataSet();
	}
	
	//#############################################################
	/*
	 * For Species selection
	 * 
	 */
	//#############################################################
	public Chrome getActualChrome(){
		if(actualChromeData!=null)return actualChromeData.getActualChrome();
		return initialChrome;
	}
	
	public Species getActualSpecies() {
		if(actualChromeData!=null)return actualChromeData.getActualSpecies();
		return initialChrome.getSpecies();
	}
	/**
	 * 
	 * @param species
	 * @param chrome
	 */
	protected void setSelectedSpeciesAndChrome(Chrome chrome){
		initialChrome = chrome;
	}
	
	public Species getSelectedSpecies(){
		return initialChrome.getSpecies();
	}
	
	public Chrome getSelectedChrome(){
		return initialChrome;
	}
	
	/**
	 * 
	 * @param chrome
	 */
	public void setTempSelectedChrome(Chrome chrome) {
		this.tempSelectedChrome = chrome;
	}

	/**
	 * 
	 * @return
	 */
	public Chrome getTempSelectedChrome() {
		return this.tempSelectedChrome;
	}

	/**
	 * 
	 * @return
	 */
	public Species getTempSelectedSpecies() {
		return tempSelectedChrome.getSpecies();
	}
	
	/**
	 * sets the selected species and chromosome and get actual data from organiser.
	 */
	public void setChromeAndData() {
		setSelectedSpeciesAndChrome(getTempSelectedChrome());
		initializeActualData();
	}
	
	//#############################################################
	/*
	 * Connection to data
	 * 
	 */
	//#############################################################
	/**
	 * returns the first bp of chromosome.
	 */
	public long getChromosomeStart() {
		if(chromeSettings!=null)return chromeSettings.getChromosomeStart();
		return 0;
	}

	/**
	 * returns the last bp of chromosome
	 * @return
	 */
	public long getChromosomeEnd() {
		if(chromeSettings!=null)return chromeSettings.getChromosomeEnd();
		return 0;
	}
	
	/**
	 * returns the length of chromosome.
	 * @return
	 */
	public long getLengthOfChromosome() {
		if(chromeSettings!=null)return chromeSettings.getChromosomeLength();
		return 0;
	}

	public long getViewStart() {
		if(chromeSettings!=null)return chromeSettings.getViewStart();
		return 0;
	}
	
	public long getViewEnd() {
		if(chromeSettings!=null)return chromeSettings.getViewEnd();
		return 0;
	}
	
	public long getViewLength() {
		if(chromeSettings!=null)return chromeSettings.getViewLength();
		return 0;
	}
	
	public ChromosomeDataSet getData() {
		return actualChromeData;
	}
	
	protected Set<Probe> getProbes(long first, long last, Strand strand) {
		
		Set<Probe> minusProbes = new HashSet<Probe>();
		
		for (LocusGeneticCoordinateObject<Probe> olgcp : getData().getProbes(first, last, strand)) {
			Probe pb = olgcp.getObject();
			minusProbes.add(pb);
		}

		return minusProbes;
	}
	
	protected MultiTreeMap<Double,Probe> getProbeValues_Abs(long first, long last, Strand strand, int exp) {
		MultiTreeMap<Double,Probe> minusProbes = new MultiTreeMap<Double,Probe>();
			
		for (LocusGeneticCoordinateObject<Probe> olgcp : getData().getProbes(first, last, strand)) {
			Probe pb = olgcp.getObject();
			double val = viewModel.getProbeValues(pb)[exp];
			minusProbes.put(Math.abs(val), pb);
		}
		
		return minusProbes;
	}

	
	protected Double getExpValue(long from, long to, Strand strand, SplitView split, int exp) {
		Set<Probe> minusProbes = getProbes(from,to,strand);
		double val = Double.NaN;
		
		if(split.equals(SplitView.mean)){
			val= getMean(exp,minusProbes);
		} else if(split.equals(SplitView.min)){
			val= getMin(exp,minusProbes);
		} else if(split.equals(SplitView.max)){
			val= getMax(exp,minusProbes);
		}
		return val;
	}

	public Double getMin(int actualExperiment, Set<Probe> list) {
		return viewModel.getMinimum(actualExperiment,list);
	}
	
	public double getAbsoluteMaxOfProbes(int exp, Strand strand) {
		return getAbsoluteMaxOfProbes(new int[]{exp}, strand);
	}
	
	public double getAbsoluteMaxOfProbes(int[] exp, Strand strand) {
		Set<Probe> set = Collections.emptySet();
			if(strand.equals(Strand.PLUS)){
				set = actualChromeData.getForwardProbesetChromosome();
			} else if(strand.equals(Strand.MINUS)){
				set = actualChromeData.getBackwardProbesetChromosome();
			}
		if(set != null && !set.isEmpty()){
			return viewModel.getMaximum(exp,set);
		} else {
			return 0;
		}		
	}
	
	public double getAbsoluteMinOfProbes(int exp, Strand strand) {
		return getAbsoluteMinOfProbes(new int[]{exp}, strand);
	}
	
	public double getAbsoluteMinOfProbes(int[] exp, Strand strand) {
		Set<Probe> set = Collections.emptySet();

		if (strand.equals(Strand.PLUS)) {
			set = actualChromeData.getForwardProbesetChromosome();
		} else if (strand.equals(Strand.MINUS)) {
			set = actualChromeData.getBackwardProbesetChromosome();
		}
		if (set != null && !set.isEmpty()) {
			return viewModel.getMinimum(exp, set);
		} else {
			return 0;
		}
	}
	
	public Double getMean(int actualExperiment, Set<Probe> list) {
		return viewModel.getMean(actualExperiment,list);
	}
	public Double getMax(int actualExperiment, Set<Probe> list) {
		return viewModel.getMaximum(actualExperiment, list);
	}
	
	public Probe getMaxProbe(int actualExperiment, Set<Probe> subset) {

		Double max = Double.NEGATIVE_INFINITY;
		Probe probe = null;
		if (subset != null) {
			for (Probe pb : subset) {
				double[] val = viewModel.getProbeValues(pb);
				if(max < val[actualExperiment]){
					max = val[actualExperiment];
					probe = pb;
				}
			}
			if (max == Double.NEGATIVE_INFINITY)
				max = Double.NaN;
		}
		return probe;
	}
	
	public Probe getMinProbe(int actualExperiment, Set<Probe> subset) {
		Double min = Double.POSITIVE_INFINITY;
		Probe probe = null;
		if (subset != null) {
			for (Probe pb : subset) {
				double[] val = viewModel.getProbeValues(pb);
				if(min > val[actualExperiment]){
					min = val[actualExperiment];
					probe = pb;
				}
			}
			if (min==Double.POSITIVE_INFINITY)
				min=Double.NaN;

		}
		return probe;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public long getSkipValue(){
		if(chromeSettings!=null)return chromeSettings.getSkipValue();
		return 0;
	}
	
	public ChromosomeSettings getChromosomeSettings() {
		return chromeSettings;
	}

	public SettingChangeListener getSettingsChangeListener() {
		return this;
	}
	
	public abstract void stateChanged(SettingChangeEvent e);
	public abstract void stateChanged(ChangeEvent e);
	
	public MasterTable getMasterTable() {
		return viewModel.getDataSet().getMasterTable();
	}
	
	/**
	 * returns the strand of probe.
	 * @param probe
	 * @return char (+,-)
	 */
	public Strand getStrandOfProbe(Probe probe){
		return this.actualChromeData.getStrand(probe);
	}
	
	public long getStartPosition(Probe probe){
		return this.actualChromeData.getStartPosition(probe);
	}

	public long getEndPosition(Probe probe){
		return this.actualChromeData.getEndPosition(probe);
	}
	
//	public Set<Probe> getAllProbesOfChrome(){
//		if(actualChromeData!=null)return actualChromeData.getProbesetChromosome();
//		return Collections.emptySet();
//	}
	
//	public boolean containsProbe(Probe probe){
//		return actualChromeData.containsProbe(probe);
//	}
	
	public void resetActualData(){
		dataInitialization(org.getActualData(tempSelectedChrome));
	}
	
	public abstract int getColumnCount();
	public abstract int getRowCount();
	public abstract Object getValueAt(int arg0, int arg1);
}
