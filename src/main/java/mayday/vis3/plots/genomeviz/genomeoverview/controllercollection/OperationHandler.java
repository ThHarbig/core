package mayday.vis3.plots.genomeviz.genomeoverview.controllercollection;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashSet;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.EnumManagerGO.SizeMode;
import mayday.vis3.plots.genomeviz.genomeoverview.ConstantData;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewComponent;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewLayeredPane;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.MyPlotScrollPane;
import mayday.vis3.plots.genomeviz.genomeoverview.SelectionsModel;
import mayday.vis3.plots.genomeviz.genomeoverview.menues.MenuModel;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.UserPanel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.PaintingPanel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;

public class OperationHandler {

	protected GenomeOverviewModel model;
	protected int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	public OperationHandler(GenomeOverviewModel Model){
		model = Model;
	}

	public void maybeShowPopup(MouseEvent e, AbstractController ac, MenuModel menuModel, SelectionsModel selectionsModel) {
		boolean initialized = ac.initialized;
		int button = e.getButton();

		if(initialized){
			if (e.getSource() instanceof GenomeOverviewLayeredPane
					|| e.getSource() instanceof MyPlotScrollPane
					|| e.getSource() instanceof PaintingPanel
					|| e.getSource() instanceof UserPanel
					|| e.getSource() instanceof ITrack) {
				if (button == MouseEvent.BUTTON3) {
					maybeShowPopup(e, menuModel);
				}
			}  
		}
	}

	public void setSelectedProbes(MouseEvent e, AbstractController ac, SelectionsModel selectionsModel) {
		boolean initialized = ac.initialized;
		int button = e.getButton();

		if(e.getSource() instanceof GenomeOverviewLayeredPane && initialized && (button == MouseEvent.BUTTON3)){
			selectionsModel.setSelectedProbes(new HashSet<Probe>());
		} else if(e.getSource() instanceof MyPlotScrollPane && initialized && (button == MouseEvent.BUTTON3)){
			selectionsModel.setSelectedProbes(new HashSet<Probe>());
		} else if(e.getSource() instanceof PaintingPanel && initialized && (button == MouseEvent.BUTTON3)){
			selectionsModel.setSelectedProbes(((PaintingPanel)e.getSource()).getSelectedProbes(e));
		}
	}

	public void setSelectedTrack(ITrack at,	SelectionsModel selectionModel) {
		selectionModel.setSelectedTrack(at);
	}

	protected void maybeShowPopup(MouseEvent e, MenuModel menuModel) {
		if (e.isPopupTrigger()){

			// source is layeredPane
			if (e.getSource() instanceof GenomeOverviewLayeredPane){
				menuModel.openPopupMenu_Background(e.getComponent(), e.getX(), e.getY());
			}

			// source is abstractTrack but NOT a scaleTrack
			if(e.getSource() instanceof ITrack && Track.isScaleTrack(e.getSource())){
				menuModel.openPopupMenu_Track(e.getComponent(), e.getX(), e.getY());
			}

			// source is AbstractPaintingPanel
			if(e.getSource() instanceof PaintingPanel){
				PaintingPanel paintingpanel = (PaintingPanel)e.getSource();
				if(paintingpanel.getParent() instanceof ITrack && !Track.isScaleTrack(e.getSource())){
					menuModel.openPopupMenu_Track(e.getComponent(), e.getX(), e.getY());
				}
			}

			// source is the scrollPane
			if(e.getSource() instanceof MyPlotScrollPane){
				menuModel.openPopupMenu_Background(e.getComponent(), e.getX(), e.getY());
			}

			if(e.getSource() instanceof UserPanel){
				UserPanel up = (UserPanel)e.getSource();
				if(!Track.isScaleTrack(up.getParent())){
					menuModel.openPopupMenu_Track(e.getComponent(), e.getX(), e.getY());
				}
			}

		}

	}

	public void mouseWheelOperations(MouseWheelEvent e, boolean ctrl_pressed, boolean shift_pressed) {

		if (ctrl_pressed && !shift_pressed) {
			if (e.getWheelRotation() > 0) {
				// in-zoom | change width
				mouseZoom(false, true, false);
			} else {
				mouseZoom(true, true, false);
			}
			
		} else if (!ctrl_pressed && shift_pressed){

			// get number of "clicks"
			if (e.getWheelRotation() > 0) {
				// in-zoom | change width | change height
				mouseZoom(false, false, true);
			} else {
				mouseZoom(true, false, true);
			}
		}
	}


	/** 
	 * For zooming of boxes.
	 * @param inZoom
	 * @param resizeWidth if width is resized
	 */
	public void mouseZoom(boolean inZoom, boolean resizeWidth, boolean resizeHeight){
		boolean repaint = false;
		GenomeOverviewComponent comp = (GenomeOverviewComponent) model.getComponent();

		long center_bp = model.getCenterposition_bp();
		int center_x = model.getCenterposition_x();

		if(resizeWidth == true){
			resizeWidth(inZoom);
			if(model.isWidthChanged()){
				model.centerView(center_bp,center_x);
				model.computeVisiblePositions();
				model.getSelectionModel().setDrawFoundProbe(false);
				center_bp = model.getCenterposition_bp();
				center_x = model.getCenterposition_x();
			}
		}
		else if(resizeHeight == true){
			repaint = resizeHeight(inZoom);
			ITrack panel = (ITrack)model.getSelectionModel().getSelectedTrack();
			if(comp!= null){
				if(repaint){
					panel.getTrackPlugin().actualizeTrack();
					//					model.actualizeTrack(panel);
					model.fireChanged();
				}
			} else {
				panel.getTrackPlugin().actualizeTrack();
				//				model.actualizeTrack(panel);
				model.fireChanged();
			}		

			model.centerView(center_bp,center_x);
			model.computeVisiblePositions();
			model.getSelectionModel().setDrawFoundProbe(false);
			center_bp = model.getCenterposition_bp();
			center_x = model.getCenterposition_x();
		}	
	}


	private boolean resizeHeight(boolean inZoom) {
		ITrack tps = (ITrack)model.getSelectionModel().getSelectedTrack();
		if (tps==null)
			return false;
		int actheight = tps.getHeight();
		int percent = (int)Math.floor((double)actheight/10.);
		int newHeight = 0;
		if(inZoom){
			newHeight = actheight + percent;
		} else{
			newHeight = actheight - percent;
		}


		if(newHeight < ConstantData.INITIAL_TRACK_HEIGHT){
			newHeight = ConstantData.INITIAL_TRACK_HEIGHT;
		}

		if(newHeight != actheight){
			model.getPanelPositioner().resizeTrackheight(tps, newHeight);
			return true;
		} 

		return false;
	}

	private void resizeWidth(boolean inZoom) {
		int actwidth = model.getWidth_LayeredPane();
		int percent = (int)Math.floor((double)actwidth/10.);
		int newWidth = 0;
		if(inZoom){
			newWidth = actwidth + percent;
		} else{
			newWidth = actwidth - percent;
		}
		model.setSizeLayeredPane(newWidth, SizeMode.SIZE_ZOOM);
	}

	public void movePanelToFront(ITrack track, int i) {
		if(track!=null)track.movePanelToFront(i);
	}

	public void setPreviousTrackLocation(ITrack at, MouseEvent e) {
		if (at!=null && 
				(e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
			at.setPreviousTrackLocation(e, at);
			at.setMovedFlag(true);
			model.setFixSize_layeredPane(true);
			model.resetHeight_layeredPane();
		}
	}

	public void moveComponentByDragging(ITrack at, MouseEvent e) {
		if (at != null 
				&& at.isMovedFlag()
				&& (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) {
			at.moveComponentByDragging();
		}
	}

	public void movePanelByRealeased(ITrack at, boolean pressedflag, boolean leftmouse) {
		if(at!=null && leftmouse && at.isMovedFlag()){
			at.movePanelByRealeased(pressedflag);
		}
		model.setFixSize_layeredPane(false);
		model.setSizeLayeredPane();
	}

	public void setMovedFlag(ITrack tp, boolean b) {
		if(tp!=null){
			tp.setMovedFlag(b);
		}
	}
}

