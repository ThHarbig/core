package mayday.core.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingsPreferencePane;

@SuppressWarnings("serial")
public class PreferencesDialog extends MaydayDialog {
	
	private ArrayList<PreferencePane> allprefpanes = new ArrayList<PreferencePane>();
	private DefaultMutableTreeNode PluginPrefsNode;
	
    /**
     * Constructs the dialog and makes it modal.
     *  
     * @throws HeadlessException 
     */
    public PreferencesDialog()
    throws HeadlessException
    {
        super();        
        setTitle( "Preferences" );        
        setModal( true );        
        init();
    }
    
    private DefaultMutableTreeNode TreeRoot; 
    private JTree PrefTree;
    private JTabbedPane prefPane = new JTabbedPane();
    
    protected void buildTree() {
    	TreeRoot.removeAllChildren();
    	
    	PreferencePane pp;

    	pp = new SettingsPreferencePane(MaydayDefaults.Prefs.AppearanceSettings);
    	TreeRoot.add(new DefaultMutableTreeNode(pp));
    	allprefpanes.add(pp);

    	pp = new SettingsPreferencePane(MaydayDefaults.Prefs.EditorSettings);
    	TreeRoot.add(new DefaultMutableTreeNode(pp));
    	allprefpanes.add(pp);
    	
    	pp = new SettingsPreferencePane(MaydayDefaults.Prefs.InternetSettings);
    	TreeRoot.add(new DefaultMutableTreeNode(pp));
    	allprefpanes.add(pp);
    	
    	pp = new PluginPreferencesPane();
    	PluginPrefsNode = new DefaultMutableTreeNode(pp);
    	TreeRoot.add(PluginPrefsNode);
    	allprefpanes.add(pp);    	
		
		TreeMap<String, TreeMap<String, PreferencePane>> orderedPlugins = new TreeMap<String, TreeMap<String, PreferencePane>>(); 
		for (String masterComponent: PluginManager.getInstance().getMasterComponents()) {
			TreeMap<String, PreferencePane> tm = new TreeMap<String, PreferencePane>();
			for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(masterComponent)) {
				try {
					if (pli.getInstance()!=null && pli.getInstance().getPreferencesPanel()!=null) {					
						PreferencePane c = pli.getInstance().getPreferencesPanel();
						c.setName(pli.getName());
						tm.put(pli.getName().toUpperCase(),c);
					}
				} catch (Exception e) {
					System.out.println("Could not get preference object for "+pli.getIdentifier());
					System.out.println(e+" "+e.getMessage());
					e.printStackTrace();
				}
			}
			if (tm.size()>0)
				orderedPlugins.put(masterComponent, tm);
		}
		
		// add core plugins furst
    	int CoreIndex = TreeRoot.getChildCount();

		DefaultMutableTreeNode mcNode = new DefaultMutableTreeNode(Constants.MC_CORE);
		PluginPrefsNode.add(mcNode);
		for (PreferencePane pli : orderedPlugins.get(Constants.MC_CORE).values()) {
			mcNode.add(new DefaultMutableTreeNode(pli));	    	
			allprefpanes.add(pli);
		}
		
		for (String mc : orderedPlugins.keySet()) {
			if (!mc.equals(Constants.MC_CORE)) {
				mcNode = new DefaultMutableTreeNode(mc);
				PluginPrefsNode.add(mcNode);
				for (PreferencePane pli : orderedPlugins.get(mc).values()) {
					mcNode.add(new DefaultMutableTreeNode(pli));	    	
					allprefpanes.add(pli);
				}
			}
		}		
	 
		int maxRowExpand = CoreIndex+1; //Math.min(CoreIndex+1,PrefTree.getRowCount()-1);
			
		for (int i = 0; i <= maxRowExpand; i++) {			
			PrefTree.expandRow(i);
		}
		
    }
    
    public void selectPluginPrefPane() {
    	PrefTree.getSelectionModel().setSelectionPath(new TreePath(PluginPrefsNode.getPath()));
    }
    
    /**
     * Initializes the content pane of the dialog and its bounds.
     */
    protected void init() 
    {
    	TreeRoot = new DefaultMutableTreeNode("Preferences");
    	PrefTree = new JTree(TreeRoot);
		JScrollPane TreeScrollPane = new JScrollPane();
		TreeScrollPane.setBorder(new EmptyBorder(0,0,0,0));
		TreeScrollPane.setViewportView(PrefTree);
		TreeScrollPane.setMinimumSize(new Dimension(200,200));
		
    	JSplitPane jsli  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, TreeScrollPane, prefPane);
    	
    	getContentPane().add(jsli, BorderLayout.CENTER);
    	
        // initialize the ok and the cancel button and set mnemonics
        JButton l_okButton = new JButton( new OkAction() );    
        JButton l_cancelButton = new JButton( new CancelAction() );       
        l_okButton.setMnemonic( KeyEvent.VK_ENTER );
        l_cancelButton.setMnemonic( KeyEvent.VK_ESCAPE );
        
        
        // create a panel to hold the OK and the cancel button
        Box l_buttonPanel = Box.createHorizontalBox();		
        l_buttonPanel.add( Box.createHorizontalGlue() ); // right-aligns the buttons
        l_buttonPanel.add( l_cancelButton );
        l_buttonPanel.add( new JLabel( " " ) ); // horizontal spacer
        l_buttonPanel.add( l_okButton );
        
        // make the OK button the default button (invoked when the user hits
        // the return key) of the dialog
        getRootPane().setDefaultButton( l_okButton );
        
        getContentPane().add(l_buttonPanel, BorderLayout.SOUTH);
    	
        buildTree();
        
        PrefTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				prefPane.removeAll();
				Object selectedNode = PrefTree.getLastSelectedPathComponent();
				if (selectedNode!=null) {
					Object selected = ((DefaultMutableTreeNode)selectedNode).getUserObject();					
					if (selected instanceof Component)  {
						JScrollPane jsp = new JScrollPane((Component)selected);
						jsp.setName(((Component)selected).getName());
						prefPane.add(jsp);
						((Component)selected).invalidate();
						((Component)selected).repaint();
					}
					PrefTree.expandPath(PrefTree.getSelectionPath());
				}
			}
		});
        
        
		PrefTree.getSelectionModel().setSelectionPath(new TreePath(((DefaultMutableTreeNode)TreeRoot.getChildAt(0)).getPath()));
        
        // set the size of the dialog and make this the fixed size 
        setSize( getContentPane().getPreferredSize() );
        pack();

    }
    
    
    
    /**
     * This class handles the termination of the dialog when the user
     * invokes the corresponding action (clicking the OK button or hitting the return
     * key). The termination process includes the update of the preferences file.
     *   
     * @author Nils Gehlenborg
     * @version 0.1
     */
    protected class OkAction extends AbstractAction {
    	
        public OkAction() {
            super( "OK" );
        }

        public void actionPerformed( ActionEvent event ) {
        	for (PreferencePane pp : allprefpanes) {
        		try {
        			pp.writePreferences();
        		} catch (BackingStoreException ex){
        			JOptionPane.showMessageDialog( null,
        					ex.getMessage() + "\n\n" +
        					"No preferences were saved for "+pp.toString(),
        					MaydayDefaults.Messages.ERROR_TITLE,
        					JOptionPane.ERROR_MESSAGE ); 
        		}
        	}
        	MaydayDefaults.Prefs.save();        	
        	dispose();
        }
    }
    
    
   protected class CancelAction extends AbstractAction {
        public CancelAction() {
            super( "Cancel" );
        }

        public void actionPerformed( ActionEvent event ) {
            // close the dialog window
            dispose();
        }
    }
    
   public class PluginPreferencesPane extends PreferencePane {
        private JTextField pathField;
        private JButton browseButton;
        
        public PluginPreferencesPane() {
            super();
            this.setName("Plugin Path");
            init(); 
        }

        protected void init() {            
            
            // initialize the input fields, set width and height 
            this.pathField = new JTextField( "", 40 );
            
            // initialize labels that describe the input fields
            JLabel l_pathLabel = new JLabel( "Plug-in Directory" + 
                ( MaydayDefaults.isWebstartApplication() ? 
                        " (additional directory for webstart)" :
                            ""                  
                ) );
            
            // intitialize buttons
            this.browseButton = new JButton( new BrowseAction() );
            
            // set the content of the input fields
            String l_path="";
            try {
            	l_path = MaydayDefaults.Prefs.getPluginDirectory();
            } catch(Exception exception) {
                JOptionPane.showMessageDialog( null,
                		exception.toString(), MaydayDefaults.Messages.ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE );
            }
                        
            this.pathField.setText( l_path );
            
            // create a box that holds the path input field and the browse button
            Box l_pathBrowseBox = Box.createHorizontalBox();
            JPanel l_pathBrowsePanel = new JPanel(); // prevents resizing
            l_pathBrowsePanel.setLayout( new BoxLayout( l_pathBrowsePanel, BoxLayout.Y_AXIS ) );            
            l_pathBrowseBox.add( Box.createHorizontalGlue() );
            l_pathBrowseBox.add( this.browseButton );
            l_pathBrowsePanel.add( l_pathBrowseBox );       
            
            // create a box that holds the path input field and the corresponding label
            Box l_pathBox = Box.createHorizontalBox();
            JPanel l_pathPanel = new JPanel(); // prevents resizing
            l_pathPanel.setLayout( new BoxLayout( l_pathPanel, BoxLayout.Y_AXIS ) );            
            l_pathBox.add( l_pathLabel );
            l_pathBox.add( Box.createHorizontalGlue() );
            l_pathPanel.add( l_pathBox );       
            l_pathPanel.add( this.pathField );    
            l_pathPanel.add( l_pathBrowsePanel );    
            
            // create a panel to hold the boxes with the previously grouped components
            JPanel l_contentPane = new JPanel();
            l_contentPane.setLayout( new BoxLayout( l_contentPane, BoxLayout.Y_AXIS ) );
            l_contentPane.setBorder( MaydayDefaults.DIALOG_DEFAULT_BORDER );    
            l_contentPane.add( l_pathPanel );
            l_contentPane.add( Box.createVerticalStrut( MaydayDefaults.DEFAULT_VSPACE ) );
            l_contentPane.setVisible( true );
            
            add( l_contentPane );
            
            // set the size of the dialog and make this the fixed size 
            setSize( l_contentPane.getPreferredSize() );
            
            // set the name of this tab
            setName( MaydayDefaults.Prefs.NODE_PLUGINS );     
        }
        
        
        public void writePreferences() throws BackingStoreException {
            String l_path="";
            try {
            	l_path = MaydayDefaults.Prefs.getPluginDirectory();
            } catch(Exception exception) {
            }
          
            //MZ 2006-06-25
            MaydayDefaults.Prefs.storePluginDirectory(new File(pathField.getText()));
            if (!pathField.getText().equals(l_path)) {
            	new Thread() {
            		public void run() {
            			PluginManager.getInstance().init();
            		}
            	}.start();
            	
            }            
        }
        
        
        protected class BrowseAction extends AbstractAction  
        {
            public BrowseAction() {
                super( "Browse ..." );
            }            
            
            public BrowseAction( String text ) {
                super( text );
            }
                        
            public void actionPerformed( ActionEvent event ) {     
                JFileChooser l_chooser;
                l_chooser = new JFileChooser();
                l_chooser.setFileSelectionMode(  JFileChooser.DIRECTORIES_ONLY );
                int l_option = l_chooser.showOpenDialog( (Component)event.getSource() );
                if ( l_option  == JFileChooser.APPROVE_OPTION ) {
                    String l_fileName = l_chooser.getSelectedFile().getAbsolutePath();
                    // if the user presses cancel, then quit
                    if ( l_fileName == null ) {
                        return;
                    }
                    pathField.setText( l_fileName );
                }
            }
        }
    }
      
}
