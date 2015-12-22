package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.realheatmap;

import java.util.LinkedList;
import java.util.List;

import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.genetics.basic.Strand;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class FullHeatmapTrackSettings extends AbstractTrackSettings{

	protected ObjectSelectionSetting<Object> representation;
	protected final static String MIN = "min";
	protected final static String MAX = "max";
	protected final static String MEAN = "mean";
//	protected BooleanSetting useTransparency, isOpacity;
//	protected ValueProvider transparencyProvider;
	protected ColorGradientSetting gradient;
	protected BooleanSetting mirror;
	
	public FullHeatmapTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);
		
		List<Integer> exps = new LinkedList<Integer>();
		for (int i=0; i!=Model.getViewModel().getDataSet().getMasterTable().getNumberOfExperiments(); ++i)
			exps.add(i);
		
		root
		.addSetting(strand = new ObjectSelectionSetting<Object>("Strand",
				null, 2, new Object[] { PLUS,MINUS,BOTH }).setLayoutStyle(
				ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS))
		.addSetting(mirror = new BooleanSetting("Mirror double stranded view", "Mirrored view displays the first experiment in the center of the track,\n" +
				"normal view displays the first experiment always at the top of each heatmap.", true))
//		.addSetting(new HierarchicalSetting("Graphical representations")
				.addSetting(gradient = new ColorGradientSetting("Heatmap Gradient",null,ColorGradient.createDefaultGradient(0, 1)).setLayoutStyle(ColorGradientSetting.LayoutStyle.FULL))
//				.addSetting(new HierarchicalSetting("Transparency")
//					.addSetting(useTransparency = new BooleanSetting("Use transparency",null, false))
//					.addSetting(isOpacity = new BooleanSetting("Interpret values as opacity","Check if large values mean less transparency", false))
//					.addSetting((transparencyProvider = new ValueProvider(model.getViewModel(),"Data source")).getSetting())
//				)
//				.setLayoutStyle(LayoutStyle.TABBED)
//			)
		.addSetting(representation = new ObjectSelectionSetting<Object>("Representation",
				null, 2, new Object[] { MIN, MAX, MEAN}).setLayoutStyle(
				ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS));
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

		identString = strand;// + (useTransparency()? ", t=" + getTransparencyProvider().getSourceName():"");
		
	}
	
	public ColorGradient getGradient() {
		return gradient.getColorGradient();
	}

	@Override
	public void setInitialExperiment(int experiment) {
		// ignore
		
	}
	
//	public boolean useTransparency() {
//		return useTransparency.getBooleanValue();
//	}
//	
//	public boolean invertTransparency() {
//		return isOpacity.getBooleanValue();
//	}
	
	public boolean mirrored() {
		return mirror.getBooleanValue();
	}
	
//	
//	public ValueProvider getTransparencyProvider() {
//		return transparencyProvider;
//	}
//	
	
	
}
