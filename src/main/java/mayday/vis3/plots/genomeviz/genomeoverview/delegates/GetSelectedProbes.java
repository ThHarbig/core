package mayday.vis3.plots.genomeviz.genomeoverview.delegates;

import java.awt.Point;
import java.util.Collections;
import java.util.Set;

import mayday.core.Probe;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.PaintingPanel;

public abstract class GetSelectedProbes {

	public static Set<Probe> getProbes(Point point, Strand strand, PaintingPanel panel, GenomeOverviewModel model, FromToPosition ftp){
		return getProbes(point, strand, panel.getHeight(), panel.getWidth(), model, ftp);

	}
	
	public static Set<Probe> getProbes(Point point, Strand strand, double height, double width, GenomeOverviewModel model, FromToPosition ftp){

		Set<Probe> set = Collections.emptySet();
		DataMapper.getBpOfView((int)width,model, point.getX(),ftp);
		if (ftp.isValid()) {
			long from = ftp.getFrom();
			long to = ftp.getTo();

			if (from > to) {
				System.err.println("From Position is higher than to position");
			} else{
				if(strand==null || strand.equals(Strand.BOTH)){
					if (point.getY() <= Math.floor(height / 2.)-1) {
						if (from == to) {
							set = model.getAllForwardProbes(from);
						} else {
							set = model.getAllForwardProbes(from, to);
						}
					} else if(point.getY() >= Math.ceil(height / 2.)){
						if (from == to) {
							set = model.getAllBackwardProbes(from);
						} else {
							set = model.getAllBackwardProbes(from, to);
						}
					}
				} else{
					if (from == to) {
						if (strand.equals(Strand.PLUS)) {
							set = model.getAllForwardProbes(from);
						} else if (strand.equals(Strand.MINUS)) {
							set = model.getAllBackwardProbes(from);
						}
					} else {
						if (strand.equals(Strand.PLUS)) {
							set = model.getAllForwardProbes(from, to);
						} else if (strand.equals(Strand.MINUS)) {
							set = model.getAllBackwardProbes(from, to);
						}
					}
				}
			}
		}
		
		return set;
}


	public static Set<Probe> getProbes(int i, Strand strand,
			PaintingPanel panel, GenomeOverviewModel chromeModel) {
		Set<Probe> set = Collections.emptySet();
		FromToPosition ftp = new FromToPosition();		
		DataMapper.getBpOfView(panel.getWidth(),chromeModel, i, ftp);
		if (ftp.isValid()) {
			long from = ftp.getFrom();
			long to = ftp.getTo();

			if (from > to) {
				System.err.println("From Position is higher than to position");
			} else {
				if (from == to) {
					switch(strand){
					case PLUS:
						set = chromeModel.getAllForwardProbes(from);
						break;
					case MINUS:
						set = chromeModel.getAllBackwardProbes(from);
						break;
					case BOTH:
						set = chromeModel.getBothProbes(from);
						break;
					}
				} else {
					switch(strand){
					case PLUS:
						set = chromeModel.getAllForwardProbes(from, to);
						break;
					case MINUS:
						set = chromeModel.getAllBackwardProbes(from, to);
						break;
					case BOTH:
						set = chromeModel.getBothProbes(from, to);
						break;
					}
				}
			}
		}
		return set;
	}
}
