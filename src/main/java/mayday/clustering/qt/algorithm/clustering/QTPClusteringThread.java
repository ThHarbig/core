package mayday.clustering.qt.algorithm.clustering;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import mayday.clustering.qt.algorithm.clustering.QTPPair;

/**
 * @author Sebastian Nagel
 * @version 0.1
 */
public class QTPClusteringThread extends SwingWorker<Void,Void> {
	
	private ArrayList<ArrayList<Integer>> listOfA;
	private QTPClustering qt;
	private int threadIndex;
	private Integer indexFrom;
	private Integer indexTo;
	private ArrayList<QTPPair> backupList;
	
	public ArrayList<ArrayList<Integer>> getListOfA() {
		return listOfA;
	}

	/**
	 * 
	 * @param G
	 * @param d
	 * @param index
	 * @param cTask
	 * @param logger 
	 */
	QTPClusteringThread(int threadIndex, Integer indexFrom, Integer indexTo, QTPClustering qt) {
		this.threadIndex = threadIndex;
		this.indexFrom = indexFrom;
		this.indexTo = indexTo;
		this.qt = qt;
		this.listOfA = new ArrayList<ArrayList<Integer>>();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		for(int i = indexFrom; i < indexTo;  i++) {
			if (qt.getcTask() != null && threadIndex == 1) {
				qt.getcTask().reportCurrentFractionalProgressStatus((double) i/indexTo);
			}
			boolean flag = true;
			ArrayList<Integer> A = new ArrayList<Integer>();
			int aProbe = qt.getG().get(i);
			backupList = (ArrayList<QTPPair>) qt.getQtAdList().getClone(aProbe);
			A.add(aProbe);
			
			while((flag == true) && (A.size() != qt.getG().size())) {
				if(qt.getcTask() != null) {
					if(qt.getcTask().hasBeenCancelled()) {
						cancel(true);
						return null;
					}
				}
				
				Integer j = getNextQualityElement();
				
				if(j != null) {
					A.add(j);
				} else {
					flag = false;
				}
			}
			
			listOfA.add(A);
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.SwingWorker#done()
	 */
	@Override
	protected void done() {}
	
	/**
	 * @param probe
	 * @return next Element that minimizes the diameter
	 */
	public Integer getNextQualityElement() {	
		Integer minElem = null;
		//the first element of the list is the next quality-element
		if(backupList.size() != 0) {
			minElem = backupList.get(0).getProbe();
			backupList.remove(0);
			mergeLists(minElem);
		}
		
		return minElem;
	}
	
	/**
	 * merges two distance-lists.
	 * the new list contains all probes that are found in the first and second list.
	 * so it is guaranteed that only probes are added to the list, which satisfy the 
	 * threshold distance.
	 * 
	 * for each probe which is added to the new list, a new distance is calculated.
	 * newDistance = max(oldDistance1, oldDistance2)
	 * 
	 * by doing so, it is guaranteed, that the first element of the new list is the next
	 * quality-element for the algorithm
	 * 
	 * runtime: O(k*log(k)), where k is the length of the shorter list
	 *
	 * @param l2
	 */
	public void mergeLists(Integer l2) {
		ArrayList<QTPPair> newList = new ArrayList<QTPPair>();
		QTPPair elem = null;
		ArrayList<QTPPair> list1 = backupList;
		ArrayList<QTPPair> list2 = qt.getQtAdList().getClone(l2);
		Integer minSize = null;
		ArrayList<QTPPair> minList = null;
		ArrayList<QTPPair> maxList = null;
		
		if(list1.size() < list2.size()) {
			minSize = list1.size();
			minList = list1;
			maxList = list2;
		} else {
			minSize = list2.size();
			minList = list2;
			maxList = list1;
		}
		
		for(int i = 0; i < minSize; i++) {
			elem = minList.get(i);
			if(maxList.contains(elem)) {
				//TODO: sinnvoll? maxElem und elem sind doch immer das gleiche Element
				QTPPair maxElem = maxList.get(maxList.indexOf(elem));
				double newDistance = Math.max(elem.getDistance(), maxElem.getDistance());
				elem.setDistance(newDistance);
				QTPAdList.add(elem, newList);
			}
		}
		backupList = newList;
	}

	public boolean isRunning() {
		return !isDone() && !isCancelled();
	}
}
