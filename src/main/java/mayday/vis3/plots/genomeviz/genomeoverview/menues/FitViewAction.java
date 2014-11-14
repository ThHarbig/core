package mayday.vis3.plots.genomeviz.genomeoverview.menues;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.plots.genomeviz.EnumManagerGO.SizeMode;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
@SuppressWarnings("serial")
public class FitViewAction extends AbstractAction{
	
	protected GenomeOverviewModel model;
	protected Controller c;
	
	public FitViewAction(String text, GenomeOverviewModel Model, Controller C){
		super(text);
		model = Model;
		c = C;
		
	}
	
	public void actionPerformed(ActionEvent e) {
		model.setSizeLayeredPane(0,SizeMode.SIZE_ZOOM);
	}
}

