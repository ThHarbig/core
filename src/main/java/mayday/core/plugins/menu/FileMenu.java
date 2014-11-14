package mayday.core.plugins.menu;

import java.util.HashMap;

import javax.swing.JMenu;

import mayday.core.io.nativeformat.FileRepository;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.MenuPlugin;

public class FileMenu extends AbstractPlugin implements MenuPlugin {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.FileMenu",
				new String[0],
				Constants.MC_MENUBAR,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Provides the \"File\" menu.",
				"File Menu"
		);
		return pli;
	}

	public void init() {
	}

	public JMenu getMenu() {
		return FileRepository.getMenu();	
	}

	public int getPreferredPosition() {
		return 0; 
	}


}
