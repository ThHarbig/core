package mayday.core.gui.probelist;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayDialog;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.gui.cellrenderer.ProbeListCellRenderer;

/**
 * This dialog provides a pane that enables the user to define a probe list selecting
 * single probes from the master table. Furthermore, the user can select an exisiting
 * probe list and use it as basis for a probe list. Finally, it is possible to
 * use the complement of the probe list instead of the probe list.
 * The dialog can be used to create either new probe lists or to edit existing ones.
 * The dialog works on three probe lists:
 * 
 * <ul>
 *  <li>
 *   The source probe list is a list that contains all probes, that are not included in the
 *   target probe list, but in the master table.
 *  </li>
 *  <li>
 *   The target probe list contains all probes that have been selected to be contained in
 *   the manipulated probe list (actually the target probe list is the currently manipulated
 *   probe list).
 *  </li>
 *  <li>
 *   The base probe list is the probe list that the user provided for manipulation. The base
 *   probe list is <code>null</code> if a new probe list is created.  
 *  </li>
 * </ul>
 * 
 * @author Nils Gehlenborg
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ProbeListContentDialog
extends MaydayDialog
{
	private ProbeListManager probeListManager;
	private ProbeList targetProbeList;
	private ProbeList sourceProbeList;
	private ProbeList baseProbeList;
	private ProbeListbox sourceProbeListView;
	private ProbeListbox targetProbeListView;
	private JLabel nameLabel;
	private JLabel sourceSizeLabel;
	private JLabel targetSizeLabel;
	private JComboBox baseProbeListComboBox;
	private ExchangeProbeAction addProbeAction;
	private ExchangeProbeAction removeProbeAction;

	//090116 fb: properly implement the ProbeListPlugin interface   
	private ProbeList result;

	/**
	 * Constructs the dialog and makes it modal.
	 * 
	 * @param baseProbeList The probe list to be manipulated, <code>null</code> if a new probe
	 *                      list is to be created.
	 * @param probeListManager Probe list manager.
	 * @param newName Name of the probe list if the dialog is called to create a new
	 *                probe list, <code>null</code> otherwise.
	 * 
	 * @throws HeadlessException
	 */
	public ProbeListContentDialog( ProbeList baseProbeList,
			ProbeListManager probeListManager,
			String newName )
	throws HeadlessException
	{
		super();

		this.targetProbeList = new ProbeList( probeListManager.getDataSet(), false );
		this.probeListManager = probeListManager;

		setTitle( "Probe List Content" );

		if ( baseProbeList != null )
		{
			this.targetProbeList = (ProbeList)baseProbeList.clone();
			this.baseProbeList = baseProbeList;
		}
		else
		{
			this.targetProbeList.setName( newName );
			this.baseProbeList = null;
		}

		setModal( true );

		init();
	}


	/**
	 * Sets the content pane of the dialog and its bounds. 
	 */
	@SuppressWarnings("unchecked")
	protected void init()
	{    		
		// initialize actions that can be disabled (they must live as long as the object!)
		addProbeAction = new ExchangeProbeAction( ExchangeProbeAction.ADD );
		addProbeAction.setEnabled( false );
		removeProbeAction = new ExchangeProbeAction( ExchangeProbeAction.REMOVE );
		removeProbeAction.setEnabled( false );    

		// initialize buttons
		JButton l_okButton = new JButton( new OkAction() );
		JButton l_cancelButton = new JButton( new CancelAction() );       
		JButton l_addButton = new JButton( addProbeAction );
		JButton l_removeButton = new JButton( removeProbeAction );
		JButton l_swapButton = new JButton( new SwapAction() );
		//JButton l_propertiesButton = new JButton( new EditPropertiesAction() );

		// set mnemonics for the buttons
		l_okButton.setMnemonic( KeyEvent.VK_ENTER );    
		l_cancelButton.setMnemonic( KeyEvent.VK_ESCAPE );
		l_addButton.setMnemonic( KeyEvent.VK_A );
		l_removeButton.setMnemonic( KeyEvent.VK_R );    
		l_swapButton.setMnemonic( KeyEvent.VK_S );
		//l_propertiesButton.setMnemonic( KeyEvent.VK_P );

		// determine content of the source and the target list
		this.sourceProbeList = new ProbeList( this.targetProbeList.getDataSet(), false );
		Object[] l_masterTableProbes = this.probeListManager.getDataSet().getMasterTable().getProbes().values().toArray();

		for ( int i = 0; i < l_masterTableProbes.length; ++i )
		{
			// probes that are not in the target probe list are added to the source
			// probe list
			if ( !this.targetProbeList.contains( (Probe)l_masterTableProbes[i] ) )
			{
				this.sourceProbeList.addProbe( (Probe)l_masterTableProbes[i] );
			}

			// IMPROVEMENT: this could be sped-up for new probe lists (all probes are source probes)
		}

		// sort list data
		java.util.List l_tempSourceList = new ArrayList( this.sourceProbeList.toCollection() );
		java.util.List l_tempTargetList = new ArrayList( this.targetProbeList.toCollection() );
		Collections.sort( l_tempSourceList );
		Collections.sort( l_tempTargetList );

		// initialize list boxes that will hold the probes of the source and the target
		// probe list and insert them into scroll panes
		this.sourceProbeListView = new ProbeListbox();
		this.targetProbeListView = new ProbeListbox();
		JScrollPane l_sourceProbeListViewScroller = new JScrollPane( this.sourceProbeListView );
		JScrollPane l_targetProbeListViewScroller = new JScrollPane( this.targetProbeListView );

		// set list data (fill the list boxes with the sorted probes)
		this.sourceProbeListView.setListData( l_tempSourceList.toArray() );
		this.targetProbeListView.setListData( l_tempTargetList.toArray() );

		// add listeners that are notified each time the selection of a list box is altered
		// (these listeners disable/enable the add action and the remove action depending on the
		// selection)
		this.sourceProbeListView.addListSelectionListener( new ProbeSelectionListener() );
		this.targetProbeListView.addListSelectionListener( new ProbeSelectionListener() );    

		// initialize a combo box that holds all existing probe lists and allows the user
		// to select a probe list as basis
		this.baseProbeListComboBox = new JComboBox();		
		// NOTE: should we do this?
		//this.baseProbeListComboBox.setBackground( Color.white );

		// set base data (fill the combo box)
		for ( int i = 0; i < this.probeListManager.getNumberOfObjects(); ++i )
		{
			// if the user is about to edit an exisiting probe list, the target probe 
			// list is added to the basis combo box, so all changes to the probe list (annotation,
			// layout and size) will be immediately visible in this combo box; the original probe list
			// is only manipulated when the user clicks the OK button
			if ( ((ProbeList)this.probeListManager.getObjects().toArray()[i]).getName() !=
				this.targetProbeList.getName() )
			{
				this.baseProbeListComboBox.addItem( this.probeListManager.getObjects().toArray()[i] );
			}
			else
			{
				this.baseProbeListComboBox.addItem( this.targetProbeList );       
			}
		}    

		// if the user is about to create a new probe list, the target probe list is added to the
		// basis combo box
		if ( !this.probeListManager.contains( this.targetProbeList ) )
		{
			this.baseProbeListComboBox.addItem( this.targetProbeList );       
		}

		// set renderer for the combo box (colors the probe lists)
		this.baseProbeListComboBox.setRenderer( new ProbeListCellRenderer() );

		// set basis probe list to the target probe list
		this.baseProbeListComboBox.setSelectedItem( this.targetProbeList );

		// add a selection listener that updates the target list if a new basis probe list is
		// selected
		// NOTE: this must be done after the initial item has been selected!
		this.baseProbeListComboBox.addItemListener( new ProbeListSelectionListener() );

		// set prototype widths for the list boxes and the combo box
		this.sourceProbeListView.setPrototypeCellValue( "probesetidentifierprobesetidentifier" );
		this.targetProbeListView.setPrototypeCellValue( "probesetidentifierprobesetidentifier" );        
		ProbeList l_tempProbeList = new ProbeList( this.probeListManager.getDataSet(), false );
		l_tempProbeList.setName( "probesetidentifier" );
		this.baseProbeListComboBox.setPrototypeDisplayValue( l_tempProbeList );

		// initialize all labels 
		JLabel l_baseProbeListLabel = new JLabel( "Basis" );
		this.nameLabel = new JLabel( this.targetProbeList.getName() );    
		this.sourceSizeLabel = new JLabel();
		this.targetSizeLabel = new JLabel();    
		updateStatistics(); // updates the source- and target-size labels

		// create a box that contains the add, swap and remove button 
		Box l_exchangeButtonBox = Box.createHorizontalBox();
		l_exchangeButtonBox.add( l_addButton );
		l_exchangeButtonBox.add( Box.createHorizontalGlue() );
		l_exchangeButtonBox.add( new JLabel( " " ) ); // horizontal spacer
		l_exchangeButtonBox.add( l_swapButton );
		l_exchangeButtonBox.add( Box.createHorizontalGlue() );
		l_exchangeButtonBox.add( new JLabel( " " ) ); // horizontal spacer
		l_exchangeButtonBox.add( l_removeButton );    

		// create a box that contains the name label (updated whenever the
		// name of the probe list is changed)
		Box l_nameBox = Box.createHorizontalBox();
		l_nameBox.setAlignmentX( Component.CENTER_ALIGNMENT );    
		l_nameBox.add( this.nameLabel );
		l_nameBox.add( Box.createHorizontalGlue() );    

		// create a box that contains the properties button
		///Box l_propertiesBox = Box.createHorizontalBox();
		//l_propertiesBox.setAlignmentX( Component.CENTER_ALIGNMENT );    
		//l_propertiesBox.add( l_propertiesButton );
		//l_propertiesBox.add( Box.createHorizontalGlue() );    

		// create a container for the statistics (probe list size) information
		Box l_vStatBox = Box.createVerticalBox();    
		l_vStatBox.add( this.sourceSizeLabel );
		l_vStatBox.add( this.targetSizeLabel );		
		Box l_statisticsBox = Box.createHorizontalBox();		
		l_statisticsBox.add( l_vStatBox );
		l_statisticsBox.add( Box.createHorizontalGlue() );

		// create a box that contains the basis combo box and the corresponding label
		JPanel l_comboBoxPanel = new JPanel();
		l_comboBoxPanel.add( l_baseProbeListLabel );
		l_comboBoxPanel.add( this.baseProbeListComboBox );
		Box l_comboBoxPanelBox = Box.createHorizontalBox();
		l_comboBoxPanelBox.add( l_comboBoxPanel );

		// add all (control) components that appear in the center of the dialog
		// to a single box
		Box l_centerBox = Box.createVerticalBox();
		l_centerBox.setBorder( BorderFactory.createEmptyBorder( 0, 10, 10, 10 ) );
		l_centerBox.add( l_nameBox );
		l_centerBox.add( Box.createVerticalStrut( MaydayDefaults.DEFAULT_VSPACE ) );
		//l_centerBox.add( l_propertiesBox );
		//l_centerBox.add( Box.createVerticalStrut( MaydayDefaults.DEFAULT_VSPACE ) );
		l_centerBox.add( l_statisticsBox );
		l_centerBox.add( Box.createVerticalStrut( MaydayDefaults.DEFAULT_VSPACE ) );
		l_centerBox.add( l_exchangeButtonBox );
		l_centerBox.add( Box.createVerticalStrut( MaydayDefaults.DEFAULT_VSPACE ) );
		l_centerBox.add( l_comboBoxPanelBox );    
		l_centerBox.add( Box.createVerticalGlue() );

		// create a box that represents the upper part of the dialog (OK and cancel button
		// are not contained)
		Box l_upperBox = Box.createHorizontalBox();
		l_upperBox.add( l_sourceProbeListViewScroller ); // left part
		l_upperBox.add( l_centerBox );
		l_upperBox.add( l_targetProbeListViewScroller ); // right part    		

		// create a box that contains the OK and the cancel button
		Box l_buttonPanel = Box.createHorizontalBox();
		l_buttonPanel.add( Box.createHorizontalGlue() ); // right-aligns the buttons
		l_buttonPanel.add( l_cancelButton );
		l_buttonPanel.add( new JLabel( " " ) ); // horizontal spacer
		l_buttonPanel.add( l_okButton );

		// make the OK button the default button for the dialog
		// (i. e. if the user presses the return key, the action associated
		// with the OK button will be invoked)		 
		getRootPane().setDefaultButton( l_okButton );

		// create a panel that contains all components previously grouped
		JPanel l_contentPane = new JPanel();
		l_contentPane.setLayout( new BoxLayout( l_contentPane, BoxLayout.Y_AXIS ) );
		l_contentPane.setBorder( MaydayDefaults.DIALOG_DEFAULT_BORDER );
		l_contentPane.add( l_upperBox );
		l_contentPane.add( Box.createVerticalStrut( MaydayDefaults.DEFAULT_VSPACE ) );
		l_contentPane.add( l_buttonPanel );   

		// add this panel to content pane of the dialog
		getContentPane().add( l_contentPane, BorderLayout.CENTER );

		// set the size of the dialog and make this the fixed size 
		setSize( getPreferredSize().width + 10, 500 );
		setResizable( false );
	}


	/**
	 * Returns the newly created or the manipulated probe list. This should
	 * be called after the user has terminated the manipulation process by
	 * clicking the OK button.
	 * 
	 * @return The newly created or the edited probe list.
	 */
	public ProbeList getEditedProbeList()
	{
		return ( this.baseProbeList );
	}

	/**
	 * Returns the newly created probe list or null if none was created. This should
	 * be called after the user has terminated the manipulation process by
	 * clicking the OK button.
	 * 
	 * @return The newly created probe list or null.
	 */
	public ProbeList getResultProbeList() {
		return result;
	}
	
	
	/**
	 * Updates the statistics information, this means the displayed size of the
	 * source and the target probe list.
	 */
	protected void updateStatistics()
	{
		sourceSizeLabel.setText( sourceProbeList.getNumberOfProbes() + 
				( sourceProbeList.getNumberOfProbes() == 1 ?
						" probe" : " probes" ) + " available" );

		targetSizeLabel.setText( targetProbeList.getNumberOfProbes() + 
				( targetProbeList.getNumberOfProbes() == 1 ?
						" probe" : " probes" ) + " in probe list" );
	}



	/**
	 * Handles the invokation of the probe list properties dialog when the
	 * user clicks the properties button of the dialog.
	 * 
	 * @author Nils Gehlenborg
	 * @version 0.1
	 */
	/*
  protected class EditPropertiesAction
  extends AbstractAction
  {

    public EditPropertiesAction( String text, Icon icon )
    {
      super( text, icon );
    }



    public EditPropertiesAction( String text )
    {
      super( text );
    }


    public EditPropertiesAction()
    {
      super( "Properties ..." );
    }


    public void actionPerformed( ActionEvent event )
    {
      AbstractPropertiesDialog l_dialog = 
    	  PropertiesDialogFactory.createDialog(targetProbeList);

      l_dialog.setVisible( true );


      // update the name label 
      nameLabel.setText( targetProbeList.getAnnotation().getName() );

      // update basis combo box
      baseProbeListComboBox.setEnabled( false );
      baseProbeListComboBox.setEnabled( true );																	
    }
  }
	 */


	/**
	 * Handles the invokation of probe exchanges between the source and the
	 * target probe list. This occurs when the user clicks the add or the 
	 * remove button when probe in either list are selected. 
	 * 
	 * @author Nils Gehlenborg
	 * @version 0.1
	 */
	protected class ExchangeProbeAction
	extends AbstractAction	
	{
		/**
		 * Indicates that probes should be added to the target probe list.
		 */
		public final static boolean ADD = true;

		/**
		 * Indicates that probes should be removed from the target probe list.
		 */
		public final static boolean REMOVE = false;
		private boolean mode;

		/**
		 * Defines the action depending on the <code>mode</code> flag.
		 * If the flag is set to <code>ADD</code>, the action is described
		 * by the string ">>", if the flag is set to <code>REMOVE</code>
		 * the description string is "<<". For these descriptions to make sense
		 * the corresponding buttons have to be placed, so that the description of
		 * the add button points to the target probe list and the description
		 * of the remove button points to the source probe list.
		 * 
		 * @param mode Either <code>ADD</code> or <code>REMOVE</code>. Indicates whether
		 *             probe should be added to the target probe list or removed from it.
		 */
		public ExchangeProbeAction( boolean mode )
		{
			super( ( mode == ADD ) ? ">>" : "<<" );

			this.mode = mode; 
		}		

		/**
		 * Invoked when the action occurs. If in <code>ADD</code> mode, the probes selected
		 * in the source probe list are added to the target probe list and removed from the
		 * source probe list. If in <code>REMOVE</code> mode, the probes selected in the
		 * target probe list are added to the source probe list and removed from the target
		 * probe list.
		 * Selections are not maintained.
		 * 
		 * @param The received event.
		 */
		@SuppressWarnings("unchecked")
		public void actionPerformed( ActionEvent event )
		{
			// determine source and target of the exchange
			JList l_tempSourceView;
			JList l_tempTargetView;
			ProbeList l_tempSource;
			ProbeList l_tempTarget;

			if ( mode == ADD )
			{
				l_tempSourceView = sourceProbeListView;
				l_tempTargetView = targetProbeListView;
				l_tempSource = sourceProbeList;
				l_tempTarget = targetProbeList;
			}
			else
			{
				l_tempSourceView = targetProbeListView;
				l_tempTargetView = sourceProbeListView;
				l_tempSource = targetProbeList;
				l_tempTarget = sourceProbeList;			
			}

			// get selected probes from source list
			Object[] l_selectedValues = l_tempSourceView.getSelectedValues();

			if ( l_selectedValues.length <= 0 )
			{
				// no selection, quit				
				return;
			}

			// move the probes from the source of the exchange to the target of the
			// exchange (note that the target exchange is not necessarily the target
			// probe list and vice versa)
			for ( int i = 0; i < l_selectedValues.length; ++i )
			{
				l_tempTarget.addProbe( (Probe)l_selectedValues[i] );
				l_tempSource.removeProbe( (Probe)l_selectedValues[i] );
			}

			// sort the probe lists
			java.util.List l_tempSourceUtilList = new ArrayList( l_tempSource.toCollection() );
			java.util.List l_tempTargetUtilList = new ArrayList( l_tempTarget.toCollection() );
			Collections.sort( l_tempSourceUtilList );
			Collections.sort( l_tempTargetUtilList );

			// display the probes
			l_tempTargetView.setListData( l_tempTargetUtilList.toArray() );
			l_tempSourceView.setListData( l_tempSourceUtilList.toArray() );

			// update basis combo box
			baseProbeListComboBox.setEnabled( false );
			baseProbeListComboBox.setEnabled( true );

			// select the currently edited probe list
			baseProbeListComboBox.setSelectedItem( targetProbeList );

			// update statistics
			updateStatistics();						
		}
	}



	/**
	 * Handles swap actions. A swap action exchanges the content of the 
	 * source probe list with that of the target probe list. 
	 * 
	 * @author Nils Gehlenborg
	 * @version 0.1
	 */
	protected class SwapAction
	extends AbstractAction	
	{
		/**
		 * Defines the action and uses "Swap" as description.
		 */
		public SwapAction()
		{
			super( "Swap" );
		}		


		/**
		 * Invoked when the action occurs. Swaps the content of the source
		 * and the target probe list. Selections are maintained.
		 * 
		 * @param event The received event.
		 */
		@SuppressWarnings("unchecked")
		public void actionPerformed( ActionEvent event )
		{			
			// save selected indices
			int[] l_originalSourceProbeListIndices = sourceProbeListView.getSelectedIndices();
			int[] l_originalTargetProbeListIndices = targetProbeListView.getSelectedIndices();

			Probe[] l_tempProbes = new Probe[targetProbeList.getNumberOfProbes()];
			Object[] l_targetProbes = targetProbeList.toCollection().toArray();

			// save probes contained in the target list
			for ( int i = 0; i < l_targetProbes.length; ++i )
			{
				l_tempProbes[i] = (Probe)l_targetProbes[i];
			}

			targetProbeList.clearProbes();		

			// copy probes contained in the source list
			Object[] l_sourceProbes = sourceProbeList.toCollection().toArray();

			for ( int i = 0; i < l_sourceProbes.length; ++i )
			{
				targetProbeList.addProbe( (Probe)l_sourceProbes[i] );
				sourceProbeList.removeProbe( (Probe)l_sourceProbes[i] );
			}

			sourceProbeList.clearProbes();

			// copy probes contained in the target list
			for ( int i = 0; i < l_targetProbes.length; ++i )
			{				
				sourceProbeList.addProbe( l_tempProbes[i] ); // no need to clone!
			}

			// sort the probe lists
			java.util.List l_tempSourceList = new ArrayList( sourceProbeList.toCollection() );
			java.util.List l_tempTargetList = new ArrayList( targetProbeList.toCollection() );
			Collections.sort( l_tempSourceList );
			Collections.sort( l_tempTargetList );

			// display the probes
			targetProbeListView.setListData( l_tempTargetList.toArray() );
			sourceProbeListView.setListData( l_tempSourceList.toArray() );

			// restore selected indices
			targetProbeListView.setSelectedIndices( l_originalSourceProbeListIndices );
			sourceProbeListView.setSelectedIndices( l_originalTargetProbeListIndices );

			// update basis combo box
			baseProbeListComboBox.setEnabled( false );
			baseProbeListComboBox.setEnabled( true );

			// select the currently edited probe list
			baseProbeListComboBox.setSelectedItem( targetProbeList );

			// update statistics
			updateStatistics();
		}
	}	



	/**
	 * Handles ListSelectionEvents that are fired whenever the selection
	 * in one of the two list boxes of the dialog is changed. 
	 * 
	 * @author Nils Gehlenborg
	 * @version 0.1
	 */
	protected class ProbeSelectionListener
	implements ListSelectionListener
	{
		/**
		 * Invoked when a selection is changed. Enables or disables the
		 * exchange actions. 
		 * 
		 * @param event The received event.
		 */
		public void valueChanged( ListSelectionEvent event )
		{
			addProbeAction.setEnabled( !sourceProbeListView.isSelectionEmpty() );
			removeProbeAction.setEnabled( !targetProbeListView.isSelectionEmpty() );
		}
	}



	/**
	 * Handles the change of the selected item in the basis
	 * combo box. If the basis probe list is changed, the content
	 * of the selected probe list is copied into the target probe list,
	 * which is cleared before the probes are copied.
	 * 
	 * @author Nils Gehlenborg
	 * @version 0.1
	 */
	protected class ProbeListSelectionListener
	implements ItemListener
	{
		/**
		 * Invoked when the selected item of the basis combo box
		 * is changed. Copies the content from the selected
		 * probe list to the cleared target probe list.
		 * 
		 * @param event The received event.
		 */
		@SuppressWarnings("unchecked")
		public void itemStateChanged( ItemEvent event )
		{
			if ( event.getStateChange() == ItemEvent.SELECTED )
			{
				// get new base probe list
				ProbeList l_baseProbeList = (ProbeList)event.getItem();

				// get all probes from the base probe list
				Object[] l_tempProbes = l_baseProbeList.toCollection().toArray();

				// remove all probes from the original target probe list
				targetProbeList.clearProbes();

				// copy probes from the base probe list to the target probe list
				for ( int i = 0; i < l_tempProbes.length; ++i )
				{
					targetProbeList.addProbe( (Probe)l_tempProbes[i] );
				}

				// determine content of the new source probe list
				sourceProbeList = new ProbeList( targetProbeList.getDataSet(), false );
				Object[] l_masterTableProbes = targetProbeList.getDataSet().getMasterTable().getProbes().values().toArray();

				for ( int i = 0; i < l_masterTableProbes.length; ++i )
				{
					if ( !targetProbeList.contains( (Probe)l_masterTableProbes[i] ) )
					{
						sourceProbeList.addProbe( (Probe)l_masterTableProbes[i] );
					}
				}				

				// sort both target and source probe list
				java.util.List l_tempSourceList = new ArrayList( sourceProbeList.toCollection() );
				java.util.List l_tempTargetList = new ArrayList( targetProbeList.toCollection() );
				Collections.sort( l_tempSourceList );
				Collections.sort( l_tempTargetList );

				// set the content of the target and the source probe list views 
				targetProbeListView.setListData( l_tempTargetList.toArray() );
				sourceProbeListView.setListData( l_tempSourceList.toArray() );

				// update basis combo box
				baseProbeListComboBox.setEnabled( false );
				baseProbeListComboBox.setEnabled( true );														

				// update statistics
				updateStatistics();
			}
		}
	}



	/**
	 * Handles the termination of the dialog when the user
	 * invokes the corresponding action (clicking the OK button or hitting the return
	 * key). The termination process includes the update of the manipulated
	 * probe list. 
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
		public void actionPerformed( ActionEvent event )
		{
			if ( baseProbeList == null )
			{
				baseProbeList = new ProbeList( targetProbeList.getDataSet(), true );

				// copy annotation and layout
				baseProbeList.setName(targetProbeList.getName());
				baseProbeList.setAnnotation( targetProbeList.getAnnotation() );
				baseProbeList.setColor( targetProbeList.getColor() );

				// silence the probe list
				baseProbeList.setSilent( true );

				// get the probes contained in the target probe list
				Object[] l_tempProbes = targetProbeList.toCollection().toArray();
				Probe[] l_probes = new Probe[l_tempProbes.length];

				// clear the probes from the target probe list
				targetProbeList.clearProbes();

				// add the probes to the target probe list and add the target probe list
				// to these probes
				for ( int i = 0; i < l_probes.length; ++i )
				{
					l_probes[i] = (Probe)l_tempProbes[i]; 
				}
				baseProbeList.setSilent( false );

				baseProbeList.setProbes( l_probes );

				result = baseProbeList;
			}
			else
			{        
				// copy annotation and layout
				baseProbeList.setAnnotation( targetProbeList.getAnnotation() );
				baseProbeList.setColor( targetProbeList.getColor() );

				// silence the probe list
				baseProbeList.setSilent( true );

				// clear the probes from the base probe list and release them
				baseProbeList.clearProbes();

				// add the probes from the target probe list to the base probe list and
				// add the base probe list to these probes
				// get the probes contained in the target probe list
				Object[] l_tempProbes = targetProbeList.toCollection().toArray();
				Probe[] l_probes = new Probe[l_tempProbes.length];

				// add the probes to the target probe list and add the target probe list
				// to these probes
				for ( int i = 0; i < l_probes.length; ++i )
				{
					l_probes[i] = (Probe)l_tempProbes[i]; 
				}
				baseProbeList.setSilent( false );

				baseProbeList.setProbes( l_probes );
			}			

			// close the dialog window
			dispose();
		}
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
			// close the dialog window      
			dispose();
		}
	}	
}