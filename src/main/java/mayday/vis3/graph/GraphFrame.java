package mayday.vis3.graph;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import mayday.core.gui.MaydayFrame;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.maps.DefaultValueMap;
import mayday.vis3.graph.arrows.ArrowSettings;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.model.GraphModel;

@SuppressWarnings("serial")
public class GraphFrame extends MaydayFrame
{
	private GraphCanvas canvas;
	
	public GraphFrame(Graph g) 
	{
		canvas=new GraphCanvas(new DefaultGraphModel(g));
		init();
	}
	
	public GraphFrame(GraphModel g) 
	{
		canvas=new GraphCanvas(g);
		init();
	}
	
	private void init()
	{
		setLayout(new BorderLayout());
		add(new JScrollPane(canvas));
		canvas.setSize(800, 600);
		pack();
	}
	
	public void setLayouter(CanvasLayouter layouter)
	{
		canvas.setLayouter(layouter);
	}
	
	/**
	 * @return the arrowSettings
	 */
	public DefaultValueMap<Edge, ArrowSettings> getArrowSettings() 
	{
		return canvas.getArrowSettings();
	}

	/**
	 * @param arrowSettings the arrowSettings to set
	 */
	public void setArrowSettings(DefaultValueMap<Edge, ArrowSettings> arrowSettings) {
		canvas.setArrowSettings(arrowSettings);
	}
	
	@Override
	public void setVisible(boolean vis) 
	{
		canvas.updateLayout();
		super.setVisible(vis);
	}

	
}
