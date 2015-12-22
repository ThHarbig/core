package mayday.mushell.tokenize;

public class Token implements Comparable<Token> {
	
	public enum TokenType {
		TEXT,
		PUNCTUATION,
		COMMAND,
		OPERATOR,
		OBJECT,
		NUMBER,
		STRING,
		WHITESPACE,
		COMMENT,
		TYPE,
		ERROR_TOKEN
	}
	
	int offset, length;
	TokenType type;

	public Token(int Offset, int Length, TokenType Type) {
		offset = Offset;
		length = Length;
		type = Type;
	}
	
	public int getStart() {
		return offset;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getEnd() {
		return offset+length-1;
	}

	public TokenType getType() {
		return type;
	}

	public int compareTo(Token o) {
		return Integer.valueOf(getStart()).compareTo(o.getStart());
	}
	
	public String toString() {
		return getStart()+"-"+getEnd()+" "+getType();
	}
	
}
