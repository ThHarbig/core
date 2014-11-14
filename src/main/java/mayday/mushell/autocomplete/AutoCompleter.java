package mayday.mushell.autocomplete;

import java.util.List;

/**
 * This interface defines methods for syntax completion for the Mayday shell.
 * @author Stephan Symons
 *
 */
public interface AutoCompleter 
{
	/**
	 * This method provides the completions based on the command typed so far. 
	 * Implementing class have to decide how the commands have to be tokenized. 
	 * 
	 * @param command The command typed so far. 
	 * @return all completions matching the prefix. 
	 */
	public List<Completion> allCompletions(String command);
	
	public Completion complete(String command);
		
	
}
