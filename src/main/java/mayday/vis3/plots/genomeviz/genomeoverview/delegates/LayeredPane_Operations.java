package mayday.vis3.plots.genomeviz.genomeoverview.delegates;

import mayday.vis3.plots.genomeviz.genomeoverview.ConstantData;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;

public abstract class LayeredPane_Operations {
	
	public static double computeNewWidthOfLayeredPane(double visLowPos_new_bp, double visHighPos_new_bp, GenomeOverviewModel model) {
		double neededVisible_bp = visHighPos_new_bp-visLowPos_new_bp+1;
		double newWidthLayeredPane = getNeededWidthLayeredPane(model, neededVisible_bp);
		if(newWidthLayeredPane > model.getMaximalTrackWidth()){
			newWidthLayeredPane = model.getMaximalTrackWidth();
		}
		return newWidthLayeredPane;
	}
	
	public static double getNeededWidthLayeredPane(GenomeOverviewModel model, double neededVisible_bp){

		long length_chrome = model.getLengthOfChromosome();
		double usablewidth = model.getWidth_usableSpace();


		double newWidth_pp = Math.round(length_chrome*(usablewidth/neededVisible_bp));
		double newWidth_layeredPane = newWidth_pp + model.getLocation_paintingpanel_X() + ConstantData.RIGHT_MARGIN;

		return newWidth_layeredPane;
	}
}
