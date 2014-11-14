package mayday.core.meta.miotree;

import java.util.regex.Pattern;

import mayday.core.meta.MIGroup;

public class FileRegexIterator extends FileIterator {

	private MIGroup nextElement;
	private Pattern matchpat;
	
	public FileRegexIterator(String regExp, boolean recursive, Directory rootDir) {
		super(recursive, rootDir);
		matchpat = Pattern.compile(regExp);
		getNextMatching();
	}
	
	private void getNextMatching() {
		nextElement = null;
		while (nextElement==null && super.hasNext()) {
			MIGroup candidate = super.next();
			if (matchpat.matcher(candidate.getName()).matches())
				nextElement = candidate;
		}
		/*if (nextElement!=null)
			System.out.println("FMFileRegexIterator::next = "+nextElement);*/
	}
	
	public boolean hasNext() {
		return (nextElement!=null);
	}

	public MIGroup next() {
		MIGroup ret = nextElement;
		getNextMatching();
		return ret;
	}
	
}