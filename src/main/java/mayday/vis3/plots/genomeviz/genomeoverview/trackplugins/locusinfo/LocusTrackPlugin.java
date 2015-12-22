package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.locusinfo;

import java.util.HashMap;

import javax.swing.AbstractAction;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class LocusTrackPlugin extends AbstractTrackPlugin{

	/* we are an plugin
	and extend trackplugin
	 */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.core.ChromeOverview.LocusTrackPlugin", 
				new String[0], 
				MC2,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Rendering locus information as informational track.",
				"Locus Information");
		return pli;
	}

	public LocusTrackPlugin() {}

	protected AbstractTrackSettings makeSetting() {
		return new LocusTrackSettings(model, this);
	}
	
	protected AbstractTrackRenderer makeRenderer() {
		return new LocusTrackRenderer(model, this);
	}
	
	protected int getDefaultHeight() {
		return 60;
	}
	
	public AbstractAction getAddTracksForAllExperimentsAction(){
		return null;
	}
}
