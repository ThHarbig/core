package mayday.vis3.plots.genomeviz.genomeoverview.menues;

import java.awt.Component;
import java.util.Collections;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.gui.actions.GoToProbeAction;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.SearchProbe;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;

public class MenuModel implements SettingChangeListener {

	protected GenomeOverviewModel model = null;
	protected Controller c; 
//	protected PopupMenu_OnBackground popupMenu;
//	protected PopupMenu_OnTrack proppop;
	protected ChromeSelectionMenu chromeSel;
//	protected Labeling labeling;
//	protected TrackSelection trackSelection;
	
	public MenuModel(GenomeOverviewModel chromeModel, Controller controller){
		this.model = chromeModel;
		this.c = controller;
//		popupMenu = new PopupMenu_OnBackground(chromeModel, c, this);
//		proppop = new PopupMenu_OnTrack(chromeModel, c, this);
		chromeSel = new ChromeSelectionMenu(this, chromeModel, controller);
//		trackSelection = new TrackSelection(this);
	}
	
//	public TrackSelectionSetting getTrackSelectionSetting(){
//		return trackSelection.getSetting();
//	}
	
	public void removeNotify() {
//		trackSelection.removeNotify();
	}
	
//	public LabelSetting getLabelingSetting(){
//		if(model.getSelectionModel().getSelectedTrack() instanceof Track){
//			labeling = new Labeling(((Track)model.getSelectionModel().getSelectedTrack()),model);
//			return labeling.getSetting();
//		}
//		return null;
//	}

	public void openPopupMenu_Background(Component component,int x,int y) {
		TrackPopupMenu popupMenu = new TrackPopupMenu(model, c, this);
		popupMenu.init();
		popupMenu.show(component,x, y);
	}
	
	public void openPopupMenu_Track(Component component,int x,int y) {
		TrackPopupMenu proppop = new TrackPopupMenu(model, c, this);
		ITrack tp = null;
		ITrack panel = model.getSelectionModel().getSelectedTrack();
		if (panel != null) {
			if (panel instanceof ITrack) {
				tp = (ITrack) panel;
			}
		}
		proppop.setTrack(tp);
		proppop.init();
		proppop.show(component,x, y);
	}
	
	public JMenu getSpecChrSelMenu(){
		return chromeSel.speciesAndChromeSelectionMenu();
	}

	@SuppressWarnings("serial")
	public JMenu findProbe_window() {
		JMenu menu = new JMenu( "Find" );
		
		// Find a single probe
		menu.add(new GoToProbeAction() {

			public boolean goToProbe(String probeIdentifier) {
				
				Probe pb_searched = SearchProbe.getSearchedProbe(probeIdentifier, model);
				if(pb_searched != null){
					int posX_start = -1;
					int posX_end = -1;
					int startpos = (int)model.getStartPosition(pb_searched);
					int endpos = (int)model.getEndPosition(pb_searched);
					int chromestartpos = (int)model.getChromosomeStart();
					int chromeendpos = (int)model.getChromosomeEnd();					
					int panelWidth = model.getWidth_paintingpanel_reduced();
					
					if(startpos > chromestartpos){
						startpos = startpos-1;
					}
					
					posX_start = DataMapper.getXPosition(startpos, panelWidth, 
							chromestartpos, chromeendpos);
					
					Set<Probe> set = Collections.emptySet();
					set = fillListWithProbes(posX_start,panelWidth);
					while(!set.isEmpty() && (set.contains(pb_searched))){
						posX_start--;
						if(posX_end==0)break;
						set = fillListWithProbes(posX_start,panelWidth);
					}
					
					for (int i = posX_start; i != panelWidth; ++i) {
						set = fillListWithProbes(i,panelWidth);
						if(!set.isEmpty()){
							if(set.contains(pb_searched)){
								posX_start = i;
								break;
							} 
						}
					}
	
					if(endpos < chromeendpos){
						endpos = endpos+1;
					}
					posX_end = DataMapper.getXPosition(endpos, panelWidth, 
							chromestartpos, chromeendpos);
					
					set = Collections.emptySet();
					set = fillListWithProbes(posX_end,panelWidth);
					while(!set.isEmpty() && (set.contains(pb_searched))){
						posX_end++;
						if(posX_end==(panelWidth-1))break;
						set = fillListWithProbes(posX_end,panelWidth);
					}
					
					for (int i = posX_end; i >= 0; --i) {
						set = fillListWithProbes(i,panelWidth);
						if(!set.isEmpty()){
							if(set.contains(pb_searched)){
								posX_end = i;
								break;
							} 
						}
					}
					
					int[] positions_x = null;
					if(posX_start >= 0 
						&& posX_end >= 0){
						
						if(posX_start<=posX_end){
							positions_x = new int[2];
							positions_x[0] = posX_start;
							positions_x[1] = posX_end;
						}
					}	

					if(positions_x!=null){
						model.getSelectionModel().setFoundProbe(pb_searched,positions_x);
						return SearchProbe.searchTracksAndScroll(model, c);
					}
				} 
				return false;
			}
		});
		// find single probe in separate window
		//menu.add(new FindProbe("Detach this menu", master));

		return ( menu );
	}
	
	private Set<Probe> fillListWithProbes(double pos_x, int panelWidth) {

		Set<Probe> set = Collections.emptySet();
//		long length_chrome = chromeModel.getEndPositionOfChromosome() -chromeModel.getStartPositionOfChromosome();
		FromToPosition ftp = new FromToPosition();
		DataMapper.getBpOfView(panelWidth,model,pos_x,ftp);
		if(ftp.isValid()){
			long from = ftp.getFrom();
			long to = ftp.getTo();
			
			if(from > to){
				System.err.println("From Position is higher than to position");
				return Collections.emptySet();
			} else if(from == to){
					set = model.getBothProbes(from);
			} else {
					set = model.getBothProbes(from, to);
			}
		}
		return set;
	}

	public void stateChanged(SettingChangeEvent e) {
		try {
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){

				}
			});
			
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		}
	}
}
