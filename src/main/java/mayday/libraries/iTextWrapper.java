package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class iTextWrapper extends AbstractPlugin {
   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.iText", new String[0], "Libraries", (HashMap)null, "iText Software Corp. (California, USA) and 1T3XT BVBA (Ghent, Belgium)", "http://www.itextpdf.com", "The iText library allows to produce PDF files via Java graphics operations.", "iText PDF library 5.0.6");
   }
}
