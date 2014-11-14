package mayday.statistics.RP;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import mayday.core.ClassSelectionModel;
import mayday.core.math.stattest.CorrectedStatTestResult;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.ConstantIndexVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public class RPPlugin extends StatTestPlugin {

	protected RPSetting setting;

	private Random random = new Random();

	public Map<Object, double[]> thevals;

	public RPPlugin(){
		setting = new RPSetting("Rank Product");		
	}
	
	@Override
	public Setting getSetting() {
		return setting;
	}

	@Override
	public StatTestResult runTest(Map<Object, double[]> values, ClassSelectionModel classes) {

		int permutations = setting.getPermutationCount();
		
		double _totalRuns_ = permutations+1;
		
		CorrectedStatTestResult res = new CorrectedStatTestResult();

		// transform the data into a form that is much easier to work with for RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));
		int num_gene = values.size();

		DoubleVector RP_ori_upin2 = RankProd1(data1, data2, false);
		DoubleVector rank_ori_upin2 = RP_ori_upin2.rank();

		DoubleVector RP_ori_downin2 = RankProd1(data1, data2, true);
		DoubleVector rank_ori_downin2 = RP_ori_downin2.rank(); 
		
		setProgress(100.0/_totalRuns_);

//		System.out.println("Starting "+permutations+" permutations");

		DoubleVector temp2_up = rank_ori_upin2.clone();
		DoubleVector temp2_down = rank_ori_downin2.clone();
		
		
		for (int p=1; p<=permutations; ++p) {
			DoubleMatrix[] temp_data = Newdata( data1, data2 );
			DoubleVector RP_per_upin2_oneCol = RankProd1( temp_data[0], temp_data[1], false );
			DoubleVector RP_per_downin2_oneCol = RankProd1( temp_data[0], temp_data[1],  true );
			updateRank( temp2_up, RP_ori_upin2, RP_per_upin2_oneCol);
			updateRank( temp2_down, RP_ori_downin2, RP_per_downin2_oneCol);			
			setProgress((double)p*10000.0/_totalRuns_);
		}

//		System.out.println("Computing pfp");
		DoubleVector[] upStat = computePPFP( temp2_up, rank_ori_upin2, num_gene );
		DoubleVector[] downStat = computePPFP( temp2_down, rank_ori_downin2, num_gene );
		
		// Store pval, pfp, RP in results
		MIGroup overallP = res.getPValues();
		MIGroup pval_up = res.addAdditionalValue("pval up");
		MIGroup pval_down = res.addAdditionalValue("pval down");
		MIGroup pfp_up = res.addAdditionalValue("pfp up");
		MIGroup pfp_down = res.addAdditionalValue("pfp down");
		MIGroup RP_up = res.addAdditionalValue("RP up");
		MIGroup RP_down = res.addAdditionalValue("RP down");
		
		int pvalSourceIndex = setting.returnPFP()?1:0;
		
		for (Object o : values.keySet()) {
			int i = indexMap.get(o);
			pval_up.add(o, new DoubleMIO(upStat[0].get(i)));
			pval_down.add(o, new DoubleMIO(downStat[0].get(i)));

			pfp_up.add(o, new DoubleMIO(upStat[1].get(i)));
			pfp_down.add(o, new DoubleMIO(downStat[1].get(i)));
			
			RP_up.add(o, new DoubleMIO(RP_ori_upin2.get(i)));
			RP_down.add(o, new DoubleMIO(RP_ori_downin2.get(i)));

			overallP.add( o, new DoubleMIO(
					Math.min( upStat[pvalSourceIndex].get(i), downStat[pvalSourceIndex].get(i) )
			));
		}
		
		return res;
	}
	
	
	public DoubleMatrix[] Newdata(PermutableMatrix data1, PermutableMatrix data2) {

		int k1 = data1.ncol();
		int num_gene = data1.nrow();
		DoubleMatrix new_data1 = new DoubleMatrix( num_gene, k1 ); 
		for (int k=0; k!=k1; ++k) {
			AbstractVector v = data1.getColumn(k);
			v.permute(random);
			new_data1.setColumn(k, v);
		}

		int k2 = data2.ncol();
		DoubleMatrix new_data2 = new DoubleMatrix( num_gene, k2 ); 
		for (int k=0; k!=k2; ++k) {
			AbstractVector v = data2.getColumn(k);
			v.permute(random);
			new_data2.setColumn(k, v);
		}

		DoubleMatrix[] result = new DoubleMatrix[]{new_data1, new_data2};
		return result;
	}
	
	public DoubleVector[] computePPFP( DoubleVector count_perm, DoubleVector rank_ori, int num_gene ) {
		count_perm.sort();
		AbstractVector expected = new ConstantIndexVector(count_perm.size(), 1);
		count_perm.subtract( expected );
		count_perm.unpermute();
		
		int permutations = setting.getPermutationCount(); 
		
		DoubleVector exp_count = count_perm.clone();
		exp_count.divide( permutations );
		
		DoubleVector pval = count_perm.clone();
		pval.divide( permutations * num_gene );
		
		DoubleVector pfp = exp_count;
		pfp.divide( rank_ori );
		
		return new DoubleVector[]{pval, pfp};
	}

	public DoubleVector RankProd1( PermutableMatrix data1, PermutableMatrix data2, boolean reverse) {
		
		PermutableMatrix data1_wk = reverse? data2 : data1;
		PermutableMatrix data2_wk = reverse? data1 : data2;
		
		int k1 = data1_wk.ncol();
		int k2 = data2_wk.ncol();		
		int num_col = k1*k2;
		int num_gene = data1.nrow();
		
		DoubleVector rank_prod = DoubleVector.rep( 1.0, num_gene );
		boolean largeData = (num_col > 50 && num_gene > 2000 | num_col > 100);
		
		boolean log = setting.isLogged();
		
		for (int i1=0; i1!=k1; ++i1) {
			for (int i2=0; i2!=k2; ++i2) {
				DoubleVector rank_prod_temp = data1_wk.getColumn(i1).clone();
				if (log)
					rank_prod_temp.subtract(data2_wk.getColumn(i2));
				else
					rank_prod_temp.divide(data2_wk.getColumn(i2));
				rank_prod_temp = rank_prod_temp.rank();
				if (largeData) { // apply root right now 
					rank_prod_temp.raise(1.0/num_col);
				} 
				rank_prod.multiply(rank_prod_temp);
			}				
		}	
		if (!largeData) { // do the rooting at the end
			rank_prod.raise(1.0/num_col);
		}
				
		return rank_prod;
	}

	protected void updateRank( DoubleVector oldRank, DoubleVector col1, DoubleVector nextCol ) {
		int[] ord = col1.order(); // move NAs to the end
		col1.setPermutation(ord);
		oldRank.setPermutation(ord);

		nextCol.sort();  //move NAs to the end

		// this behaves exactly like the R code only that access indices are decremented by 1 (zero-based)
		
		int i = col1.size(); 
		int j = nextCol.size();
		
		// skip NAs now
		while (Double.isNaN(nextCol.get(j-1)))
			--j; 
		
		while (Double.isNaN(col1.get(i-1)))
			--i;
			
		int firstNA = j;

		while(i>0 && j>0) { 
			while (j>0 && nextCol.get(j-1) >= col1.get(i-1)) 
				j--;
			int k=i;
			while (i>0 && j>0 && nextCol.get(j-1) < col1.get(i-1))  
				i--;
			oldRank.add( j, i, (k-1) );
		}

		// change the rank of NA items by adding the number of non-NA elements
		for (int pos : col1.whichIsNA())
			oldRank.set(pos, oldRank.get(pos)+firstNA);

		oldRank.unpermute(); 
		nextCol.unpermute();
		col1.unpermute(); 
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.statistics.RankProd",
				new String[0],
				StatTestPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Calculate RP-scores and corrected p-values",
				"Rank Product"
		);
//		DoubleVector or = new DoubleVector(new double[]{11 , 78 , 31 , 88 , 28 , 69 , 33 , 26 , 34 , 4 , 57 , 13 , 63 , 89 , 72 , 42 , 54 , 83 , 43 , 73 , 62 , 30 , 80 , 46 , 35 , 64 , 81 , 92 , 3 , 85 , 93 , 84 , 76 , 47 , 12 , 70 , 36 , 56 , 100 , 86 , 9 , 29 , 45 , 65 , 27 , 41 , 21 , 94 , 7 , 8 , 14 , 25 , 39 , 61 , 67 , 38 , 79 , 48 , 95 , 97 , 16 , 20 , 55 , 51 , 19 , 24 , 66 , 52 , 53 , 10 , 99 , 98 , 68 , 2 , 22 , 32 , 6 , 37 , 71 , 23 , 59 , 74 , 17 , 5 , 18 , 75 , 60 , 90 , 96 , 77 , 87 , 82 , 50 , 91 , 58 , 1 , 15 , 44,49,40 });
//		DoubleVector nc = new DoubleVector(new double[]{Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , 56 , 70 , 74 , 57 , 60 , 92 , 47 , 69 , 58 , 23 , 63 , 5 , 41 , 82 , 18 , 19 , 32 , 93 , 62 , 68 , 40 , 45 , 21 , 9 , 72 , 24 , 88 , 51 , 31 , 61 , 97 , 16 , 46 , 25 , 12 , 65 , 7 , 79 , 64 , 80 , 8 , 98 , 99 , 28 , 86 , 50 , 33 , 66 , 42 , 10 , 53 , 52 , 22 , 34 , 39 , 29 , 20 , 95 , 49 , 96 , 84 , 71 , 3 , 90 , 78 , 1 , 26 , 73 , 59 , 38 , 11 , 14 , 17 , 77 , 48 , 67 , 4 , 6 , 54 , 36 , 87 , 76 , 81 , 91 , 35 , 13 , 75 , 85 , 55 , 100});
//		DoubleVector c1 = new DoubleVector(new double[]{76 , 79 , 74 , 94 , 86 , 2 , 3 , 77 , 55 , 10 , 12 , 39 , 63 , 95 , 60 , 7 , 66 , 82 , 71 , 22 , 92 , 89 , 91 , 81 , 4 , 52 , 85 , 43 , 75 , 59 , 56 , 24 , 11 , 36 , 57 , 26 , 31 , 97 , 16 , 53 , 40 , 47 , 58 , 51 , 68 , 34 , 61 , 23 , 15 , 35 , 50 , 88 , 98 , 87 , 49 , 13 , 17 , 14 , 80 , 90 , 84 , 99 , 30 , 72 , 70 , 54 , 20 , 46 , 18 , 96 , 28 , 41 , 44 , 62 , 21 , 1 , 69 , 73 , 32 , 93 , 67 , 8 , 83 , 42 , 6 , 38 , 48 , 100 , 9 , 29 , 33 , 37 , 27 , 25 , 78 , 19 , 5 , 64 , 45 , 65});
//		
//		c1.set(Double.NaN,0,9);
//		
//		updateRank(or, c1, nc);
//		System.out.println(or);
//		
//		DoubleVector ur = new DoubleVector(new double[]{101 , 168 , 121 , 178 , 118 , 159 , 123 , 116 , 124 , 94 , 67 , 46 , 118 , 173 , 124 , 47 , 112 , 157 , 106 , 92 , 144 , 110 , 161 , 119 , 37 , 108 , 157 , 129 , 70 , 136 , 141 , 105 , 85 , 78 , 61 , 93 , 62 , 142 , 113 , 131 , 43 , 68 , 95 , 108 , 87 , 70 , 74 , 114 , 20 , 38 , 56 , 104 , 126 , 139 , 108 , 49 , 93 , 60 , 167 , 177 , 91 , 108 , 81 , 115 , 81 , 70 , 83 , 90 , 68 , 95 , 123 , 133 , 105 , 56 , 40 , 32 , 67 , 102 , 98 , 106 , 118 , 80 , 92 , 41 , 22 , 107 , 100 , 179 , 103 , 102 , 115 , 114 , 74 , 113 , 128 , 17 , 18 , 100 , 86 , 97 });
//		System.out.println(ur);
//		System.out.println(or);
//		System.out.println("Result as expected? "+ur.allValuesEqual(or));
		
		return pli;
	}

}
