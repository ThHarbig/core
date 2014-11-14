/*
 * Created on 22.11.2004
 */
package mayday.interpreter.rinterpreter.core;

import java.util.EventObject;

/**
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class RProcessKilledEvent extends EventObject
{
    public RProcessKilledEvent(Object source)
    {
        super(source);
    }
}
