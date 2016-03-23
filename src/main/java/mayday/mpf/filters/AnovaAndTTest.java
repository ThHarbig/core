package mayday.mpf.filters;

import java.util.ArrayList;
import java.util.List;

import mayday.core.Probe;
import mayday.core.math.Binomial;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.types.DoubleMIO;
import mayday.mpf.FilterBase;
import mayday.mpf.options.OptBoolean;
import mayday.mpf.options.OptClasses;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptString;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.util.ArithmeticUtils;


public class AnovaAndTTest extends FilterBase {

	private OptClasses Groups  = new OptClasses("Groups","Select which experiments belong to which group\nThis can only be set during pipeline execution.","");

	private OptString p = new OptString("MIO group Name for the p-values","Enter the name of the MIO group that stores the p-value \n this name is import for a p-value adjustment ", "p-value");
	private OptBoolean f = new OptBoolean("Add F-statistic as MIO group", "Do you want to have the f-value as MIO-Group",false);
	private OptDropDown variances = new OptDropDown("t-test: Variance assumption of samples:","Do you assume homoscedastic (equal) or heteroscedastic(unequal) variance \n for heteroscedastic variance a Welsh-test is performed \n variance is pooled for homoscedastic test ",
			new String[]{"homoscedastic", "heteroscedastic"},1);
	private OptBoolean t = new OptBoolean("Add t-statistic as MIO", "Do you want to have the t-statistic as MIO-Group",false);

	
	
	public AnovaAndTTest() {
		super(1,1);
		
		Groups.setEditable(false);

		pli.setName("ANOVA with post-hoc t-tests");
		pli.setIdentifier("PAS.affyrma.anovattest");
		pli.setAuthor("Anna Jasper");
		pli.setEmail("Anna.ivic@gmail.com");
		pli.setAbout("Module running a simple one-way Analysis of Variance (ANOVA) and post-hoc T-Tests between all groups");
		pli.addDependencies(new String[]{"LIB.Commons.math","PAS.affyrma.anova"});
		pli.replaceCategory("Statistics");
		
		Options.add(Groups);
		Options.add(p);
		
		Options.add(f);
		Options.add(variances);
		Options.add(t);

	}



	@SuppressWarnings({ "deprecation" })
	public void execute() throws Exception {

		OutputData[0]=InputData[0];
		
		Anova a = new Anova();
		
		TTest tTest= new TTest();

		MIManager mim = OutputData[0].getProbeList().getDataSet().getMIManager();
		
		MIGroup pAnova = mim.newGroup("PAS.MIO.Double", (p.Value + " ANOVA"));

		MIGroup fVal=null;
		if(f.Value){
			fVal = mim.newGroup("PAS.MIO.Double",("F-statistic corresponding to ANOVA"+ p.Value), pAnova);
		}


		ArrayList<MIGroup> pMios= new ArrayList<MIGroup>();
		ArrayList<MIGroup> tMios= new ArrayList<MIGroup>();
		
		// start class selection dialog
		String experimentnames="";
		for (int i=0; i!=InputData[0].getNumberOfExperiments(); ++i) {
			String ename= InputData[0].getProbeList().getDataSet().getMasterTable().getExperimentName(i);
			if (ename==null)
				ename="Exp. "+i;
			experimentnames += ","+ename ;
		}
		Groups.ValueFromString(experimentnames);
		Groups.showDialog();
		
		List<List<Integer>> classSelection = Groups.getClasses();
		
		// check if each group contains >=2 experiments and we have at least 3 groups
		if (classSelection.size()<3)
			throw new Exception("You need at least 3 groups to do ANOVA");
		
		int numberOfGroups = classSelection.size();

		Integer[][] indexes = new Integer[numberOfGroups][];
		
		for (int i=0; i!=classSelection.size(); ++i) {
			if (classSelection.get(i).size()<2)
				throw new Exception("You need at least 2 experiments per group to do ANOVA.");
			indexes[i] =  classSelection.get(i).toArray(new Integer[0]);
		}

		int numberOfTtests = (int) ArithmeticUtils.binomialCoefficient(numberOfGroups,2);

		//create MIO Group for each post hoc t-test
		for (int i=0;i < numberOfTtests; ++i){
			MIGroup mioG = null;
			pMios.add(mioG);
			if (t.Value) tMios.add(mioG);
		}
		
		// create MIO groups for post-hoc t-tests
		int[] indexmap = new int[numberOfGroups*numberOfGroups];
		int k=0;
		for(int i=0; i!=(numberOfGroups-1); ++i) {
			for(int j=i+1; j!=numberOfGroups; ++j) {
				indexmap[(i*numberOfGroups)+j] = k;
				++k;
			}
		}
				
		for(int i=0; i!=(numberOfGroups-1); ++i) {
			for(int j=i+1; j!=numberOfGroups; ++j) {
				int mappedindex=indexmap[i*numberOfGroups+j];
				MIGroup pTTest = mim.newGroup("PAS.MIO.Double",(p.Value + " post-hoc t-test group "+(i+1)+" vs "+(j+1)), pAnova);
				pMios.set(mappedindex, pTTest);
				if (t.Value) {
					MIGroup tTTest = mim.newGroup("PAS.MIO.Double",
							("t-statistic post-hoc t-test group "+(i+1)+" vs "+(j+1)), pAnova);
					tMios.set(mappedindex, tTTest);
				}
				
			}
		}

		int n = 0; // number of experiments
		for (int i =0; i < indexes.length; ++i)
			n=n+indexes[i].length;

		ProgressMeter.initializeStepper(InputData[0].size());
		
		// create f-distribution 
		FDistribution fDist =
			new FDistribution(classSelection.size()-1, n-classSelection.size());
			//(Groups.Value+3-1),(n-(Groups.Value+3)));
		
		for (Probe pb : OutputData[0]) {

			double[][] values = a.getAllValues(pb, indexes); // get values from indexes
			double sqwg= 0.0; // sum of squares within group
			double[][] means=new double[values.length][2]; // means[i][0]: group mean value of group i, means[i][1] number of elements in group i
			double mean = 0.0; //overall mean
			double sum = 0.0; // overall sum
			double sqbg = 0.0; // sum of squares between groups
			double vwg = 0.0; // variance within group
			double vbg = 0.0; // variance between groups
			double fRatio = 0.0; // F-statistic
			double p = 0.0; // p-value 
			double[] entry = new double[2]; // entry[0]: t-statistic , entry[1]: corresponding p-value
			
			for(int i=0; i<values.length; ++i){
				// post-hoc t-tests
				double[] sampleOne = values[i]; // sample one for t-test
				for(int j=i+1; j<values.length;++j){
					if (this.isCancelled()) return;
					double[] sampleTwo=values[j]; //sample two for t-test
					int mappedindex=indexmap[i*numberOfGroups+j];
					try {
						entry[0] = getTStatistics(tTest, sampleOne, sampleTwo);
						entry[1] = getPValueTTest(tTest, sampleOne, sampleTwo/*, entry[0]*/);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					// write t-test p-value in MIO group
					MIGroup pMIOs = pMios.get(mappedindex);
					DoubleMIO pMio = new DoubleMIO(entry[1]);
					pMIOs.add(pb, pMio);

					if(t.Value){
						MIGroup tMIOs = tMios.get(mappedindex);
						DoubleMIO tMio = new DoubleMIO(entry[0]);
						tMIOs.add(pb, tMio);
					}

				}
				
				// ANOVA
				means[i][0] = StatUtils.mean(values[i]);
				for(int h=0; h<values[i].length; ++h){
					sqwg= sqwg + Math.pow((values[i][h]-means[i][0]),2);
				}
				means[i][1] = values[i].length;
				sum = sum + StatUtils.sum(values[i]);
			}
			
			mean = (sum / n);
			sqbg = a.getSQbg(mean, means);
			vwg = (sqwg)/(n-(classSelection.size()));
			vbg = sqbg/(classSelection.size()-1);
			fRatio= vbg/vwg;
			p = 1.0-fDist.cumulativeProbability(fRatio); // one sided greater f-fest
			
			// write ANOVA p-value
			DoubleMIO pMio = new DoubleMIO (p);
			pAnova.add(pb, pMio);
			if(f.Value && fVal!=null){
				DoubleMIO fMio = new DoubleMIO (fRatio);
				fVal.add(pb, fMio);
			}
			ProgressMeter.stepStepper(1);
		}

	}


	private double getTStatistics(TTest tTest, double [] sample1, double [] sample2)
	throws Exception{
		double tStat = 0.0d;
		if (variances.Value==0) { // equal variances
			tStat = tTest.homoscedasticT(sample1, sample2);
		} else { // unequal variances
			tStat = tTest.t(sample1,sample2);
		}

		return tStat;
	}


	private double getPValueTTest(TTest tTest, double [] sample1, double [] sample2/*, double tStatistics*/)
	throws Exception {
		double pVal = 2.0d;
		if (variances.Value==0) { // equal variances
			pVal = tTest.homoscedasticTTest(sample1, sample2);
		} else { // unequal variances
			pVal = tTest.tTest(sample1,sample2);
		}

		return pVal;
	}
	

	
}
