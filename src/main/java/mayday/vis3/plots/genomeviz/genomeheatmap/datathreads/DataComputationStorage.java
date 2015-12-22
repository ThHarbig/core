package mayday.vis3.plots.genomeviz.genomeheatmap.datathreads;

import java.util.HashMap;
import java.util.Map.Entry;

import mayday.core.DelayedUpdateTask;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;


public class DataComputationStorage {
	protected GenomeHeatMapTableModel tableModel = null;
	protected HashMap<Double, WorkOnData> work_to_do;		
		
		public DataComputationStorage(GenomeHeatMapTableModel TableModel){
			tableModel = TableModel;
			work_to_do = new HashMap<Double, WorkOnData>();
		}
		
		protected void addForUpdating(Double index, WorkOnData work) {
			synchronized(this) {
				work_to_do.put(index,work);
			}
		}
		
		protected boolean hasWork() {
			synchronized(this) {
				return work_to_do.size()>0;
			}
		}
		
		protected Object[] getForUpdating() {
			synchronized(this) {
				if (work_to_do.size()==0)
					return null;
				Entry<Double, WorkOnData> e = work_to_do.entrySet().iterator().next();
				Object[] ret = new Object[]{e.getKey(), e.getValue()};
				work_to_do.remove(e.getKey());
				return ret;
			}
		}
		
		protected DelayedUpdateTask dataSetMaker = new DelayedUpdateTask("Creating DataSet", 1000) {

			protected boolean needsUpdating() {
				return hasWork();
			}

			protected void performUpdate() {
				Object[] itd = getForUpdating();
				while ( itd!=null ) {
//					Double index = (Double) itd[0];
					WorkOnData work = (WorkOnData) itd[1];
					boolean actWorkFinished=false;

					while (actWorkFinished==false) {
						try {
							actWorkFinished = work.runOnData();
						} catch (Exception e) {
							// trying again in a moment
							try {
								e.printStackTrace();
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								System.err.println(e1);
							}
						}
					}					
					synchronized(work) {
						//work.setData();
					}
					work.setData();
					itd = getForUpdating();
				}
			}
		};
		
		public void updateCache(final Double index, final WorkOnData work) {
			addForUpdating(index, work);
			dataSetMaker.trigger();
		}
		
//		public void clearCache()
//		{
//			content.clear();
//		}
}
