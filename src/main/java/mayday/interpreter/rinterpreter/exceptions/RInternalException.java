package mayday.interpreter.rinterpreter.exceptions;

/**
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class RInternalException extends RuntimeException
{
	public RInternalException(String msg)
	{
		super(msg);
	}
}
