package mayday.mushell.snippet;

import java.util.EventObject;

@SuppressWarnings("serial")
public class SnippetEvent extends EventObject {

	protected String snip;
	protected boolean replace;
	
	public SnippetEvent(SnippetField source, String snippet, boolean asReplacement) {
		super(source);
		snip = snippet;
		replace = asReplacement;
	}
	
	public SnippetField getSource() {
		return ((SnippetField)super.getSource());
	}
	
	public String getSnippet() {
		return snip;
	}
	
	public boolean isReplacement() {
		return replace;
	}
	

}
