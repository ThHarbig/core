package mayday.vis3.plots.genomeviz.genomeheatmap.menues.windows;

import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;

public class RangeSelectionSettings extends Settings {

		protected Controller controller;
		protected MasterManager master;
		
		protected boolean opened = false;
		
		protected IntSetting from;
		protected IntSetting to;
		protected int startPosition = -1;
		protected int endPosition = -1;
		protected String fromtext = "Select lowest genomic location in range: ";
		protected String totext = "Select highest genomic location in range: ";
		
		public RangeSelectionSettings(Controller Controller, MasterManager Master) {
			super(new HierarchicalSetting("Range selection"), null);
			master = Master;
			controller = Controller;
			root.addChangeListener(controller.getC_dt());
			
			startPosition = (int)master.getStartPositionOfChromosome();
			endPosition = (int)master.getEndPositionOfChromosome();
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
		
		public boolean isValid(int val){
			return from.isValidValue(Integer.toString(val));
		}
}
