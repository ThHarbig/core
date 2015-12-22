package mayday.core.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class WindowListWindow extends MaydayFrame implements ActionListener {
	
	protected WindowListPanel windowListPanel;
	
	protected static WindowListWindow instance;
	
	public static void makeVisible(boolean v) {
		if (v) {
			if (instance==null)
				instance = new WindowListWindow();
			instance.setVisible(true);
			instance.toFront();
			instance.requestFocus();
		} else {
			if (instance!=null) {
				instance.dispose();
			}
		}
	}
	
	private WindowListWindow() {
		super("Mayday Windows");
		add(windowListPanel = new WindowListPanel());
				
		MaydayWindowManager.addListener(this);
		
		setMinimumSize(new Dimension(200,100));
		
		pack();
		setSize(new Dimension(300,300));

	}
	
	public void dispose() {
		MaydayWindowManager.removeListener(this);
		super.dispose();
		windowListPanel.dispose();
	}

	public void actionPerformed(ActionEvent e) {
		toFront();
	}
	
	public void setVisible(boolean v) {
		if (!v) {
			dispose();
			instance = null;
		}
		else
			super.setVisible(v);
	}
	
	public static AbstractAction getAction() {
		return new AbstractAction("Show Window Manager") {

			public void actionPerformed(ActionEvent e) {
				WindowListWindow.makeVisible(true);
			}
			
		};
	}

	public static JMenu getMenuElement() {
		JMenu myMenu = new JMenu("Windows");
		JMenuItem jm = new JMenuItem("Double click items or folders to bring windows to front.");
		jm.setEnabled(false);
		myMenu.add(jm);		
		myMenu.add(new WindowListPanel());
		myMenu.addSeparator();
		myMenu.add(getAction());
		myMenu.setMnemonic( KeyEvent.VK_W );
				
		return myMenu;
	}
}
