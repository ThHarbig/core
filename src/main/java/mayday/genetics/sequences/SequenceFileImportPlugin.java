package mayday.genetics.sequences;

import java.util.List;

public interface SequenceFileImportPlugin {
	
	public void importFrom(List<String> files, SequenceContainer sc);

}
