package mayday.vis3.gui.actions;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import mayday.core.pluma.PluginInfo;
import mayday.vis3.PlotPlugin;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class AddPlotAction extends AbstractAction {

	private Visualizer visualizer;
	private PluginInfo pluginInfo;
	
	public AddPlotAction(PluginInfo pli, Visualizer visualizer) {
		super(pli.getName());
		ImageIcon ico = pli.getIcon();
		if (ico!=null) {
			ImageIcon sico = new ImageIcon(ico.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));		
			this.putValue(AbstractAction.SMALL_ICON, sico);
			this.putValue(AbstractAction.SHORT_DESCRIPTION, pli.getName());

		}
		this.visualizer = visualizer;
		pluginInfo = pli;
	}
	
//	public AddPlotAction(PluginInfo pli, Visualizer visualizer, boolean asIcon) {
//		this(pli, visualizer);
//		ImageIcon ico = pli.getIcon();
//		if (ico!=null) {
//			ImageIcon sico = new ImageIcon(ico.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));		
//			this.putValue(AbstractAction.SMALL_ICON, sico);
//			this.putValue(AbstractAction.SHORT_DESCRIPTION, pli.getName());
//		}
//	}
//	
	public void actionPerformed(ActionEvent e) {
		Component c=((PlotPlugin)pluginInfo.newInstance()).getComponent();
		if (c==null)
			return;
		PlotWindow newPlot = new PlotWindow(c, visualizer);
		visualizer.addPlot(newPlot);
		newPlot.setVisible(true);
	}

}
