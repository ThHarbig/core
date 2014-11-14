package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.wiggle;

import java.util.HashMap;

import javax.swing.AbstractAction;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class WiggleTrackPlugin extends AbstractTrackPlugin{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.transkriptorium.wiggletrack", 
				new String[0], 
				MC3,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Rendering data from wiggle files.",
				"Wiggle file data");
		return pli;
	}

	public WiggleTrackPlugin() {}

	protected AbstractTrackSettings makeSetting() {
		return new WiggleTrackSettings(model, this);
	}
	
	protected AbstractTrackRenderer makeRenderer() {
		return new WiggleTrackRenderer(model, this);
	}
	
	protected int getDefaultHeight() {
		return 150;
	}
	
	public AbstractAction getAddTracksForAllExperimentsAction(){
		return null;
	}
}
