package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class JUNITWrapper extends AbstractPlugin {
   public Object execute(Object var1) {
      return null;
   }

   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.JUNIT", new String[0], "Libraries", (HashMap)null, "SUN Microsystems", "http://java.sun.com", "Java JUnit library", "JUnit");
   }
}
