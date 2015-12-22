/**
 * 
 */
package mayday.genetics.basic;

public enum Strand {
	
	PLUS('+',"Forward"),
	MINUS('-',"Backward"),
	BOTH('#',"Both"),
	UNSPECIFIED(' ',"Unspecified");
	
	public final char c;
	public final String name;
	
	Strand(char C, String Name) {
		c=C; name=Name;
	}
	
	public static Strand fromChar(char c) {
		if (c==PLUS.c)
			return PLUS;
		if (c==MINUS.c)
			return MINUS;
		if (c==BOTH.c)
			return BOTH;
		if (c==UNSPECIFIED.c || c=='.')
			return UNSPECIFIED;
		throw new RuntimeException("Invalid strand id: "+c);
	}
	
	public static Strand fromString(String aStrand) {
		if(aStrand == null || aStrand.trim().length()==0) 
			return Strand.UNSPECIFIED;
		else 
			return Strand.fromChar(aStrand.trim().charAt(0));
	}
	
	public static boolean validChar(char c) {
		return (c==PLUS.c || c==MINUS.c || c==BOTH.c || c==UNSPECIFIED.c);
	}
	
	public char toChar() {
		return c;
	}
	
	public String toString() {
		return name;
	}
	
	public boolean similar(Strand other) {
		if (this==UNSPECIFIED || other==UNSPECIFIED || this==BOTH || other==BOTH)
			return true;
		return (this==other);
	}
	
	public Strand reverse() {
		switch (this) {
		case PLUS:
			return MINUS;
		case MINUS:
			return PLUS;
		default: return this;
		}
	}
}