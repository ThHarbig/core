/*
 * Created on 21.11.2004
 */
package mayday.interpreter.rinterpreter.core;

/**
 * @author Matthias
 * @deprecated
 */
public class RProcessState
{
    public String message;
    public int current;
    
    public RProcessState(String msg, int current)
    {
        this.message=msg;
        this.current=current;
    }
    
    public String toString()
    {
        StringBuffer buf=new StringBuffer();
        buf.append(current);
        buf.append(";");
        buf.append(message);
        return buf.toString();
    }

}
