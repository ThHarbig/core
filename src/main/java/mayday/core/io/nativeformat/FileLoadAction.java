package mayday.core.io.nativeformat;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.datasetmanager.DataSetManager;

@SuppressWarnings("serial")
public class FileLoadAction extends AbstractAction
{

	public FileLoadAction() {
		super( "Open..." );
		setEnabled(!FileRepository.hasFile());
	}

	public FileLoadAction(String title) {
		super(title);
	}

	public void actionPerformed( ActionEvent event ) {

		if (DataSetManager.singleInstance.getSize()>0) {

			String question = FileRepository.hasFile() ? "Close "+new File(FileRepository.getName()).getName()+" ?" : "Close all open DataSets?";

			question+="\n" +
			"Select YES to close all open datasets and load the newly opened file.\n" +
			"Select NO  to merge all open datasets with those in the newly opened file.\n" +
			"Select Cancel to abort.";

			int result = JOptionPane.showConfirmDialog(null, question, "Mayday: Close open datasets?", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			switch(result) {
			case JOptionPane.YES_OPTION:  
				while (DataSetManager.singleInstance.getNumberOfObjects()>0)
					DataSetManager.singleInstance.remove(0);
				FileRepository.setName(null);
				break;
			case JOptionPane.CANCEL_OPTION:
				return;
			default: //nothing
			}		
		}
		load();
	}

	protected void load() {
		new FileLoadDialog();
	}
}