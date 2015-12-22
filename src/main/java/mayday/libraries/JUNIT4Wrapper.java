package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class JUNIT4Wrapper extends AbstractPlugin {
   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.JUNIT4", new String[0], "Libraries", (HashMap)null, "JUnit4 Developers", "http://www.junit.org/", "Java JUnit4 library", "JUnit4");
   }
}
