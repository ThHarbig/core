package mayday.vis3.plots.genomeviz.genomeoverview.controllercollection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.settings.SettingsDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.genomeviz.IController;
import mayday.vis3.plots.genomeviz.Organiser;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.EnumManagerGO.SizeMode;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewComponent;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.LayeredPane_Operations;
import mayday.vis3.plots.genomeviz.genomeoverview.menues.MenuModel;
import mayday.vis3.plots.genomeviz.genomeoverview.menues.RangeSelectionSettings;

public class Controller extends AbstractController implements IController, SettingChangeListener, ComponentListener,
		MouseListener, ActionListener, KeyListener, MouseWheelListener, AdjustmentListener, WindowListener {

	protected MenuModel menuModel = null;
	
	protected Controller_tc c_tp = null;
	protected Controller_header c_ch = null;
	protected Controller_ppc c_pp = null;
	protected Controller_upc c_up_spp = null;
	
//	protected JCheckBox newWindowCheckbox = null;
	protected RangeSelectionSettings rss = null;
	
//	protected Controller_settings c_set;
	
	protected boolean ctrl_pressed = false;
	protected boolean resizeLayeredPane_Height = false;
	
	public Controller(GenomeOverviewModel ChromeModel, DataSetted ds) {
		super(ChromeModel);
		c_tp = new Controller_tc(model, this);
		c_ch = new Controller_header(model, this);
		c_pp = new Controller_ppc(model, this);
		c_up_spp = new Controller_upc(model, this);
//		c_set = new Controller_settings(this,model);
		ds.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				initialized = true;
				setupMenuModel();
			}
		});
		
	}
	
//	public Controller_settings getC_set() {
//		return c_set;
//	}
	
	public Controller_tc getController_tp(){
		return c_tp;
	}
	
	public void setupMenuModel(){
		menuModel = new MenuModel(this.model, this);
		rss = new RangeSelectionSettings(this, model);
	}
	
	public void removeNotify() {
		if (menuModel!=null)
			menuModel.removeNotify();
	}

	//#############################################################
	/*
	 * Events from ChangeListener
	 * 
	 * 
	 */
	//#############################################################
	/**
	 * 
	 */
	public void stateChanged(ChangeEvent e) {
		if(initialized){
			if(rss!=null){
				int from = rss.getFromPosition();
				int to = rss.getToPosition();
				
				if(from>to){
					int val = from;
					from = to;
					to = val;
				} 
			}
		}
		
	}

	//#############################################################
	/*
	 * Events from Component
	 * 
	 * 
	 */
	//#############################################################
	public void componentHidden(ComponentEvent arg0) {

	}

	public void componentMoved(ComponentEvent e) {
		
	}

	public void componentResized(ComponentEvent e) {
		if(initialized){
			if(model!=null && model.getOrganiser()!=null
					&& model.getOrganiser().getChromeManager()!=null
					&& model.getOrganiser().getChromeManager().containsLoci()){
				if (e.getSource() instanceof GenomeOverviewComponent) {
					GenomeOverviewComponent comp = (GenomeOverviewComponent) e
							.getSource();
					
					boolean gettingBigger = model.isComponentGettingBigger(comp);
					
					if(gettingBigger){
						if(model.getWidth_LayeredPane()<comp.getWidth()){
							model.setSizeLayeredPane(0,SizeMode.SIZE_LAYER);
						}

						model.computeVisiblePositions();
						model.getSelectionModel().setDrawFoundProbe(false);
					}
				}
			}
		}
	}

	public void componentShown(ComponentEvent arg0) {

	}

	//#############################################################
	/*
	 * Mouse clicking events
	 * 
	 */
	//#############################################################
	public void mouseClicked(MouseEvent e) {
		oph.setSelectedTrack(null, model.getSelectionModel());
	}

	public void mouseEntered(MouseEvent e) {
	
	}

	public void mouseExited(MouseEvent e) {
		if (ctrl_pressed)
			ctrl_pressed = ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0);
	}

	public void mousePressed(MouseEvent e) {
		oph.setSelectedTrack(null, model.getSelectionModel());
		oph.maybeShowPopup(e, this, menuModel, model.getSelectionModel());
		oph.setSelectedProbes(e, this, model.getSelectionModel());
		if((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0){
			resizeLayeredPane_Height = true;
			model.resetHeight_layeredPane();
		}
		
	}

	public void mouseReleased(MouseEvent e) {
		oph.maybeShowPopup(e, this, menuModel, model.getSelectionModel());
		resizeLayeredPane_Height = false;
	}

	//#############################################################
	/*
	 * Item events
	 * 
	 */
	//#############################################################
	public void actionPerformed(ActionEvent e) {
		if(initialized){
			if(UserGestures.SELECT_RANGE_FROM_PRESSED.equals(e.getActionCommand())){
				model.setRangeSelectionButton(true);
			} else if(UserGestures.SELECT_RANGE_TO_PRESSED.equals(e.getActionCommand())){
				model.setRangeSelectionButton(false);
			} else if(UserGestures.SELECT_RANGE_FOR_DETAILS.equals(e.getActionCommand())){
	        	model.openChromeHeatMapComponent(KindOfData.BY_POSITION);
			} else if(UserGestures.SHOW_CHROME.equals(e.getActionCommand())){
//				if(this.newWindowCheckbox.isSelected()){
//					createWindow_Chrome();
//				} else if(!newWindowCheckbox.isSelected()){
					sameWindow_Chrome();
//				}
			} else if(UserGestures.RANGE_SELECTION.equals(e.getActionCommand())){
				if(this.rss!=null){
					SettingsDialog sd = rss.getDialog();
					if(sd!=null){
						sd.setVisible(true);
					}
				}
			} else if(UserGestures.GOTO_POSITION.equals(e.getActionCommand())){
				if(model.getFps()!=null){
					SettingsDialog sd = model.getFps().getDialog();
					if(sd != null){
						sd.showAsInputDialog();
						searchPositionInChromosome();
					}
				}
			} else if (UserGestures.SELECT_RANGE_TO_VIEW.equals(e
					.getActionCommand())) {
				double visLowPos_new_bp = model
						.getFromPosition_RangeSelection();
				double visHighPos_new_bp = model.getToPosition_RangeSelection();
				double newWidthLayeredPane = LayeredPane_Operations
						.computeNewWidthOfLayeredPane(visLowPos_new_bp,
								visHighPos_new_bp, model);

				if (newWidthLayeredPane>0.) {
					model.resizeLayeredPane((int)visLowPos_new_bp,(int)visHighPos_new_bp,
							(int)newWidthLayeredPane,SizeMode.SIZE_ZOOM);
				} else {
					System.out.println("Width not valid");
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						model.repositionVisibleRect_mouseClicked(null);
					}
				});
			}
		}
		
	}

	//#############################################################
	/*
	 * MouseWheel events
	 * 
	 */
	//#############################################################
	public void mouseWheelMoved(MouseWheelEvent e) {
		ctrl_pressed = ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0);
		
		boolean shift_pressed = ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0);
		 
		oph.mouseWheelOperations(e, ctrl_pressed, shift_pressed);
	}
	
	/**
	 * repaint headerpanel if scrollbars moved.
	 */
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if(this.initialized){
			model.computeVisiblePositions();
			model.repositionChromeHeaderInScrollPane();
			model.setLocationOfUserpanel();
		}
	}

	/**
	 * select actual clicked track.
	 * @param source
	 */

	public Controller_header getController_chrh() {
		return this.c_ch;
	}

	public Controller_ppc getController_pp() {
		return this.c_pp;
	}

	
	//#############################################################
	/*
	 * Window listener
	 * 
	 */
	//#############################################################
	public void windowActivated(WindowEvent e) {

	}

	public void windowClosed(WindowEvent e) {
		
	}

	public void windowClosing(WindowEvent e) {
		model.setRangeFlag(false);
	}

	public void windowDeactivated(WindowEvent e) {
		
	}

	public void windowDeiconified(WindowEvent e) {
		
	}

	public void windowIconified(WindowEvent e) {
		
	}

	public void windowOpened(WindowEvent e) {
		model.setRangeFlag(true);
	}

//	// Item Listener
//	public void itemStateChanged(ItemEvent e) {
//		if (e.getSource() instanceof JCheckBox) {
//			JCheckBox cb = (JCheckBox) e.getSource();
//			
//			if (newWindowCheckbox != null && cb.equals(newWindowCheckbox)) {
//				if (e.getStateChange() == ItemEvent.SELECTED) {
//					setChromeCheckboxes(true);
//				} else {
//					setChromeCheckboxes(false);
//				}
//			} 
//		}
//	}

//	public void setCheckBox_nw(JCheckBox newWindowCheckbox) {
//		this.newWindowCheckbox = newWindowCheckbox;
//	}
	
//	private void setChromeCheckboxes(boolean val) {
//		if(newWindowCheckbox!=null){
//			newWindowCheckbox.setSelected(val);
//		}
//	}

	public MenuModel getMenuModel() {
		return this.menuModel;
	}
	
//	private void createWindow_Chrome() {
//		if (model.getOrganiser() != null) {
//			Organiser org = model.getOrganiser();
//			org.setTempSpeciesAndChrome(model.getTempSelectedChrome());
//			if (getVisualizerFromController() != null) {
//				Visualizer viz = getVisualizerFromController();
//				PlotWindow pw = new PlotWindow(new GenomeOverviewComponent(),viz);
//				pw.setVisible(true);
//				Layouter l = new Layouter(2, 1);
//				l.nextElement().placeWindow(pw);
//			}
//		}
//	}
	
	public void sameWindow_Chrome() {			
		if(model.getOrganiser() != null){
			Organiser org = model.getOrganiser();
			org.clearTempSpeciesAndChrome();
			model.setActualData(org.getActualData(model.getTempSelectedChrome()));
			model.setSizeLayeredPane(0,SizeMode.SIZE_ZOOM);
			model.fireChanged();			
			model.getPanelPositioner().updateTracks();
			model.paintManager.zoomChanged();
			model.repositionChromeHeaderInScrollPane();			
		}
	}
	
	public Visualizer getVisualizerFromController(){
		if(model.getViewModel()!=null){
			return model.getViewModel().getVisualizer();
		}
		return null;
	}

	public RangeSelectionSettings getRangeSelectionSettings() {
		return this.rss;
	}

	public Controller_upc getC_up_spp() {
		return c_up_spp;
	}
	
	/**
	 * search position in chromosome and jump visible rect to this position.
	 */
	public void searchPositionInChromosome() {
		model.centerView((int) (model.getSearchedChromePosition()),0);
	}

	public boolean isCtrl_pressed() {
		return ctrl_pressed;
	}

	
	public void stateChanged(SettingChangeEvent e) {

	}

	
	public void keyPressed(KeyEvent arg0) {
		
	}

	
	public void keyReleased(KeyEvent arg0) {
		
	}

	
	public void keyTyped(KeyEvent arg0) {
		
	}
}
