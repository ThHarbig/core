package mayday.clustering.qt.algorithm.clustering;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import mayday.clustering.ClusterAlgorithms;
import mayday.clustering.ClusterTask;
import mayday.clustering.qt.algorithm.QTPSettings;
import mayday.clustering.qt.algorithm.clustering.QTPAdList;
import mayday.core.structures.linalg.matrix.PermutableMatrix;

/**
 * @author Sebastian Nagel
 * @author G&uuml;nter J&auml;ger
 * @version 0.1
 */
public class QTPClustering extends ClusterAlgorithms {
	protected Integer numOfClusters;
	protected ClusterTask cTask;
	protected ArrayList<Integer> G;
	protected ArrayList<ArrayList<Integer>> listOfA;
	protected ArrayList<SwingWorker<Void,Void>> listOfThreads;
	protected QTPAdList qtAdList;
	protected QTPSettings settings;

	public QTPSettings getSettings() {
		return settings;
	}

	public ClusterTask getcTask() {
		return cTask;
	}
	
	public PermutableMatrix getClusterData() {
		return ClusterData;
	}
	
	public ArrayList<ArrayList<Integer>> getListOfA() {
		return listOfA;
	}
	
	public QTPAdList getQtAdList() {
		return qtAdList;
	}
	
	public ArrayList<Integer> getG() {
		return G;
	}
	
	public int getRows_ClusterData() {
		return rows_ClusterData;
	}

	/**
	 * Default constructor
	 * 
	 * @param Data
	 * @param diameter
	 * @param distance
	 * @param minNumOfElem
	 * @param logger 
	 */
	public QTPClustering(PermutableMatrix Data, QTPSettings settings) {
		super(Data);
		this.settings = settings;
		this.numOfClusters = 0;
		this.listOfA = new ArrayList<ArrayList<Integer>>();
		this.listOfThreads = new ArrayList<SwingWorker<Void,Void>>();
		this.G = new ArrayList<Integer>();
	}

	/**
	 * @param cTask
	 */
	public void setClusterTask(ClusterTask cTask) {
		this.cTask = cTask;
	}
	
	/**
	 * Normalize all data vectors to row-mean = 0 and row-sd = 1
	 */
	public void normalizeDataMatrix() {
		this.ClusterData.normalizeRowWise();
	}
	
	/**
	 * QT-Clustering algorithm
	 * @return Clustering
	 */
	public int[] runClustering() {
		long startTime = System.currentTimeMillis();
		if (cTask!=null)
			cTask.writeLog("Creating adjacency list\n");
		this.createQTAdList();
		waitForThreads();
		listOfThreads.clear();
		if (cTask!=null)
			cTask.writeLog("Starting clustering\n");
		
		int currentThread = 0;

		//Set of Probes
		fillGWithProbeValues(G);
		//a max-cluster
		ArrayList<Integer> C = new ArrayList<Integer>();
		//List of found max-clusters
		ArrayList<ArrayList<Integer>> listOfClusters = new ArrayList<ArrayList<Integer>>();
		
		if(G.size() <= 1) {
			if(this.settings.getMinNumOfElem() < 2) {
				listOfClusters.add(G);
			}
			
			if(this.cTask != null) {
				if(this.cTask.hasBeenCancelled()) {
					this.doCancel();
					return null;
				}
				this.cTask.reportCurrentFractionalProgressStatus(1.0);
			}
		} else 	{
			do {
				C = new ArrayList<Integer>();
				
				int subCount = (int) Math.ceil(G.size()/this.settings.getCoreCount());
				
				for (int i=1; i<=this.settings.getCoreCount(); i++) {
					if(this.cTask != null) {
						if(this.cTask.hasBeenCancelled()) {
							this.doCancel();
							return null;
						}
					}
					
					currentThread++;
					QTPClusteringThread thread;
					if (i!=this.settings.getCoreCount()) {
						thread = new QTPClusteringThread(currentThread, (i-1) * subCount, i* subCount, this);
					} else {
						thread = new QTPClusteringThread(currentThread, (i-1) * subCount, G.size(), this);
					}
					listOfThreads.add(thread);
					thread.execute();
				}
				
				waitForThreads();
				
				fillListOfA();
				
				C = getClusterWithMaxCardinality();
				
				this.listOfA.clear();
				this.listOfThreads.clear();
				
				if(C.size() >= this.settings.getMinNumOfElem()) {
					listOfClusters.add(C);
					
					G = this.removeProbes(C, G);
					this.qtAdList.removeProbes(C);
					
					if(this.cTask != null) {
						if(this.cTask.hasBeenCancelled()) {
							this.doCancel();
							return null;
						}
						this.cTask.reportCurrentFractionalProgressStatus(1.0);
						this.cTask.writeLog("A cluster of size "+C.size()+" has been found! Continuing search...\n");
					}
				}
			} while (C.size() >= this.settings.getMinNumOfElem());
		}
		
		long timeRequired = ((System.currentTimeMillis() - startTime)/1000);
		
		this.numOfClusters = listOfClusters.size();
		
		if(this.cTask != null) {
			if(this.cTask.hasBeenCancelled()) {
				doCancel();
				return null;
			}
			this.cTask.reportCurrentFractionalProgressStatus(1.0);
			this.cTask.writeLog("QTClustering finished!\n");
			this.cTask.writeLog("Number of clusters: " + listOfClusters.size() + "\n");
			this.cTask.writeLog("time required: " + timeRequired + " sec");
		}
		
		return this.toIntArray(listOfClusters);
	}

	/**
	 * get all calculated clusters Ai 
	 */
	private void fillListOfA() {
		for (SwingWorker<Void,Void> thread : listOfThreads){
			if (thread instanceof QTPClusteringThread) {
				this.listOfA.addAll(((QTPClusteringThread)thread).getListOfA());
			}
		}
	}

	/**
	 * get the cluster with max cardinality
	 * @return
	 */
	private ArrayList<Integer> getClusterWithMaxCardinality() {
		ArrayList<Integer> A = new ArrayList<Integer>();
		
		for (ArrayList<Integer> Ai : this.listOfA){
			if (Ai != null) {
				if (A.size() < Ai.size()){
					A = Ai;
				}
			}
		}
		
		return A;
	}

	/**
	 * waits until all threads in listOfThreads are finished
	 */
	private void waitForThreads() {
		synchronized(this) {
			while (true) {
				if(this.cTask != null) {
					if(this.cTask.hasBeenCancelled()) {
						this.doCancel();
						return;
					}
				}
				
				boolean finished = true;
				for (SwingWorker<Void,Void> thread : listOfThreads){
					if (!(thread.isDone() || thread.isCancelled())) {
						finished = false;
						break;
					}
				}
				if (finished) {
					break;
				}
			}
		}
	}

	/**
	 * 
	 */
	private void doCancel() {
		for (SwingWorker<Void,Void> thread : listOfThreads) {
			if (!thread.isDone() && !thread.isCancelled()) {
				thread.cancel(true);
			}
		}
		this.cTask.writeLog("QTClustering has been canceled!");
		this.cTask.processingCancelRequest();
	}

	/**
	 * 
	 * @param G
	 */
	private void fillGWithProbeValues(ArrayList<Integer> G) {
		for(int i = 0; i < this.rows_ClusterData; i++) {
			G.add(i);
		}
	}
	
	/**
	 * Should be called, after running the QT-algorithm
	 * @return numOfClusters, the number of clusters
	 */
	public int getNumOfClusters() {
		return this.numOfClusters;
	}

	/**
	 * 
	 * @param A
	 * @param G
	 * @return
	 */
	@SuppressWarnings("unchecked") 
	public ArrayList<Integer> removeProbes(ArrayList<Integer> A, ArrayList<Integer> G) {
		ArrayList<Integer> G_A = (ArrayList<Integer>)G.clone();
		for(Integer i : A) {
			G_A.remove(i);
		}
		return G_A;
	}
	
	/**
	 * 
	 * @param listOfClusters
	 * @return
	 */
	private int[] toIntArray(ArrayList<ArrayList<Integer>> listOfClusters) {
		int[] Clustering  = new int[this.rows_ClusterData];
		//Initialize each field with -1
		//Fields with -1 will not be considered later
		//-1 corresponds to unclustered probes
		initializeClusteringArray(Clustering);

		for(int c = 0; c < listOfClusters.size(); c++) {
			for(Integer p: listOfClusters.get(c)) {
				Clustering[p] = c;
			}
		}
		
		return Clustering;
	}

	/**
	 * 
	 * @param Clustering
	 */
	private void initializeClusteringArray(int[] Clustering) {
		for(int i = 0; i < Clustering.length; i++) {
			Clustering[i] = -1;
		}
	}
	
	/**
	 * 	
	 */
	protected void createQTAdList() {
		this.qtAdList = new QTPAdList(this.settings, this.rows_ClusterData, this.ClusterData);

		int subCount = (int) Math.ceil(this.rows_ClusterData/this.settings.getCoreCount());
		
		int currentThread = 0;
		
		this.settings.checkMaxDiameter();
		
		for (int i=1; i<=this.settings.getCoreCount(); i++) {
			currentThread++;
			QTPAdListCreatorThread thread;
			if (i!=this.settings.getCoreCount()) {
				thread = new QTPAdListCreatorThread(
						currentThread, 
						(i-1) * subCount, 
						i* subCount, 
						this);
			} else {
				thread = new QTPAdListCreatorThread(
						currentThread, 
						(i-1) * subCount, 
						this.rows_ClusterData, 
						this);
			}
			listOfThreads.add(thread);
			thread.execute();
		}
	}
}
