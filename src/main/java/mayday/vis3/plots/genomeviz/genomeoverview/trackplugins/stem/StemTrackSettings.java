package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.stem;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.genetics.basic.Strand;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.ValueProvider;
import mayday.vis3.ValueProvider.ExperimentProvider;
import mayday.vis3.ValueProvider.Provider;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class StemTrackSettings extends AbstractTrackSettings{
	
	protected ValueProvider heightProvider, transparencyProvider;
	protected BooleanSetting useTransparency, isOpacity;
	
	public StemTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);		

		root
			.addSetting(strand = new ObjectSelectionSetting<Object>("Strand",
						null, 0, new Object[] { PLUS,MINUS }).setLayoutStyle(
						ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS))
			.addSetting(new HierarchicalSetting("Graphical representations")
				.addSetting((heightProvider = new ValueProvider(model.getViewModel(),"Height")).getSetting())
				.addSetting(new HierarchicalSetting("Transparency")
					.addSetting(useTransparency = new BooleanSetting("Use transparency",null, false))
					.addSetting(isOpacity = new BooleanSetting("Interpret values as opacity","Check if large values mean less transparency", false))
					.addSetting((transparencyProvider = new ValueProvider(model.getViewModel(),"Data source")).getSetting())
				)
				.addSetting(coloring.getSetting())
				.setLayoutStyle(LayoutStyle.TABBED)
			);
		
		
	}

	public ValueProvider getHeightProvider() {
		return heightProvider;
	}
	
	public ValueProvider getTransparencyProvider() {
		return transparencyProvider;
	}
	
	public boolean useTransparency() {
		return useTransparency.getBooleanValue();
	}
	
	public boolean invertTransparency() {
		return isOpacity.getBooleanValue();
	}

	public int getExperimentForTooltip() {
		Provider p = heightProvider.getProvider();
		if (p instanceof ExperimentProvider) {
			return ((ExperimentProvider)p).getExperiment();
		}
		return coloring.getExperiment();
	}

	
	protected void getInternalIdentifierLabel(){
		String strand = "";
		if(this.getStrand().equals(Strand.PLUS)){
			strand = "+";
		} else {
			strand = "-";
		}

		identString = strand + " h=" + getHeightProvider().getSourceName()
		+ (useTransparency()? ", t=" + this.getTransparencyProvider().getSourceName():"")
		+ ", col="+coloring.getSourceName();
	}

	@Override
	public void setInitialExperiment(int experiment) {
		coloring.setExperiment(experiment);
		coloring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
		coloring.fireChanged();
		heightProvider.setProvider(heightProvider.new ExperimentProvider(experiment));
	}
	
	
}
