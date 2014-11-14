package mayday.core.plugins;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.SupportPlugin;

public class StartBrowser extends AbstractPlugin implements SupportPlugin {

	
	private PluginInfo pli;
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.StartBrowser",
				new String[]{},
				Constants.MC_SUPPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Opens a web browser at the desired location.",
				"Start Browser"
		);
		return pli;	
	}

	public Object run(Object URL) {
        String os = System.getProperty("os.name");
        if (os==null) return "";

        String command;
        String parameters = (String)URL;
        
        try {
            Process ps;
            if (os.toLowerCase().contains("windows")) {
            	String cmdpath = System.getenv("windir");
            	command = cmdpath+"\\System32\\cmd.exe";
            	parameters = "/C start "+parameters;
            } else if (os.toLowerCase().contains("mac")){
            	command="/usr/bin/open";
            } else {
            	command = "/usr/bin/xdg-open";
            }
            System.out.println("Trying to start browser via: "+command+"\nwith parameters: "+parameters);
        	ps = Runtime.getRuntime().exec(new String[]{command,parameters});
        	ps.waitFor();
        	if (ps.exitValue()!=0) {
        		System.err.println("Could not start browser for OS: "+os+". Exit code:\n"+ps.exitValue());
        		return Boolean.FALSE;
        	}
        } catch (Exception E) {
        	System.err.println("Could not start browser for OS: "+os+". Exception message:\n"+E.getMessage());
        	return Boolean.FALSE;
        }
        return Boolean.TRUE;
	}

}
