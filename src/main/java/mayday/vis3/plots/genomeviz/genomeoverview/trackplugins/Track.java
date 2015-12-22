package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JPanel;

import mayday.core.Probe;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.EnumManagerGO.ActionModes;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewLayeredPane;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.SelectionsModel;
import mayday.vis3.plots.genomeviz.genomeoverview.caching.TileCache;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.TrackPositioner;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.UserPanel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.scale.ScaleTrackPanel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.scale.ScaleTrackPlugin;

@SuppressWarnings("serial")
public class Track extends JPanel implements ITrack{

	protected GenomeOverviewModel model = null;	
	protected UserPanel userpanel = null;
	
	protected ActionModes mode = ActionModes.NOTHING;
	
	private Point p1 = new Point();
	private Point p2 = new Point();
	private Point prevLocation = new Point();
	
	protected int position_y = -1;
	protected int index = -1;
	protected boolean delete = false;

	public BufferedImage bufferedImage = null;

	public TileCache cache;

	protected AbstractTrackPlugin trackPlugin;
	
	protected boolean movedFlag = false;
	protected boolean draw = false;
	
	protected Color selPbColor = Color.BLUE;
	protected Color searchedPbColor = Color.RED;
	
	protected Polygon forwardSelectionMarker;
	protected Polygon backwardSelectionMarker;
	
	public Track(GenomeOverviewModel ChromeModel,
			int posY, int Index, int componentWidth, int trackHeight, AbstractTrackPlugin TrackPlugin){
		setOpaque(false);
		model = ChromeModel;
		trackPlugin = TrackPlugin;
		position_y = posY;
		index = Index;
		initialization(trackHeight);
		setDoubleBuffered(true);
	}
	
	private void initialization(int trackHeight) {
		Controller c = (Controller)model.getController();

		if(c!=null){
			addMouseListener(c.getController_tp());
			addMouseMotionListener(c.getController_tp());
		}
	
		setLayout(null);
		
		int width = model.getWidth_LayeredPane();
		
		this.setSize(width, trackHeight);
		this.setPreferredSize(new Dimension(width,trackHeight));
		this.setBounds(0, position_y, width, trackHeight);
		this.setBackground(Color.WHITE);
		userpanel = new UserPanel(model,this);
		add(userpanel);
		userpanel.setLocation(model.getLocation_userpanel_X(),model.getLocation_userpanel_Y());
	}
	
	public void init(){
		model.setLocationOfUserpanel(this);
		trackPlugin.actualizeTrack();
	}
	
	protected void internalInit(){
	
	}

	public void resetActionMode() {
		mode = ActionModes.NOTHING;
	}
	
	public void setActionMode(ActionModes Mode) {
		if(mode != Mode){
			mode = Mode;
			model.checkPanels(this);
		}
	}
	
	public ActionModes getActionMode() {
		return this.mode;
	}

	/**
	 * move this trackpanel towards passed position.
	 * @param newYpos
	 */
	private void movePanel(int newYpos) {
		TrackPositioner pp = Track.this.model.getPanelPositioner();
		pp.movePanel(Track.this,newYpos);
		position_y = newYpos;
	}
	
	private Component getOutermostComponent() {
		Component comp = Track.this;

		// get outermost component
		while (comp != null && !(comp instanceof GenomeOverviewLayeredPane)) {
			comp = comp.getParent();
		}
		return comp;
	}
	
	public int getPositionInPane(){
		return this.position_y;
	}
	
	public int getIndexInPane(){
		return index;
	}

	public void repositionPanel(){
		int width = model.getWidth_LayeredPane();
		int height = model.getHeight_trackpanel();
		int oldX = getBounds().x;
		this.setSize(new Dimension(width,height));
		this.setBounds(oldX,position_y,width,height);
	}
	
	public void setDeleteFlag(boolean b) {
		delete = b;
	}


	public boolean getDeleteFlag() {
		return delete;
	}
	
	public void setIndex(int newIndex) {
		index = newIndex;
	}
	
	public void setLocationInPanel(int positionInPanel) {
		position_y = positionInPanel;
		
		int width = model.getWidth_LayeredPane();
		int height = getHeight();
		int oldX = getBounds().x;
		setSize(new Dimension(width,height));
		setBounds(oldX,position_y,width,height);
	}
	
	public void resizeTrackwidth(){
		int width = model.getWidth_LayeredPane();
		int height = getHeight();
		
		int oldX = getBounds().x;
		int oldY = getBounds().y;
		
		setSize(new Dimension(width,height));
		setBounds(oldX,oldY,width,height);

	}

	public void deleteBufferedImage() {
		setDrawing(false);
		bufferedImage = null;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public void setPreviousTrackLocation(MouseEvent e, ITrack at) {
		p1.setLocation(e.getPoint());
		prevLocation = at.getLocation();
	}
	
	public void moveComponentByDragging() {
		Component comp = getOutermostComponent();

		if (comp != null) {
			// get Mouse position and move panel
			GenomeOverviewLayeredPane colp = ((GenomeOverviewLayeredPane) comp);

				prevLocation = this.getLocation();
				p2 = colp.getMousePosition();
				if(p2 != null){
					int newYpos = p2.y - p1.y;
					int unusableY = Track.this.model.getUnusableSpace_y();
					if (newYpos > unusableY) {
						Track.this.setLocation(0, newYpos);
					} else {
						Track.this.setLocation(0, prevLocation.y);
					}
				} else {
					Track.this.setLocation(0, prevLocation.y);
				}
		}
	}
	
	public void movePanelToFront(int button) {
		if(button == MouseEvent.BUTTON1){
        	if(Track.this.getParent() instanceof GenomeOverviewLayeredPane){
        		GenomeOverviewLayeredPane colp = (GenomeOverviewLayeredPane)Track.this.getParent();
        		colp.moveToFront(Track.this);
        	}
        }
	}

	public void movePanelByRealeased(boolean pressedflag) {
		if(pressedflag){
			Component comp = getOutermostComponent();

			if (comp != null && comp instanceof GenomeOverviewLayeredPane) {
	     
				// get Mouseposition and move panel
				GenomeOverviewLayeredPane golp = ((GenomeOverviewLayeredPane) comp);
				p2 = golp.getMousePosition();
				if(p2 != null){
					int newYpos = p2.y - p1.y;
					movePanel(newYpos);
				} else {
					if(prevLocation != null){
						movePanel(prevLocation.y);
					} 
				}
			} else {
				if(prevLocation != null){
					movePanel(prevLocation.y);
				}
			}
		}
	}
	
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}
	
	public void setNewLabel(String text) {
		userpanel.setLabelForPanel(text);
	}

	public void paintRange(int[] posOfFoundProbe) {
		this.repaint();
	}
	
	protected Set<Probe> selectedProbes;
	
	public void paint(Graphics g) {
		// hide selection frame on export
		if (!isShowing()) {
			setBorder(null);
		}
		
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D)g;
		if(trackPlugin !=null && trackPlugin.getTrackSettings() !=null && !Track.isScaleTrack(trackPlugin)){
			Strand trackStrand = trackPlugin.getTrackSettings().getStrand();
			if(trackStrand!=null){
				
				// indicate searched probe
				if(model.getSelectionModel().isDrawFoundProbe()){
					if(model.getSelectionModel().getFoundProbe_strand()!=null){
						if(trackStrand==model.getSelectionModel().getFoundProbe_strand() || trackStrand==Strand.BOTH){
							if(model.getSelectionModel().getPosOfFoundProbe()!=null){
								drawFoundProbe(g2d);
							}
						}
					}
				}
				
				// indicate selected probes
				if(!model.getSelectedProbes().isEmpty()){
					selectedProbes = model.getSelectedProbes();
					JPanel ppanel = trackPlugin.getPaintingPanel();				
					int ppWidth = ppanel.getWidth();
					int ppHeight = ppanel.getHeight();
					long viewStart = model.getViewStart();
					long viewEnd = model.getViewEnd();
					int beg_x = model.getVis_leftPos_x();
					int end_x = model.getVis_rightPos_x();
					g2d.setColor(selPbColor);
					Point location = ppanel.getLocation();
					int y = (int)location.getY();
					int offset= (int)location.getX();
					g2d.translate(offset, y);

					for(Probe pb: selectedProbes){
						Strand probeStrand=model.getStrandOfProbe(pb);
						if(probeStrand==trackStrand || trackStrand==Strand.BOTH){
							long startPos= model.getStartPosition(pb);
							long endPos= model.getEndPosition(pb);
							int startindex = DataMapper.getXPosition(startPos, ppWidth, viewStart, viewEnd);
							int endindex = DataMapper.getXPosition(endPos, ppWidth, viewStart, viewEnd);
							if (endindex>=beg_x && startindex <= end_x)
								drawSelectedProbes(g2d, startindex, endindex, trackStrand, probeStrand, ppHeight);	
						}
					}
					
					g2d.translate(-offset, -y);
					g2d.setClip(null);
				}
			}
		}
	}

	private void drawSelectedProbes(Graphics2D g2d, int startX, int endX, Strand trackStrand, Strand probeStrand, int ppHeight) {
			
		boolean plusTrack = trackStrand==Strand.PLUS;
		boolean minusTrack = trackStrand == Strand.MINUS;
		boolean anyTrack = trackStrand==Strand.BOTH;

		boolean plusProbe = probeStrand==Strand.PLUS || probeStrand==Strand.BOTH;
		boolean minusProbe = probeStrand==Strand.MINUS || probeStrand==Strand.BOTH;
		
		boolean drawPlusMarker = plusTrack || (anyTrack && plusProbe) ;
		boolean drawMinusMarker = minusTrack || (anyTrack && minusProbe) ;
		
		if (drawPlusMarker) 
			putForwardPolygon(g2d, startX, -1, endX-startX);
		if (drawMinusMarker)
			putBackwardPolygon(g2d, startX, ppHeight+1, endX-startX);
		
	}
	

	protected void putForwardPolygon(Graphics2D g, int x, int y, int stretch) {
		if (forwardSelectionMarker==null) {
			int[] xcoords = {-4, 0, 0, 4};
		    int[] ycoords = {-4, 0, 0, -4};
		    forwardSelectionMarker = new Polygon(xcoords,ycoords,xcoords.length);			
		}
		// move painting region
		g.translate(x, y);
		// stretch the marker
		forwardSelectionMarker.xpoints[2]+=stretch;
		forwardSelectionMarker.xpoints[3]+=stretch;
		g.fillPolygon(forwardSelectionMarker);
		// undo stretch
		forwardSelectionMarker.xpoints[2]-=stretch;
		forwardSelectionMarker.xpoints[3]-=stretch;
		// restore painting region
		g.translate(-x, -y);
	}
	
	protected void putBackwardPolygon(Graphics2D g, int x, int y, int stretch) {
		if (backwardSelectionMarker==null) {
			int[] xcoords = {-4, 0, 0, 4};
		    int[] ycoords = {4, 0, 0, 4};
		    backwardSelectionMarker = new Polygon(xcoords,ycoords,xcoords.length);			
		}		
		// move painting region
		g.translate(x, y);
		// stretch the marker
		backwardSelectionMarker.xpoints[2]+=stretch;
		backwardSelectionMarker.xpoints[3]+=stretch;
		g.fillPolygon(backwardSelectionMarker);
		// undo stretch
		backwardSelectionMarker.xpoints[2]-=stretch;
		backwardSelectionMarker.xpoints[3]-=stretch;
		// restore painting region
		g.translate(-x, -y);
	}

	/**
	 * marks the selected probes which was searched in the containing tracks.
	 * @param g2d
	 */
	private void drawFoundProbe(Graphics2D g2d) {
		g2d.setColor(searchedPbColor);
		
		SelectionsModel slmodel = model.getSelectionModel();
		int first_x = slmodel.getPosOfFoundProbe()[0] - 1;
		int last_x = slmodel.getPosOfFoundProbe()[1] + 1;
		int width = last_x-first_x;
		first_x = first_x + model.getLocation_paintingpanel_X();
		last_x = first_x +width;
		if(first_x < (getFirstPaintablePosition())){
			first_x = (int)(getFirstPaintablePosition());
			width = last_x-first_x;
		}
		
		if(trackPlugin.getTrackSettings().getStrand().equals(Strand.BOTH)){
			if(first_x<last_x){
				if(model.getSelectionModel().getFoundProbe_strand().equals(Strand.PLUS)){
					g2d.drawRect(first_x, 1, width, (int)Math.floor((double)getHeight()/2.));
				} else if(model.getSelectionModel().getFoundProbe_strand().equals(Strand.MINUS)){
					g2d.drawRect(first_x, (int)Math.ceil((double)getHeight()/2.), width, (int)Math.ceil((double)getHeight()/2.)-2);
				}
			}
		}else{
			g2d.drawRect(first_x,1, width, getHeight()-3);
		}
	}

	/**
	 * sets the location of userpanel.
	 */
	public void setLocationOfUserpanel() {
		double d = model.getVisibleRectOfLayeredPane().getX();
		userpanel.setNewSize();
		userpanel.setLocation((int)d,model.getLocation_userpanel_Y());
		setInternalPanelLocation(d);
	}
	
	/**
	 * extending track must override this method to specify special positionings.
	 * @param d
	 */
	protected void setInternalPanelLocation(double d){
		
	}

	/**
	 * sets a flag if track is moved.
	 */
	public boolean isMovedFlag() {
		return movedFlag;
	}

	/**
	 * sets the flag in case of track movement.
	 */
	public void setMovedFlag(boolean movedFlag) {
		this.movedFlag = movedFlag;
	}

	/**
	 * used to identify tracks which are ready to be painted.
	 */
	public boolean isDraw() {
		return draw;
	}
	
	/**
	 * sets the drawing flag, which is used to identify tracks which are ready to be painted.
	 */
	public void setDrawing(boolean b) {
		draw = b;
	}

	/**
	 * resizes the hight of tracks and relocates the userpanel.
	 */
	public void resizeTrackheight(int newHeight) {
		int width = model.getWidth_LayeredPane();
		int height = newHeight;
		
		int oldX = getBounds().x;
		int oldY = getBounds().y;
		
		this.setSize(new Dimension(width,height));
		this.setBounds(oldX,oldY,width,height);
		userpanel.setNewSize();
	}
	
	/**
	 * return AbstractTrackPlugin
	 */
	public AbstractTrackPlugin getTrackPlugin(){
		return trackPlugin;
	}
	
	/**
	 * 
	 */
	public void setBufferedImage(BufferedImage img){
		bufferedImage=img;
	}
	
	/**
	 * 
	 */
	public void setTileCache(TileCache c) {
		cache = c;
	}
	
	/**
	 * 
	 */
	public TileCache getTileCache() {
		return cache;
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public static boolean isScaleTrack(ITrack t) {
		return ScaleTrackPlugin.class.isAssignableFrom(t.getTrackPlugin().getClass());
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isScaleTrack(Object c) {
		if (c instanceof ITrack)
			return isScaleTrack((ITrack)c);
		if (c instanceof ScaleTrackPanel)
			return true;
		if (c instanceof ScaleTrackPlugin)
			return true;
		return false;
	}
	
	/**
	 * returns the first index in track which is available for painting (because its covered from userpanel).
	 * @return
	 */
	private int getFirstPaintablePosition(){
		return (int)(userpanel.getLocation().getX() + userpanel.getWidth()-1);
	}
	
}
