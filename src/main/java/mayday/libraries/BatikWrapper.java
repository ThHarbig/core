package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class BatikWrapper extends AbstractPlugin {
   public Object execute(Object var1) {
      return null;
   }

   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.Batik", new String[0], "Libraries", (HashMap)null, "Apache Foundation", "http://www.apache.org", "A subset of the Apache Batik Library providing XML, PDF and SVG capabilities.", "Batik Library");
   }
}
