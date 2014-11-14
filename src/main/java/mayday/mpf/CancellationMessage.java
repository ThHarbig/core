package mayday.mpf;

/**
 * CancellationMessage is simply a wrapper for boolean used to define a variable shared between Applicator
 * and the instances of FilterBase run by it.
 * @author Florian Battke
 */
public class CancellationMessage {
	public boolean cancelRequested=false;
}
