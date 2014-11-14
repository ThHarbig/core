package mayday.core;
import java.util.*;

/**
 * @author Nils Gehlenborg
 * @version 0.1
 */
public interface DataSetListener
extends EventListener
{
  public void dataSetChanged( DataSetEvent event );  
}
