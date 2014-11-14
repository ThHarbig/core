package mayday.vis3.plots.genomeviz.genomeheatmap.menues;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.GHMStyle;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;

public class StyleSetting extends HierarchicalSetting{
//	protected GenomeHeatMapTableModel model;
//	protected MasterManager master;
	
	// Style
	protected SelectableHierarchicalSetting style;
	protected final static String CLASSIC = "Black";
	protected final static String MODERN = "White";
	protected Controller target;
	protected MasterManager master;
	
	public StyleSetting(String Name, String Description, Controller Target, MasterManager Master) {
		super(Name);
		target = Target;
		master = Master;
		//addSetting(new HierarchicalSetting("Style settings")
		addSetting(style = new SelectableHierarchicalSetting("style",
				null, 1, new Object[] { CLASSIC, MODERN},
				SelectableHierarchicalSetting.LayoutStyle.PANEL_HORIZONTAL, false));
		
		addChangeListener(target);
		setChildrenAsSubmenus(false);
	}

	public StyleSetting clone() {
		StyleSetting ss = new StyleSetting("Style", "StyleSelection",target, master);
		ss.style.setValueString(style.getValueString());		
        return ( ss );
	}
	
	public GHMStyle getStyle(){
		if(style.getValueString().equals(CLASSIC)){
			return GHMStyle.CLASSIC;
		} else if(style.getValueString().equals(MODERN)){
		}
			return GHMStyle.MODERN;
		} 	
}
