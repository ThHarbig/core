package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.locusinfo;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.genetics.basic.Strand;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapSetting;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class LocusTrackSettings extends AbstractTrackSettings{
	
	protected LocusMapSetting lms;
	protected BooleanSetting exons;
	
	public LocusTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);		

		root
		.addSetting(new HierarchicalSetting("Locus Track")
			.addSetting(exons = new BooleanSetting("Show exon structure if available",null,false))
			.addSetting(lms = new LocusMapSetting())
		).setLayoutStyle(LayoutStyle.PANEL_VERTICAL);
		
	}

	public LocusMap getLocusMap() {
		return lms.getLocusMap();
	}

	protected void getInternalIdentifierLabel(){
		if (lms.getLocusMap()!=null) {
			identString = "Locus Data: "+lms.getLocusMap().getName();
		}
	}
	
	public Strand getStrand() {
		return Strand.BOTH;
	}

	@Override
	public void setInitialExperiment(int experiment) {
		// never called
	}
	
	public boolean showExons() {
		return exons.getBooleanValue();
	}
	
	
}
