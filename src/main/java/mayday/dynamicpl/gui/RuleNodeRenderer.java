package mayday.dynamicpl.gui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import mayday.dynamicpl.Rule;
import mayday.dynamicpl.RuleSet;

@SuppressWarnings("serial")
public class RuleNodeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

	public Component getTreeCellRendererComponent(JTree arg0, Object arg1,
			boolean arg2, boolean arg3, boolean arg4, int arg5, boolean arg6) {

		// use parent class to set background etc.
		super.getTreeCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5, arg6);

		if (!(arg1 instanceof DefaultMutableTreeNode))
			return this;

		String nodeText;
		
		arg1 = ((DefaultMutableTreeNode)arg1).getUserObject();		
		
		// Leaf nodes
		if (arg1 instanceof Rule) {

			Rule fr = (Rule)arg1;
			nodeText = "<html>"+fr.toString();

		} else 
			// Internal nodes
			if (arg1 instanceof RuleSet) {

				RuleSet rs = (RuleSet)arg1;
				nodeText=rs.toHTMLString();			
			}
		else {
			nodeText = arg1.toString();
		}

		this.setText(nodeText);

		return this;
	}

	
	
}
