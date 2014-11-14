package mayday.core.pluma;
import java.util.*;

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
public interface PluginManagerListener extends EventListener {
	
	public void pluginAdded( PluginInfo pli );
	
}
