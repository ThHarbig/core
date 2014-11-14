package mayday.core.plugins.menu;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.JMenu;

import mayday.core.gui.PluginMenu;
import mayday.core.gui.WindowListWindow;
import mayday.core.pluginrunner.GenericPluginRunner;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuPlugin;

public class WindowMenu extends AbstractPlugin implements MenuPlugin { //implements ActionListener {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.WindowMenu",
				new String[0],
				Constants.MC_MENUBAR,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Builds and maintains the \"Window\" menu.",
				"Window Menu"
				);
		return pli;
	}
	
	public void init() {
	}

	
	protected PluginMenu<?> theMenu; 
	
	@SuppressWarnings({ "unchecked", "serial" })
	public JMenu getMenu() {	
		
		theMenu = new PluginMenu("Windows", Constants.MC_PLUGGABLEVIEWS) {

			@Override
			public void callPlugin(PluginInfo pli, List selection) {
				runGenericPlugin(pli);
			}
			
		};
	    theMenu.setMnemonic( KeyEvent.VK_W );
	    
	    
	    theMenu.add(WindowListWindow.getAction(), true);
	    theMenu.addSeparator();

	    theMenu.fill();
	    
		return theMenu;
	}

	public int getPreferredPosition() {
		return 4; 
	}
	
	public static void runGenericPlugin(final PluginInfo pli)  {
		GenericPluginRunner gpl = new GenericPluginRunner(pli);
		gpl.execute();
	}
	
}
