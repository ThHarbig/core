package mayday.vis3.plots.genomeviz.genomeheatmap;

import java.util.TreeSet;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.ChromosomeMarker;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.Spaces;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableComputations;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableMapper;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.ScalaBufferedImage;

public class ScaleImageModel {

	protected Spaces spaces = new Spaces(); 
	private int marker_diff = 0;		// space between markers
	protected GenomeHeatMapTableModel model;
	protected int rowWidth = 0;
	protected int rowHeight = 0;
	protected TreeSet<Double> tickmarks = null;
	protected double v_big;
	protected double v_small;
	
	public ScaleImageModel(GenomeHeatMapTableModel Model){
		model = Model;
		tickmarks = new TreeSet<Double>();
	}
	
	public void updateScalaTicks(){
		int boxSizeX = model.getTableSettings().getBoxSizeX();
		marker_diff = boxSizeX * 20;
		spaces = new Spaces(); 
		rowWidth = boxSizeX*(model.getTableSettings().getNumberOfNecessaryCells()) ;
		rowHeight = model.getTableSettings().getBoxSizeY();
		spaces.setUserSpace(rowWidth);
        spaces.setValueSpace(model.getViewEnd()-model.getViewStart());
        tickmarks = ChromosomeMarker.tickmarks(model.getViewStart(), 
        		model.getViewEnd(),  
                Math.max((rowWidth)/marker_diff,2), false);
        computeTicks();
	}
	
	private void computeTicks() {
		if(tickmarks.size()>1){
			double val1 = tickmarks.first();
			double val2 = tickmarks.higher(val1);
			v_big = (int)Math.abs(val2-val1);
			v_small = Math.round(v_big/2.);
		}
	}
	
	public ScalaBufferedImage paintScalaImage(int row, GenomeHeatMapTableModel model, StrandInformation strand){
		// subtract last unused row and subtract -1 because its counted
		// from zero
		int lastRowOfTable = model.getRowCount()- model.getNumberOfBackUnusedRows() - 1;
		int predecessorRowsOfStrand = TableComputations.computePredecessorsRowsOfStrand(row, strand);
		
		int firstColumn = 0 + model.getNumberOfFrontUnusedColumns();
		int firstCell = TableMapper.getCellNumber(row, firstColumn, model, predecessorRowsOfStrand);
		
		int lastColumn = model.getColumnCount()- model.getNumberOfUnusedColumns();
		int lastCell = TableMapper.getCellNumber(row, lastColumn, model, predecessorRowsOfStrand);
		
		return new ScalaBufferedImage(model,firstCell, lastCell, row, lastRowOfTable,this, tickmarks);
	}

	public double getBig() {
		return this.v_big;
	}

	public double getSmall() {
		return this.v_small;
	}
}
