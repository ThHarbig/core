package mayday.vis3.plots.genomeviz;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.vis3.ColorProvider;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.SelectedRange;
import mayday.vis3.plots.genomeviz.genomeorganisation.Chrome;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeDataSet;
import mayday.vis3.plots.genomeviz.genomeorganisation.GenomeDAO;
import mayday.vis3.plots.genomeviz.genomeorganisation.ViewModelController;

public final class Organiser{
 
	private static Organiser instance = null;

	
	protected GenomeDAO genomeDAO = null;
	protected static ViewModel viewModel;
	protected boolean dataSetted = false;			// true only if organiser holds all data
	
	protected Chrome tempChrome = null;				// transferred species and chromosome
	protected SelectedRange selectedRange = null;
	protected Set<Probe> selectedProbes = null;
	protected KindOfData kindOfData = KindOfData.STANDARD;		// for new chromosome window, indicates if data is standard, or by range (probes, positions)
	protected ColorProvider coloring = null;
	protected Set<PlotComponent> pcs = new HashSet<PlotComponent>();
	
	
	protected ViewModelController vmc;
	
	/**
	 * @param ViewModel 
	 * @param pc 
	 * 
	 */
	private Organiser(ViewModel ViewModel){
		viewModel = ViewModel;
		genomeDAO = new GenomeDAO(viewModel);
		vmc = new ViewModelController(this,genomeDAO, viewModel);
	}

	public synchronized static Organiser getInstance(ViewModel ViewModel){
		ViewModel.addViewModelListener(new ViewModelListener() {
			public void viewModelChanged(ViewModelEvent vme) { 
				if (vme.getChange()==ViewModelEvent.VIEWMODEL_CLOSED)
					instance = null;
			}
		});
		
		if (instance==null && viewModel!=ViewModel)
			instance = new Organiser(ViewModel);
			
		return instance;
	}
	
	/**
	 * initializes the actual chromosome data for the table model, check if range is selected, 
	 * if true actualChromeData is created in another way.
	 * @param species
	 * @param chrome
	 * @param  
	 * @return ActualChromeData
	 */
	public ChromosomeDataSet getActualData(Chrome chrome){
		if (genomeDAO != null) {
				switch (kindOfData) {
				case STANDARD:
					return genomeDAO.getActualData(chrome);
				case BY_POSITION:
					return genomeDAO.getActualRangeData(chrome,selectedRange);
				default:
					System.err.println("ActualChromeData : getActualData - KindOfData not valid");
					break;
				}

			
		}
		System.err.println("genomeManager is NULL");
		return null;
	}
	
	/**
	 * sets temporary species and chomosome for the new window, so new window is opened with selected species
	 * and chomosome.
	 * @param species
	 * @param chrome
	 */

	public void setTempSpeciesAndChrome(Chrome chrome){
		this.tempChrome = chrome;
	}
	
	/**
	 * if new selected species and chromosome is opened in the same window, so clear temporary saved species.
	 */
	public void clearTempSpeciesAndChrome() {
		this.tempChrome = null;
	}
	

	
	/**
	 * For view of chromosome in new window, returns the selected chromosome to show
	 * in new window.
	 * @return selected chromosome
	 */
	public Chrome getTransferredChrome(){
		if(this.tempChrome != null) return this.tempChrome;
		return null;
	}
	
	public GenomeDAO getChromeManager(){
		return genomeDAO;
	}

	/**
	 * set if data is setted.
	 * @param val
	 */
	public void setDataSetted(boolean val){
		dataSetted = val;
//		if(dataSetted){
//			System.out.println("data setted.");
//		} else{
//			System.out.println("data not setted");
//		}
	}
	
	/**
	 * true if data from organiser is setted.
	 * @return
	 */
	public boolean isDataSetted(){
		if(genomeDAO !=null && genomeDAO.containsLoci()){
			return true;
		}
		return false;
	}
	
	public void setRangeSelection_withPos(SelectedRange selectedRange){
		this.selectedRange = selectedRange;
	}
	
	public void setRangeSelection_withProbes(Set<Probe> set){
		this.selectedProbes = set;
	}
	
	/**
	 * return the type of showing new chromosome window (complete window or only a selection by selected probes or selected positions).
	 * @return
	 */
	public KindOfData getKindOfData(){
		return kindOfData;
	}

	public void setKindOfData(KindOfData kindOfData) {
		this.kindOfData = kindOfData;
	}

	public void removePlotComponent(PlotComponent pv){
		pcs.remove(pv);
		if (pcs.size()==0)
			dispose();
	}

	public void addPlotComponent(PlotComponent pv){
		if (pcs.size()==0)
			vmc.addNotify();
		pcs.add(pv);
		
	}

	public ColorProvider getColoring() {
		return coloring;
	}

	public void setColoring(ColorProvider coloring) {
		this.coloring = coloring;
	}

	public boolean containsLoci(){
		return this.genomeDAO.containsLoci();
	}

	public void addListenerToGDAO(ChangeListener cl) {
		if (genomeDAO != null) genomeDAO.addChangeListener(cl);
	}
	
	public void dispose() {
		vmc.removeNotify();
	}
}
