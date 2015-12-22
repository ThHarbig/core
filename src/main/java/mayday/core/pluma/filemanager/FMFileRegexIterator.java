package mayday.core.pluma.filemanager;

import java.util.regex.Pattern;

public class FMFileRegexIterator extends FMFileIterator {

	private FMFile nextElement;
	private Pattern matchpat;
	
	public FMFileRegexIterator(String regExp, boolean recursive, FMDirectory rootDir) {
		super(recursive, rootDir);
		matchpat = Pattern.compile(regExp);
		getNextMatching();
	}
	
	private void getNextMatching() {
		nextElement = null;
		while (nextElement==null && super.hasNext()) {
			FMFile candidate = super.next();
			if (matchpat.matcher(candidate.Name).matches())
				nextElement = candidate;
		}
		/*if (nextElement!=null)
			System.out.println("FMFileRegexIterator::next = "+nextElement);*/
	}
	
	public boolean hasNext() {
		return (nextElement!=null);
	}

	public FMFile next() {
		FMFile ret = nextElement;
		getNextMatching();
		return ret;
	}
	
}