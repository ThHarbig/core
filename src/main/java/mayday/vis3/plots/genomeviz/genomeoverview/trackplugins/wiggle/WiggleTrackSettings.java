package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.wiggle;

import java.awt.Color;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class WiggleTrackSettings extends AbstractTrackSettings{
	
	protected WiggleData wiggle;

	protected FilesSetting wigFiles;
	protected StringSetting species;
	protected ColorSetting coloring;
	protected ColorSetting naColor;

	protected SelectableHierarchicalSetting method;
	protected final static String LINEMODE="as wiggle line";
	protected final static String BLOCKMODE="as loci (filtering)";
	
	protected HierarchicalSetting lineSett, blockSett;
	
	// for the wiggle line method
	protected DoubleSetting minRange, maxRange;
	protected BooleanSetting computeRange;
	
	// for the block method
	protected DoubleSetting minFilter, maxFilter;
//	protected IntSetting minBP, maxBP;
	
	public WiggleTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);		

		wigFiles = new FilesSetting("Wiggle files", "Multiple files can be selected, usually one per chromosome.", null, false, null);
		species = new StringSetting("Species name","Wig files do not contain species names, please supply a name here","");
		coloring = new ColorSetting("Color", "Select which color to use for plotting", Color.black);
		naColor = new ColorSetting("NA color", "Select which color to use for indicating NA colors", Color.red);

		lineSett = new HierarchicalSetting(LINEMODE)
		.addSetting(computeRange = new BooleanSetting("Use min and max from data", null, true))
		.addSetting(minRange = new DoubleSetting("Minimal value",null, Double.NEGATIVE_INFINITY))
		.addSetting(maxRange = new DoubleSetting("Maximal value",null, Double.POSITIVE_INFINITY))
		;
		
		blockSett = new HierarchicalSetting(BLOCKMODE)
		.addSetting(minFilter = new DoubleSetting("Minimal value",null, Double.NEGATIVE_INFINITY))
		.addSetting(maxFilter = new DoubleSetting("Maximal value",null, Double.POSITIVE_INFINITY))
//		.addSetting(minBP = new IntSetting("Minimal length (bp)",null, 1,1,null,true,false))
//		.addSetting(maxBP = new IntSetting("Maximal length (bp)",null, Integer.MAX_VALUE,1,null,true,true))
		;
	
		method = new SelectableHierarchicalSetting("Drawing method", null, 0, new Object[]{lineSett, blockSett});
		
		root
		.addSetting(new HierarchicalSetting("Wiggle Track")
			.addSetting(species)
			.addSetting(wigFiles)
			.addSetting(coloring)
			.addSetting(naColor)
			.addSetting(method)
			.setCombineNonhierarchicalChildren(true)
		).setLayoutStyle(LayoutStyle.PANEL_VERTICAL);
		 
		wiggle = new WiggleData(wigFiles, species, Model.getDAO().getContainer());
	}

	protected void getInternalIdentifierLabel(){
		identString = "Wiggle files "+wigFiles.getFileNames();
	}
	
	public Strand getStrand() {
		return Strand.BOTH;
	}
	
	public WiggleData getWiggle() {
		return wiggle;
	}
	
	public Color getColor() {
		return coloring.getColorValue();
	}
	
	public Color getNAColor() {
		return naColor.getColorValue();
	}
	
	protected String getRenderingMethod() {
		if (method.getSelectedIndex()==0)
			return LINEMODE;
		return BLOCKMODE;
	}
	
//	protected int[] getSizeFilter() {
//		return new int[]{minBP.getIntValue(), maxBP.getIntValue()};		
//	}
	
	protected double[] getValueFilter() {
		return new double[]{minFilter.getDoubleValue(), maxFilter.getDoubleValue()};
	}

	protected double[] getValueRange() {
		if (computeRange.getBooleanValue())
			return null;
		return new double[]{minRange.getDoubleValue(), maxRange.getDoubleValue()};
	}
	
	@Override
	public void setInitialExperiment(int experiment) {
		// don't care
	}
	
}
