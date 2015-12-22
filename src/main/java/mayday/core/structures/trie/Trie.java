package mayday.core.structures.trie;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Trie extends TrieNode
{
	public Trie()
	{
		super('\0',false,null);
//		content=new TrieNodeMap();	
	}
	
	public void add(String s)
	{
		if (s.length()==0)
			return;
		
		TrieNodeMap parent=children;
		TrieNode parentNode = null;
		TrieNode node=null;
		for(int i=0; i!=s.length(); ++i)
		{
			node=parent.get(s.charAt(i));
			//if this character is not available in the trie, add it. 
			if(node==null)
			{
				node=new TrieNode(s.charAt(i),false,parentNode);
				parent.put(s.charAt(i), node );
			}
			//else: do nothing.
			parentNode = node;
			parent=node.getChildren();
		}
		if (node!=null) // will never be null, prevents warning only
			node.setWordEnd(true);
	}
	
	public TrieNode getNode(String prefix) {
		return getNode(prefix, false);
	}
	
	public TrieNode getNode(String prefix, boolean ignoreCase) {
		if (prefix.length()==0)
			return this;
		TrieNodeMap parent=children;
		TrieNode node=null;
		for(int i=0; i!=prefix.length(); ++i)
		{
			node=parent.get(prefix.charAt(i), ignoreCase);
			if(node==null) 
				return null;
			//else: do nothing.
			parent=node.getChildren();
		}
		return node;
	}
	
	public boolean contains(String s) {
		return contains(s, false);
	}
	
	public boolean contains(String s, boolean ignoreCase)
	{
		TrieNode node = getNode(s,ignoreCase);
		return (node!=null && node.isWordEnd())	;
	}	
	
	public String longestInfix(String prefix) {
		return longestInfix(prefix, false);
	}
	
	public String longestInfix(String prefix, boolean ignoreCase)
	{
		return longestInfix(getNode(prefix, ignoreCase));
	}
	
	public String longestInfix(TrieNode node) {
		StringBuffer sb=new StringBuffer("");
		while(node!=null && node.size()==1) {
			node=node.getFirstChild();
			sb.append(node.getContent());			
		}
		return sb.toString();
	}
	
	public List<String> allSuffixes(String prefix) {
		return allSuffixes(prefix, false);
	}
	
	public List<String> allSuffixes(String prefix, boolean ignoreCase)
	{
		TrieNode node = getNode(prefix, ignoreCase);
		return (allSuffixes(node));
	}

	public List<String> allSuffixes(TrieNode node) {
		if (node==null)
			return Collections.emptyList();		
		return collectSuffixes(new StringBuffer(), node);
	}
		

	
	private List<String> collectSuffixes(StringBuffer prefix, TrieNode node)
	{
		List<String> result=new LinkedList<String>();
		if(node.isWordEnd())
		{
			result.add(prefix.toString());			
		}		
		
		for (TrieNode n : node)
		{
			StringBuffer currentPrefix=new StringBuffer(prefix);
			currentPrefix.append(n.getContent());			
			result.addAll(collectSuffixes(currentPrefix, n));
		}
		return result;
	}
	
	@Override
	public String getPrefix() {
		return "";	
	}

}
