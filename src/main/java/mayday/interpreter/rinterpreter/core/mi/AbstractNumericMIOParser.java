/*
 * Created on 19.03.2005
 */
package mayday.interpreter.rinterpreter.core.mi;

import mayday.core.meta.NumericMIO;


/**
 * Abstract MIOParser for simple MIOTypes
 * containing numeric payloads which can
 * simply be transformed in R using 
 * <code>as.numeric()</code> and 
 * <code>as.character()</code>
 * 
 * @author Matthias Zschunke
 *
 */
@SuppressWarnings("unchecked")
public abstract class AbstractNumericMIOParser<T extends NumericMIO> extends MITypeParser<T>
{
    /* (non-Javadoc)
     * @see mayday.interpreter.rinterpreter.core.mi.MIOTypeParser#parseR()
     */
    public String parseR()
    {
        return "function(v){as.numeric(v)}";
    }

    /* (non-Javadoc)
     * @see mayday.interpreter.rinterpreter.core.mi.MIOTypeParser#outputR()
     */
    public String outputR()
    {
        return "function(v){as.character(v)}";
    }

}
