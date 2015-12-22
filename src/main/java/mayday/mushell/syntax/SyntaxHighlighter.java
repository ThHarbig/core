package mayday.mushell.syntax;

import javax.swing.text.AttributeSet;

import mayday.mushell.tokenize.Token;

public interface SyntaxHighlighter 
{
	public AttributeSet highlight(Token token);
}
