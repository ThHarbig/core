package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTable;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;

public class SearchOperations {
	
	/**
	 * searches probe and scrolls the searched probe in the center of view.
	 * @param identifier
	 * @param table
	 * @param model
	 * @param c
	 * @return
	 */
	public static boolean searchProbe(String identifier, GenomeHeatMapTable table, GenomeHeatMapTableModel model, Controller c){
		int row = 0;
		int column = 0;

		int frontUnusedRows = model.getNumberOfFrontUnusedRows();
		int backUnusedRows = model.getNumberOfBackUnusedRows();
		int frontUnusedColumns = model.getNumberOfFrontUnusedColumns();
		int backUnusedColumns = model.getNumberOfBackUnusedColumns();
		int necessaryCells = model.getTableSettings().getNumberOfNecessaryCells();
		
		int rowCount = model.getRowCount();
		int columnCount = model.getColumnCount();
		
		
		for (row = frontUnusedRows; 
		row < rowCount-backUnusedRows; row++) {
			for (column = frontUnusedColumns; 
			column < columnCount-backUnusedColumns; column++) {
				CellObject probesInf = (CellObject)model.getValueAt(row, column);
				
				if (probesInf != null && probesInf.getProbes() != null) {
					for (Probe probe : probesInf.getProbes()) {
						if (probe.getName().equals(identifier)) {
							if (probesInf.getCellnumber() < necessaryCells) {
								
								VisRangeMovements.centerView(table.getCellRect((int)row,(int)column, true), table);

								c.probeSearched(true, probe, row, column);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * searches cellnumber ands scrolss this position in the middle of view.
	 * @param cellnumber
	 * @param model
	 * @param table
	 */
	public static void searchCellnumber(int cellnumber, GenomeHeatMapTableModel model, GenomeHeatMapTable table){
		double row = 0;
		double column = 0;
		int numberBoxesEachRow = model.getTableSettings().getNumberOfBoxesEachRow();
		
		if(cellnumber <= numberBoxesEachRow){
			row = 0;
			column = cellnumber;
		} else{
			double preTrippleRows = Math.floor((double) cellnumber/(double)numberBoxesEachRow);
			double predecessorTrippleCells = (preTrippleRows*numberBoxesEachRow);
			column = cellnumber - predecessorTrippleCells;
			row = preTrippleRows*3;
		}
		
		if(row < model.getRowCount()-model.getNumberOfBackUnusedRows() && column < model.getColumnCount()-model.getNumberOfBackUnusedColumns()){
			
			VisRangeMovements.centerView(table.getCellRect((int)row,(int)column, true), table);
		}
	}
}
