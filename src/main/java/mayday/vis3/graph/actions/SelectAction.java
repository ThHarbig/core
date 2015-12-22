package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.DepthFirstIterator;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

@SuppressWarnings("serial")
public class SelectAction extends AbstractAction
{
	public static final int NEIGHBORS=0;
	public static final int IN_NEIGHBORS=1;
	public static final int OUT_NEIGHBORS=2;
	public static final int ALL_REACHABLE=3;
		
	private int mode;
	private GraphModel model;
	private CanvasComponent component;
	private GraphCanvas canvas;
	
	public SelectAction(GraphCanvas canvas,GraphModel model, CanvasComponent comp, int m) 
	{
		super("Select");
		this.mode=m;
		this.component=comp;
		this.model=model;	
		this.canvas=canvas;
		
		switch(m)
		{
			case 0: putValue(NAME, "Select Neighbors"); break;
			case 1: putValue(NAME, "Select Incoming Neighbors"); break;
			case 2: putValue(NAME, "Select Outgoing Neighbors"); break;
			case 3: putValue(NAME, "Select all reachable Nodes"); break;
		}
		
	}	
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		canvas.getSelectionModel().clearSelection();
		select(model,component,mode);		
	}
	
	public static List<CanvasComponent> select(GraphModel model,CanvasComponent comp, int m)
	{
		switch(m)
		{
			case 0: return selectNeighbors(model, comp);
			case 1: return selectInNeighbors(model, comp);
			case 2: return selectOutNeighbors(model, comp);
			case 3: return selectAllReachable(model, comp);
		}
		return new ArrayList<CanvasComponent>();
	}
	
	public static List<CanvasComponent> selectNeighbors(GraphModel model,CanvasComponent comp)
	{
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		comp.toggleSelection();
		res.add(comp);
		for(Node n:model.getGraph().getNeighbors(model.getNode(comp)))
		{
			CanvasComponent nc=model.getComponent(n);
			nc.toggleSelection();
			res.add(nc);
		}
		return res;
	}
	
	public static List<CanvasComponent> selectInNeighbors(GraphModel model,CanvasComponent comp)
	{
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		comp.toggleSelection();
		res.add(comp);
		for(Node n:model.getGraph().getInNeighbors(model.getNode(comp)))
		{
			CanvasComponent nc=model.getComponent(n);
			nc.toggleSelection();
			res.add(nc);
		}
		return res;
	}
	
	public static List<CanvasComponent> selectOutNeighbors(GraphModel model,CanvasComponent comp)
	{
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		comp.toggleSelection();
		res.add(comp);
		for(Node n:model.getGraph().getOutNeighbors(model.getNode(comp)))
		{
			CanvasComponent nc=model.getComponent(n);
			nc.toggleSelection();
			res.add(nc);
		}
		return res;
	}
	
	public static List<CanvasComponent> selectAllReachable(GraphModel model,CanvasComponent comp)
	{
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		DepthFirstIterator dfi=new DepthFirstIterator(model.getGraph(), model.getNode(comp));
		dfi.setSingleComponent(true);
		while(dfi.hasNext())
		{
			Node n=dfi.next();
			CanvasComponent nc=model.getComponent(n);
			
			nc.toggleSelection();
			res.add(nc);
		}
		return res;
	}
		
}
