package mayday.vis3.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.export.ExportDialog;

@SuppressWarnings("serial")
public class ExportVisibleAreaAction extends AbstractAction {

	private Component plotWindow;
	
	public ExportVisibleAreaAction(Component plotComponent) {
		super("Export visible area only...");
		this.plotWindow=plotComponent;
	}
	
	public void actionPerformed(ActionEvent e) {
		new ExportDialog(plotWindow, true);
	}

}
