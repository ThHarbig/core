package mayday.mushell.autocomplete;

public class Completion
{
	String prefixReplacement;
	String completion;
	
	public Completion(String completion, String PrefixReplacement) 
	{
		this.completion=completion;
		prefixReplacement = PrefixReplacement;
	}
	
	/**
	 * @return The length of the token prefix to be replaced
	 */
	public int getOldTokenLength() {
		return prefixReplacement.length();
	}
	
	/**
	 * @return The new token prefix to use as replacement
	 */
	public String prefixReplacement() {
		return prefixReplacement;
	}
	
	/**
	 * @return The new token suffix to append to the prefix. The final replacement operation is:
	 * remove oldTokenLength() characters left of the cursor and then add prefixReplacement+Completion
	 */
	public String getCompletion() 
	{
		return completion;
	}
	
	public String toString() {
		return getCompletion();
	}
	
	public boolean isEmpty()
	{
		return completion.isEmpty();
	}
	
	public boolean startsWith(String prefix)
	{
		return completion.startsWith(prefix);
	}
	
	public void pop()
	{
		prefixReplacement+=completion.charAt(0);
		completion=completion.substring(1);
	}
}