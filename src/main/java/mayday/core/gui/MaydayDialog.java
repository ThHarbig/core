package mayday.core.gui;

import java.awt.Window;

import javax.swing.JDialog;

import mayday.core.MaydayDefaults;

/*
 * Created on 07.07.2003.
 *
 * @author neil
 * @version 0.1
 */
/**
 * @author neil
 *
 */

public class MaydayDialog
extends JDialog
{

	public MaydayDialog() {
		super();
	}
	
	public MaydayDialog(Window owner) {
		super(owner);
	}
	
	public MaydayDialog(Window owner, String title) {
		super(owner,title);
	}
	
	private static final long serialVersionUID = 1L;

	public void removeNotify() {
		MaydayWindowManager.removeWindow(this);
		super.removeNotify();
	}

	@SuppressWarnings("deprecation")
	public void setVisible(boolean vis) {
		if (vis)
			MaydayWindowManager.addWindow(this);
		else
			MaydayWindowManager.removeWindow(this);
		if (vis && !isVisible()) 
			MaydayDefaults.centerWindowOnScreen(this);
		super.setVisible(vis);
	}
}
