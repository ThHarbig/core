package mayday.vis3.plots.genomeviz.genomeheatmap.datathreads;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeDataSet;
 
public class ThreadManager {

	protected GenomeHeatMapTableModel model;
	
	protected FittedWholeThread activeWholeThread = null;
	protected FittedCondensedThread activeCondensedThread = null;
	
	public ThreadManager(GenomeHeatMapTableModel model){
		this.model = model;
	}

	public void computeFittedWholeData(ChromosomeDataSet actualChromeData, int fitMultiplikator) {
		if(activeWholeThread != null){
			activeWholeThread.setRunning(false);
		}
		activeWholeThread = new FittedWholeThread(model, actualChromeData, fitMultiplikator);
		activeWholeThread.setRunning(true);
		activeWholeThread.start();
	}
	
	public void killThreads(){
		if(activeWholeThread != null){
			activeWholeThread.setRunning(false);
		}
		
		if(activeCondensedThread != null){
			activeCondensedThread.setRunning(false);
		}
	}

	public void setFittedWholeData() {
		if (activeWholeThread.isFinished()) {
			if (activeWholeThread.getFittedWholeProbes() != null) {

//				System.out.println("-----------");
//				System.out.println("DATA WHOLE SETTED");
//				System.out.println("-----------");

				model.setFittedWholeProbes(activeWholeThread.getFittedWholeProbes());
				model.fittedProbesSetted_Whole(true);
			}
		}
	}

	public void computeFittedCondensedData(ChromosomeDataSet actualChromeData,
			int fitMultiplikator) {
		if(activeCondensedThread != null){
			activeCondensedThread.setRunning(false);
		}
		// starts a new condensed thread for new fit multiplikator
		activeCondensedThread = new FittedCondensedThread(model,actualChromeData, fitMultiplikator);
		activeCondensedThread.setRunning(true);
		activeCondensedThread.start();
	}

	public void setFittedCondensedData() {

		if (activeCondensedThread.isFinished()) {
			if (activeCondensedThread.getFitMultiplikator() == model
					.getActualFitMultiplikator_Condensed()) {
				if (activeCondensedThread.getFittedCondensedProbes() != null) {

//					System.out.println("-----------");
//					System.out.println("DATA CONDENSED SETTED");
//					System.out.println("-----------");

					model.setFittedCondensedProbes(activeCondensedThread
							.getFittedCondensedProbes());
					model.fittedProbesSetted_Condensed(true);
				}
			}
		}
	}

	public void runWholeThreads(ChromosomeDataSet actualChromeData) {

		WorkOnData work_1000 = new WorkOnData(1000, actualChromeData, 
				KindOfChromeView.WHOLE, false, this.model);
		double workId = work_1000.getWorkId();
		model.updateCache(workId, work_1000);
		
		WorkOnData work_2000 = new WorkOnData(2000, actualChromeData, 
				KindOfChromeView.WHOLE, false, this.model);
		workId = work_2000.getWorkId();
		model.updateCache(workId, work_2000);
		
		
		WorkOnData work_5000 = new WorkOnData(5000, actualChromeData, 
				KindOfChromeView.WHOLE, false, this.model);
		workId = work_5000.getWorkId();
		model.updateCache(workId, work_5000);
	}
	
	public void runCondensedThreads(ChromosomeDataSet actualChromeData) {

		WorkOnData work_1000 = new WorkOnData(1000, actualChromeData, 
				KindOfChromeView.CONDENSED, false, this.model);
		double workId = work_1000.getWorkId();
		model.updateCache(workId, work_1000);
		
		WorkOnData work_2000 = new WorkOnData(2000, actualChromeData, 
				KindOfChromeView.CONDENSED, false, this.model);
		workId = work_2000.getWorkId();
		model.updateCache(workId, work_2000);
		
		
		WorkOnData work_5000 = new WorkOnData(5000, actualChromeData, 
				KindOfChromeView.CONDENSED, false, this.model);
		workId = work_5000.getWorkId();
		model.updateCache(workId, work_5000);
	}
}
