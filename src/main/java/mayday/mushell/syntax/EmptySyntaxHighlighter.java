package mayday.mushell.syntax;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;

import mayday.mushell.tokenize.Token;

public class EmptySyntaxHighlighter implements SyntaxHighlighter
{
		
	public AttributeSet highlight(Token token) 
	{
		return SimpleAttributeSet.EMPTY;
	}

}
