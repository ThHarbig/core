/*
 * Created on Feb 4, 2005
 *
 */
package mayday.core.gui.abstractdialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import mayday.core.MaydayDefaults;
import mayday.core.gui.MaydayDialog;


/**
 * @author gehlenbo
 *
 */
@SuppressWarnings("serial")
public class StandardDialog
extends MaydayDialog
{
	protected ArrayList< AbstractStandardDialogComponent > tabs;
	protected ArrayList< Action > additionalActions;
	protected ArrayList< Action > additionalOkActions;
	protected JTabbedPane tabbedPane;
	protected Box upperBox;
	protected boolean okActionsCalled;

	public StandardDialog( String name, ArrayList< AbstractStandardDialogComponent > tabs )
	throws HeadlessException
	{
		//super( Mayday.sharedInstance );

		this.tabs = tabs;
		this.additionalActions = new ArrayList< Action >();
		this.additionalOkActions = new ArrayList< Action >();

		this.okActionsCalled = false;

		setModal( true );
		setTitle( name );

		// this will cause to call removeNotify() on all components of the dialog,
		// which can be used to remove listeners etc.
		setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
	}

	//KD: 21.04.2006 Creates a modal StandardDialog title and with the specified
	//Frame as its owner. 
	public StandardDialog(Frame owner,String name,ArrayList< AbstractStandardDialogComponent > tabs)
	throws HeadlessException{

		super(owner);

		this.tabs = tabs;
		this.additionalActions = new ArrayList< Action >();
		this.additionalOkActions = new ArrayList< Action >();

		this.okActionsCalled = false;

		setModal( true );
		setTitle( name );

		setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

	}//END KD


	/**
	 * Sets the content pane of the dialog and its bounds. 
	 */
	protected void compose()
	{
		compose( false );
	}


	/**
	 * Sets the content pane of the dialog and its bounds. 
	 */
	protected void compose( boolean alwaysTabbed )
	{       
		// initialize buttons
		JButton l_okButton = new JButton( new OkAction() );
		JButton l_cancelButton = new JButton( new CancelAction() );       

		// set mnemonics for the buttons
		l_okButton.setMnemonic( KeyEvent.VK_ENTER );    
		l_cancelButton.setMnemonic( KeyEvent.VK_ESCAPE );

		// create a box that contains the OK and the cancel button
		Box l_buttonPanel = Box.createHorizontalBox();

		if ( this.additionalActions.size() > 0 )
		{
			for ( Action l_action : this.additionalActions )
			{
				JButton l_actionButton = new JButton( l_action );
				l_buttonPanel.add( l_actionButton );
				l_buttonPanel.add( Box.createRigidArea( new Dimension( MaydayDefaults.DEFAULT_VSPACE, 0 ) ) ); // horizontal spacer
			}

			// add some extra space between additional buttons and regular buttons
			l_buttonPanel.add( Box.createRigidArea( new Dimension( MaydayDefaults.DEFAULT_VSPACE, 0 ) ) ); // horizontal spacer      
		}

		l_buttonPanel.add( Box.createHorizontalGlue() ); // right-aligns the buttons
		l_buttonPanel.add( l_cancelButton );
		l_buttonPanel.add( Box.createRigidArea( new Dimension( MaydayDefaults.DEFAULT_VSPACE, 0 ) ) ); // horizontal spacer
		l_buttonPanel.add( l_okButton );

		Box l_buttonBox = Box.createHorizontalBox();
		l_buttonBox.add( Box.createHorizontalGlue() );
		l_buttonBox.add( l_buttonPanel );

		// create a box that represents the upper part of the dialog (OK and cancel button
		// are not contained)
		this.upperBox = Box.createVerticalBox();

		if ( this.tabs.size() == 1 && !alwaysTabbed )
		{
			AbstractStandardDialogComponent l_component = this.tabs.get( 0 );      
			l_component.setBorder( MaydayDefaults.DIALOG_DEFAULT_BORDER );

			this.upperBox.add( l_component );
		}
		else if ( this.tabs.size() > 1 || ( alwaysTabbed && this.tabs.size() > 0 ) )
		{
			this.tabbedPane = new JTabbedPane();

			for ( AbstractStandardDialogComponent l_component : tabs )
			{
				addTab( l_component );
			}

			this.upperBox.add( this.tabbedPane );
		}
		else
		{
			// no tabs available
			this.tabbedPane = new JTabbedPane();      
			this.upperBox.add( this.tabbedPane );
			this.tabbedPane.setVisible( false );
		}

		this.upperBox.add( Box.createVerticalStrut( MaydayDefaults.DEFAULT_VSPACE ) );
		this.upperBox.add( l_buttonBox );
		this.upperBox.setBorder( MaydayDefaults.DIALOG_DEFAULT_BORDER );    

		// make the OK button the default button for the dialog
		// (i. e. if the user presses the return key, the action associated
		// with the OK button will be invoked)     
		getRootPane().setDefaultButton( l_okButton );    

		// add this panel to content pane of the dialog
		getContentPane().add( this.upperBox, BorderLayout.CENTER );


		// set the size of the dialog 
		setSize( getPreferredSize().width + 10, 500 );
		setResizable( true );
		pack();        
	}


	public void addTab( JComponent component )
	{
		boolean l_move = false;

		if ( this.tabbedPane.getTabCount() == 0 )
		{
			l_move = true;
		}

		component.setBorder( MaydayDefaults.DIALOG_DEFAULT_BORDER );
		this.tabbedPane.add( component );
		this.tabbedPane.setVisible( true );    
		pack();

		if ( l_move )
		{
			// get the screen resolution
			Dimension l_screen = Toolkit.getDefaultToolkit().getScreenSize();

			// compute the upper edge of the dialog to center it vertically on
			// the screen
			int l_y = (int)( ( l_screen.height - getHeight() ) * 0.5 );
			setLocation( getX(), l_y );    
		}    
	}


	public void removeTab( int index )
	{
		this.tabbedPane.remove( index );

		if ( this.tabbedPane.getTabCount() == 0 )
		{
			this.tabbedPane.setVisible( false );
			pack();
		}
	}


	public void addAction( Action newAction )
	{
		this.additionalActions.add( newAction );
	}


	public int getSelectedTab()
	{
		return ( this.tabbedPane.getSelectedIndex() );
	}


	/**
	 * Handles the termination of the dialog.
	 * 
	 * @author Nils Gehlenborg
	 * @version 0.1
	 */
	protected class OkAction
	extends AbstractAction  
	{
		/**
		 * Defines an action named "OK".
		 */
		public OkAction()
		{
			super( "OK" );
		}


		/**
		 * Invoked when the action occurs.
		 * 
		 * @param event The received action event.
		 */
		public void actionPerformed( final ActionEvent event )
		{      
			final String realTitle = StandardDialog.this.getTitle();
			StandardDialog.this.setEnabled(false);
			StandardDialog.this.setTitle("Finishing... ");
			
			new Thread("Performing final dialog actions for "+realTitle) {
				
				public void run() {
					ArrayList< Action > l_okActions; 
					okActionsCalled = true;

					// perform ok actions of individual tabs
					for ( AbstractStandardDialogComponent l_component : tabs ) {
						l_okActions = l_component.getOkActions();
						if (l_okActions!=null)
							for ( Action l_action : l_okActions )
								l_action.actionPerformed( event );
					}      

					// perform dialog-wide ok actions
					l_okActions = additionalOkActions;

					for ( Action l_action : l_okActions )
						l_action.actionPerformed( event );

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							StandardDialog.this.setTitle(realTitle);
							StandardDialog.this.setEnabled(true);
							dispose();
						}
					});

				}
				
			}.start();
				
			

			// close the dialog window          
			
		}
	}

	/**
	 * Can be used to determine whether the user clicked ok or cancel.
	 * 
	 * @author Nils Gehlenborg
	 * @return Returns true if the user clicked ok, false otherwise.
	 */     
	public boolean okActionsCalled()
	{
		return ( this.okActionsCalled );
	}


	/**
	 * Handles the termination of the dialog without any updates to
	 * the manipulated probe lists.
	 * 
	 * @author Nils Gehlenborg
	 * @version 0.1
	 */
	protected class CancelAction
	extends AbstractAction  
	{
		/**
		 * Defines an action named "Cancel".
		 */
		public CancelAction()
		{
			super( "Cancel" );
		}


		/**
		 * Invoked when the action occurs.
		 * 
		 * @param event The received action event.
		 */
		public void actionPerformed( ActionEvent event )
		{
			okActionsCalled = false;

			// close the dialog window
			dispose();
		}
	}   
}
