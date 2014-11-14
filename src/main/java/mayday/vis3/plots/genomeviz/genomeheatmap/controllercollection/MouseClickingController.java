package mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection;

import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ClickedSelection;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableComputations;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableMapper;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.ClickedProbes;

public class MouseClickingController {

	protected MasterManager master;
	protected GenomeHeatMapTableModel model;
	 
	public MouseClickingController(MasterManager master, GenomeHeatMapTableModel model){
		this.master = master;
		this.model = model;
	}
	
	public void mouseSelection(int row, int column, MouseEvent e ){
		
		// get strand of actual row/column
		StrandInformation strand = TableMapper.getStrand(row, master.getNumberOfRows());
		int predecessorRowsOfStrand = TableComputations.computePredecessorsRowsOfStrand(row, strand);

		// get cellNumber
		int cellnumber = TableMapper.getCellNumber(row, column, model, predecessorRowsOfStrand);

			List<Probe> actualClickedProbes = Collections.emptyList();
			
			if(column != model.getColumnCount()-1 && column != 0){
				// get probes in cell
				actualClickedProbes = model.getAllProbes_DependingOnStrand(strand, cellnumber);
			}

			// set actual clicked 
			ClickedProbes actProbes = null;
			if(!actualClickedProbes.isEmpty()){
				actProbes = new ClickedProbes(cellnumber, strand, actualClickedProbes);
			} 
			
			
			// clicked cell contain Probes
			if (actProbes != null) {
				probeCellClicked(e, actProbes);
				model.setPreviousClickedCell(actProbes);
			} 
			// no cell containing probes clicked
			else if(actProbes == null){
				noProbeCellClicked(e);
				model.setPreviousClickedCell(null);
			}

	}
	
	private void probeCellClicked(MouseEvent e, ClickedProbes actPair) {
		ClickedProbes previousClickedPair = model.getPreviousClickedCell();
		// Shift pressed
		if (e.isShiftDown()) {
			// Reset previousSelection
			if (previousClickedPair != null){
				shiftPressed(actPair, previousClickedPair);
			}
			// select probes from beginning to actual cell
			else if(previousClickedPair == null){
				Set<Probe> newSelection = new HashSet<Probe>();
				for (int i = 1; i <= actPair.getCellnumber(); i++) {
					List<Probe> list = master.getAllProbes(i);
					if(!list.isEmpty()){
						newSelection.addAll(list);
					}
				}
				master.getViewModel().setProbeSelection(newSelection);
			}
		}
		// STRG pressed
		else if(e.isControlDown()){
			
			// store the actual clicked probe id as the
			// previousClickedProbeId needed for later use to get the
			// previous clicked probe
			if(previousClickedPair != null){
				if(previousClickedPair.compareTo(actPair) == 0){

					model.setClickedSelection();
					
				} else if(previousClickedPair.compareTo(actPair) != 0){

					model.resetClickedSelection();
				}
			
				selectionWithStrg(actPair);
				
			} else {
				model.resetClickedSelection();
				
				
				// check if all probes are just selected, if so, deselect all
				if(master.getViewModel().getSelectedProbes().containsAll(actPair.getProbes())){
					deselectAllProbes(actPair);
				}
				// no ore not all probes selected, so select All
				else {
					Set<Probe> set = new HashSet<Probe>(master.getViewModel().getSelectedProbes());
					set.addAll(actPair.getProbes());
					master.getViewModel().setProbeSelection(set);
				}			
			}
		}
		// neither SHIFT nor STRG pressed
		else {
			
			// Delete actual selection
			master.getViewModel().setProbeSelection(new HashSet<Probe>());
			
			if(previousClickedPair != null){
				// check if user clicks on the same probes (cell)
				if(previousClickedPair.compareTo(actPair) == 0){
					// change mode of cell selection
					model.setClickedSelection();
					
				} else if(previousClickedPair.compareTo(actPair) != 0){

					// reset mode of cell selection, so that all probes are chosen
					model.resetClickedSelection();
				}

				selectionWithoutStrg(actPair);

			} else {
				// set selection to all contained probes
				model.resetClickedSelection();

				master.getViewModel().setProbeSelection(actPair.getProbes());

			}
		}
		master.repaintTable();
	}
	
	public void shiftPressed(ClickedProbes actPair, ClickedProbes prevPair) {

		// create new Set of selected Probes
		Set<Probe> newSelection = new HashSet<Probe>();
		int previousCell = prevPair.getCellnumber();
		int actCell = actPair.getCellnumber();

		if (actCell > previousCell) {
			for (int i = previousCell; i <= actCell; i++) {
				List<Probe> list = master.getAllProbes(i);
				if (!list.isEmpty()) {
					newSelection.addAll(list);
				}
			}

		} else if (actCell < previousCell) {
			for (int i = actCell; i <= previousCell; i++) {
				List<Probe> list = master.getAllProbes(i);
				if (!list.isEmpty()) {
					newSelection.addAll(list);
				}
			}
		} else if (actCell == previousCell) {
			List<Probe> list = master.getAllProbes(actCell);
			if (!list.isEmpty()) {
				newSelection.addAll(list);
			}
		}
		master.getViewModel().setProbeSelection(newSelection);
}
	
	private void selectionWithStrg(ClickedProbes actPair) {
		
		// check if all probes are just selected, if so, deselect all
		if(master.getViewModel().getSelectedProbes().containsAll(actPair.getProbes())){
			deselectAllProbes(actPair);
		} else {
			
			// first time clicked on cell, select all probes contained in cell
			if(model.getClickedSelection().equals(ClickedSelection.ALL)){
				// select all Probes in cell
				Set<Probe> set = new HashSet<Probe>(master.getViewModel().getSelectedProbes());
				set.addAll(actPair.getProbes());		
				master.getViewModel().setProbeSelection(set);
				System.out.println(master.getViewModel().getSelectedProbes().size()+" 2");
			}
			// second time clicked on cell, deselect all Probes contained in cell
			else if(model.getClickedSelection().equals(ClickedSelection.NONE)){
				deselectAllProbes(actPair);
			}
			// set first probe of list as selected
			else if(model.getClickedSelection().equals(ClickedSelection.SINGLE)
					&& model.clickedProbeIndex < model.clickedProbeNumber) {
				Set<Probe> set = new HashSet<Probe>(master.getViewModel()
						.getSelectedProbes());
				set.removeAll(actPair.getProbes());
				set.add(actPair.getProbes().get(model.clickedProbeIndex));
				master.getViewModel().setProbeSelection(set);

				model.updateClickedProbeIndex();
				if (model.clickedProbeIndex >= model.clickedProbeNumber) {
					// System.out.println("RESET CLICKED");
					model.setClickedSelection(ClickedSelection.PENDING);
				}
			}
		}
	}

	private void deselectAllProbes(ClickedProbes actPair) {
		// set Selection to none
		model.setClickedSelection(ClickedSelection.NONE);
		// deselect all probes
		Set<Probe> set = new HashSet<Probe>(master.getViewModel().getSelectedProbes());
		set.removeAll(actPair.getProbes());
		master.getViewModel().setProbeSelection(set);
	}

	/**
	 * first time clicked on cell, select all probes contained in cell.
	 * @param actPair
	 */
	private void selectionWithoutStrg(ClickedProbes actPair) {

		if(model.getClickedSelection().equals(ClickedSelection.ALL)){
			// select all Probes in cell
			master.getViewModel().setProbeSelection(actPair.getProbes());
		}
		// second time clicked on cell, deselect all Probes contained in cell
		else if(model.getClickedSelection().equals(ClickedSelection.NONE)){
			// nothing to do
		}
		
		else if(model.getClickedSelection().equals(ClickedSelection.SINGLE)
				&& model.clickedProbeIndex < model.clickedProbeNumber) {
			master.getViewModel().setProbeSelection(
					actPair.getProbes().get(model.clickedProbeIndex));
			model.updateClickedProbeIndex();
			if (model.clickedProbeIndex >= model.clickedProbeNumber) {
				model.setClickedSelection(ClickedSelection.PENDING);
			}
		}
	}

	/**
	 * User clicked somewhere, so deselect all selected Probes.
	 * @param e
	 */
	private void noProbeCellClicked(MouseEvent e) {
		if (e.isControlDown()) {
			// Do nothing
			
		} else if(e.isShiftDown()){
			// Do nothing
			
		}
		// deselect all selected Probes
		else {
			master.getViewModel().setProbeSelection(new HashSet<Probe>());
		}
	}

}
