package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;

public class TableMapper {
	
	/**
	 * return for row and column the actual cellnumber, lowest value is 1.
	 * @param row
	 * @param col
	 * @return cellnumber of row,col, lowest cellnumber is 1
	 */
	public static int getCellNumber(int row, int col, GenomeHeatMapTableModel model, int predecessorRowsOfStrand){
		
		int numberOfColumns = model.getColumnCount(); 
		int numberOfUnusedColumns = model.getNumberOfUnusedColumns();
		
		int cellNumber = -1;

		if(row > 1){

			//numberOfColumns = col of probes AND unsused columns
			int numberProbeCellsEachRow = numberOfColumns - numberOfUnusedColumns;
			// indexOfPreRows: number of cells of the predecessor rows
			int indexOfPreRows = numberProbeCellsEachRow * predecessorRowsOfStrand; 
			
			// add col to add cells from last row
			cellNumber = indexOfPreRows + col;
		} else {
			//probePosition = col - numberOfUnusedColumns;
			cellNumber = col;
		}

	return cellNumber;
	}
	
	/**
	 * 
	 * @param identifier
	 * @param model
	 * @return
	 */
	public static int getCellnumberOfProbe(String identifier,GenomeHeatMapTableModel model){
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
								return probesInf.getCellnumber();
							}
						}
					}
				}
			}
		}
		return 0;
	}
	
	/**
	 * /**
	 * return if its forward,backward or placeholder.
	 * @param row
	 * @param numberOfRows
	 * @return strand information (+,- or placeholder)
	 */
	public static StrandInformation getStrand(int row, int numberOfRows){
		if(numberOfRows-1 == row){
			return StrandInformation.BORDER;
		}
		
		switch(row%3){
		case 0:
			return StrandInformation.MINUS;
		case 1:
			return StrandInformation.PLUS;
		default:
			return StrandInformation.PLACEHOLDER;
		}
	}
}
