package mayday.vis3.graph.menus;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.layout.GridLayouter;
import mayday.vis3.graph.layout.RandomLayout;

@SuppressWarnings("serial")
public class LayouterMenu extends JMenu
{
	private GraphCanvas graphCanvas;
	public LayouterMenu(GraphCanvas graphCanvas)
	{
		super("Layout Graph");
		this.graphCanvas=graphCanvas;
		init();
	}
	
	private void init()
	{
		add(new LayouterAction(new GridLayouter(),"Grid Layout"));
		add(new LayouterAction(new RandomLayout(),"Random Layout"));
		add(new LayouterAction(new FruchtermanReingoldLayout(),"Fruchterman-Reingold Layout"));
	}
	
	public void add(CanvasLayouter layouter, String title)
	{
		add(new LayouterAction(layouter,title));
	}
	
	private class LayouterAction extends AbstractAction
	{
		private CanvasLayouter layouter;

		public LayouterAction(CanvasLayouter layouter,String title)
		{
			super(title);
			this.layouter=layouter;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			graphCanvas.setLayouter(layouter);			
		}
		
	}
	
	
}
