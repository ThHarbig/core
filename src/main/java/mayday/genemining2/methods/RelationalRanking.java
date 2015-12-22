package mayday.genemining2.methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import mayday.core.ClassSelectionModel;
import mayday.core.math.scoring.DefaultScoringResult;
import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.matrix.VectorBasedMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class RelationalRanking extends AbstractMiningMethod {
	
	protected DoubleSetting pval;
	
	@Override
	public Setting getSetting() {
		if(pval == null){
			this.pval = new DoubleSetting("p-value", "p-value to calculate number of permutations", 0.5);
		}
		return this.pval;
	}

	@Override
	public ScoringResult runTest(Map<Object, double[]> values,
			ClassSelectionModel classes) {
		
		double pvalue = pval.getDoubleValue();
		
		// calculate number of permutations depending on pvalue
		boolean notFound = true;
		int permutations = 0;
		for(int i = 10; notFound; i += 10){
			double rDouble = pvalue * i;
			int rInt = (int)rDouble;
			if(rDouble == rInt){
				notFound = false;
				permutations = i;
			}
		}
		
		// transform the data into a form that is much easier to work with for
		// RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra
				.<int[]> createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra
				.<int[]> createNativeArray(index[1]));
		int num_gene = values.size();

		//int n_l = index[0].length; // number of experiments in class 1
		//int n_r = index[1].length; // number of experiments in class 2
		
		Set<Object> probes = values.keySet();
		
		return this.getSignificantGenes(num_gene, permutations, pvalue, data1, data2, probes);
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.relationalranking", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de", "Relational Ranking",
				"Relational Ranking");
		return pli;
	}
	
	private ScoringResult getSignificantGenes(int num_gene, int permutations, double cutOff, PermutableMatrix data1, PermutableMatrix data2, Set<Object> probes){
		ScoringResult res = new DefaultScoringResult(false);
		
		int tmp = 0;
		if(data1.ncol() <= data2.ncol()){
			tmp = data1.ncol();
		} else {
			tmp = data2.ncol();
		}
		
		Permutator permutator = new Permutator(tmp / 2);
		
		HashMap<Object, Vector<Integer>> sigMap = new HashMap<Object, Vector<Integer>>(data1.getColumn(0).size());
		ArrayList<AbstractVector> init1 = new ArrayList<AbstractVector>();
		ArrayList<AbstractVector> init2 = new ArrayList<AbstractVector>();
		
		for(int i = 0; i < data1.ncol(); i++){
			init1.add(data1.getColumn(i));
		}
		for(int i = 0; i < data2.ncol(); i++){
			init2.add(data2.getColumn(i));
		}
		
		Gene<Object>[] geneRanking = getRankedDiffProfile(init1, init2, probes, num_gene);
		int size = geneRanking.length;
		int lastIndex = size - 1;
		
		for(int i = lastIndex; i >= 0; i--){
			int rank = size - i;
			Vector<Integer> vec = new Vector<Integer>(3);
			vec.add(0, rank);
			vec.add(1, 0);
			vec.add(2, 0);
			sigMap.put(geneRanking[i].getAnnotationValue(), vec);
		}
		
		ArrayList<AbstractVector> random1 = new ArrayList<AbstractVector>();
		ArrayList<AbstractVector> random2 = new ArrayList<AbstractVector>();
		
		for(int i = 0; i < permutations; i++){
			random1.addAll(permutator.getPermutation(data1));
			random1.addAll(permutator.getPermutation(data2));
			random2.addAll(permutator.getPermutation(data1));
			random2.addAll(permutator.getPermutation(data2));
			
			//evaluating ranking list
			Gene<Object>[] geneList = getRankedDiffProfile(random1, random2, probes, num_gene);
			size = geneList.length;
			lastIndex = size - 1;
			
			for(int j = lastIndex; j >= 0; j--){
				int rank = size - j;
				Object o = geneList[j].getAnnotationValue();
				if(sigMap.containsKey(o)){
					//case: new gene rank is higher or equal
					if(rank <= sigMap.get(o).get(0)){
						Vector<Integer> vtmp = sigMap.get(o);
						vtmp.add(1, vtmp.get(1) + 1);
						vtmp.add(2, vtmp.get(2) + 1);
						sigMap.put(o, vtmp);
					}
					//case: new gene rank is lower
					else {
						Vector<Integer> vtmp = sigMap.get(o);
						vtmp.add(2, vtmp.get(2) + 1);
						sigMap.put(o, vtmp);
					}
				}
			}
		}
		
		ArrayList<Gene<Object>> significantGenes = new ArrayList<Gene<Object>>();
		//ArrayList<GeneRank> geneRanklist = new ArrayList<GeneRank>();
		
		for(Object o : sigMap.keySet()){
			double pvalue = (double)sigMap.get(o).get(1) / (double)sigMap.get(o).get(2);
			if(pvalue <= cutOff){
				//geneRanklist.add(new GeneRank(o, sigMap.get(o).get(0), pvalue));
				significantGenes.add(new Gene<Object>(-1 * (double)sigMap.get(o).get(0),o));
				//System.out.println("Id: " + o + " pvalue: " + pvalue);
			}
		}
		
		MIGroup rawScore = res.getRawScore();
		rawScore.setName("values");
		
		for(int i = 0; i < significantGenes.size(); i++){
			Gene<Object> g = significantGenes.get(i);
			rawScore.add(g.getAnnotationValue(), new DoubleMIO(g.getDataValue()));
		}

		return res;
	}
	
	@SuppressWarnings("unchecked")
	private Gene<Object>[] getRankedDiffProfile(ArrayList<AbstractVector> data1,
			ArrayList<AbstractVector> data2, Set<Object> probes, int num_gene) {
		
		PermutableMatrix set1 = new VectorBasedMatrix(data1, false);
		PermutableMatrix set2 = new VectorBasedMatrix(data2, false);
		
		double[] val = this.computeClusterDinstance(num_gene, set1, set2);
		Gene<Object>[] genes = new Gene[num_gene];
		
		int i = 0;
		for(Iterator<Object> it = probes.iterator(); it.hasNext();){
			genes[i] = new Gene<Object>(val[i], it.next());
			i++;
		}
		
		Arrays.sort(genes);
		return genes;
	}

	//returns same values as getProfile() in the old genemining (MHighestValuePercent)
	protected double[] computeClusterDinstance(int num_gene, PermutableMatrix data1, PermutableMatrix data2) {
		double[] clusterDistances = new double[num_gene];
		for(int i = 0; i < num_gene; i++){
			double median1 = data1.getRow(i).median();
			double median2 = data2.getRow(i).median();
			clusterDistances[i] = Math.abs(median1 - median2);
		}
		return clusterDistances;
	}
	
	/*
	 * Helper class to generate permutations
	 */
	private class Permutator {
		private int size;
		
		public Permutator(int size){
			this.size = size;
		}
		
		public ArrayList<AbstractVector> getPermutation(PermutableMatrix data){
			int limit = data.ncol();
			if(limit < size){
				throw new IllegalArgumentException("Size of random sets is to big!  Profile Set has only "+ limit +" Profiles. Random set size is " + size);
			}
			
			ArrayList<AbstractVector> random = new ArrayList<AbstractVector>();
			Random r = new Random();
			ArrayList<Integer> usedNumbers = new ArrayList<Integer>();
			boolean validSet = false;
			int tmp = 0;
			while(!validSet){
				while(contains(usedNumbers, tmp = r.nextInt(limit)));
				usedNumbers.add(tmp);
				random.add(data.getColumn(tmp));
				if(random.size() == size){
					validSet = true;
				}
			}
			
			return random;
		}
		
		private boolean contains(ArrayList<Integer> numbers, int n){
			int l = numbers.size();
			for(int i = 0; i < l; i++){
				if (numbers.get(i).intValue() == n){
					return true;
				}
			}		
			return false;
		}
	}


}
