package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.profile;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.Strand;
import mayday.vis3.ValueProvider;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class ProfileTrackRenderer extends AbstractTrackRenderer {

	protected Set<Probe> list = null;
	protected AbstractTrackSettings ts = null;
	protected Rectangle2D.Double r2d = null;
	double maxy, miny;
	SplitView split;

	public ProfileTrackRenderer(GenomeOverviewModel Model, AbstractTrackPlugin TrackPlugin) {
		super(Model,TrackPlugin);
		ts = tp.getTrackSettings();
	}

	public void updateInternalVariables() {
		ProfileTrackSettings pts = ((ProfileTrackSettings)ts);
		maxy=pts.getHeightProvider().getMaximum();
		miny=pts.getHeightProvider().getMinimum();
		split = pts.getRepresentation();
	}

	public void paint(Graphics g) {

		super.paint(g);
		Graphics2D g2D = (Graphics2D)g;

		ProfileTrackSettings pts = ((ProfileTrackSettings)ts);

		ValueProvider vp = pts.getHeightProvider();
		
		drawZeroLine(g2D, miny, maxy);

		g2D.setColor(Color.black);
		
		long lfrom=-1, lto=-1;
		Double valP = null, valM=null;
		Integer lastMappedP = null, lastMappedM=null;

		for (int i = beg_x; i != end_x; ++i) {
			DataMapper.getBpOfView(width, chromeModel, i, ftp);

			if (lfrom!=ftp.getFrom() || lto!=ftp.getTo()) {
				lfrom = ftp.getFrom(); 
				lto=ftp.getTo();
				List<LocusGeneticCoordinateObject<Probe>> probes = chromeModel.getData().getProbes(lfrom, lto, Strand.UNSPECIFIED);
				
				int plus=0;
				int minus=0;
				for (LocusGeneticCoordinateObject<Probe> olgcp : probes)
					if (olgcp.getStrand()==Strand.PLUS)
						++plus;
					else if (olgcp.getStrand()==Strand.MINUS)
						++minus;
				
				DoubleVector dvP = new DoubleVector(plus);
				DoubleVector dvM = new DoubleVector(minus);
				
				int kP=0, kM=0;
				for (LocusGeneticCoordinateObject<Probe> olgcp : probes)
					if (olgcp.getStrand()==Strand.PLUS)
						dvP.set(kP++, vp.getValue(olgcp.getObject()));
					else if (olgcp.getStrand()==Strand.MINUS)
						dvM.set(kM++, vp.getValue(olgcp.getObject()));

				if (dvP.size()>0) {				
					if(split.equals(SplitView.mean)){
						valP= dvP.mean( true );
					} else if(split.equals(SplitView.min)){
						valP= dvP.min();
					} else if(split.equals(SplitView.max)){
						valP= dvP.max();
					}
				}
				if (dvM.size()>0) {				
					if(split.equals(SplitView.mean)){
						valM= dvM.mean( true );
					} else if(split.equals(SplitView.min)){
						valM= dvM.min();
					} else if(split.equals(SplitView.max)){
						valM= dvM.max();
					}
				}
				
				
			}

			g.setColor(pts.getPlusColor());
			if (valP!=null && !Double.isNaN(valP)) {
				int mapped = mapValue(miny,maxy,valP);
				if (lastMappedP!=null)
					g2D.drawLine(i-1, lastMappedP, i, mapped);
				else
					g2D.drawLine(i-1, mapped, i, mapped);
				lastMappedP = mapped;
			} else {
				lastMappedP = null;
			}
			
			g.setColor(pts.getMinusColor());
			if (valM!=null && !Double.isNaN(valM)) {
				int mapped = mapValue(miny,maxy,valM);
				if (lastMappedM!=null)
					g2D.drawLine(i-1, lastMappedM, i, mapped);
				else
					g2D.drawLine(i-1, mapped, i, mapped);
				lastMappedM = mapped;
			} else {
				lastMappedM = null;
			}
		}

	}

	private int mapValue(double min, double max, double val) {
		double v = (double) height * (val-min) / (max-min);
		return height-(int)Math.round(v);			 
	}

	private void drawZeroLine(final Graphics2D g2dmain, double min, double max) {
		g2dmain.setColor(Color.DARK_GRAY);
		int zero = mapValue(min, max, 0);
		g2dmain.drawLine(beg_x, zero, end_x, zero);			
	}

}
