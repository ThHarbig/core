package mayday.vis3.plots.genomeviz.genomeorganisation;

import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.genetics.LocusMIO;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.SelectedRange;

public class GenomeDAO{
 
	protected ViewModel viewModel = null;
	protected MIGroup locusMioGroup = null;
	
	protected ChromosomeSetContainer chromeSetContainer = null;
	protected HashMap<Chrome,ChromosomeDataSet> complete_species_chrome_set = new HashMap<Chrome, ChromosomeDataSet>();
	protected boolean lociData = false;
	protected EventListenerList eventListenerList = new EventListenerList();
	
	protected final static GeneticCoordinate defaultCoordinate = new GeneticCoordinate("None> 0: 0-0:+", ChromosomeSetContainer.getDefault());
	
	public GenomeDAO(ViewModel ViewModel){
		viewModel = ViewModel;
		init();
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
	
	public ChromosomeSetContainer getContainer() {
		return chromeSetContainer;
	}
	

	
	protected void init_species() {
//		System.out.println("------------------------");
//		System.out.println("INIT GENOME_DAO");
//		System.out.println("------------------------");

		if (locusMioGroup==null) {			
			DataSet ds = viewModel.getDataSet();
			MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(ds.getMIManager(), LocusMIO.class);
			if (mgsd.getSelectableCount()>1) {
				mgsd.setModal(true);	
				mgsd.setDialogDescription("Please select locus information for this genome view model");
				mgsd.setVisible(true);
				MIGroupSelection<MIType> mgs = mgsd.getSelection();
				if (mgs.size()>0)
					locusMioGroup = mgsd.getSelection().get(0);				
			} else {
				MIGroupSelection<MIType> group = ds.getMIManager().getGroupsForType(LocusMIO.myType);
				if (group.size()>0)
					locusMioGroup = group.get(0);
			}
		}
		
		// Fill species only if locus information is available
		if(locusMioGroup != null){
			init_dataContainer();
		}
	}
	
	/**
	 * initialize actual data, fires event if data initialized.
	 */
	public void init(){
		complete_species_chrome_set.clear();
		init_species();	
		fireChanged();
	}
	
	public MIGroup getMIGroup(){
		return locusMioGroup;
	}
	
	/**
	 * returns the actualChromeData for whole selected species and chromosome to show in equal or separate window.
	 * @param species
	 * @param chrome
	 * @return
	 */
	public ChromosomeDataSet getActualData(Chrome chrome){
		ChromosomeDataSet ds = complete_species_chrome_set.get(chrome);
		if (ds == null) {
			complete_species_chrome_set.put(chrome, ds = new ChromosomeDataSet(locusMioGroup, chrome));			
		}
		return ds;
	}
	
	/**
	 * returns actualChromeData for selected species and chromosome, but here only a cutout of probes rather 
	 * a cutout of range is used to create actualChromeData.
	 * @param species
	 * @param chrome
	 * @param range
	 * @return
	 */
	public ChromosomeDataSet getActualRangeData(Chrome chrome, SelectedRange range){
		return new ChromosomeDataSet(locusMioGroup, chrome, range);
	}
	
	protected void init_dataContainer(){
//		System.out.println("-----------------------------------------------");
//		System.out.println("GenomeDAO initialize all species and chromosome");
//		System.out.println("-----------------------------------------------");
		
		long startTime = System.currentTimeMillis();
		
		resetAllChromosomes();

		for(Probe probe: viewModel.getProbes()){
			// Fill new Data structure only if probe contains MIO information
			LocusMIO lm;
			if((lm=(LocusMIO) locusMioGroup.getMIO(probe))!= null){
				AbstractGeneticCoordinate c = lm.getValue().getCoordinate();
				Chromosome chromosome = c.getChromosome();
				Species species = chromosome.getSpecies();
				String chromeId = chromosome.getId();
				long start = c.getFrom();
				long end = c.getTo();
				
				Chrome chrome = (Chrome)this.chromeSetContainer.getChromosome(species, chromeId, chromosome.getLength());
				chrome.addLocus(start, end, c.getStrand(), probe);
				lociData = true;
			}
		}

		
		// extend chrome length if correct length of source chromosome was known
		
		// time measure
		long endTime = System.currentTimeMillis();
		
	    System.out.println ("Genome Viz: Time to build data structure: " + (endTime-startTime) + " ms"); 
	    // time measure 
	}

	/**
	 * reset all chromosomes before adding actual probes.
	 */
	private void resetAllChromosomes() {
		chromeSetContainer = new ChromosomeSetContainer(new Chrome.Factory());
	}
	
	//#############################################################
	/*
	 * Operations on LocusMIO
	 * 
	 */
	//#############################################################
	protected AbstractGeneticCoordinate getCoordinate(Probe pb) {
		LocusMIO lm;
		if (locusMioGroup!=null && (lm = (LocusMIO)locusMioGroup.getMIO(pb))!=null)
			return lm.getValue().getCoordinate();
		return defaultCoordinate;
	}
	
	protected Species getSpeciesOfProbe(Probe probe){
		return getCoordinate(probe).getChromosome().getSpecies();		
	}
	
	protected String getChromosomeId(Probe probe){
		return getCoordinate(probe).getChromosome().getId();
	}
	
	public long getStartPosition(Probe probe) {
		return getCoordinate(probe).getFrom();
	}

	public long getEndPosition(Probe probe) {
		return getCoordinate(probe).getTo();
	}

	public boolean containsLoci() {
		return lociData;
	}
	
	/**
	 * clear actual dataset if data changed or viewmodel is closed.
	 */
	public void clear() {
		chromeSetContainer = null;
	}
}
