/*
 * Created on 04.06.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.util.EventListener;


public interface ProblemListener
extends EventListener
{
    public void problemOccured(ProblemEvent e);
    
    public void problemSolved(ProblemEvent e);
    
}
