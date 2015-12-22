package mayday.core.gui;

import java.awt.Image;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import mayday.core.pluma.PluginInfo;

@SuppressWarnings("serial")
public abstract class PluginInfoMenuAction extends AbstractAction {
	
	PluginInfo p;
	
	public PluginInfoMenuAction(PluginInfo pli) {
		if (pli==null)
			return;
		putValue(Action.NAME, pli.getMenuName().replace("\0", ""));
		ImageIcon ico = pli.getIcon();
		if (ico!=null) {
			ImageIcon sico = new ImageIcon(ico.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));		
			this.putValue(AbstractAction.SMALL_ICON, sico);
		}
		p = pli;			
		//this.putValue(AbstractAction.SHORT_DESCRIPTION, pli.getAbout().replace("<br>", "\n"));
	}
	
	public PluginInfo getPlugin() {
		return p;
	}
	
}