package mayday.vis3.gui.actions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import mayday.core.LastDirListener;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.gui.MaydayDialog;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ExportTableAction extends AbstractAction {

	private JTable tabular;
	private ViewModel viewModel;
	
	public ExportTableAction(JTable tabular, ViewModel viewModel) {
		super("Export...");
		this.tabular=tabular;
		this.viewModel=viewModel;
	}
	
	public void actionPerformed(ActionEvent e) {
		new ExportTabularProbeListViewerDialog().setVisible(true);
	}
	
	
	public void export( String fileName, String delimiter, boolean includeHeader, boolean useDisplayNames, boolean identifiersOnly, boolean selectionOnly )
	throws FileNotFoundException, IOException
	{
		TableModel model = tabular.getModel();
		int l_rows = model.getRowCount();
		int l_columns = 0;

		FileWriter l_fileWriter = new FileWriter( fileName );

		// NOTE: it is assumed, that the first column holds the identifier
		if ( identifiersOnly )
			l_columns = 2;
		else
			l_columns = model.getColumnCount();

		if ( includeHeader ) {
			l_fileWriter.write( MaydayDefaults.IDENTIFIER_NAME );
			if ( !identifiersOnly ) {
				for (int i=2; i!=l_columns; ++i) {
					l_fileWriter.write(delimiter);
					l_fileWriter.write(tabular.getColumnName(i));
				}
			}
			l_fileWriter.write( "\n" );
		}      

		for ( int i = 0; i < l_rows; ++i ) {
			// write only selected probes
			if ( selectionOnly ) {
				Object l_value = model.getValueAt( i, 1 );
				if (l_value == null || !viewModel.isSelected( (Probe)l_value ) )
					continue;				
			}
			
			for ( int j = 1; j < l_columns; ++j ) {
				Object l_value = model.getValueAt( i, j ); 
				if ( l_value != null )
					if ((l_value) instanceof Probe) {
						l_fileWriter.write( useDisplayNames?((Probe)l_value).getDisplayName():l_value.toString());
					} else {
						l_fileWriter.write( l_value.toString() );
					}
				else
					l_fileWriter.write( "" );
				if ( j < l_columns - 1 ) 
					l_fileWriter.write( delimiter );
			}
			l_fileWriter.write( "\n" );
		}
		l_fileWriter.close();    
	}
	
	public class ExportTabularProbeListViewerDialog
	extends MaydayDialog
	implements ActionListener
	{
		private JRadioButton tabulatorDelimiter;
		private JRadioButton blankDelimiter;
		private JRadioButton commaDelimiter;
		private JRadioButton semicolonDelimiter;
		private JCheckBox includeHeaderCheckBox;
		private JCheckBox identifiersOnlyCheckBox;
		private JCheckBox useDisplayNamesCheckBox;
		private JCheckBox selectionOnlyCheckBox;
		private JLabel delimiterLabel;
		private String delimiter;
		private boolean includeHeader;
		private boolean useDisplayNames;
		private boolean identifiersOnly;
		private boolean selectionOnly;

		public ExportTabularProbeListViewerDialog( )
		{
			setModal( true );
			setTitle( "Export" );
			init();
		}


		protected void init()
		{
			this.delimiter = "\t";
			this.includeHeader = true;
			this.identifiersOnly = false;
			this.useDisplayNames = true;

			// init radio buttons for delimiter selection
			this.tabulatorDelimiter = new JRadioButton( "Tabulator" );
			this.tabulatorDelimiter.setMnemonic( KeyEvent.VK_T );
			this.tabulatorDelimiter.addActionListener( this );

			this.blankDelimiter = new JRadioButton( "Blank" );
			this.blankDelimiter.setMnemonic( KeyEvent.VK_B );
			this.blankDelimiter.addActionListener( this );

			this.commaDelimiter = new JRadioButton( "Comma" );
			this.commaDelimiter.setMnemonic( KeyEvent.VK_C );
			this.commaDelimiter.addActionListener( this );

			this.semicolonDelimiter = new JRadioButton( "Semicolon" );
			this.semicolonDelimiter.setMnemonic( KeyEvent.VK_S );
			this.semicolonDelimiter.addActionListener( this );

			// create a button group for the delimiter radio buttons
			ButtonGroup l_buttonGroup = new ButtonGroup();
			l_buttonGroup.add( this.tabulatorDelimiter );
			l_buttonGroup.add( this.blankDelimiter );
			l_buttonGroup.add( this.commaDelimiter );
			l_buttonGroup.add( this.semicolonDelimiter );

			// init checkboxes for header selection and identifiers only
			this.includeHeaderCheckBox = new JCheckBox( "Include Header" );
			this.includeHeaderCheckBox.setMnemonic( KeyEvent.VK_H );
			this.includeHeaderCheckBox.setSelected( this.includeHeader );   
			this.includeHeaderCheckBox.addActionListener( this );

			this.useDisplayNamesCheckBox = new JCheckBox( "Use display names" );
			this.useDisplayNamesCheckBox.setMnemonic( KeyEvent.VK_D );
			this.useDisplayNamesCheckBox.setSelected( this.useDisplayNames );   
			this.useDisplayNamesCheckBox.addActionListener( this );
			
			this.identifiersOnlyCheckBox = new JCheckBox( "Identifiers Only" );
			this.identifiersOnlyCheckBox.setMnemonic( KeyEvent.VK_I );
			this.identifiersOnlyCheckBox.setSelected( this.identifiersOnly );   
			this.identifiersOnlyCheckBox.addActionListener( this );

			this.selectionOnlyCheckBox = new JCheckBox( "Selection Only" );
			this.selectionOnlyCheckBox.setMnemonic( KeyEvent.VK_L );
			this.selectionOnlyCheckBox.setSelected( this.selectionOnly );   
			this.selectionOnlyCheckBox.addActionListener( this );

			// enable/disable "selection only" check box
			this.selectionOnlyCheckBox.setEnabled( viewModel.getSelectedProbes().size() > 0 );

			// setup a label for the column delimiter selection
			this.delimiterLabel = new JLabel( "Column Delimiter" );

			// create ok and cancel buttons
			JButton l_okButton = new JButton( new OkAction() );
			l_okButton.setMnemonic( KeyEvent.VK_ENTER );

			JButton l_cancelButton = new JButton( new CancelAction() );       
			l_cancelButton.setMnemonic( KeyEvent.VK_ESCAPE );


			// create the main content pane
			JPanel l_contentPane = new JPanel();
			l_contentPane.setLayout( new BoxLayout( l_contentPane, BoxLayout.Y_AXIS ) );
			l_contentPane.setBorder( BorderFactory.createEmptyBorder( 20, 20, 20, 20 ) );

			// create two boxes to hold the delimiter selection
			Box l_formatSelectionVBox = Box.createVerticalBox();    
			Box l_formatSelectionHBox = Box.createHorizontalBox();

			l_formatSelectionVBox.add( this.delimiterLabel );
			l_formatSelectionVBox.add( this.tabulatorDelimiter );
			l_formatSelectionVBox.add( this.blankDelimiter );
			l_formatSelectionVBox.add( this.commaDelimiter );
			l_formatSelectionVBox.add( this.semicolonDelimiter );
			l_formatSelectionVBox.add( Box.createVerticalGlue() );

			l_formatSelectionHBox.add( l_formatSelectionVBox );
			l_formatSelectionHBox.add( Box.createHorizontalGlue() );


			// create a box to hold content settings
			Box l_dimensionsSubHBox = Box.createHorizontalBox();

			l_dimensionsSubHBox.add( new JLabel( "Content" ) );
			l_dimensionsSubHBox.add( Box.createHorizontalGlue() );

			// create a box thats holds the "include header" check box
			Box l_includeHeaderBox = Box.createHorizontalBox();
			l_includeHeaderBox.add( this.includeHeaderCheckBox );
			l_includeHeaderBox.add( Box.createHorizontalGlue() );
			
			Box l_useDisplayNamesBox = Box.createHorizontalBox();
			l_useDisplayNamesBox.add( this.useDisplayNamesCheckBox );
			l_useDisplayNamesBox.add( Box.createHorizontalGlue() );
			

			// create a box thats holds the "identifiers only" check box
			Box l_identifiersOnlyBox = Box.createHorizontalBox();
			l_identifiersOnlyBox.add( this.identifiersOnlyCheckBox );
			l_identifiersOnlyBox.add( Box.createHorizontalGlue() );

			// create a box thats holds the "selection only" check box
			Box l_selectionOnlyBox = Box.createHorizontalBox();
			l_selectionOnlyBox.add( this.selectionOnlyCheckBox );
			l_selectionOnlyBox.add( Box.createHorizontalGlue() );

			// create two boxes to hold the content settings
			Box l_dimensionsVBox = Box.createVerticalBox();    
			Box l_dimensionsHBox = Box.createHorizontalBox();

			l_dimensionsVBox.add( l_dimensionsSubHBox );
			l_dimensionsVBox.add( l_includeHeaderBox );
			l_dimensionsVBox.add( l_useDisplayNamesBox );
			l_dimensionsVBox.add( l_identifiersOnlyBox );
			l_dimensionsVBox.add( l_selectionOnlyBox );
			l_dimensionsVBox.add( Box.createVerticalGlue() );

			l_dimensionsHBox.add( l_dimensionsVBox );
			l_dimensionsHBox.add( Box.createVerticalGlue() );


			// create the upper portion of the dialog
			Box l_upperBox = Box.createHorizontalBox();

			l_upperBox.add( Box.createHorizontalGlue() );
			l_upperBox.add( l_formatSelectionHBox );
			l_upperBox.add( new JLabel( "      " ) );
			l_upperBox.add( l_dimensionsHBox );
			l_upperBox.add( Box.createHorizontalGlue() );

			// add the upper part of the dialog to the main content pane
			l_contentPane.add( l_upperBox );

			// create a box for ok and cancel buttons
			Box l_buttonPanel = Box.createHorizontalBox();

			l_buttonPanel.add( Box.createHorizontalGlue() );
			l_buttonPanel.add( l_cancelButton );
			l_buttonPanel.add( new JLabel( " " ) );
			l_buttonPanel.add( l_okButton );

			// make the ok button the default button of the dialog
			getRootPane().setDefaultButton( l_okButton );

			// add the buttons to the main content pane
			l_contentPane.add( Box.createVerticalStrut( 10 ) );
			l_contentPane.add( l_buttonPanel );   

			// add the main content pane to the dialog's content pane
			getContentPane().add( l_contentPane, BorderLayout.CENTER );			

			// set the size of the dialog
			setSize( getPreferredSize().width + 10, getPreferredSize().height + 20 );

			setResizable( false );		

			// tab-delimited is default
			this.tabulatorDelimiter.setSelected( true );
			enableDelimiterSelection( !this.identifiersOnly );
		}


		protected void enableDelimiterSelection( boolean enable )
		{
			delimiterLabel.setEnabled( enable );
			this.commaDelimiter.setEnabled( enable );
			this.tabulatorDelimiter.setEnabled( enable );
			this.semicolonDelimiter.setEnabled( enable );
			this.blankDelimiter.setEnabled( enable );
		}


		public void actionPerformed( ActionEvent event )
		{
			if ( event.getSource() == tabulatorDelimiter )
			{
				this.delimiter = "\t"; 
			}
			if ( event.getSource() == blankDelimiter )
			{
				this.delimiter = " "; 
			}
			if ( event.getSource() == commaDelimiter )
			{
				this.delimiter = ","; 
			}
			if ( event.getSource() == semicolonDelimiter )
			{
				this.delimiter = ";"; 
			}

			if ( event.getSource() == includeHeaderCheckBox )
			{
				this.includeHeader = includeHeaderCheckBox.isSelected();
			}


			if ( event.getSource() == useDisplayNamesCheckBox )
			{
				this.useDisplayNames = useDisplayNamesCheckBox.isSelected();
			}

			
			if ( event.getSource() == identifiersOnlyCheckBox )
			{
				this.identifiersOnly = identifiersOnlyCheckBox.isSelected();

				// disable/enable delimiter selection
				enableDelimiterSelection( !this.identifiersOnly );
			}

			if ( event.getSource() == selectionOnlyCheckBox )
			{
				this.selectionOnly = selectionOnlyCheckBox.isSelected();
			}
		}


		protected class OkAction
		extends AbstractAction	
		{
			public OkAction()
			{
				super( "OK" );
			}


			public void actionPerformed( ActionEvent event )
			{
				//MZ 23.01.04
				String s_lastExportPath=
					MaydayDefaults.Prefs.NODE_PREFS.get(
							MaydayDefaults.Prefs.KEY_LASTSAVEDIR,
							MaydayDefaults.Prefs.DEFAULT_LASTSAVEDIR
					);

				//old: JFileChooser l_chooser;
				JFileChooser l_chooser=new JFileChooser();
				l_chooser.addActionListener(new LastDirListener());

				/*
				if ( MaydayDefaults.s_lastExportPath.equals( "" ) )
				{
					l_chooser = new JFileChooser();
				}
				else
				{
					l_chooser = new JFileChooser( MaydayDefaults.s_lastExportPath );
				}//*/

				if(!s_lastExportPath.equals(""))
				{
					l_chooser.setCurrentDirectory(new File(s_lastExportPath));
				}

				//end MZ


				String l_defaultFileName = viewModel.getDataSet().getName();
				l_defaultFileName = l_defaultFileName.toLowerCase();
				l_defaultFileName = l_defaultFileName.replace( ' ', '_' ); // replace spaces
				l_defaultFileName += "." + MaydayDefaults.DEFAULT_TABULAR_EXPORT_EXTENSION;

				l_chooser.setSelectedFile( new File( l_defaultFileName ) );

				int l_option = l_chooser.showSaveDialog( (Component)event.getSource() );

				if ( l_option  == JFileChooser.APPROVE_OPTION )
				{
					String l_fileName = l_chooser.getSelectedFile().getAbsolutePath();
					MaydayDefaults.s_lastExportPath = l_chooser.getCurrentDirectory().getAbsolutePath();

					// if the user presses cancel, then quit
					if ( l_fileName == null )
					{
						return;
					}
					
					// ask before overwriting file
					if (new File(l_fileName).exists() && 
							JOptionPane.showConfirmDialog(ExportTabularProbeListViewerDialog.this, 
									"Do you really want to overwrite the existing file \""+l_fileName+"\"?",
									"Confirm file overwrite", 
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
									!=JOptionPane.YES_OPTION
					) {
						return;
					}
						
					

					try
					{      
						export( l_fileName, delimiter, includeHeader, useDisplayNames, identifiersOnly, selectionOnly );

						// dialog window can be closed savely now
						dispose();      
					}
					catch ( FileNotFoundException exception )
					{
						String l_message = MaydayDefaults.Messages.FILE_NOT_FOUND;
						l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT, l_fileName );

						JOptionPane.showMessageDialog( null,
								l_message,
								MaydayDefaults.Messages.ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE ); 
					}
					catch ( IOException exception )
					{
						JOptionPane.showMessageDialog( null,
								exception.getMessage(),
								MaydayDefaults.Messages.ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE ); 
					}
				}			
			}
		}


		protected class CancelAction
		extends AbstractAction	
		{
			public CancelAction()
			{
				super( "Cancel" );
			}		


			public void actionPerformed( ActionEvent event )
			{
				// close the dialog window
				dispose();
			}
		}

	}


}
