package mayday.vis3.plots.genomeviz.genomeoverview.menues;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import mayday.core.Preferences;
import mayday.core.gui.PluginMenu;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingsDialog;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller_tc;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.UserGestures;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;

@SuppressWarnings("serial")
public class TrackPopupMenu extends JPopupMenu{
	/**
	 * 
	 */
	protected Controller c;
	protected GenomeOverviewModel chromeModel;
	protected MenuModel menuModel;
	protected ITrack tp = null;
	
	public TrackPopupMenu(GenomeOverviewModel chromeModel, Controller c, MenuModel MenuModel){
		this.c = c;
		this.chromeModel = chromeModel;
		menuModel = MenuModel;
	}
	
	protected void setTrack(ITrack tp) {
		this.tp = tp;
	}
	
	protected void init() {

		// first show track specific stuff
		if (tp!=null) {
			Controller_tc c_tp = c.getController_tp();

			this.add(new PluginMenu.TitleMenuComponentAction("-- Track: "+tp.getTrackPlugin().getPluginInfo().getName()+" --"));
			
			JMenuItem properties = new JMenuItem("Properties");
			properties.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					AbstractTrackSettings ts = tp.getTrackPlugin().getTrackSettings();
					if (ts != null) {
						SettingsDialog sd = ts.getDialog();
						if (sd != null) {
							sd.setVisible(true);
						}
					}
				}		
			});
			this.add(properties);
			
//			JMenu showMenu = new JMenu("selected probes...");
//			
//			JMenuItem showProperties = new JMenuItem("show properties");
//			showProperties.addActionListener(c_tp);
//			showProperties.setActionCommand(UserGestures.SHOW__PROBES_PROPERTIES);
//			showMenu.add(showProperties);
//
//			JMenuItem showDetailed = new JMenuItem("show separate view");
//			showDetailed.addActionListener(c_tp);
//			showDetailed.setActionCommand(UserGestures.SHOW__PROBES_DETAILED);
//			showMenu.add(showDetailed);
//
//			this.add(showMenu);
			
			//movement
			JMenu submenu = new JMenu("move...");
			JMenuItem moveUp_item = new JMenuItem("move up");
			moveUp_item.addActionListener(c_tp);
			moveUp_item.setActionCommand(UserGestures.MOVE_UP);
			submenu.add(moveUp_item);

			JMenuItem moveDown_item = new JMenuItem("move down");
			moveDown_item.addActionListener(c_tp);
			moveDown_item.setActionCommand(UserGestures.MOVE_DOWN);
			submenu.add(moveDown_item);

			JMenuItem moveToTop_item = new JMenuItem("move to top");
			moveToTop_item.addActionListener(c_tp);
			moveToTop_item.setActionCommand(UserGestures.MOVE_TO_TOP);
			submenu.add(moveToTop_item);

			JMenuItem moveToBottom_item = new JMenuItem("move to bottom");
			moveToBottom_item.addActionListener(c_tp);
			moveToBottom_item.setActionCommand(UserGestures.MOVE_TO_BOTTOM);
			submenu.add(moveToBottom_item);
			this.add(submenu);
			
			JMenuItem cloneTrack = new JMenuItem(new AbstractAction("clone track") {
				@Override
				public void actionPerformed(ActionEvent e) {
					PluginInfo pli = tp.getTrackPlugin().getPluginInfo();
					AbstractTrackPlugin trackPlugin = (AbstractTrackPlugin)pli.newInstance();
					trackPlugin.init(chromeModel, c);
					Preferences originalSettings = tp.getTrackPlugin().getTrackSettings().getRoot().toPrefNode();
					trackPlugin.getTrackSettings().getRoot().fromPrefNode(originalSettings);
					ITrack it = chromeModel.createNewTrack(trackPlugin);
					it.resizeTrackheight(tp.getHeight());
					if (!chromeModel.getPanelPositioner().snapTracks())
						it.setLocationInPanel(tp.getPositionInPane()+tp.getHeight());
					trackPlugin.actualizeTrack();
					trackPlugin.getTrackSettings().getDialog().setVisible(true);
				}
			});
			add(cloneTrack);

			JMenuItem deleteItem = new JMenuItem("delete track");
			deleteItem.addActionListener(c_tp);
			deleteItem.setActionCommand(UserGestures.DELETE_TRACK);
			this.add(deleteItem);

			this.addSeparator();
		}
		
//		JMenu rangeMenu = new JMenu("select range for details...");
//		rangeMenu.add(new ChooseRange("by position", chromeModel, c, UserGestures.SELECT_RANGE_FOR_DETAILS));
//		rangeMenu.add(new AbsActRangeView("by visible range",chromeModel,c));
//		this.add(rangeMenu);	

		JMenu subMenu = new JMenu("add track...");
		JMenu subMenuAllExp = new JMenu("add track for each experiment");

		int c1=0, c2=0;
		
		for(String MC : new String[]{AbstractTrackPlugin.MC1,AbstractTrackPlugin.MC2,AbstractTrackPlugin.MC3}) {
			if (c1>0)
				subMenu.addSeparator();
			if (c2>0)
				subMenuAllExp.addSeparator();
			c1=0;
			c2=0;
			for(PluginInfo pli: PluginManager.getInstance().getPluginsFor(MC)){
				AbstractPlugin ap = pli.newInstance();
				if(ap instanceof AbstractTrackPlugin){
					AbstractTrackPlugin tp = (AbstractTrackPlugin)ap;
					tp.init(chromeModel, c);
					AbstractAction ata = tp.getAddTrackAction();
					if(ata!=null) {
						subMenu.add(ata);
						++c1;
					}
					ata = tp.getAddTracksForAllExperimentsAction();
					if(ata!=null) {
						subMenuAllExp.add(ata);
						++c2;
					}
				}
			}
		}
		if (subMenu.getMenuComponent(subMenu.getMenuComponentCount()-1) instanceof JSeparator)
			subMenu.remove(subMenu.getMenuComponentCount()-1);
		if (subMenuAllExp.getMenuComponent(subMenuAllExp.getMenuComponentCount()-1) instanceof JSeparator)
			subMenuAllExp.remove(subMenuAllExp.getMenuComponentCount()-1);
		
		
		this.add(subMenu);
		this.add(subMenuAllExp);
		
		JMenu viewMenu = new JMenu("view...");
		viewMenu.add(new FitViewAction("fit view",chromeModel,c));
		viewMenu.add(new ChooseRange("by position", chromeModel, c, UserGestures.SELECT_RANGE_TO_VIEW));
//		if (chromeModel.getChromosomeSettings()!=null)
//			chromeModel.getChromosomeSettings().addToMenu(viewMenu, null);
		add(viewMenu);
		

	}

}
