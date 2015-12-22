/*
 * Created on 04.06.2005
 */
package mayday.interpreter.rinterpreter.gui;

import mayday.core.gui.abstractdialogs.AbstractStandardDialogComponent;

@SuppressWarnings("serial")
public abstract class AbstractProblemDialogComponent 
extends AbstractStandardDialogComponent
{
    protected ProblemEvent problemEvent;
    
    public AbstractProblemDialogComponent(int direction)
    {
        super(direction);
    }

    public void addProblemListener(ProblemListener l) {
        listenerList.add(ProblemListener.class, l);
    }

    public void removeProblemListener(ProblemListener l) {
        listenerList.remove(ProblemListener.class, l);
    }

    
    protected void fireProblemOccured()
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ProblemListener.class) {
                // Lazily create the event:
                if (problemEvent == null)
                    problemEvent = new ProblemEvent(this);
                ((ProblemListener)listeners[i+1]).problemOccured(problemEvent);
            }
        }
    }
    
    protected void fireProblemSolved()
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ProblemListener.class) {
                // Lazily create the event:
                if (problemEvent == null)
                    problemEvent = new ProblemEvent(this);
                ((ProblemListener)listeners[i+1]).problemSolved(problemEvent);
            }
        }        
    }
}
