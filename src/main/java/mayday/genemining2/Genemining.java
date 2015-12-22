package mayday.genemining2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.math.scoring.DefaultScoringResult;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.math.scoring.TestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.math.stattest.UncorrectedStatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskStateEvent;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class Genemining {
	private ArrayList<TestPlugin<ScoringResult>> methods;
	private List<List<ScoringResult>> geneminingResults;
	private List<ClassSelectionModel> classSet;
	private List<String> classIndices;
	private ScoringResult consensus = null;
	/**
	 * 
	 */
	public boolean permTest = false;
	/**
	 * 
	 */
	public boolean permTestHeuristic = false;

	/**
	 * Constructor
	 */
	public Genemining() {
		this.methods = new ArrayList<TestPlugin<ScoringResult>>();
		this.geneminingResults = new ArrayList<List<ScoringResult>>();
		this.classSet = new ArrayList<ClassSelectionModel>();
		this.classIndices = new ArrayList<String>();
	}

	/**
	 * Register a Method (StatTestPlugin), which will be applied later
	 * 
	 * @param method
	 */
	public void registerMethod(TestPlugin<ScoringResult> method) {
		this.methods.add(method);
	}

	/**
	 * Apply selected methods to all possible pairs of two classes
	 * 
	 * @param probeLists
	 * @param masterTable
	 * @param classes
	 */
	public void run(List<ProbeList> probeLists, MasterTable masterTable,
			final ClassSelectionModel classes) {
		final ProbeList uniqueProbes = ProbeList
				.createUniqueProbeList(probeLists);
		// create all possible pairwise combinations of classes
		this.createClassPairs(classes);
		assert (this.classSet.size() > 0);
		assert (this.methods.size() > 0);
		
		AbstractTask at = new AbstractTask("Gene Mining") {
			protected void initialize() { }

			@Override
			protected void doWork() throws Exception {
				
				try {
					int perstep = (int)(10000d/((double)(methods.size()*classSet.size())));
					int curprog = 0;
					this.setProgress(0);
					
					for (int i = 0; i < methods.size(); i++) {
						geneminingResults.add(new ArrayList<ScoringResult>());
						
						for (int j = 0; j < classSet.size(); j++) {

							// Check if calculation has been aborted
							if (this.hasBeenCancelled())
								return;

							ScoringResult res = methods.get(i).runTest(
									uniqueProbes.getAllProbes(), classSet.get(j));

							// Check if calculation has been aborted
							if (this.hasBeenCancelled())
								return;
							
							if (res != null) {
								if (permTest == true) {
									final Comparator<Double> rsc = res.getRawScoreComparator();
									final UncorrectedStatTestResult result = new UncorrectedStatTestResult() {
										public Comparator<Double> getRawScoreComparator() {
											return rsc;
										}
									};
									result.setRawScore(res.getRawScore());
									for(int k = 0; k < result.getAdditionalValues().size(); k++){
										result.addAdditionalValue(res.getAdditionalValues().get(k));
									}
									if(res instanceof StatTestResult){
										result.setPValues(((StatTestResult)res).getPValues());
									}
//									result.setSmallerScoreIsBetter(res.getRawScoreComparator().compare(0.0, 1.0) >= 0);
									
									final int ii = i, jj = j;
									AbstractTask theTask = new AbstractTask(
											"Permutation-Test: "
													+ methods.get(i).toString()) {
										@Override
										protected void doWork() throws Exception {
											PermutationTest.permutationTest(classSet
													.get(jj), result, methods.get(ii),
													uniqueProbes, this);
										}

										@Override
										protected void initialize() {}
									};
									theTask.start();
									theTask.waitFor();
									if (theTask.getTaskState()!=TaskStateEvent.TASK_FINISHED) // failed, cancelled
										this.cancel();
									
									res = result;
								}
								if (permTestHeuristic == true) {
									
									final Comparator<Double> rsc = res.getRawScoreComparator();
									final UncorrectedStatTestResult result = new UncorrectedStatTestResult() {
										public Comparator<Double> getRawScoreComparator() {
											return rsc;
										}
									};
									result.setRawScore(res.getRawScore());
									for(int k = 0; k < result.getAdditionalValues().size(); k++){
										result.addAdditionalValue(res.getAdditionalValues().get(k));
									}
									if(res instanceof StatTestResult){
										result.setPValues(((StatTestResult)res).getPValues());
									}
//									result.setSmallerScoreIsBetter(res.getRawScoreComparator().compare(0.0, 1.0) >= 0);
									
									AbstractTask theTask = new AbstractTask(
											"Permutation-Test-Heuristic: "
													+ methods.get(i).toString()) {
										@Override
										protected void doWork() throws Exception {
											PermutationTest.permutationTestHeuristic(
													classes, result, this);
										}

										@Override
										protected void initialize() {}
									};
									theTask.start();
									theTask.waitFor();
									if (theTask.getTaskState()!=TaskStateEvent.TASK_FINISHED) // failed, cancelled 
										this.cancel();
									
									res = result;
								}
								geneminingResults.get(i).add(res);
							}
						}
						
						curprog+=perstep;							
						setProgress(curprog);
					}
				} catch (Exception tce) {
					geneminingResults.clear();
				}

			}
		};
		at.start();
		at.waitFor();
		
		if (at.hasBeenCancelled())
			geneminingResults.clear();
	}

	/**
	 * Create all possible pairs of two classes
	 * 
	 * @param classes
	 */
	// FIXME: I am not sure that the strings created here ("classIndices.add(...)") are correct -fb 
	public void createClassPairs(ClassSelectionModel classes) {
		int numClasses = classes.getNumClasses();
		Integer[][] classIndices = classIndices(classes);
		boolean[] sum = new boolean[numClasses];
		boolean[] one = new boolean[numClasses];
		one[numClasses - 1] = true;

		for (int i = 0; i < ((Math.pow(2, numClasses) / 2) - 1); i++) {
			ClassSelectionModel newClasses = new ClassSelectionModel(classes);
			
			String classesString = "";
			sum = binaryAddition(sum, one);			
			
			for (int j = 0; j < sum.length; j++) {				
				if (sum[j] == true) {
					Integer[] class1Indices = classIndices[j];
					for (int k = 0; k < class1Indices.length; k++) {
						newClasses.setClass(class1Indices[k],  "1");
					}
					classesString += classes.getClassesLabels().get(j) + "+";
				} else {
					Integer[] class2Indices = classIndices[j];
					for (int k = 0; k < class2Indices.length; k++) {
						newClasses.setClass(class2Indices[k],  "2");
					}
				}
			}
			// remove last char and add to classIndices
			classesString = classesString.substring(0,
					classesString.length() - 1);
			
			this.classIndices.add(classesString + " vs. other");
			this.classSet.add(newClasses);
		}
	}

	/**
	 * @return gene mining results
	 */
	public List<List<ScoringResult>> getResults() {
		return this.geneminingResults;
	}
	
	/**
	 * @return class identifier
	 */
	public List<String> getClassIdentifier() {
		return this.classIndices;
	}

	/**
	 * performs a ranking and
	 * 
	 * @param masterTable
	 * @param numGenes
	 * @return top ranking genes
	 */
	public List<ProbeList> getTopGenes(MasterTable masterTable, int _numGenes, double maxpVal) {
		List<ProbeList> topGenes = new ArrayList<ProbeList>();
		Color[] colors = GUIUtilities.rainbow(this.methods.size() + 1, 0.75);

		for (int i = 0; i < this.methods.size(); i++) {
			List<ScoringResult> results = this.geneminingResults.get(i);
			int j = 1;
			for (ScoringResult res : results) {
				ProbeList pl = new ProbeList(masterTable.getDataSet(), true);
				pl.setName(this.methods.get(i).toString() + " : " + (j++));
				pl.getAnnotation().setQuickInfo(this.classIndices.get(j - 2));
				pl.setColor(colors[i]);

				int numGenes=_numGenes;				
				Gene[] ranking = doRanking(res, true);
				// return all probes if numGenes is too large
				if (ranking.length < numGenes)
					numGenes = ranking.length;
				// get top genes and put them into the corresponding probeList
				for (int k = 0; k < numGenes; k++) {
					if ((this.permTest || this.permTestHeuristic) && res instanceof StatTestResult) {
						DoubleMIO dm = (DoubleMIO)(((StatTestResult)res).getPValues().getMIO(ranking[k].o));
						if (dm==null || dm.getValue()>=maxpVal) {
							numGenes = k;
							break;
						}
					}
					pl.addProbe((Probe) ranking[k].o);
				}
				// remove probes with small rank from result
				for (int k = ranking.length - 1; k >= numGenes; k--) {
					if(res instanceof StatTestResult){
						((StatTestResult)res).getPValues().remove(ranking[k].o);
					}
					if(res.hasRawScore()) {
						res.getRawScore().remove(ranking[k].o);
					}
					for (MIGroup mg : res.getAdditionalValues()) {
						mg.remove(ranking[k].o);
					}
				}
				// add probeList to final output
				topGenes.add(pl);
			}
		}
		if (this.consensus != null) {
			ProbeList pl = new ProbeList(masterTable.getDataSet(), true);
			pl.setName("Consensus ProbeList");
			pl.setAnnotation(new AnnotationMIO("Consensus",
					"Consensus ProbeList"));
			pl.setColor(colors[colors.length - 1]);

			int numGenes=_numGenes;				
			Gene[] ranking = doRanking(this.consensus, true);
			// return all probes if numGenes is too large
			if (ranking.length < numGenes)
				numGenes = ranking.length;
			// get top genes and put them into the corresponding probeList
			for (int k = 0; k < numGenes; k++) {
				if ((this.permTest || this.permTestHeuristic) && consensus instanceof StatTestResult) {
					DoubleMIO dm = (DoubleMIO)(((StatTestResult)consensus).getPValues().getMIO(ranking[k].o));
					if (dm==null || dm.getValue()>=maxpVal) {
						numGenes = k;
						break;
					}
				}
				pl.addProbe((Probe) ranking[k].o);
			}
			// remove probes with small rank from StatTestResult
			for (int k = ranking.length - 1; k >= numGenes; k--) {
				this.consensus.getRawScore().remove(ranking[k].o);
			}
			// add probeList to final output
			topGenes.add(pl);
		}
		return topGenes;
	}
	
	private double round(double val){
		int tmp =(int)(val * 1000);
		return (double)tmp / 1000;
	}

	/**
	 * @return consensus genes
	 */
	public ScoringResult getConsensus() {
		ArrayList<Gene[]> rankingResults = new ArrayList<Gene[]>();

		ScoringResult consensusGenes = new DefaultScoringResult(false);
		
		HashSet<Gene> consGenes = new HashSet<Gene>();
		
		MIGroup rankGroup = consensusGenes.getRawScore();
		int maxSize = 0;
		int numProfiles = 0;
		
		for(int i = 0; i < this.geneminingResults.size(); i++){
			List<ScoringResult> results = this.geneminingResults.get(i);
			numProfiles += results.size();
			for(int j = 0; j < results.size(); j++){
				ScoringResult result = results.get(j);
				int thisResultSize = 0;
				
				if(result.hasRawScore()){
					thisResultSize = result.getRawScore().size();
				} else if(result instanceof StatTestResult){
					thisResultSize = ((StatTestResult)result).getPValues().size();
				}
				
				if (thisResultSize > maxSize) 
					maxSize = thisResultSize;
				rankingResults.add(doRanking(result, false));
				
				if(i == 0) { // first time: add all genes
					for(int k = 0; k < rankingResults.get(j).length; k++){
						consGenes.add(rankingResults.get(j)[k]);
					}
				} else { // next time: compute intersection
					keepOnly(consGenes, rankingResults.get(j));
				}
			}
		}
		
		for(Gene g : consGenes){
			rankGroup.add(g.o, new DoubleMIO(0.0));
		}
		
		int[] rank = new int[numProfiles];
		Arrays.fill(rank, 1);
		int[] equalNumber = new int[numProfiles];
		Arrays.fill(equalNumber, 0);
		
		for(int i = 0; i < maxSize; i++){
			for(int j = 0; j < numProfiles; j++){
				Gene[] thisResult = rankingResults.get(j);				
				int thisResultSize = thisResult.length;
				if(i < thisResultSize){
					Gene tmpGene = thisResult[i];
					int tmpRank = rank[j];
					if(consGenes.contains(tmpGene)){
						DoubleMIO theMIO = ((DoubleMIO)rankGroup.getMIO(tmpGene.o));
						double oldVal = theMIO.getValue().doubleValue();
						theMIO.setValue(oldVal + tmpRank);
					}
					if(i < thisResultSize-1 && tmpGene.value != thisResult[i+1].value){
						rank[j] += 1 + equalNumber[j];
						equalNumber[j] = 0;
					} else if(i < thisResultSize-1 && tmpGene.value == thisResult[i+1].value){
						equalNumber[j] += 1;
					}
				}
			}
		}
		
		// divide all values by number of profiles
		for(Gene tmpGene : consGenes){
			DoubleMIO theMio = ((DoubleMIO)rankGroup.getMIO(tmpGene.o));
			double oldVal = theMio.getValue();
			theMio.setValue(round(oldVal / numProfiles));
		}
		
		this.consensus = consensusGenes;
		return consensusGenes;
	}
	
	private void keepOnly(Set<Gene> consGenes, Gene[] genes) {
		
		HashSet<Gene> wantedGenes = new HashSet<Gene>();
		for(int i = 0; i < genes.length; i++) {
			wantedGenes.add(genes[i]);
		}
		consGenes.retainAll(wantedGenes);

	}

	private Gene[] doRanking(ScoringResult res, boolean reverse){
		MIGroup values = null;
		Gene[] ranking;

		if (res.hasRawScore()) {
			values = res.getRawScore();
		} else if(res instanceof StatTestResult) {
			values = ((StatTestResult)res).getPValues();
		}
		
		if (values!=null) {
			int size = values.size();
			int sortSize = size;
			
			// remove all NA before consideration --> they get sorted inconsistently otherwise
			for (Entry<Object, MIType> entry : values.getMIOs()) {
				double val = ((DoubleMIO)entry.getValue()).getValue();
				if (Double.isNaN(val))
					--sortSize;
			}
			
			ranking = new Gene[size];
			int valuePosition = 0;
			int NaPosition = sortSize;
			
			for (Entry<Object, MIType> entry : values.getMIOs()) {
				double val = ((DoubleMIO)entry.getValue()).getValue();
				if (Double.isNaN(val)) 
					ranking[NaPosition++] = new Gene(entry.getKey(), Double.NaN);
				else 
					ranking[valuePosition++] = new Gene(entry.getKey(), val);
			}

			// NAN will always get the lowest ranks now and will stay at the end of ranking
			Arrays.sort(ranking, 0, sortSize, new GeneComparator(res.getRawScoreComparator()));
			
			if (reverse) { // only reverse the order of non-NaN-values
				for (int i = 0; i < sortSize / 2; i++) {
					Gene tmp = ranking[i];
					ranking[i] = ranking[sortSize - 1 - i];
					ranking[sortSize - 1 - i] = tmp;
				}
			}
		} else {
			ranking = new Gene[0];
		}
				
		return ranking;
	}

	protected static Integer[][] classIndices(ClassSelectionModel classes) {
		List<List<Integer>> selected = getClasses(classes);
		// array with all the experiments' indexes (in their respective classes)
		Integer[][] index = new Integer[classes.getNumClasses()][];

		for (int i = 0; i < classes.getNumClasses(); i++) {
			index[i] = selected.get(i).toArray(new Integer[0]);
		}
		return index;
	}

	protected static List<List<Integer>> getClasses(ClassSelectionModel classes) {
		ArrayList<List<Integer>> channels = new ArrayList<List<Integer>>();
		for (int i = 0; i != classes.getNumClasses(); i++)
			channels.add(classes.toIndexList(i));
		return channels;
	}

	private boolean[] binaryAddition(boolean[] array1, boolean[] array2) {
		boolean[] result = new boolean[array1.length];
		boolean overflow = false;

		for (int i = array1.length - 1; i >= 0; i--) {
			if (array1[i] && array2[i]) // 1 und 1
			{
				if (overflow) {
					result[i] = true;
					overflow = true;
				} else {
					result[i] = false;
					overflow = true;
				}
			} else if (array1[i] || array2[i]) // 1 und 0 oder 0 und 1
			{
				if (overflow) {
					result[i] = false;
					overflow = true;
				} else {
					result[i] = true;
					overflow = false;
				}
			} else // 0 und 0
			{
				if (overflow) {
					result[i] = true;
					overflow = false;
				} else {
					result[i] = false;
					overflow = false;
				}
			}
		}
		return result;
	}

	/*
	 * helper class, that simplifies ranking of genes
	 */
	private static class Gene implements Comparable<Gene> {
		public Object o;
		public double value;

		public Gene(Object o, double value) {
			this.o = o;
			this.value = value;
		}

		@Override
		public int compareTo(Gene g) {
			if (value < g.value)
				return -1;
			if (value > g.value)
				return 1;
			return 0;
		}
	}
	
	public class GeneComparator implements Comparator<Gene> {

		protected Comparator<Double> scorecmp;
		
		public GeneComparator(Comparator<Double> scoreComparator) {
			scorecmp = scoreComparator;
		}
		
		public int compare(Gene o1, Gene o2) {
			return scorecmp.compare(o1.value, o2.value);
		}
	}
	
}
