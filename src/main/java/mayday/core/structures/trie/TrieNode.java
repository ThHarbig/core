package mayday.core.structures.trie;

import java.util.Iterator;

public class TrieNode implements Iterable<TrieNode>
{
	protected TrieNodeMap children;
	private boolean wordEnd;
	protected char content;
	private TrieNode parent;
	
	public TrieNode(char content, boolean wordEnd, TrieNode parent) 
	{
		this.content=content;
		this.wordEnd=wordEnd;
		this.parent=parent;
		children=new TrieNodeMap();
	}
	
	public TrieNode getFirstChild()
	{
		if (size()>=1)
			return children.values().iterator().next();
		return null;
	}
	
	public TrieNode getChild(char c, boolean ignoreCase) {
		if (ignoreCase) {
			
		}
		return children.get(c);
	}
	
	public TrieNode getParent() {
		return parent;
	}
	
	public String getPrefix() {
		StringBuffer sb = new StringBuffer();
		getPrefix(sb);
		return sb.reverse().toString();
	}
	
	protected void getPrefix(StringBuffer sb) {
		sb.append(getContent());
		if (parent!=null)
			parent.getPrefix(sb);
	}
	
	public int size() {
		return children.size();
	}
	
	public String toString() {
		return ""+content;
	}

	public Iterator<TrieNode> iterator() {
		return children.values().iterator();
	}

	public boolean isWordEnd() {
		return wordEnd;
	}

	public void setWordEnd(boolean wordEnd) {
		this.wordEnd = wordEnd;
	}

	public char getContent() {
		return content;
	}

	public void setContent(char content) {
		this.content = content;
	}

	public TrieNodeMap getChildren() {
		return children;
	}
	
	
}