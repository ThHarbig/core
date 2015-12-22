package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins;

import javax.swing.AbstractAction;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.plots.genomeviz.genomeoverview.ConstantData;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.AddAllExperimentsAction;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.AddTrackAction;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.TrackPositioner;

public abstract class AbstractTrackPlugin extends AbstractPlugin implements SettingChangeListener {

	public final static String MC0 = "Track Renderer/Internal";
	public final static String MC1 = "Track Renderer/Probe Data";
	public final static String MC2 = "Track Renderer/Meta Data";
	public final static String MC3 = "Track Renderer/External Data";
	
	
	protected boolean initialized = false;
	protected GenomeOverviewModel model;
	protected Controller c;
	
	protected ITrack track;
	protected AbstractTrackSettings trackSettings;
	protected AddTrackAction absAddTrackAction;
	protected ITrackRenderer trackRenderer;
	protected PaintingPanel paintingpanel;
	
	protected abstract AbstractTrackSettings makeSetting();
	protected abstract AbstractTrackRenderer makeRenderer();
	
	public void init(){};

	public final ITrack getTrack() {
		if(track==null){
			TrackPositioner pp = model.getPanelPositioner();
			int freeIndex = pp.getNextFreeIndex();
			int y_pos = pp.getPositionInPanel(freeIndex);
			
			track = makeTrack(
					model, y_pos, freeIndex,model.getWidth_LayeredPane(),
					getDefaultHeight(), this);
		} 	
		
		return track;
	}
	
	protected int getDefaultHeight() {
		return ConstantData.INITIAL_TRACK_HEIGHT;
	}
	
	protected Track makeTrack(GenomeOverviewModel model, int y_pos, int index, int width, int height, AbstractTrackPlugin plugin) {
		return new Track(model, y_pos, index, width, height, this);
	}
	
	public final void init(GenomeOverviewModel Model, Controller C){
		model = Model;
		c = C;
	}
	
	public final AbstractTrackSettings getTrackSettings() {
		if(trackSettings==null)
			trackSettings = makeSetting();
		return trackSettings;
	}
	
	public AbstractAction getAddTrackAction(){
		return new AddTrackAction(PluginManager.getInstance().getPluginFromClass(getClass()).getName(), model, c, this);
	}
	
	public AbstractAction getAddTracksForAllExperimentsAction(){
		return new AddAllExperimentsAction(model, c, PluginManager.getInstance().getPluginFromClass(getClass()));
	}
	
	public final ITrackRenderer getTrackRenderer(){
		return trackRenderer;
	}
	
	public String toString() {
    	return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
    }
	
	public void stateChanged(SettingChangeEvent e){
		internalInit();
		if(track != null){
			track.init();
			actualizeTrack();
			initialized = true;
		}
		/* sets label of track with the new settings */
		if(track!=null && trackSettings!=null)track.setNewLabel(trackSettings.getTrackLabel());
//		trackplugin.getTrack().setNewLabel(trackplugin.getTrackSettings().getNewLabel());
	}

	public boolean isInitialized() {
		return initialized;
	}

	protected PaintingPanel makePanel() {
		return new PaintingPanel(model, this);
	}
	
	public void internalInit() {
		if(paintingpanel==null){
			paintingpanel = makePanel();
			((Track)getTrack()).add(paintingpanel);
		}
		paintingpanel.setLocation(model.getLocation_paintingpanel_X(),
				model.getLocation_paintingpanel_Y());	
		if (trackRenderer==null)
			trackRenderer = makeRenderer();
	}

	public void repaintTrack() {
		if(paintingpanel!=null){
			paintingpanel.repaint();
			model.setLocationOfUserpanel();
		}
			
	}

	public void actualizeTrack() {
		if(track!=null){
			track.deleteBufferedImage();
			track.resizeTrackwidth();
			if(paintingpanel!=null){
				paintingpanel.resizePanel();
			}
			model.updateCache(this);
		}
	}

	// default to true
	public boolean isBufferedPaint() {
		return true;
	}
	
	public PaintingPanel getPaintingPanel() {
		return paintingpanel;
	}
	
	
}