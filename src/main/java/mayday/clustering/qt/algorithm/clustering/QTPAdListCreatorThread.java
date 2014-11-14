package mayday.clustering.qt.algorithm.clustering;

import javax.swing.SwingWorker;

/**
 * @author Sebastian Nagel
 * @version 0.1
 */
public class QTPAdListCreatorThread extends SwingWorker<Void,Void> {

	private int threadIndex;
	private int i;
	private int j;
	private QTPClustering qt;

	public QTPAdListCreatorThread(int threadIndex, int i, int j, QTPClustering qt) {
		this.threadIndex = threadIndex;
		this.i = i;
		this.j = j;
		this.qt = qt;
	}
	/*
	 * (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		for(int i = this.i; i < this.j; i++) {
			for(int j = i+1; j < this.qt.getRows_ClusterData(); j++) {
				if (qt.getcTask() != null && threadIndex == 1) {
					//qt.getcTask().reportCurrentFractionalProgressStatus((double) i*j/(this.j * this.qt.getRows_ClusterData()));
				}
				if(qt.getcTask() != null) {
					if(qt.getcTask().hasBeenCancelled()) {
						cancel(true);
						return null;
					}
				}
				if (qt.getSettings().isEnableEnhancement()
						&& qt.getSettings().getEnhancementThreshold() > 0.0d) {
					qt.getQtAdList().addEnhancement(i, j);
				} else {
					qt.getQtAdList().add(i, j);
				}
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.SwingWorker#done()
	 */
	@Override
	protected void done() {}
}
