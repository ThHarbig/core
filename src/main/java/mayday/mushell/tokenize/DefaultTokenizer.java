package mayday.mushell.tokenize;

import java.util.HashMap;
import java.util.Stack;

import mayday.mushell.tokenize.Token.TokenType;

public class DefaultTokenizer extends AbstractTokenizer {
	
	protected Stack<BraceToken> openBraces = new Stack<BraceToken>(); 
	
	protected String punctuation = "[\\,\\;]";	
	protected String stringDelim = "[\\\"\\'`]";    			// resolves to [\"\'`]
	protected String operators 	 = "[\\+\\-\\*\\/\\^\\$@&\\|\\:\\=\\!<>]";		// resolves to [\+\-\*\/\^\$@&\|\:\=\!]
	protected String digits  = "[\\d]";
	protected String hexDigits  = "[\\p{XDigit}]";
	protected String whitespace	 = "[\\s]";
	protected char escapeChar = '\\';
	protected char startOfCommand = '(';
	protected char startOfLineComment = '#';
	protected HashMap<String, TokenType> knownTokens = new HashMap<String, TokenType>();
	
	public TokenSet tokenize(String text) {
		openBraces.clear();
		return super.tokenize(text);
	}

	public Token parseNextToken(String text, int offset) {
		
		offset = skipWhitespace(text, offset);
		if (offset==text.length())
			return null;

		String oneChar = text.substring(offset,offset+1);

		if (oneChar.matches(punctuation))
			return collectPunctuation(text, offset);
		
		if (BraceToken.isBrace(oneChar)) {
			BraceToken braceT = new BraceToken(offset, 1, oneChar);
			if (BraceToken.isOpeningBrace(oneChar)) {
				openBraces.push(braceT);
			} else {
				if (openBraces.size()>0 && openBraces.peek().fits(braceT))
					braceT.setPartner(openBraces.pop());
			}
			return braceT;
		}
			
		
		// number starts here
		if (oneChar.matches(digits)) 
			return collectNumber(text, offset);
		
		if (oneChar.matches(stringDelim)) 
			return collectString(text, offset);
		
		if (oneChar.matches(operators))
			return new Token(offset, 1, TokenType.OPERATOR);
		
		if (text.charAt(offset)==startOfLineComment) {
			return collectLineComment(text, offset);
		}
		
		// now we collect non-whitespace together, i.e. everything but whitespace,punctuation,operators,stringDelim
		// numbers are allowed now also
		int endOffset = collectIdentifier(text, offset);		
		
		TokenType type = TokenType.OBJECT;
		// find out if that was an object name or a function name
		if (endOffset < text.length()-1 && text.charAt(endOffset+1)==startOfCommand)
			type = TokenType.COMMAND;		
		
		// map multi-character strings to token types
		if (type==TokenType.OBJECT)
			type = getTokenType(text.substring(offset, endOffset+1), type);
		
		return new Token(offset, endOffset-offset+1, type);
	}
	
	protected TokenType getTokenType(String s, TokenType defaultType) {
		TokenType t = knownTokens.get(s);
		if (t==null)
			t = defaultType;
		return t;
	}
	
	protected void declareTokens(TokenType type, String... keywords) {
		for (String s : keywords)
			knownTokens.put(s,type);
	}
	
	protected int collectIdentifier(String text, int offset) {
		String current;
		int newOffset = offset;
		while (newOffset < text.length() &&
			  !(current = text.substring(newOffset, newOffset+1)).matches(punctuation) &&
			  !current.matches(whitespace) &&
			  !current.matches(operators) &&
			  !current.matches(stringDelim) &&
			  !(current.charAt(0)==startOfCommand) &&
			  !BraceToken.isBrace(current)
		) {
			++ newOffset;
		}	
		return newOffset-1;
	}
	
	protected Token collectLineComment(String text, int offset) {
		int newOffset = offset+1;
		while (newOffset < text.length() && text.charAt(newOffset)!='\n')
			++newOffset;
		return new Token(offset, newOffset-offset, TokenType.COMMENT);
	}
	
	protected int skipWhitespace(String text, int offset) {
		while (offset < text.length() && text.substring(offset, offset+1).matches(whitespace))
			++offset;
		return offset;
	}

	protected Token collectNumber(String text, int offset) {
		// first char already tested. 
		if (offset < text.length() - 1) {
			// check for hex or not hex number
			if (text.charAt(offset+1)=='x') 
				return collectHexNumber(text, offset);
			else 
				return collect10Number(text, offset);			
		}
		return new Token(offset,1,TokenType.NUMBER);
	}
	
	protected Token collectHexNumber(String text, int offset) {
		// first 2 chars already ok
		int newOffset = offset+2;
		while (newOffset < text.length() && text.substring(newOffset, newOffset+1).matches(hexDigits))
			++newOffset;
		return new Token(offset, newOffset-offset, TokenType.NUMBER);
	}
	
	protected Token collect10Number(String text, int offset) {
		// first 1 chars already ok
		int newOffset = offset+1;
		boolean pointfound = false;
		boolean efound = false;
		while (newOffset < text.length()) {
			char c = text.charAt(newOffset);
			if (c=='.') {
				if (pointfound || efound)
					break;
				else 
					pointfound=true;
			} else if (c=='E' || c=='e') {
				if (efound)
					break;
				else 
					efound = true;
			} else if (!text.substring(newOffset,newOffset+1).matches(digits)) 
				break;
			++ newOffset;
		}		
		return new Token(offset, newOffset-offset, TokenType.NUMBER);
	}	
	
	protected Token collectString(String text, int offset) {
		// first char defines how the string will be terminated
		char terminator = text.charAt(offset);
		int newOffset = offset+1;
		boolean escapeSeen = false;
		while (newOffset < text.length() && 
			   (escapeSeen || text.charAt(newOffset)!=terminator)) {
			++newOffset;
			if (text.charAt(newOffset-1)==escapeChar) {
				escapeSeen=!escapeSeen;
			} else {
				escapeSeen = false;
			}
		}
		// check if this string is terminated correctly
		if (newOffset < text.length()-1)
			newOffset+=1; // also collect the terminating character
		return new Token(offset, newOffset-offset, TokenType.STRING);
	}
	
	protected Token collectPunctuation(String text, int offset) {
		int newOffset = offset+1;
		while (newOffset<text.length() && text.substring(newOffset, newOffset+1).matches(punctuation))
			++newOffset;
		return new Token(offset, newOffset-offset, TokenType.PUNCTUATION);
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		TokenSet ts;
//		ts = new DefaultTokenizer().tokenize(" 1+0x9A5*9.5 - FUN(\"bla\\\"blu\", OBJECT) ");
		ts = new DefaultTokenizer().tokenize("a\"d\\\"as\\\\d\"d");
/*		ts = new DefaultTokenizer().tokenize(
				"clusterTest <- function(dataset, set1=\"QT-Clust\", set2=\"Bin\") { "+
					  "pl1i <- grep(set1,names(dataset));"+
					  "pl2i <- grep(set2,names(dataset)); #Some comment,() bdfsdfkljlsfd++\n"+
					  "pl1 <- dataset[[pl1i]];"+
					  "pl2 <- dataset[[pl2i]];"+
					  "ma <- matrix(nrow=length(pl1), ncol=length(pl2));"+
					  "rownames(ma)<-names(pl1);"+
					  "colnames(ma)<-names(pl2);"+
					  "for(i in 1:nrow(ma)) {"+
					  "  for(j in 1:ncol(ma)) {"+
					  "    ma[i,j] <- intersect(pl1[], pl2[])"+
					  "  }"+
					  "}"
		); */
	}
	
}
