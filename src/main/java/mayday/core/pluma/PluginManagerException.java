package mayday.core.pluma;

@SuppressWarnings("serial")
public class PluginManagerException extends Exception {
	
	public PluginManagerException(String message) {
		super("PluginManager: "+message);
	}
	
}
