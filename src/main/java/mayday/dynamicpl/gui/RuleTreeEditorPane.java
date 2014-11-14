package mayday.dynamicpl.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import mayday.dynamicpl.Rule;
import mayday.dynamicpl.RuleSet;

@SuppressWarnings("serial")
public class RuleTreeEditorPane extends JPanel {

	protected RuleSet ruleSet;
	protected RuleTreeModel ruleTreeModel;
	protected JTree ruleTree;
	
	public RuleTreeEditorPane(RuleSet rs) {
		ruleSet = rs;
		setLayout(new BorderLayout());
		RuleTreePane rtp = new RuleTreePane(rs);
		ruleTreeModel = rtp.getRuleTreeModel();
		ruleTree = rtp.getRuleTree();
		
        ruleTree.setDragEnabled(true);
        ruleTree.setTransferHandler(new RuleTreeTransferHandler(this));

		
		add(rtp, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(2,2));
		buttons.add(new JButton(new AddRuleAction()));
		buttons.add(new JButton(new RemoveAction()));
		buttons.add(new JButton(new AddRuleSetAction()));
		
		add(buttons, BorderLayout.SOUTH);
		ruleTree.getSelectionModel().setSelectionPath(new TreePath(ruleTreeModel.getRoot()));
	}
	
	public void addTreeSelectionListener(TreeSelectionListener arg0) {
		ruleTree.getSelectionModel().addTreeSelectionListener(arg0);
	}
	
	protected MutableTreeNode getSelectedNode() {
		return (MutableTreeNode)ruleTree.getLastSelectedPathComponent();
	}
	
	public void insertNode(MutableTreeNode newN,  MutableTreeNode oldN, int index) {
		ruleTreeModel.insertNodeInto(newN, oldN, index);
		// expand tree path
		TreePath tp = new TreePath(ruleTreeModel.getPathToRoot(newN));
		ruleTree.expandPath(tp);
		ruleTree.getSelectionModel().setSelectionPath(tp);	
	}
	
	public void removeNode(MutableTreeNode node) {
		TreeNode parent = node.getParent();
		ruleTreeModel.removeNodeFromParent(node);
		// select the parent
		if (parent!=null)
			ruleTree.getSelectionModel().setSelectionPath(new TreePath(ruleTreeModel.getPathToRoot(parent)));
	}
	
	public RuleTreeModel getRuleTreeModel() {
		return ruleTreeModel;
	}

	public class AddRuleSetAction extends AbstractAction {

		public AddRuleSetAction() {
			super("Add Rule Set");
			setEnabled(false);
			ruleTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode tn = ((DefaultMutableTreeNode)getSelectedNode());
					if (tn!=null)
						AddRuleSetAction.this.setEnabled(tn!=null && (tn.getUserObject() instanceof RuleSet));
				}
			});
		}
		
		public void actionPerformed(ActionEvent arg0) {
			MutableTreeNode parent = getSelectedNode();
			if (parent!=null)
				insertNode(
						new DefaultMutableTreeNode(new RuleSet(ruleSet.getDynamicProbeList())),
						parent,
						parent.getChildCount());
		}
	}
	
	public class AddRuleAction extends AbstractAction {

		public AddRuleAction() {
			super("Add Rule");
			setEnabled(false);
			ruleTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode tn = ((DefaultMutableTreeNode)getSelectedNode());
					AddRuleAction.this.setEnabled(tn!=null && (tn.getUserObject() instanceof RuleSet));
				}
			});
		}
		
		public void actionPerformed(ActionEvent arg0) {
			MutableTreeNode parent = getSelectedNode();
			if (parent!=null)
				insertNode(
						new DefaultMutableTreeNode(new Rule(ruleSet.getDynamicProbeList())),
						parent,
						parent.getChildCount());
		}
	}
	
	public class RemoveAction extends AbstractAction {

		public RemoveAction() {
			super("Remove");
			setEnabled(false);
			ruleTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode tn = ((DefaultMutableTreeNode)getSelectedNode());
					RemoveAction.this.setEnabled(tn!=null && tn.getParent()!=null);
				}
			});
		}
		
		public void actionPerformed(ActionEvent arg0) {
			MutableTreeNode node = getSelectedNode();
			if (node!=null && node.getParent()!=null)
				removeNode(node);
		}
	}

	
}
