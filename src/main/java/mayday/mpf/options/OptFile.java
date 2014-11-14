package mayday.mpf.options;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class OptFile extends OptBase {
	
	
		
		/** The current value of this option	 */
		public String Value;
	
		
		protected JTextField tf;
		protected JButton bu;
		protected File file;
		protected JFileChooser fileDialog;
		//setting the filetype for the filefilter. First Element is the type (e.g. ".txt") second the filedescription (e.g."text")
		protected String fileType, fileDescription ; 
		
		

		/** Creates a new instance with a given name, description and initial value
		 * @param name The name of the new option
		 * @param description What this option means
		 * @param fT The type of the file that should be choosen
		 */
		public OptFile(String name, String description, String fT) {
			super(name, description);
			fileDescription = (fT + "-Files");
			fileType = fT;
		}
		
		protected void createEditArea() {
			super.createEditArea();
			tf = new JTextField(30);
			if (Value!=null) tf.setText(Value);
			bu = new JButton(new BrowseAction());
			EditArea.add(tf);
			EditArea.add(bu);			
		}
		
		/* (non-Javadoc)
		 * @see mayday.mpf.options.OptBase#validate()
		 */
		public boolean validate() {
			if (file == null){
				JOptionPane.showMessageDialog(null, 
						"Please choose a File");
				return false;
			}
			
			return true;
		}
		
		/* (non-Javadoc)
		 * @see mayday.mpf.options.OptBase#accept()
		 */
		public void accept() {
			
			boolean changed = (!tf.getText().equals(Value));
			Value = tf.getText();
			if (changed) postEvent("File changed");
		}
		
		/* (non-Javadoc)
		 * @see mayday.mpf.options.OptBase#cancel()
		 */
		public void cancel() {
			tf.setText(Value);
		}

		/* (non-Javadoc)
		 * @see mayday.mpf.options.OptBase#ValueToString()
		 */
		public String ValueToString() {
			return Value;
		}

		/* (non-Javadoc)
		 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
		 */
		
		
		public void ValueFromString(String valueStr) {
			Value = valueStr;
			if (tf!=null) tf.setText(Value);
			file = new File(Value);
		}
		
		
		@SuppressWarnings("serial")
		protected class BrowseAction
		extends AbstractAction  
		{
		/**
			 * 
			 */
			;


		public BrowseAction()
		  {
			super( "Browse" );
		  }
	   
	   
		  /**
		   * Invoked when the action occurs.
		   * 
		   * @param event The received action event.
		   */
		  public void actionPerformed( ActionEvent event )
		  {
			// open a FileChooserDialog
			
			fileDialog=new JFileChooser();
			fileDialog.setMultiSelectionEnabled(false);
			fileDialog.setFileFilter( new FileFilter() {
		          public boolean accept( File f ) {
		            return f.isDirectory() ||
		                   f.getName().toLowerCase().endsWith(fileType);
		          }
		          public String getDescription() {
		            return fileDescription;
		          }
		        } );
			
			int fdr = fileDialog.showOpenDialog(bu) ;
			if(fdr==JFileChooser.APPROVE_OPTION)
			{
				file=fileDialog.getSelectedFile();
				tf.setText(file.getPath());
			}
			
			
			// close the dialog window
			
			
		  }
		  
		 
		  
		
		
	}
	

	
	
	
}
