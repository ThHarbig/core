package mayday.vis3.plots.genomeviz.genomeorganisation;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.settings.Settings;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.LongSetting;
import mayday.vis3.plots.genomeviz.ILogixVizModel;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.genomeheatmap.Const;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.SelectedRange;

public class ChromosomeSettings extends Settings{
	
	protected ChromosomeDataSet cdset;
	protected ILogixVizModel model;
	
	protected LongSetting start_view_bp;
	protected LongSetting end_view_bp;
	
//	protected BooleanSetting newWindow;

	public ChromosomeSettings(ChromosomeDataSet Cdset) {
		super(new HierarchicalSetting("Chromosome range"), null);
		cdset = Cdset;
		Long chromeStart=getChromosomeStart();
		Long chomeEnd = getChromosomeEnd();
		
		start_view_bp = new LongSetting("View start","Please select bp as the beginning of range view.",chromeStart,chromeStart,chomeEnd,true, true);
		end_view_bp = new LongSetting("View end","Please select bp as the end of range view.",chomeEnd,chromeStart,chomeEnd,true, true);
//		newWindow = new BooleanSetting("separate window","Select if selected range should be visualized in separate window.", false);
		root
		.addSetting(new HierarchicalSetting("start view").addSetting(start_view_bp).setLayoutStyle(LayoutStyle.PANEL_VERTICAL))
		.addSetting(new HierarchicalSetting("end view").addSetting(end_view_bp))
		.addSetting(new ComponentPlaceHolderSetting("complete", new JButton(new ShowCompleteAction())));
		//.addSetting(newWindow);
		setEndStartPositionOfChromosome(cdset.getKindOfData());
	}
	
	public void initialize(ILogixVizModel Model){
		model = Model;
		root.addChangeListener(model.getSettingsChangeListener());
	}

	protected void setEndStartPositionOfChromosome(KindOfData kindOfData) {
	switch (kindOfData) {
	case STANDARD:
		start_view_bp.setLongValue(getChromosomeStart());
		end_view_bp.setLongValue(getChromosomeEnd());
		break;
	case BY_POSITION:
		SelectedRange sr = cdset.getRange();
		if(sr!=null){
			start_view_bp.setLongValue(sr.getFromPosition());
			end_view_bp.setLongValue(sr.getToPosition());
		}
		break;
//	case BY_PROBES:
//		int size = (int)getViewLength(KindOfChromeView.CONDENSED);
//		if(size>0){
//			if(!whole.get(condensed.get(0)).isEmpty()){
//				for(Probe pb: whole.get(condensed.get(0))){
//					if (getStartPosition(pb) < startPosition)
//					startPosition = getStartPosition(pb);
//				}
//			} else {
//				System.err.println("ActualChromeData : setEndStartPositionOfChromosome - No first entry in CondensedArray");
//				startPosition = Const.CHROMOSOME_STARTPOSITION;
//			}
//			
//			
//			if(!whole.get(condensed.get(condensed.size()-1)).isEmpty()){
//				for(Probe pb: whole.get(condensed.get(condensed.size()-1))){
//					if (getEndPosition(pb) > endPosition)
//						endPosition = getEndPosition(pb);
//				}
//			} else {
//				System.err.println("ActualChromeData : setEndStartPositionOfChromosome - No last entry in CondensedArray");
//				endPosition = actualChrome.getLength();
//			}
//		}			
//		break;
	default:
		start_view_bp.setLongValue(getChromosomeStart());
		end_view_bp.setLongValue(getChromosomeEnd());
	}
}
	
//	public boolean isSeparateWindow(){
//		return newWindow.getBooleanValue();
//	}
	
	public long getChromosomeStart() {
		return Const.CHROMOSOME_STARTPOSITION;
	}

	
	public long getChromosomeEnd() {
		return cdset.getActualChrome().getLength();
	}

	public long getViewStart() {
		return start_view_bp.getLongValue();
	}
	
	public void setViewStart(Long nv) {
		start_view_bp.setLongValue(nv);
	}
	
	public long getViewEnd() {
		return end_view_bp.getLongValue();
	}
	
	public void setViewEnd(Long nv) {
		end_view_bp.setLongValue(nv);
	}

	public void removeListener(ILogixVizModel genomeHeatMapTableModel) {
		if(root!=null)root.removeChangeListener(genomeHeatMapTableModel.getSettingsChangeListener());
	}
	
	public long getViewLength() {
		return getViewEnd() - getViewStart() + 1;
	}
	
	public long getViewLength(KindOfChromeView kocv) {
		switch (kocv) {
		case WHOLE:
			return getViewLength();
		case CONDENSED:
			return getViewLengthCondensed();
		default:
			return 0;
		}
	}
	
	private long getViewLengthCondensed(){
		return cdset.getCondensedSize();
	}

	public long getChromosomeLength() {
		return cdset.getActualChrome().getLength();
	}

	public long getSkipValue() {
		return getChromosomeStart()-1;
	}
	
	@SuppressWarnings("serial")
	protected class ShowCompleteAction extends AbstractAction {
		public ShowCompleteAction() {
			super("Show complete");
		}
		public void actionPerformed(ActionEvent e) {
			ChromosomeSettings.this.setViewStart(model.getChromosomeStart());
			ChromosomeSettings.this.setViewEnd(model.getChromosomeEnd());
		}		
	}
}
