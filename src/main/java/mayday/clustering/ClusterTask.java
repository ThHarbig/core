package mayday.clustering;

import java.util.HashMap;

import mayday.core.tasks.AbstractTask;

/**
 * Adapts the old CLusterTask interface to AbstractTask
 * @author Janko Dietzsch, Florian Battke
 * @version 0.1
 */
public class ClusterTask extends AbstractTask implements IProgressState {
	
	boolean canceled = false;
	ClusterAlgorithms clAlg = null;
	int[] clResult = null;
	
	private HashMap<String, Object> stateEvent = new HashMap<String, Object>();
	
	/**
	 * @param name
	 */
	public ClusterTask(String name) {
		super(name);
	}



	/* (non-Javadoc)
	 * @see mayday.core.tasks.AbstractTask#initialize()
	 */
	protected void initialize() {	}
	

	/* (non-Javadoc)
	 * @see mayday.core.tasks.AbstractTask#doWork()
	 */
	protected void doWork() {
		this.clResult = this.clAlg.runClustering();
	}

	/* (non-Javadoc)
	 * @see mayday.clustering.IProgressState#reportCurrentFractionalProgressStatus(double)
	 */
	public boolean reportCurrentFractionalProgressStatus(double FractionalStatus) {		
		int value = (int) (FractionalStatus * 10000.0);
		setProgress(new Integer(value));
		return canceled;
	}

	/* (non-Javadoc)
	 * @see mayday.clustering.IProgressState#setMaxProgressStatus(int)
	 */
	public void setMaxProgressStatus(int ProgressMax) {
		
		this.stateEvent.put("ACTIONS.MAX",new Integer(ProgressMax));
	}

	/* (non-Javadoc)
	 * @see mayday.clustering.IProgressState#setMinProgressStatus(int)
	 */
	public void setMinProgressStatus(int ProgressMin) {
		
		this.stateEvent.put("ACTIONS.MIN",new Integer(ProgressMin));
	}
	
	/* (non-Javadoc)
	 * @see mayday.clustering.IProgressState#reportCurrentAbsolutProgressStatus(int)
	 */
	public boolean reportCurrentAbsolutProgressStatus(int CurrentStatus) {
		this.stateEvent.put("ACTIONS.VALUE", new Integer(CurrentStatus));
		return hasBeenCancelled();
	}

	/* (non-Javadoc)
	 * @see mayday.clustering.IProgressState#countUpProgressStatus(int)
	 */
	public boolean increaseProgressStatus(int Progress) {
		int curStatus = ((Integer) this.stateEvent.get("ACTIONS.VALUE"));
		curStatus+=Progress;
		this.stateEvent.put("ACTIONS.VALUE", new Integer(curStatus));
		return canceled;
	}
	
	public synchronized ClusterAlgorithms getClAlg() {
		return clAlg;
	}

	public synchronized void setClAlg(ClusterAlgorithms clAlg) {
		this.clAlg = clAlg;
	}

	public synchronized int[] getClResult() {
		return clResult;
	}

	public synchronized void setClResult(int[] clResult) {
		this.clResult = clResult;
	}

	public synchronized boolean isCanceled() {
		return canceled;
	}

}
