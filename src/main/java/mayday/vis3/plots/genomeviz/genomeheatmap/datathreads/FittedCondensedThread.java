package mayday.vis3.plots.genomeviz.genomeheatmap.datathreads;

import java.util.ArrayList;
import java.util.List;

import mayday.core.Probe;
import mayday.core.structures.natives.LinkedLongArray;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeDataSet;
 
public class FittedCondensedThread extends Thread{
	
	protected GenomeHeatMapTableModel tableModel;
	protected ChromosomeDataSet actualData;
	protected boolean running = false;
	protected boolean finished = false;
	protected int fitMultiplikator = 0;
	protected int threadId = 0;
	
	
	protected ArrayList<List<Probe>> fittedCondensedProbes = null;
	
	public FittedCondensedThread(GenomeHeatMapTableModel tableModel,ChromosomeDataSet actualData, int fitMultiplikator){
		super("FittedCondensedThread (multiplikator: " + fitMultiplikator +")");
		this.tableModel = tableModel;
		this.actualData = actualData;
		this.fitMultiplikator = fitMultiplikator;
		this.threadId = randomNumber(100); 
	}

	public int getFitMultiplikator(){
		return this.fitMultiplikator;
	}
	
	public ArrayList<List<Probe>> getFittedCondensedProbes(){
		if (this.fittedCondensedProbes!=null) return this.fittedCondensedProbes;
		else return null;
	}
	
	  public static int randomNumber(int n) {
	        
	        // create random number between 0 and 1
	        double dezimalZahl = Math.random();

	        // stretch value in range of 0 to n
	        int ganzZahl = (int)Math.round( dezimalZahl * n );

	        // return result
	        return ganzZahl;     
	        
	    } 
	  
	public void run() {
		
		fittedCondensedProbes = new ArrayList<List<Probe>>();
		int counter = 0;
		List<Probe> actList = new ArrayList<Probe>();
		long sizeOfData = actualData.getCondensedSize();
		LinkedLongArray lla = actualData.getCondensed();

		for(int i = 0; i< sizeOfData; i++){
			
			List<LocusGeneticCoordinateObject<Probe>> lolgcp = actualData.getProbes(lla.get(i));
			
			if (running == false) {
				break;
			}
			
			if (counter == fitMultiplikator){
				fittedCondensedProbes.add(actList);
				actList = new ArrayList<Probe>();
				counter = 0;
			}
			
			if (counter < fitMultiplikator){
				if (!lolgcp.isEmpty())
					for (LocusGeneticCoordinateObject<Probe> olgcp : lolgcp)
						actList.add(olgcp.getObject());
				counter++;
			}
		}

		if (running == true) {
			fittedCondensedProbes.add(actList);

			tableModel.waitForCondensedFit(false);
			this.finished = true;
			
//			System.out
//			.println("----------------------------------------------------------------------------");
//		System.out.println("THREAD CONDENSED " + this.threadId + " COMPLETE FOR MULTIPLIKATOR " + fitMultiplikator);
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
