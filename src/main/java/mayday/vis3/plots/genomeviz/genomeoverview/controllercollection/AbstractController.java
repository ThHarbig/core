package mayday.vis3.plots.genomeviz.genomeoverview.controllercollection;

import java.awt.event.MouseEvent;

import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;

public abstract class AbstractController {

	protected GenomeOverviewModel model = null;
	protected Controller c = null;
	
	
	protected OperationHandler oph = null;
	protected boolean initialized = false;
	
	protected FromToPosition first = null;
	protected FromToPosition last = null;
	protected FromToPosition ftp;
	protected double[] result_arry_dbl = new double[2];
	
	protected boolean draggedFlag = false;
	protected boolean pressedflag = false;
	protected boolean rightmouse = false;
	protected boolean leftmouse = false;
	
	public AbstractController(GenomeOverviewModel Model, Controller C){
		model = Model;
		c = C;
		
		oph = new OperationHandler(model);
		ftp = new FromToPosition();
		first=new FromToPosition();
		last=new FromToPosition();
		
	}
	
	public AbstractController(GenomeOverviewModel Model) {
		model = Model;
		
		oph = new OperationHandler(model);
		ftp = new FromToPosition();
	}

	protected void setKindOfButton(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			rightmouse = false;
			leftmouse = true;
		} else if(e.getButton() == MouseEvent.BUTTON3){
			rightmouse = true;
			leftmouse = false;
		} else {
			rightmouse = false;
			leftmouse = false;
		}
	}
}
