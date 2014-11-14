package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

import mayday.vis3.plots.genomeviz.EnumManagerGO.Up_Down;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;

public class DataMapper {
	
	public static void getDataForMousePos(int widthWindow, GenomeHeatMapTableModel model, double mousePosX, FromToPosition ftp){

		if(mousePosX <0.){
			ftp.clear();
		} else{
			
			long end_chrome = model.getChromosomeEnd();

			double start_data = model.getChromosomeStart();
			double end_data = model.getChromosomeEnd();
			
			double start_view = 0.;
			double end_view = widthWindow-1; // -1 because pixel is counted from zero
			double pos_view = mousePosX; // +1 because counting from zero
			
			double from_view = (pos_view-start_view)/(end_view-start_view)*(end_data-start_data) + start_data;

			pos_view = pos_view+1;
			double to_view = (pos_view-start_view)/(end_view-start_view)*(end_data-start_data) + start_data;

			long from = (long)Math.floor(from_view);
			long to = (long)Math.floor(to_view)-1;

			if (from == 0)from=1;
			if(from > end_chrome ){
				from = end_chrome;
			}
			
			if (to == 0)to=1;
			if(to > end_chrome ){
				to = end_chrome;
			}
			
			if(to < from){
				to = from;
			}
			
			ftp.setPositions((int)from, (int)to);
		}
	}
	
	public static double getMousePosForData(double pos_bp, double  reducedWidth, GenomeHeatMapTableModel model,	int left_margin, Up_Down up_Down){
	
		double start_data = model.getViewStart();
		double end_data = model.getViewEnd();
		double pos_data = pos_bp; // +1 because counting from zero
		
		double start_view = 1.;
		double end_view = reducedWidth -1;
		double pos_view = 0.; // +1 because counting from zero
		
		pos_view = start_view + (pos_data-start_data)*(end_view-start_view)/(end_data-start_data);
		pos_view = Math.round(pos_view);

		
		pos_view = pos_view+left_margin-1.; // -1 because pixel counted from zero
		
		if(pos_view<0){
//			System.err.println("getPosition_x VALUE <0");
		}
		return pos_view;
	}
}
