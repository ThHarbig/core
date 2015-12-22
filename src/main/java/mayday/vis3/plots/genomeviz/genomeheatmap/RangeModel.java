package mayday.vis3.plots.genomeviz.genomeheatmap;

import java.util.Set;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.Organiser;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.windows.RangeSelectionSettings;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.SelectedRange;

public class RangeModel {

	protected MasterManager master;
	protected Organiser org;
	protected GenomeHeatMapTableModel model;
	
	protected long fromPosition = 1;
	protected long toPosition = 1;
	protected boolean chromeInNewWindowCheckbox = false;
	
	public RangeModel(GenomeHeatMapTableModel model, MasterManager master){
		this.master = master;
		this.model = model;
	}
	
	/**
	 * informs the organiser that range is selected, if one of the selected range position is not valid, so 
	 * false is returned.
	 * @param if true set selected range, else if false set rangeSelection flag of organiser to false
	 * @return true if range selection was valid, else false
	 * 
	 */
	
	public boolean infOrg_aboutRange() {
		System.out.println("infOrg_aboutRange");
		if(org.getKindOfData().equals(KindOfData.BY_POSITION)){
			if(master.getRs()!=null){
				RangeSelectionSettings rss =  master.getRs();
				long from = rss.getFromPosition();
				long to = rss.getToPosition();

				if(from >to){
					long val = from;
					from = to;
					to = val;
				}
				
				if(from >= 1){
					if(to <= model.getOriginalSizeOfChromosome_Whole()){
						if(from <= to){
							org.setRangeSelection_withPos(new SelectedRange(from,to));
							return true;
						}
					}
				}
			}
			
		} else if(org.getKindOfData().equals(KindOfData.STANDARD)){
			org.setRangeSelection_withPos(null);
		}
		
		return false;
	}
	
//	public boolean infOrg_aboutRange() {
//		if(org.getKindOfData().equals(KindOfData.BY_POSITION)){
//			long from = this.getFromPosition_RangeSelection();
//			long to = this.getToPosition_RangeSelection();
//
//			if(from >= 1){
//				if(to <= model.getOriginalSizeOfChromosome_Whole()){
//					if(from <= to){
//						org.setRangeSelection_withPos(new SelectedRange(from,to));
//						return true;
//					}
//				}
//			}
//		} else if(org.getKindOfData().equals(KindOfData.STANDARD)){
//			org.setRangeSelection_withPos(null);
//		}
//		
//		return false;
//	}

	public boolean infOrg_aboutRange(Set<Probe> selectedProbes) {
//		if (org.getKindOfData().equals(KindOfData.BY_PROBES)) {
//			if (selectedProbes!= null && !selectedProbes.isEmpty()) {
//				org.setRangeSelection_withProbes(selectedProbes);
//				return true;
//			}
//		} else 
		if (org.getKindOfData().equals(KindOfData.STANDARD)) {
			org.setRangeSelection_withProbes(null);
		}
		return false;
	}
	
	/**
	 * set how new chromosome window is show, so if whole data is shown (standard), or only selection is shown 
	 * (selection by selected probes or selection by selected positions).
	 * @param kindOfData
	 */
	public void setKindOfData(KindOfData kindOfData){
		org.setKindOfData(kindOfData);
	}
	
	public void setRangeSelection_withPos(SelectedRange selectedRange){
		org.setRangeSelection_withPos(selectedRange);
	}
	
	public void setFromPosition_RangeSelection(int val) {
		this.fromPosition = val;
	}
	
	public long getFromPosition_RangeSelection(){
		return this.fromPosition;
	}
	
	public void setToPosition_RangeSelection(int val) {
		this.toPosition = val;
	}
	
	public long getToPosition_RangeSelection(){
		return this.toPosition;
	}
	
	/**
	 * checkbox if new chrome is shown in additional window or not.
	 * @param val
	 */
	public void setChromeNewWindowCheckbox(boolean val){
		this.chromeInNewWindowCheckbox = val;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getChromeNewWindowCheckbox(){
		return this.chromeInNewWindowCheckbox;
	}

	public void setOrganiser(Organiser Org) {
		this.org = Org;
	}
}
