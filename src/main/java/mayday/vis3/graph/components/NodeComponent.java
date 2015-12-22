package mayday.vis3.graph.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.structures.graph.Node;
import mayday.vis3.graph.components.LabelRenderer.Orientation;

@SuppressWarnings("serial")
public class NodeComponent extends CanvasComponent
{
	private Node node;
	
	/**
	 * @param node
	 */
	public NodeComponent(Node node)
	{
		super();
		this.node=node;
		setLabel(node.getName());	
		setToolTipText(getLabel());
	}
	
	/**
	 * @return the node
	 */
	public Node getNode() 
	{
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) 
	{
		this.node = node;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g1)
	{
		Graphics2D g=(Graphics2D)g1;
		renderer.draw(g, getNode(), new Rectangle(getSize()), node,  labelComponent==null?getLabel():"", isSelected());
	}
	
	public void resetSize()
	{
		super.resetSize();
		setSize(renderer.getSuggestedSize(node,getPayload()));
		repaint();
	}
	
	@Override
	public boolean hasLabel() 
	{
		return getRenderer().hasLabel(getNode(),getPayload());
	}
	
	@Override
	public Orientation getLabelOrientation() 
	{
		return getRenderer().getLabelOrientation(getNode(),getPayload());
	}
	
	@Override
	public void setLabel(String label) 
	{
		super.setLabel(label);
		setToolTipText(label);
		getNode().setName(label);
	}
	
	@Override
	public String getLabel() 
	{
		return node.getName();
	}
	
	public void nodeUpdated(NodeUpdate cause) {
		// ready for subclasses.
	}
	
	public enum NodeUpdate {
		NAME, ROLE, EDGES
	}
	
}
