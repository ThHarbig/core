/*
 * Created on 04.06.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ProblemEvent
extends ActionEvent
{
    public ProblemEvent(Object source)
    {
        super(source,0,null);
    }
}
