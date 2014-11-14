package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.sequence;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import mayday.core.MaydayDefaults;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;

public class SequenceTrackRenderer extends AbstractTrackRenderer {

	protected Chromosome chrome;
	protected CharSequence seq;
	protected boolean showForward, showReverse;
	
	public SequenceTrackRenderer(GenomeOverviewModel Model, AbstractTrackPlugin track) {
		super(Model,track);
	}

	public void updateInternalVariables() {
		SequenceTrackSettings rts = ((SequenceTrackSettings)tp.getTrackSettings());
		Chromosome c = chromeModel.getActualChrome();
		try {
			seq = rts.getData().getSequence(c);
			showForward = rts.getStrand().similar(Strand.PLUS);
			showReverse = rts.getStrand().similar(Strand.MINUS);
		} catch (Exception e) {}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		if (seq==null)
			updateInternalVariables();

		if (seq==null)
			return;

		Font f = new Font( "Lucida Typewriter", Font.BOLD, 9);
		if (f==null || f.getFamily().equals("Dialog")) // Dialog is Java's fallback font if a font is not found
			f = MaydayDefaults.DEFAULT_PLOT_LARGE_LEGEND_FONT;
		
		FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
				false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );
		TextLayout sLayout = new TextLayout( "ACGT", f, l_frc );

		int charWidth = (int)sLayout.getBounds().getWidth()/4;
		
		Chromosome c = chromeModel.getActualChrome();

		DataMapper.getBpOfView(width, chromeModel, beg_x, ftp);
		long firstBase = ftp.getFrom();
		DataMapper.getBpOfView(width, chromeModel, end_x, ftp);
		long lastBase = ftp.getTo();
		if (firstBase<0)
			firstBase=0;
		if (lastBase<0)
			lastBase=0;
		if (lastBase>c.getLength())
			lastBase = c.getLength();
		
		double pixPerBase = lastBase!=firstBase?
			( end_x - beg_x ) / (lastBase-firstBase):
				0;
		
		int h2 = height/2;
		int topHalfStart = 1;
		int botHalfStart = h2+3;
		
		if (pixPerBase>charWidth) {
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(Color.black);
			long lBase=-1;
			for (int i = beg_x; i<=end_x; ++i) {
				ftp.clear();
				DataMapper.getBpOfView(width, chromeModel, i, ftp);
				long curBase = ftp.getFrom()-1; // zero-based indexing in sequence objects
				if (curBase<0)
					curBase=0;
				if (curBase>=c.getLength())
					curBase = c.getLength()-1;
				if (curBase!=lBase) {
					char cc = seq.charAt((int)curBase);

					// get optimal painting position
					int good_x = DataMapper.getXPosition(curBase+1, width, chromeModel.getViewStart(), chromeModel.getViewEnd());

					if (showForward)
						g.drawString(Character.toString(cc), good_x, (int)(topHalfStart+sLayout.getAscent()) );

					if (showReverse) {
						char crev = '?';					
						switch (Character.toLowerCase(cc)) {
						case 'a': crev = 't'; break;
						case 'c': crev = 'g'; break;
						case 'g': crev = 'c'; break;
						case 't': crev = 'a'; break;
						case 'y': crev = 'r'; break;
						case 'r': crev = 'y'; break;
						case 'k': crev = 'm'; break;
						case 'm': crev = 'k'; break;
						case 's': crev = 's'; break;
						case 'w': crev = 'w'; break;
						case 'b': crev = 'v'; break;
						case 'v': crev = 'b'; break;
						case 'd': crev = 'h'; break;
						case 'h': crev = 'd'; break;
						case 'u': crev = 'a'; break;
						case 'x': crev = 'x'; break;
						case '-': crev = '-'; break;
						case 'n': crev = 'n'; break;
						}
						if (Character.isUpperCase(cc))
							crev = Character.toUpperCase(crev);

						g.drawString(Character.toString(crev), good_x, (int)(botHalfStart+sLayout.getAscent()) );
					}
						
					lBase = curBase;

				} 
			}
			
			
		} else if (lastBase!=firstBase) {
			g.setColor(Color.gray);
			if (showForward)
				g.drawLine(beg_x, topHalfStart+h2/2, end_x,  topHalfStart+h2/2);
			if (showReverse)
				g.drawLine(beg_x, botHalfStart+h2/2, end_x,  botHalfStart+h2/2);
		}
		
	}
		
	public String getInformationAtMousePosition(Point point) {		
		
		if (seq==null)
			return null;
		
		Chromosome c = chromeModel.getActualChrome();

		if (c==null)
			return null;
		
		DataMapper.getBpOfView(width, chromeModel, point.getX(),ftp);
		
		if (!ftp.isValid())
			return null;
		
		long start = ftp.getFrom();
		long end = ftp.getTo();
		// zero-based access
		start--;
		end--;
		if (start<0)
			start=0;
		if (end<0)
			end=0;
		if (end>=c.getLength())
			end = c.getLength()-1;

		
		String t = "<html>loc: ";
		
		if (ftp.getFrom() == ftp.getTo()) {
			t+= ftp.getFrom()+ " (" + 1 + "bp) ";
		} else {
			t+= ftp.getFrom()+ "-" + ftp.getTo() + " ("+ ((ftp.getTo() - ftp.getFrom() + 1)) + "bp)";
		}

		if (end-start>1000)
			t+="<br> too much sequence to display.";
		else {
			t+=" Sequence:<font face='Courier New'>";
			for (long cur = start; cur<=end; ) {
				long curend = Math.min(end, cur+80);
				String label = seq.subSequence((int)cur, (int)curend+1).toString(); 
				t+="<br>"+label;
				cur+=80;
			}
		}

			
			
		return t;
	}
	
}
