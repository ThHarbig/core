package mayday.mushell.snippet;

import java.util.EventListener;

public interface SnippetListener extends EventListener {

	public void snippetSelected(SnippetEvent event);
	
}
