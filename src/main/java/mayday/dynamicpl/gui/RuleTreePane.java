package mayday.dynamicpl.gui;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import mayday.dynamicpl.RuleSet;

@SuppressWarnings("serial")
public class RuleTreePane extends JScrollPane implements ChangeListener, TreeSelectionListener {

	private RuleSet ruleSet;
	private RuleTreeModel ruleTreeModel;
	private JTree tree;
	
	public RuleTreePane( RuleSet rules ) {
		ruleSet = rules;
		ruleSet.addChangeListener(this);
		ruleTreeModel = new RuleTreeModel(ruleSet);
		tree = new JTree(ruleTreeModel);
		tree.setCellRenderer(new RuleNodeRenderer());
		tree.addTreeSelectionListener(this);
		setViewportView(tree);
	}

	public RuleTreeModel getRuleTreeModel() {
		return ruleTreeModel;
	}
	
	public JTree getRuleTree() {
		return tree;
	}
	
	public void removeNotify() {
		ruleSet.removeChangeListener(this);
		super.removeNotify();
	}

	public void stateChanged(ChangeEvent arg0) {
		tree.repaint();
	}

	public void valueChanged(TreeSelectionEvent e) {
		try {
			ruleTreeModel.nodeChanged((TreeNode)e.getNewLeadSelectionPath().getLastPathComponent());
			ruleTreeModel.nodeChanged((TreeNode)e.getOldLeadSelectionPath().getLastPathComponent());
		} catch (Exception ex) {
			// never mind.
		}
	}
	
}
