package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.profile;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.vis3.ValueProvider;
import mayday.vis3.ValueProvider.ExperimentProvider;
import mayday.vis3.ValueProvider.Provider;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class ProfileTrackSettings extends AbstractTrackSettings{

	protected ObjectSelectionSetting<Object> representation;
	protected ValueProvider heightProvider;
	protected final static String MIN = "min";
	protected final static String MAX = "max";
	protected final static String MEAN = "mean";
	protected ColorSetting colP, colM;
	
	public ProfileTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);
		
		List<Integer> exps = new LinkedList<Integer>();
		for (int i=0; i!=Model.getViewModel().getDataSet().getMasterTable().getNumberOfExperiments(); ++i)
			exps.add(i);
		
		root
		.addSetting(colP = new ColorSetting("Forward Strand Color",null, Color.blue))
		.addSetting(colM = new ColorSetting("Backward Strand Color",null, Color.red))
		.addSetting((heightProvider = new ValueProvider(model.getViewModel(),"Height")).getSetting())
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
	
	public Color getPlusColor() {
		return colP.getColorValue();
	}
	
	public Color getMinusColor() {
		return colM.getColorValue();
	}

	
	public int getExperimentsForPlotting() {
		Provider p = heightProvider.getProvider();
		if (p instanceof ExperimentProvider) {
			return ((ExperimentProvider)p).getExperiment();
		}
		return 0;
	}
	
	protected void getInternalIdentifierLabel(){
		identString = heightProvider.getSourceName();
	}
	
	public ValueProvider getHeightProvider() {
		return heightProvider;
	}

	@Override
	public void setInitialExperiment(int experiment) {
		heightProvider.setProvider(heightProvider.new ExperimentProvider(experiment));
	}
	
}
