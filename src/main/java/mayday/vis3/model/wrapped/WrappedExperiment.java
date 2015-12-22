package mayday.vis3.model.wrapped;
import mayday.core.Experiment;
import mayday.core.meta.types.AnnotationMIO;


/** WrappedExperiment wraps around an existing Experiment and 
 * 1) replaces the MasterTable with an instance of WrappedMasterTable (provided in the constructor)
 * 2) Equality checks using hashCode() and equals() make no distinction between
 *    the WrappedExperiment and the wrapped Experiment contained inside. 
 * Everything else is delegated to the wrapped experiment
 * @author battke
 *
 */
public class WrappedExperiment extends Experiment
{

	protected Experiment wrapped;

	// === REAL FUNCTIONS ===

	public WrappedExperiment(Experiment parent, WrappedMasterTable smt) {
		this(parent.getName(), smt);
		wrapped = parent;
	}
	
	protected WrappedExperiment(String name, WrappedMasterTable smt) {
		super(smt, name);
	}

	public int hashCode() {
		return wrapped.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof WrappedExperiment)
			return ((WrappedExperiment)o).wrapped.equals(wrapped);
		return wrapped.equals(o);
	}
	
	public Experiment getWrappedExperiment() {
		return wrapped;
	}

	// === WRAPPED FUNCTIONS ===
	public String getName() {
		return wrapped.getName();
	}

	public String getDisplayName()	{
		return wrapped.getDisplayName();
	}

	public AnnotationMIO getAnnotation() {
		return wrapped.getAnnotation();
	}

	public void setName(String name2) {
		wrapped.setName(name2);
	}

	public void setAnnotation( AnnotationMIO annotation ) {
		wrapped.setAnnotation(annotation);
	}
	

}
