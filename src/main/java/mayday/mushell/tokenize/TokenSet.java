package mayday.mushell.tokenize;

import java.util.Collections;
import java.util.LinkedList;

import mayday.mushell.tokenize.Token.TokenType;

@SuppressWarnings("serial")
public class TokenSet extends LinkedList<Token>{
	
	boolean finalized = false;
	
	/** add next typed token, filling the gap to the previous token with "TEXT" */
	public void addToken(Token token) {
		fillUpTo(token.getStart()-1);
		add(token);
	}
	
	protected void fillUpTo(int offset) {		
		Token prv = null;
		if (size()>0)
			prv = getLast();
		int lastTokenEnd = 0;
		if (prv!=null)
			lastTokenEnd = prv.getEnd();
		int fillLen = (offset-lastTokenEnd);
		if (fillLen>0) {
			Token fill = new Token(lastTokenEnd+1, fillLen, TokenType.TEXT);
			add(fill);
		}
	}
	
	public boolean add(Token token) {
		finalized = false;	
		return super.add(token);
	}
	
	public void finalize(int documentLength) {
		if (documentLength>0) {
			fillUpTo(documentLength-1);
		}
		finalized = true;
	}
	
	public Token get(int index) {
		if (!finalized)
			throw new RuntimeException("Finalize TokenSet before accessing it!");
		return super.get(index);
	}
	
	public Token getNext(Token token) {
		int pos = getOffsetIndex(token.getStart());
		if (pos<size()-1)
			return get(pos+1);
		return null;
	}
	
	public Token getPrevious(Token token) {
		int pos = getOffsetIndex(token.getStart());
		if (pos>0)
			return get(pos-1);
		return null;
	}
	
	/** return the token describing the text at a given offset, 
	 * i.e. starting before (or at) the offset and ending after (or at) the offset.
	 * @param offset the position to get the token for
	 * @return the token for that position
	 */
	public int getOffsetIndex(int offset) {
		Token dummy = new Token(offset, 1, TokenType.TEXT);
		int position = Collections.binarySearch(this, dummy);
		if (position>0)
			return position;
		else // position points to the next element AFTER offset
			return (-position)-1;
	}
	
	public Token getTokenAt(int offset) {
		return get(getOffsetIndex(offset));
	}
	
	public String toString() {
		String s="TOKENSET\n";
		for (Token t : this) {
			s+=t.toString()+"\n";
		}
		return s;
	}
	
	public String toString(String document) {
		String s="TOKENSET\n";
		for (Token t : this) {
			s+=t.toString()+": \t"+document.substring(t.getStart(),t.getEnd()+1)+"\n";
		}
		return s;
	}

}

