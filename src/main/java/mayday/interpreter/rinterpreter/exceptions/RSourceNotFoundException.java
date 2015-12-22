package mayday.interpreter.rinterpreter.exceptions;

/**
 * Catch these exceptions when running R
 * in internal mode.
 * 
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class RSourceNotFoundException extends RInternalException
{
	public RSourceNotFoundException(String msg)
	{
		super(msg);
	}
}
