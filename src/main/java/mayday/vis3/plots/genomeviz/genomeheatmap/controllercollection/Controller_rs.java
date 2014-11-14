package mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;

public class Controller_rs implements ChangeListener {

	protected Controller c;
	protected GenomeHeatMapTableModel model;
	
	public Controller_rs(Controller C, GenomeHeatMapTableModel Model){
		this.c = C;
		this.model = Model;
	}

	public void stateChanged(ChangeEvent arg0) {
		
	}
}
