package mayday.core;
import java.util.*;

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
public interface ProbeListViewerListener
extends EventListener
{
  public void probeListViewerChanged( ProbeListViewerEvent event );  
}
