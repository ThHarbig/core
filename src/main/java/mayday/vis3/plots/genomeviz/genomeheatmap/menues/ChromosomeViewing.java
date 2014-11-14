package mayday.vis3.plots.genomeviz.genomeheatmap.menues;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ProbeListColoring;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;

public class ChromosomeViewing {
	protected MasterManager master;
	protected ChromosomeViewingSetting viewingSetting;
	protected MenuManager menuManager;
	
	
	public ChromosomeViewing(MasterManager Master, MenuManager MenuManager){
		master = Master;
		menuManager = MenuManager;
		viewingSetting = new ChromosomeViewingSetting("Chromosome View","Chromosome View", menuManager);
	}
	
	public ChromosomeViewingSetting getSetting(){
		
		return viewingSetting;
	}
	
	public class ChromosomeViewingSetting extends HierarchicalSetting{
		
		protected SelectableHierarchicalSetting representation;
		protected SelectableHierarchicalSetting condensed_whole;
		protected final static String MEAN = "mean";
		protected final static String MAX = "max";
		protected final static String MIN = "min";
		protected final static String WHOLE = "condensed";
		protected final static String CONDENSED = "whole";
		protected BooleanSetting colorHighestProbe;
		protected MenuManager target;
		
		public ChromosomeViewingSetting(String Name, String Description, MenuManager Target) {
			super(Name);
			
			description = Description;
			
			target = Target;

			addSetting(new HierarchicalSetting("Representation").addSetting(representation = new SelectableHierarchicalSetting("Representation",
					null, 0, new Object[] { MEAN, MAX, MIN },
					SelectableHierarchicalSetting.LayoutStyle.PANEL_HORIZONTAL, false)));
			
			colorHighestProbe = new BooleanSetting("Color highest probelist occurence", null, true);
			addSetting(new HierarchicalSetting("Coloring of Probelist").addSetting(colorHighestProbe));

			addSetting(new HierarchicalSetting("Condensed/Whole - View").addSetting(condensed_whole = new SelectableHierarchicalSetting("Condensed/Whole",
					null, 0, new Object[] { CONDENSED,WHOLE },
					SelectableHierarchicalSetting.LayoutStyle.PANEL_HORIZONTAL, false)));
			
			setChildrenAsSubmenus(false);
			
			this.addChangeListener(target);
			
			this.setLayoutStyle(LayoutStyle.PANEL_VERTICAL);
		}
		
		public ChromosomeViewingSetting clone() {
			ChromosomeViewingSetting cvs = new ChromosomeViewingSetting("Chromosome View","Chromosome View", menuManager);
			cvs.representation.setValueString(representation.getValueString());
			cvs.condensed_whole.setValueString(condensed_whole.getValueString());
			cvs.colorHighestProbe.setBooleanValue(colorHighestProbe.getBooleanValue());
			
	        return ( cvs );
		}
		
		protected ProbeListColoring getProbelistColoring(){
			if(colorHighestProbe.getBooleanValue() == true){
				return ProbeListColoring.COLOR_HIGHEST_PROBELIST;
			} else if(colorHighestProbe.getBooleanValue() == false){
				return ProbeListColoring.COLOR_ALL_PROBELISTS;
			}
			else return null;
		}
		
		protected SplitView getRepresentation(){
			
			if(representation.getValueString().equals("mean")){
				return SplitView.mean;
			} else if(representation.getValueString().equals("max")){
				return SplitView.max;
			} else if(representation.getValueString().equals("min")){
				return SplitView.min;
			}
			return null;
		}
		
		protected KindOfChromeView getKindOfChromeView(){
			
			if(condensed_whole.getValueString().equals("condensed")){
				return KindOfChromeView.CONDENSED;
			} else if(condensed_whole.getValueString().equals("whole")){
				return KindOfChromeView.WHOLE;
			} 
			return null;
		}

		public void enableCondensedViewButtons(boolean b) {
			if(b){
				condensed_whole.setPredefined(new Object[] {WHOLE, CONDENSED});
			} else{
				condensed_whole.setPredefined(new Object[] {WHOLE});
			}
		}
	}
}
