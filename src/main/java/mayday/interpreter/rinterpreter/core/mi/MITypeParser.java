/*
 * Created on 19.03.2005
 */
package mayday.interpreter.rinterpreter.core.mi;

import java.lang.reflect.ParameterizedType;

import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;

/**
 * Each extending class gives instructions
 * for the serialization of the parameter {@linkplain MIType} 
 * into and from a string representation.
 * <br><br>
 * Each MIType is mapped to an MIOTypeParser
 * that manages the serialization.
 * It has 4 tasks, namly (in chronological order)
 * <ul>
 *   <li>create the string representation from the MIType instance (Java)  // done by the MIType itself
 *   <li>parse the string representation to an R object (R)
 *   <li>create the string rep. from the R object (R)
 *   <li>create a MIType instance from the string (Java).  // done by the MIType itself
 * </ul>
 * 
 * For an example you should have a look at 
 * {@linkplain mayday.interpreter.rinterpreter.core.mi.ComplexNumberMIOParser}.
 * <br><br>
 * <b>Note:</b> Each extending class should provide at least the default
 * constructor without any parameters.
 * 
 * @author Matthias Zschunke
 * @see mayday.interpreter.rinterpreter.MIOTypeExchangeManager
 */
public abstract class MITypeParser<T extends MIType> extends AbstractPlugin
{
    /**
     * Give a string representation of the given MIOTye.
     * 
     * @param value
     * @return
     */
    public String asString(T value) {
    	return value.serialize(MIType.SERIAL_TEXT);
    }
    
    /**
     * Parse the string representation of the MIOType object
     * and create this object.
     * 
     * @param s
     * @return
     */
    public T parse(String s) throws IllegalAccessException, InstantiationException {
    	T mio = type().newInstance();
    	mio.deSerialize(MIType.SERIAL_TEXT, s);
    	return mio;
    }
    
    /**
     * A string containing an R function definition for parsing
     * the string representation.
     * 
     * @return
     */
    public abstract String parseR();
    
    /**
     * A string containing an R function for output of such an
     * MIOType.
     * 
     * @return
     */
    public abstract String outputR();
    
    
    /**
     * Returns a string with the name of T.
     */
    public String toString()
    {
        return "MIOTypeParser for " + type().getName() ;
    }
    
    /**
     * The type with which the MIOTypeParser is parameterized.
     * @return
     */
    @SuppressWarnings("unchecked")
	public final Class<T> type()
    {
        return ((Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
