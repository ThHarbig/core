package mayday.vis3.plots.genomeviz.genomeheatmap.menues.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import mayday.core.gui.MaydayFrame;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller_cb;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.MenuManager;
 
/**
 * Create separate window to choose Chrome View options.
 */
public class ChromeView extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9079840874217053421L;
	protected MenuManager menuManager;
	protected MasterManager master;
	protected Controller c;
	protected Controller_cb cc;
	/**
	 * Frame with Title.
	 * @param text
	 */
	public ChromeView(String text, MenuManager menuManager, MasterManager master, Controller c) {
		super(text);
		this.menuManager = menuManager;
		this.master = master;
		this.c = c;
		this.cc = c.getC_cb();
	}

	/**
	 * Create Frame and set entries to choose Chrome View Options.
	 * @param ActionEvent
	 */
	public void actionPerformed(ActionEvent arg0) {
		//if(chromeViewFrame==null){
			MaydayFrame chromeViewFrame = new MaydayFrame("Select Chromosome View");
			//chromeViewFrame.setLayout(new GridLayout(0,1));
			GridBagLayout gbl = new GridBagLayout();
			chromeViewFrame.setLayout(gbl);
			GridBagConstraints gbc = new GridBagConstraints(); 
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(1,1,1,1);
			gbc.gridx = 0;  // x-Position im gedachten Gitter
			gbc.gridy = 0;  // y-Position im gedachten Gitter
			gbc.gridheight = 1;  // zwei Gitter-Felder hoch
		
			gbc.gridx = 0;  // x-Position im gedachten Gitter
			gbc.gridy = 3;  // y-Position im gedachten Gitter
			gbc.gridheight = 1;  // zwei Gitter-Felder hoch
			gbc.gridwidth = 6;
			JSeparator splitSepartor = new JSeparator();
			gbl.setConstraints(splitSepartor, gbc);
			chromeViewFrame.add(splitSepartor);
			
			gbc.gridx = 0;  // x-Position im gedachten Gitter
			gbc.gridy = 7;  // y-Position im gedachten Gitter
			gbc.gridheight = 1;  // zwei Gitter-Felder hoch
			gbc.gridwidth = 6;
			JSeparator chromeViewSeparator = new JSeparator();
			gbl.setConstraints(chromeViewSeparator, gbc);
			chromeViewFrame.add(chromeViewSeparator);
			
			gbc.gridx = 0;  // x-Position im gedachten Gitter
			gbc.gridy = 8;  // y-Position im gedachten Gitter
			gbc.gridheight = 1;  // zwei Gitter-Felder hoch
			JLabel zoomLabel = new JLabel( "Zoom selection:");
			gbl.setConstraints(zoomLabel, gbc);
			chromeViewFrame.add(zoomLabel);
			
			gbc.gridx = 0;  // x-Position im gedachten Gitter
			gbc.gridy = 9;  // y-Position im gedachten Gitter
			gbc.gridheight = 2;  // 3 Gitter-Felder hoch
			gbc.gridwidth = 6;
			JPanel anotherzoomPanel = menuManager.getZoomViewMenu().anotherZoomPanel();
			gbl.setConstraints(anotherzoomPanel, gbc);
			chromeViewFrame.add(anotherzoomPanel);
			
			gbc.gridx = 0;  // x-Position im gedachten Gitter
			gbc.gridy = 11;  // y-Position im gedachten Gitter
			gbc.gridheight = 1;  // zwei Gitter-Felder hoch
			gbc.gridwidth = 6;
			JSeparator zoomSeparator = new JSeparator();
			gbl.setConstraints(zoomSeparator, gbc);
			chromeViewFrame.add(zoomSeparator);
			
			chromeViewFrame.pack(); // choose size of frame that children of frame fit window
			
			// set the right buttons selected
			menuManager.setZoomLevelButtons(master.getZoomLevel());
			chromeViewFrame.setVisible(true);
	}
}