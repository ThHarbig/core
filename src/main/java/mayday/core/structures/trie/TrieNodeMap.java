package mayday.core.structures.trie;

import java.util.TreeMap;

@SuppressWarnings("serial")
public class TrieNodeMap extends TreeMap<Character, TrieNode> {
	
	public TrieNode get(char c, boolean ignoreCase) {
		TrieNode res = super.get(c);
		if (res==null && ignoreCase) {
			char cother = Character.isLowerCase(c)?Character.toUpperCase(c):Character.toLowerCase(c);
			res = super.get(cother);
		}
		return res;
	}

}
