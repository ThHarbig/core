package mayday.vis3.model.wrapped;

import java.util.ArrayList;
import java.util.List;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.types.AnnotationMIO;

/** WrappedProbe wraps around an existing Probe and 
 * 1) replaces the MasterTable with an instance of WrappedMasterTable (provided in the constructor)
 * 2) wraps all ProbeLists in WrappedProbeLists UPON ACCESS (might be inefficient when called often)
 * 3) Equality checks using hashCode() and equals() make no distinction between
 *    the WrappedProbeList and the wrapped ProbeList contained inside.
 * 4) Creation of probe values is delegated to the WrappedMasterTable 
 * Everything else is delegated to the wrapped Probe
 * @author battke
 *
 */
public class WrappedProbe extends Probe {

	protected Probe wrapped;
	
	public WrappedProbe( Probe pb, WrappedMasterTable smt ) {
		super(smt);
		wrapped = pb;
		fillValues();
	}
	
	protected void fillValues() {
		values = ((WrappedMasterTable)masterTable).getDerivedProbeValues(wrapped);
	}
	
	// === REAL FUNCTIONS ===
	
	public void setValue( Double value, int experiment ) {
		throw new RuntimeException("WrappedProbe does not support setValue()");
	}
	
	public Object clone() {
		return new WrappedProbe(wrapped, (WrappedMasterTable)masterTable);
	}
	
	public void setValues(double[] values) { 
		throw new RuntimeException("WrappedProbe does not support setValues()");
	}
	public void setValues(double[] values, boolean doCopy) {
		throw new RuntimeException("WrappedProbe does not support setValues()");
	}
	
	public List<ProbeList> getProbeLists() {
		List<ProbeList> vl = wrapped.getProbeLists();
		ArrayList<ProbeList> ret = new ArrayList<ProbeList>(vl.size());
		for (ProbeList pl : vl)
			ret.add(new WrappedProbeList(pl, (WrappedMasterTable)masterTable));
		return ret;
	}
	
	public Probe getWrappedProbe() {
		return wrapped;
	}
	
	
	// === WRAPPING ===
	
	
	public AnnotationMIO getAnnotation() {
		return wrapped.getAnnotation();
	}
	
	public String getName() {
		return wrapped.getName();
	}
	
	public void setName(String newName) {
		wrapped.setName(newName);
	}
	
	public void setAnnotation( AnnotationMIO annotation ) {
		wrapped.setAnnotation(annotation);
	}
	
	public boolean isImplicitProbe() {
		return wrapped.isImplicitProbe();
	}
	
	public void setImplicitProbe( boolean implicitProbe ) {
		wrapped.setImplicitProbe(implicitProbe);
	}
	
	public boolean isExplicitProbe() {
		return wrapped.isExplicitProbe();
	}
	
	public void setExplicitProbe( boolean explicitProbe ) {
		wrapped.setExplicitProbe(explicitProbe);
	}
	
	public void setProbeLists( List<ProbeList> probeLists ) {
		wrapped.setProbeLists(probeLists);
	}
	
	public int getNumberOfProbeLists() {
		return wrapped.getNumberOfProbeLists();
	}
	
	public void addProbeList( ProbeList probeList ) {
		wrapped.addProbeList(probeList);
	}
	
	public void removeProbeList( ProbeList probeList ) {
		wrapped.removeProbeList(probeList);	
	}
	
	public int compareTo( Object probe ) {
		return wrapped.compareTo(probe);
	}
	
	public boolean equals( Object probe ) {
		return wrapped.equals(probe);
	}
	
	public int hashCode() {
		return wrapped.hashCode();
	}
	
	public String toDebugString() {
		return wrapped.toDebugString();
	}
	
	public String toString() {
		return wrapped.toString();		
	}
	
	public void addExperiment( Double experiment ) {
		wrapped.addExperiment(experiment);
	}

 	public String getDisplayName()	{
		return wrapped.getDisplayName();
	}
	

 	
 	// === OBSOLETE ===
//	public Double getValue( int experiment ) {
//	return ((SubsetMasterTable)masterTable).getDerivedValue(wrapped, experiment);
//}
//	
//	
//	public Double getMaxValue() {
//		double[] values = getValues();
//		double max = Double.MIN_VALUE;
//		for (int i=0; i!=values.length; ++i) {
//			double v = getValue(i);
//			if (v>max)
//				max = v;
//		}
//		if (max>Double.MIN_VALUE)
//			return max;
//		return null;
//		
//	}
//	public Double getMinValue() {
//		double[] values = getValues();
//		double min = Double.MAX_VALUE;
//		for (int i=0; i!=values.length; ++i) {
//			double v = getValue(i);
//			if (v<min)
//				min = v;
//		}
//		if (min<Double.MAX_VALUE)
//			return min;
//		return null;
//	}	
//				
//	public int getNumberOfExperiments() {
//		return masterTable.getNumberOfExperiments();
//	}
//		
//	public double getMean()
//	{
//		double[] values = getValues();	
//		double l_mean = 0.0;
//		double count=0;
//		for ( double d : values ) {
//			if (!Double.isNaN(d)) {
//				l_mean += d;
//				count ++;
//			}
//		}
//		return ( l_mean / count );
//	}
//
//
//	public double getStandardDeviation() {    
//		return ( Math.sqrt( getVariance() ) );
//	}
//
//
//	public double getVariance()	{		
//		double[] values = getValues();
//		double l_mean = getMean();
//		double l_sum = 0.0;
//		double count=0;
//		for ( double d : values ) {
//			if (!Double.isNaN(d)) {
//				l_sum += (l_mean-d)*(l_mean-d);
//				count ++;
//			}
//		}
//		return ( l_sum / count );
//	}
 	

//	public int getFirstMissingValue() {
//		double[] vals = getValues();
//		for (int i=0; i!=vals.length; ++i)
//			if (vals[i]==Double.NaN)
//				return i;
//		return -1;
//	}
//	
//	public double[] getValues() {
//		// delegate to WrappedMasterTable
//		return ((SubsetMasterTable)masterTable).getDerivedProbeValues(wrapped);
//	}
	
 	
}
