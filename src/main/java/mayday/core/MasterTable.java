package mayday.core;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.ProbeListImageStorage;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.tasks.AbstractTask;

/**
 * @author neil
 * @version 
 */
public class MasterTable
{
    protected DataSet dataSet;
    
    protected Map<String,Probe> probes;
    protected ArrayList<Experiment> experiments;
    protected Map<String,Experiment> experimentIndex = new HashMap<String,Experiment>(); // lazily filled
    

    protected boolean isUniquifying;
    
	protected EventFirer<MasterTableEvent, MasterTableListener> eventfirer = new EventFirer<MasterTableEvent, MasterTableListener>() {
		protected void dispatchEvent(MasterTableEvent event, MasterTableListener listener) {
			listener.masterTableChanged(event);
		}		
	};

    private boolean isSilent; // indicates whether listeners are notified or not
    
    
	public MasterTable( DataSet dataSet )
    {
        this.dataSet = dataSet;
        
        //MZ 16.03.2004
        // now the dataset knows about the mastertable
        this.dataSet.setMasterTable(this);
        
        this.experiments = new ArrayList<Experiment>();
        
        this.probes = new HashMap<String, Probe>();
       
    }
    
    
    
    public void addMasterTableListener( MasterTableListener listener )
    {
    	eventfirer.addListener( listener );
    }
    
    
    public void removeMasterTableListener( MasterTableListener listener )
    {
    	eventfirer.removeListener( listener );
    }
    
    
    protected void fireMasterTableChanged( int change )
    {
        if ( isSilent() )
            return;
        eventfirer.fireEvent(new MasterTableEvent( this, change ));
    }
    
    
    public boolean isSilent()
    {
        return ( isSilent );
    }
    
    
    public void setSilent( boolean isSilent )
    {
        this.isSilent = isSilent;
    }
    
    public List<Experiment> getExperiments() {
    	return Collections.unmodifiableList(experiments);
    }
    
    
    public void clear()
    {
        probes.clear();
        experiments.clear();
        
        fireMasterTableChanged( MasterTableEvent.SYSTEM_PROBE_REMOVED_CHANGE |
            MasterTableEvent.USER_PROBE_REMOVED_CHANGE );
    }
    
    
    public DataSet getDataSet()
    {
        return ( this.dataSet );
    }
    
    
    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }
    
    
    public Map<String,Probe> getProbes()
    {
        return ( this.probes );
    }
  
    
	public void addProbe( Probe probe )
    throws RuntimeException
    {
		if (probe.getName()==null) {
			throw new RuntimeException("Unable to add an UNNAMED probe to the mastertable!");
		}
        if ( probe.getNumberOfExperiments() != this.getNumberOfExperiments() )
        {
            throw ( new RuntimeException( probe.getNumberOfExperiments() +
                " experiments found, but " +
                this.getNumberOfExperiments() +
                " experiments expected. " + 
                "Unable to add probe \"" +
                probe.getName() +
            "\" to master table." ) );
        }
        
        if ( this.probes.containsKey( probe.getName() ) )
        {
            if ( this.isUniquifying )
            {
                while ( this.probes.containsKey( probe.getName() ) )
                {
                    if ( probe.getName() == null ||
                            probe.getName() == "" )
                    {
                        probe.setName( "N/A" );
                    }
                    
                    probe.setName( probe.getName() + "\'" ); 
                }
            }
            else
            {
                throw ( new RuntimeException( "Probe \"" +
                    probe.getName() +
                "\" already in master table." ) );
            }
        }
        
        probes.put( probe.getName(), probe );
        
        // notify listeners
        fireMasterTableChanged( probe.isImplicitProbe() ?
                MasterTableEvent.SYSTEM_PROBE_ADDED_CHANGE :
                    MasterTableEvent.USER_PROBE_ADDED_CHANGE );
    }
    
    
    public void removeProbe( String name )
    throws RuntimeException
    {
        Probe l_probe = (Probe)this.probes.get( name ); 
        
        if ( l_probe == null )
        {    	
            // this probe is not stored in this master table,
            // so there is nothing to do -> quit
            return;
        }
        
        if ( !l_probe.isImplicitProbe() )
        {
            throw ( new RuntimeException( "Probe \"" +
                name +
                "\" is not a system probe and cannot be " +
            "removed from the master table." ) );
        }
        
        this.probes.remove( name ); 
        
        // notify listeners
        fireMasterTableChanged( l_probe.isImplicitProbe() ?
                MasterTableEvent.SYSTEM_PROBE_REMOVED_CHANGE :
                    MasterTableEvent.USER_PROBE_REMOVED_CHANGE );
    }
    
    public void removeProbe( Probe l_probe, boolean suppressExceptions )
    throws RuntimeException
    {
        if ( l_probe == null ) {    	
            return;
        }
        
        if ( !l_probe.isImplicitProbe() )
        {
        	if (suppressExceptions)
        		return;
        	else
        		throw ( new RuntimeException( "Probe \"" +
        				l_probe.getName() +
        				"\" is not a system probe and cannot be " +
        		"removed from the master table." ) );
        }
        
        this.probes.remove( l_probe.getName() ); 
        
        // notify listeners
        fireMasterTableChanged( l_probe.isImplicitProbe() ?
                MasterTableEvent.SYSTEM_PROBE_REMOVED_CHANGE :
                    MasterTableEvent.USER_PROBE_REMOVED_CHANGE );
    }
   
    public void renameProbe(String oldName, String newName, boolean suppressWarnings) {
    	if (!suppressWarnings) {
    		if (probes.containsKey(newName))
    			throw new RuntimeException("Can't rename Probe: This name exists already (\""+oldName+"\"-->\""+newName+"\")");
    		if (!probes.containsKey(oldName))
    			throw new RuntimeException("Can't rename Probe: Probe not found (\""+oldName+"\"-->\""+newName+"\")");
    	}
    	Probe pb = probes.get(oldName);
    	probes.remove(oldName);
    	pb.setName(newName);
    	probes.put(newName,pb);
    }
    
    public void renameProbe(String oldName, String newName) {
    	renameProbe(oldName, newName, false);
    }
    
    @SuppressWarnings("unchecked")
	public Double getMaxValue( int mode )
    { 
        Probe l_probe = (Probe)Collections.max( this.probes.values(),
            new Probe.ExperimentMaximumComparator( mode ) );
        if ( ( l_probe.isExplicitProbe() && ( ( mode & Probe.EXPLICIT_PROBE ) != 0 ) ) ||
                ( l_probe.isImplicitProbe() && ( ( mode & Probe.IMPLICIT_PROBE ) != 0 ) ) )
        {
            return ( l_probe.getMaxValue() );
        }
        
        return ( null );
    }
    
    
    @SuppressWarnings("unchecked")
	public Double getMinValue( int mode )
    {
        Probe l_probe = (Probe)Collections.min( this.probes.values(),
            new Probe.ExperimentMinimumComparator( mode ) );
        if ( ( l_probe.isExplicitProbe() && ( ( mode & Probe.EXPLICIT_PROBE ) != 0 ) ) ||
                ( l_probe.isImplicitProbe() && ( ( mode & Probe.IMPLICIT_PROBE ) != 0 ) ) )
        {
            return ( l_probe.getMinValue() );
        }
        
        return ( null );
    }
    
    
    public boolean isNonNegative()
    {
        return( getMinValue( Probe.EXPLICIT_PROBE | Probe.IMPLICIT_PROBE ).doubleValue() >= 0 );
    }
    
    
    public boolean isNonPositive()
    {
        return( getMaxValue( Probe.EXPLICIT_PROBE | Probe.IMPLICIT_PROBE ).doubleValue() <= 0 );    
    }
    
    
    public int getNumberOfExperiments()
    {
        return ( this.experiments.size() );
    }
    
    
    public void setNumberOfExperiments( int numberOfExperiments )
    {
    	while (experiments.size()>numberOfExperiments)
        	experiments.remove(experiments.size()-1);
        while (experiments.size()<numberOfExperiments)
        	experiments.add(new Experiment(this, Integer.toString(experiments.size()+1)));
    }
    
    public void addExperiment(Experiment e) {
    	// can only be done while there are no probes
//    	if (getNumberOfProbes()>0)
//    		throw new RuntimeException("Experiments can only be added while there are no Probes")
    	experiments.add(e);
    	fireMasterTableChanged( MasterTableEvent.EXPERIMENT_ORDERING_CHANGED );
    }
    
    public void reorderExperiments(final int[] newIndices) {
    	AbstractTask at = new AbstractTask("Reordering Matrix") {

			@Override
			protected void doWork() throws Exception {

				setProgress(0, "Reordering columns");
				
				int numOfPb = getNumberOfProbes();
				int pbCount = 0;						

				for (Probe pb : getProbes().values()) {					
					double[] tmp = pb.getValues();
					double[] copy = new double[tmp.length];
					int j=0;
					for(int i: newIndices)
						copy[j++] = tmp[i];
					pb.setValues(copy);
					
					setProgress((5000*(++pbCount))/numOfPb);
				}
				
				// update experiments
				List<Experiment> le = new ArrayList<Experiment>(getExperiments());
				int j=0;
				for(int i: newIndices)
					experiments.set(j++, le.get(i));
				
				ProbeListImageStorage.singleInstance().clearCache();
				List<DataSet> l=DataSetManager.singleInstance.getDataSets();
	        	for(DataSet ds : l)
	        		ds.getProbeListManager().getProbeListManagerView().updateCellRenderer();
				
		        fireMasterTableChanged( MasterTableEvent.EXPERIMENT_ORDERING_CHANGED );
			}

			protected void initialize() {}

		};
		at.start();
    }
    
    public Experiment getExperiment(int experiment) {
    	if (experiment >= getNumberOfExperiments() || experiment<0)
    		throw (new RuntimeException( "Experiment " + experiment + " does not exist." ) );
    	return experiments.get(experiment);
    }
    
    public Experiment getExperiment(String name) {
    	// if the mapping is out of date, update it
    	indexExperiments();
		Experiment e = experimentIndex.get(name);		
		if (e==null || !e.getName().equals(name)) { // names can change in the meantime
			experimentIndex.clear();
			indexExperiments();
			e = experimentIndex.get(name);
		}			
    	return e;
    }
    
	protected void indexExperiments() {
		if (experimentIndex.size()!=getNumberOfExperiments()) {
			experimentIndex.clear();
			for (int i=0; i!=getNumberOfExperiments(); ++i) {
				Experiment e = getExperiment(i);
				experimentIndex.put(e.getName(), e);
			}
		}
	}
    
    public String getExperimentName( int experiment ) {
    	return getExperiment(experiment).getName();
    }
    
    public List<String> getExperimentNames() {
    	LinkedList<String> exNames = new LinkedList<String>();
    	for (Experiment e : experiments)
    		exNames.add(e.getName());
    	return Collections.unmodifiableList(exNames);
    }
    
    
	public void setExperimentName( int experiment, String name ) {
		getExperiment(experiment).setName(name);
    }

	
	@SuppressWarnings("unchecked")
	public void setExperimentNames( Collection experimentNames ) {
		if (experimentNames.size()!=getNumberOfExperiments())
			throw new RuntimeException("Number of experiment names must match number of experiments!");
		if (experimentNames.size()==0)
			return;
		Collection<String> lli;
		if (experimentNames.iterator().next() instanceof String) {
			lli = (Collection<String>)experimentNames;
		} else {
			lli = new LinkedList<String>();
			for (Object o : experimentNames)
				lli.add(o.toString());
		}
		int i=0;
		for (String s : lli)
			experiments.get(i++).setName(s);
	}
	
	public String getExperimentDisplayName(int experiment) {
		return getExperiment(experiment).getDisplayName();
	}
	
    public List<String> getExperimentDisplayNames() {
    	LinkedList<String> exNames = new LinkedList<String>();
    	for (Experiment e : experiments)
    		exNames.add(e.getDisplayName());
    	return Collections.unmodifiableList(exNames);
    }

	/** Create a new global probe list
	 * @param sticky if set to true, the new probelist will be STICKY.
	 * if set to false, it will only be returned and not stored anywhere 
	 * @see ProbeList The ProbeList class for an explanation of stickyness 
	 * @see getNumberOfProbes() for a fast way to find out the size of the mastertable
	 * @see getProbes() if you want to get all the probes in the mastertable (or getProbes().values()) */
	public ProbeList createGlobalProbeList(boolean sticky) {
		
        ProbeList l_globalProbeList = new ProbeList( getDataSet(), sticky );
                
        for (Probe pb : getProbes().values()) {
        	if ( !pb.isImplicitProbe() )
        		l_globalProbeList.addProbe( pb );
        }
        
        l_globalProbeList.setAnnotation( new AnnotationMIO(
            "Global probe list.",
            "<html>" +
            "<body>" +
            "<h1>Global</h1>" +
            "This is a global probe list. It was extracted " +
            "from the user master table and contains all probes " +
            "contained in the active data set at the time of creation." +                                                     
            "</body>" +
        "</html>" ) );
        l_globalProbeList.setName(MaydayDefaults.GLOBAL_PROBE_LIST_NAME);
        
        return ( l_globalProbeList );
	}
	
	/** Create a new non-sticky global probe list. 
	 * @see createGlobalProbeList(boolean) 
	 * @see getNumberOfProbes() for a fast way to find out the size of the mastertable
	 * @see getProbes() if you want to get all the probes in the mastertable (or getProbes().values()) */
	public ProbeList getGlobalProbeList() {
		return createGlobalProbeList(false);
	}
    
    
    public int getNumberOfProbes()
    {
        return ( probes.size() );
    }
    
    
    public Probe getProbe( String name )
    {
        return ( (Probe)probes.get( name ) );
    }
    
    
    public String toString()
    {
        String l_string = new String();
        
        l_string += getNumberOfExperiments() + "\n";
        l_string += getNumberOfProbes() + "\n";
        
        Object[] l_probes = this.probes.values().toArray();
        
        for ( int i = 0; i < l_probes.length; ++i )
        {
            l_string += (Probe)l_probes[i] + "\n";
        }		
        
        return ( l_string );
    }
    

    
    
    /**
     * @return Returns the isUniquifying.
     */
    public boolean isUniquifying()
    {
        return isUniquifying;
    }
    
    
    
    /**
     * @param isUniquifying The isUniquifying to set.
     */
    public void setUniquifying( boolean isUniquifying )
    {
        this.isUniquifying = isUniquifying;
    }
    
    /**
     * Checks weather or not the MasterTable has missing values.
     * @return 
     */
    public boolean hasMissingValues()
    {
        for(Object o:probes.keySet())
        {
            if(((Probe)probes.get(o)).getFirstMissingValue()!=-1)
                return true;
        }
        
        return false;
    }
    
    
}
