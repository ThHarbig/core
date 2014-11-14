package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.TableSettings;

public class GetWholeChromePosition_Delegate {
	
	public GetWholeChromePosition_Delegate(){
		 
	}
	
	/**
	 * Return for cellnumber the position in Chromosome depending on ZoomLebel
	 * If Whole Chromosome View is used, cellnumber (multiplied with zoomlevel) is returned
	 * For condensed view this method is not applicable.
	 * @param cellnumber
	 * @param view
	 * @param zoomMultiplikator
	 * @param originalNumberOfCells
	 * @return position of cell in chromosome (for whole chrome view, else -1 returned)
	 */
	public int execFirstPos(int cellnumber, GenomeHeatMapTableModel model){
		
		
		KindOfChromeView view = model.getKindOfChromeView(); 
		int zoomMultiplikator = model.getZoomMultiplikator();
		TableSettings ts = model.getTableSettings();
		int originalNumberOfCells = ts.getOriginalNumberOfCells();
		if(originalNumberOfCells<cellnumber)return (int)model.getViewEnd();
		long skipValue = model.getSkipValue();
		
		int chromePosition = -1;

		if(view.equals(KindOfChromeView.WHOLE)){
			chromePosition = (cellnumber * zoomMultiplikator) - (zoomMultiplikator-1);
			// return negative chromePosition if chromePosition is higher than available cells
			if(chromePosition > originalNumberOfCells){
				return (int)model.getViewEnd();
			}
		}
		
		chromePosition = chromePosition + (int) skipValue;
		
		return chromePosition;
	}
	
	public int execLastPos(int cellnumber, GenomeHeatMapTableModel model){
		
		KindOfChromeView view = model.getKindOfChromeView(); 
		int zoomMultiplikator = model.getZoomMultiplikator();
		TableSettings ts = model.getTableSettings();
		int originalNumberOfCells = ts.getOriginalNumberOfCells();
		long skipValue = model.getSkipValue();
		
		int chromePosition = -1;

		if(view.equals(KindOfChromeView.WHOLE)){
			chromePosition = (cellnumber * zoomMultiplikator);
			// return negative chromePosition if chromePosition is higher than available cells
			if(chromePosition > originalNumberOfCells){
				return (int)model.getViewEnd();
			}
		}
		
		chromePosition = chromePosition + (int) skipValue;
		return chromePosition;
	}
}
