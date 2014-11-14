package mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.ProbeListColoring;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.RangeModel;

public class Controller_cb implements ItemListener {
 
	protected GenomeHeatMapTableModel model;
	protected RangeModel rangeModel;
	
//	private JCheckBox diffbox = null;
	private JCheckBox pblistColoring = null;
	private JCheckBox newChromewindow_frame = null;
	private JCheckBox newChromewindow_menu = null;

	
	public Controller_cb(GenomeHeatMapTableModel model){

		this.model = model;
		rangeModel = model.getRangeModel();
	}


	public void itemStateChanged(ItemEvent e) {

		Object o = e.getSource();
		if (o instanceof JCheckBox) {
			JCheckBox cb = (JCheckBox) o;

//			if (diffbox != null && cb instanceof Diffbox) {
//				System.out.println("DIFF");
//				if (e.getStateChange() == ItemEvent.SELECTED) {
//					model.setDifferenceCheckbox(true);
//				} else {
//					model.setDifferenceCheckbox(false);
//				}
//			} else 
				
			if (newChromewindow_frame != null && cb.equals(newChromewindow_frame)) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setChromeCheckboxes(true);
				} else {
					setChromeCheckboxes(false);
				}
			} else if (newChromewindow_menu != null && cb.equals(newChromewindow_menu)) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setChromeCheckboxes(true);
				} else {
					setChromeCheckboxes(false);
				}
			} else if(pblistColoring != null && cb.equals(pblistColoring)){
				if (e.getStateChange() == ItemEvent.SELECTED) {
					model.setPbListColoring(ProbeListColoring.COLOR_HIGHEST_PROBELIST);
				} else {
					model.setPbListColoring(ProbeListColoring.COLOR_ALL_PROBELISTS);
				}
			}
		}
	}


	private void setChromeCheckboxes(boolean val) {
		rangeModel.setChromeNewWindowCheckbox(val);
		if(newChromewindow_frame!=null){
			newChromewindow_frame.setSelected(val);
		}
		if(newChromewindow_menu != null){
			newChromewindow_menu.setSelected(val);
		}
	}


//	public void setDiffBox(JCheckBox diffbox) {
//		this.diffbox = diffbox;
//	}
	
	public void setPblistColoring(JCheckBox pblistColoring){
		this.pblistColoring = pblistColoring;
	}


	public void setChromeNewWindowCheckbox_Frame(JCheckBox newWindowCheckbox) {
		this.newChromewindow_frame = newWindowCheckbox;
	}


	public void setChromeNewWindowCheckbox_Menu(JCheckBox newWindowCheckbox) {
		newChromewindow_menu = newWindowCheckbox;
	}

}
