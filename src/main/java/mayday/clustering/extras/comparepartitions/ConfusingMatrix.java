package mayday.clustering.extras.comparepartitions;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Vector;

import mayday.core.Probe;

public class ConfusingMatrix {
	
	int[][] data;
	Partition p1,p2;
	Collection<Probe> probes = new TreeSet<Probe>();
	
	public ConfusingMatrix(Partition p1, Partition p2) {
		data = new int[p1.size()][p2.size()];
		this.p1=p1;
		this.p2=p2;
		
		LinkedList<String> names1 = new LinkedList<String>(p1.getPartitionNames());
		LinkedList<String> names2 = new LinkedList<String>(p2.getPartitionNames());
		
		probes.clear();
		probes.addAll(p1.getProbes());
		probes.addAll(p2.getProbes());
		
		for (Probe p : probes) {
			String g1 = p1.getPartition(p);
			String g2 = p2.getPartition(p);
			
			int i1 = names1.indexOf(g1);
			int i2 = names2.indexOf(g2);

			data[i1][i2]++;
		}
	}
	
	protected static Object[] argMax(int[][] values, int margin, int fixPos) {
		return argMax(values, margin, fixPos, null);		
	}
	
	protected static Object[] argMax(int[][] values, int margin, int fixPos, Integer firstArgMax) {
		int max = margin==0 ? values.length : values[0].length;
		double mval = Double.MIN_VALUE;
		int argmax = 0;
		for (Integer i=0; i!=max; ++i) {
			double val = margin==0 ? values[i][fixPos] : values[fixPos][i];
			if (val>mval && i!=firstArgMax) {
				mval = val;
				argmax = i;
			}
		}
		return new Object[]{argmax, mval};		
	}
	
	public int[] countShards() {
		int shards=0;
		int n1 = p1.size();
		int n2 = p2.size();
		for(int i=0; i!=n1; ++i) 
			for (int j=0; j!=n2; ++j)
				if (data[i][j]!=0 && (i+j)>0)
					shards++;
		
		int maxshards=((n1-1)*(n2-1));
		int minshards=Math.max(n1,n2)-1;
		return new int[]{shards, minshards, maxshards};
	}
	
	public long[] countPairs() {
		long pairs = 0;
		long okPairs = 0;
		for (Probe p : probes) {
			for (Probe q : probes) {
				if (p!=q) {
					++pairs;
					boolean together1 = p1.getPartition(p).equals(p1.getPartition(q)); 
					boolean together2 = p2.getPartition(p).equals(p2.getPartition(q)); 
					if (together1==together2)
						++okPairs;
				}
			}
		}
		return new long[]{okPairs, pairs};
	}

	public String getShardInfo() {
		int[] r = countShards();
		return "<b>"+r[0]+" nonempty intersections</b> (theoretical limits: "+r[1]+"-"+r[2]+")";
	}
	
	public String getPairInfo() {
		long[] r = countPairs();
		double pperc = ((double)Math.round(r[0]*10000/r[1]))/100;
		return "<b>"+r[0]+" of "+r[1]+" pairs</b> have the same status in both partitions (<b>"+pperc+"%</b>)";
	}
	
	@SuppressWarnings("unchecked")
	public Collection<ClusterOverlap> getInclusions() {
		LinkedList<ClusterOverlap> ret = new LinkedList<ClusterOverlap>();
		
		int n1 = p1.size();
		int n2 = p2.size();
		LinkedList<String> leftNames = new LinkedList<String>(p1.getPartitionNames());
		LinkedList<String> rightNames = new LinkedList<String>(p2.getPartitionNames());

		Vector<ClusterOverlap> leftMapping = new Vector<ClusterOverlap>();
		Vector<ClusterOverlap> rightMapping = new Vector<ClusterOverlap>();
		
		// Map first clustering to second
		for(int i=0; i!=n1; ++i) {
			Object[] r = argMax(data, 1, i);
			int mapped_to_cluster = (Integer) r[0];
			String leftName = leftNames.get(i);
			if (rightNames.get(mapped_to_cluster).equals(Partition.UNCLUSTERED)) {
				// never map to the *UNCLUSTERED* partition			
				r = argMax(data,1,i,mapped_to_cluster);
				if ((Double)r[1]>0)
					mapped_to_cluster = (Integer) r[0];
			}
			int overlap = ((Double)r[1]).intValue();
			String rightName = rightNames.get(mapped_to_cluster);
			int leftCount = p1.getPartitionPart(leftName).size();
			int rightCount = p2.getPartitionPart(rightName).size();
			
			leftMapping.add(new ClusterOverlap(i, mapped_to_cluster, leftCount, rightCount, overlap , leftName, rightName ));
		}
			
		// Map second clustering to first
		for(int i=0; i!=n2; ++i) {
			Object[] r = argMax(data, 0, i);
			int mapped_to_cluster = (Integer) r[0];
			String rightName = rightNames.get(i);
			if (leftNames.get(mapped_to_cluster).equals(Partition.UNCLUSTERED)) {
				// never map to the *UNCLUSTERED* partition			
				r = argMax(data,0,i,mapped_to_cluster);
				if ((Double)r[1]>0)
					mapped_to_cluster = (Integer) r[0];
			}

			int overlap = ((Double)r[1]).intValue();
			String leftName = leftNames.get(mapped_to_cluster);
			int leftCount = p1.getPartitionPart(leftName).size();
			int rightCount = p2.getPartitionPart(rightName).size();			
			rightMapping.add(new ClusterOverlap(mapped_to_cluster, i, leftCount, rightCount, overlap, leftName, rightName ));			
		}
		
		// Now begin adding lines to the output. First all the lines for the first clustering, then the remaining lines for the second clustering
		
		for (ClusterOverlap leftPartner : leftMapping) {			
			if (leftPartner.leftName.equals(Partition.UNCLUSTERED))
				continue;			
			ClusterOverlap rightPartner = rightMapping.get(leftPartner.rightIndex);
			if (rightPartner.rightName.equals(Partition.UNCLUSTERED))
				continue;			
			rightPartner.used = true;
			boolean twoway = (rightPartner.leftIndex==leftPartner.leftIndex);
			if (twoway)
				leftPartner.direction = ClusterOverlap.DIR_BOTH;
			else
				leftPartner.direction = ClusterOverlap.DIR_LTR;
			if (leftPartner.overlap>0)
				ret.add(leftPartner);			
		}
		
		// remaining ones from the other partition
		for (ClusterOverlap rightPartner : rightMapping) {			
			if (rightPartner.used)
				continue;
			if (rightPartner.rightName.equals(Partition.UNCLUSTERED))
				continue;
			rightPartner.direction = ClusterOverlap.DIR_RTL;
			if (rightPartner.overlap>0)
				ret.add(rightPartner);			
		}
		
		Collections.sort(ret);
		
		return ret;
	}

}
