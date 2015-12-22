package mayday.core.io.gude;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class GUDEEvent extends ActionEvent {

	private String filename;
	
	public GUDEEvent(Object sender, String fileName) {
		super(sender, 0, "Import");
		filename=fileName;
	}
	
	public String getFileName() {
		return filename;
	}

}
