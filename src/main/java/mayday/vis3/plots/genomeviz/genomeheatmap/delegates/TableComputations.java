package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

import java.awt.Rectangle;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTable;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;

 
public class TableComputations {
	
	/**
	 * ComputeFirstVisibleRow_Delegate Computes depending on heatMapTable's visibleRect-size how many rows fit into actual window visibleRect.
	 * @param visRect
	 * @param box size y
	 * @return int: id of the first visible row (id is lower than actual first visible row, to buffer additional
	 * images)
	 */
	public static int getFirstVisibleRow(int visRectY, int boxSizeY){
		int numberOfRows = 0;

		numberOfRows = (int)Math.floor((double)visRectY /(double)boxSizeY); 
		// needed to buffer some previous images

		return numberOfRows;
	}
	
	/**
	 * 
	 * @param y
	 * @param boxSizeY
	 * @param rowCount
	 * @return
	 */
	public static int getLastVisibleRow(int y, int boxSizeY, int rowCount){
		
		int lastRow = 0;

		lastRow = (int)Math.ceil((double) y / (double)boxSizeY);

		return lastRow -1;
	}
	
	/**
	 * 
	 * @param windowFittedRowsOneStrand
	 * @param numberOfBoxesEachRow
	 * @param originalNumberOfCells
	 * @return
	 */
	public static int computeFitMultiplikator(int windowFittedRowsOneStrand, int numberOfBoxesEachRow, int originalNumberOfCells){
		int multiplikator = -1;

		// only recomputed if heightOfViewport or boxSizeY changed
		//master.computeWindowFittedRowsOneStrand();
		int fittedRows = windowFittedRowsOneStrand;

		double possibleCellsOneStrand = fittedRows * numberOfBoxesEachRow;

		multiplikator = (int) Math.ceil((double) originalNumberOfCells
				/ possibleCellsOneStrand);

		if (multiplikator < 1 )
			multiplikator = 1;
		
		return multiplikator;
	}
	
	/**
	 * Computes the necessary cellnumber depends on size of genome and zoomlevel.
	 * @param originalCellNumber
	 * @param zoomMultiplikator
	 * @return necessary cellnumber
	 */
	public static int computeNecessaryCells(int originalCellNumber, int zoomMultiplikator){
			int cellnumber = -1;
			cellnumber = (int)Math.ceil((double)originalCellNumber / (double)zoomMultiplikator);
			return cellnumber;
	}
	
	/**
	 * compute for given chromeposition depending on zoomlevel the cellnumber.
	 * Only usable for whole fit view.
	 * @param chromePosition
	 * @param multiplikator
	 * @param skipvalue
	 * @return
	 */
	public static int computeCellnumberOfChromePosition(int chromePosition, int multiplikator, int skipvalue){
		int cellnumber = 0;
		if(multiplikator != 0){
			double val = Math.floor((double)(chromePosition- 1 - skipvalue)/(double)multiplikator);
			cellnumber = (int)(val) +1;
		}
		return cellnumber;
	}
	
	/**
	 * 
	 * @param rowsForOneStrand
	 * @return
	 */
	public static int computeNecessaryRowsAllStrands(int rowsForOneStrand){
		int number = 0;
		// get number of rows for each strands
		double rowNumbers = rowsForOneStrand;

		number = new Double(rowNumbers).intValue();
		// compute number of rows for all strands (forward/backward/placeholder)
		number = (number * 3);

		// beacause counted from zero
		//number = number -1;
		
		if (number == 0) {
			System.err
					.println("ChromeHeatMapTableModel getColumnCount() - Number of rows not setted");
		}

		return number;
	}
	
	/**
	 * 
	 * @param numberOfCells
	 * @param boxesEachRow
	 * @return
	 */
	public static int computeNecessaryRowsOneStrand(int numberOfCells, int boxesEachRow){

		double rowNumbers = -1;

		// mod is zero
		if ((double)numberOfCells % (double)boxesEachRow == 0) {
			rowNumbers = ((double)numberOfCells / (double)boxesEachRow);
		}
		// mod not zero, add additional row
		else {
			rowNumbers = Math.floor(((double)numberOfCells / (double)boxesEachRow)) + 1.0;
		}
		return (int)rowNumbers;
	}
	
	/**
	 * computes depending on window size and box size how many boxes filled with data fit in a row (unused columns NOT included).
	 * @param widthOfViewport actual width of viewport
	 * @param boxSizeX	actual width of boxes in table
	 * @param number of front unused columns wich are needed to display (strand information)
	 * @param number of boxes each row which are filled with probe data
	 */
	public static int computeNumberOfBoxes(int widthOfViewport, int boxSizeX, int numberOfFrontUnusedColumns, int numberOfBoxesEachRow){
	
			double numberOfBoxes = numberOfBoxesEachRow;

			double actualWidth = (double)widthOfViewport;
			double actualBoxSizeX = (double)boxSizeX;

			if (widthOfViewport != 0) {
				if(actualWidth%actualBoxSizeX == 0){
					// compute number of boxes each row depending on boxSizeX
					// don't forget to subtract the unused columns
					numberOfBoxes = actualWidth/actualBoxSizeX;
					// subtract the first column: numberOfBoxes is WITHOUT unused column
					// -1 else it is to thin
					
					numberOfBoxes = numberOfBoxes - (double)numberOfFrontUnusedColumns - 1;

				} else {
					// compute number of boxes each row depending on boxSizeX
					double floor = (double)Math.floor(actualWidth/actualBoxSizeX);
					
					// don't forget to subtract the unused columns
					// number of back unused columns not important for computation
					numberOfBoxes = floor - (double)numberOfFrontUnusedColumns;
				}
				
				if(numberOfBoxes < 0){
					System.err.println("ChromeHeatMapTable - computeNumberOfBoxes: numberOfBoxes havent been computed");
				}

			}
		return (int)numberOfBoxes;
	}
	
	/**
	 * Compute number of PREDECESSOR rows of actual strand.
	 * @param row
	 * @param strand
	 * @return
	 */
	public static int computePredecessorsRowsOfStrand(int row, StrandInformation strand){
		// if we are in the forward strand
		switch (strand) {
		case MINUS:
			if (row != 0) {
				return row / 3;
			}
			return 0;
		case PLUS:
			if (row != 1) {
				int val = (row - 1) / 3;
				return val;
			}

			return 0;

		case PLACEHOLDER:
			if (row != 2) {
				int val = (row - 2) / 3;
				return val;
			}

			return 0;
		default:
			return 0;
		}
		
	}
	
	/**
	 * 
	 * @param model
	 * @param table
	 * @param zoomMultiplikator
	 * @return
	 */
	public static Integer computeWindowRangeForZoomLevel(GenomeHeatMapTableModel model, GenomeHeatMapTable table, int zoomMultiplikator){
		
		int boxSizeY = model.getTableSettings().getBoxSizeY();
		int numberofboxesEachRow = model.getTableSettings().getNumberOfBoxesEachRow();
		//int zoomMultiplikator = model.getZoomMultiplikator();
		int maxNumberOf_bp = model.getTableSettings().getOriginalNumberOfCells();
		Rectangle rect = table.getVisibleRect();

		double height = rect.getHeight(); 
		double boxesVertical = 0;
		double boxesHorizontal = 0;
		double numberOfBoxes = 0;
		double bp = 0;
		
		if (boxSizeY != 0){
			boxesHorizontal = numberofboxesEachRow;
			// :3 because of +,- and placeholder
			boxesVertical = Math.floor((height / (double)boxSizeY)/3.);	
		}
		
		numberOfBoxes = boxesVertical*boxesHorizontal;
	
		bp = numberOfBoxes*zoomMultiplikator;

		if(bp>maxNumberOf_bp){
			return maxNumberOf_bp;
		}
		return (int)bp;
	}
	
	public static int getZoomMultiplikator(ZoomLevel zoomlevel){
		Integer multiplikator = null;
		
		switch(zoomlevel){
		case one:
			
			 multiplikator = 1;
			 break;
		case two:
			
			multiplikator = 2;
			break;
		case five:
			
			multiplikator = 5;
			break;
		case ten:
			
			multiplikator = 10;
			break;
		case fifteen:
			
			multiplikator = 15;
			break;
		case twenty:
			
			multiplikator = 20;
			break;
		case twentyfive:
			
			multiplikator = 25;
			break;
		case fifty:
			multiplikator = 50;
			break;
		case hundred:
			multiplikator = 100;
			break;
		case twohundred:
			multiplikator = 200;
			break;
		case thousand:
			multiplikator = 1000;
			break;	
		case twothousand:
			multiplikator = 2000;
			break;	
		case fivethousand:
			multiplikator = 5000;
			break;
			
		default:
			System.err.println("ERROR: ZoomMultiplikator_Delegate -  No match with zoomlevel");
			multiplikator = 1;	
		}

		return multiplikator;
	}
	
	public static int computeFittedRowsOneStrand(int heightOfViewport, int boxSizeY){

		double numberOfRows = 0.0;
		double actualHeight = (double) heightOfViewport;
		double actualBoxSizeY = (double) boxSizeY;

		int windowFittedRowsOneStrand = 0;
		
		// this case happens at the beginning during the initialization
		if (actualHeight != 0.0){
			if (actualHeight % actualBoxSizeY == 0) {
				// compute number of boxes each row depending on boxSizeX
				// here the whole number of strands been computed, don't forget
				// to divide by 3 (+,-,placeholder)
				numberOfRows = actualHeight / actualBoxSizeY;

			} else {
				// compute number of boxes each row depending on boxSizeX
				double floor = (double) Math.floor(actualHeight
						/ actualBoxSizeY);

				// don't forget to subtract the unused columns
				numberOfRows = floor;
			}

			if (numberOfRows < 0) {
				System.err
						.println("FittedRowsOneStrandComputation_Delegate - execute: numberOfRows is set to default 1" +
								"(windowsize is too small)");
			}

			// set number of boxes; stored in tableManager
			windowFittedRowsOneStrand = new Double(numberOfRows / 3.0)
					.intValue();
		}

		// Check this, if Window is to small
		if(windowFittedRowsOneStrand >= 1) return windowFittedRowsOneStrand;
		return 1;
	}
}
