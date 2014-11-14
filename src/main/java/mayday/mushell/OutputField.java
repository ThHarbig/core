package mayday.mushell;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import mayday.mushell.syntax.SyntaxHighlighter;
import mayday.mushell.tokenize.Token;
import mayday.mushell.tokenize.TokenSet;
import mayday.mushell.tokenize.Tokenizer;

@SuppressWarnings("serial")
public class OutputField extends JTextPane
{
	protected int rollbackPosition=-1;
	protected boolean rollbackDelayed = false;
	
	private static final AttributeSet defaultAttributeSet=new SimpleAttributeSet();
	
	public OutputField() 
	{
		Font monospaced = new Font("Monospaced", Font.PLAIN, 12);
		setEditable(false);
		setFont(monospaced);
		setBackground(Color.white);
		setForeground(Color.black);
	}
	
	protected void print0(String s, AttributeSet attrs) {
		try {
			setCaretPosition(getDocument().getLength());
			getDocument().insertString(getCaretPosition(), s, attrs);
			setCaretPosition(getDocument().getLength());
		} catch (BadLocationException e) 
		{
			System.out.println(e.getMessage());
		}
	}	
	
	/**
	 * Appends the current text with a string 
	 * @param s The string to be added
	 */
	public void print(String s)
	{
		// blank line if "\r" found
		if (s.contains("\r")) {
			int pos = s.indexOf("\r");
			String pre = s.substring(0,pos);
			String post = s.substring(pos+1);
			print(pre);
			if (post.length()>0) {
				rollback();
				print(post);
			} else {
				rollbackDelayed = true;
			}
		} else {
			if (rollbackDelayed)
				rollback();
			// no more "\r"
			if (s.contains("\n")) {
				int posoffset = s.lastIndexOf("\n");
				rollbackPosition = getDocument().getLength() + posoffset+1;
			}
			print0(s, defaultAttributeSet);
		}
	}
	
	public void printWithHighlight(String s, Tokenizer tok, SyntaxHighlighter sh) {
		TokenSet tokens=tok.tokenize(s);		
		for(Token t:tokens)
			print0(s.substring(t.getStart(),t.getEnd()+1), sh.highlight(t));
				
	}
	
	protected void rollback() {
		if (rollbackPosition>-1) {
			try {
				getDocument().remove(rollbackPosition, getDocument().getLength()-rollbackPosition);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			rollbackDelayed = false;
		}
	}

	
}
