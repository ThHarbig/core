package mayday.vis3.plots.genomeviz.genomeheatmap.datathreads;

import java.util.LinkedList;
import java.util.List;

import mayday.core.Probe;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeDataSet;
 
public class FittedWholeThread extends Thread{

	protected GenomeHeatMapTableModel tableModel;
	protected ChromosomeDataSet actualData;
	protected boolean running = false;
	protected boolean finished = false;
	protected int fitMultiplikator = 0;
	protected int threadId = 0;
	
	protected long startPosition = 0;
	protected long endPosition = 0;
	
	protected MultiTreeMap<Long,Probe> fittedWholeProbes = null;	// contains for each original position List of probes (+,-)
	
	public FittedWholeThread(GenomeHeatMapTableModel tableModel,ChromosomeDataSet actualData, int fitMultiplikator){
		super("FittedWholeThread (multiplikator: " + fitMultiplikator +")");
		this.tableModel = tableModel;
		this.actualData = actualData;
		this.fitMultiplikator = fitMultiplikator;
		this.threadId = randomNumber(100); 
		
		startPosition = tableModel.getViewStart();
		endPosition = tableModel.getViewEnd();
//		System.out.println("WHOLE FITTED THREAD START " + startPosition + " END " + endPosition);
	}
	
	public int getFitMultiplikator(){
		return this.fitMultiplikator;
	}
	
	public MultiTreeMap<Long,Probe> getFittedWholeProbes(){
		if (this.fittedWholeProbes!=null) return this.fittedWholeProbes;
		else return null;
	}
	
	public static int randomNumber(int n) {
        
        // create random number between 0 and 1
        double dezimalZahl = Math.random();

        // strech value in range of 0 to n
        int ganzZahl = (int)Math.round( dezimalZahl * n );

        // return result
        return ganzZahl;     
        
    } 
	
	public void run() {
//		System.out
//				.println("----------------------------------------------------------------------------");
//		System.out.println("THREAD WHOLE " + this.threadId + " STARTING FOR MULTIPLIKATOR " + fitMultiplikator);
//		System.out
//				.println("----------------------------------------------------------------------------");

		fittedWholeProbes = new MultiTreeMap<Long,Probe>();
		
		List<Probe> actList ;
		List<Probe> emptyList = new LinkedList<Probe>();
		
		long zoomPos = 1;
		
		for (long key = startPosition; key <= endPosition; key+=fitMultiplikator) {

			if (running == false) {
				break;
			}
			
			long k2 = Math.min(key+fitMultiplikator-1, endPosition);

			List<LocusGeneticCoordinateObject<Probe>> lolgcp = actualData.getActualChrome().get(key, k2, Strand.UNSPECIFIED);			
			
			if (!lolgcp.isEmpty()) {
				actList = new LinkedList<Probe>();
				for (LocusGeneticCoordinateObject<Probe> olgcp : lolgcp) 
					actList.add(olgcp.getObject());	
			} else 
				actList = emptyList;
			
			fittedWholeProbes.put(zoomPos, actList);
			zoomPos++;

		}

		if (running == true) {
			

			tableModel.waitForWholeFit(false);
			this.finished = true;
			
//			System.out
//			.println("----------------------------------------------------------------------------");
//		System.out.println("THREAD WHOLE " + this.threadId + " COMPLETE FOR MULTIPLIKATOR " + fitMultiplikator);
//		System.out
//			.println("----------------------------------------------------------------------------");
		}

		setRunning(false);
	}
	
	public boolean isFinished(){
		return this.finished;
	}
	
	public void setRunning(boolean val){
		running = val;
//		System.out.println("Thread " + this.threadId + " told to stop");
	}
}
