package mayday.clustering.dbscan;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import mayday.clustering.ClusterAlgorithms;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.PermutableMatrix;

public class DBScanClustering extends ClusterAlgorithms {

	private int MinPts;
	private double Eps;
    private DistanceMeasurePlugin usedDistance;
    private int[] ClusterIndicator;
	private int numberOfClustersFound=0;
    private static final int UNCLASSIFIED = -1;
    private static final int NOISE = 0;
    private int pointsDone;
    private HashSet<Integer> unclassified = new HashSet<Integer>();
    private boolean cancelled = false;
    private HashMap<Integer, HashMap<Integer, Double>> distances; 
    
    private void calculateDistances() {
    	try {
    		distances = new HashMap<Integer, HashMap<Integer, Double>>();
    		for (int i=0; i!=ClusterData.nrow(); ++i) {
    			HashMap<Integer, Double> submap = new HashMap<Integer, Double>();
    			for (int j=i+1; j!=ClusterData.nrow(); ++j) {
    				submap.put(j, usedDistance.getDistance(ClusterData.getRow(i), ClusterData.getRow(j)));
    			}
    			distances.put(i, submap);
    		}
    	}catch (OutOfMemoryError e) {
    		//crap
    		distances = null;
    		System.gc();
    	}
    	
    }
    
	public DBScanClustering(PermutableMatrix matrix, int MinPts, DistanceMeasurePlugin distance) {
		super(matrix);
		this.MinPts = MinPts;
		this.usedDistance = distance;
		this.ClusterIndicator=new int[rows_ClusterData];
	}

	public int[] runClustering() {

		Integer[] indexSet;
		// If the number of samples is > 2500, we use a random sample of 2000 points 
		if (ClusterData.nrow()>2500) {
			Random rnd = new Random();
			HashSet<Integer> randomNumbers = new HashSet<Integer>();
			while (randomNumbers.size()<2000) {
				randomNumbers.add(rnd.nextInt(ClusterData.nrow()));
			}
			indexSet = new Integer[randomNumbers.size()];
			Object[] tmp = randomNumbers.toArray();
			for (int i=0; i!=randomNumbers.size(); ++i)
				indexSet[i]=(Integer)tmp[i];
		} else {
			indexSet = new Integer[ClusterData.nrow()];
			for (int i=0; i!=ClusterData.nrow(); ++i)
				indexSet[i]=i;
		}

		boolean broken = false;
		
		// Create and show a plot for Eps selection
	    LinkedList<Double> distances = new LinkedList<Double>();
	    for (int i=0; i!=indexSet.length; ++i) {
	    	LinkedList<Double> dists = new LinkedList<Double>();
	    	for (int j=0; j!=indexSet.length; ++j) {
	    		dists.add(usedDistance.getDistance(
	    				this.ClusterData.getRow(indexSet[i]),
	    				this.ClusterData.getRow(indexSet[j])));
	    	}
	    	Collections.sort(dists);
	    	// pick the MinPts'th distance here
	    	distances.add(dists.get(this.MinPts));
	    	if (this.ProgressHook != null &&
	    		this.ProgressHook.reportCurrentFractionalProgressStatus(( double) i / (double) indexSet.length)) {
	    			broken = true; 
	    			return ClusterIndicator;
	    		}
	    }
	    
	    PlotEps l_ploteps = null;
	    
	    if (broken)
	    	return null;
	    

	    Collections.sort(distances);

	    int max = distances.size(); //Math.min(150, distances.size());

	    double[] Epses = new double[max] ;

	    for (int i=0; i!=max; ++i)
	    	Epses[i]=distances.get(max-(i+1));

	    l_ploteps = new PlotEps(
	    		"Eps Plot for MinPts = "+this.MinPts,
	    		Epses,
	    		640,480);

	    l_ploteps.initiate();

	    this.Eps = l_ploteps.eps;
	    
	    if (Double.isNaN(Eps))
	    	return null;

//	    this.Eps = -1;
//	    while (Eps<=0) {
//		    String tmp = 
//          		 (String)JOptionPane.showInputDialog( null, 
//          				 "Enter a double value >0 for Epsilon: ", 
//                  "DBScan",
//                  JOptionPane.QUESTION_MESSAGE,
//                  null,
//                  null,
//                  "" );
//		    if (tmp==null) { // cancel pressed
//		    	if (l_ploteps!=null)
//		    		l_ploteps.dispose();
//		    	this.cancelled=true;
//		    	return new int[0];
//		    }
//			try {
//			    this.Eps = Double.parseDouble(tmp);
//	    	} catch (Exception e) {
//	    		// ignore
//	    	}
//	    }

	    if (l_ploteps != null) l_ploteps.setVisible(false);
	    
	    // Now that we have Eps, we can start clustering
	    pointsDone = 0;
	    int ClusterID = 1;
	    
	    if (ProgressHook!=null)
	    	ProgressHook.reportCurrentFractionalProgressStatus(0.0);
	    this.calculateDistances();
	    
	    for (int i=0; i!=ClusterData.nrow(); ++i) {
	    	unclassified.add(i);
	    	ClusterIndicator[i] = UNCLASSIFIED;
	    }
	    
	    for (int pointIdx=0; pointIdx!=ClusterData.nrow(); ++pointIdx) {
	    	if (ClusterIndicator[pointIdx] == UNCLASSIFIED) { 
	    		
	    		if (expandCluster(pointIdx, ClusterID)) {
	    			++ClusterID;
	    		} // expandable cluster
	    		
	    		if (cancelled) return new int[0];
	    	} // unclassified points
	    } // all points

	    this.numberOfClustersFound = ClusterID;
	    
		return ClusterIndicator;
	}

	
	private boolean expandCluster(int pointIdx, int ClusterID) {
		boolean can_expand;
		
		List<Integer> seeds = regionQuery(pointIdx);
		
		if (seeds.size() < this.MinPts) {
			setCI(pointIdx, NOISE);
			can_expand = false;
		} else {
			for (Integer p : seeds)
				setCI(p, ClusterID);
			
			for (int pi = 0; pi!=seeds.size(); ++pi) {
				if (cancelled) return false; 
				
				Integer p = seeds.get(pi);
				
				List<Integer> results = regionQuery(p);
				
				if (results.size() >= MinPts) {
				
					for (Integer q : results) {
						
						if (ClusterIndicator[q] == UNCLASSIFIED || ClusterIndicator[q] == NOISE) {
							
							if (ClusterIndicator[q] == UNCLASSIFIED) 
								seeds.add(q);
							
							setCI(q,ClusterID);
							
						} // Unclassified or noise
						
					} // q over results
					
				} // MinPts in results
			}  // p over seeds
			can_expand = true;
		}
	
		return can_expand;
	}
	
	private void setCI(int idx, int ClusterID) {
		ClusterIndicator[idx] = ClusterID;
		++pointsDone;
		unclassified.remove(idx);
    	if (ProgressHook!=null)
    		cancelled = ProgressHook.reportCurrentFractionalProgressStatus((double)pointsDone / (double) ClusterData.nrow()); 
	}
	
	private double getDistance(int i, int j) {
		if (distances==null)
			return usedDistance.getDistance(ClusterData.getRow(i), ClusterData.getRow(j));
		if (i<j) return distances.get(i).get(j);
		else return distances.get(j).get(i);
	}
	
	private List<Integer> regionQuery(int pointIdx) {
 		List<Integer> retval = new LinkedList<Integer>();
		for (int i : unclassified) {
			if (i!=pointIdx &&
					getDistance(i,pointIdx) <= this.Eps)
					retval.add(i);
		}
		return retval;
	}
	
	public int getNumberOfClustersFound() {
		return numberOfClustersFound;
	}

	public double getEps() {
		return Eps;
	}

}
