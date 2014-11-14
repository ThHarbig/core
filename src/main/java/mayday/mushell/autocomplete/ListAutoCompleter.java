package mayday.mushell.autocomplete;

import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import mayday.core.structures.StringListModel;
import mayday.core.structures.trie.Trie;
import mayday.mushell.tokenize.DefaultTokenizer;
import mayday.mushell.tokenize.Tokenizer;

public class ListAutoCompleter extends DefaultAutoCompleter implements ListDataListener
{
	StringListModel model;
	
	public ListAutoCompleter(StringListModel model) 
	{
		this(model, new DefaultTokenizer());
		setOld();
	}
	
	public ListAutoCompleter(StringListModel model, Tokenizer tokenizer)
	{
		super(tokenizer);
		this.model=model;
		model.addListDataListener(this);
		setOld();
	}

	public List<Completion> allCompletions(String command) {
		if (old())
			update();
		return super.allCompletions(command);
	}

	public Completion complete(String command) {
		if (old())
			update();
		return super.complete(command);
	}

	protected void update()
	{
		trie = new Trie();	
		for(int i=0; i!= model.getSize(); ++i)
		{
			trie.add(model.get(i));
		}
	}
	
	public void contentsChanged(ListDataEvent e) {
		setOld();		
	}

	public void intervalAdded(ListDataEvent e) {
		setOld();
	}

	public void intervalRemoved(ListDataEvent e) {
		setOld();
	}
	
	protected boolean old() {
		return trie == null;
	}
	
	protected void setOld() {
		this.trie = null;
	}
	
	
}
