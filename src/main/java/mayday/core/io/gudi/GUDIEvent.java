package mayday.core.io.gudi;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class GUDIEvent extends ActionEvent {

	public GUDIEvent(Object sender) {
		super(sender, 0, "Import");
	}

}
