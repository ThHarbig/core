package mayday.core.plugins.help;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.core.pluma.prototypes.SupportPlugin;

public class ReportBug extends AbstractPlugin implements GenericPlugin {

	
	private PluginInfo pli;
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.ReportBug",
				new String[]{"PAS.core.StartBrowser"},
				Constants.MC_HELP,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Opens a web browser at the Mayday bugtracker.",
				"Report Bug"
		);
		pli.setMenuName("Report a bug...");
		pli.setIcon(MaydayDefaults.PROGRAM_ICON_IMAGE);
		return pli;	
	}

	public void run() {
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.core.StartBrowser");
		Boolean success=false;
		if (pli!=null) {
			SupportPlugin StartBrowser = (SupportPlugin)(pli.getInstance());
			success = (Boolean)StartBrowser.run("http://www-ps.informatik.uni-tuebingen.de/mantis/Mayday-Welcome.html");			
		}
		if (!success) {
			JOptionPane.showMessageDialog((Component)null, 
					"Could not start your web browser.\n" +
					"Please visit our bugtracker at\n" +
					"http://www-ps.informatik.uni-tuebingen.de/mantis",
					"Browser could not be started",
					JOptionPane.ERROR_MESSAGE,				
					this.pli.getIcon()
					);
		}
					
	}

}
