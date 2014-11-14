package mayday.vis3.plots.genomeviz.genomeoverview.menues;

import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;

public class RangeSelectionSettings extends Settings {

	protected Controller controller;
	protected GenomeOverviewModel chromeModel;
	
	protected boolean opened = false;
	
	protected IntSetting from;
	protected IntSetting to;
	protected int startPosition = -1;
	protected int endPosition = -1;
	protected String fromtext = "Select lowest genomic location in range: ";
	protected String totext = "Select highest genomic location in range: ";
	public RangeSelectionSettings(Controller Controller, GenomeOverviewModel ChromeModel) {
		super(new HierarchicalSetting("Range selection"), null);
		chromeModel = ChromeModel;
		controller = Controller;
		root.addChangeListener(controller);
		
		startPosition = (int)this.chromeModel.getChromosomeStart();
		endPosition = (int)this.chromeModel.getChromosomeEnd();
		String s = Integer.toString(startPosition) + "bp - " + Integer.toString(endPosition) + "bp";
		fromtext = fromtext + "\n" + s;
		totext = totext + "\n" + s;
		controller = Controller;

		from = new IntSetting("from:", fromtext, startPosition, startPosition, endPosition, true, true);
		to = new IntSetting("to:    ", totext, startPosition, startPosition, endPosition, true, true);

		root
		.addSetting(new HierarchicalSetting("Select range").addSetting(from).addSetting(to))
		.setLayoutStyle(LayoutStyle.PANEL_HORIZONTAL);
		
	}
	
	public int getFromPosition(){
		if(from!=null){
			return from.getIntValue();
		}
		else return -1;
	}

	public int getToPosition(){
		if(to!=null){
			return to.getIntValue();
		}
		else return -1;
	}

	public void setFrom(int fromval) {
		from.setIntValue(fromval);
	}

	public void setTo(int _to) {
		to.setIntValue(_to);
	}
	
	public boolean isValid(int val){
		return from.isValidValue(Integer.toString(val));
	}
}
