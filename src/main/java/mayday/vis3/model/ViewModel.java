package mayday.vis3.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.DataSet;
import mayday.core.EventFirer;
import mayday.core.Experiment;
import mayday.core.MasterTableEvent;
import mayday.core.MasterTableListener;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.probelistmanager.ProbeListManagerEvent;
import mayday.core.probelistmanager.ProbeListManagerListener;
import mayday.core.structures.linalg.impl.ViewModelProbeMatrix;
import mayday.core.structures.linalg.matrix.AbstractMatrix;

public class ViewModel implements ProbeListListener, ProbeListManagerListener, MasterTableListener {

	protected Visualizer visualizer;
	
	public Visualizer getVisualizer() {
		return visualizer;
	}
	
	protected DataSet dataSet;
	
	private ChangeListener manipulatorListener; 
	
	public DataSet getDataSet() {
		return dataSet;
	}

	// We have a set of probe lists as selected by the user with their ordering given by probelistmanager
	protected List<ProbeList> naturalProbeListSelection = Collections.synchronizedList(new LinkedList<ProbeList>());
	
	// We have a second set of probe lists for efficient rendering
	protected List<ProbeList> optimizedProbeListSelection = Collections.synchronizedList(new LinkedList<ProbeList>());
	
	// We have a set of selected Probes
	final protected Set<Probe> probeSelection = Collections.synchronizedSet(new HashSet<Probe>());
	
	// We have a set of selected experiments
	// this property is not observed by most plots
	final protected Set<Experiment> experimentSelection = Collections.synchronizedSet(new HashSet<Experiment>());
	
	// We cache a set of all probes for speedup
	protected Set<Probe> allProbes = Collections.synchronizedSet(new HashSet<Probe>());
	
	protected ProbeDataManipulator dataManipulator = new ProbeDataManipulator();
	
	protected ProbeListSorter probeListSorter;
	
	private HashMap<ProbeListListener,Boolean> RefreshingListeners= new HashMap<ProbeListListener,Boolean>();
	
	protected EventFirer<ViewModelEvent, ViewModelListener> eventfirer = new EventFirer<ViewModelEvent, ViewModelListener>() {
		protected void dispatchEvent(ViewModelEvent event, ViewModelListener listener) {
			listener.viewModelChanged(event);
		}		
	};
	
	protected boolean isDisposed=false;
	
	public ViewModel(Visualizer viz, DataSet ds, List<ProbeList> initialSelection) {
		visualizer = viz;
		dataSet = ds;
		
		if (initialSelection!=null) {
			naturalProbeListSelection.addAll(initialSelection);
			// insert listeners into all probelists
			for (ProbeList pl : initialSelection)
				pl.addProbeListListener(this);
		}
		
		probeListSorter = new ProbeListSorter(this);
		
		buildOptimizedProbeListSelection();
		translateProbeListOrderingChangedEvent();
		
		dataSet.getProbeListManager().addProbeListManagerListener(this);
		manipulatorListener=new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				eventfirer.fireEvent(new ViewModelEvent(ViewModel.this, ViewModelEvent.DATA_MANIPULATION_CHANGED));	
			}
		};
		dataManipulator.addChangeListener(manipulatorListener);
		
		dataSet.getMasterTable().addMasterTableListener(this);
	}
	
	
	protected synchronized void buildOptimizedProbeListSelection() {
		optimizedProbeListSelection.clear();
		Set<Probe> alreadyPainted = new TreeSet<Probe>();
		for (ProbeList p : naturalProbeListSelection) {
			VolatileProbeList vpl = new VolatileProbeList(dataSet);
			Collection<Probe> cp = p.getAllProbes();
			cp.removeAll(alreadyPainted);
			vpl.setProbes(cp);
			vpl.setName(p.getName()+" (optimized)");
			vpl.setOriginalName(p.getName());
			vpl.setColor(p.getColor());
			optimizedProbeListSelection.add(vpl);
			alreadyPainted.addAll(cp);
		}
		allProbes = alreadyPainted;
	}
	
	protected synchronized void rebuildOptimizedProbeListSelection(int startingAt) {
		if (startingAt==naturalProbeListSelection.size())
			return;
		Set<Probe> alreadyPainted = new TreeSet<Probe>();
		for(int i=0; i!=startingAt; ++i) {
			ProbeList vpl = naturalProbeListSelection.get(i);
			alreadyPainted.addAll(vpl.getAllProbes());
		}
		for(int i=startingAt; i!=optimizedProbeListSelection.size(); ++i) {
			VolatileProbeList vpl = (VolatileProbeList)optimizedProbeListSelection.get(i);
			ProbeList pl = naturalProbeListSelection.get(i);
			Collection<Probe> cp = pl.getAllProbes();
			cp.removeAll(alreadyPainted);
			vpl.setProbes(cp);
			vpl.setName(pl.getName()+" (optimized)");
			vpl.setOriginalName(pl.getName());
			vpl.setColor(pl.getColor());
			alreadyPainted.addAll(cp);
		}
		allProbes = alreadyPainted;
	}
	
	protected synchronized void translateProbeListContentChangedEvent(ProbeList changedProbeList) {
		// which one is it?
		int pos = naturalProbeListSelection.indexOf(changedProbeList);
		rebuildOptimizedProbeListSelection(pos);
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.TOTAL_PROBES_CHANGED));
	}
	
	protected synchronized void translateProbeListOrderingChangedEvent() {
		// where does this change happen?
		int pos=0;
		LinkedList<ProbeList> newOrder = getSelectedProbeListOrdering();
		if (newOrder.size()!=naturalProbeListSelection.size()) {
			System.out.println("ProbeList ordering can not be established right now");
			return;
		}
		for(pos=0; pos!=naturalProbeListSelection.size(); ++pos) 
			if (naturalProbeListSelection.get(pos)!=newOrder.get(pos))
				break;
		for (int i=pos; i!=newOrder.size(); ++i)
			naturalProbeListSelection.set(i, newOrder.get(i));
		rebuildOptimizedProbeListSelection(pos);
	}
	
	// this is package private so that only vis3.model can access it
	void translateProbeListOrderingChangedEvent_calledFromProbeListSorter() {
		translateProbeListOrderingChangedEvent();
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBELIST_ORDERING_CHANGED));
	}
	
	protected LinkedList<ProbeList> getSelectedProbeListOrdering() {
		return probeListSorter.getSelectedProbeListOrdering(naturalProbeListSelection);
	}
	
	public void addProbeListToSelection(ProbeList pl) {
		if (naturalProbeListSelection.contains(pl))
			return;
		// add to selection
		removeRefreshingListenersFromAll();
		naturalProbeListSelection.add(pl);
		optimizedProbeListSelection.add(new VolatileProbeList(dataSet));		
		addRefreshingListenersToAll();
		// update ordering --> updates optimized lists
		translateProbeListOrderingChangedEvent();
		// if the new list is still at the end of naturalProbeListSelection, ordering update hasn't done anything
		if (naturalProbeListSelection.indexOf(pl)==naturalProbeListSelection.size()-1) {
			rebuildOptimizedProbeListSelection(naturalProbeListSelection.indexOf(pl));
		}
		pl.addProbeListListener(this);
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBELIST_SELECTION_CHANGED));
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.TOTAL_PROBES_CHANGED));
	}
	
	public void removeProbeListFromSelection(ProbeList pl) {
		if (!naturalProbeListSelection.contains(pl))
			return;
		pl.removeProbeListListener(this);
		int pos = naturalProbeListSelection.indexOf(pl);
		removeRefreshingListenersFromAll();
		naturalProbeListSelection.remove(pos);		
		optimizedProbeListSelection.remove(pos);
		addRefreshingListenersToAll();
		rebuildOptimizedProbeListSelection(pos);
		allProbes.clear();
		for (ProbeList p : optimizedProbeListSelection)
			allProbes.addAll(p.getAllProbes());
		if (probeSelection.retainAll(allProbes)) {
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBE_SELECTION_CHANGED));
		}
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBELIST_SELECTION_CHANGED));
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.TOTAL_PROBES_CHANGED));
	}
	
	
	public void probeListChanged(ProbeListEvent event) {
		switch(event.getChange()) {
		case ProbeListEvent.PROBELIST_CLOSED:
			removeProbeListFromSelection((ProbeList)event.getSource());
			break;
		case ProbeListEvent.CONTENT_CHANGE:
			translateProbeListContentChangedEvent((ProbeList)event.getSource());
			// viewers will have to listen here, or on one of the VolatileProbeLists' CONTENT_CHANGE event
			break;
		case ProbeListEvent.LAYOUT_CHANGE:
			// change color of the corresponding optimizedPL
			int pos = naturalProbeListSelection.indexOf((ProbeList)event.getSource());
			optimizedProbeListSelection.get(pos).setColor(((ProbeList)event.getSource()).getColor());
			break;
		}		
	}
	
	public int indexOf(ProbeList pl) {
		int i = naturalProbeListSelection.indexOf(pl);
		if (i==-1)
			i = optimizedProbeListSelection.indexOf(pl);
		return i;
	}
	

	public void probeListManagerChanged(ProbeListManagerEvent event) {
		switch(event.getChange()) {
		case ProbeListManagerEvent.ORDER_CHANGE:
			translateProbeListOrderingChangedEvent();
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBELIST_ORDERING_CHANGED));
			break;
		}
	}

	public ProbeList getTopPriorityProbeList(Probe pb) {
		for (ProbeList pl : optimizedProbeListSelection)
			if (pl.contains(pb))
				return pl;
		return null;
	}
	
	public Set<Probe> getSelectedProbes() {
		return Collections.unmodifiableSet(probeSelection);
	}
	
	public boolean isSelected(Probe pb) {
		return probeSelection.contains(pb);
	}
	
	public void selectProbe(Probe pb) {
		if (probeSelection.add(pb)) {
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBE_SELECTION_CHANGED));
		}
	}
	
	public void unselectProbe(Probe pb) {
		if (probeSelection.remove(pb)) {
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBE_SELECTION_CHANGED));
		}
	}
	
	public void toggleProbeSelected(Probe pb) {
		if (probeSelection.contains(pb))
			unselectProbe(pb);
		else
			selectProbe(pb);
	}
	
	public void toggleProbesSelected(Set<Probe> probes) {
		for (Probe pb : probes)
			if (probeSelection.contains(pb)){
				probeSelection.remove(pb);
			}
				
			else
				probeSelection.add(pb);
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBE_SELECTION_CHANGED));
	}
	
	public void setProbeSelection(Collection<Probe> newSelection) {
		setProbeSelectionSilent(newSelection);
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.PROBE_SELECTION_CHANGED));
	}
	
	public void setProbeSelectionSilent(Collection<Probe> newSelection) 
	{
		if (newSelection.size()==probeSelection.size()) {
			TreeSet<Probe> tmp = new TreeSet<Probe>(probeSelection);
			tmp.removeAll(newSelection);
			if (tmp.size()==0)
				return;  // identical, no change
		}
		probeSelection.clear();
		probeSelection.addAll(newSelection);
	}
	
	public void setProbeSelection(Probe pb) {
		LinkedList<Probe> probes = new LinkedList<Probe>();
		if (pb!=null) 
			probes.add(pb);
		setProbeSelection(probes);
	}
	
	
	public Set<Experiment> getSelectedExperiments() {
		return Collections.unmodifiableSet(experimentSelection);
	}
	
	public boolean isSelected(Experiment e) {
		return experimentSelection.contains(e);
	}
	
	public void selectExperiment(Experiment e) {
		if (experimentSelection.add(e)) {
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.EXPERIMENT_SELECTION_CHANGED));
		}
	}
	
	public void unselectExperiment(Experiment e) {
		if (experimentSelection.remove(e)) {
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.EXPERIMENT_SELECTION_CHANGED));
		}
	}
	
	public void toggleExperimentSelected(Experiment e) {
		if (experimentSelection.contains(e))
			unselectExperiment(e);
		else
			selectExperiment(e);
	}
	
	public void toggleExperimentsSelected(Set<Experiment> experiments) {
		for (Experiment e : experiments)
			if (experimentSelection.contains(e))
				experimentSelection.remove(e);							
			else
				experimentSelection.add(e);
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.EXPERIMENT_SELECTION_CHANGED));
	}
	
	public void setExperimentSelection(Collection<Experiment> newSelection) {
		setExperimentSelectionSilent(newSelection);
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.EXPERIMENT_SELECTION_CHANGED));
	}
	
	public void setExperimentSelectionSilent(Collection<Experiment> newSelection) 
	{
		if (newSelection.size()==experimentSelection.size()) {
			HashSet<Experiment> tmp = new HashSet<Experiment>(experimentSelection);
			tmp.removeAll(newSelection);
			if (tmp.size()==0)
				return;  // identical, no change
		}
		experimentSelection.clear();
		experimentSelection.addAll(newSelection);
	}
	
	public void setExperimentSelection(Experiment e) {
		LinkedList<Experiment> experiments = new LinkedList<Experiment>();
		if (e!=null) 
			experiments.add(e);
		setExperimentSelection(experiments);
	}
	
	
	public List<ProbeList> getProbeLists(boolean optimized) {
		if (optimized)
			return optimizedProbeListSelection;
		else
			return naturalProbeListSelection;
	}

	public Set<Probe> getProbes() {
		return allProbes;
	}
	

	/** Returns the probe data according to the currently selected manipulation (e.g. z-score normalized)
	 * @param pb The probe to get data from
	 * @return the (transformed) data 
	 */
	public double[] getProbeValues(Probe pb) {
		return dataManipulator.getProbeValues(pb);
	}

	/**
	 * compute the maximum for (a subset of) this visualizer
	 * @param experiments the experiments to evaluate or null to take all experiments
	 * @param subset the probes to look at or null to look at all probes
	 * @return the maximum or NaN if there is no probe in the subset
	 */
	public Double getMaximum( int[] experiments , Collection<Probe> subset) {		
		if (subset==null)
			subset=getProbes();
		return dataManipulator.getMaximum(experiments, subset);
	}
	
	/** convenience method, see getMaximum(int[] experiment, Collection<Probe> subset) */
	public Double getMaximum(int experiment, Collection<Probe> subset) {
		return getMaximum(new int[]{experiment}, subset);
	}
	
	/**
	 * compute the standard deviation for (a subset of) this visualizer
	 * @param experiments the experiments to evaluate or null to take all experiments
	 * @param subset the probes to look at or null to look at all probes
	 * @return
	 */
	public Double getStdev(int[] experiments, Collection<Probe> subset)
	{
		if (subset==null)
			subset=getProbes();
		return dataManipulator.getStdev(experiments, subset);
	}
	
	/** convenience method, see getStdev(int[] experiment, Collection<Probe> subset) */
	public Double getStdev(int experiment, Collection<Probe> subset) {
		return getStdev(new int[]{experiment}, subset);
	}
	
	
	/**
	 * compute the mean for (a subset of) this visualizer
	 * @param experiments the experiments to evaluate or null to take all experiments
	 * @param subset the probes to look at or null to look at all probes
	 * @return
	 */
	public Double getMean(int[] experiments, Collection<Probe> subset)
	{
		if (subset==null)
			subset=getProbes();
		return dataManipulator.getMean(experiments, subset);
	}
	
	/** convenience method, see getMaximum(int[] experiment, Collection<Probe> subset) */
	public Double getMean(int experiment, Collection<Probe> subset) {
		return getMean(new int[]{experiment}, subset);
	}
	
	/**
	 * compute the minimum for (a subset of) this visualizer
	 * @param experiments the experiments to evaluate or null to take all experiments
	 * @param subset the probes to look at or null to look at all probes
	 * @return the minimum or NaN if there is no probe in the subset
	 */
	public Double getMinimum( int[] experiments , Collection<Probe> subset ) {
		if (subset==null)
			subset = getProbes();
		return dataManipulator.getMinimum(experiments, subset);
	}
	
	/** convenience method, see getMinimum(int[] experiment, Collection<Probe> subset) */
	public Double getMinimum(int experiment, Collection<Probe> subset) {
		return getMinimum(new int[]{experiment}, subset);
	}


	public ProbeList.Statistics getStatistics(Collection<Probe> subset)
    {        
        // compute the mean for each experiment        
        if (subset==null)
        	subset = getProbes();
        return dataManipulator.getStatistics(subset, getDataSet().getMasterTable());       
    }
	
	
	public ProbeDataManipulator getDataManipulator() {
		return dataManipulator;
	}
	
	public void setDataManipulator(ProbeDataManipulator dataManipulator) {
		if (this.dataManipulator!=dataManipulator) {
			this.dataManipulator.removeChangeListener(manipulatorListener);
			this.dataManipulator=dataManipulator;
			this.dataManipulator.addChangeListener(manipulatorListener);
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.DATA_MANIPULATION_CHANGED));
		}
	}
	
	public ProbeListSorter getProbeListSorter() {
		return probeListSorter;
	}

	
	public void addViewModelListener(ViewModelListener vml) {
		eventfirer.addListener(vml);		
		if (MaydayDefaults.isDebugMode()) {
			System.out.println("VM "+getVisualizer().getID()+": "+eventfirer.getListeners().size()+" listeners");
			System.out.println("-- added "+vml.getClass()+"@"+vml.hashCode());
		}
		
	}
	
	public void removeViewModelListener(ViewModelListener vml) {
		eventfirer.removeListener(vml);
		if (MaydayDefaults.isDebugMode()) {
			System.out.println("VM "+getVisualizer().getID()+": "+eventfirer.getListeners().size()+" listeners");
			System.out.println("-- removed "+vml.getClass()+"@"+vml.hashCode());
		}
	}
	

	public synchronized void dispose() {
		if (isDisposed)
			return;
		dataSet.getProbeListManager().removeProbeListManagerListener(this);
		for (ProbeList pl : naturalProbeListSelection)
			pl.removeProbeListListener(this);
		dataSet.getMasterTable().removeMasterTableListener(this);
		eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.VIEWMODEL_CLOSED));
		if (MaydayDefaults.isDebugMode()) {
			System.out.println("VM "+getVisualizer().getID()+" CLOSING WITH : "+eventfirer.getListeners().size()+" listeners");
			for (Object o : eventfirer.getListeners())
				System.out.println("--" + o.getClass()+"@"+o.hashCode());
		}
		isDisposed=true; 
	}
	
	public void addRefreshingListenerToAllProbeLists(ProbeListListener pll, boolean optimized) {
		RefreshingListeners.put(pll,optimized);
		for (ProbeList pl : optimized?optimizedProbeListSelection:naturalProbeListSelection)
			pl.addProbeListListener(pll);
	}
	
	public void removeRefreshingListenerToAllProbeLists(ProbeListListener pll) {
		if (!RefreshingListeners.containsKey(pll))
			return;
		for (ProbeList pl : RefreshingListeners.get(pll)?optimizedProbeListSelection:naturalProbeListSelection)
			pl.removeProbeListListener(pll);
		RefreshingListeners.remove(pll);
	}
	
	protected void removeRefreshingListenersFromAll() {
		for (Entry<ProbeListListener,Boolean> e : RefreshingListeners.entrySet())			
			for (ProbeList pl : e.getValue()?optimizedProbeListSelection:naturalProbeListSelection)
				pl.removeProbeListListener(e.getKey());
	}
	protected void addRefreshingListenersToAll() {
		for (Entry<ProbeListListener,Boolean> e : RefreshingListeners.entrySet())			
			for (ProbeList pl : e.getValue()?optimizedProbeListSelection:naturalProbeListSelection)
				pl.addProbeListListener(e.getKey());
	}

	public void masterTableChanged(MasterTableEvent event) {
		if ( event.getChange() == MasterTableEvent.EXPERIMENT_ORDERING_CHANGED )
			eventfirer.fireEvent(new ViewModelEvent(this, ViewModelEvent.DATA_MANIPULATION_CHANGED)); 
		// data manip change is the best fit for exp order change
	}
	
	public AbstractMatrix asMatrix() {
		return new ViewModelProbeMatrix(this);
	}
	
}
