package mayday.core.io.csv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParserSettings {
	
	public int version=0;
	public String separator = "\t";
	public int skipLines = 0;
	public boolean hasHeader = false;
	public String commentChars = "#";
	public char quote='\"';
	
	protected Pattern splitter;
	protected Matcher matcher;
	protected String splitter_sep;
	
	public Pattern getSplitter() {
		if (splitter==null || splitter_sep!=separator) {
			splitter = Pattern.compile(separator);
			splitter_sep=separator;
		}
		return splitter;
	}
	
	public Matcher getMatcher(CharSequence input) {
		if (matcher==null || splitter_sep!=separator) {
			return matcher = getSplitter().matcher(input);			
		}
		matcher.reset(input);
		return matcher;
	}
	
}