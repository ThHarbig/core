package mayday.vis3.model.wrapped;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.MasterTableEvent;
import mayday.core.MasterTableListener;
import mayday.core.Probe;

/** WrappedMasterTable wraps around an existing MasterTable and 
 * 1) wraps all Experiments in WrappedExperiments (upon creation)
 * 2) wraps all Probes in WrappedProbes (upon first access, or after change of the wrapped mastertable)
 * 3) wraps all the DataSet in a WrappedDataSet
 * Everything else is delegated to the wrapped mastertable
 * DERIVED classes MUST fill the 'experiments' list during construction by calling createExperiments!
 * @author battke
 *
 */
public abstract class WrappedMasterTable extends MasterTable implements MasterTableListener{

	protected MasterTable wrapped;

	public WrappedMasterTable( MasterTable parent ) {
		super(parent.getDataSet());
		parent.getDataSet().setMasterTable(parent); // restore 
		setDataSet(new WrappedDataSet(parent.getDataSet(), this));
		wrapped = parent;
		probes = null;
		parent.addMasterTableListener(new WeakMasterTableListener(this, wrapped)); 
	}

	public abstract double[] getDerivedProbeValues(Probe wrapped);
	public abstract int getNumberOfExperiments();	
	protected abstract WrappedExperiment createExperiment(int experiment);
	
	// === REAL FUNCTIONS ===

	protected void createExperiments() {
		for (int i=0; i!=getNumberOfExperiments(); ++i) {
			experiments.add(createExperiment(i));
		}		
	}
	
	public MasterTable getWrappedMasterTable() {
		return wrapped;
	}


	public Map<String,Probe> getProbes() {
		if (probes==null) {
			probes = new HashMap<String, Probe>();
			for (Probe pb : wrapped.getProbes().values())
				probes.put(pb.getName(), new WrappedProbe(pb, this));
		}
		return probes;
	}
	
	public void masterTableChanged( MasterTableEvent event ) {
		probes = null;
	}

	@SuppressWarnings("unchecked")
	public Double getMaxValue( int mode )
	{ 
		Probe l_probe = (Probe)Collections.max( getProbes().values(),
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
		Probe l_probe = (Probe)Collections.min( getProbes().values(),
				new Probe.ExperimentMinimumComparator( mode ) );
		if ( ( l_probe.isExplicitProbe() && ( ( mode & Probe.EXPLICIT_PROBE ) != 0 ) ) ||
				( l_probe.isImplicitProbe() && ( ( mode & Probe.IMPLICIT_PROBE ) != 0 ) ) )
		{
			return ( l_probe.getMinValue() );
		}

		return ( null );
	}



	public void setNumberOfExperiments( int numberOfExperiments ) {
		throw new RuntimeException("SubsetMasterTable can not change number of experiments");
	}
	public void addExperiment(Experiment e) {
		throw new RuntimeException("SubsetMasterTable can not change number of experiments");
	}
	public void reorderExperiments(final int[] newIndices) {
		throw new RuntimeException("SubsetMasterTable can not change order of experiments");

	}	


	
    public boolean hasMissingValues()
    {
        for(Probe pb:getProbes().values())
        {
            if (pb.getFirstMissingValue()!=-1)
                return true;
        }
        
        return false;
    }


	public Probe getProbe( String name ) {
		return ( (Probe)getProbes().get( name ) );
	}

	private static class WeakMasterTableListener implements MasterTableListener {

		WeakReference<WrappedMasterTable> wspl;
		MasterTable parent;
		
		public WeakMasterTableListener(WrappedMasterTable subsetMT, MasterTable parent) {
			wspl = new WeakReference<WrappedMasterTable>(subsetMT);
			this.parent=parent;
		}

		public void masterTableChanged( MasterTableEvent event ) {
			WrappedMasterTable spl = wspl.get();
			if (spl==null) 
				parent.removeMasterTableListener(this);
			else 
				spl.masterTableChanged(event);				
		}
		
	}
	
	// === WRAPPING ===


	public void addMasterTableListener( MasterTableListener listener ) {
		wrapped.addMasterTableListener(listener);
	}

	public void removeMasterTableListener( MasterTableListener listener ) {
		wrapped.removeMasterTableListener(listener);
	}

	public boolean isSilent() {
		return wrapped.isSilent();
	}

	public void setSilent( boolean isSilent ) {
		wrapped.setSilent(isSilent);
	}

	public void clear() {
		wrapped.clear();
	}

	public void addProbe( Probe probe ) {
		wrapped.addProbe(probe);
	}

	public void removeProbe( String name ) {
		wrapped.removeProbe(name);
	}

	public void removeProbe( Probe l_probe, boolean suppressExceptions ) {
		wrapped.removeProbe(l_probe, suppressExceptions);
	}

	public void renameProbe(String oldName, String newName, boolean suppressWarnings) { 
		wrapped.renameProbe(oldName, newName, suppressWarnings);
	}

	public void renameProbe(String oldName, String newName) {
		wrapped.renameProbe(oldName, newName);
	}

	public int getNumberOfProbes() {
		return wrapped.getNumberOfProbes();
	}

	public String toString() {
		return wrapped.toString();
	}

	public boolean isUniquifying() {
		return wrapped.isUniquifying();
	}

	public void setUniquifying( boolean isUniquifying ) {
		wrapped.setUniquifying(isUniquifying);
	}

	public int hashCode() {
		return wrapped.hashCode();
	}
	
	
	// === OBSOLETE ===
//	public String getExperimentName( int experiment ) {
//		return getExperiment(experiment).getName();
//	}
//
//	public List<String> getExperimentNames() {
//		LinkedList<String> exNames = new LinkedList<String>();
//		for (int i =0; i!=getNumberOfExperiments(); ++i)
//			exNames.add(getExperimentName(i));
//		return Collections.unmodifiableList(exNames);		
//	}
//
//	public void setExperimentName( int experiment, String name ) {
//		getExperiment(experiment).setName(name);
//	}
//
//	@SuppressWarnings("unchecked")
//	public void setExperimentNames( Collection experimentNames ) {
//		throw new RuntimeException("SubsetMasterTable can not change all names of experiments");
//	}
//
//	public String getExperimentDisplayName(int experiment) {
//		return getExperiment(experiment).getDisplayName();
//	}
//
//	public List<String> getExperimentDisplayNames() {
//		LinkedList<String> exNames = new LinkedList<String>();
//		for (int i =0; i!=getNumberOfExperiments(); ++i)
//			exNames.add(getExperimentDisplayName(i));
//		return Collections.unmodifiableList(exNames);	
//	}
//	
//	public List<Experiment> getExperiments() {		
//	ArrayList<Experiment> ex = new ArrayList<Experiment>(getNumberOfExperiments());
//	for (int i =0; i!=getNumberOfExperiments(); ++i)
//		ex.add(getExperiment(i));
//	return ex;
//}


}
