package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.sequence;

import java.util.HashMap;

import javax.swing.AbstractAction;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class SequenceTrackPlugin extends AbstractTrackPlugin{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.transkriptorium.sequencetrack", 
				new String[0], 
				MC2,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Rendering chromosome sequence files.",
				"Chromosome Sequence");
		return pli;
	}

	public SequenceTrackPlugin() {}

	protected AbstractTrackSettings makeSetting() {
		return new SequenceTrackSettings(model, this);
	}
	
	protected AbstractTrackRenderer makeRenderer() {
		return new SequenceTrackRenderer(model, this);
	}
	
	protected int getDefaultHeight() {
		return 45;
	}
	
	public AbstractAction getAddTracksForAllExperimentsAction(){
		return null;
	}
}
