package mayday.core.gui.dataset;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayDialog;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.probelistmanager.ProbeListManager;

@SuppressWarnings("serial")
public class DataSetOverview extends MaydayDialog
{
    private DataSet dataSet;    
    
    private JTabbedPane tabbedPane;
    private JComboBox actionBox;
    
    //private JComboBox dataModeCombo;

    
    private String[] actions ={
            "Do nothing",
            "Show a Boxplot of the \"Global\" Probe list", 
            "Show a heatmap of the \"Global\" Probe list",
            "Cluster  \"Global\" Probe list using k-Means",
            "Cluster  \"Global\" Probe list using Self organizing maps",
            "Cluster  \"Global\" Probe list hierarchically",
            "Run the Mayday Processing Framework",
            //"Run the R Interpreter"
            };
    
    private String[] identifier ={
            "dummy", //Dummy!
            "PAS.visualization.boxplot",
            "PAS.visualization.expressionimage", 
            "PAS.clustering.kmeans",
            "PAS.clustering.batchsom",
            "PAS.clustering.hierarchical",
            "PAS.mpf",
            //"mayday.interpreter.rinterpreter.RPlugin"
            };
    
    public DataSetOverview(DataSet dataSet)
    {
        this.dataSet = dataSet;
        setTitle("Sucessfully opened Dataset");        
        init();
        
    }
    
    
    
    
    protected void init()
    {
        // initialize tabbed pane
        this.tabbedPane = new JTabbedPane();
        
        // add tabs to tabbed pane
        this.tabbedPane.add( initBasicPanel() );
        
        
        // initialize the ok and the cancel button and set mnemonics
        //JButton runButton = new JButton( new RunAction() );    
        JButton okButton = new JButton( new RunAction() );       
        //runButton.setMnemonic( KeyEvent.VK_ENTER );
        //okButton.setMnemonic( KeyEvent.VK_ENTER );
        
        
        // create a panel to hold the OK and the cancel button
        Box buttonPanel = Box.createHorizontalBox();    
        buttonPanel.add( Box.createHorizontalGlue() ); // right-aligns the buttons
        //buttonPanel.add( runButton );
        buttonPanel.add( new JLabel( " " ) ); // horizontal spacer
        buttonPanel.add( okButton );
        
        
        // make the OK button the default button (invoked when the user hits
        // the return key) of the dialog
        getRootPane().setDefaultButton( okButton );
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
        contentPane.setBorder( MaydayDefaults.DIALOG_DEFAULT_BORDER );    
        contentPane.add( this.tabbedPane );
        contentPane.add( Box.createVerticalStrut(MaydayDefaults.DEFAULT_VSPACE ) );
        contentPane.add( buttonPanel );
        
        // add the panel to the content pane of the dialog
        getContentPane().add( contentPane, BorderLayout.CENTER );
        
        // set the size of the dialog and make this the fixed size 
        setSize( getContentPane().getPreferredSize() );
        setResizable( false );
        pack();    
        
        
        // 2008-04-23 fb: Sometimes, Enter does not activate the "OK" button, fix it here
        final ActionListener listener = new ActionListener() {
            public final void actionPerformed(final ActionEvent e) {
            	new RunAction().actionPerformed(null);
            }
        };
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true);
        getRootPane().registerKeyboardAction(listener, keyStroke,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        
        
        
    }
    
    protected class RunAction extends AbstractAction  
    {
        /**
         * Defines an action named "OK".
         */
        public RunAction()
        {
            super( "Ok" );
        }
        
        
        /**
         * Invoked when the action occurs.
         * 
         * @param event The received action event.
         */
        @SuppressWarnings("unchecked")
		public void actionPerformed( final ActionEvent event ) 
        {            
            dispose();
            
            //apply DataMode
//            dataSet.getMasterTable().setDataMode(
//            		DataMode.getModeByID(DataMode.UNDETERMINED)
//                (DataMode)dataModeCombo.getSelectedItem()    
//            );
            
            int selectedAction=actionBox.getSelectedIndex();
            
            // 060813:fb: find correct action! This was not working properly, because not all actions were shown in the
            // combobox and thus indices could be wrong (e.g. selecting MPF in my installation gives selectedAction=3 but
            // should give selectedAction=6!. This is due to the fact that all "Cluster..." entries are not 
            // visible on my installation.)
            for (int i=0; i!=actions.length; ++i)
            	if (((String)actionBox.getSelectedItem()).equals(actions[i])) {
            		selectedAction = i; 
            		break;
            	}
            // end of addition -fb
                                    
        	final LinkedList<ProbeList> pl = new LinkedList<ProbeList>();
                    	
    		if (!dataSet.getProbeListManager().contains("global"))
    			pl.add(dataSet.getMasterTable().createGlobalProbeList(false));
    		else
    			pl.addAll(dataSet.getProbeListManager().getObjects());

            if(selectedAction ==0) 
                return;

            
            //run the selected action
            final PluginInfo pli = PluginManager.getInstance().getPluginFromID(identifier[selectedAction]);
            if (pli!=null) {
            	final ProbelistPlugin plp = (ProbelistPlugin)pli.getInstance();
            	final List<ProbeList> result = plp.run(pl, dataSet.getMasterTable());
            	insertIntoProbeListManager(dataSet.getProbeListManager(), result);
            }
            
        }
    }
    

    public void insertIntoProbeListManager(ProbeListManager probeListManager, List<ProbeList> results)
    {
        for ( int i = 0; i < results.size(); ++i )
        {
            boolean l_hasName = true;
            
            while ( probeListManager.contains( (ProbeList)results.get( i ) ) )
            {
                String l_message = MaydayDefaults.Messages.PROBE_LIST_NOT_UNIQUE;
                l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT,
                    ((ProbeList)results.get( i )).getName() );
                l_message += "\n" + MaydayDefaults.Messages.ENTER_NEW_NAME;
                
                String l_name = (String)JOptionPane.showInputDialog( null,
                    l_message,
                    MaydayDefaults.Messages.WARNING_TITLE,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    null,
                    ((ProbeList)results.get( i )).getName() );
                
                // quit if the user pressed cancel
                if ( l_name == null )
                {
                    l_hasName = false;
                    ((ProbeList)results.get( i )).clearProbes();
                    
                    break;
                }
                
                ((ProbeList)results.get( i )).setName( l_name );                                                   
            }
            
            if ( l_hasName ) // the user entered a valid name and clicked ok 
            {
                probeListManager.addObjectAtTop( (ProbeList)(results.get( i ) ) );
                
            }
        }
    }
    
    protected JPanel initBasicPanel()
    {
        JPanel thePanel = new JPanel(); 
        thePanel.setName( "DataSet" );
        
        Box headBox=Box.createVerticalBox();
        
        JTextField nameField=new JTextField(30);
        nameField.setEditable(false);
        nameField.setText(dataSet.getName());
        headBox.setBorder(BorderFactory.createTitledBorder("Data Set"));
        headBox.add(produceLabelBox("Sucessfully opened Dataset"));
        headBox.add(nameField);
        headBox.add(Box.createVerticalStrut(5));
        /*  wird nicht mehr verwendet, wurde noch nie verwendet (issue 18) --fb
        headBox.add(produceLabelBox("Data mode"));
        dataModeCombo = new JComboBox(DataMode.getAvailableDataModi());
        dataModeCombo.setSelectedItem(
            dataSet.getMasterTable().getDataMode().compareTo(DataMode.getModeByID(DataMode.UNDETERMINED))==0?
                    estimateDataMode() : 
                    dataSet.getMasterTable().getDataMode()
        );
        headBox.add(dataModeCombo);*/
        
        
        
        //extract MasterTable
        MasterTable masterTable=dataSet.getMasterTable();
        
        if (masterTable.hasMissingValues())
        {
            
            headBox.add(produceLabelBox("<html><b>Attention: This data set contains missing values!</b></html>"));

        }
        
        
        Box statsBox = Box.createHorizontalBox();
        
        statsBox.setBorder(BorderFactory.createTitledBorder("Properties"));
        
        Box statsLeftBox=Box.createVerticalBox();
        Box statsRightBox=Box.createVerticalBox();
        
        
        // basic stats:
        statsLeftBox.add(new JLabel("Number of Probes"));
        statsLeftBox.add(new JLabel("Number of Experiments"));
        statsLeftBox.add(new JLabel("Number of Probe Lists"));
        
        statsRightBox.add(new JLabel(""+masterTable.getNumberOfProbes()));
        statsRightBox.add(new JLabel(""+masterTable.getNumberOfExperiments()));
        statsRightBox.add(new JLabel(""+dataSet.getProbeListManager().getNumberOfObjects()));
        
        
        statsBox.add(statsLeftBox);
        statsBox.add(Box.createHorizontalStrut(10));
        statsBox.add(statsRightBox); 
        statsBox.add(Box.createHorizontalGlue());
        
        //meta information:
        
        MIGroupSelection<MIType> mios=masterTable.getDataSet().getMIManager().getGroups();
        
        DefaultListModel mioModel= new DefaultListModel();
        
        for(MIGroup s:mios)
        {
            mioModel.addElement(s);
        }
        
        JList mioList=new JList(mioModel);
        if(mioModel.isEmpty())
        {
            mioModel.addElement("(no meta information available)");
        }
        mioList.setVisibleRowCount(3);
        mioList.setFixedCellWidth(200);
        
        Box mioBox=Box.createVerticalBox();
        
        mioBox.setBorder(BorderFactory.createTitledBorder("Meta Information"));
        mioBox.add(new JScrollPane(mioList));
        
        //first steps:
        
        
        Box wizardBox=Box.createHorizontalBox();
        wizardBox.setBorder(BorderFactory.createTitledBorder("First steps"));
        
        
        DefaultComboBoxModel actionModel=new DefaultComboBoxModel();
        for(int i=0; i!= actions.length; ++i)
        {
            if(PluginManager.getInstance().getPluginFromID(identifier[i])!=null || i==0)
                actionModel.addElement(actions[i]);
        }
        actionBox=new JComboBox(actionModel);
        
        wizardBox.add(actionBox);
        
        

        
        //finish layout;
        
        Box allBox = Box.createVerticalBox();
        allBox.add(headBox);
        allBox.add(statsBox);
        allBox.add(mioBox);
        allBox.add(wizardBox);
        
        thePanel.add(allBox);
        
        
        
        return thePanel;
        
    }
    
    /**
     * @param label
     * @return
     * Produce a horizontal box with a left-aligned label of text label.
     */
    public static Box produceLabelBox(String label)
    {
        Box theBox = Box.createHorizontalBox();
        theBox.add(new JLabel(label));
        theBox.add(Box.createHorizontalGlue());
        
        return theBox;
    }
    
    
    public void setVisible(boolean visible) {
    	if (visible) {
    		if (MaydayDefaults.Prefs.showOverviewDialog.getStringValue().equals("no")) {
    			System.out.println("DatasetOverviewDialog was suppressed by user preferences.");
    			dispose();
    			return;
    		} 
    		
    		// Decide which kind of dialog to show        	
    		boolean oldstyle = MaydayDefaults.Prefs.showOverviewDialog.getStringValue().equals("old style");
    		if (!oldstyle) {
    			PropertiesDialogFactory.createDialog(this.dataSet).setVisible(true);
    			dispose();
    			return;
    		} 
    	}
    		
		super.setVisible(visible);
    }
       
}
