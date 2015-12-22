package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.wiggle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import mayday.core.structures.natives.LinkedDoubleArray;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;

public class WiggleTrackRenderer extends AbstractTrackRenderer {

	protected int exp=-1;
	protected Chromosome chrome;
	protected LinkedDoubleArray wiggle;
	protected Double max, min;
	protected WiggleTrackSettings wts;
	protected int zeroheight;
	
	public WiggleTrackRenderer(GenomeOverviewModel Model, AbstractTrackPlugin track) {
		super(Model,track);
	}

	public void updateInternalVariables() {
		wts = ((WiggleTrackSettings)tp.getTrackSettings());
		Chromosome c = chromeModel.getActualChrome();
		try {
			wiggle = wts.getWiggle().getWiggle(c);
			max=null;
		} catch (Exception e) {}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		if (wiggle==null)
			updateInternalVariables();

		if (wiggle==null)
			return;
		
		if (wts.getRenderingMethod()==WiggleTrackSettings.LINEMODE) 
			paintWiggle(g);
		else
			paintBlocks(g);
	}
	
	protected void doUpdateRange() {
		double[] val = wts.getValueRange();
		
		if (val!=null) {
			
			min = val[0];
			max = val[1];
			
		} else if (max==null) {

			max=Double.NEGATIVE_INFINITY;
			min=Double.POSITIVE_INFINITY;
			Chromosome c = chromeModel.getActualChrome();

			for (int i=0; i<=width; ++i) {
				DataMapper.getBpOfView(width, chromeModel, i, ftp);
				long start = ftp.getFrom();
				long end = ftp.getTo();
				ftp.clear();
				if (start<0)
					start=0;
				if (end<0)
					end=0;
				if (end>c.getLength())
					end = c.getLength();
				double sum=0;	
				for (long s = start; s<=end; ++s) {
					double wg = wiggle.get(s+1); // wiggle files are one based
					if (!Double.isNaN(wg)) {						
						sum +=wg;
					}
				}

				sum /= end-start+1;
				max = max>sum?max:sum;
				min = min<sum?min:sum;
			}
		}
		
		if (max<=0 && min<=0) // negative wiggles
			zeroheight = 0;
		else if (max>=0 && min>=0) // positive wiggles
			zeroheight = height;
		else { //mixed wiggles: max>0>min
			double maxperc = max / (max-min); 
			zeroheight = (int)((height-1)*maxperc);
		}

	}
	
	protected void paintWiggle(Graphics g) {
		Chromosome c = chromeModel.getActualChrome();

		doUpdateRange();
		
		g.setColor( wts.getColor() );
		Color naColor = wts.getNAColor();
	
//		int last=0;
//		double lastPerc=Double.NaN;

		for (int i=beg_x; i<=end_x; ++i) {
			DataMapper.getBpOfView(width, chromeModel, i, ftp);
			long start = ftp.getFrom();
			long end = ftp.getTo();
			if (start<0)
				start=0;
			if (end<0)
				end=0;
			if (end>c.getLength())
				end = c.getLength();
			double sum=0;			
			for (long s = start; s<=end; ++s) {
				sum += wiggle.get(s+1); // wiggle files are one based
			}
			
			double bc = end-start+1;
			sum /= bc;
			
			double perc = (sum-min)/(max-min);
			
			sum = (int)((height-1)*perc);
			
			// indicate NAs
			if (Double.isNaN(perc)) {
				Color prevcol = g.getColor();
				g.setColor(naColor);
				g.drawLine(i, 0, i, height);
				g.setColor(prevcol);
			} else {	
				g.drawLine(i, zeroheight, i, height-(int)sum-1);
			
//				if (!Double.isNaN(lastPerc)) {  // connected line
//					g.drawLine(i-1, height-last-1, i, height-(int)sum-1);
//				} else { // unconnected line
//					g.drawLine(i, height-(int)sum-1, i, height-(int)sum-1);
//				}				
			}			
			
//			last = (int)sum;
//			lastPerc = perc;
		}
	}
	
	public void paintBlocks(Graphics g) {
		Chromosome c = chromeModel.getActualChrome();

//		int[] len = wts.getSizeFilter();
		double[] val = wts.getValueFilter();
		
		g.setColor(wts.getColor());
		
		for (int i=beg_x; i<=end_x; ++i) {
			DataMapper.getBpOfView(width, chromeModel, i, ftp);
			long start = ftp.getFrom();
			long end = ftp.getTo();
			if (start<0)
				start=0;
			if (end<0)
				end=0;
			if (end>c.getLength())
				end = c.getLength();
			
			boolean pixelHasValue = false; 

			for (long s = start; s<=end; ++s) {
				double wg = wiggle.get(s+1); // wiggle files are one based
				if (!Double.isNaN(wg))
					pixelHasValue |= (wg >= val[0] && wg <= val[1]);
			}
			
			if (pixelHasValue)
				g.drawLine(i, height, i, 0);
		}
	}
		
	public String getInformationAtMousePosition(Point point) {		
		
		if (wiggle==null)
			return null;
		
		Chromosome c = chromeModel.getActualChrome();

		if (c==null )
			return null;
		
		DataMapper.getBpOfView(width, chromeModel, point.getX(),ftp);
		
		if (!ftp.isValid())
			return null;
		
		long start = ftp.getFrom();
		long end = ftp.getTo();
		if (start<0)
			start=0;
		if (end<0)
			end=0;
		if (end>c.getLength())
			end = c.getLength();
		double sum=0;			
		for (long s = start; s<=end; ++s) {
			sum += wiggle.get(s+1); // wiggle files are one based
		}
		
		double bc = end-start+1;
		sum /= bc;
		
		String t = "<html>loc: ";
		
		if (ftp.getFrom() == ftp.getTo()) {
			t+= ftp.getFrom()+ " (" + 1 + "bp) ";
		} else {
			t+= ftp.getFrom()+ "-" + ftp.getTo() + " ("+ ((ftp.getTo() - ftp.getFrom() + 1)) + "bp) ";
		}
		
		t+="Average value = "+sum;
			
			
		return t;
	}
	
}
