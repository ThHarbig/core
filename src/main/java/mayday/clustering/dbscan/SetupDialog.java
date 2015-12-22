/**
 *  File SetupDialog.java 
 *  Created on 05.07.2005
 *  As part of the package clustering.kmeans.ui
 *  By Janko Dietzsch
 *  
 */

package mayday.clustering.dbscan;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import mayday.core.MaydayDefaults;
import mayday.core.gui.MaydayDialog;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;


/**
 * @author Janko Dietzsch
 * @version 0.1
 */
@SuppressWarnings("serial")
public class SetupDialog extends MaydayDialog {
    
   
    // Input fields for the setup panel 
    private JTextField MinPts;
    private JComboBox distanceMeasure;
    private JTextField clusterIdentifierPrefix;
    
    private DBScanSettings settings;
    
    public SetupDialog() {
        this(null);
    }
    
    public SetupDialog(DBScanSettings settings) {
        super();
        
        this.setModal(true);
        this.setTitle("DBScan Algorithm");
      
        if (settings == null) 
        	this.settings = new DBScanSettings(); // default settings defined by class KmeansSettings 
        else this.settings = settings;
        
        this.init();
    }
    
    protected JPanel initSetupPanel() {
        JPanel jp = new JPanel();
        jp.setName("Settings");
        
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
                
        // Set all necessary numerical features for the k-Means-Algorithm
        this.MinPts = new JTextField(6);
        this.MinPts.setText(""+this.settings.getMinPts());
   
        Box MinPtsLabelBox = Box.createHorizontalBox();
        MinPtsLabelBox.add(new JLabel("Minimal number of points:"));
        MinPtsLabelBox.add(Box.createHorizontalGlue());
        
        Box MinPtsBox = Box.createVerticalBox();
        MinPtsBox.add(MinPtsLabelBox);
        MinPtsBox.add(this.MinPts);
          
        Box clusterAlgorithmDefineBox = Box.createHorizontalBox();
        clusterAlgorithmDefineBox.add(MinPtsBox);
        //TitledBorder tb = new TitledBorder("Features of the k-Means Algorithm");
        //tb.setTitleFont(new Font("Courier",Font.PLAIN,17));
        //mapDefineBox.setBorder(tb);
        clusterAlgorithmDefineBox.setBorder(new TitledBorder("Features of the DBScan Algorithm"));
        
        jp.add(Box.createVerticalStrut(5));
        jp.add(clusterAlgorithmDefineBox);
        
              
        // Set the used distance measure
        this.distanceMeasure = new JComboBox(DistanceMeasureManager.values().toArray());
        this.distanceMeasure.setSelectedItem(this.settings.getDistanceMeasure());
        this.distanceMeasure.setEditable(false);
        this.distanceMeasure.setMaximumRowCount(4);
        
        Box  distanceMeasureLabelBox= Box.createHorizontalBox();
        distanceMeasureLabelBox.add(new JLabel("Distance measure:"));
        distanceMeasureLabelBox.add(Box.createHorizontalGlue());
        
        Box distanceMeasureBox = Box.createVerticalBox();
        distanceMeasureBox.add(distanceMeasureLabelBox);
        distanceMeasureBox.add(this.distanceMeasure);
        
        // put the both together on the panel 
        Box distInitBox = Box.createHorizontalBox();
        distInitBox.add(distanceMeasureBox);
        
        jp.add(Box.createVerticalStrut(20));
        jp.add(distInitBox);
        
        // Cluster identifier prefix
        this.clusterIdentifierPrefix = new JTextField(20);
        this.clusterIdentifierPrefix.setText(this.settings.getClusterIdentifierPrefix());
        
        Box identPrefixLabelBox = Box.createHorizontalBox();
        identPrefixLabelBox.add(new JLabel("Cluster identifier prefix:"));
        identPrefixLabelBox.add(Box.createHorizontalGlue());
        
        Box identPrefixBox = Box.createVerticalBox();
        identPrefixBox.add(identPrefixLabelBox);
        identPrefixBox.add(this.clusterIdentifierPrefix);
        
        // put the both together on the panel 
        Box cyclesIdentBox = Box.createHorizontalBox();
        cyclesIdentBox.add(identPrefixBox);
        
        jp.add(Box.createVerticalStrut(20));
        jp.add(cyclesIdentBox); 
         
        return jp;
    }
    
    protected void init() {
        // Create OK- and Cancel-button and set the according mnemonics
        JButton runButton = new JButton(new RunAction());
        runButton.setToolTipText("Starts the DBScan algorithm with the given parameters");
        JButton cancelButton = new JButton(new CancelAction());
        cancelButton.setToolTipText("Abort the dialog");
        runButton.setMnemonic(KeyEvent.VK_ENTER);
        cancelButton.setMnemonic(KeyEvent.VK_ESCAPE);
      
        // Put OK- and Cancel-button on an own box
        Box buttonBox = Box.createHorizontalBox();    
        buttonBox.add(Box.createHorizontalGlue()); // right-aligns the buttons
        buttonBox.add(cancelButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(runButton);
        //buttonBox.add(new JLabel(" ")); // horizontal spacer
      
        // OK-button as default
        this.getRootPane().setDefaultButton(runButton);
      
        // Top content pane
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(MaydayDefaults.DIALOG_DEFAULT_BORDER);    
        contentPane.add(this.initSetupPanel());
        contentPane.add(Box.createVerticalStrut(MaydayDefaults.DEFAULT_VSPACE));
        contentPane.add(buttonBox);
          
        // Lay the top content pane on the content pane of the dialog
        getContentPane().add(contentPane, BorderLayout.CENTER);
      
        // Set the size of the dialog and make this the fixed size 
        setSize(getContentPane().getPreferredSize());
        setResizable(false);
        pack();    

       
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
    
    
    /**
     * This method is used to get settings. 
     *
     * @return Returns the settings.
     */
    public DBScanSettings getSettings() {
        return settings;
    }
    

    /**
     * This method is used to set the settings 
     *
     * @param settings The settings to set.
     */
    public void setSettings(DBScanSettings settings) {
        this.settings = settings;
    }
    
  
    public static void main(String s[]) {      
        SetupDialog dialog = new SetupDialog(null);
        //dialog.pack();
        //dialog.setTitle("Batch-SOM-Algorithm");
        //dialog.setSize(200,150);
        dialog.setVisible(true);
        if (dialog.getSettings()!=null) System.out.println(dialog.getSettings().toString());
    }
    

    /**
     * This class reads all settings and starts the clustering procedure 
     */
	protected class RunAction extends AbstractAction {
        
        /**
         * Defines an action named "Run".
         */
        public RunAction() {
            super( "Run" );
        }


        /**
         * Invoked when the action occurs.
         * 
         * @param event The received action event.
         */
        public void actionPerformed(ActionEvent event) {
            
            int temp_int;
            
            // Get the MinPts
            try {
                temp_int = (new Integer(MinPts.getText())).intValue();
                if (temp_int > 1) 
                    settings.setMinPts(temp_int);
                else
                    throw (new RuntimeException("MinPts must be an integer greater than 1."));
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,"The minimal number of points must be an integer greater than 1.",
                                              // Adaption to Mayday:
                                              MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
                    
            // Get the prefix for the cluster identifier
            try {
                String temp_str = clusterIdentifierPrefix.getText();
                if (!temp_str.equals(new String(""))) 
                    settings.setClusterIdentifierPrefix(temp_str);
                else 
                    throw (new RuntimeException("The prefix of the cluster identifier must be not empty."));
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,"The prefix of the cluster identifier must be not empty.",
                                              // Adaption to Mayday:
                                              MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get the type of the used distance measure
            settings.setDistanceMeasure((DistanceMeasurePlugin) distanceMeasure.getSelectedItem());
            
            dispose();
        }
        
    }

    
    /**
     * This class terminats the dialog and cancels the action
     */
	protected class CancelAction extends AbstractAction {
  
        /**
         * Defines an action named "Cancel".
         */
        public CancelAction() {
            super("Cancel");
        }


        /**
         * Invoked when the action occurs.
         * 
         * @param event The received action event.
         */
        public void actionPerformed(ActionEvent event) {
            
            // this propagates the cancellation to the plugin
            settings = null;

            // close the dialog window
            dispose();
        }
    }

}