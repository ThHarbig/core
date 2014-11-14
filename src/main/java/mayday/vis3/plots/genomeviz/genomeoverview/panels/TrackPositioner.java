package mayday.vis3.plots.genomeviz.genomeoverview.panels;


import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JPanel;

import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.plots.genomeviz.EnumManagerGO.ActionModes;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewLayeredPane;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.scale.ScaleTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.usageinfo.InfoTrackPlugin;


public class TrackPositioner implements SettingChangeListener {

	protected GenomeOverviewModel chromeModel;
	
	protected TreeMap<Integer,ITrack> listOfTracks = null;

	protected int nextFreePosition = 0;
	
	protected ITrack newTrack = null;
	protected AbstractTrackPlugin scalePlugin = null;
	
	protected BooleanSetting mySetting = new BooleanSetting("Free track placement","" +
			"Allow tracks to be moved anywhere, even if they overlap?\n" +
			"The default is to prevent overlaps.",false);	
	
	public TrackPositioner(GenomeOverviewModel ChromeModel){
		chromeModel = ChromeModel;
		listOfTracks = new TreeMap<Integer,ITrack>();
		mySetting.addChangeListener(this);
	}
	
	public int getTopIndex(){
		return 0;
	}
	
	public int getNextFreeIndex(){
		if(listOfTracks.isEmpty()){
			return 0;
		} else {
			return listOfTracks.lastKey()+1;
		}
	}

	
	public int getLastUsedIndex(){
		if(listOfTracks.isEmpty()){
			return 0;
		} else {
			
			return listOfTracks.lastKey();
		}
	}
	
	public Setting getSetting() {
		return mySetting;
	}
	
	/**
	 * change panel with index by actual panel.
	 * @param index
	 * @param track
	 */
	public void changeTrack(int index, ITrack track) {
		if (!listOfTracks.containsKey(index)) {
			System.err.println("POSITION " + index + " NOT contained");
		} else {
			newTrack = track;
			if (listOfTracks.get(index) instanceof ITrack) {
				ITrack panelToDelete = (ITrack) listOfTracks.get(index);
				if (panelToDelete != null) {
					if (panelToDelete.getParent() != null) {
						if (panelToDelete.getParent() instanceof GenomeOverviewLayeredPane) {
							GenomeOverviewLayeredPane comp = (GenomeOverviewLayeredPane) panelToDelete
									.getParent();
							comp.removeTrack(panelToDelete);
						}
					}
				}
				listOfTracks.put(index, track);
			} else {
				System.err.println("Panel to delete is not a TrackPanel");
			}
		}
		
		reorderLocationOfPanels();
	}

	
	public ITrack addNewTrack(AbstractTrackPlugin trackplugin){
		trackplugin.internalInit();
		newTrack = trackplugin.getTrack();
		int index = newTrack.getIndexInPane();
		
		if(!listOfTracks.containsKey(index)){
			listOfTracks.put(index, trackplugin.getTrack());
		} else {
			System.err.println("POSITION " + index + " just contained");
		}
		reorderLocationOfPanels();
		return newTrack;
	}

	public void deleteImagesOfTracks() {
			// for different tracks
			for(Integer key : listOfTracks.keySet()){
				ITrack track = listOfTracks.get(key);
				if(track instanceof ITrack){	
					ITrack tp = (ITrack)track;
					tp.deleteBufferedImage();
				}
			}
	}
	
	public void deleteImageOfTrack(ITrack tp) {
		tp.deleteBufferedImage();
	}
	
	public void resizeTracks() {
			// for different tracks
			for(Integer key : listOfTracks.keySet()){
				ITrack track = listOfTracks.get(key);
				if(track instanceof ITrack){	
					if(track.getTrackPlugin()!=null)
						(track.getTrackPlugin()).actualizeTrack();
				}
			}
			chromeModel.fireChanged();
	}
	
	public void setLocationOfUserpanel(JPanel panel) {
			if(panel instanceof ITrack){	
				ITrack tp = (ITrack)panel;
					tp.setLocationOfUserpanel();
			}
		this.chromeModel.fireChanged();
	}
	
	public void setLocationOfUserpanel() {
		for(Integer key : listOfTracks.keySet()){
			ITrack panel = listOfTracks.get(key);
			if(panel instanceof ITrack){	
				ITrack tp = (ITrack)panel;
					tp.setLocationOfUserpanel();
			}
		}
		this.chromeModel.fireChanged();
	}
	
	public void resizeTrackheight(ITrack tp, int newHeight) {
		tp.resizeTrackheight(newHeight);
		reorderLocationOfPanels();
		chromeModel.fireChanged();
	}
	
	public void updateTracks() {
			// for different tracks
			for(Integer key : listOfTracks.keySet()){
				ITrack panel = listOfTracks.get(key);
				if(panel instanceof ITrack){	
					ITrack tp = (ITrack)panel;
					if(!Track.isScaleTrack(tp)){
						chromeModel.updateCache(tp.getTrackPlugin());
					} 
				}
			}
	}
	
	public void updateTrack(ITrack tp) {
		chromeModel.updateCache(tp.getTrackPlugin());
	}

	
	public void repaintTracks() {
		// for different tracks
		for(Integer key : listOfTracks.keySet()){
			ITrack panel = listOfTracks.get(key);
			if(panel instanceof ITrack){	
				ITrack tp = (ITrack)panel;
				tp.getTrackPlugin().repaintTrack();
			}
		}
	}
	
	public ITrack getTrackToAdd() {
		return newTrack;
	}
	
	public void checkTracks(ITrack changedPanel) {
		int oldIndex = changedPanel.getIndexInPane();
		
		ArrayList<ITrack> list = new ArrayList<ITrack>();
		
		for(Integer index : listOfTracks.keySet()){
			list.add(listOfTracks.get(index));
		}
		
		if(changedPanel.getActionMode() == ActionModes.MOVE_UP){
			moveUp(changedPanel, oldIndex, list);
		} else if(changedPanel.getActionMode() == ActionModes.MOVE_DOWN){
			moveDown(changedPanel, oldIndex, list);
		} else if(changedPanel.getActionMode() == ActionModes.MOVE_TO_TOP){
			moveToTop(changedPanel, oldIndex, list);
		} else if(changedPanel.getActionMode() == ActionModes.MOVE_TO_BOTTOM){
			moveToBottom(changedPanel, oldIndex, list);
		} else if(changedPanel.getActionMode() == ActionModes.DELETE){
			remove(changedPanel, oldIndex, list);
		}
	}

	public void removeTrack(AbstractTrackPlugin plugin) {
		if (plugin != null) {
			ITrack track = plugin.getTrack();
			track.setActionMode(ActionModes.DELETE);
		}
	}
	
	public void removeTracks() {
		removeTracks(false);
	}
	
	public void removeTracks(boolean all) {
		TreeMap<Integer,ITrack> list = new TreeMap<Integer,ITrack>(listOfTracks);
		for(Integer key: list.keySet()){
			if(list.get(key) instanceof ITrack){
				ITrack tp = (ITrack)(list.get(key));
				if(all || !Track.isScaleTrack(tp)){
					tp.setActionMode(ActionModes.DELETE);
				}	
			}
		}
		list.clear();
	}
	
	public void moveUp(ITrack panelToRemove){
		if(panelToRemove instanceof ITrack){
			ITrack tp = (ITrack)panelToRemove;
			tp.setActionMode(ActionModes.MOVE_UP);
		}
	}
	
	public void moveDown(ITrack panelToRemove){
		if(panelToRemove instanceof ITrack){
			ITrack tp = (ITrack)panelToRemove;
			tp.setActionMode(ActionModes.MOVE_DOWN);
		}
	}
	
	public void moveToTop(ITrack panelToRemove){
		if(panelToRemove instanceof ITrack){
			ITrack tp = (ITrack)panelToRemove;
			tp.setActionMode(ActionModes.MOVE_TO_TOP);
		}
	}
	
	public void moveToBottom(ITrack panelToRemove){
		if(panelToRemove instanceof ITrack){
			ITrack tp = (ITrack)panelToRemove;
			tp.setActionMode(ActionModes.MOVE_TO_BOTTOM);
		}
	}

	
	private void remove(ITrack changedPanel, int oldIndex,
			ArrayList<ITrack> list) {

		for(ITrack panel: list){
			if(panel instanceof ITrack){
				ITrack epanel = (ITrack)panel;
				int actIndex = epanel.getIndexInPane();
				
				// this is the panel at this position
				if(actIndex < oldIndex){
					// do nothing
				}
				// this is the panel to move
				else if(actIndex == oldIndex){
					epanel.setDeleteFlag(true);
				} 
				else if(actIndex > oldIndex){
					epanel.setIndex(actIndex-1);
				}
				
			}
		}
		
		listOfTracks.clear();
		ITrack panelToDelete = null;
		
		for(ITrack panel: list){
			ITrack epanel = (ITrack)panel;
			int actIndex = epanel.getIndexInPane();
			
			if(!epanel.getDeleteFlag()){
				listOfTracks.put(actIndex, epanel);
			} else {
				panelToDelete = epanel;
			}
		}
		
		if(panelToDelete != null) {
			if(panelToDelete.getParent() != null){
				if(panelToDelete.getParent() instanceof GenomeOverviewLayeredPane){
					GenomeOverviewLayeredPane comp = (GenomeOverviewLayeredPane)panelToDelete.getParent();
					comp.removeTrack(panelToDelete);
					AbstractTrackSettings ats = panelToDelete.getTrackPlugin().getTrackSettings();
					if (ats!=null)
						ats.removeNotify();
				}
			}
		}
		
		

		reorderLocationOfPanels();
		changedPanel.resetActionMode();
	}


	public void movePanel(ITrack trackPanel, int newPositionY){
		int oldIndex = trackPanel.getIndexInPane();
		int heightTP = trackPanel.getHeight();
		
		if(newPositionY <= chromeModel.getUnusableSpace_y()){
			newPositionY = chromeModel.getUnusableSpace_y();
		}
		
		int newIndex = getTheRightIndexInPanel(newPositionY,heightTP,oldIndex);
		
		if(newIndex > getLastUsedIndex()){
			newIndex = getLastUsedIndex();
		}
		
		ArrayList<ITrack> list = new ArrayList<ITrack>();
		
		for(Integer index : listOfTracks.keySet()){
			list.add(listOfTracks.get(index));
		}
		
		if(oldIndex!=newIndex){
			// index changed so set position
			
			// moved up
			if(newIndex < oldIndex){
				
				for (ITrack panel : list) {
					if (panel instanceof ITrack) {
						ITrack epanel = (ITrack) panel;
						int actIndex = epanel.getIndexInPane();

						if (actIndex < newIndex) {
							// do nothing
						}
						// move one pos down
						else if (actIndex == newIndex) {
							epanel.setIndex(actIndex + 1);
						}
						// this is the panel at this position
						else if (actIndex > newIndex) {

							if (actIndex < oldIndex) {
								epanel.setIndex(actIndex + 1);
							} else if (actIndex == oldIndex) {
								epanel.setIndex(newIndex);
							} else if (actIndex > oldIndex) {
								// do nothing
							}
						}
					}
				}
			}
			// moved down
			else if(newIndex > oldIndex){
				
				for (ITrack panel : list) {
					if (panel instanceof ITrack) {
						ITrack epanel = (ITrack) panel;
						int actIndex = epanel.getIndexInPane();

						if (actIndex < oldIndex) {
							// do nothing
						}
						// move positions down
						else if (actIndex == oldIndex) {
							epanel.setIndex(newIndex);
						}
						// this is the panel at this position
						else if (actIndex > oldIndex) {

							if (actIndex < newIndex) {
								epanel.setIndex(actIndex - 1);
							} else if (actIndex == newIndex) {
								epanel.setIndex(actIndex - 1);
							} else if (actIndex > newIndex) {
								// do nothing
							}
						}
					}
				}
			}

		}else {
			// index is equal so reset position
			
			for (ITrack panel : list) {
				if (panel instanceof ITrack) {
					ITrack epanel = (ITrack) panel;
					int actIndex = epanel.getIndexInPane();
					epanel.setIndex(actIndex);
				}
			}
		}
		
		listOfTracks.clear();
		
		reorderIndexOfPanels(list);

		reorderLocationOfPanels();
	}
	
	


	private void moveToTop(ITrack changedPanel, int oldIndex,
			ArrayList<ITrack> list) {
		int newIndex = 0;
		
		for(ITrack panel: list){
			if(panel instanceof ITrack){
				ITrack epanel = (ITrack)panel;
				int actIndex = epanel.getIndexInPane();
				
				// this is the panel at this position
				if(actIndex < oldIndex){
					epanel.setIndex(actIndex+1);
				}
				// this is the panel to move
				else if(actIndex == oldIndex){
					epanel.setIndex(newIndex);
				} 
				else if(actIndex > oldIndex){
					// do nothing
				}
			}
		}
		
		listOfTracks.clear();
		
		reorderIndexOfPanels(list);
		reorderLocationOfPanels();
		changedPanel.resetActionMode();
	}


	private void moveToBottom(ITrack changedPanel, int oldIndex,
			ArrayList<ITrack> list) {
		int newIndex = this.getNextFreeIndex() - 1;
		
		for(ITrack panel: list){
			if(panel instanceof ITrack){
				ITrack epanel = (ITrack)panel;
				int actIndex = epanel.getIndexInPane();
				
				// this is the panel at this position
				if(actIndex < oldIndex){
					// do nothing
				}
				// this is the panel to move
				else if(actIndex == oldIndex){
					epanel.setIndex(newIndex);
				} 
				else if(actIndex > oldIndex){
					epanel.setIndex(actIndex-1);
				}
			}
		}
		
		listOfTracks.clear();
		
		reorderIndexOfPanels(list);

		reorderLocationOfPanels();
		changedPanel.resetActionMode();
	}

	private void moveDown(ITrack changedPanel, int oldIndex,
			ArrayList<ITrack> list) {
		int newIndex = oldIndex+1;
		if(newIndex >= this.getNextFreeIndex()){
			newIndex = oldIndex;
		}

		
		for(ITrack panel: list){
			if(panel instanceof ITrack){
				ITrack epanel = (ITrack)panel;
				int actIndex = epanel.getIndexInPane();
				
				// this is the panel at this position
				if(actIndex == oldIndex +1){
					epanel.setIndex(actIndex-1);
				}
				// this is the panel to move
				else if(actIndex == oldIndex){
					epanel.setIndex(newIndex);
				}

			}
		}
		
		listOfTracks.clear();
		
		reorderIndexOfPanels(list);

		reorderLocationOfPanels();

		changedPanel.resetActionMode();
	}
	
	/* this is implemented to allow interactive switching between 
	 * -- tracks that snap to positions and don't overlap, and
	 * -- tracks that can be placed anywhere the user likes
	 */
	public boolean snapTracks() {
		return !mySetting.getBooleanValue();
	}
	

	private void reorderLocationOfPanels() {
		if (!snapTracks())
			return;
			
		int posY = this.chromeModel.getUnusableSpace_y();
		for(Integer index : listOfTracks.keySet()){
			if(listOfTracks.get(index) instanceof ITrack){
				ITrack panel = (ITrack)listOfTracks.get(index);
				panel.setLocationInPanel(posY);
				posY = posY + panel.getHeight();
			}
		}
	}
	
	private void moveUp(ITrack changedPanel, int oldIndex,
			ArrayList<ITrack> list) {
		int newIndex = oldIndex-1;
		if(newIndex < 0){
			newIndex = 0;
		}
		
		for(ITrack panel: list){
			if(panel instanceof ITrack){
				ITrack epanel = (ITrack)panel;
				int actIndex = epanel.getIndexInPane();
				
				// this is the panel at this position
				if(actIndex == oldIndex -1){
					epanel.setIndex(actIndex+1);
				}
				// this is the panel to move
				else if(actIndex == oldIndex){
					epanel.setIndex(newIndex);
				}
			}
		}
		
		listOfTracks.clear();
		
		reorderIndexOfPanels(list);

		reorderLocationOfPanels();
		changedPanel.resetActionMode();
	}

	public int getNumberOfPanels(){
		return listOfTracks.size();
	}
	
	public void resetPanelToAdd() {
		this.newTrack = null;
	}


	public int getPositionInPanel(int freeIndex) {
		int usedY = 0;
		
		for(Integer index: listOfTracks.keySet()){
			if(freeIndex <= index){
				if(listOfTracks.get(index) instanceof ITrack){
					ITrack tp = (ITrack)listOfTracks.get(index);
					usedY += tp.getHeight();
				}
			} else {
				break;
			}
			
		}
		
		int pos_y = chromeModel.getUnusableSpace_y();

		return  pos_y + usedY;
	}

	
	private int getTheRightIndexInPanel(int newPositionY, int heightTP, int oldIndex) {
		int pos_y = chromeModel.getUnusableSpace_y();
		int newIndex = -1;
	
		for (Integer index : listOfTracks.keySet()) {
			ITrack panel = (ITrack) listOfTracks.get(index);
			int height = panel.getHeight();
			int panel_y_bottom = pos_y + height;

			int halfHeight = (int) Math.round((double) height / 2.);
			int panel_y_half = panel_y_bottom - halfHeight;

			if (panel_y_half < newPositionY) {
				pos_y = pos_y + height;
				newIndex = index;
			} else {
				newIndex = index;
				break;
			}
		}

		if(newIndex < 0){
			newIndex = oldIndex;
		}
		return (int)newIndex;
	}
	
	private void reorderIndexOfPanels(ArrayList<ITrack> list) {
		for(ITrack panel: list){
			ITrack epanel = (ITrack)panel;
			int actIndex = epanel.getIndexInPane();
			listOfTracks.put(actIndex, epanel);
		}
	}

	public TreeMap<Integer, ITrack> getTracks() {
		return listOfTracks;
	}

	public void dataUpdated() {
		if(scalePlugin!=null && scalePlugin.getTrack() != null)
			scalePlugin.getTrack().setNewLabel(null);
	}

	public void createScalePlugin() {
		if(chromeModel!=null
				&& chromeModel.getOrganiser()!=null
				&& chromeModel.getOrganiser().getChromeManager()!=null
				&& chromeModel.getOrganiser().getChromeManager().containsLoci()){
			scalePlugin = new ScaleTrackPlugin();
			scalePlugin.init(chromeModel, chromeModel.getController());
			chromeModel.createNewTrack(scalePlugin);
			
			// infopanel
			InfoTrackPlugin itp = new InfoTrackPlugin();
			itp.init(chromeModel, chromeModel.getController());
			chromeModel.createNewTrack(itp);
		}
		
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		reorderLocationOfPanels();		
		repaintTracks();
	}
}
