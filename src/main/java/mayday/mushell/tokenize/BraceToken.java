package mayday.mushell.tokenize;


public class BraceToken extends Token {

	protected Token partner;
	protected char myChar;
	
	protected final static String bracesOpen = "({[";
	protected final static String bracesClose = ")}]";
	
	public BraceToken(int Offset, int Length, String Char) {
		super(Offset, Length, TokenType.PUNCTUATION);
		myChar = Char.charAt(0);
	}
	
	public TokenType getType() {
		if (partner==null)
			return TokenType.ERROR_TOKEN;
		return super.getType();
	}

	public void setPartner(BraceToken Partner) {
		Partner.partner = this;
		partner = Partner;
	}
	
	public boolean fits(BraceToken Partner) {
		return fits0(Partner) || Partner.fits0(this);
	}
	
	protected boolean fits0(BraceToken Partner) {
		char pChar = Partner.myChar;
		int myi = bracesOpen.indexOf(myChar);
		if (myi>-1)
			return bracesClose.indexOf(pChar)==myi;
		return false;
	}
	
	public static boolean isBrace(String c) {
		return bracesOpen.contains(c) || bracesClose.contains(c);
	}
	
	public static boolean isOpeningBrace(String c) {
		return bracesOpen.contains(c);
	}
	
	
}
