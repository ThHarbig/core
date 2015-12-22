package mayday.clustering.qt.algorithm.clustering;

import java.util.ArrayList;

import mayday.clustering.qt.algorithm.QTPSettings;
import mayday.clustering.qt.algorithm.clustering.QTPPair;
import mayday.core.structures.linalg.matrix.AbstractMatrix;

/**
 * @author Sebastian Nagel
 * @author G&uuml;nter J&auml;ger
 * @version 0.1
 */
public class QTPAdList implements Cloneable {
	protected ArrayList<ArrayList<QTPPair>> adList;
	protected AbstractMatrix ClusterData;
	protected QTPSettings settings;
	
	/**
	 * @param usedDistance
	 * @param threshold
	 * @param numOfProbes
	 * @param ClusterData
	 */
	public QTPAdList(QTPSettings settings, int numOfProbes, AbstractMatrix ClusterData) {
		this.settings = settings;
		this.ClusterData = ClusterData;
		this.adList = new ArrayList<ArrayList<QTPPair>>();
		this.createQTMatrix(numOfProbes);
	}

	/**
	 * 
	 * @param numOfProbes
	 */
	private void createQTMatrix(int numOfProbes) {
		for(int i = 0; i < numOfProbes; i++) {
			this.adList.add(new ArrayList<QTPPair>());
		}
	}
	
	/**
	 * @param i
	 * @param j
	 */
	public synchronized void add(int i, int j) {
		double distance = this.settings.getDistanceMeasure().getDistance(this.ClusterData.getRow(i), 
				this.ClusterData.getRow(j));
		
		if (distance <= this.settings.getDiameterThreshold()) {
			//add probe j to list i
			int index = getInsertionIndex(this.adList.get(i), distance);
			this.adList.get(i).add(index, new QTPPair(j, distance));
			//add probe i to list j
			index = getInsertionIndex(this.adList.get(j), distance);
			this.adList.get(j).add(index, new QTPPair(i, distance));
		}
	}
	
	public synchronized void addEnhancement(int i, int j) {
		double distance = this.settings.getDistanceMeasure().getDistance(this.ClusterData.getRow(i), 
				this.ClusterData.getRow(j));
		
		if (this.settings.isUseMinDiameter()
				&& distance <= this.settings.getDiameterThreshold() 
								+ settings.getEnhancementThreshold()) {
			//add probe j to list i
			int index = getInsertionIndex(this.adList.get(i), distance);
			this.adList.get(i).add(index, new QTPPair(j, distance));
			//add probe i to list j
			index = getInsertionIndex(this.adList.get(j), distance);
			this.adList.get(j).add(index, new QTPPair(i, distance));
		} else if (distance <= this.settings.getMaxDiameter()) {
			//add probe j to list i
			int index = getInsertionIndex(this.adList.get(i), distance);
			if (index == this.adList.get(i).size()) {	
				if (index != 0) {
					double highestDistance = this.adList.get(i).get(index-1).getDistance();
					double enhancement = Math.max(distance - highestDistance, 0);
					
					if (enhancement <= this.settings.getEnhancementThreshold()) {
						this.adList.get(i).add(index, new QTPPair(j, distance));
					}
				} else {
					if (distance <= this.settings.getEnhancementThreshold()) {
						this.adList.get(i).add(index, new QTPPair(j, distance));
					}
				}
			} else {
				this.adList.get(i).add(index, new QTPPair(j, distance));
			}
			//add probe i to list j
			index = getInsertionIndex(this.adList.get(j), distance);
			if (index == this.adList.get(j).size()) {
				if (index != 0){
					double highestDistance = this.adList.get(j).get(index-1).getDistance();
					double enhancement = Math.max(distance - highestDistance, 0);
	
					if (enhancement <= this.settings.getEnhancementThreshold()) {
						this.adList.get(j).add(index, new QTPPair(i, distance));
					}
				} else {
					if (distance <= this.settings.getEnhancementThreshold()) {
						this.adList.get(j).add(index, new QTPPair(i, distance));
					}
				}
			} else {
				this.adList.get(j).add(index, new QTPPair(i, distance));
			}
		}
	}
	
	/**
	 * calculates the index, where the new probe should be inserted
	 * runtime O(log(k))
	 * ensures that every distance-list is sorted
	 */
	public static int getInsertionIndex(ArrayList<QTPPair> list, double distance) {
		int lowIndex = 0;
		int highIndex = list.size();
		int pos = (highIndex+lowIndex)/2;
		boolean finished = false;
		
		if(list.size() == 0) {
			return 0;
		}
		
		while(!finished) {
			if(highIndex - lowIndex == 1) {
				if(list.get(lowIndex).getDistance() > distance) {
					pos = lowIndex;
				} else {
					pos = highIndex;
				}
				finished = true;
			} else if(list.get(pos).getDistance() < distance) {
				lowIndex = pos;
				pos = (highIndex+lowIndex)/2; 
			} else if(list.get(pos).getDistance() > distance) {
				 highIndex = pos;
				 pos = (highIndex+lowIndex)/2;
			} else if(list.get(pos).getDistance() == distance) {
				finished = true;
			}
		}
		
		return pos;
	}
	
	/**
	 * 
	 * @param probe
	 * @param list
	 */
	public static void add(QTPPair probe, ArrayList<QTPPair> list) {
		QTPPair newProbe = new QTPPair(probe.getProbe(), probe.getDistance());
		int insertionIndex = getInsertionIndex(list, probe.getDistance());
		list.add(insertionIndex, newProbe);
	}
	
	/**
	 * 
	 * @param probe
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<QTPPair> getClone(Integer probe) {
		return (ArrayList<QTPPair>)this.adList.get(probe).clone();
	}

	/**
	 * @param c
	 */
	public void removeProbes(ArrayList<Integer> c) {
		for(int i = 0; i < c.size(); i++) {
			this.adList.set(c.get(i), new ArrayList<QTPPair>());
		}
		
		for(int i = 0; i < this.adList.size(); i++) {
			for(int j = this.adList.get(i).size()-1; j >= 0; j--) {
				for(int k = 0; k < c.size(); k++) {
					if(this.adList.get(i).get(j).getProbe() == c.get(k).intValue()) {
						this.adList.get(i).remove(j);
						break;
					}
				}
			}
		}
	}
}
