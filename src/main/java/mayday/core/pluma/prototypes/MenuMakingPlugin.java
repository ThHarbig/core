package mayday.core.pluma.prototypes;

import java.util.Vector;

import javax.swing.JMenuItem;

public interface MenuMakingPlugin {

	public abstract Vector<JMenuItem> createMenu();

	
}
