package mayday.mushell.snippet;

import javax.swing.JComponent;

public interface SnippetField {

	public JComponent getComponent();
	
	public void addSnippetListener(SnippetListener l);
	
	public void removeSnippedListener(SnippetListener l);
	
}
