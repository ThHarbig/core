package mayday.core;
import java.util.*;

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
public interface ProbeListListener
extends EventListener
{
	public void probeListChanged( ProbeListEvent event );  
}
