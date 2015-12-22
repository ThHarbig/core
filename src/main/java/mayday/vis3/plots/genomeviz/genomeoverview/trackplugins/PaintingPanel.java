package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.caching.TileCache;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.GetSelectedProbes;

@SuppressWarnings("serial")
public class PaintingPanel extends JPanel{

	protected GenomeOverviewModel model = null;
	protected AbstractTrackPlugin tp = null;
	
	protected Double from = null;
	protected Double to = null;
	protected String loadingString = "rendering...";
	protected BufferedImage image = null;
	protected Set<Probe> toolboxset = null;
	
	protected FromToPosition ftp;
	
	protected Rectangle2D.Double r2d = null;
	protected Rectangle2D.Double r2d_p = null;
	protected Rectangle2D.Double r2d_m = null;
	protected Set<Probe> set = null;

	public PaintingPanel(GenomeOverviewModel Model, AbstractTrackPlugin Tp){
		model = Model;
		tp = Tp;

		ftp = new FromToPosition();
		
		addIndividualListener();
		resizeInternalPaintingPanel();
	}
	

	protected int getDefaultWidth() {
		return model.getWidth_paintingpanel_reduced();
	}
	
	protected int getDefaultHeight() {
		return tp.getTrack().getHeight() -20;
	}
	
	public void setRange(){
		if(from != null && to != null){
			if(this.from > this.to){
				double val = from;
				from = to;
				to = val;
			} else if(from == to){
				to = to+1.;
			}
			model.setFromToPosition_RangeSelection(Math.round(from),Math.round(to));
		}
	}
	
	public void addIndividualListener() {
		Controller c = (Controller)model.getController();
		if(c!=null){
			addMouseListener(c.getController_pp());
			addMouseMotionListener(c.getController_pp());
		}
	}
	
	
	protected String getListOfProbesAtMousePosition(Point point) {

		if(tp.getTrackSettings().getColorProvider()!=null){
				fillListWithProbes(point);
				
				DataMapper.getBpOfView(getWidth(), model, point.getX(),ftp);
				
				String toolBoxText = "";
				if (toolboxset!=null && !toolboxset.isEmpty()) {
					toolBoxText = PaintingPanel.this.createToolboxText(
							toolboxset, ftp.getFrom(), ftp.getTo());
				} else {
					if (ftp.getFrom() == ftp.getTo()) {
						toolBoxText = "Probes: (0) " + "loc: " + ftp.getFrom()
							 + " (" + 1 + "bp) " + point.getX();
					} else {
						toolBoxText = "Probes: (0) " + "loc: " + ftp.getFrom()
								+ "-" + ftp.getTo() + " ("
								+ ((ftp.getTo() - ftp.getFrom() + 1)) + "bp)";
					}
				}					
				return toolBoxText;
		}
		return null;
	}
	
	protected void fillListWithProbes(Point point) {
		toolboxset = null;
		toolboxset = new HashSet<Probe>();
		toolboxset.addAll(GetSelectedProbes.getProbes(point, tp.getTrackSettings().getStrand(), this, model, ftp));
		toolboxset = Collections.synchronizedSet(toolboxset);			
	}
	
	private String createToolboxText(Set<Probe> list, long from, long to) {
		String toolBoxText = "";
		
		synchronized (list) {
			list = new HashSet<Probe>(list);
		}
		
		if(from==to){
			toolBoxText = "<html><body>Probes: ("+ list.size() +") " + "loc: " + from +"-"+to+  " (" + 1 +"bp)<br>";
		} else {
			toolBoxText = "<html><body>Probes: ("+ list.size() +") " + "loc: " + from +"-"+to+  " (" + (((to-from)+1)) + "bp)<br>";
		}
		
		int size = list.size();
		
		if(size > 10){
			int counter = 0;
			for(Probe p: list){
				if(counter == 0){
					toolBoxText = toolBoxText + p.getDisplayName() + " val: " + 
					p.getValue(tp.getTrackSettings().getExperimentForTooltip()) +"<br>";
					toolBoxText = toolBoxText + " ... <br>";
				}
				
				if(counter == size-1){
					toolBoxText = toolBoxText + p.getDisplayName() + " val: " 
					+ p.getValue(tp.getTrackSettings().getExperimentForTooltip()) +"<br>";
				}
				counter++;
			}
		} else {
			for(Probe p: list){
				toolBoxText = toolBoxText + p.getDisplayName() + " val: " 
				+ p.getValue(tp.getTrackSettings().getExperimentForTooltip()) + "<br>";
			}				
		}

		toolBoxText = toolBoxText + "</body></html>";
		return toolBoxText;
	}

	public void resizePanel() {
		resizeInternalPaintingPanel();
	}

	protected void resizeInternalPaintingPanel() {
		int width = getDefaultWidth();
		int height = getDefaultHeight();

		this.setPreferredSize(new Dimension(width,height));
		this.setSize(new Dimension(width,height));
	}
	
	public Set<Probe> getSelectedProbes(MouseEvent e) {
		return GetSelectedProbes.getProbes(e
				.getPoint(), tp.getTrackSettings().getStrand(), this, model, ftp);
	}

	public void searchForProbe() {
		
	}

	public void setFrom(Double From) {
		from = From;
	}

	public void setTo(Double To) {
		to = To;
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2_main = (Graphics2D) g;
		setOpaque(false);
//		g2_main.clearRect(0, 0, getWidth(), getHeight());

		if(tp.getTrack().isDraw()){
			if(!isShowing() || model.isDirectpaint()){
//				g2_main.setColor(Color.WHITE);
//				g2_main.fillRect(model.getVis_leftPos_x(), 0, (model.getVis_rightPos_x()-model.getVis_leftPos_x()+1), getHeight());
			} 
		}  else{
			g2_main.setColor(Color.black);
			int center = model.getCenterposition_x();
			g2_main.drawString(loadingString,(int) (center - (Math.min(g2_main.getFontMetrics().stringWidth(loadingString) >> 1, g2_main.getFontMetrics().stringWidth(loadingString)+ Math.abs(model.getWidth_paintingpanel_reduced() - center) - 1))),
					getHeight()/2 +2);
		}
		paintTrack(g2_main);
	}

	protected final void paintTrack(Graphics2D g2_main) {
		if (!tp.getTrack().isDraw())
			return;
		
		int h = getHeight();

		// get area to paint
		Rectangle r = model.getViewRectOfJLayeredPane();
		int left = r.x-1;
		int right = left + r.width;
		
		if(!isShowing() || model.isDirectpaint()){	

			// if exporting or directly painting, render directly
			tp.getTrackRenderer().renderWindow(g2_main, left, right, getWidth(), h);
			
		} else if ((getParent() instanceof Track)) {	

			Track tp = (Track)getParent();

			// if tiling is active, use tilecache
			TileCache c = tp.getTileCache();
			if (c!=null) {
				c.renderRegion(g2_main, left, right, 0, h);

			} else {
				image = tp.getBufferedImage();
				if (image != null) {
					// if image is available, use image
					g2_main.drawImage(
							image, 
							left, 0, right, h,
							left, 0, right, h, 
							this);
				} 
			}
		} else {
			// WTF??				
		}

	}

	public AbstractTrackPlugin getTrackPlugin() {
		return tp;
	}
	
}
