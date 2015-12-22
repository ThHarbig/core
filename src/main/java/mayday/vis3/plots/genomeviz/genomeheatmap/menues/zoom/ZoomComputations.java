package mayday.vis3.plots.genomeviz.genomeheatmap.menues.zoom;

import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.TranslatedKey;
 
/*
 * translater needed to translate the id of the cell to positions in whole-chrome hashmap or condensed-chrome arraylist
 */
public class ZoomComputations {

	protected MasterManager master;
	
	public ZoomComputations(MasterManager master) {
		this.master = master;

	}
	
	/**
	 * computes for cell in table depending on zoom level, which original cell data is contained in this cell
	 * if numberOfOrigCell is too high, so highest existing cellnumber is returned.
	 * @param key
	 * @param level
	 * @return original cellnumber (or greatest cellnumber if original cellnumber is too high)
	 */
	public int getOnlyOneTranslatedKey(int key, int multiplikator) {
		
		//Integer multiplikator = master.getZoomMultiplikator();
		int numberOfOrigCell = (key - 1) * multiplikator + 1;
		
		return numberOfOrigCell;
	}
	
	public TranslatedKey getTranslatedKeyPair(int key, int originalNumberOfCells) {
		//int maxCellNumber = master.getOriginalNumberOfCells();
		
		Integer multiplikator = master.getZoomMultiplikator();
		int firstOrigCell = (key - 1) * multiplikator + 1;
		int lastOrigCell = ((key+1) - 1) * multiplikator + 1;
		
		TranslatedKey transKey = new TranslatedKey(originalNumberOfCells,firstOrigCell,lastOrigCell);
		if (transKey.getTranslatedKeyFirst() > originalNumberOfCells) return null;
		
		return transKey;
	}
}
