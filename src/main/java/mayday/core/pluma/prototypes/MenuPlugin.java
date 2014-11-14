package mayday.core.pluma.prototypes;

import javax.swing.JMenu;

public interface MenuPlugin {

	public abstract JMenu getMenu();
	
	public abstract int getPreferredPosition();
	
}
