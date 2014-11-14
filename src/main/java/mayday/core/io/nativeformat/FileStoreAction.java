package mayday.core.io.nativeformat;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class FileStoreAction extends AbstractAction {
	
	public final static FileStoreAction instance = new FileStoreAction();
	
	FileStoreAction() {
		super( "Save as..." );
	}

	public void actionPerformed( ActionEvent event ) {
		
		new FileStoreDialog();
	}
	

}
