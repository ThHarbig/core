package mayday.core.probelistmanager;
import java.util.*;

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
public interface ProbeListManagerListener
extends EventListener
{
  public void probeListManagerChanged( ProbeListManagerEvent event );  
}
