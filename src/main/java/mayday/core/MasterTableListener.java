package mayday.core;
import java.util.*;

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
public interface MasterTableListener
extends EventListener
{
  public void masterTableChanged( MasterTableEvent event );  
}
