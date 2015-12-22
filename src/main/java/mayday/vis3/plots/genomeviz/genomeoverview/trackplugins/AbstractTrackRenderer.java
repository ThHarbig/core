package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;

public abstract class AbstractTrackRenderer implements ITrackRenderer{
	
	protected GenomeOverviewModel chromeModel = null;
	protected AbstractTrackPlugin tp = null;

	protected int width = 0;
	protected int height = 0;
	
	protected int beg_x = 0;
	protected int end_x = 0;
	
	protected FromToPosition ftp;
	
	public AbstractTrackRenderer(GenomeOverviewModel Model,
			AbstractTrackPlugin TrackPlugin) {
		
		this.chromeModel = Model;
		this.tp = TrackPlugin;
		
		height = getImageHeight();
		width = getImageWidth();
		
		ftp = new FromToPosition();
	}
	
	public void paint(Graphics g) {
//		Graphics2D g2D = (Graphics2D)g;
//		g2D.setBackground(Color.white);
//		g2D.clearRect(beg_x, 0, end_x-beg_x, height);
	}
	
	protected final int getImageWidth() {
		return chromeModel.getWidth_paintingpanel_reduced();
	}

	protected final int getImageHeight() {
		return tp.getPaintingPanel().getHeight();
	}
	
	public void updateInternalVariables() {
	}

	public BufferedImage createEmptyBufferedImage() {
		return new BufferedImage(getImageWidth(), getImageHeight(), BufferedImage.TYPE_3BYTE_BGR);
	}
	
	public final Dimension getPreferredSize() {
		return null;
	}
	
	public final BufferedImage getBufferedImage() {
		int width = getImageWidth();
		int height = getImageHeight();
		setDrawingRange(0, width);
		return getBufferedImage(width, height);
	}
	
	protected final BufferedImage getBufferedImage(int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_3BYTE_BGR);
		this.width = width;
		this.height = height;
		paint(bufferedImage.createGraphics());
		return bufferedImage;
	}

	public String getInformationAtMousePosition(Point point) {		
		return tp.getPaintingPanel().getListOfProbesAtMousePosition(point);
	}
	
	protected final void setDrawingRange(int b, int e){
		beg_x = b;
		end_x = e;
	}
	
	public final void renderWindow(Graphics g, int start, int end, int Width, int Height) {
		setDrawingRange(start, end);
		this.width = Width;
		this.height = Height;
		paint(g);
	}
	
	public final void render(Graphics g, int Width, int Height) {
		int start, end;
		if(!((Track)tp.getTrack()).isShowing()){
			start = 0;
			end = Width+1;
		} else{
			start = chromeModel.getVis_leftPos_x();
			end = chromeModel.getVis_rightPos_x();
		}
		renderWindow(g,start,end,Width,Height);
	}
	
	public MouseListener getMouseClickHandler() {
		return null;
	}
}
