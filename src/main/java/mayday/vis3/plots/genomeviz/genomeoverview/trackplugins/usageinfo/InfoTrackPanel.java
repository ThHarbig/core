package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.usageinfo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JEditorPane;

import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.PaintingPanel;

@SuppressWarnings("serial")
public class InfoTrackPanel extends PaintingPanel {

	protected JEditorPane information;
	
	public InfoTrackPanel(GenomeOverviewModel chromeModel,AbstractTrackPlugin tp, JEditorPane info) {
		super(chromeModel, tp);
		setBackground(Color.white);
		information = info;
		setForeground(Color.WHITE);
		setBackground(Color.WHITE);
		setOpaque(false);
		add(information);
	}
	
	public void setRange(){}
	
	protected int getDefaultWidth() {
		return model.getWidth_scala_pp();
	}
	
	public void paint(Graphics g) {
		// move the textpane to the current viewport
		Rectangle rect = model.getVisibleRectOfLayeredPane();
//		r.x += model.getWidth_userpanel();
		rect.y=0;
		((Graphics2D)g).setClip(rect.intersection(((Graphics2D)g).getClipBounds()));
		information.setBounds(rect.x, 0, rect.width, getHeight());
		g.clearRect(0, 0, getWidth(), getHeight());
		super.paint(g);
	}



	

}
