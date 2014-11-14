package mayday.core.pluma.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Vector;
import java.util.prefs.BackingStoreException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import mayday.core.gui.MaydayFrame;
import mayday.core.gui.PreferencePane;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

@SuppressWarnings("serial")
public class PluginManagerView extends MaydayFrame {

	private MainPanel mp;
	
	public PluginManagerView(boolean hierarchical, boolean configurable) {
		mp = new MainPanel();
		mp.hierarchicalView.setSelected(hierarchical);
		//mp.configurableOnly.setSelected(configurable);
		mp.initPluginList();
		this.getContentPane().add(mp);
		this.setBounds(50, 50,1000,800);
		this.setTitle("Mayday Plugins");
		this.setVisible(true);
	}
	
	public PluginManagerView() {
		this(true,false);
	}
	
	private class MainPanel extends JPanel {
		protected JPanel CenterPanel;
		//private JPanel BottomPanel;
		//protected JButton PrefButton;
		//private JPanel ButtonPanel;
		private JScrollPane jScrollPane1;
		private JPanel PluginInfoPanel;
		private JTree PluginTree;
		private JEditorPane PluginInfoLabel;
		private DefaultMutableTreeNode PluginTreeRoot;
		private PreferencePane selectedPluginPrefs;
		private JCheckBox hierarchicalView;
		//private JCheckBox configurableOnly;
		private JTabbedPane TabbedCenterPanel;

		public MainPanel() {
			super(new BorderLayout());
			//CENTER PANEL
			CenterPanel = new JPanel();
			add(CenterPanel, BorderLayout.CENTER);
			initCenterPanel();			
			
		
		}
		
		
		protected void initCenterPanel() {
			{
				GridBagLayout CenterPanel1Layout = new GridBagLayout();
				CenterPanel1Layout.columnWidths = new int[] {7, 7, 7};
				CenterPanel1Layout.rowHeights = new int[] {7, 7, 7, 7};
				CenterPanel1Layout.columnWeights = new double[] {0.1, 0.1, 0.8};
				CenterPanel1Layout.rowWeights = new double[] {0.9, 0.2, 0.1, 0.0};
				CenterPanel.setLayout(CenterPanel1Layout);
				{
					PluginTreeRoot = new DefaultMutableTreeNode("Mayday Plugins");
					PluginTree = new JTree(PluginTreeRoot);
					JScrollPane TreeScrollPane = new JScrollPane();
					TreeScrollPane.setBorder(new EmptyBorder(0,0,0,0));
					TreeScrollPane.setViewportView(PluginTree);
					JPanel TreeAndCheckBox = new JPanel(new BorderLayout());
					TreeAndCheckBox.add(TreeScrollPane,BorderLayout.CENTER);
					
					hierarchicalView = new JCheckBox("Hierarchical View");
					hierarchicalView.setSelected(true);
					hierarchicalView.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							initPluginList();
						}
					});
					/*configurableOnly = new JCheckBox("Only configurable");
					configurableOnly.setSelected(false);
					configurableOnly.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							initPluginList();
						}
					})*/;
					
					JPanel checkboxes = new JPanel();
					checkboxes.add(hierarchicalView);
					//checkboxes.add(configurableOnly);
										
					TreeAndCheckBox.add(checkboxes, BorderLayout.NORTH);
						
					JButton RescanButton = new JButton();					
					RescanButton.setText("Scan again");
					RescanButton.setEnabled(true);
					RescanButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							new Thread() { 
								public void run() {
									PluginManager.getInstance().init();
									initPluginList();
									ModuleSelectionChanged(null);
								}
							}.start();
						}
					});
					TreeAndCheckBox.add(RescanButton, BorderLayout.SOUTH);
									
					PluginTree.addTreeSelectionListener(new TreeSelectionListener() {
						public void valueChanged(TreeSelectionEvent arg0) {
							Object selectedNode = PluginTree.getLastSelectedPathComponent();
							if (selectedNode!=null) {
								Object selected = ((DefaultMutableTreeNode)selectedNode).getUserObject();
								if (selected instanceof PluginInfo)  {
									ModuleSelectionChanged((PluginInfo)selected);
								} else {
									ModuleSelectionChanged(null);
								}
								PluginTree.expandPath(PluginTree.getSelectionPath());
							}
							else ModuleSelectionChanged(null);
						}
					});
					//initPluginList();

					CenterPanel.add(TreeAndCheckBox, new GridBagConstraints(0, 0, 2, 3, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));

				}
				{
					PluginInfoPanel = new JPanel();
					BorderLayout PluginInfoPanelLayout = new BorderLayout();
					PluginInfoPanel.setLayout(PluginInfoPanelLayout);
					
					TabbedCenterPanel = new JTabbedPane();
					TabbedCenterPanel.add("Plugin Details", PluginInfoPanel);					
					
					CenterPanel.add(TabbedCenterPanel, new GridBagConstraints(2, 0, 1, 3, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
					//PluginInfoPanel.setBorder(BorderFactory.createTitledBorder(null, "Plugin Information", TitledBorder.LEADING, TitledBorder.TOP));
					{
						jScrollPane1 = new JScrollPane();
						PluginInfoPanel.add(jScrollPane1, BorderLayout.CENTER);
						jScrollPane1.setBorder(new EmptyBorder(0,0,0,0));
						{
							PluginInfoLabel = new JEditorPane();
							jScrollPane1.setViewportView(PluginInfoLabel);
							PluginInfoLabel.setOpaque(false);
							PluginInfoLabel.setEditable(false);
							PluginInfoLabel.setContentType("text/html");
							PluginInfoLabel.setFont(UIManager.getFont(this)); // TextArea font should fit other GUI fonts
							PluginInfoLabel.setText("No plugin selected");
						}
					}
					//PluginInfoPanel.add(BottomPanel, BorderLayout.SOUTH);
				}
			}
		}
		
	
		private boolean showPlugin(PluginInfo pli) {
			return true;
			/*
			return (!configurableOnly.isSelected() || 
					(pli.getInstance()!=null && pli.getInstance().getPreferencesPanel()!=null)
					);*/
		}
		
		@SuppressWarnings("unchecked")
		private synchronized void initPluginList() {
			
			PluginTreeRoot.removeAllChildren();
			
			subTree st = new subTree(PluginTreeRoot);			
			
			if (hierarchicalView.isSelected()) {
				for (String masterComponent: PluginManager.getInstance().getMasterComponents()) {
					String mcName = masterComponent.equals("") ? "Unassigned" : masterComponent;
					for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(masterComponent)) {
						if (!showPlugin(pli)) 
							continue;
						Vector<String> plpath = (Vector<String>)(pli.getProperties().get(Constants.CATEGORIES));
						if (plpath==null || !hierarchicalView.isSelected())
							st.addPath(mcName, pli);
						else 
							for (String subcat : plpath) 
								st.addPath(mcName+"/"+subcat, pli);					
					}
				}
			} else {
				TreeMap<String, PluginInfo> orderedPlugins = new TreeMap<String, PluginInfo>(); 
				for (String masterComponent: PluginManager.getInstance().getMasterComponents()) {
					for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(masterComponent)) {
						if (showPlugin(pli)) 
							orderedPlugins.put(pli.getName().toUpperCase(),pli);
					}				
				}
				for (PluginInfo pli : orderedPlugins.values())
					st.addPath("",pli);
			}

			st.finalize();
			
			DefaultMutableTreeNode category = new DefaultMutableTreeNode("BROKEN Plugins");

			for (PluginInfo pli : PluginManager.getInstance().getBrokenPlugins()) {
				category.add(new PluginInfoTreeNode(pli));
			}
			if (category.children().hasMoreElements())
				PluginTreeRoot.add(category);
				
			if (!PluginTreeRoot.children().hasMoreElements()) 
				PluginTreeRoot.add(new DefaultMutableTreeNode("No plugins found!"));
			
			((DefaultTreeModel)PluginTree.getModel()).nodeStructureChanged(PluginTreeRoot);
			PluginTree.expandRow(0);
		}
		

		
		@SuppressWarnings("unchecked")
		public void ModuleSelectionChanged(PluginInfo pli) {
			
			// save previously edited prefs
			if (selectedPluginPrefs!=null)
				try {
					selectedPluginPrefs.writePreferences();
				} catch (BackingStoreException e) {
					e.printStackTrace();
				}
			
			// remove pref tabs if present
			while (TabbedCenterPanel.getComponentCount()>1)
				TabbedCenterPanel.remove(1);
			
			if (pli!=null) {
				AbstractPlugin apl = (AbstractPlugin)(pli.getProperties().get(Constants.CLASS_INSTANCE));
				if (apl!=null) {
					selectedPluginPrefs = apl.getPreferencesPanel();
				}
				//PrefButton.setEnabled(selectedPluginPrefs!=null);
				
				if (selectedPluginPrefs!=null) {
					TabbedCenterPanel.add("Plugin Settings",selectedPluginPrefs);
				}

				StringBuilder text = new StringBuilder();
				text.append(
						"<HTML><h2>"+pli.getName()+" ["+pli.getIdentifier()+"]</h2><hr>"+
						"<table border=0>"+
						"<tr><td><strong>Author: </strong></td><td>"+pli.getAuthor()+"</td></tr>"+
						"<tr><td><strong>eMail: </strong></td><td>"+pli.getEmail()+"</td></tr>"+
						"<tr><td><strong>Plugin Class: </strong></td><td>"+pli.getPluginClass().getCanonicalName()+"</td></tr>"+
						"<tr><td><strong>Dependencies: </strong></td>"
						);				
				
				HashSet<String> missingdep = new HashSet<String>();
				if (pli.getProperties().containsKey("PluginManager-MissingDependencies")) {
					for(String mis : (Vector<String>)(pli.getProperties().get("PluginManager-MissingDependencies"))) {
						missingdep.add(mis);
					}
				}
				
				String nextLine="<td>";
				for (String dep : pli.getDependencies()) {
					text.append(nextLine);
					if (missingdep.contains(dep)) 
						text.append("<strong><font color='#ff0000'>Missing: "+dep+"</font></strong>");
					else
						text.append(dep);
					text.append("</td></tr>");
					nextLine = "<tr><td></td><td>";
				}
				if (nextLine.equals("<td>")) // no deps needed
					text.append("<td>(none)</td>");
				text.append("</table><hr>");
				
				text.append(pli.getAbout());
					
				PluginInfoLabel.setText(text.toString());
				PluginInfoLabel.setCaretPosition(0);
			} else {
				PluginInfoLabel.setText("No plugin selected");
				//PrefButton.setEnabled(false);
			}
		}
		

		
		
		
	}
		
	private static class PluginInfoTreeNode extends DefaultMutableTreeNode {
		
		public PluginInfoTreeNode(PluginInfo pli) {
			userObject = pli;
		}
		
		public String toString() {
			return ((PluginInfo)this.userObject).getName();
		}
	}
	
	
	public static class subTree extends TreeMap<String, subTree> {
		
		DefaultMutableTreeNode root;
		
		public subTree(String rootname) {
			root = new DefaultMutableTreeNode(rootname);
		}
		
		public subTree(DefaultMutableTreeNode rootnode) {
			root = rootnode;
		}
		
		public void addPath(String path, PluginInfo pli) {
			if (path.equals(""))
				root.add(new PluginInfoTreeNode(pli));
			else {
				String subPath = "";
				if (path.contains("/")) {
					subPath = path.substring(path.indexOf("/")+1);
					path = path.substring(0,path.indexOf("/"));
				}
				subTree child = get(path);
				if (child==null) {
					child = new subTree(path);
					put(path,child);
				}
				get(path).addPath(subPath, pli);
			}
		}
		
		public void finalize() {
			for(String childname : this.keySet()) {
				get(childname).finalize();
				root.add(get(childname).root);
			}
		}
		
	}
	

		

	
}
