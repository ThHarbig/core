package mayday.mpf.filters;

import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.types.DoubleMIO;
import mayday.mpf.FilterBase;
import mayday.mpf.options.OptBoolean;
import mayday.mpf.options.OptClasses;
import mayday.mpf.options.OptString;

public class Anova extends FilterBase{
	

	private OptClasses Groups  = new OptClasses("Groups","Select which experiments belong to which group\nThis can only be set during pipeline execution.","");
	
	private OptString p = new OptString("MIO group Name for the p-value","Enter the name of the MIO group that stores the p-value \n this name is import for a following p-value adjustment", "ANOVA p-Value");
	private OptBoolean f = new OptBoolean("Add F-statistic as Mio", "Do you want to have the F-statistic as MIO group",false);
	
	public Anova() {
		super(1,1);
		
		Groups.setEditable(false);
		
		pli.setName("ANOVA");
		pli.setIdentifier("PAS.affyrma.anova");
		pli.setAuthor("Anna Jasper");
		pli.setEmail("Anna.ivic@gmail.com");
		pli.setAbout("Module running a simple one-way Analysis of Variance (ANOVA)");
		pli.addDependencies(new String[]{"LIB.Commons.math"});
		pli.replaceCategory("Statistics");
		

		Options.add(Groups);
		Options.add(p);
		Options.add(f);
	}





	@SuppressWarnings("deprecation")
	public void execute() throws Exception {
		OutputData[0]=InputData[0];
		
		MIManager mim = OutputData[0].getProbeList().getDataSet().getMIManager();
		
		// create MIOGoups for p and optionally f-values
		MIGroup pVal = mim.newGroup("PAS.MIO.Double", p.Value);
		
		MIGroup fVal=null;
		if(f.Value){
			fVal = mim.newGroup("PAS.MIO.Double", "ANOVA F-statistic corresponding to "+ p.Value, pVal);
		}
	
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
		int before=classSelection.size();
		classSelection=pruneClasses(classSelection);
	
		while(classSelection.size() < 3)
		{
			// show message
			Object[] options = {"Review Class Settings",
                    "Cancel"};
			int n = JOptionPane.showOptionDialog(null,
		    "At least 3 classes with at least 3 elements each are required for ANOVA",
		    "Warning",
		    JOptionPane.OK_CANCEL_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[0]);
			if(n==JOptionPane.OK_OPTION)
			{
				Groups.showDialog();
				classSelection = Groups.getClasses();
				before=classSelection.size();
				pruneClasses(classSelection);
			}else
			{
				throw new  Exception("At least 3 classes with at least 3 elements each are required for ANOVA");
			}
			
			// ask for more classes

		}
		
		if(before != classSelection.size())
		{
			ProgressMeter.writeLogLine("Warning: Some classes were removed since they only contained one experiment");
		}
		
		Integer[][] indexes = new Integer[classSelection.size()][];
		
		for (int i=0; i!=classSelection.size(); ++i) {
			if (classSelection.get(i).size()<=2)
			{
				ProgressMeter.writeLogLine("Warning: Class "+i+" contains only "+classSelection.get(i).size()+" experiments");
			}				
			indexes[i] =  classSelection.get(i).toArray(new Integer[0]);
		}

		
		
		//total number of experiments
		int n = 0; 
		for (int i =0; i < indexes.length; ++i){
			n=n+indexes[i].length;
		}
		
		
		
		ProgressMeter.initializeStepper(InputData[0].size());
		// create f-distribution
		org.apache.commons.math.distribution.FDistributionImpl fDist = 
			new org.apache.commons.math.distribution.FDistributionImpl(classSelection.size()-1, n-classSelection.size());
					//(Groups.Value+3-1),(n-(Groups.Value+3)));
		
		for (Probe pb : OutputData[0]) {
			
			double[][] values = getAllValues(pb, indexes);
			double sqwg= 0.0; // sum of squares within group
			double[][] means=new double[values.length][2]; // means[i][0] having group mean value of group i, means[i][1] number of elements in group i
			double mean = 0.0; // overall mean
			double sum = 0.0; // overall sum
			double sqbg = 0.0; // sum of squares between groups
			double vwg = 0.0; //variance within group
			double vbg = 0.0; // variance between groups
			double fRatio = 0.0; // f-statistic=(vbg/vwg)
			double p = 0.0; // p-value
			for(int i=0; i< values.length; ++i){
			
				means[i][0] = org.apache.commons.math.stat.StatUtils.mean(values[i]); // get group mean value 
				
				for(int h=0; h<values[i].length; ++h){
					sqwg= sqwg + Math.pow((values[i][h]-means[i][0]),2);
					
				}
							
				
				means[i][1] = values[i].length;
				sum = sum + org.apache.commons.math.stat.StatUtils.sum(values[i]); // summarize all values to compute mean value
			}
			mean = (sum / n);
			
			sqbg = getSQbg(mean, means);
			
			vwg = (sqwg)/(n-(classSelection.size()));  //vwg = (sqwg)/(n-(Groups.Value+3));
			vbg = sqbg/(classSelection.size()-1);      //vbg = sqbg/(Groups.Value+3-1)
			fRatio= vbg/vwg;
			p = 1.0-fDist.cumulativeProbability(fRatio); // one sided-greater t-test
			
			// write p (and f-) values into MIOs
			// create new MIO for p-value MIO Group
			
			((DoubleMIO)pVal.add(pb)).setValue(p);
			
			if(f.Value && fVal!=null){
				DoubleMIO fMio = new DoubleMIO(fRatio);
				fVal.add(pb, fMio);
			}
			ProgressMeter.stepStepper(1);
		}
		
	}
		
	private List<List<Integer>> pruneClasses(List<List<Integer>> classSelection)
	{
		Iterator<List<Integer>> iter=classSelection.iterator();
			while(iter.hasNext())
		{
			List<Integer> curr=iter.next();
			if (curr.size()<3)
			{
				iter.remove();
			}
		}
		return classSelection;
	}
	
	// get Indexes from string and check if at least two experiments are assigned to each group
	
	public int[] getIndexes(String indexes) throws Exception{
		
		String[] s;
		s= indexes.split(",");
		if(s.length < 2) throw new Exception("You need at least 2 experiments per group to do an ANOVA \n seperate experiment numbers with \", \"");
		int[] ind = new int[s.length];
		for(int i =0; i< s.length; ++i){
			ind[i]=(Integer.parseInt(s[i])-1);
		}
		
		return ind;
	}
	
	//put all values in a matrix, where each row corresponds to a group
	double[][] getAllValues(Probe pb, Integer[][] indexes){
		double vals[][] = new double[indexes.length][];
		for (int i = 0; i < indexes.length; ++i){
			vals[i] = getVals(pb, indexes[i]);
		}
		return vals;
	}
	
	// extract the values of a probe
	public double[] getVals(Probe pb, Integer[] index){
		double[] d = new double[index.length];
		for (int i = 0; i < index.length; ++i){
			d[i] = pb.getValue(index[i]);
		}
		return d;
		
 	}
	
	// compute sum of squares between groups
	public double getSQbg(double mean, double[][] GroupMeans){
		double sqbg = 0.0;
		for(int i= 0; i < GroupMeans.length; ++i){
			sqbg = sqbg + (GroupMeans[i][1] * Math.pow((GroupMeans[i][0] - mean),2));
		}
		return sqbg;
	}
	

	
	
}
