package mayday.vis3.plots.genomeviz.genomeoverview.controllercollection;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.UserPanel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.scale.ScaleTrackPanel;

public class Controller_upc extends AbstractController implements MouseMotionListener, MouseListener {
	
	/**
	 * constructor for userpanel component.
	 * @param Model
	 * @param C
	 */
	public Controller_upc(GenomeOverviewModel Model,Controller C){
		super(Model, C);

	}
	
	public void mouseClicked(MouseEvent e) {
		
		ITrack at = getAbstractTrack(e);
		oph.setSelectedTrack(at, model.getSelectionModel());
		
		setKindOfButton(e);
	}
	
	public void mouseDragged(MouseEvent e) {
		draggedFlag = true; 
		ITrack at = getAbstractTrack(e);
		oph.movePanelToFront(at, e.getButton());
		oph.moveComponentByDragging(at, e);
	}


	public void mouseMoved(MouseEvent e) {
	
	}
	
	

	public void mouseEntered(MouseEvent arg0) {
		
	}

	public void mouseExited(MouseEvent arg0) {
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
	}


	public void mouseReleased(MouseEvent e) {
		ITrack at = getAbstractTrack(e);
		oph.setSelectedTrack(at, model.getSelectionModel());
		oph.maybeShowPopup(e, c, c.getMenuModel(), model.getSelectionModel());
		oph.movePanelByRealeased(at, pressedflag, leftmouse);
		oph.setMovedFlag(at, false);
		
		draggedFlag = false;
		pressedflag = false;
		rightmouse = false;
		leftmouse = false;
	}
	
	protected ITrack getAbstractTrack(MouseEvent e){
		if(Track.isScaleTrack(e.getSource())){
			ScaleTrackPanel spp = (ScaleTrackPanel)e.getSource();
			return (ITrack)spp.getParent();
		} else if(e.getSource() instanceof UserPanel){
			UserPanel up = (UserPanel)e.getSource();
			return (ITrack)up.getParent();
		}
		return null;
	}
}
