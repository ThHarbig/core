package mayday.vis3.plots.genomeviz.genomeoverview.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.vis3.components.CenteredMiddleLayout;
import mayday.vis3.plots.genomeviz.EnumManagerGO.ActionModes;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons.Delete_Button;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons.MoveDown_Button;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons.MoveToBottom_Button;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons.MoveToTop_Button;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons.MoveUp_Button;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;

@SuppressWarnings("serial")
public class UserPanel extends JPanel{


	protected MoveUp_Button move_up = null;
	protected MoveDown_Button move_down = null;
	protected MoveToTop_Button move_to_top = null;
	protected MoveToBottom_Button move_to_bottom = null;
	protected Delete_Button delete_button = null;
	protected ITrack track = null;
	protected GenomeOverviewModel model;
	protected JLabel label = null;
	
	protected Controller c;
	
	
	public UserPanel(GenomeOverviewModel ChromeModel,ITrack trackPanel){
		super(new CenteredMiddleLayout());
		
		track = trackPanel;
		model = ChromeModel;
		c = model.getController();
		if (!Track.isScaleTrack(track))
			setLayout(null);
		
		setSize(model.getWidth_userpanel(), track.getHeight()-2);
		setPreferredSize(new Dimension(model.getWidth_userpanel(), track.getHeight()-2));

		setOpaque(false);
		
		addListeners();

		init();	
	}
	  
	private void addListeners() {
		addMouseMotionListener(c.getC_up_spp());
		addMouseListener(c.getC_up_spp());			
	}

	
	    
	protected int posy = 2;
	
	private void init() {

		if (! (Track.isScaleTrack(track))) {
			
			delete_button = new Delete_Button();
			delete_button.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					actionPanelChanged(ActionModes.DELETE);
				}

			});
			add(delete_button);
			delete_button.setLocation(1,posy);
			
			move_up = new MoveUp_Button();
			move_up.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					actionPanelChanged(ActionModes.MOVE_UP);
				}
			});

			add(move_up);
			move_up.setLocation(delete_button.getLocation().x + delete_button.getWidth()+2,posy);
			
			move_down = new MoveDown_Button();
			move_down.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					actionPanelChanged(ActionModes.MOVE_DOWN);
				}
			});

			add(move_down);
			move_down.setLocation(move_up.getLocation().x + move_up.getWidth()+2,posy);
			
			move_to_top = new MoveToTop_Button();
			move_to_top.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					actionPanelChanged(ActionModes.MOVE_TO_TOP);
				}

			});

			add(move_to_top);
			move_to_top.setLocation(move_down.getLocation().x +  move_down.getWidth()+2,posy);
			
			move_to_bottom = new MoveToBottom_Button();
			move_to_bottom.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					actionPanelChanged(ActionModes.MOVE_TO_BOTTOM);
				}
			});

			add(move_to_bottom);
			move_to_bottom.setLocation(move_to_top.getLocation().x + move_to_top.getWidth()+2,posy);
			
			setLabelForPanel();
			
			label.setPreferredSize(new Dimension(model.getWidth_userpanel()-2, model.getHeight_userpanel()/2));
			label.setSize(new Dimension(model.getWidth_userpanel()-2, model.getHeight_userpanel()/2));
			add(label);
			label.setLocation(1, delete_button.getLocation().y + label.getHeight()-4);
		} else 	if (Track.isScaleTrack(track)) {
			label = new JLabel("");
			add(label,"Middle");
			
			setLabelForPanel();
		}
	}
	
	protected void setLabelForPanel() {
		
		if (! (Track.isScaleTrack(track))){
			String text = "";
			if (track.getTrackPlugin()!= null && 
					track.getTrackPlugin().getTrackSettings() != null
					&& track.getTrackPlugin().getTrackSettings().getColorProvider() != null) {
				text = track.getTrackPlugin().getTrackSettings().getTrackLabel();
			}
			label = new JLabel(text);
		} else if  (Track.isScaleTrack(track)){
			label.setText("<html>"+model.getActualSpecies().getName() + "<br>chromosome: " + model.getActualChrome().getId());
		}
	
	}
	
	public void setLabelForPanel(String text){
		if (! (Track.isScaleTrack(track))){
			if(label!=null){
				if(track.getTrackPlugin().getTrackSettings().getColorProvider()!=null){
					label.setText(track.getTrackPlugin().getTrackSettings().getTrackLabel());
				}
				label.setText(text);
				label.setToolTipText(text);
			}
		} else if  (Track.isScaleTrack(track)){
			if(label!=null){
				label.setText("<html>"+model.getActualSpecies().getName() + "<br>chromosome: " + model.getActualChrome().getId());
			}
		}
	}

	private void actionPanelChanged(ActionModes mode) {
		if(track != null){
			track.setActionMode(mode);
		}
	}

	public void setNewSize() {
		setSize(model.getWidth_userpanel(), track.getHeight()-2);
		setPreferredSize(getSize());	
	}
	
	public void paint(Graphics g) {
		boolean v = model.getPanelPositioner().snapTracks();
		if (move_down!=null)
			move_down.setVisible(v);
		if (move_up!=null)
			move_up.setVisible(v);
		if (move_to_bottom!=null)
			move_to_bottom.setVisible(v);
		if (move_to_top!=null)
			move_to_top.setVisible(v);
		super.paint(g);
	}
}
