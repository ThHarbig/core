package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.sequence;

import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.sequences.SequenceContainer;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;

public class ExtraSequenceTrackSettings extends SequenceTrackSettings {
	
	protected SequenceContainer mySC;
	
	public ExtraSequenceTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);		
		mySC = new SequenceContainer(ChromosomeSetContainer.getDefault());
	}

	protected void getInternalIdentifierLabel(){
		identString = "Additional sequence data ";
	}
	
	public SequenceContainer getData() {
		return mySC;
	}
	
	public String getButtonName() {
		return "Import sequence data...";
	}
	
}
