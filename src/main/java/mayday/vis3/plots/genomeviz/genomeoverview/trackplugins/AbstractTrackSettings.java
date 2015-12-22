package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins;

import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.LogixVizColorProvider;

public abstract class AbstractTrackSettings extends Settings {

	protected AbstractTrackPlugin tp;

	// Strand
	protected ObjectSelectionSetting<Object> strand;
	protected final static String PLUS = "forward";
	protected final static String MINUS = "backward";
	protected final static String BOTH = "both";

	protected LogixVizColorProvider coloring;
	protected GenomeOverviewModel model;

	protected StringSetting userLabel;
	protected BooleanSetting showInternalLabel;
	protected String identString = "";

	public AbstractTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(new HierarchicalSetting("Track Settings"), null);
		
		HierarchicalSetting labelling = new HierarchicalSetting("Track Label");
		labelling.addSetting(showInternalLabel = new BooleanSetting("Show internal description",
				"The internal description usually provides some information about what the track is showing.",true));
		labelling.addSetting(userLabel = new StringSetting("Track Label:",null,""));
		root.addSetting(labelling);
		
		tp = Tp;
		root.addChangeListener(tp);

		model = Model;
		coloring = model.getNewColorProvider();
	}

	public Strand getStrand() {
		if (strand==null)
			return null;
		if (strand.getValueString().equals(PLUS)) {
			return Strand.PLUS;
		} else if (strand.getValueString().equals(MINUS)) {
			return Strand.MINUS;
		} else if (strand.getValueString().equals(BOTH)) {
			return Strand.BOTH;
		}
		return null;
	}
	
	public void setStrand(Strand aStrand) {
		if (strand==null)
			return;
		switch(aStrand) {
		case PLUS: 
			strand.setStringValue(PLUS);
			break;
		case MINUS: 
			strand.setStringValue(MINUS);
			break;
		case BOTH: 
			strand.setStringValue(BOTH);
			break;
		}
	}

	public String getTrackLabel() {
		String ret="";
		
		if (isIdentificator()) {
			getInternalIdentifierLabel();
			ret = identString;
		}
		
		if (userLabel.getStringValue().length()>0)
			ret = userLabel.getStringValue()+(isIdentificator()?(" ("+ret+")"):ret);
		
		return ret;
	}

	protected void getInternalIdentifierLabel() {

	}

	public boolean isIdentificator() {
		return showInternalLabel.getBooleanValue();
	}

	public abstract void setInitialExperiment(int experiment);

	public LogixVizColorProvider getColorProvider() {
		return coloring;
	}
	
	public int getExperimentForTooltip() {
		return coloring.getExperiment();
	}
	
	public void removeNotify() {
		if (coloring!=null)
			coloring.removeNotify();
	}
}
