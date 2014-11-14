package mayday.core.settings.generic;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.TopMostSettable;

public class HierarchicalSettingComponent_Tree implements SettingComponent {

	protected HierarchicalSetting mySetting;
	protected boolean topMost = false;
    private DefaultMutableTreeNode root; 
    private JTree tree;
    protected JSplitPane pnl;
    protected Dimension minDim = new Dimension();
    
	List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();
	HashMap<Setting, SettingComponent> settingComponentsMap = new HashMap<Setting, SettingComponent>();

	public HierarchicalSettingComponent_Tree(HierarchicalSetting s, boolean TopMost) {
		mySetting = s;
		topMost = TopMost;
	}
	
	@SuppressWarnings("unchecked")
	public JComponent getEditorComponent() {
		if (tree==null) {
	    	root = new DefaultMutableTreeNode(mySetting.getName());
	    	tree = new JTree(root);
			JScrollPane TreeScrollPane = new JScrollPane();
			TreeScrollPane.setBorder(new EmptyBorder(0,0,0,0));
			TreeScrollPane.setViewportView(tree);
			TreeScrollPane.setMinimumSize(null);
			
			pnl  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, TreeScrollPane, new JPanel());

			for (Setting s : mySetting.getChildren()) {
				if (s instanceof TopMostSettable)
					((TopMostSettable) s).setTopMost(true);
				SettingComponent sc = s.getGUIElement();
				settingComponents.add(sc);
				settingComponentsMap.put(s,sc);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(s);
				root.add(node);			
			}
	    	
			for (SettingComponent sc : settingComponents) {
				Dimension d = sc.getEditorComponent().getPreferredSize();
				minDim.width = Math.max(minDim.width, d.width);
				minDim.height = Math.max(minDim.height, d.height);
			}
			
	        tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent arg0) {
					pnl.setRightComponent(new JPanel());
					Object selectedNode = tree.getLastSelectedPathComponent();
					if (selectedNode!=null) {
						Object selected = ((DefaultMutableTreeNode)selectedNode).getUserObject();						
						if (selected instanceof Setting)  {
							SettingComponent sc = settingComponentsMap.get(selected);
							if (sc!=null) {
								Component comp = sc.getEditorComponent();
								JScrollPane jsp = new JScrollPane(comp);
								pnl.setRightComponent(jsp);
								comp.setPreferredSize(minDim);
								comp.invalidate();
								comp.repaint();									
							}
						}
						tree.expandPath(tree.getSelectionPath());
					}
				}
			});	        
	        
	        if (root.getChildCount()>0)
	        	tree.getSelectionModel().setSelectionPath(new TreePath(((DefaultMutableTreeNode)root.getChildAt(0)).getPath()));	    	

			setTopMost(topMost);
		}
		return pnl;
	}
	
	public boolean updateSettingFromEditor(boolean failSilently) {
		if (tree!=null) {
			Map<Setting, SettingComponent> editors = new HashMap<Setting, SettingComponent>();
			for (SettingComponent sc : settingComponents) {
				editors.put(sc.getCorrespondingSetting(), sc);
			}
			return mySetting.updateChildrenFromEditors(editors, failSilently);
		}
		return true;
	}

	public void setTopMost(boolean TopMost) {
		topMost = TopMost;
		if (tree==null)
			return;
	}

	public Setting getCorrespondingSetting() {
		return mySetting;
	}

	
}
