package mayday.core.math.scoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.ClassSelectionModel;
import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.tasks.AbstractTask;



public abstract class TestPlugin<T extends ScoringResult> extends AbstractPlugin{
	
	private AbstractTask theTask;
	
	/** Runs the test on a collection of probes. The resulting ScoringResult
	 * maps input probes to test result values. 
	 * @param probes the probes to test
	 * @param classes the class labels obtained by using a ClassSelectionDialog instance
	 * @return a ScoringResult object containing the test results.
	 */
	public final T runTest(Collection<Probe> probes, ClassSelectionModel classes) {
		HashMap<Object, double[]> conv = new HashMap<Object,double[]>();
		for (Probe probe : probes)
			conv.put(probe, probe.getValues());
		return runTestTask(conv, classes);
	}
	
	/** Runs the test on an array of double vectors. The resulting ScoringResult
	 * uses the index of the respective input vector as key in its output MIGroups. 
	 * @param vectors the double vectors representing each sample to test
	 * @param classes the class labels obtained by using a ClassSelectionDialog instance
	 * @return a ScoringResult object containing the test results. Input vector i 
	 * is represented by the MIGroup entry <new Integer(i), pValue(i)>
	 */
	public final T runTest(double[][] vectors, ClassSelectionModel classes) {
		HashMap<Object, double[]> conv = new HashMap<Object,double[]>();
		for(double[] vector : vectors)
			conv.put(vector, vector);
		return runTestTask(conv, classes);
	}
	
	@SuppressWarnings("unchecked")
	public final T runTestTask(final Map<Object,double[]> values, final ClassSelectionModel classes) {
		
		final Object[] result = new Object[1];
		
		theTask = new AbstractTask(toString()) {

			protected void doWork() throws Exception {
				result[0] = runTest(values, classes);
			}

			protected void initialize() {
			}
			
		};
		theTask.start();
		theTask.waitFor();
		
		return (T)result[0];
	}
	

	/** Runs the statistical test on double values linked to objects (keys).
	 * The resulting ScoringResult maps input objects to test result values
	 * @param values Key-Value Map of input objects and observations for each
	 * @param classes the class labels obtained by using a ClassSelectionDialog instance
	 * @return a ScoringResult object containing the test results.
	 */
	public abstract T runTest(Map<Object,double[]> values, ClassSelectionModel classes);
    
    public void init() {}
    
    public String toString() {
    	return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
    }
    
    public boolean equals(Object o) {
    	return toString().equals(o.toString()); 
    }

    public AbstractTask getTask() {
    	return theTask;
    }
    
    public void setProgress(double percentageX100) {
    	if (theTask!=null)
    		theTask.setProgress((int)percentageX100);
    }
    
    
    
	protected final Integer[][] classIndices( ClassSelectionModel classes ) {
		//get the channels
		List<List<Integer>> selected = this.getClasses(classes);

		//array with all the experiments' indexes (in their respective classes) 
		Integer[][] index = new Integer[2][];

		for(int i = 0; i<2; i++){
			index[i] = selected.get(i).toArray(new Integer[0]);
		}
		return index;
	}
	
	public final List<List<Integer>> getClasses(ClassSelectionModel classes) {
		ArrayList<List<Integer>> channels = new ArrayList<List<Integer>> ();
		for (int i=0; i!=classes.getNumClasses(); ++i)
			channels.add(classes.toIndexList(i));
		return channels;
	}
    
    
}
