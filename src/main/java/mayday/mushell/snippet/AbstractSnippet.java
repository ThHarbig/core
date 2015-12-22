package mayday.mushell.snippet;

import mayday.core.EventFirer;

public abstract class AbstractSnippet implements SnippetField {
	
	protected EventFirer<SnippetEvent, SnippetListener> eventfirer = new EventFirer<SnippetEvent, SnippetListener>() {
		protected void dispatchEvent(SnippetEvent event, SnippetListener listener) {
			listener.snippetSelected(event);
		}
	};
	
	public void addSnippetListener(SnippetListener l) {
		eventfirer.addListener(l);
	}

	public void removeSnippedListener(SnippetListener l) {
		eventfirer.removeListener(l);
	}

}
