package mayday.vis3.graph.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import mayday.core.Probe;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;

@SuppressWarnings("serial")
public class ComponentZoomFrame extends JWindow implements MouseListener
{
	private CanvasComponent component;
	private ComponentRenderer renderer;
	private static final int MAGNIFICATION=4;

	public ComponentZoomFrame(CanvasComponent component, ComponentRenderer rend) 
	{
		super();
		this.component=component;
		renderer=rend;	
		//		Dimension size=new Dimension(component.getWidth()*MAGNIFICATION,component.getHeight()*MAGNIFICATION);
		//		size.width=size.width > 400?400:size.width;
		//		size.height=size.height > 300?300:size.height;
		//		setPreferredSize(size);
		setLayout(new BorderLayout(5,5));
		add(new ComponentZoomPanel(), BorderLayout.CENTER);

		add(new JLabel(component.getLabel()), BorderLayout.NORTH);

		if(component instanceof NodeComponent)
			init(((NodeComponent) component).getNode());

		pack();
		addMouseListener(this);
	}

	private void init(Node n)
	{
		add(new JLabel(component.getLabel()+" "+n.getRole()), BorderLayout.NORTH);

		Graph g=n.getGraph();
		
		double win=0;
		for(Edge e: g.getInEdges(n))
			win+=e.getWeight();
		
		JLabel inLabel=new JLabel("<html><center>&nbsp;&nbsp;in:<br><font size=+2>"+n.getInDegree()+"</font><br>" +
				"(" +NumberFormat.getNumberInstance().format(win)+")"+
				"</center></html>");
		add(inLabel,BorderLayout.WEST);
		
		
		double wout=0;
		for(Edge e: g.getOutEdges(n))
			wout+=e.getWeight();
		
		JLabel outLabel=new JLabel("<html><center>out:&nbsp;&nbsp;<br><font size=+2>"+n.getOutDegree()+"</font><br>" +
				"(" +NumberFormat.getNumberInstance().format(wout)+")"+
				"</center></html>");
		add(outLabel,BorderLayout.EAST);

		String probesLabel=new String();
		if(n instanceof MultiProbeNode)
		{
			probesLabel=new String("<html>Contains "+((MultiProbeNode)n).getProbes().size()+" Probes:"); 
			if(((MultiProbeNode)n).getProbes().size() < 50)
			{
				int pc=0;
				for(Probe p: ((MultiProbeNode) n).getProbes())
				{				
					probesLabel=probesLabel+p.getDisplayName()+",";
					if(pc%6==0)
						probesLabel=probesLabel+"<br>";
					++pc;
				}
			}
			probesLabel=probesLabel+"</html>";
		}
		add(new JLabel(probesLabel), BorderLayout.SOUTH);
	}

	public void mouseClicked(MouseEvent e) 
	{
		dispose();		
	}

	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

	private class ComponentZoomPanel extends JPanel
	{
		public ComponentZoomPanel() 
		{
			Dimension size=new Dimension(component.getWidth()*MAGNIFICATION,component.getHeight()*MAGNIFICATION);
			size.width=size.width > 400?400:size.width;
			size.height=size.height > 300?300:size.height;
			setPreferredSize(size);
		}

		/* (non-Javadoc)
		 * @see java.awt.Container#paint(java.awt.Graphics)
		 */
		@Override
		public void paint(Graphics g1)
		{
			Graphics2D g=(Graphics2D)g1;
			Rectangle b=new Rectangle(0,0,getWidth(),getHeight());
			if(component instanceof MultiProbeComponent)
			{
				if(((MultiProbeComponent) component).getProbes().isEmpty())
				{
					renderer.draw(g, ((MultiProbeComponent)component).getNode(), b, ((NodeComponent)component).getNode(), component.getLabel(), component.isSelected());
					return;
				}
				renderer.draw(g, ((MultiProbeComponent)component).getNode(), b, ((MultiProbeComponent) component).getProbes(), component.getLabel(), component.isSelected());
				return;
			}
			if(component instanceof NodeComponent)
			{
				renderer.draw(g, ((NodeComponent)component).getNode(), b, ((NodeComponent)component).getNode(), component.getLabel(), component.isSelected());
				return;
			}	
			DefaultComponentRenderer.getDefaultRenderer().draw(g, null, b, null, component.getLabel(), component.isSelected());
		}
	}
}
