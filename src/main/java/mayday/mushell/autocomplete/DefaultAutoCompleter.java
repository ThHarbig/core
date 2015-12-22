package mayday.mushell.autocomplete;

import java.util.ArrayList;
import java.util.List;

import mayday.core.structures.trie.Trie;
import mayday.core.structures.trie.TrieNode;
import mayday.mushell.tokenize.DefaultTokenizer;
import mayday.mushell.tokenize.Token;
import mayday.mushell.tokenize.Tokenizer;

public class DefaultAutoCompleter implements AutoCompleter
{
	protected Trie trie;
	protected Tokenizer tokenizer;
	
	public DefaultAutoCompleter() {
		this(new DefaultTokenizer());
	}

	public DefaultAutoCompleter(Tokenizer tokenizer) 
	{
		trie=new Trie();		
		this.tokenizer = tokenizer;
	}	
	
	public void addString(String s)
	{
		trie.add(s);
	}
	
	public String tokenize(String command) {
		if (command.length()==0)
			return "";
		Token t = tokenizer.tokenize(command).getLast();
		if (t!=null)
			return command.substring(t.getStart(), t.getEnd()+1);
		return "";
	}
	
	protected TrieNode findCompletionNode(String token) {
		TrieNode node = trie.getNode(token);
		if (node==null)
			node = trie.getNode(token, true);
		return node;
	}
	
	protected String correctToken(TrieNode node, String token) {
		String correctedToken = token;		
		if (node!=null)
			correctedToken = node.getPrefix();
		return correctedToken;
	}

	public List<Completion> allCompletions(String command) 
	{
		String token = tokenize(command);		
		TrieNode node = findCompletionNode(token);		
		String correctedToken = correctToken(node, token);		
		List<String> ret = trie.allSuffixes(node);
		List<Completion> result = new ArrayList<Completion>(ret.size());
		for (String r : ret) 
			result.add(new Completion(r, correctedToken));
		return result;
	}

	public Completion complete(String command) 
	{
		String token = tokenize(command);		
		TrieNode node = findCompletionNode(token);		
		String correctedToken = correctToken(node, token);		
		String ret = trie.longestInfix(node);		
		return new Completion(ret, correctedToken);
	}

}
