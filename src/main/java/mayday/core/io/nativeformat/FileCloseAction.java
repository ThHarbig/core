package mayday.core.io.nativeformat;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.datasetmanager.DataSetManager;

@SuppressWarnings("serial")
public class FileCloseAction extends AbstractAction
{

	public static final FileCloseAction instance = new FileCloseAction();

	FileCloseAction() {
		super( "Close..." );
		setEnabled(FileRepository.hasFile());
	}

	public void actionPerformed( ActionEvent event ) {

		if (DataSetManager.singleInstance.getSize()>0) {

			String question = "Close all open DataSets?";

			question+="\n" +
			"Select YES to close all open datasets and close the project file.\n" +
			"Select NO  to keep all open datasets after closing the project file.\n" +
			"Select Cancel to abort.";

			int res = JOptionPane.showConfirmDialog(null, question, "Mayday: Close open datasets?", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (res==JOptionPane.YES_OPTION) {
				while (DataSetManager.singleInstance.getNumberOfObjects()>0)
					DataSetManager.singleInstance.remove(0);
			}
			if (res!=JOptionPane.CANCEL_OPTION)
				FileRepository.setName(null);

		} else {
			String question ="Close "+new File(FileRepository.getName()).getName()+" ?" ;

			int res = JOptionPane.showConfirmDialog(null, question, "Mayday: Close open datasets?", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (res==JOptionPane.YES_OPTION) {
				FileRepository.setName(null);
			}

		}


	}
}