package mayday.vis3.plots.genomeviz.genomeheatmap;

import java.awt.Rectangle;

import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableComputations;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.SetFirstAndLastVisiblePositions;

public class TableSettings extends Settings{

	public TableSettings(GenomeHeatMapTableModel Model) {
		super(new HierarchicalSetting("Table settings"), null);

		model = Model;
		frontunusedCols = model.getNumberOfFrontUnusedColumns();
	}

	protected GenomeHeatMapTableModel model;
	protected int firstVisRow = -1;
	protected int lastVisRow = -1;
	// initializes the box size
	protected int boxSizeX = 15;
	protected int boxSizeY = 15;
	
	protected int vp_height = 0;
	protected int vp_width = 0;
	protected int vp_width_prev = 0;		// store previous width of viewport
	public boolean widthOrBoxSizeChanged = false;
	
	protected int boxSizeX_prev = 15;			// store previous width of boxes
	protected int boxSizeY_prev = 15;
	protected boolean boxSizeXchanged = false;
	protected boolean boxSizeYchanged = false;
	
	protected int numberOfColumns = 0;				// columns of probes AND unused columns so actual number of columns in table
	protected int numberOfRows = 0;
	
	protected int necessaryRowsOneStrand = 0;
	protected int necessaryRowsAllStrands = 0;
	
	protected int numberOfBoxesEachRow = 36;		// number of boxes containing probe data (unused columns NOT included)
	protected boolean boxnumberEachRowChanged = false;
	
	protected int numberOfNecessaryCells = -1;
	public boolean cellNumberChanged = false;
	
	protected int originalNumberOfCells = -1;
	protected boolean originalNumberOfCellsChanged = false;
	
	
	
	protected int firstVisibleRow = -1;
	protected int lastVisibleRow = -1;
	protected int visibleRectY_bottom = 0;

	protected int firstVisiblePosition = -1;
	protected int lastVisiblePosition = -1;
	public boolean visibleRectChanged = false;
	public boolean firstVisibleRowsChanged = false;
	public boolean lastVisibleRowsChanged = false;
	
	public int frontunusedCols = 0;
	
	public void computeNumberOfBoxesEachRow(){
		setWidth_vp();
		if(vp_width_prev != vp_width || boxSizeX_prev != boxSizeX){
			widthOrBoxSizeChanged = true;
			setBoxSizeX_prev(boxSizeX);
			setVp_width_prev(vp_width);

			numberOfBoxesEachRow = TableComputations.computeNumberOfBoxes(vp_width,boxSizeX, frontunusedCols, numberOfBoxesEachRow);
			boxnumberEachRowChanged = true;
		} else {
			widthOrBoxSizeChanged = false;
			boxnumberEachRowChanged = false;
		}
	}
	
	private void setWidth_vp() {
		if(model.getScrollPane()!=null){
			vp_height = model.getScrollPane().getViewport().getHeight();
			vp_width = model.getScrollPane().getViewport().getWidth();
		}
	}

	public int getHeight_vp() {
		return vp_height;
	}

	public int getWidth_vp() {
		return vp_width;
	}

	public int getBoxSizeX() {
		return boxSizeX;
	}

	public void setBoxSizeX(int val) {
		if(val != boxSizeX){
			boxSizeXchanged = true;
			boxSizeX = val;
		} else {
			boxSizeXchanged = false;
		}
	}

	public int getBoxSizeY() {
		return boxSizeY;
	}

	public void setBoxSizeY(int val) {
		if(val != boxSizeY){
			boxSizeYchanged = true;
			boxSizeY = val;
			
		} else{
			boxSizeYchanged = false;
		}
	}

	public int getVp_width_prev() {
		return vp_width_prev;
	}

	public void setVp_width_prev(int vp_width_prev) {
		this.vp_width_prev = vp_width_prev;
	}

	public int getBoxSizeX_prev() {
		return boxSizeX_prev;
	}

	public void setBoxSizeX_prev(int boxSizeX_prev) {
		this.boxSizeX_prev = boxSizeX_prev;
	}

	public int getBoxSizeY_prev() {
		return boxSizeY_prev;
	}

	public void setBoxSizeY_prev(int boxSizeY_prev) {
		this.boxSizeY_prev = boxSizeY_prev;
	}

	public void setNumberOfRowsOneStrand() {
		necessaryRowsOneStrand = TableComputations.computeNecessaryRowsOneStrand(numberOfNecessaryCells,numberOfBoxesEachRow);
	}

	public void setNumberOfRowsAllStrands(int rowsOneStrand) {
		necessaryRowsAllStrands = TableComputations.computeNecessaryRowsAllStrands(model.getNumberOfRowsOneStrand());
	}
	protected int getNecessaryRowsAllStrands() {
		return necessaryRowsAllStrands;
	}
	public void setNecessaryCellnumber() {				
				
		int val = TableComputations.computeNecessaryCells(originalNumberOfCells,model.getZoomMultiplikator());
				
		if(numberOfNecessaryCells != val){
			numberOfNecessaryCells = val;
			cellNumberChanged = true;
		} else {
			cellNumberChanged = false;
		}
	}

	public void setOriginalNumberOfCells() {
		int val = model.getChromosomeSize();
		
		if(this.originalNumberOfCells != val){
			this.originalNumberOfCells = val;
			this.originalNumberOfCellsChanged = true;
		} else {
			this.originalNumberOfCellsChanged = false;
		}
	}

	private void setFirstVisibleRow(int val) {
		if(this.firstVisibleRow != val){
			this.firstVisibleRowsChanged = true;
			this.firstVisibleRow = val;
		} else {
			this.firstVisibleRowsChanged = false;
		}
	}

	private void setLastVisibleRow(int val) {
		if(this.lastVisibleRow != val){
			this.lastVisibleRow = val;
			this.lastVisibleRowsChanged = true;
		} else {
			this.lastVisibleRowsChanged = false;
		}
	}

	public void setVisibleRows(GenomeHeatMapTable genomeHeatMapTable) {
		Rectangle rect = genomeHeatMapTable.getVisibleRect();
		int visRectTop = rect.y;
		int visRectBottom = rect.y + rect.height;
		int boxSizeY = getBoxSizeY();
		int numberOfRows = model.getRowCount();
		
		int firstVisibleRow = TableComputations.getFirstVisibleRow(visRectTop, boxSizeY);

		// create some images before as buffer and check that value is not
		// too small
		firstVisibleRow = firstVisibleRow - Const.IMAGE_ROW_BUFFER;
		if (firstVisibleRow < 0)
			firstVisibleRow = 0;

		int lastVisibleRow = TableComputations.getLastVisibleRow(
				visRectBottom, boxSizeY, numberOfRows);
		// needed to buffer some following images
		lastVisibleRow = lastVisibleRow + Const.IMAGE_ROW_BUFFER;
		// -1 to change from numberOfRows to row-index
		if (lastVisibleRow > numberOfRows)
			lastVisibleRow = numberOfRows - 1;
		else
			lastVisibleRow = lastVisibleRow - 1;

		setFirstVisibleRow(firstVisibleRow);
		setLastVisibleRow(lastVisibleRow);
	}

	public void setVisiblePositions() {
		SetFirstAndLastVisiblePositions.setPositions(model, model.master.getTable());
	}

	public void setFirstAndLastVisiblePositions(int chromePositionFirst,
			int chromePositionLast) {
			firstVisiblePosition = chromePositionFirst;
			lastVisiblePosition = chromePositionLast;
	}


	public int getOriginalNumberOfCells() {
		return originalNumberOfCells;
	}


	public int getNumberOfBoxesEachRow() {
		return numberOfBoxesEachRow;
	}


	public int getFirstVisibleRow() {
		return firstVisibleRow;
	}


	public int getLastVisibleRow() {
		return lastVisibleRow;
	}


	public int getNumberOfNecessaryCells() {
		return numberOfNecessaryCells;
	}

	public void setBoxSizeXY(int boxSizeX2, int boxSizeY2) {
		setBoxSizeX(boxSizeX2);
		setBoxSizeY(boxSizeY2);
	}
}
