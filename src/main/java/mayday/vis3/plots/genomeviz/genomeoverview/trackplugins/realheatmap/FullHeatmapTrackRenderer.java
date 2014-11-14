package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.realheatmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.Strand;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.GetSelectedProbes;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class FullHeatmapTrackRenderer extends AbstractTrackRenderer {

	protected Set<Probe> list = null;
	protected AbstractTrackSettings ts = null;
	protected Rectangle2D.Double r2d = new Rectangle2D.Double();
	protected SplitView split;
	protected ColorGradient gradient;
	protected boolean mirror;

	public FullHeatmapTrackRenderer(GenomeOverviewModel Model, AbstractTrackPlugin TrackPlugin) {
		super(Model,TrackPlugin);
		ts = tp.getTrackSettings();
	}

	public void updateInternalVariables() {
		FullHeatmapTrackSettings pts = ((FullHeatmapTrackSettings)ts);
		split = pts.getRepresentation();
		gradient = null;
		mirror = pts.mirrored();
	}

	public void paint(Graphics g) {

		super.paint(g);
		Graphics2D g2D = (Graphics2D)g;

		if (split==null)
			return;
		
		FullHeatmapTrackSettings pts = ((FullHeatmapTrackSettings)ts);

		ViewModel vm = chromeModel.getViewModel();
		int nr = vm.getDataSet().getMasterTable().getNumberOfExperiments();

		if (gradient==null) {
			double max = vm.getMaximum(null, null);
			double min = vm.getMinimum(null, null);
			gradient = pts.getGradient();
			gradient.setMax(max);
			gradient.setMin(min);
		}
		
		double interval = height/(double)nr;
		double startP=0; 
		double startM=0;
		
		switch(pts.getStrand()) {
		case BOTH: // draw null line, split region
			startM = height/2d;
			interval/=2;
			break;
		default:
			break;
		}
		
		long lfrom=-1, lto=-1;

		AbstractVector dvP = null, dvM = null;

		for (int i = beg_x; i != end_x; ++i) {
			DataMapper.getBpOfView((int)width,chromeModel, i,ftp);
			
			if (lfrom!=ftp.getFrom() || lto!=ftp.getTo()) {
				lfrom = ftp.getFrom(); 
				lto=ftp.getTo();
				List<LocusGeneticCoordinateObject<Probe>> probes = chromeModel.getData().getProbes(lfrom, lto, pts.getStrand());
				
				int plus=0;
				int minus=0;
				for (LocusGeneticCoordinateObject<Probe> olgcp : probes)
					if (olgcp.getStrand()==Strand.PLUS)
						++plus;
					else if (olgcp.getStrand()==Strand.MINUS)
						++minus;
				
				// get expression matrix for this coordinate
				
				DoubleMatrix dmP = new DoubleMatrix(nr, plus);
				DoubleMatrix dmM = new DoubleMatrix(nr, minus);
				
				int kP=0, kM=0;
				for (LocusGeneticCoordinateObject<Probe> olgcp : probes)
					if (olgcp.getStrand()==Strand.PLUS)
						dmP.setColumn(kP++, vm.getProbeValues(olgcp.getObject()));
					else if (olgcp.getStrand()==Strand.MINUS)
						dmM.setColumn(kM++, vm.getProbeValues(olgcp.getObject()));

				dvP = null;
				dvM = null;
				
				// compute row-wise mean,min,max
				if (dmP.ncol()>0) {				
					if(split.equals(SplitView.mean)){
						dvP = dmP.applyVec(0, "mean");
					} else if(split.equals(SplitView.min)){
						dvP = dmP.applyVec(0, "min");
					} else if(split.equals(SplitView.max)){
						dvP = dmP.applyVec(0, "max");
					}
				}
				if (dmM.ncol()>0) {				
					if(split.equals(SplitView.mean)){
						dvM = dmM.applyVec(0, "mean");
					} else if(split.equals(SplitView.min)){
						dvM = dmM.applyVec(0, "min");
					} else if(split.equals(SplitView.max)){
						dvM = dmM.applyVec(0, "max");
					}
				}

			}
				
			// with all the data ready, draw heatmaps

			if (dvP!=null) {
				double pos = startP;
				for (int r=0; r!=dvP.size(); ++r) {
					r2d.setFrame(i, pos, 1, interval);
					g.setColor(gradient.mapValueToColor(dvP.get(r)));
					g2D.fill(r2d);
					pos+=interval;
				}
			}

			if (dvM!=null) {
				double pos = mirror?height-interval:startM;
				for (int r=0; r!=dvM.size(); ++r) {
					r2d.setFrame(i, pos, 1, interval);
					g.setColor(gradient.mapValueToColor(dvM.get(r)));
					g2D.fill(r2d);
					if (mirror)
						pos-=interval;
					else 
						pos+=interval;
				}
			}

			if (startM!=0)
				drawZeroLine(g2D);
		}

	}
	
	public String getInformationAtMousePosition(Point point) {		
		Set<Probe> items = GetSelectedProbes.getProbes(point, Strand.BOTH, height, width, chromeModel, ftp);
		
		String t = "<html>"+items.size()+" probes, loc: ";
		
		if (!ftp.isValid())
			return null;
		
		if (ftp.getFrom() == ftp.getTo()) {
			t+= ftp.getFrom()+ " (" + 1 + "bp) ";
		} else {
			t+= ftp.getFrom()+ "-" + ftp.getTo() + " ("+ ((ftp.getTo() - ftp.getFrom() + 1)) + "bp)";
		}
		
		int size = items.size();
		t+="<br>";
		if(size > 10){
			int counter = 0;
			for(Probe p: items){
				t = t + p.getDisplayName() + "<br>";
				
				if(counter == 10){
					t = t + " ... <br>";
					break;
				}
				counter++;
			}
		} else {
			for(Probe p: items){
				t = t + p.getDisplayName() + "<br>";
			}				
		}
		
		return t;
	}


	private void drawZeroLine(final Graphics2D g2dmain) {
		g2dmain.setColor(Color.DARK_GRAY);
		g2dmain.drawLine(beg_x, height/2, end_x, height/2);			
	}

}
