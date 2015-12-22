package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.components.NodeComponent.NodeUpdate;

@SuppressWarnings("serial")
public class RoleAction extends AbstractAction
{
	private String role; 
	private NodeComponent component;

	public RoleAction(String role, NodeComponent component) 
	{
		super(role);
		this.role=role;
		this.component=component;
	}

	public void actionPerformed(ActionEvent e) 
	{
		component.getNode().setRole(role);
		component.nodeUpdated(NodeUpdate.ROLE);
		component.repaint();
	}

	public String getRole() {
		return role;
	}
	
}
