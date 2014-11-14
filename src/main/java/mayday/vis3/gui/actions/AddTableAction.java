package mayday.vis3.gui.actions;

import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import mayday.core.pluma.PluginInfo;
import mayday.vis3.gui.AbstractTableWindow;
import mayday.vis3.model.Visualizer;
import mayday.vis3.tables.TablePlugin;

@SuppressWarnings("serial")
public class AddTableAction extends AbstractAction {

	private Visualizer visualizer;
	private PluginInfo pluginInfo;
	
	public AddTableAction(PluginInfo pli, Visualizer visualizer) {
		super(pli.getName());
		ImageIcon ico = pli.getIcon();
		if (ico!=null) {
			ImageIcon sico = new ImageIcon(ico.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));		
			this.putValue(AbstractAction.SMALL_ICON, sico);
		}
		this.visualizer = visualizer;
		pluginInfo = pli;
	}
	
	public AddTableAction(PluginInfo pli, Visualizer visualizer, boolean asIcon) {
		this(pli, visualizer);
		ImageIcon ico = pli.getIcon();
		if (ico!=null) {
			ImageIcon sico = new ImageIcon(ico.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));		
			this.putValue(AbstractAction.SMALL_ICON, sico);
			this.putValue(AbstractAction.SHORT_DESCRIPTION, pli.getName());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		AbstractTableWindow newTable = ((TablePlugin)pluginInfo.newInstance()).getTableWindow(visualizer);
		visualizer.addPlot(newTable);
		newTable.setVisible(true);
	}

	

}
