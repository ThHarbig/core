package mayday.core.plugins;

import java.util.HashMap;
import java.util.Properties;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;

public class ApplyProxySettings extends AbstractPlugin implements CorePlugin {

	
	private PluginInfo pli;
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.ApplyProxySettings",
				new String[]{},
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Applies the proxy settings from Mayday's preferences",
				"Apply Proxy Settings"
		);
		return pli;	
	}

	public void run() {
        boolean useP = MaydayDefaults.Prefs.ProxyActive.getBooleanValue();
        if (useP) {
            String host = MaydayDefaults.Prefs.ProxyHost.getStringValue();
            String port = ""+MaydayDefaults.Prefs.ProxyPort.getIntValue();
            if (host.length()>0 && port.length()>0) {
            	Properties systemSettings = System.getProperties();
            	systemSettings.put("http.proxyHost", host);
            	systemSettings.put("http.proxyPort", port);
            	System.setProperties(systemSettings);
            	System.out.println("Proxy set to: "+host+":"+port);
            }
        }
		
	}

}
