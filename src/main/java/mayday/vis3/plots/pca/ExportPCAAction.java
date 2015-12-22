package mayday.vis3.plots.pca;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import mayday.core.Experiment;
import mayday.core.LastDirListener;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.gui.MaydayDialog;
import mayday.core.math.JamaSubset.Matrix;

@SuppressWarnings("serial")
public class ExportPCAAction extends AbstractAction {

	private Matrix PCAData;
	@SuppressWarnings("rawtypes")
	private List things;
	
	public ExportPCAAction() {
		super("Export PCA data...");
	}
	
	@SuppressWarnings("rawtypes")
	public void setData(Matrix pcaData, List allthings) {
		PCAData = pcaData;
		things = allthings;
	}
	
	public void actionPerformed(ActionEvent e) {
		new ExportPCAMatrixDialog().setVisible(true);
	}
	
	
	public void export( String fileName, String delimiter)
	throws FileNotFoundException, IOException
	{
		int l_rows = 0;
		int l_columns = 0;

		FileWriter fileWriter;

		fileWriter = new FileWriter( fileName );

		l_rows = PCAData.getRowDimension();

		l_columns = PCAData.getColumnDimension();

		fileWriter.write( "" );
		for ( int j = 0; j < l_columns; ++j ) {
			fileWriter.write( delimiter );
			fileWriter.write( "PC"+j );
		}
		fileWriter.write( "\n" );

		for ( int i = 0; i < l_rows; ++i ) {
			// row header
			String name="";
			Object o = things.get(i);
			if (o instanceof Probe)
				name=((Probe)o).getName();
			if (o instanceof Experiment)
				name=((Experiment)o).getName();
			fileWriter.write( name );
			fileWriter.write( delimiter );
			// write row contents
			for ( int j = 0; j < l_columns; ++j ) {
				Object l_value = PCAData.get(i,j);
				if ( l_value != null )           
					fileWriter.write( l_value.toString() );
				else
					fileWriter.write( "" );

				if ( j < l_columns - 1 ) 
					fileWriter.write( delimiter );
			}
			fileWriter.write( "\n" );
		}
		fileWriter.close();    
	}
	
	public class ExportPCAMatrixDialog
	extends MaydayDialog
	implements ActionListener
	{
		private JRadioButton tabulatorDelimiter;
		private JRadioButton blankDelimiter;
		private JRadioButton commaDelimiter;
		private JRadioButton semicolonDelimiter;
		private JLabel delimiterLabel;
		private String delimiter;
		
		public ExportPCAMatrixDialog(  )
		{
			setModal( true );
			setTitle( "Export PCA Data" );
			this.delimiter = "\t"; 
			init();
		}


		protected void init()
		{

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

			// create two boxes to hold the content settings
			Box l_dimensionsVBox = Box.createVerticalBox();    
			Box l_dimensionsHBox = Box.createHorizontalBox();

			l_dimensionsVBox.add( l_dimensionsSubHBox );
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
			enableDelimiterSelection( true );
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
				String s_lastExportPath=
					MaydayDefaults.Prefs.NODE_PREFS.get(
							MaydayDefaults.Prefs.KEY_LASTSAVEDIR,
							MaydayDefaults.Prefs.DEFAULT_LASTSAVEDIR
					);

				JFileChooser l_chooser=new JFileChooser();
				l_chooser.addActionListener(new LastDirListener());
				

				if(!s_lastExportPath.equals(""))
				{
					l_chooser.setCurrentDirectory(new File(s_lastExportPath));
				}

				String l_defaultFileName = "PCAresult";
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

					try
					{      
						export( l_fileName, delimiter );

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
