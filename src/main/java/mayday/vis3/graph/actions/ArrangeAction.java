package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.SwingConstants;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class ArrangeAction extends AbstractAction
{
	private int direction;
	private int anchor;
	private GraphCanvas canvas;
	
	public ArrangeAction(int anchor, int direction, GraphCanvas canvas) {
		this.anchor=anchor;
		this.direction = direction;
		this.canvas = canvas;		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(direction==SwingConstants.HORIZONTAL)
		{
			arrangeHorizontal();
		}
		if(direction==SwingConstants.VERTICAL)
		{
			arrangeVertical();
		}
		canvas.updatePlot();
		
	}
	
	private void arrangeHorizontal()
	{
		// get components
		List<CanvasComponent> comps=canvas.getSelectionModel().getSelectedComponents();
		int y=0;
		// getAvg mid Point
		if(anchor==SwingConstants.CENTER)
		{
			for(CanvasComponent cc: comps)
			{
				y+=(cc.getY()+cc.getHeight()/2);
			}
			y/=comps.size();
		}
		if(anchor==SwingConstants.LEADING)
		{
			int minX=Integer.MAX_VALUE; 
			for(CanvasComponent cc: comps)
			{
				if(cc.getX() < minX)
				{
					y=(cc.getY()+cc.getHeight()/2);
					minX=cc.getX();
				}
			}
		}
		if(anchor==SwingConstants.TRAILING)
		{
			int maxX=Integer.MIN_VALUE; 
			for(CanvasComponent cc: comps)
			{
				if(cc.getX() > maxX)
				{
					y=(cc.getY()+cc.getHeight()/2);
					maxX=cc.getX();
				}
				
			}
		}
		// place all comp at y-cc.getHeight/2; 
		for(CanvasComponent cc: comps)
		{
			cc.setLocation(cc.getX(), y-cc.getHeight()/2);
		}
		
	}
	
	private void arrangeVertical()
	{
		// get components
		List<CanvasComponent> comps=canvas.getSelectionModel().getSelectedComponents();
		int x=0;
		// getAvg mid Point
		if(anchor==SwingConstants.CENTER)
		{
			for(CanvasComponent cc: comps)
			{
				x+=(cc.getX()+cc.getWidth()/2);
			}
			x/=comps.size();
		}
		if(anchor==SwingConstants.LEADING)
		{
			int minY=Integer.MAX_VALUE; 
			for(CanvasComponent cc: comps)
			{
				if(cc.getY() < minY)
				{
					x=(cc.getX()+cc.getWidth()/2);
					minY=cc.getY();
				}
			}
		}
		if(anchor==SwingConstants.TRAILING)
		{
			int maxY=Integer.MIN_VALUE; 
			for(CanvasComponent cc: comps)
			{
				if(cc.getX() > maxY)
				{
					x=(cc.getX()+cc.getWidth()/2);
					maxY=cc.getY();
				}
				
			}
		}
		// place all comp at y-cc.getHeight/2; 
		for(CanvasComponent cc: comps)
		{
			cc.setLocation(x-cc.getWidth()/2,cc.getY());
		}
	}
}
