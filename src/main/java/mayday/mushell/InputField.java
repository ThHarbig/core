package mayday.mushell;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import mayday.core.DelayedUpdateTask;
import mayday.mushell.autocomplete.AutoCompleter;
import mayday.mushell.autocomplete.ChoiceChooser;
import mayday.mushell.autocomplete.Completion;
import mayday.mushell.autocomplete.DefaultChoiceChooser;
import mayday.mushell.contexthelp.ContextHelp;
import mayday.mushell.syntax.DefaultSyntaxHighlighter;
import mayday.mushell.syntax.SyntaxHighlighter;
import mayday.mushell.tokenize.DefaultTokenizer;
import mayday.mushell.tokenize.Token;
import mayday.mushell.tokenize.TokenSet;
import mayday.mushell.tokenize.Tokenizer;


@SuppressWarnings("serial")
public class InputField extends JEditorPane implements DocumentListener
{
	/** Handles auto completion	 */
	private AutoCompleter autoCompleter;

	/** Handles Syntax highlighting  */
	private SyntaxHighlighter syntaxHighlighter;

	/** Displays context-based help */
	private ContextHelp contextHelp;

	/** The console */
	private Console console;

	/** The key(s) used to invoke auto completion. */
	private int completionKey;

	/** The key used to execute the current command	 */
	private int enterKey;

	public static final int CTRL_SPACE=KeyEvent.CTRL_DOWN_MASK | KeyEvent.VK_SPACE;

	private boolean completionInvoked;

	private ChoiceChooser choiceChooser;

	private DefaultStyledDocument doc;

	private Tokenizer tokenizer;

	/**
	 * Creates a new empty input field 
	 */
	public InputField(Console console)
	{
		super("text/rtf","");
		doc=new DefaultStyledDocument();
		setDocument(doc);
		setBackground(Color.WHITE);
		this.console=console;
		Font monospaced = new Font("Monospaced", Font.PLAIN, 12);
		getInsets().set(5, 5,5,5);
		setFont(monospaced);
		syntaxHighlighter=new DefaultSyntaxHighlighter();
		enterKey = KeyEvent.VK_ENTER;
		completionKey = KeyEvent.VK_TAB;
		addKeyListener(new InputListener());
		completionInvoked=false;
		choiceChooser=new DefaultChoiceChooser(this);	
		tokenizer=new DefaultTokenizer();
		getDocument().addDocumentListener(this);
	}



	/**
	 * Inserts a piece of code at the current caret position. 
	 * @param snippet The code to be inserted
	 */
	public void insertSnippet(String snippet)
	{
		try 
		{
			doc.insertString(getCaretPosition(), snippet, null);
			requestFocus();			
		} catch (BadLocationException e) 
		{
			System.out.println(e.getMessage());
		}
	}



	/**
	 * Removes the last character from the field
	 */
	public void backspace()
	{
		try 
		{
			getDocument().remove(getCaretPosition()-1, 1);
			setCaretPosition(getCaretPosition());
			requestFocus();
		} catch (BadLocationException e) 
		{
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Removes the last character from the field
	 */
	public void deleteChar()
	{
		try 
		{
			getDocument().remove(getCaretPosition(), 1);
			setCaretPosition(getCaretPosition());
			requestFocus();
		} catch (BadLocationException e) 
		{			
		}
	}


	/**
	 * Inserts a piece of code at the current caret position. 
	 * @param command The code to be inserted
	 */
	public void replaceContent(String command)
	{
		try 
		{
			getDocument().remove(0, getDocument().getLength());
			getDocument().insertString(0, command, null);
			setCaretPosition(getDocument().getLength());
			requestFocus();
		} catch (BadLocationException e) 
		{
			System.out.println(e.getMessage());
		}
	}

	private DelayedUpdateTask highlighterTask = new DelayedUpdateTask("Syntax highlighting", 50) {
	
		protected Runnable runnable = new Runnable(){

			public void run() 
			{
				TokenSet tokens=tokenizer.tokenize(getText());
				for(Token t:tokens)
				{
					doc.setCharacterAttributes(t.getStart(), t.getLength()+1, syntaxHighlighter.highlight(t), true);
				}					
			}};
		
		protected void performUpdate() {
			SwingUtilities.invokeLater(runnable);
		}
	
		protected boolean needsUpdating() {
			return true;
		}
	};
	
	private void setStyle()
	{
		highlighterTask.trigger();
	}

	/**
	 * Reacts to the input in this field. 
	 * @author symons
	 *
	 */
	private class InputListener extends KeyAdapter
	{		
		/* (non-Javadoc)
		 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) 
		{
			//			
			if (e.isControlDown()) 
			{
				// Control key: insert line break				
				switch(e.getKeyCode())
				{
				case KeyEvent.VK_ENTER:  //   
					e.consume();
					int carpos = getCaretPosition();
					String currentCommand = getText().substring(0,carpos)
					+"\n"
					+ getText().substring(carpos);
					replaceContent(currentCommand);
					setCaretPosition(carpos+1);
				}
			} else {
				if(e.getKeyCode()==enterKey)
				{
					try {
						console.dispatch(getDocument().getText(0, getDocument().getLength()));
					} catch (BadLocationException e1) 
					{
						e1.printStackTrace();
					}
					replaceContent("");
					e.consume();
					return;
				}
				if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
				{
					replaceContent("");
					e.consume();
					return;
				}
				if(e.getKeyCode()==completionKey)
				{
					try 
					{
						autoComplete();
					} catch (BadLocationException e1) 
					{
						e1.printStackTrace();
					}
					e.consume();
					return;
				}
				completionInvoked=false;
				if(e.getKeyCode()==KeyEvent.VK_PAGE_UP)
				{
					// get previous item from history.
					if(console.getHistoryField()!=null)
						replaceContent(console.getHistoryField().getPrevious(getText()));
					e.consume();
				}
				if(e.getKeyCode()==KeyEvent.VK_PAGE_DOWN)
				{
					// get next item from history.
					if(console.getHistoryField()!=null)
						replaceContent(console.getHistoryField().getNext(getText()));				
					e.consume();
				}
			}			
		}
	}

	public void autoComplete() throws BadLocationException
	{
		if(completionInvoked)
		{
			allCompletions();
		}else
		{
			singleCompletion();
		}		
	}

	public void singleCompletion() throws BadLocationException
	{
		Completion completion=autoCompleter.complete(getText(0,getCaretPosition()));
		if(!completion.isEmpty())
			autoComplete(completion);
		completionInvoked=true;
	}

	public void allCompletions() throws BadLocationException
	{
		List<Completion> choice=autoCompleter.allCompletions(getText(0,getCaretPosition()));
		if(choice.size()!=0)
		{
			if(choice.size()==1 && choice.get(0).isEmpty())
			{
				return;
			}
			Point p=getCaret().getMagicCaretPosition();
			if (p==null)
				p = new Point(0,0);
//			choiceChooser=new ChoiceChooser(this);
			choiceChooser.setChoice(choice);	
			choiceChooser.show(this,p.x,p.y+15);
			SwingUtilities.invokeLater(new Runnable(){

				public void run() {
					choiceChooser.requestFocus();							
				}});
		}
		completionInvoked=false;
	}

	public void autoComplete(Completion completion) throws BadLocationException
	{
		getDocument().remove(getCaretPosition()-completion.getOldTokenLength(), completion.getOldTokenLength());
		getDocument().insertString(getCaretPosition(), completion.prefixReplacement()+completion.getCompletion(), null);
	}


	/**
	 * @return the autoCompleter
	 */
	public AutoCompleter getAutoCompleter() {
		return autoCompleter;
	}



	/**
	 * @param autoCompleter the autoCompleter to set
	 */
	public void setAutoCompleter(AutoCompleter autoCompleter) {
		this.autoCompleter = autoCompleter;
	}



	/**
	 * @return the syntaxHighlighter
	 */
	public SyntaxHighlighter getSyntaxHighlighter() {
		return syntaxHighlighter;
	}



	/**
	 * @param syntaxHighlighter the syntaxHighlighter to set
	 */
	public void setSyntaxHighlighter(SyntaxHighlighter syntaxHighlighter) {
		this.syntaxHighlighter = syntaxHighlighter;
	}



	/**
	 * @return the contextHelp
	 */
	public ContextHelp getContextHelp() {
		return contextHelp;
	}



	/**
	 * @param contextHelp the contextHelp to set
	 */
	public void setContextHelp(ContextHelp contextHelp) {
		this.contextHelp = contextHelp;
	}

	/**
	 * @return the console
	 */
	public Console getConsole() {
		return console;
	}



	/**
	 * @param console the console to set
	 */
	public void setConsole(Console console) {
		this.console = console;
	}



	/**
	 * @return the completionKey
	 */
	public int getCompletionKey() {
		return completionKey;
	}



	/**
	 * @param completionKey the completionKey to set
	 */
	public void setCompletionKey(int completionKey) {
		this.completionKey = completionKey;
	}



	/**
	 * @return the enterKey
	 */
	public int getEnterKey() {
		return enterKey;
	}



	/**
	 * @param enterKey the enterKey to set
	 */
	public void setEnterKey(int enterKey) {
		this.enterKey = enterKey;
	}

	public String getText()
	{		
		try {
			return getDocument().getText(0, getDocument().getLength());
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		return null;
	}



	public void changedUpdate(DocumentEvent e) {
		// nothing
	}



	public void insertUpdate(DocumentEvent e) {
		setStyle();		
	}



	public void removeUpdate(DocumentEvent e) {
		setStyle();		
	}



	public Tokenizer getTokenizer() {
		return tokenizer;
	}



	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}


	public void setChoiceChooser(ChoiceChooser cc) {
		choiceChooser=cc;
	}
	
}
