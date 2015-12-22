package mayday.mushell.syntax;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import mayday.mushell.tokenize.Token;

public class DefaultSyntaxHighlighter implements SyntaxHighlighter
{

	
	
	public AttributeSet highlight(Token token)
	{
		AttributeSet a=new SimpleAttributeSet();
		StyleContext styleContext= StyleContext.getDefaultStyleContext();

		switch(token.getType())
		{
			case COMMAND:
				a=styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Bold, true);		
				a=styleContext.addAttribute(a, StyleConstants.Foreground, Color.red);
				return a;			
			case OBJECT:
				return styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.blue);
			case NUMBER:
				return styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.red);
			case OPERATOR:
				a=styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Bold, true);		
				a=styleContext.addAttribute(a, StyleConstants.Foreground, Color.red);
				return a;
			case PUNCTUATION:
				return styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, true);
			case STRING:
				return styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.yellow);
			case TEXT:
				return styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.black);
			case COMMENT:
				return styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.gray);
			case WHITESPACE:
				return a;
			case TYPE:
				a=styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Bold, true);		
				a=styleContext.addAttribute(a, StyleConstants.Foreground, Color.red.darker());
				return a;	
			case ERROR_TOKEN:
				a=styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.red);
				return a;
			default:
				return a;
				
		}
	}
	
}
