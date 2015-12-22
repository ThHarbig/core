package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.heatmap;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.genetics.basic.Strand;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.ValueProvider;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ProbeListColoring;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class HeatmapTrackSettings extends AbstractTrackSettings{

	protected ObjectSelectionSetting<Object> representation;
	protected final static String MIN = "min";
	protected final static String MAX = "max";
	protected final static String MEAN = "mean";
	protected BooleanSetting colorHighestProbe;
	protected BooleanSetting useTransparency, isOpacity;
	
	protected ValueProvider transparencyProvider;
	
	public HeatmapTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);
		
		colorHighestProbe = new BooleanSetting("Use majority coloring", 
				"If selected, the coloring by \"top-priority probe list\" will only use the color of the top-priority probe list.\n" +
				"If deselected, boxes for all present probelists will be drawn in their respective colors, \n" +
				"with the height of each box representing the percentage of probes contained in it.", 
				true);
		
		root
			.addSetting(strand = new ObjectSelectionSetting<Object>("Strand",
						null, 0, new Object[] { PLUS,MINUS,BOTH }).setLayoutStyle(
						ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS))
			.addSetting(new HierarchicalSetting("Graphical representations")
				.addSetting(coloring.getSetting())
				.addSetting(new HierarchicalSetting("Transparency")
					.addSetting(useTransparency = new BooleanSetting("Use transparency",null, false))
					.addSetting(isOpacity = new BooleanSetting("Interpret values as opacity","Check if large values mean less transparency", false))
					.addSetting((transparencyProvider = new ValueProvider(model.getViewModel(),"Data source")).getSetting())
				)
				.setLayoutStyle(LayoutStyle.TABBED)
			);
		
		root
		.addSetting(new HierarchicalSetting("Multiple values for one location")
			.addSetting(colorHighestProbe)		
			.addSetting(representation = new ObjectSelectionSetting<Object>("Representation",
						null, 2, new Object[] { MIN, MAX, MEAN}).setLayoutStyle(
						ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS))
		);
	}
	
	public ValueProvider getTransparencyProvider() {
		return transparencyProvider;
	}
	
	public ProbeListColoring getProbelistColoring(){
		if(colorHighestProbe.getBooleanValue() == true){
			return ProbeListColoring.COLOR_HIGHEST_PROBELIST;
		} else if(colorHighestProbe.getBooleanValue() == false){
			return ProbeListColoring.COLOR_ALL_PROBELISTS;
		}
		else return null;
	}
	
	public boolean useTransparency() {
		return useTransparency.getBooleanValue();
	}
	
	public boolean invertTransparency() {
		return isOpacity.getBooleanValue();
	}
	
	public SplitView getRepresentation(){
		
		if(representation.getValueString().equals(MEAN)){
			return SplitView.mean;
		} else if(representation.getValueString().equals(MAX)){
			return SplitView.max;
		} else if(representation.getValueString().equals(MIN)){
			return SplitView.min;
		}
		return null;
	}
	
	protected void getInternalIdentifierLabel(){
		
		identString = "";
		String strand = "";
		if(this.getStrand().equals(Strand.PLUS)){
			strand = "+";
		} else if(this.getStrand().equals(Strand.MINUS)){
			strand = "-";
		} else {
			strand = "+/-";
		}

		identString = strand + " col=" + getColorProvider().getSourceName() + 
		(getColorProvider().getColoringMode() == 1?" ("+getRepresentation().toString()+")":"")
		+ (useTransparency()? ", t=" + getTransparencyProvider().getSourceName():"");
		
	}

	@Override
	public void setInitialExperiment(int experiment) {
		coloring.setExperiment(experiment);
		coloring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
		coloring.fireChanged();
		strand.setObjectValue(BOTH);
	}
}
