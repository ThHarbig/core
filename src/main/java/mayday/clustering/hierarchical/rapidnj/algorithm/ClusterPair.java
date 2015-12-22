package mayday.clustering.hierarchical.rapidnj.algorithm;

/**
 * @author Kirsten Heitmann & Günter Jäger
 * @version 0.1
 *
 */
public class ClusterPair implements Comparable<Object>
{
	/**
	 * index of the other cluster
	 */
	public int index; 
	/**
	 * distance between clusters
	 */
	public double distance;
	/**
	 * id of the other cluster
	 */
	public int id;
	
	//used to check if this pair is obsolete
	boolean obsolete (ClusterPair a)
	{
		return distance < a.distance;
	}

	public int compareTo(Object arg0) 
	{
		ClusterPair tmp = (ClusterPair)arg0;
		return Double.compare(this.distance, tmp.distance);
//		if(this.distance < tmp.distance) return -1;
//		else if(this.distance == tmp.distance) return 0;
//		return 1;
	} 
}
