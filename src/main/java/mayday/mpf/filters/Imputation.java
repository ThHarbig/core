package mayday.mpf.filters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Vector;

import mayday.core.Probe;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.mpf.options.OptBoolean;
import mayday.mpf.options.OptDistanceMeasure;
import mayday.mpf.options.OptDouble;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptInteger;
import mayday.mpf.options.OptPagedDropDown;

public class Imputation extends mayday.mpf.FilterBase {
	
	private String[] Methods = new String[]{
			"Remove probes",
			"Set to constant value",
			"Set to probe average",
			"Set to experiment average",
			"K-Nearest Neighbor"};
	
	private int DefaultMethod = 0;
	
	private OptDropDown AveragingMethod;
	private OptPagedDropDown MethodSelector;
	private OptDistanceMeasure DistanceMetric;
	private OptDouble ConstantValue, MinimumDataThreshold;
	private OptBoolean UseThreshold;
	private OptInteger NumberK;

	public Imputation() {
		super(1,1);

		pli.setName("Missing Value Imputation");
		pli.setIdentifier("PAS.mpf.imputation");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Provides different methods to cope with missing values. You can choose to remove " +
				"probes with missing values or select an imputation method to create data for " +
				"missing values. Available methods are constant imputation, gene/experiment average " +
				"and K-Nearest Neighbor.");
		pli.addDependencies(new String[]{"PAS.core.DistanceMeasures"});
		pli.replaceCategory("Data transformation");
		
		
		MethodSelector = new OptPagedDropDown("Method",
				"Select how to treat missing values", Methods,DefaultMethod);
		
		Options.add(MethodSelector);

		// Options for all imputation methods
		UseThreshold = new OptBoolean("Remove bad probes","Activate this option to remove probes that have too many missing values.", true);
		Options.add(UseThreshold);
		MinimumDataThreshold = new OptDouble("Removal threshold (percent)","Probes with less than the given percentage of existing data will be removed",70.0);
		MinimumDataThreshold.setBounds(0.0,100.0);
		Options.add(MinimumDataThreshold);

		// Options for constant value
		ConstantValue = new OptDouble("Constant value","The new value to fill in for missing values",0.0);
		Options.add(ConstantValue);
		MethodSelector.addOption(1,ConstantValue);
		MethodSelector.addOption(1,UseThreshold);
		MethodSelector.addOption(1,MinimumDataThreshold);
				
		// Options for probe average
		AveragingMethod = new OptDropDown("Averaging method","Select how averages will be computed",
				new String[]{"Mean","Median"},0);
		Options.add(AveragingMethod);
		MethodSelector.addOption(2,AveragingMethod);
		MethodSelector.addOption(2,UseThreshold);
		MethodSelector.addOption(2,MinimumDataThreshold);

		// Options for experiment average
		MethodSelector.addOption(3,AveragingMethod);
		MethodSelector.addOption(3,UseThreshold);
		MethodSelector.addOption(3,MinimumDataThreshold);
	
		// Options for KNN
		NumberK = new OptInteger("Number of Neighbors (k)","Specify how many nearest neighbors should be used",10);
		Options.add(NumberK);
		MethodSelector.addOption(4,NumberK);
	
		DistanceMetric = new OptDistanceMeasure(Options); 

		MethodSelector.addOption(4,DistanceMetric);
		MethodSelector.addOption(4,AveragingMethod);
		MethodSelector.addOption(4,UseThreshold);
		MethodSelector.addOption(4,MinimumDataThreshold);

	}
	
	private int mvCounter, rpCounter;

	public void execute() {
		OutputData[0] = InputData[0];
		if (InputData[0].size()==0) return;
		
		int totalSteps = InputData[0].size();
		totalSteps += UseThreshold.Value ? InputData[0].size() : 0;
		ProgressMeter.initializeStepper(totalSteps);
		
		mvCounter=0; // imputed values 
		rpCounter=0; // removed probes
		
		switch (MethodSelector.Value) {
		case 0: execute_RemoveProbes(); break;
		case 1: execute_Constant(); break;
		case 2: execute_AverageProbe(); break;
		case 3: execute_AverageExperiment(); break;  
		case 4: execute_KNN(); break;
		}
		
		ProgressMeter.writeLogLine(mvCounter + " missing values imputed.");
	}
	
	private void execute_applyThreshold(double minimumPercentage) {
				
		for (Probe pb : OutputData[0]) {
			if (isCancelled()) return;
			double perc = 0;
			for (int j=0; j!=pb.getNumberOfExperiments(); ++j)
				if (pb.getValue(j)!=null) ++perc;
			perc/=pb.getNumberOfExperiments();
			if (perc*100<minimumPercentage) {
				OutputData[0].remove(pb);
				++rpCounter;
			}
			
			ProgressMeter.stepStepper(1);
		}
		ProgressMeter.writeLogLine(rpCounter + " probes removed.");
	}
	
	private void execute_RemoveProbes() {
		execute_applyThreshold(100.0);
	}
	
	private void execute_Constant() {
		if (UseThreshold.Value) execute_applyThreshold(MinimumDataThreshold.Value);
		for (Probe pb : OutputData[0]) {
			if (isCancelled()) return;

			for (int j=0; j!=pb.getNumberOfExperiments(); ++j) {
				if (pb.getValue(j)==null) {
					pb = OutputData[0].setProbeValue(pb,j,ConstantValue.Value);
					++mvCounter;
				}
			}

			ProgressMeter.stepStepper(1);
		}
	}
	

	private double ComputeVecAverage(Vector<Double> vd) {
		if (vd.size()==0) return 0;
		switch(AveragingMethod.Value) {
		case 0: // Mean
			double avg = 0; double count=0;
			for (int j=0; j!=vd.size(); ++j) {
				if (vd.get(j)!=null) {
					avg+=vd.get(j);
					++count;
				}
			}
			return avg/count;
		case 1: // Median
			Vector<Double> values = new Vector<Double>();			
			for (int j=0; j!=vd.size(); ++j)  
				if (vd.get(j)!=null) values.add(vd.get(j));
			Collections.sort(values);
			double middle = values.size()/2.0;
			if (values.size()%2==1) 
				return values.get((int)Math.floor(middle));
			else
				return (values.get((int)middle-1) + values.get((int)middle))/2.0;			
		}
		return 0.0; // will never happen				
	}
	
	private double execute_ComputeProbeAverage(Probe pb) {
		Vector<Double> temp = new Vector<Double>();
		for(int i=0; i!=pb.getNumberOfExperiments(); ++i)
			temp.addElement(pb.getValue(i));
		return ComputeVecAverage(temp);
			
	}

	private void execute_AverageProbe() {		
		if (UseThreshold.Value) execute_applyThreshold(MinimumDataThreshold.Value);
		for (Probe pb : OutputData[0]) {
			if (isCancelled()) return;
			//calculate probe average;
			double avg = execute_ComputeProbeAverage(pb);
			// deal with missing values
			for (int j=0; j!=pb.getNumberOfExperiments(); ++j) {
				if (pb.getValue(j)==null) {
					pb = OutputData[0].setProbeValue(pb,j,avg);
					++mvCounter;
				}
			}
			
			ProgressMeter.stepStepper(1);
		
		}		
	}
	
	private void execute_AverageExperiment() {		
		if (UseThreshold.Value) execute_applyThreshold(MinimumDataThreshold.Value);
		//calculate experiment averages independently
		Vector<Vector<Double>> avgs = new Vector<Vector<Double>>();
		for (int i=0; i!=OutputData[0].getNumberOfExperiments(); ++i)
			avgs.add(new Vector<Double>());
		if (OutputData[0].size()>0) {
			for (Probe pb : OutputData[0]) {
				for (int j=0; j!=pb.getNumberOfExperiments(); ++j)
					avgs.get(j).add(pb.getValue(j));
			}
				
		}
		Vector<Double> avg = new Vector<Double>();
		for (int i=0; i!=avgs.size(); ++i)
			avg.add(ComputeVecAverage(avgs.get(i)));
			
		for (Probe pb : OutputData[0]) {
			if (isCancelled()) return;
			// deal with missing values
			for (int j=0; j!=pb.getNumberOfExperiments(); ++j) {
				if (pb.getValue(j)==null) {
					pb = OutputData[0].setProbeValue(pb,j,avg.get(j));
					++mvCounter;
				}
			}
			ProgressMeter.stepStepper(1);
			
		}		
	}

	private double getKNNAverage(Probe pb, int experimentIndex, LinkedList<Integer> validExperiments, DistanceMeasurePlugin dmt) {

		// Convert probe to double array
		double[] pbV = new double[validExperiments.size()];
		for (int i=0; i!=validExperiments.size(); ++i) pbV[i]=pb.getValue(validExperiments.get(i));

		// Create distance list
		TreeMap<Double, Probe> neighbors = new TreeMap<Double, Probe>(); //sorted in ascending key order
		
		for (Probe candidate : OutputData[0]) {
			if (candidate!=pb) {
				double[] candidateV = new double[validExperiments.size()];
           		for (int j=0; j!=validExperiments.size(); ++j) 
           			candidateV[j]=candidate.getValue(validExperiments.get(j));
				double dist = dmt.getDistance(pbV,candidateV); 
				neighbors.put(dist,candidate);				
			}
		}
		
		// Treemap is automatically sorted, so we just pick the k nn
		Vector<Probe> KNN = new Vector<Probe>();
		for (Probe nn : neighbors.values()) {
			KNN.add(nn);
			if (KNN.size()==NumberK.Value) break;
		}
		
		// Get the values for the specified experiment
		Vector<Double> vals = new Vector<Double>();
		for (Probe nn : KNN) vals.add(nn.getValue(experimentIndex));
		return ComputeVecAverage(vals);
	}
	
	
	private void execute_KNN() {
		
		if (UseThreshold.Value) execute_applyThreshold(MinimumDataThreshold.Value);
		
		// Remove all experiments that contain missing values, check if this leaves enough experiments
		LinkedList<Integer> validExperiments = new LinkedList<Integer>();
		for (int i=0; i!=OutputData[0].getNumberOfExperiments(); ++i) validExperiments.add(i);
		for (Probe pb : OutputData[0]) {
			int i=0;
			while (i<validExperiments.size()) {
				if (pb.getValue(validExperiments.get(i))==null) validExperiments.remove(i);
				else ++i;
			}
		}
		if (isCancelled()) return;
		if (validExperiments.size()==0) 
			throw new RuntimeException("KNN can not be run: The matrix contains to many missing values to\n" +
					                   "compute distances between probes. Try to use another imputation method.");

		// Create distance measure
		DistanceMeasurePlugin idm =  DistanceMetric.getDistanceMeasure();
		
		// Impute
		for (Probe pb : OutputData[0]) {
			if (isCancelled()) return;
			// deal with missing values
			for (int j=0; j!=pb.getNumberOfExperiments(); ++j) {
				if (pb.getValue(j)==null) {
					pb = OutputData[0].setProbeValue(pb,j,getKNNAverage(pb,j,validExperiments,idm));
					++mvCounter;
				}
			}
			
			ProgressMeter.stepStepper(1);

		}				
	}
	
}
