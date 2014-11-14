package mayday.vis3.plots.genomeviz.genomeoverview.delegates;

import mayday.vis3.plots.genomeviz.delegates.HelperClass;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;

public abstract class DataMapper {

	/**
	 * For input x position the selected position in chromosome is computed, if the clicked position is out of range
	 * -1 for left border oder -2 for right border is returned.
	 * @param width
	 * @param lMargin
	 * @param rMargin
	 * @param xpos
	 * @param ftp
	 * @param model
	 * 
	 */
	public static void getSelectedPosition(int width, int lMargin, int rMargin, int xpos, FromToPosition ftp, GenomeOverviewModel model){

		int width_reduced = HelperClass.getWidthReduced(width, lMargin, rMargin);
		int x_r_s = HelperClass.getXReducedStart(lMargin);
		int x_r_e = HelperClass.getXReducedEnd(width, rMargin);
		
		if(xpos >= x_r_s && xpos <= x_r_e){
			double xposReduced = HelperClass.getTranslatedX(width, lMargin, rMargin, xpos);
			if(xposReduced>=0) getBpOfChromosome(width_reduced,model,xposReduced,ftp);
		} else if(xpos<x_r_s){
			ftp.clear(-1);
		} else if(xpos>x_r_e){
			ftp.clear(-2);
		}
	}
	
	public static int getBorder(int width, int lMargin, int rMargin, int xpos, GenomeOverviewModel model){

//		int width_reduced = HelperClass.getWidthReduced(width, lMargin, rMargin);
		int x_r_s = HelperClass.getXReducedStart(lMargin);
		int x_r_e = HelperClass.getXReducedEnd(width, rMargin);
		
		if( xpos > x_r_e){
			return (int)model.getChromosomeEnd();
		} else if(xpos<x_r_s){
			return (int)model.getChromosomeStart();
		}
		return 0;
	}
	
	/**
	 * mapping of sVal (source Value) to tVal (target Value).
	 * @param sMin
	 * @param sMax
	 * @param sVal
	 * @param tMin
	 * @param tMax
	 * @return
	 */
	public static double mapValue(double sMin, double sMax, double sVal, double tMin, double tMax) {
		double tVal = Math.round((sVal-sMin)/(sMax-sMin)*(tMax-tMin) + tMin);
		return tVal;			 
	}
	
	/*
	 * 	                        ------------- | VIEW-AREA | ------------- 
	 * 
	 * (start_view)              (pos_view)                                       (end_view) 
	 * 		 |                       |                                                |
	 * 		 --------------------------------------------------------------------------
	 *       ------------------------------------------------------------------------------------------- 
	 *       |          |                                                                              |
	 * (start_data) (pos_data)                                                                     (end_data)
	 * 
	 * ------------- | DATA-AREA | -------------
	 */

	/**
	 * computes base-pair position(s) for x-position in view, remember to decide
	 * if StartData and EndData equals the chromosome- or viewing-range.
	 * 
	 * @param widthViewArea:
	 *            area from zero to x which contains some data
	 * @param chromeModel
	 * @param pos_view
	 * @param ftp
	 * @param StartData:
	 *            beginning of bp-range
	 * @param EndData:
	 *            end of bp-range
	 */
	public static void getBpOfView(int widthViewArea,
			GenomeOverviewModel chromeModel, double pos_view, FromToPosition ftp) {
		ftp.clear();
		if (pos_view >= 0.) {

			// in bases
			double start_data = chromeModel.getViewStart();
			double end_data = chromeModel.getViewEnd();
			double width_data = end_data - start_data;

			// in pixels
			double start_view = 0.;
			double end_view = widthViewArea - 1; // -1 because pixel is counted from zero
			double width_view = end_view - start_view;

			// factor
			double pixPerBase = end_data!=start_data 
					? width_view / width_data
					: 1;

			// if a base has multiple pixels, shift it's area to the _left_ by half that amount, i.e. center it around its screen coordinate
			if (pixPerBase>1)
				pos_view-=(pixPerBase/2);
			
			double relative_from =  (pos_view - start_view) / width_view;			
			double from_view = (relative_from * width_data) + start_data;
			
			double relative_to = (pos_view+1 - start_view) / width_view;			
			double to_view = (relative_to * width_data) + start_data;

			// there are no fractional bases			
			long from = (long) Math.floor(from_view);
			long to = (long) Math.floor(to_view) - 1;

			// ensure correct interval
			from = from>0 ? from : 1;
			from = from<=end_data ? from : (long)end_data;
			to = to>0 ? to : 1;
			to = to<=end_data ? to : (long)end_data;

			// finally, ensure that to>=from
			to = to>=from ? to : from;

			ftp.setPositions((int) from, (int) to);
		}
	}

	public static void getBpOfChromosome(int width,
			GenomeOverviewModel model, double pos_view, FromToPosition ftp) {
		ftp.clear();
		if (pos_view >= 0.) {
			double start_data = model.getChromosomeStart();
			double end_data = model.getChromosomeEnd();

			double start_view = 0.;
			double end_view = HelperClass.getLastXIndex(width);

			double from_view = (pos_view - start_view)
					/ (end_view - start_view) * (end_data - start_data)
					+ start_data;

			pos_view = pos_view + 1;
			double to_view = (pos_view - start_view) / (end_view - start_view)
					* (end_data - start_data) + start_data;

			long from = (long) Math.floor(from_view);
			long to = (long) Math.floor(to_view) - 1;

			if (from == 0)
				from = 1;
			if (from > end_data) {
				from = (long) end_data;
			}

			if (to == 0)
				to = 1;
			if (to > end_data) {
				to = (long) end_data;
			}

			if (to < from) {
				long val = to;
				to = from;
				from = val;
			}

			ftp.setPositions((int) from, (int) to);
		}
	}
	
	/*
											 -------------
											|PAINTING-AREA|
											 -------------
	  (start_view)      (pos_view)                                               (end_view)
			|			    |														 |
			--------------------------------------------------------------------------
			-------------------------------------------------------------------------------------------
			|                              |                                                          |
      (start_data)                     (pos_data)                                                 (end_data)

											 -------------
											|  DATA-AREA  |
											 -------------
*/
	/*
	 * Convert a base position to a screen coordinate
	 * 
	 */
	public static int getXPosition(double pos_data, double  width, double start_data, double end_data){
		double start_view = 0.;
		double end_view = width-1; // -1 because counted from zero
		
		// in bases
		double width_data = end_data - start_data;
		// in pixels
		double width_view = end_view - start_view;
		// factor
		double pixPerBase = end_data!=start_data 
				? width_view / width_data
				: 1;
		
		double pos_view = start_view + (pos_data-start_data)*(end_view-start_view)/(end_data-start_data);
		// if a base has multiple pixels, shift it's position to the _right_ by half that amount, i.e. center it around its screen coordinate
		if (pixPerBase>1)
			pos_view+=pixPerBase/2;
		
		pos_view = Math.floor(pos_view);
		

		
		if(pos_view<0){
			return Integer.MIN_VALUE;
		}
		return (int)pos_view;
	}
	
	/**
	 * 
	 * @param pos_bp
	 * @param width
	 * @param start_data
	 * @param end_data
	 * @param model
	 * @param offset
	 * @return
	 */
	public static double getLeftmostPositionX(double pos_bp, double width, double start_data, double end_data, GenomeOverviewModel model, int offset){
		
		double pos_x = 0;
			//getPosition_x(pos_bp, width, start_data, end_data);
		int lastIndex= HelperClass.getLastXIndex((int)width);
		FromToPosition ftp = new FromToPosition();
		DataMapper.getBpOfView((int)width,model,pos_x, ftp);
		
		for(pos_x = 0; pos_x<=lastIndex; pos_x++){
			ftp.clear();
			getBpOfChromosome((int)width,model,pos_x, ftp);
			if(ftp.getFrom()<=pos_bp && ftp.getTo()>=pos_bp){
				break;
			}
		}
		
		
		return pos_x+offset;
	}
	
	/**
	 * 
	 */
	public static double getRightmostPositionX(double pos_bp, double width, double start_data, double end_data, GenomeOverviewModel model, int offset){
		double pos_x;
		int lastIndex= HelperClass.getLastXIndex((int)width);
		FromToPosition ftp = new FromToPosition();
		
		for(pos_x = lastIndex; pos_x>=0; pos_x--){
			ftp.clear();
			getBpOfChromosome((int)width,model,pos_x, ftp);
			if(ftp.getFrom()<=pos_bp && ftp.getTo()>=pos_bp){
				break;
			}
		}
		return pos_x+offset;
	}
}
