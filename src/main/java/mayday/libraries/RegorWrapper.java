package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class RegorWrapper extends AbstractPlugin {
   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.Regor", new String[0], "Libraries", (HashMap)null, "Taschek Joerg", "http://sourceforge.net/projects/java-registry/", "Windows Registry reader/writer in Java.", "Registry Library");
   }
}
