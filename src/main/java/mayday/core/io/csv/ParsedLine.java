
package mayday.core.io.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

public final class ParsedLine implements Iterable<String> {

	protected CharSequence originalLine;
	protected boolean iscommentline;
	protected ParserSettings settings;
	protected int version=-1; 
	
	protected ArrayList<MatchString> content = new ArrayList<MatchString>();
	protected int externalSize = 0;
	
	public ParsedLine(CharSequence line, ParserSettings settings) {
		originalLine = line;
		this.settings = settings;
	}
	

	private void reparse() {
		if (settings.version!=version) {
			parse();
			version=settings.version;
		}
	}
	
	public void clear() {
		externalSize = 0;
	}
	
	private void add(int begin, int end) {
		++externalSize;
		MatchString ms;
		if (content.size()<externalSize) {
			content.add(ms = new MatchString());
		} else {
			ms = content.get(externalSize-1);
		}
		ms.start=begin;
		ms.end=end;
	}

	private void parse() {
		clear();
		iscommentline=false;
		
		if (originalLine.length()==0)
			return; // fast track
		
		// is this a comment line ?
		for (int i=0; i!=settings.commentChars.length(); ++i)
			if (originalLine.charAt(0)==settings.commentChars.charAt(i))
				iscommentline=true;
		
		if (!iscommentline) {
			// not a comment, start parsing
			// first split into all tokens on any of the separators
			
			if (settings.separator.length()==0)
				add(0, originalLine.length());
			else {
				
				Matcher m = settings.getMatcher(originalLine);
							
				int first=0;
				int last=originalLine.length();
				int ldelta=0;
				boolean inGroup = false;
				boolean hasNext = false;
				
				while ((hasNext=m.find()) || last<originalLine.length()) {
					if (hasNext)
						last = m.start();
					else
						last=originalLine.length();
					ldelta=0;
					if (first!=last) {
						if (originalLine.charAt(first)==settings.quote) {
							inGroup = true;
							first++;
						}
						if (originalLine.charAt(last-1)==settings.quote && (last-first<2 || originalLine.charAt(last-2)!='\\')) { //ending with unescaped quote
							inGroup = false;
							ldelta=-1;
						}
						if (!inGroup) {
							add(first,last+ldelta);
							if (hasNext)
								first=m.end();
						}
					} else {
						if (hasNext) {
							first=m.end();
							add(first, last);
						}
					}
				}
				
				if (externalSize==0) // no separator found at all
					add(first, last);
				
			}
		}
	}	
	
//	@SuppressWarnings("null")
//	private void parse() {
//		clear();
//		iscommentline=false;
//		// is this a comment line ?
//		if (originalLine.length()>0)
//			for (int i=0; i!=settings.commentChars.length(); ++i)
//				if (originalLine.charAt(0)==settings.commentChars.charAt(i))
//					iscommentline=true;
//		
//		if (!iscommentline) {
//			// not a comment, start parsing
//			// first split into all tokens on any of the separators
//			String[] parts;			
//			if (settings.separator.length()>0)
////				parts = originalLine.split(settings.separator);
//				parts = settings.getSplitter().split(originalLine);
//			else
//				parts = new String[]{originalLine};
//				
//			
//			// now "unsplit" tokens where the separator is protected by quotes (but not by escaped quotes using \)
//			boolean inGroup = false;
//			StringBuffer group = null;
//			
//			for (String part : parts) {
//				if (!inGroup) {
//					boolean sg = startsGroup(part);
//					boolean eg = endsGroup(part);
//					if (sg && !eg) { // start of token group
//						inGroup = true;
//						group = new StringBuffer();
//						group.append(part.substring(1)); // remove quote
//					}
//					else if (sg && eg)
//						this.add(part.substring(1, part.length()-1)); //remove quotes
//					else
//						this.add(part); // single token
//				} else {
//					group.append(" ");
//					group.append(part);
//					if (endsGroup(part)) { // end of token group
//						inGroup = false;
//						this.add(group.toString().substring(0,group.length()-1)); // remove quote
//						group.setLength(0);
//					}
//				}
//			}				
//		}
//	}	
//	
//	private boolean startsGroup(String s) {
//		return s.startsWith(settings.quote);
//	}
//	
//	private boolean endsGroup(String s) {
//		return s.endsWith(settings.quote) && !s.endsWith("\\"+settings.quote);
//	}
	
	public boolean isCommentLine() {
		reparse();
		return iscommentline;
	}
	
	public String get(int column) {
		CharSequence opt = getOptimized(column);
		if (opt!=null) {
			opt = opt.toString();
			if (opt!=null)
				opt=((String)opt).trim();
			return (String)opt;
		}
		return null;
	}
	
	public void set(int columnIndex, String columnName) {
		
	}
	
	/** return column content without creating new objects */
	public CharSequence getOptimized(int column) {
		reparse();
		if (column>=this.size())
			return null;
		else
			return content.get(column);
	}
	
	public int size() {
		reparse();
		return externalSize;
	}
	
	public void replaceLine(CharSequence newLine) {
		originalLine = newLine;
		parse();
	}
	
	public String toString() {
		return originalLine+"\n"+content.toString();
	}
	
	
	public class MatchString implements CharSequence {

		protected int start, end;
		
		public char charAt(int index) {
			return originalLine.charAt(start+index);
		}

		public int length() {
			return (end-start);
		}

		public CharSequence subSequence(int start, int end) {
			return originalLine.subSequence(start+this.start, end+this.start);
		}
		
		public String toString() {
			if (start>end)
				return null;
			return originalLine.toString().substring(start, end);
		}
		
	}

	public Iterator<CharSequence> iteratorOptimized() {
		return new Iterator<CharSequence>() {
			int index=0;
			public boolean hasNext() {
				return index<externalSize;
			}

			public CharSequence next() {
				return getOptimized(index++);
			}

			public void remove() {				
				throw new UnsupportedOperationException();
			}
			
		};
	}

	public Iterator<String> iterator() {
		return new Iterator<String>() {
			int index=0;
			public boolean hasNext() {
				return index<externalSize;
			}

			public String next() {
				return get(index++);
			}

			public void remove() {				
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	public List<MatchString> asList() {
		while(content.size()>externalSize)
			content.remove(content.size()-1);			
		return Collections.unmodifiableList(content);
	}
}