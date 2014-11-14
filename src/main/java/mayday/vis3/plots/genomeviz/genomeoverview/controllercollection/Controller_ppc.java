package mayday.vis3.plots.genomeviz.genomeoverview.controllercollection;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.EnumManagerGO.Fixed;
import mayday.vis3.plots.genomeviz.EnumManagerGO.ProbeSelection;
import mayday.vis3.plots.genomeviz.EnumManagerGO.SizeMode;
import mayday.vis3.plots.genomeviz.EnumManagerGO.TrackPanelMode;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.GetSelectedProbes;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.LayeredPane_Operations;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.PaintingPanel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.scale.ScaleTrackPanel;

public class Controller_ppc extends AbstractController implements MouseListener,MouseMotionListener {

	protected TrackPanelMode tp_mode = TrackPanelMode.TP_DEFAULT;
	
	protected int visLowPos_new_x = 0;
	protected int visHighPos_new_x = 0;
	
	protected double visLowPos_new_bp = 0.;
	protected double visHighPos_new_bp = 0.;
	
	protected Fixed fixed = null;
	double newWidthLayeredPane = 0;

	public Controller_ppc(GenomeOverviewModel Model,Controller C){
		super(Model, C);
		
	}
	
	public void mouseClicked(MouseEvent e) {

		ITrack at = getAbstractTrack(e);
		oph.setSelectedTrack(at, model.getSelectionModel());
		setKindOfButton(e);
		
		if (!Track.isScaleTrack(at)) {
			
			AbstractTrackPlugin apl = at!=null?at.getTrackPlugin():null;
			ITrackRenderer itr = apl!=null?apl.getTrackRenderer():null;
			MouseListener ml = itr!=null?itr.getMouseClickHandler():null;

			if (ml==null) {
				// default click handling
				if(e.getButton() == MouseEvent.BUTTON1){
					if((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0
							&& (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0){
						setSelectedProbes(e, ProbeSelection.SINGLE_SEL);
						model.getSelectionModel().setSelectedPositon_first(e.getPoint().getX());
						model.getSelectionModel().setSelectedPositon_last(-1.);
					}
					
					else if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0
							&& (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0) {
						setSelectedProbes(e, ProbeSelection.CTRL_SEL);
						model.getSelectionModel().setSelectedPositon_first(e.getPoint().getX());
						model.getSelectionModel().setSelectedPositon_last(-1.);
					}
					
					else if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0
							&& (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0) {
						
						if(model.getSelectionModel().getSelectedXPos_first()>= 0.){
							model.getSelectionModel().setSelectedPositon_last(e.getPoint().getX());
							setSelectedProbes(e, ProbeSelection.SHIFT_SEL);
						}
					}
				}
			} else {
				ml.mouseClicked(e);
			}
			
			
		}
	}
	
	private boolean computeNewWidthOfLayeredPane() {
		double neededVisible_bp = visHighPos_new_bp-visLowPos_new_bp+1;
		newWidthLayeredPane = LayeredPane_Operations.getNeededWidthLayeredPane(model, neededVisible_bp);
		if(newWidthLayeredPane > model.getMaximalTrackWidth()){
			newWidthLayeredPane = model.getMaximalTrackWidth();
		}
		return true;
	}
	
	
	
	protected FromToPosition getClickedPosition(MouseEvent e){

		if (Track.isScaleTrack(e.getSource())){
			ScaleTrackPanel app = ((ScaleTrackPanel)e.getSource());
			FromToPosition ftp = new FromToPosition();
			DataMapper.getSelectedPosition(app.getWidth(), app.getLeft_margin(), app.getRight_margin(), e.getX(), ftp, model);
			return ftp;
		}
		return null;
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	
	public void mouseExited(MouseEvent e) {
		draggedFlag = false;
	}

	
	public void mousePressed(MouseEvent e) {
		pressedflag = true;
		setKindOfButton(e);
		
		ITrack at = getAbstractTrack(e);
		oph.setSelectedTrack(at, model.getSelectionModel());
		oph.movePanelToFront(at, e.getButton());
		oph.setPreviousTrackLocation(at, e);
		oph.maybeShowPopup(e, c, c.getMenuModel(), model.getSelectionModel());
		
		if(Track.isScaleTrack(at)){
			visLowPos_new_x = visHighPos_new_x = e.getX();
			first = getClickedPosition(e);
			((ScaleTrackPanel)e.getSource()).drawLine(true, (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK));
		}  else {
			if (e.getSource() instanceof PaintingPanel) {
				PaintingPanel absPanel = (PaintingPanel) e.getSource();
		
				if (e.getButton() == MouseEvent.BUTTON1) {

					if (model.getRangeFlag()) {
						DataMapper.getBpOfView(absPanel.getWidth(), model, e
										.getPoint().getX(),ftp);
						if (ftp.isValid()) {
							absPanel.setFrom((double) ftp.getFrom());
						} else
							absPanel.setFrom(null);
					}

					if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0
							&& (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0) {
						model.getSelectionModel().setSelectedPositon_first(e
								.getPoint().getX());
					}
				}
			} 
		}
	}

	
	public void mouseReleased(MouseEvent e) {
		ITrack at = getAbstractTrack(e);
		oph.setSelectedTrack(at, model.getSelectionModel());
		oph.movePanelByRealeased(at, pressedflag, leftmouse);
		oph.maybeShowPopup(e, c, c.getMenuModel(), model.getSelectionModel());
		oph.setMovedFlag(at, false);
	
		if (Track.isScaleTrack(at)) {
			((ScaleTrackPanel)e.getSource()).drawLine(false, (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK));
			visHighPos_new_x = e.getX();
			ScaleTrackPanel spp = (ScaleTrackPanel) e.getSource();
			if (spp.getParent() instanceof ITrack) {
				ITrack st = (ITrack) spp.getParent();

				if (!st.isMovedFlag()) {
					if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0
							&& (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0) {
						zoomingAction(e);
					}
				}
			}
		} else{
				PaintingPanel pp = (PaintingPanel) e.getSource();
				if (model.getRangeFlag()) {
					DataMapper.getBpOfView(pp.getWidth(), model, e.getPoint().getX(),ftp);
					if (ftp.isValid()) {
						pp.setTo((double) ftp.getTo());
					} else
						pp.setTo(null);
					pp.setRange();
				}
				
				
				if (e.getButton() == MouseEvent.BUTTON1) {
					if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0) {
						if (e.getSource() instanceof PaintingPanel) {
							if (draggedFlag) {
								model.getSelectionModel().clearSelectedProbes();
								model.getSelectionModel().setSelectedPositon_last(e.getPoint().getX());
								setSelectedProbes(e, ProbeSelection.SHIFT_SEL);
							}
						}
					}
				}
		}
		
		draggedFlag = false;
		pressedflag = false;
		rightmouse = false;
		leftmouse = false;
	}


	private void zoomingAction(MouseEvent e) {
		if (draggedFlag) {
			
			last = getClickedPosition(e);

			if (last.getFrom() >= 1. && last.getTo() >= 1.
					&& first.getFrom() >= 1. && first.getTo() >= 1.) {

				if (last.getFrom() < first.getFrom()) {
					visLowPos_new_bp = last.getFrom();
					visHighPos_new_bp = first.getTo();
				} else if (last.getFrom() > first.getFrom()) {
					visLowPos_new_bp = first.getFrom();
					visHighPos_new_bp = last.getTo();
				}

				boolean widthValid = computeNewWidthOfLayeredPane();

				if (widthValid) {
					model.resizeLayeredPane(
							(int) visLowPos_new_bp,
							(int) visHighPos_new_bp,
							(int) newWidthLayeredPane,
							SizeMode.SIZE_ZOOM);
				} else {
					System.out.println("Width not valid");
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						model
								.repositionVisibleRect_mouseClicked(null);
					}
				});
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		draggedFlag = true;
		ITrack at = getAbstractTrack(e);
		oph.movePanelToFront(at, e.getButton());
		oph.moveComponentByDragging(at, e);
		
		if(Track.isScaleTrack(at)){
			((ScaleTrackPanel)e.getSource()).drawLine(true, (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK));
			visHighPos_new_x = e.getX();
			at.repaint();
		}		
	}


	public void mouseMoved(MouseEvent e) {
		
		if(e.getSource() instanceof PaintingPanel){
			PaintingPanel app = (PaintingPanel)e.getSource();
			ITrackRenderer itr = app.getTrackPlugin().getTrackRenderer();
			if (itr!=null)
				app.setToolTipText(itr.getInformationAtMousePosition(e.getPoint()));
		} 
		
		// update marker line when mouse moves over a track
		model.updateMouseLinePosition(e.getXOnScreen());
	}	

	
	
	private void setSelectedProbes(MouseEvent e, ProbeSelection sel) {
		
		if (!model.getRangeFlag()) {
			PaintingPanel panel = (PaintingPanel) e.getSource();
			if (panel.getParent() instanceof ITrack) {

				ITrack tp = (ITrack) panel.getParent();
				if(tp.getTrackPlugin().getTrackSettings().getColorProvider() != null){
					if (sel.equals(ProbeSelection.SINGLE_SEL)) {
						Set<Probe> set = GetSelectedProbes.getProbes(e
								.getPoint(), tp.getTrackPlugin().getTrackSettings().getStrand(), panel, model, ftp);

						model.getViewModel().setProbeSelection(set);

					} else if (sel.equals(ProbeSelection.SHIFT_SEL)) {

						int firstX = (int) model.getSelectionModel()
								.getSelectedXPos_first();
						int lastX = (int) model.getSelectionModel()
								.getSelectedXPos_last();

						if (firstX >= 0. && lastX >= 0.) {

							if (firstX > lastX) {
								int val = firstX;
								firstX = lastX;
								lastX = val;
							}

							Set<Probe> set = new HashSet<Probe>();

							for (int i = firstX; i <= lastX; i++) {
								Set<Probe> tempSet = GetSelectedProbes
										.getProbes(i, tp.getTrackPlugin().getTrackSettings().getStrand(),
												panel, model);
								if (!tempSet.isEmpty()) {
									set.addAll(tempSet);
								}
							}

							model.getViewModel().setProbeSelection(set);
						}
					} else if (sel.equals(ProbeSelection.CTRL_SEL)) {
						Set<Probe> set = GetSelectedProbes.getProbes(e
								.getPoint(), tp.getTrackPlugin().getTrackSettings().getStrand(), panel
								,model, ftp);
						model.getViewModel().toggleProbesSelected(set);
					}
				}
			}
		}
	}


	public int getEndDragged() {
		return visHighPos_new_x;
	}


	public int getStartDragged() {
		return visLowPos_new_x;
	}
	
	protected ITrack getAbstractTrack(MouseEvent e){
		if(e.getSource() instanceof PaintingPanel){
			PaintingPanel app = (PaintingPanel)e.getSource();
			if(app.getParent() instanceof ITrack)
				return (ITrack)app.getParent();
		}
		return null;
	}

}
