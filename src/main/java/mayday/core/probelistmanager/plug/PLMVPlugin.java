package mayday.core.probelistmanager.plug;

import mayday.core.pluma.PluginInfo;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.gui.ProbeListManagerView;

public interface PLMVPlugin {
	
	public final static String MC = "ProbeListManagerTreeView";
	
	public PluginInfo getPluginInfo();
	
	public ProbeListManagerView createView(ProbeListManager plm);

}
