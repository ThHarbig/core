package mayday.core.plugins.probe;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.prototypes.ProbePlugin;
import mayday.core.pluma.prototypes.SupportPlugin;

public abstract class ProbeWebLookupPlugin extends AbstractPlugin implements
		ProbePlugin {

	protected abstract void processProbes(Collection<Probe> probes, MasterTable masterTable);
	
	protected boolean runWithURL(String url) {
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.core.StartBrowser");
		Boolean success=false;
		if (pli!=null) {
			SupportPlugin StartBrowser = (SupportPlugin)(pli.getInstance());
			success = (Boolean)StartBrowser.run(url);			
		}
		if (!success) {
			JOptionPane.showMessageDialog((Component)null, 
					"Could not start your web browser.",
					"Sorry",
					JOptionPane.ERROR_MESSAGE										
			);
		}
		return success;
	}
	
	public void run(Collection<Probe> probes, MasterTable masterTable) {
		if ( probes.size() == 0 ) 
			return;

		processProbes(probes, masterTable);
	}

}
