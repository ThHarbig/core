package mayday.core.gui;
import javax.swing.JFrame;

import mayday.core.MaydayDefaults;

public class MaydayFrame
extends JFrame
{
	private static final long serialVersionUID = 1L;

	public MaydayFrame(String title) {
		super(title);
	}
	
	public MaydayFrame() {
		super();
	}
	
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
