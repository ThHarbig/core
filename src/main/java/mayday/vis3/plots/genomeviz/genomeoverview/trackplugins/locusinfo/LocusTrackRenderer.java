package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.locusinfo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import mayday.core.MaydayDefaults;
import mayday.genetics.advanced.chromosome.LocusChromosomeObject;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;

public class LocusTrackRenderer extends AbstractTrackRenderer {

	protected ChromosomeSetContainer theData;
	protected final static Color locusColor = new Color(0,0,0,100);
	protected boolean showExons;
	
	public LocusTrackRenderer(GenomeOverviewModel Model, AbstractTrackPlugin track) {
		super(Model,track);
	}

	public void updateInternalVariables() {
		try {
			theData = ((LocusTrackSettings)tp.getTrackSettings()).getLocusMap().asChromosomeSetContainer();
			showExons =  ((LocusTrackSettings)tp.getTrackSettings()).showExons();
		} catch (Exception e) {
			
		}
	}
	
	protected void paint_loci(Graphics g, LocusChromosomeObject<String> olc) {
		int h2 = height/2;
		int topHalfStart = 0;
		int botHalfStart = h2+3;
		int rh = h2-3;

		DataMapper.getBpOfView(width, chromeModel, beg_x, ftp);
		long start = ftp.getFrom();
		DataMapper.getBpOfView(width, chromeModel, end_x, ftp);
		long end = ftp.getTo();
		
		int pixelsPerBase = (int)Math.max(1, (double)(end_x-beg_x)/(double)(end-start));

		int fontAscent = g.getFontMetrics().getAscent();

		if (start<0)
			start=0;
		if (end<0)
			end=0;

		List<LocusGeneticCoordinateObject<String>> items = olc.getSpanningLoci(start, end, Strand.UNSPECIFIED);

		Rectangle2D renderingWindowClip = new Rectangle2D.Double(beg_x, 0, end_x-beg_x, height);
		Rectangle2D innerClip = new Rectangle2D.Double();

		int[] arrowX = new int[3];

		for (LocusGeneticCoordinateObject<String> olgc : items) {
			int graphicsStart = DataMapper.getXPosition(					
					olgc.getFrom(),
					width, chromeModel.getChromosomeStart(), 
					chromeModel.getChromosomeEnd());
			int graphicsEnd = DataMapper.getXPosition(					
					olgc.getTo(),
					width, chromeModel.getChromosomeStart(), 
					chromeModel.getChromosomeEnd());
			int rw = Math.max(pixelsPerBase,graphicsEnd-graphicsStart);
			boolean fwd = olgc.getStrand().similar(Strand.PLUS);
			boolean bwd = olgc.getStrand().similar(Strand.MINUS);

			int ystart;
			int rectshft;

			int arrow = 5;

			if (rw<arrow*2) {
				arrow = 0;
			} 

			if (fwd&&bwd) {
				arrow = 0;
				ystart=topHalfStart;
				rectshft=0;
				// arrowX is not used anyways
				plotLocus(g, olgc.getObject(), innerClip, renderingWindowClip, ystart, arrowX, arrow, graphicsStart, height, rw, rectshft, fontAscent);
			} else if (fwd) {
				ystart = topHalfStart;
				arrowX[0] = graphicsEnd-arrow;
				arrowX[1] = graphicsEnd-arrow;
				arrowX[2] = graphicsEnd;
				rectshft = 0;
				plotLocus(g, olgc.getObject(), innerClip, renderingWindowClip, ystart, arrowX, arrow, graphicsStart, rh, rw, rectshft, fontAscent);
			} else {
				ystart = botHalfStart;
				arrowX[0] = graphicsStart+arrow;
				arrowX[1] = graphicsStart+arrow;
				arrowX[2] = graphicsStart;
				rectshft = arrow;
				plotLocus(g, olgc.getObject(), innerClip, renderingWindowClip, ystart, arrowX, arrow, graphicsStart, rh, rw, rectshft, fontAscent);
			}
		}
	}

	
	protected void paint_exons(Graphics g, LocusChromosomeObject<String> olc) {
		int h2 = height/2;
		int topHalfStart = 0;
		int botHalfStart = h2+3;
//		int rh = h2-3;
		
		DataMapper.getBpOfView(width, chromeModel, beg_x, ftp);
		long start = ftp.getFrom();
		DataMapper.getBpOfView(width, chromeModel, end_x, ftp);
		long end = ftp.getTo();
		
		int fontAscent = g.getFontMetrics().getAscent();

		if (start<0)
			start=0;
		if (end<0)
			end=0;
		
		List<LocusGeneticCoordinateObject<String>> items = olc.getSpanningLoci(start, end, Strand.UNSPECIFIED);
		
		Rectangle2D renderingWindowClip = new Rectangle2D.Double(beg_x, 0, end_x-beg_x, height);
		Rectangle2D innerClip = new Rectangle2D.Double();
		
		for (LocusGeneticCoordinateObject<String> olgc : items) {

			int connectorX=-1;
			int connectorY=-1;
			
			String name = olgc.getObject();
			int exonCount=0;
			
			for (GBAtom gbre : olgc.getCoordinateAtoms()) {
				// map cordinates
				int graphicsStart = DataMapper.getXPosition(					
						gbre.from,
						width, chromeModel.getChromosomeStart(), 
						chromeModel.getChromosomeEnd());
				int graphicsEnd = DataMapper.getXPosition(					
						gbre.to,
						width, chromeModel.getChromosomeStart(), 
						chromeModel.getChromosomeEnd());
				int rw = graphicsEnd-graphicsStart;

				boolean fwd = gbre.strand.similar(Strand.PLUS);
				
				int Y = fwd?topHalfStart:botHalfStart;
				
				g.setClip(renderingWindowClip);

				g.setColor(locusColor);
				g.setColor(new Color(0,0,0,100));
				
				if (rw<1)
					rw=1;
				g.fillRect(graphicsStart, Y, rw, Y+h2);
				
				if (connectorY>-1) {
					int X = fwd?graphicsStart:graphicsEnd;
					if (Y!=connectorY)
						g.drawLine(connectorX, connectorY+h2/2, X, Y+h2/2);
					else {
						int minX = Math.min(connectorX, X);
						int maxX= Math.max(connectorX, X);
						g.fillRect(minX, Y+h2/2, maxX-minX, 1);
					}
					
				}
				connectorY = Y;
				connectorX = fwd?graphicsEnd:graphicsStart;
				
				innerClip.setRect(graphicsStart, Y, rw, h2);
				Rectangle.intersect(renderingWindowClip, innerClip, innerClip);
				g.setClip(innerClip);
				g.setColor(Color.white);
				g.drawString(name+" ("+(++exonCount)+")", graphicsStart+2, Y+fontAscent+1 );

			}
		}		
		g.setClip(null);
	}
	
	
	protected void plotLocus(Graphics g, 
			String label,
			Rectangle2D innerClip, Rectangle2D renderingWindowClip, 
			int ystart, int[] arrowX, int arrow, int graphicsStart, 
			int rh, int rw, int rectshft, int fontAscent
	) {
		
		innerClip.setRect(graphicsStart, ystart, rw, rh);
		Rectangle.intersect(renderingWindowClip, innerClip, innerClip);
		g.setClip(innerClip);
		
		g.setColor(locusColor);
		
		if (rw<1) {
			g.drawLine(graphicsStart, ystart, graphicsStart, ystart+rh);
		} else {
			
			int[] arrowY = new int[]{ ystart, ystart+rh, ystart+(rh/2) };
			
			innerClip.setRect(graphicsStart, ystart, rw, rh);
			Rectangle.intersect(renderingWindowClip, innerClip, innerClip);
			g.setClip(innerClip);
			
			g.fillRect(graphicsStart+rectshft, ystart, rw-arrow, rh);
			g.fillPolygon(arrowX, arrowY, 3);
			
			g.setColor(Color.white);
			innerClip.setRect(graphicsStart, ystart, rw-arrow, rh);
			Rectangle.intersect(renderingWindowClip, innerClip, innerClip);			
			g.setClip(innerClip);
			
			if (label==null)
				label = "null";
			g.drawString(label, graphicsStart+rectshft+2, ystart+fontAscent+1 );
			g.setClip(null);	
		}
	}
	
	@SuppressWarnings("unchecked")
	public void paint(Graphics g) {
		if (theData==null)
			updateInternalVariables();

		if (theData==null)
			return;
		
		super.paint(g);
		
		String specID = chromeModel.getActualSpecies().getName();
		String chromeID = chromeModel.getActualChrome().getId();
		Chromosome c = theData.getChromosome(SpeciesContainer.getSpecies(specID), chromeID);
		if (c==null || !(c instanceof LocusChromosomeObject))
			return;
		
		g.setFont(MaydayDefaults.DEFAULT_PLOT_FONT);
		
		LocusChromosomeObject<String> olc = (LocusChromosomeObject<String>)c;
		
		if (showExons) 
			paint_exons(g, olc);
		else
			paint_loci(g, olc);

	}
	
	@SuppressWarnings("unchecked")
	public String getInformationAtMousePosition(Point point) {		

		String specID = chromeModel.getActualSpecies().getName();
		String chromeID = chromeModel.getActualChrome().getId();
		
		if (theData==null)
			return null;
		
		Chromosome c = theData.getChromosome(SpeciesContainer.getSpecies(specID), chromeID);
		if (c==null || !(c instanceof LocusChromosomeObject))
			return null;
		
		DataMapper.getBpOfView(width, chromeModel, point.getX(),ftp);

		if (!ftp.isValid())
			return null;
		
		LocusChromosomeObject<String> olc = (LocusChromosomeObject<String>)c;
		Strand s = Strand.PLUS;
		if (point.getY()>height/2)
			s = Strand.MINUS;
		List<LocusGeneticCoordinateObject<String>> items = olc.getSpanningLoci(ftp.getFrom(), ftp.getTo(), s);

		String t = "<html>"+items.size()+" elements, loc: ";
		
		if (ftp.getFrom() == ftp.getTo()) {
			t+= ftp.getFrom()+ " (" + 1 + "bp) ";
		} else {
			t+= ftp.getFrom()+ "-" + ftp.getTo() + " ("+ ((ftp.getTo() - ftp.getFrom() + 1)) + "bp)";
		}
		
		for (LocusGeneticCoordinateObject<String> olgc : items) 
			t+="<br>"+olgc.getCoordinateAtoms()+": "+olgc.getObject();
			
		return t;
	}
	
}





