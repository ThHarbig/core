package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.sequence;

import java.util.HashMap;

import javax.swing.AbstractAction;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class ExtraSequenceTrackPlugin extends AbstractTrackPlugin{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.transkriptorium.sequencetrack.external", 
				new String[0], 
				MC3,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Rendering external/additional sequence data.",
				"Additional Sequence Data");
		return pli;
	}

	public ExtraSequenceTrackPlugin() {}

	protected AbstractTrackSettings makeSetting() {
		return new ExtraSequenceTrackSettings(model, this);
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
