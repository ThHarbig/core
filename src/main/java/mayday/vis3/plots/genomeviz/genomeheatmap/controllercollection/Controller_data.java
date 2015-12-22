package mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.gui.Layouter;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.genomeviz.Organiser;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapComponent;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.RangeModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.SelectedRange;
import mayday.vis3.plots.genomeviz.genomeheatmap.usergestures.UserGestures;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeDataSet;

public class Controller_data implements ActionListener, SettingChangeListener {

	protected GenomeHeatMapTableModel model = null;
	protected RangeModel rangeModel = null;
	protected Controller c = null;
	
	public Controller_data(Controller c,GenomeHeatMapTableModel model){
		this.model = model;
		this.c = c;
		this.rangeModel = this.model.getRangeModel();
	}

	public void actionPerformed(ActionEvent e) {

		if(UserGestures.RANGE_SELECTION.equals(e.getActionCommand())){
			if(c.getViewModelFromController()!=null){
				rangeModel.setKindOfData(KindOfData.BY_POSITION);
				boolean rangeValid = rangeModel.infOrg_aboutRange();
				createWindow_Chrome(rangeValid);
				rangeModel.setKindOfData(KindOfData.STANDARD);
			}
		} else if(UserGestures.SHOW_CHROME.equals(e.getActionCommand())){
			rangeModel.setKindOfData(KindOfData.STANDARD);
			
			if(rangeModel.getChromeNewWindowCheckbox() == true){
				//boolean rangeValid = rangeModel.infOrg_aboutRange(c.getViewModelFromController().getSelectedProbes());
				createWindow_Chrome();
			} else if(rangeModel.getChromeNewWindowCheckbox() == false){
				equalWindow_Chrome();
			}
					
					
//		} 
//		else if(UserGestures.PROBE_RANGE_SELECTION.equals(e.getActionCommand())){
//			if(c.getViewModelFromController()!=null){
//				rangeModel.setKindOfData(KindOfData.BY_PROBES);
//				boolean rangeValid = rangeModel.infOrg_aboutRange(c.getViewModelFromController().getSelectedProbes());
//				createWindow_Chrome(rangeValid);
//				rangeModel.setKindOfData(KindOfData.STANDARD);
//			}
		} else if(e.getActionCommand().equals(UserGestures.SCROLL_RIGHT)){

			long startPos = this.model.getViewStart();
			long endPos = this.model.getViewEnd();
			long endPos_Abs = this.model.getChromosomeEnd();
			
			if(endPos != endPos_Abs){
				int range =(int)(endPos-startPos)+1;

				startPos = startPos + Math.round((double)range/2.);
				endPos = endPos + Math.round((double)range/2.);
				
				if(endPos > endPos_Abs){
					endPos = endPos_Abs;
					startPos = endPos_Abs - range + 1;
				}
				
				setNewData(startPos, endPos);
			}
			
			
		} else if(e.getActionCommand().equals(UserGestures.SCROLL_LEFT)){

			long startPos = this.model.getViewStart();
			long startPos_Abs = this.model.getChromosomeStart();
			long endPos = this.model.getViewEnd();
			
			if(startPos != startPos_Abs){
				int range =(int)(endPos-startPos)+1;
				
				startPos = startPos - Math.round((double)range/2.);
				endPos = endPos - Math.round((double)range/2.);
				
				if(startPos < startPos_Abs){
					startPos = startPos_Abs;
					endPos = startPos + range - 1;
				}
				
				setNewData(startPos, endPos);
			}
		}
	}

	/**
	 * creates new window which shows another chomosome (or the same).
	 * @param tempSpecies
	 * @param tempChrome
	 */
	private void createWindow_Chrome(boolean rangeValid) {
		if(rangeValid){
			if(model.getOrganiser() != null){
				Organiser org = model.getOrganiser();
				org.setTempSpeciesAndChrome(model.getSelectedChrome());
				if(c.getVisualizerFromController()!=null){
					Visualizer viz = c.getVisualizerFromController();
					PlotWindow pw = new PlotWindow(new GenomeHeatMapComponent(), viz);
					pw.setVisible(true);
					Layouter l = new Layouter(2,1);
					l.nextElement().placeWindow(pw);
				}
			}
		} else {
			System.err.println("Controller: selected range not valid!");
		}
	}
	
	private void createWindow_Chrome() {
		if (model.getOrganiser() != null) {
			Organiser org = model.getOrganiser();
			org.setTempSpeciesAndChrome( model.getTempSelectedChrome());
			if (c.getVisualizerFromController() != null) {
				Visualizer viz = c.getVisualizerFromController();
				PlotWindow pw = new PlotWindow(new GenomeHeatMapComponent(),viz);
				pw.setVisible(true);
				Layouter l = new Layouter(2, 1);
				l.nextElement().placeWindow(pw);
			}
		}
	}
	
	public void jumpToData(long pos){
		long startPos = model.getViewStart();
		long startPos_Abs = model.getChromosomeStart();
		long endPos = model.getViewEnd();
		long endPos_Abs = model.getChromosomeEnd();

			int range =(int)(endPos-startPos)+1;

			startPos = pos - Math.round((double)range/2.);
			endPos = pos + Math.round((double)range/2.);
			
			
			
			if(startPos < startPos_Abs){
				startPos = startPos_Abs;
				endPos = startPos + range - 1;
				
			}
			
			if(endPos > endPos_Abs){
				endPos = endPos_Abs;
				startPos = endPos_Abs - range + 1;
			}
			
			int newRange = (int)(endPos-startPos)+1;
			int diff = 0;
			if(newRange > range){
				diff = newRange-range;
				
				if(startPos == startPos_Abs && endPos < endPos_Abs){
					endPos = endPos - diff;
				} else if(startPos > startPos_Abs && endPos == endPos_Abs){
					startPos = startPos + diff;
				} else {
					if(diff == 2){
						endPos = endPos - 1;
						startPos = startPos + 1;
					} else if(diff == 1){
						endPos = endPos - diff;
					} else {
						System.err.println();
					}
				}
			}

			setNewData(startPos, endPos);
	}
	
	protected void setNewData(long startPos, long endPos) {
		rangeModel.setFromPosition_RangeSelection((int)startPos);
		rangeModel.setToPosition_RangeSelection((int)endPos);
		rangeModel.setKindOfData(KindOfData.BY_POSITION);
		rangeModel.setRangeSelection_withPos(new SelectedRange(startPos,endPos));
		
		
		//this.model.getOrganiser().clearTempSpeciesAndChrome();
		
		// set last fitted data to null
		model.clearAllPreviousComputedFittedData();
		//model.setChromeAndData();
		
		ChromosomeDataSet data = model.getOrganiser().getActualData(model.getSelectedChrome());
		model.setActualData(data);
		
		if(model.getKindOfChromeView().equals(KindOfChromeView.CONDENSED)){
			if(!model.isCondensedViewAvailable()){
				c.chromeViewChangedOperations(KindOfChromeView.WHOLE);
			}
		}

		model.setOriginalNumberOfCells();
		model.tableStructureChanged();
		// reset zoomlevel of chromosome
		this.c.zoomLevelChangedOperations(model.getZoomLevel(), true);
	}
	
	public void equalWindow_Chrome() {			
		if(model.getOrganiser() != null){
			Organiser org = model.getOrganiser();
			org.clearTempSpeciesAndChrome();
			
			// set last fitted data to null
			model.clearAllPreviousComputedFittedData();
			model.setChromeAndData();
			
			model.setOriginalNumberOfCells();
			model.tableStructureChanged();
			
			// reset zoomlevel of chromosome
			c.zoomLevelChangedOperations(ZoomLevel.one, true);
		}
	}

	public void stateChanged(SettingChangeEvent e) {
		
		rangeModel.setKindOfData(KindOfData.BY_POSITION);
		boolean rangeValid = rangeModel.infOrg_aboutRange();
		createWindow_Chrome(rangeValid);
		rangeModel.setKindOfData(KindOfData.STANDARD);
	}
}
