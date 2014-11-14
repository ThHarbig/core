/*
 * Created on Feb 14, 2005
 *
 */
package mayday.core.meta;

import java.util.EventListener;

/**
 * @author gehlenbo
 *
 */
public interface MIManagerListener
extends EventListener
{
  public void miManagerChanged( MIManagerEvent event );  
}
