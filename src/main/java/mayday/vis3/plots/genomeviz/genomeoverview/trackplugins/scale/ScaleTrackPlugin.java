package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.scale;

import java.util.HashMap;

import javax.swing.AbstractAction;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.ConstantData;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.AddTrackAction;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.PaintingPanel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;

public class ScaleTrackPlugin extends AbstractTrackPlugin{
	
	/* we are an plugin
	and extend trackplugin
	 */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.core.ChromeOverview.ScaletrackPlugin", 
				new String[0], 
				MC0,
				new HashMap<String, Object>(),
				"Christian Zipplies",
				"zipplies@informatik.uni-tuebingen.de",
				"Track rendering like scale.",
				"Scale");
		return pli;
	}
	
	public ScaleTrackPlugin() {}
	
	protected Track makeTrack(GenomeOverviewModel model, int y_pos, int index, int width, int height, AbstractTrackPlugin plugin) {
		return new ScaleTrack(model, y_pos, index, width, height, this);
	}
	
	protected int getDefaultHeight() {
		return ConstantData.INITIAL_SCALA_HEIGHT-2;
	}
	
	protected AbstractTrackSettings makeSetting() {
		return new ScaleTrackSettings(model, this);
	}

	public AddTrackAction getAddTrackAction(){
		return null;
	}

	public void internalInit() {
		super.internalInit();
		paintingpanel.setLocation(model.getLocation_scalepaintingpanel_X(),
				model.getLocation_scalepaintingpanel_Y());
	}
	
	public boolean isBufferedPaint() {
		return false;
	}

	protected AbstractTrackRenderer makeRenderer() {
		return null;
	}
	
	protected PaintingPanel makePanel() {
		return new ScaleTrackPanel(model, this);
	}
	
	public AbstractAction getAddTracksForAllExperimentsAction(){
		return null;
	}

}
