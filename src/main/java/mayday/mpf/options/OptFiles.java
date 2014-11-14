package mayday.mpf.options;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import mayday.core.gui.MaydayDialog;


public class OptFiles extends OptBase {



	/** The currently selected list of files - These will be kept if "Cancel" is clicked */
	public String oldValue[]= new String[0];	

	/** The new list of selected files - These will be used if "OK" is clicked */
	protected String currentValue[];

	protected JLabel la = new JLabel();
	protected JButton bu;
	protected File file;
	protected JFileChooser fileDialog;		
	protected String fileType, fileDescription; // Strings for setting the Filetype and the Filedescription in the FileFilter

	private DefaultListModel model= new DefaultListModel();


	/** Creates a new instance with a given name, description and initial value
	 * @param name The name of the new option
	 * @param description What this option means
	 * @param fT The type of the file that should be chosen
	 */
	public OptFiles(String name, String description, String fT) {
		super(name, description);
		fileDescription = (fT + "-Files");;
		fileType = fT;
		setCurrentValue(oldValue);
	}

	protected void setCurrentValue(String[] newCurrentValue) {
		currentValue = newCurrentValue;
		la.setText("Number of selected files: " + currentValue.length );
		model.clear();
		for (int i =0; i < currentValue.length; i++){
			model.addElement(currentValue[i]);
		}
	}

	protected void createEditArea() {
		super.createEditArea();
		bu = new JButton(new BrowseAction());
		EditArea.add(la);
		EditArea.add(bu);				
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#validate()
	 */
	public boolean validate() {
		if (this.calledFromDesigner()) return true;
		//an empty list of files is valid
//		else if (currentValue.length==0){
//			JOptionPane.showMessageDialog(null, 
//					"Please choose files");
//			return false;
//		}
		return true;
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#accept()
	 */
	public void accept() {			
		//boolean changed = (!bu.getText().equals("Number of selected files: " + nValue.length));
		boolean changed = (oldValue!=currentValue);
		oldValue = currentValue;
		if (changed) postEvent("Files changed");

	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#cancel()
	 */
	public void cancel() {
		setCurrentValue(oldValue);
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueToString()
	 */
	public String ValueToString() {
		StringBuffer result = new StringBuffer();
		if (oldValue.length > 0) {
			result.append(oldValue[0]);
			for (int i=1; i<oldValue.length; i++) {
				result.append(", ");
				result.append(oldValue[i]);
			}
		}

		return result.toString();


	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
	 */


	public void ValueFromString(String valueStr) {
		String[] values = valueStr.split(", ");
		int empty=0;
		for (String s : values)
			if (s.trim().length()==0)
				empty++;
		if (empty>0) {
			String[] valuesNonEmpty = new String[values.length-empty];
			int next=0;
			for (String s : values)
				if (s.trim().length()!=0)
					valuesNonEmpty[next++]=s;
			values=valuesNonEmpty;
		}
		setCurrentValue(values);
		oldValue=currentValue;
	}

	@SuppressWarnings("serial")
	protected class BrowseAction extends AbstractAction	{

		public BrowseAction() {
			super("Browse");
		}


		/**
		 * Invoked when the action occurs.
		 * 
		 * @param event The received action event.
		 */
		public void actionPerformed( ActionEvent event )
		{
			// open a FileChooserDialog
			MultipleFileChooserDialog mfcd = new MultipleFileChooserDialog(fileType, fileDescription, currentValue, model);
			mfcd.setVisible(true);
		}







	}
	@SuppressWarnings("serial")
	class MultipleFileChooserDialog extends MaydayDialog {
		// gui elements
		private JTabbedPane tabbedPane;
		private JList filenameList ;
		private JButton addButton;
		private JFileChooser fileDialog;

		// arraylist to store filenames

		private Vector<String> filenames=new Vector<String>();
		private int selectedFile;
		private String fT, fD;


		public  final EmptyBorder DIALOG_DEFAULT_BORDER = (EmptyBorder)BorderFactory.createEmptyBorder( 10, 10, 10, 10 );
		public static final int DEFAULT_VSPACE = 10;
		public static final String ERROR_TITLE = "Error";

		public MultipleFileChooserDialog( String fileType, String fileDescription, String[] Value, DefaultListModel model)	{
			super();
			setModal( true );
			setTitle("Choose Files" );
			fT = fileType;
			fD = fileDescription;
			init();
		}

		private void init()
		{
			// initialize tabbed pane
			this.tabbedPane = new JTabbedPane();
			this.tabbedPane.add( initDataSetPanel() );
			fileDialog=new JFileChooser();
			// initialize the ok and the cancel button and set mnemonics
			JButton runButton = new JButton( new RunAction() );    
			JButton cancelButton = new JButton( new CancelAction() );       
			runButton.setMnemonic( KeyEvent.VK_ENTER );
			cancelButton.setMnemonic( KeyEvent.VK_ESCAPE );
			// create a panel to hold the OK and the cancel button
			Box buttonPanel = Box.createHorizontalBox();    
			buttonPanel.add( Box.createHorizontalGlue() ); // right-aligns the buttons
			buttonPanel.add( runButton );
			buttonPanel.add( new JLabel( " " ) ); // horizontal spacer
			buttonPanel.add( cancelButton );

			getRootPane().setDefaultButton( runButton );
			JPanel contentPane = new JPanel();
			contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
			contentPane.setBorder(DIALOG_DEFAULT_BORDER );    
			contentPane.add( this.tabbedPane );
			contentPane.add( Box.createVerticalStrut( DEFAULT_VSPACE ) );
			contentPane.add( buttonPanel );

			// add the panel to the content pane of the dialog
			getContentPane().add( contentPane, BorderLayout.CENTER );

			// set the size of the dialog and make this the fixed size 
			setSize( getContentPane().getPreferredSize() );
			setResizable( false );
			pack();    

		}

		protected JPanel initDataSetPanel()
		{
			filenameList = new JList(model);
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.getViewport().setView(filenameList);
			filenameList.setVisibleRowCount(15);
			filenameList.setFixedCellWidth(300);
			filenameList.setSelectionMode(0);
			filenameList.addListSelectionListener( new ListSelectionListener() {
				public void valueChanged (ListSelectionEvent e)	{
					int idx = filenameList.getSelectedIndex();
					selectedFile=idx;
				}		
			});

			// Label:
			JLabel listLabel=new JLabel("Selected Files:");
			Box fileBox=Box.createVerticalBox();
			fileBox.add(listLabel);
			fileBox.add(scrollPane);
			fileBox.add(Box.createVerticalGlue());

			// add tabs to tabbed pane
			JButton addButton = new JButton( new AddAction() );
			JButton removeButton = new JButton(new RemoveAction());
			JButton clearButton = new JButton(new ClearAction());
			addButton.setMnemonic( KeyEvent.VK_SPACE);
			removeButton.setMnemonic(KeyEvent.VK_DELETE);

			Box addBox= Box.createHorizontalBox();
			addBox.add(addButton);
			addBox.add(removeButton);
			addBox.add(clearButton);
			addBox.add(Box.createHorizontalGlue());

			Box allBox = Box.createVerticalBox();
			allBox.add(fileBox);
			allBox.add(addBox);
			allBox.add(Box.createVerticalGlue());


			JPanel AllContentPane = new JPanel();
			AllContentPane.setName("Select Files");
			AllContentPane.add(allBox);
			return AllContentPane;
		}

		@SuppressWarnings("unchecked")
		public List<String> getFilenames() {
			if (filenames==null || filenames.isEmpty()) 
				return Collections.EMPTY_LIST;
			return new ArrayList<String>(filenames);
		}

		public File getDirectory()	{
			File f=new File((String)filenames.get(0));
			return f;
		}

		public void setDirectory(File dir) {
			this.fileDialog.setCurrentDirectory(dir);
		}

		protected class RunAction
		extends AbstractAction  
		{
	
			public RunAction()	{
				super( "OK" );
			}

			public void actionPerformed( ActionEvent event ) {

				String[] Value = new String[model.getSize()];
				for(int i=0; i < model.getSize(); ++i) {
					Value[i]=(String)model.elementAt(i);
				}
				setCurrentValue(Value);
				dispose();
			}
		}

		protected class CancelAction
		extends AbstractAction  
		{
		
			public CancelAction() {
				super( "Cancel" );
			}

			/**
			 * Invoked when the action occurs.
			 * 
			 * @@param event The received action event.
			 */
			public void actionPerformed( ActionEvent event )
			{
				// this propagates the cancellation to the plugin
				filenames = null;
				// close the dialog window
				setVisible(false);
				dispose();
			}
		}

		protected class AddAction
		extends AbstractAction  
		{
			public AddAction() {
				super( "Add" );
			}

			public void actionPerformed( ActionEvent event ) {
				// open a FileChooserDialog

				File[] files;
				//fileDialog=new JFileChooser();
				fileDialog.setMultiSelectionEnabled(true);
				fileDialog.setFileFilter( new FileFilter() {
					public boolean accept( File f ) {
						return f.isDirectory() ||
						f.getName().toLowerCase().endsWith(fT);

					}
					public String getDescription() {
						return fD;
					}
				} );
				int fdr = fileDialog.showOpenDialog(addButton) ;
				if(fdr==JFileChooser.APPROVE_OPTION)
				{

					files=fileDialog.getSelectedFiles();

					for(int i=0; i < files.length; ++i)
					{	



						String s = files[i].getPath();
						s = s.replace('\\', '/');
						model.addElement(s);
					}
					fileDialog.setCurrentDirectory(files[0]);
				}
				// close the dialog window


			}
		}

		protected class RemoveAction
		extends AbstractAction  
		{

			public RemoveAction(){
				super( "Remove" );
			}


			public void actionPerformed( ActionEvent event ) {
				if(selectedFile>=0)
					model.remove(selectedFile);
			}
		}

		protected class ClearAction
		extends AbstractAction  
		{

			public ClearAction(){
				super( "Clear" );
			}


			public void actionPerformed( ActionEvent event ) {
				model.clear();
			}
		}


	}





}
