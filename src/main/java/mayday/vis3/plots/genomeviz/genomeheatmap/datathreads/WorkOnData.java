package mayday.vis3.plots.genomeviz.genomeheatmap.datathreads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import mayday.core.Probe;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.core.structures.natives.LinkedLongArray;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.ForwardBackwardProbes;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeDataSet;

public class WorkOnData {

	protected ChromosomeDataSet actualData;
	protected KindOfChromeView view = null;
	protected int multiplikator = 0;
	protected boolean fitted = false;
	protected GenomeHeatMapTableModel tableModel;
	protected double workId = -1;

	protected ArrayList<ForwardBackwardProbes> condensedProbes;
	protected MultiTreeMap<Long, Probe> fittedWholeProbes;
	protected TreeMap<Long, ForwardBackwardProbes> wholeProbes;
	protected ArrayList<List<Probe>> fittedCondensedProbes;

	public WorkOnData(int Multiplikator, ChromosomeDataSet ActualChromeData, 
			KindOfChromeView View, boolean Fitted, GenomeHeatMapTableModel TableModel){
		multiplikator = Multiplikator;
		actualData = ActualChromeData;
		view = View;
		fitted = Fitted;
		tableModel = TableModel;
		setWorkId();

		fittedCondensedProbes = null;
		wholeProbes = null;
		fittedWholeProbes = null;
		condensedProbes = null;
	}

	private void setWorkId(){
		if(!fitted){
			if(view.equals(KindOfChromeView.WHOLE)){
				workId = multiplikator;
			} else if(view.equals(KindOfChromeView.CONDENSED)){
				workId = multiplikator + 0.5;
			}
		} else{
			if(view.equals(KindOfChromeView.WHOLE)){
				workId = 10000;
			} else if(view.equals(KindOfChromeView.CONDENSED)){
				workId = 10000.5;
			}
		}
	}

	public double getWorkId(){
		return workId;
	}

	public boolean runOnData() {

		if (view.equals(KindOfChromeView.CONDENSED)) {	
			workOnCondensedData();
			return true;

		} else if (view.equals(KindOfChromeView.WHOLE)) {
			workOnWholeData();
			return true;
		} 
		return true;
	}



	private void workOnWholeData() {
		
		if (fitted) {
			long startPosition = tableModel.getViewStart();
			long endPosition = tableModel.getViewEnd();
			fittedWholeProbes = new MultiTreeMap<Long, Probe>();

			List<Probe> emptyList = new LinkedList<Probe>();
			List<Probe> actList;

			long zoomPos = 1;

			for (long key = startPosition; key <= endPosition; key+=multiplikator) {

				long k2 = Math.min(key+multiplikator-1, endPosition);

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


		} else {
			wholeProbes = new TreeMap<Long, ForwardBackwardProbes>();
			long startPosition = tableModel.getViewStart();
			long endPosition = tableModel.getViewEnd();

			ForwardBackwardProbes probesForwardBackward;
			//			ForwardBackwardProbes probesForwardBackward_empty = new ForwardBackwardProbes();

			long zoomPos = 1;

			for (long key = startPosition; key <= endPosition; key+=multiplikator) {

				long k2 = Math.min(key+multiplikator-1, endPosition);

				List<LocusGeneticCoordinateObject<Probe>> lolgcp = actualData.getActualChrome().get(key, k2, Strand.UNSPECIFIED);			

				probesForwardBackward = new ForwardBackwardProbes();
				if (!lolgcp.isEmpty())
					for (LocusGeneticCoordinateObject<Probe> olgcp : lolgcp)
						probesForwardBackward.add(olgcp.getObject(), olgcp.getStrand());
				probesForwardBackward.finalize();
				wholeProbes.put(zoomPos, probesForwardBackward);
				zoomPos++;

			}
		}
	}

	private void workOnCondensedData() {
		if(!fitted){
			condensedProbes = new ArrayList<ForwardBackwardProbes>();

			int counter = 0;

			ForwardBackwardProbes probesForBack = new ForwardBackwardProbes();
			int sizeOfData = actualData.getViewLength(KindOfChromeView.CONDENSED);
			for (int i = 0; i < sizeOfData; i++) {

				LinkedLongArray lla = actualData.getCondensed();
				List<LocusGeneticCoordinateObject<Probe>> lolgcp = actualData.getProbes(lla.get(i));

				if (counter == multiplikator) {
					if (probesForBack != null) {
						condensedProbes.add(probesForBack);
					}

					probesForBack = new ForwardBackwardProbes();
					counter = 0;
				}

				if (counter < multiplikator) {
					if (!lolgcp.isEmpty())
						for (LocusGeneticCoordinateObject<Probe> olgcp : lolgcp)
							probesForBack.add(olgcp.getObject(), olgcp.getStrand());
					counter++;
				}
			}

			if (probesForBack != null) {
				condensedProbes.add(probesForBack);
				probesForBack.finalize();
			}


		} else{

			fittedCondensedProbes = new ArrayList<List<Probe>>();
			int counter = 0;
			List<Probe> actList = new ArrayList<Probe>();
			long sizeOfData = actualData.getCondensedSize();
			for(int i = 0; i< sizeOfData; i++){

				LinkedLongArray lla = actualData.getCondensed();
				List<LocusGeneticCoordinateObject<Probe>> lolgcp = actualData.getProbes(lla.get(i));

				if (counter == multiplikator){
					if (actList != null) {
						fittedCondensedProbes.add(actList);
					}

					actList = new ArrayList<Probe>();
					counter = 0;
				}

				if (counter < multiplikator){
					if (!lolgcp.isEmpty())
						for (LocusGeneticCoordinateObject<Probe> olgcp : lolgcp) 
							actList.add(olgcp.getObject());
					counter++;
				}
			}

			if (actList != null) {
				fittedCondensedProbes.add(actList);
			}
		}
	}

	public void setData(){
		if (view.equals(KindOfChromeView.CONDENSED)) {
			if (fitted) {
				int actFitMulti = tableModel.getActualFitMultiplikator_Condensed();
				if (this.multiplikator == actFitMulti) {
					if (fittedCondensedProbes != null) {
						tableModel.waitForCondensedFit(false);
						tableModel.setFittedCondensedProbes(fittedCondensedProbes);
						tableModel.fittedProbesSetted_Condensed(true);
					}
				} else{
					tableModel.clearAllPreviousComputedFittedData();
				}
			}else{

				ZoomLevel zoom = null;

				if(multiplikator == 1000){
					zoom = ZoomLevel.thousand;
				} else if(multiplikator == 2000){
					zoom = ZoomLevel.twothousand;
				} else if(multiplikator == 5000){
					zoom = ZoomLevel.fivethousand;
				}
				actualData.setCondensed(condensedProbes, zoom);
			}
		} else if (view.equals(KindOfChromeView.WHOLE)) {
			if (fitted) {
				int actFitMulti = tableModel.getActualFitMultiplikator_Whole();
				if(this.multiplikator == actFitMulti){
					if (fittedWholeProbes != null) {
						tableModel.waitForWholeFit(false);
						tableModel.setFittedWholeProbes(fittedWholeProbes);
						tableModel.fittedProbesSetted_Whole(true);
					}
				} else{
					tableModel.clearAllPreviousComputedFittedData();
				}
			} else{
				ZoomLevel zoom = null;

				if(multiplikator == 1000){
					zoom = ZoomLevel.thousand;
				} else if(multiplikator == 2000){
					zoom = ZoomLevel.twothousand;
				} else if(multiplikator == 5000){
					zoom = ZoomLevel.fivethousand;
				}
				actualData.setWhole(wholeProbes, zoom);
			}
		}
	}
}
