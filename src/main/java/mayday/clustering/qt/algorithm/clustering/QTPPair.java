package mayday.clustering.qt.algorithm.clustering;

/**
 * @author Sebastian Nagel
 * @author G&uuml;nter J&auml;ger
 * @version 0.1
 */
public class QTPPair {	
	private int probe;
	
	public void setProbe(int probe) {
		this.probe = probe;
	}

	private double distance;
	
	/**
	 * @param probe
	 * @param distance
	 */
	public QTPPair(int probe, double distance) {
		this.distance = distance;
		this.probe = probe;
	}
	
	/**
	 * @return distance
	 */
	public double getDistance() {
		return this.distance;
	}
	
	/**
	 * @return probe
	 */
	public int getProbe() {
		return this.probe;
	}
	
	/**
	 * @param distance
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof QTPPair)) return false;
		
		if(this.probe == ((QTPPair)obj).probe) {
			return true;
		}
		return false;
	}
}
