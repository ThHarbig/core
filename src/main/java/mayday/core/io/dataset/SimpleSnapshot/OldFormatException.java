package mayday.core.io.dataset.SimpleSnapshot;

@SuppressWarnings("serial")
public class OldFormatException extends Exception {

	public OldFormatException() {
		super("Snapshot file version is not supported");
	}
	
}
