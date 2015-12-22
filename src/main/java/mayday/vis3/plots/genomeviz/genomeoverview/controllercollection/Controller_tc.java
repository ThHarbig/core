package mayday.vis3.plots.genomeviz.genomeoverview.controllercollection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.vis3.plots.genomeviz.EnumManagerGO.ActionModes;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;

public class Controller_tc extends AbstractController implements MouseListener, ActionListener, ChangeListener, MouseMotionListener, MouseWheelListener {
	
	/**
	 * constructor for track component controller, catch all events for the tracks here.
	 * @param Model
	 * @param C
	 */
	public Controller_tc(GenomeOverviewModel Model,Controller C){
		super(Model, C);

	}

	
	public void mouseClicked(MouseEvent e) {
		ITrack at = getAbstractTrack(e);
		oph.setSelectedTrack(at, model.getSelectionModel());

		setKindOfButton(e);
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	
	public void mouseExited(MouseEvent e) {
		draggedFlag = false;
	}

	
	public void mousePressed(MouseEvent e) {
		if(e.getSource() instanceof ITrack)
			oph.setSelectedTrack((ITrack)e.getSource(), model.getSelectionModel());
		
		setKindOfButton(e);
		pressedflag = true;
		
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

	public void mouseDragged(MouseEvent e) {
		draggedFlag = true; 
		ITrack at = getAbstractTrack(e);
		oph.movePanelToFront(at, e.getButton());
		oph.moveComponentByDragging(at, e);
	}


	public void mouseMoved(MouseEvent e) {

	}
	
	public void actionPerformed(ActionEvent e) {
		if(UserGestures.DELETE_TRACK.equals(e.getActionCommand())){
			model.someActionWithPanel(ActionModes.DELETE);
		} else if(UserGestures.MOVE_UP.equals(e.getActionCommand())){
			model.someActionWithPanel(ActionModes.MOVE_UP);
		} else if(UserGestures.MOVE_DOWN.equals(e.getActionCommand())){
			model.someActionWithPanel(ActionModes.MOVE_DOWN);
		} else if(UserGestures.MOVE_TO_TOP.equals(e.getActionCommand())){
			model.someActionWithPanel(ActionModes.MOVE_TO_TOP);
		} else if(UserGestures.MOVE_TO_BOTTOM.equals(e.getActionCommand())){
			model.someActionWithPanel(ActionModes.MOVE_TO_BOTTOM);
		}  
//		else if (UserGestures.COLORING.equals(e.getActionCommand())) {
//			if (model.getSelectionModel().getSelectedTrack() instanceof ITrack) {
//				ITrack tp = (ITrack) model.getSelectionModel().getSelectedTrack();
//				if (tp.getTrackPlugin().getTrackSettings().getColorProvider() != null) {
//					tp.getTrackPlugin().getTrackSettings().getColorProvider().getSetting().getMenuItem(null);  
//				}
//			}
//		}  
		else if(UserGestures.SHOW__PROBES_PROPERTIES.equals(e.getActionCommand())){
			if(!model.getViewModel().getSelectedProbes().isEmpty()){
				createPropertiesWindow(model.getViewModel().getSelectedProbes());
			}
		} 
//		else if(UserGestures.SHOW__PROBES_DETAILED.equals(e.getActionCommand())){
//			if(!model.getViewModel().getSelectedProbes().isEmpty()){
//				model.openChromeHeatMapComponent(KindOfData.BY_PROBES);
//			}
//		}
	}


	
	public void stateChanged(ChangeEvent e) {

	}
	
	/**
	 * creates a new window with the properties of the selected probes.
	 * @param set
	 */
	private void createPropertiesWindow(Set<Probe> set) {
		AbstractPropertiesDialog dlg;
		dlg = PropertiesDialogFactory.createDialog(set.toArray());
		dlg.setVisible(true);
	}


	//#############################################################
	/*
	 * MouseWheel events
	 * 
	 */
	//#############################################################
	public void mouseWheelMoved(MouseWheelEvent e) {
		
	}
	
	protected ITrack getAbstractTrack(MouseEvent e){
		if(e.getSource() instanceof ITrack){
			return (ITrack)e.getSource();
		}
		return null;
	}
}
