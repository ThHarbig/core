package mayday.mushell.tokenize;


public abstract class AbstractTokenizer implements Tokenizer {

	public TokenSet tokenize(String text) {
		TokenSet ts = new TokenSet();
		int i=0;
		while(i<text.length()) {
			Token t = parseNextToken(text, i);
			if (t==null)
				break;
			i = t.getEnd()+1;
			ts.addToken(t);
		}		
		ts.finalize(text.length());
//		System.out.println(ts.toString(text));
		return ts;
	}
	
	public abstract Token parseNextToken(String text, int offset);

}
