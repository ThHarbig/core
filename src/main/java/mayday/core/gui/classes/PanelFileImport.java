package mayday.core.gui.classes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mayday.core.ClassSelectionModel;
import mayday.core.LastDirListener;
import mayday.core.MaydayDefaults;

@SuppressWarnings("serial")
public class PanelFileImport extends JPanel {
	
	protected ClassSelectionPanel panel;
	
	public PanelFileImport(ClassSelectionPanel panel) {		
		super(new BorderLayout());
		this.panel = panel;
		setBorder(BorderFactory.createTitledBorder("Import from file"));
		add(new JButton(new BrowseAction()), BorderLayout.WEST);
		add(new JButton(new BrowseHelpAction()), BorderLayout.EAST);
		setMaximumSize(getPreferredSize());
	}
	
	private class BrowseAction extends AbstractAction {

		public BrowseAction() {
			super( "Browse" );
		}

		public void actionPerformed(ActionEvent e) 
		{
			JFileChooser chooser = new JFileChooser();
			chooser.addActionListener( new LastDirListener() );
			String lastOpenPath = MaydayDefaults.Prefs.NODE_PREFS.get(
					MaydayDefaults.Prefs.KEY_LASTOPENDIR,
					MaydayDefaults.Prefs.DEFAULT_LASTOPENDIR );

			if (!lastOpenPath.equals( "" ))
				chooser.setCurrentDirectory( new File( lastOpenPath ) );

			int option = chooser.showOpenDialog( null );

			if (option != JFileChooser.APPROVE_OPTION)
				return;

			File file=chooser.getSelectedFile();

			try {
				ClassSelectionModel partition = panel.getModel();
				
				ClassSelectionModel newPartition = new ClassSelectionModel(partition);
				if(newPartition.parse(file)) {
					// only copy the new classes to the old model, don't copy the new names
					for (int i=0; i!=partition.getNumObjects(); ++i) {
						String oname = partition.getObjectName(i);
						String newClass = newPartition.getClassOf(oname);
						partition.setClass(i, newClass);
					}
					panel.setModel(partition);
				}
				//System.out.println("ap: numobj"+partition.getNumObjects());
			} catch (IOException ex)  {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,"Error parsing the file","Error",JOptionPane.ERROR_MESSAGE);
			}	          
		}

	}
	
	private class BrowseHelpAction extends AbstractAction
	{

		protected final static String message = "<html><b>Mayday recognizes two file formats:</b><br><br>" +
				"<b>Format 1</b>: Each line contains an object name and the corresponding class name, e.g.:<br>" +
				"<pre>   Object1 Class1</pre><pre>   Object2 Class2</pre><pre>   ...</pre><br>" +
				"<b>Format 2</b>: The file contains two lines. The first line contains object names, <br>" +
				"the second line contains class names, e.g.:<br>" +
				"<pre>   Object1 Object2 Object3 ...</pre><pre>   Class1 Class2 Class3</pre>";
		
		public BrowseHelpAction() {
			super( "File format information" );
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(PanelFileImport.this, message, "File format information", JOptionPane.INFORMATION_MESSAGE);			
		}
		
	}


}
