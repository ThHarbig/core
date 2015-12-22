package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

import java.awt.Rectangle;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTable;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;


public class SetFirstAndLastVisiblePositions {

	public static void setPositions(GenomeHeatMapTableModel tableModel, GenomeHeatMapTable table){
		
		Rectangle rect = table.getVisibleRect();
		int visRectTop = rect.y;
		int visRectBottom = rect.y + rect.height;
		int boxSizeY = tableModel.getTableSettings().getBoxSizeY();
		int numberOfRows = tableModel.getRowCount();
		int columnCount = tableModel.getColumnCount();
		
		
		int firstVisibleRow = TableComputations.getFirstVisibleRow(visRectTop, boxSizeY);
		
		int lastVisibleRow = TableComputations.getLastVisibleRow(visRectBottom, boxSizeY, numberOfRows);
		// needed to buffer some following images
		if(lastVisibleRow > numberOfRows) lastVisibleRow = numberOfRows - 1;
		else lastVisibleRow = lastVisibleRow - 1;

		StrandInformation strand_first = TableMapper.getStrand(firstVisibleRow, numberOfRows);
		
		StrandInformation strand_last = TableMapper.getStrand(lastVisibleRow, numberOfRows);
		
		int predRows_first = TableComputations.computePredecessorsRowsOfStrand(firstVisibleRow, strand_first);
		int predRows_last = TableComputations.computePredecessorsRowsOfStrand(lastVisibleRow, strand_last);
		
		
		
		int chromePositionFirst = Integer.MAX_VALUE;
		int chromePositionLast = 0;
		if(tableModel.getKindOfChromeView().equals(KindOfChromeView.WHOLE)){
			int firstCell = TableMapper.getCellNumber(firstVisibleRow, 1, tableModel, predRows_first);
			int lastCell = TableMapper.getCellNumber(lastVisibleRow, columnCount-1, tableModel, predRows_last);
			chromePositionFirst = new GetWholeChromePosition_Delegate().execFirstPos(firstCell, tableModel);
			chromePositionLast = new GetWholeChromePosition_Delegate().execFirstPos(lastCell, tableModel);
		} else {
			
			
			chromePositionFirst = searchForFirstPosition(tableModel,
					columnCount, firstVisibleRow, strand_first,
					chromePositionFirst);
			
			chromePositionLast = searchForLastPosition(tableModel, columnCount,
					lastVisibleRow, strand_last, chromePositionLast);
			
		}
		
		
		
		if(chromePositionLast < 0){
			chromePositionLast = (int) tableModel.getViewEnd();
		}
		
		tableModel.setFirstAndLastVisiblePositions(chromePositionFirst, chromePositionLast);
	}

	private static int searchForLastPosition(GenomeHeatMapTableModel tableModel,
			int columnCount, int lastVisibleRow, StrandInformation strand_last,
			int chromePositionLast) {
		CellObject cell_last_plus;
		CellObject cell_last_minus;
		if(strand_last.equals(StrandInformation.PLACEHOLDER)){
			for(int i = columnCount-1; i >= 1; i--){
				cell_last_plus = tableModel.getValueAt(lastVisibleRow-1, i);
				cell_last_minus = tableModel.getValueAt(lastVisibleRow-2, i);
				if(cell_last_plus.getProbes() != null && !cell_last_plus.getProbes().isEmpty()){
					for(Probe pb : cell_last_plus.getProbes()){
						if(chromePositionLast < tableModel.getEndPosition(pb)){
							chromePositionLast = (int)tableModel.getEndPosition(pb);
						}
					}
					break;
				}
				
				if(cell_last_minus.getProbes() != null && !cell_last_minus.getProbes().isEmpty()){
					for(Probe pb : cell_last_minus.getProbes()){
						if(chromePositionLast < tableModel.getEndPosition(pb)){
							chromePositionLast = (int)tableModel.getEndPosition(pb);
						}
					}
					break;
				}
			}
		} else if(strand_last.equals(StrandInformation.PLUS)){
			for(int i = columnCount-1; i >= 1; i--){
				cell_last_plus = tableModel.getValueAt(lastVisibleRow, i);
				cell_last_minus = tableModel.getValueAt(lastVisibleRow-1, i);
				if(cell_last_plus.getProbes() != null && !cell_last_plus.getProbes().isEmpty()){
					for(Probe pb : cell_last_plus.getProbes()){
						if(chromePositionLast < tableModel.getEndPosition(pb)){
							chromePositionLast = (int)tableModel.getEndPosition(pb);
						}
					}
					break;
				}
				
				if(cell_last_minus.getProbes() != null && !cell_last_minus.getProbes().isEmpty()){
					for(Probe pb : cell_last_minus.getProbes()){
						if(chromePositionLast < tableModel.getEndPosition(pb)){
							chromePositionLast = (int)tableModel.getEndPosition(pb);
						}
					}
					break;
				}
			}
		} else if(strand_last.equals(StrandInformation.MINUS)){
			for(int i = columnCount-1; i >= 1; i--){
				cell_last_plus = tableModel.getValueAt(lastVisibleRow+1, i);
				cell_last_minus = tableModel.getValueAt(lastVisibleRow, i);
				if(cell_last_plus.getProbes() != null && !cell_last_plus.getProbes().isEmpty()){
					for(Probe pb : cell_last_plus.getProbes()){
						if(chromePositionLast < tableModel.getEndPosition(pb)){
							chromePositionLast = (int)tableModel.getEndPosition(pb);
						}
					}
					break;
				}
				
				if(cell_last_minus.getProbes() != null && !cell_last_minus.getProbes().isEmpty()){
					for(Probe pb : cell_last_minus.getProbes()){
						if(chromePositionLast < tableModel.getEndPosition(pb)){
							chromePositionLast = (int)tableModel.getEndPosition(pb);
						}
					}
					break;
				}
			}
		}
		return chromePositionLast;
	}

	private static int searchForFirstPosition(GenomeHeatMapTableModel tableModel,
			int columnCount, int firstVisibleRow,
			StrandInformation strand_first, int chromePositionFirst) {
		CellObject cell_first_plus;
		CellObject cell_first_minus;
		for(int i = 1; i < columnCount-1; i++){
			if(strand_first.equals(StrandInformation.PLACEHOLDER)){
				cell_first_plus = tableModel.getValueAt(firstVisibleRow-1, i);
				cell_first_minus = tableModel.getValueAt(firstVisibleRow-2, i);
				if(cell_first_plus.getProbes() != null && !cell_first_plus.getProbes().isEmpty()){
					for(Probe pb : cell_first_plus.getProbes()){
						if(chromePositionFirst > tableModel.getStartPosition(pb)){
							chromePositionFirst = (int)tableModel.getStartPosition(pb);
						}
					}
					break;
				}
				if(cell_first_minus.getProbes() != null && !cell_first_minus.getProbes().isEmpty()){
					for(Probe pb : cell_first_minus.getProbes()){
						if(chromePositionFirst > tableModel.getStartPosition(pb)){
							chromePositionFirst = (int)tableModel.getStartPosition(pb);
						}
					}
					break;
				}
			} else if(strand_first.equals(StrandInformation.PLUS)){
				cell_first_plus = tableModel.getValueAt(firstVisibleRow, i);
				cell_first_minus = tableModel.getValueAt(firstVisibleRow-1, i);
				if(cell_first_plus.getProbes() != null && !cell_first_plus.getProbes().isEmpty()){
					for(Probe pb : cell_first_plus.getProbes()){
						if(chromePositionFirst > tableModel.getStartPosition(pb)){
							chromePositionFirst = (int)tableModel.getStartPosition(pb);
						}
					}
					break;
				}
				if(cell_first_minus.getProbes() != null && !cell_first_minus.getProbes().isEmpty()){
					for(Probe pb : cell_first_minus.getProbes()){
						if(chromePositionFirst > tableModel.getStartPosition(pb)){
							chromePositionFirst = (int)tableModel.getStartPosition(pb);
						}
					}
					break;
				}
			} else if(strand_first.equals(StrandInformation.MINUS)){
				cell_first_plus = tableModel.getValueAt(firstVisibleRow+1, i);
				cell_first_minus = tableModel.getValueAt(firstVisibleRow, i);
				
				if(cell_first_plus.getProbes() != null && !cell_first_plus.getProbes().isEmpty()){
					for(Probe pb : cell_first_plus.getProbes()){
						if(chromePositionFirst > tableModel.getStartPosition(pb)){
							chromePositionFirst = (int)tableModel.getStartPosition(pb);
						}
					}
					break;
				}
				if(cell_first_minus.getProbes() != null && !cell_first_minus.getProbes().isEmpty()){
					for(Probe pb : cell_first_minus.getProbes()){
						if(chromePositionFirst > tableModel.getStartPosition(pb)){
							chromePositionFirst = (int)tableModel.getStartPosition(pb);
						}
					}
					break;
				}
			}
		}
		return chromePositionFirst;
	}
}
