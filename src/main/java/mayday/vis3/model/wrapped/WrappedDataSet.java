package mayday.vis3.model.wrapped;
import mayday.core.DataSet;
import mayday.core.DataSetListener;
import mayday.core.datasetmanager.gui.DataSetView;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.types.AnnotationMIO;


/** WraooedDataSet wraps around an existing DataSet and replaces 
 * 1) the MasterTable with an instance of WrappedMasterTable (provided in the constructor) 
 * 2) the ProbeListManager with an instance of WrappedProbeListManager based on the WrappedMasterTable
 * Everything else is delegated to the wrapped dataset
 * @author battke
 *
 */
public class WrappedDataSet extends DataSet
{

	protected DataSet wrapped;
    
	// === REAL FUNCTIONS ===

    public WrappedDataSet(DataSet parent, WrappedMasterTable smt) {
    	super(true);
    	wrapped = parent;
    	this.masterTable = smt;
    	this.probeListManager = new WrappedProbeListManager(parent.getProbeListManager(), smt);
    }
    
   
    // === WRAPPED FUNCTIONS ===
    
    public void addDataSetListener( DataSetListener listener ) {
    	wrapped.addDataSetListener(listener);
    }
        
    public void removeDataSetListener( DataSetListener listener ) {
    	wrapped.removeDataSetListener(listener);
    }
    
    public boolean isSilent() {
    	return wrapped.isSilent();
    }
    
    public void setSilent( boolean isSilent ) {
    	wrapped.setSilent(isSilent);
    }    
    
    public AnnotationMIO getAnnotation() {
    	return wrapped.getAnnotation();
    }

    public String getName() {
  	  return wrapped.getName(); 
    }
    
    public void setName(String Name) {
    	wrapped.setName(Name);
    }
    
    public void setAnnotation( AnnotationMIO annotation ) {
    	wrapped.setAnnotation(annotation);
    }
 
    public int compareTo( Object dataSet ) {
    	return wrapped.compareTo(dataSet);
    }
    
    public boolean equals( Object dataSet ) {
    	return wrapped.equals(dataSet);
    }
    
    public void propagateClosing() {
    	wrapped.propagateClosing();
    }
    
    public DataSetView getDataSetView() {
        return wrapped.getDataSetView();
    }
    
    public void setDataSetView(DataSetView view) {
    	wrapped.setDataSetView(view);
    }
    
    public MIManager getMIManager() {
    	return wrapped.getMIManager();
     }
    
	public void setProbeDisplayNames( MIGroup mioGroup ) {
		wrapped.setProbeDisplayNames(mioGroup);
    }

	public MIGroup getProbeDisplayNames() {
		return wrapped.getProbeDisplayNames();
	}
	
	public void setExperimentDisplayNames( MIGroup mioGroup ) {
		wrapped.setExperimentDisplayNames(mioGroup);
    }

	public MIGroup getExperimentDisplayNames() {
		return wrapped.getExperimentDisplayNames();
	}

}
