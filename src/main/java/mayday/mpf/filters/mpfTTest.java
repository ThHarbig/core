package mayday.mpf.filters;

import java.util.List;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.mpf.FilterBase;
import mayday.mpf.options.OptBoolean;
import mayday.mpf.options.OptClasses;
import mayday.mpf.options.OptDouble;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptPagedDropDown;
import mayday.mpf.options.OptString;
import org.apache.commons.math3.stat.inference.TTest;

public class mpfTTest extends FilterBase{

	private OptPagedDropDown samplesize = new OptPagedDropDown("Samplesize",
			"Select if you want to do a one or two-sampled t-test",
			new String[]{"one-sample", "two-sample"},0);
	//private OptPagedDropDown allSamples = new OptPagedDropDown ("Use all Experiments", "Do you want to use all Experiments for the t-test,\nor select a subgroup by indeces?", new String[]{"use all", "select manually"},0);

	private OptClasses Groups = new OptClasses("Groups","Select two groups. The one-sample t test will only use group 1, \nthe two-sample test will use both groups.\nThis can only be done while the pipeline is running.","");

	private OptDouble mu = new OptDouble("mu","Set mu for H0 ", 0.0);
	private OptDropDown kindOfTest = new OptDropDown("kind of test:",
			"What kind of test do you want to do",
			new String[]{"one-sided, less", "one-sided, greater", "two-sided"},0);
	private OptDropDown variances = new OptDropDown("Variance assumption of samples:",
			"Do you assume homoscedastic (equal) or heteroscedastic(unequal) variance \n for heteroscedastic variance a Welsh-test is performed \n variance is pooled for homoscedastic test ",
			new String[]{"homoscedastic", "heteroscedastic"},1);
	private OptPagedDropDown paired = new OptPagedDropDown(" paired or unpaired",
			"Do you want to do a paired or unpaired t-test",
			new String[]{"paired", "unpaired"},1);
	private OptString pmioName = new OptString("MIO group name for p-value", "the name of the Meta Information Object, that later contains the p-value", "p-value");
	private OptBoolean t = new OptBoolean("Add t-statistic as MIO group", "Do you want to have the t-statistic as MIO group",false);
	private OptBoolean smeans = new OptBoolean("Add sample means and foldchange MIO groups", "Do you want to have the means for the sample(s) as MIO group",false);



	public mpfTTest() {
		super(1,1);

		pli.setName("t Test");
		pli.setIdentifier("PAS.affyrma.ttest");
		pli.setAuthor("Anna Jasper");
		pli.setEmail("Anna.ivic@gmail.com");
		pli.setAbout("t Test module with various options");
		pli.addDependencies(new String[]{"LIB.Commons.math"});
		pli.replaceCategory("Statistics");

		Options.add(samplesize);
		//Options.add(allSamples);
		Options.add(Groups);
		Options.add(kindOfTest);
		Options.add(mu);
		Options.add(variances);
		Options.add(paired);
		Options.add(pmioName);
		Options.add(t);
		Options.add(smeans);

		//samplesize.addOption(0, allSamples);
		//allSamples.addOption(1,s1);
		//samplesize.addOption(1,s1);
		//samplesize.addOption(1,s2);
		samplesize.addOption(0,mu);
		samplesize.addOption(1,variances);
		samplesize.addOption(1,paired);


		Groups.setEditable(false);
	}

	@SuppressWarnings({ "deprecation" })
	public void execute() throws Exception {
		OutputData[0] = InputData[0];

		MIManager mim = OutputData[0].getProbeList().getDataSet().getMIManager();
		
		MIGroup pVal = mim.newGroup("PAS.MIO.Double", pmioName.Value);
		
		MIGroup tVal = null;
		if(t.Value){
			tVal = mim.newGroup("PAS.MIO.Double", "t-statistic", pVal);
		}

		MIGroup[] meanGroups = null;
		MIGroup foldChange = null;
		if (smeans.Value) {
			if (samplesize.Value == 1) {// two-sample 
				meanGroups = new MIGroup[2];
				foldChange = mim.newGroup("PAS.MIO.Double", "fold change", pVal);
			}
			else 
				meanGroups = new MIGroup[1];
			for (int i=0; i!=meanGroups.length; ++i)
				meanGroups[i] = mim.newGroup("PAS.MIO.Double", "sample mean "+i, pVal);
		}

		
		ProgressMeter.initializeStepper(InputData[0].size());



		TTest tTest= new TTest();


		double [] sampleOne = null; 
		double [] sampleTwo = null;
		Integer[] s1Indexes = null;
		Integer[] s2Indexes = null;

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

		s1Indexes =  classSelection.get(0).toArray(new Integer[0]);
		sampleOne = new double[s1Indexes.length];

		if (samplesize.Value == 1) { // we make a two sample t-test
			if (classSelection.size()<2)
				throw new Exception("you need to select at least two classes for the two-sample t test.");
			s2Indexes = classSelection.get(1).toArray(new Integer[0]);
			sampleTwo = new double[s2Indexes.length];
		}

		double[] entry = null;

		// compute the t-statistics and the p-value of all probes in the probe lists and attach it as
		// an mio to each probe
		for (Probe pb: OutputData[0]) {

			sampleOne = getVals(pb, s1Indexes);
			if (samplesize.Value == 1) // two-sampled, 
			{
				sampleTwo = getVals(pb, s2Indexes);
			}

			entry = new double[2];

			try { // compute t-statistic
				entry[0] = getTStatistics(tTest, sampleOne, sampleTwo);

			} catch (Exception e1) {
				e1.printStackTrace();
			}

			try { // get p-value
				entry[1] = getPValueTTest(tTest, sampleOne, sampleTwo, entry[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// write p-value to MIO
			DoubleMIO pMio = new DoubleMIO (entry[1]);
			pVal.add(pb, pMio);

			if(t.Value && tVal!=null){
				DoubleMIO tMio = new DoubleMIO(entry[0] );
				tVal.add(pb, tMio);
			}
			
			if (smeans.Value && meanGroups!=null) {
				double m1 =  new DoubleVector(sampleOne).mean();
				meanGroups[0].add(pb, new DoubleMIO( m1 ));
				if (samplesize.Value == 1 && foldChange!=null) {// two-sample
					double m2 = new DoubleVector(sampleTwo).mean();
					meanGroups[1].add(pb, new DoubleMIO( m2 ));
					double fc = m1-m2;
					foldChange.add(pb, new DoubleMIO( fc ));
				}
			}

			ProgressMeter.stepStepper(1);

		}

	}

	// get expression values for each experiment in probe pb
	private double[] getVals(Probe pb, Integer[] index){
		double[] d = new double[index.length];
		for (int i = 0; i < index.length; ++i){
			d[i] = pb.getValue(index[i]);
		}
		return d;

	}

	// check if the user has entered an experiment twice within one sample
	private static void testDoubleExpWithinSample(int[] arr) throws Exception{
		int k=0;
		int num;
		while(k<(arr.length-1)){
			num = arr[k];
			for (int i=(k+1); i<arr.length; ++i){
				if(arr[i]==num){
					throw new Exception("Each experiment can only be once ! \nExperiment " + (num +1) + " occurs at least twice" );
				}
			}
			++k;
		}

	}

	// check if every experiment is entered only once
	public static void testDoubleExperiments(int[] arr1, int[] arr2) throws Exception{
		int k= 0;
		int num;
		testDoubleExpWithinSample(arr1);
		testDoubleExpWithinSample(arr2);
		while(k<arr1.length){
			num = arr1[k];
			for(int j = 0; j < arr2.length; j++){
				if(arr2[j] == num) {
					throw new Exception("Each experiment can only be in one group! \nExperiment " + (num +1) + " occurs in both groups! " );
				}
			}
			++k;
		}

	}


	// compute t-statistic
	private double getTStatistics(TTest tTest, double [] sample1, double [] sample2)
	throws Exception{
		double tStat = 0.0d;
		if (samplesize.Value==0) { // one-sample t-test
			tStat = tTest.t(mu.Value, sample1);
		} else { // two-sample t-test
			if (paired.Value==0) { // paired t-test
				tStat = tTest.pairedT(sample1,sample2);
			} else if (variances.Value==0) { // equal variances
				tStat = tTest.homoscedasticT(sample1, sample2);
			} else { // unequal variances
				tStat = tTest.t(sample1,sample2);
			}
		}
		return tStat;
	}

	// compute p-value
	private double getPValueTTest(TTest tTest, double [] sample1, double [] sample2, double tStatistics)
	throws Exception {
		double pVal = 2.0d;
		if (samplesize.Value==0) { // one-sample t-test
			pVal = tTest.tTest(mu.Value, sample1);
		} else { // two-sample t-test
			if (paired.Value==0) { // paired t-test
				pVal = tTest.pairedTTest(sample1,sample2);
			} else if (variances.Value==0) { // equal variances
				pVal = tTest.homoscedasticTTest(sample1, sample2);
			} else { // unequal variances
				pVal = tTest.tTest(sample1,sample2);
			}
		}
		if (kindOfTest.Value < 2) { // t-test is one-sided
			pVal = 0.5 * pVal;
			if (kindOfTest.Value == 0 ) { // H0: "less" than mu
				if (tStatistics > 0) pVal = 1d - pVal;
			} else if (kindOfTest.Value ==1) { // HO: "greater" than mu
				if (tStatistics < 0) pVal = 1d - pVal;
			}
		};


		return pVal;
	}
}
