package mayday.dynamicpl.gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import mayday.dynamicpl.ProbeFilter;
import mayday.dynamicpl.RuleSet;

/* This TreeModel is a wrapper around the RuleSet/Rule-Tree. Adding nodes into the model
 * updates the corresponding ruleset as well.
 */

@SuppressWarnings("serial")
public class RuleTreeModel extends DefaultTreeModel  {

	protected RuleSet ruleSet;

	public RuleTreeModel( RuleSet rules ) {
		super(new DefaultMutableTreeNode(rules)); 		
		// create nodes. each node corresponds to a ruleset, each leaf to a filteringrule
		ruleSet = rules;
		addNodes(ruleSet, (MutableTreeNode)this.root);	
	}

	protected void addNodes(RuleSet rs, MutableTreeNode tn) {
		int i=0;
		for (ProbeFilter pf : rs.getSubRules()) {
			MutableTreeNode next = new DefaultMutableTreeNode(pf);
			tn.insert(next, i++);
			if (pf instanceof RuleSet)
				addNodes((RuleSet)pf, next);			
		}
	}

	public void insertNodeInto(javax.swing.tree.MutableTreeNode newNode, javax.swing.tree.MutableTreeNode parentNode, int arg2) {
		// make change known to RuleSet
		Object newRuleset = ((DefaultMutableTreeNode)newNode).getUserObject();
		Object parentRuleset = ((DefaultMutableTreeNode)parentNode).getUserObject();
		if (parentRuleset instanceof RuleSet) {
			RuleSet parent = (RuleSet)parentRuleset;
			ProbeFilter child = (ProbeFilter)newRuleset;
			parent.addSubRule(child);
			super.insertNodeInto(newNode, parentNode, 0);
			this.nodeChanged(newNode);			
		} else System.err.println("New nodes can only be added to RuleSets!");		
	}

	public void removeNodeFromParent(javax.swing.tree.MutableTreeNode arg0) {
		// make change known to RuleSet
		Object userObject1 = ((DefaultMutableTreeNode)arg0.getParent()).getUserObject();
		Object userObject2 = ((DefaultMutableTreeNode)arg0).getUserObject();
		((RuleSet)userObject1).removeSubRule((ProbeFilter)userObject2);
		super.removeNodeFromParent(arg0);
	}

}
