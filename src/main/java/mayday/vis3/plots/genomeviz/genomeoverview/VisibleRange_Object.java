package mayday.vis3.plots.genomeviz.genomeoverview;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.EventFirer;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;

public class VisibleRange_Object {

	protected GenomeOverviewModel model;
	protected GenomeOverviewLayeredPane layeredPane = null;
	
	protected int vis_centerPos_x = -1;		// center x position of visible track
	protected int vis_leftPos_x = -1;	
	protected int vis_rightPos_x = -1;	
	protected long vis_centerPos_bp = -1;	// center bp position of visible track
	protected long vis_leftPos_bp = -1;		// center bp position of visible track
	protected long vis_rightPos_bp = -1;		// center bp position of visible track
	
	protected FromToPosition ftp;
	protected int centerPos_visrect = -1;	// centerposition of visible rect
	protected int leftPos_visrect = -1;		// left of visible rect
	protected int rightPos_visrect = -1;	// right of visible rect
	
	protected long scale = 1;
	
	protected EventFirer<ChangeEvent, ChangeListener> firer = new EventFirer<ChangeEvent, ChangeListener>() {
		protected void dispatchEvent(ChangeEvent event, ChangeListener listener) {
			listener.stateChanged(event);
		}
	};
	
	public VisibleRange_Object(){
		
	}
	
	public void init(GenomeOverviewModel Model, GenomeOverviewLayeredPane LayeredPane){
		model = Model;
		layeredPane = LayeredPane;
		ftp = new FromToPosition();
		update();
	}

	public int getCenterposition_x() {
		return vis_centerPos_x;
	}

	public void update(){
		computeVisRectPositions();
		computeVisibleTrackPositions_x();
		computeVisibleTrackPositions_bp();
		computeScale();
		firer.fireEvent(new ChangeEvent(this));
	}

	private void computeScale() {
		DataMapper.getBpOfView(model.getWidth_paintingpanel_reduced(), model, vis_centerPos_x,ftp);
		scale = (ftp.getTo()-ftp.getFrom() +1);
	}

	private void computeVisibleTrackPositions_x() {
		int width_userpanel = model.getWidth_userpanel();
		
		if(leftPos_visrect>=width_userpanel){
			vis_leftPos_x = leftPos_visrect;
			vis_rightPos_x = rightPos_visrect-width_userpanel;
			vis_centerPos_x=(int)vis_leftPos_x+(int)(Math.round((vis_rightPos_x-vis_leftPos_x)/2.));
		} else{
			vis_leftPos_x = 0+leftPos_visrect;
			vis_rightPos_x = rightPos_visrect-width_userpanel;
			vis_centerPos_x=(int)vis_leftPos_x+(int)(Math.round((vis_rightPos_x-vis_leftPos_x)/2.));
		}
		vis_leftPos_x = vis_leftPos_x-1;
		if(vis_leftPos_x<0)vis_leftPos_x=0;
		vis_centerPos_x = vis_centerPos_x-1;
		vis_rightPos_x = vis_rightPos_x-1;
		
//		System.out.println("vis_leftPos_x " + vis_leftPos_x + " vis_rightPos_x " + vis_rightPos_x);
	}

	private void computeVisibleTrackPositions_bp() {

		DataMapper.getBpOfView(model.getWidth_paintingpanel_reduced(), model, vis_centerPos_x,ftp);
		vis_centerPos_bp = (int)ftp.getFrom() + (int)Math.abs((double)(ftp.getTo()-ftp.getFrom())/2.);
		
		DataMapper.getBpOfView(model.getWidth_paintingpanel_reduced(), model, vis_leftPos_x,ftp);
		vis_leftPos_bp = (int)ftp.getFrom(); 

		
		
		ftp.clear();
		DataMapper.getBpOfView(model.getWidth_paintingpanel_reduced(), model, vis_rightPos_x,ftp);
		vis_rightPos_bp = (int)ftp.getTo();
//		System.out.println("vis_leftPos_bp " + vis_leftPos_bp + " vis_rightPos_bp " + vis_rightPos_bp);
	}

	public long getCenterposition_bp() {
		return vis_centerPos_bp;
	}
	
	private void computeVisRectPositions(){
		if(layeredPane!=null
				&& layeredPane.getVisibleRect()!=null){
				leftPos_visrect = layeredPane.getVisibleRect().x;
				rightPos_visrect = layeredPane.getVisibleRect().x + layeredPane.getVisibleRect().width - 1;
				centerPos_visrect = (int)leftPos_visrect+(int)(Math.round((rightPos_visrect-leftPos_visrect)/2.));
		} else{
			centerPos_visrect = -1;
		}
		
	}

	public long getVisPos_low_bp() {
		return vis_leftPos_bp;
	}

	public long getVisPos_high_bp() {
		return vis_rightPos_bp;
	}

	public int getVis_centerPos_x() {
		return vis_centerPos_x;
	}

	public int getVis_leftPos_x() {
		return vis_leftPos_x;
	}

	public int getVis_rightPos_x() {
		return vis_rightPos_x;
	}

	public long getScale() {
		return scale;
	}
	
	public void addListener(ChangeListener cl ) {
		firer.addListener(cl);
	}
	
	public void removeListener(ChangeListener cl) {
		firer.removeListener(cl);
	}
}
