package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.usageinfo;

import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.PaintingPanel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;

public class InfoTrackPlugin extends AbstractTrackPlugin{
	
	/* we are an plugin
	and extend trackplugin
	 */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.core.ChromeOverview.UserInfoPanel", 
				new String[0], 
				MC0,
				new HashMap<String, Object>(),
				"Christian Zipplies",
				"zipplies@informatik.uni-tuebingen.de",
				"Provides information on user interaction",
				"Usage information");
		return pli;
	}
	
	protected JEditorPane information;
	
	public InfoTrackPlugin() {
		information = new JEditorPane();
		information.setContentType("text/html");
		information.setText(
				"<font face='Arial' size=-1><b>Overview of possible mouse interactions</b><br>"
				+"<ul>" 
						+ "<li>Right-click on the background to add a track"
						+" <li>Control + Mouse wheel: Zoom view horizontally"
						+ "<li>On the tracks<br><ul>"
							+"<li>Left-click: Select probes"
							+"<li>Control + left-click: Add/remove probes to selection"
							+"<li>Shift + left-click: Select a range of probes"
							+"<li>Right-click for menu"
							+"<li>Control + Drag: Move track up/down in the track list"
							+"<li>Shift + Mouse wheel: Zoom track vertically"
							+"</ul>"
						+ "<li>On the <i>major</i> scale<br><ul>"
							+"<li>Left-click: Center view around this position"
							+"<li>Control + left-click: Move closest border of viewing region to this position"
							+"<li>Drag: Select region for viewing"
							+"</ul>"
						+"<li>On the <i>minor</i> scale<br><ul>"
							+"<li>Drag: Select region for viewing"
							+"<li>Control + Drag: Move minor scale up/down in the track list"
							+"</ul>"
						+"</ul>"
		);
		information.setEditable(false);
	}
	
	protected int getDefaultHeight() {
		return (int)information.getPreferredSize().getHeight()+50;
	}
	
	protected Track makeTrack(GenomeOverviewModel model, int y_pos, int index, int width, int height, AbstractTrackPlugin plugin) {
		return new InfoTrack(model, y_pos, index, width, height, this);
	}
	
	protected AbstractTrackSettings makeSetting() {
		return null;
	}

	public AbstractAction getAddTrackAction(){
		return null;
	}
	
	public AbstractAction getAddTracksForAllExperimentsAction(){
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
		return new InfoTrackPanel(model, this, information);
	}

}
