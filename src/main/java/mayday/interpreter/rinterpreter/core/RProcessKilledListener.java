/*
 * Created on 21.11.2004
 */
package mayday.interpreter.rinterpreter.core;

import java.util.EventListener;

/**
 * @author Matthias
 *
 */
public interface RProcessKilledListener 
extends EventListener
{  
  public void processKilled(RProcessKilledEvent event);
}
