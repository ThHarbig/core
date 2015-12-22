package mayday.vis3.plots.genomeviz;

import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.plots.genomeviz.EnumManager.RANGE_OF_CHROMOSOME;

public class FindPositionSettings extends Settings {

		protected IController controller;
		protected ILogixVizModel model;
		
		protected boolean opened = false;
		
		protected IntSetting searchedpos;
		protected int startPosition = -1;
		protected int endPosition = -1;
		protected String fromtext = "select genomic location to search for between:";
		
		public FindPositionSettings(IController Controller, ILogixVizModel Model, RANGE_OF_CHROMOSOME roc) {
			super(new HierarchicalSetting("Jump to position"), null);
			model = Model;
			controller = Controller;
//			root.addChangeListener(controller.getC_set());

			startPosition =0;
			endPosition =0;

			switch(roc){
			case COMPLETE:
				startPosition = (int)model.getChromosomeStart();
				endPosition = (int)model.getChromosomeEnd();
				break;
			case REDUCED:
				startPosition = (int)model.getViewStart();
				endPosition = (int)model.getViewEnd();
				break;
			}
	
			String s = Integer.toString(startPosition) + "bp - " + Integer.toString(endPosition) + "bp";
			fromtext = fromtext + "\n" + s;
			controller = Controller;

			searchedpos = new IntSetting("position:", fromtext, startPosition, startPosition, endPosition, true, true);

			root
//			.addSetting(new HierarchicalSetting("Select range")
			.addSetting(searchedpos);//)
//			.setLayoutStyle(LayoutStyle.PANEL_VERTICAL);
			
		}
		
		public int getPosition(){
			if(searchedpos!=null){
				return searchedpos.getIntValue();
			}
			else return -1;
		}

		public void setPosition(int fromval) {
			searchedpos.setIntValue(fromval);
		}
}
