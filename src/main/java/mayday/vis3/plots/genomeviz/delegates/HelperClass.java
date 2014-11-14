package mayday.vis3.plots.genomeviz.delegates;

import mayday.vis3.plots.genomeviz.ILogixVizModel;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;

public abstract class HelperClass {
	
	/**
	 * returns the width for given indices.
	 * @param x_s
	 * @param x_e
	 * @return
	 */
	public static int getWidth(int x_s, int x_e){
		return (x_e-x_s)+1;
	}
	
	/**
	 * returns the start index of an area (default is zero).
	 * @return
	 */
	public static int getXStart(){
		return 0;
	}
	
	/**
	 * returns the last index of an area given the area-width.
	 * @param width
	 * @return
	 */
	public static int getXEnd(int width){
		return getLastXIndex(width);
	}
	/**
	 * returns last index of a panel and so on.
	 * @param width
	 * @return last index
	 */
	public static int getLastXIndex(int width){
		// -1 because pixel is counted from zero
		return width-1;
	}
	
	/**
	 * returns the reduced width if an area has margins.
	 * @param width
	 * @param left_margin
	 * @param right_margin
	 * @return
	 */
	public static int getWidthReduced(int width, int left_margin, int right_margin){
		return width-(left_margin+right_margin);
	}
	
	/**
	 * returns the index of x in area at which the painting begins.
	 * @param left_margin
	 * @return
	 */
	public static int getXReducedStart(int left_margin){
		return getXStart()+left_margin;
	}
	
	/**
	 * returns index of x in area at which the painting ends.
	 * @param width
	 * @param right_margin
	 * @return
	 */
	public static int getXReducedEnd(int width, int right_margin){
		return getXEnd(width)-right_margin;
	}
	
	/**
	 * used to translate an index of a bigger area into an index of a smaller area.
	 * @param width: of complete area
	 * @param left_margin
	 * @param right_margin
	 * @param posX: position to translate
	 * @return translated index; -1 if index is out of bound on the left and -2 if index is out of bound on the right
	 */
	public static int getTranslatedX(int width, int left_margin, int right_margin, int posX){
		int x_r_s= getXReducedStart(left_margin);
		int x_r_e = getXReducedEnd(width, right_margin);
		
		if(posX < x_r_s)return -1;
		else if (posX > x_r_e)return -2;
		else return posX-x_r_s;
	}
	
	/**
	 * computes range which is selected.
	 * @param from_arry_dbl
	 * @param to_arry_dbl
	 * @param first
	 * @param last
	 * @param model
	 */
	public static void getRangeOfClickedPosition(double[] result, 
			FromToPosition first, FromToPosition last, ILogixVizModel model) {
		double from_beg=first.getFrom();
		double from_end=first.getTo();
		double to_beg=last.getFrom();
		double to_end=last.getTo();

		if (from_beg == -1)
			from_beg = from_end = model.getViewStart();
		else if (from_beg == -2)
			from_beg = from_end = model.getViewEnd();

		if (to_beg == -1)
			to_beg = to_end = model.getViewStart();
		else if (to_beg == -2)
			to_beg = to_end = model.getViewEnd();
		
		result[0] = Math.min(from_beg,to_beg);
		result[1] = Math.max(from_end,to_end);
	}
}
