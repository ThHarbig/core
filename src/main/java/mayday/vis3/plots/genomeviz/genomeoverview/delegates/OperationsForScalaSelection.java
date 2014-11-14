package mayday.vis3.plots.genomeviz.genomeoverview.delegates;

import java.awt.Rectangle;

import mayday.vis3.plots.genomeviz.EnumManagerGO.Fixed;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;

public abstract class OperationsForScalaSelection {

	public  static  double getRangeOfVisible_BP(double visLowPos_bp, double visHighPos_bp){

		double range_bp = visHighPos_bp - visLowPos_bp +1;
		
		if(visHighPos_bp <1. || visLowPos_bp <1. || range_bp < 1.){
			System.err.println("Visible Range of bp not valid. return -1");
			return -1.;
		}
		return range_bp;
	}
	
	public static double getClickedPositionInHeader(FromToPosition ftp){
		
		double clickedPos_bp = ftp.getFrom()+(Math.floor(getRangeOfVisible_BP(ftp.getFrom(),ftp.getTo())/2.));
		
		if(ftp.getFrom() < 1. || ftp.getTo() <1. || clickedPos_bp < 1.){
			System.err.println("Clicked position of bp not valid. return -1");
			return -1;
		}
		return clickedPos_bp;
	}
	
	public  static Rectangle repositionVisibleRect_MouseClicked(
			GenomeOverviewModel model, double visLowPos_new_bp,
			double visHighPos_new_bp, Fixed fixed) {

		FromToPosition ftp = new FromToPosition();

		int start_pp_x = model.getLocation_paintingpanel_X();
		int width_pp_reduced = model.getWidth_paintingpanel_reduced();

		// compute x position of visible rect
		double visRect_Low_x = 0.;
		visRect_Low_x = DataMapper.getXPosition(visLowPos_new_bp,
				width_pp_reduced, model.getChromosomeStart(), model.getChromosomeEnd());

		for (int i = (int) visRect_Low_x; i >= 0; --i) {
			DataMapper.getBpOfView(width_pp_reduced, model, i, ftp);

			if (ftp.getFrom() >= visLowPos_new_bp
					|| ftp.getTo() >= visLowPos_new_bp) {
				visRect_Low_x = i;
			} else {
				break;
			}
		}

		// original x position in layered pane
//		visRect_Low_x = visRect_Low_x + (start_pp_x - 1);

		double visRect_High_x = 0.;
		visRect_High_x = DataMapper.getXPosition(visHighPos_new_bp,
				width_pp_reduced, model.getChromosomeStart(), model.getChromosomeEnd());

		for (int i = (int) visRect_High_x; i < width_pp_reduced; ++i) {
			ftp.clear();
			DataMapper.getBpOfView(width_pp_reduced, model, i, ftp);
			if (ftp.getFrom() <= visHighPos_new_bp
					|| ftp.getTo() <= visHighPos_new_bp) {
				visRect_High_x = i;
			} else {
				break;
			}
		}

		// original x position in layered pane
		visRect_High_x = visRect_High_x + start_pp_x;

		Rectangle rect = null;
		
//		visRect_Low_x = visRect_Low_x - (start_pp_x - 1);
		
		if (model.getVisibleRectOfLayeredPane() != null) {
			if (fixed == null) {
				if (model.getVisibleRectOfLayeredPane().getX() < visRect_Low_x) {
					rect = new Rectangle((int) visRect_High_x, 0, 0, 0);
				} else if (model.getVisibleRectOfLayeredPane().getX() >= visRect_Low_x) {
					rect = new Rectangle((int) visRect_Low_x, 0, 0, 0);
				}
			} else {
				if (fixed.equals(Fixed.LEFT_FIXED_RIGHT_BIGGER)) {
//					System.out.println("left fix right bigger");
					if (model.getVisibleRectOfLayeredPane().getX() < visRect_Low_x) {
						rect = new Rectangle((int) visRect_High_x, 0, 0, 0);
					} else if (model.getVisibleRectOfLayeredPane().getX() >= visRect_Low_x) {
						rect = new Rectangle((int) visRect_Low_x, 0,0, 0);
					}
				} else if (fixed.equals(Fixed.LEFT_FIXED_RIGHT_SMALLER)) {
//					System.out.println("left fix right smaller");
					rect = new Rectangle((int) visRect_High_x, 0, 0, 0);
				} else if (fixed.equals(Fixed.RIGHT_FIXED_LEFT_BIGGER)) {
//					System.out.println("right fix left bigger");
					if (model.getVisibleRectOfLayeredPane().getX() < visRect_Low_x) {
						rect = new Rectangle((int) visRect_High_x, 0, 0, 0);
					} else if (model.getVisibleRectOfLayeredPane().getX() >= visRect_Low_x) {
						rect = new Rectangle((int) visRect_Low_x, 0, 0, 0);
					}
				} else if (fixed.equals(Fixed.RIGHT_FIXED_LEFT_SMALLER)) {
//					System.out.println("right fix left smaller");
					rect = new Rectangle((int) visRect_High_x, 0, 0, 0);
				}
			}
		}

		return rect;
	}
	
	public  static Rectangle repositionVisibleRect_Centering(
			GenomeOverviewModel model, double center_bp) {
		FromToPosition ftp = new FromToPosition();

		int width_pp_reduced = model.getWidth_paintingpanel_reduced();

		// compute x position of visible rect
		double visRect_center_x = 0.;
		visRect_center_x = DataMapper.getXPosition(center_bp,
				width_pp_reduced, model.getChromosomeStart(), model.getChromosomeEnd());

		for (int i = (int) visRect_center_x; i >= 0; --i) {
			DataMapper.getBpOfView(width_pp_reduced, model, i,ftp);

			if (ftp.getFrom() >= center_bp
					|| ftp.getTo() >= center_bp) {
				visRect_center_x = i;
			} else {
				break;
			}
		}

		Rectangle rect = null;

		if (model.getVisibleRectOfLayeredPane() != null) {
			double width = model.getVisibleRectOfLayeredPane().getWidth();
			width = width - model.getWidth_userpanel();	
			int half = (int)(Math.round(width/2.));
			rect = new Rectangle((int)visRect_center_x - half, 0, (int)model.getVisibleRectOfLayeredPane().getWidth(), 0);
		}
		return rect;
	}
}
