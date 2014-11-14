package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class Move extends AbstractAction {

	private int by;
	private int direction;
	private GraphCanvas canvas;
	
	public static final int UP=0;
	public static final int DOWN=1;
	public static final int LEFT=2;
	public static final int RIGHT=3;
	
	
	
	public Move(int by, int direction, GraphCanvas canvas) {
		this.by = by;
		this.direction = direction;
		this.canvas = canvas;
	}



	@Override
	public void actionPerformed(ActionEvent e) 
	{
		for(CanvasComponent cc: canvas.getSelectionModel().getSelectedComponents())
		{
			if(cc.isLocked())
				continue;
			if(direction==UP)
			{
				cc.setLocation(cc.getX(), cc.getY()-by);
			}
			if(direction==DOWN)
			{
				cc.setLocation(cc.getX(), cc.getY()+by);
			}
			if(direction==LEFT)
			{
				cc.setLocation(cc.getX()-by, cc.getY());
			}
			if(direction==RIGHT)
			{
				cc.setLocation(cc.getX()+by, cc.getY());
			}
		}
		canvas.updatePlot();

	}

}
