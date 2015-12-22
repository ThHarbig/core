package mayday.vis3.graph.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;

@SuppressWarnings("serial")
public class GotoAction extends AbstractAction
{
	public static final int HOME=0;
	public static final int END=3;

	private int mode;

	private GraphCanvas canvas;

	public GotoAction(GraphCanvas canvas, int mode) 
	{
		super("Reset View");
		this.canvas=canvas;
		this.mode=mode;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		switch(mode)
		{
		case HOME:
			canvas.centerSilent(new Rectangle(0,0,1,1), true);
			break;
		case END:
			canvas.centerSilent(new Rectangle(0,canvas.getHeight()-2,1,1), true);
			break;
		}

	}
}
