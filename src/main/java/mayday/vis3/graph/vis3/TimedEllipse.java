package mayday.vis3.graph.vis3;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class TimedEllipse extends Ellipse2D.Double
{
	private long endtime;
	
	public TimedEllipse(Rectangle comp)
	{
		super(comp.getX()-comp.getWidth()/2.0,  comp.getY()-comp.getHeight()/2.0, comp.getWidth()*2, comp.getHeight()*2 );
		endtime=System.currentTimeMillis()+5000;
	}
	
	public TimedEllipse(CanvasComponent comp)
	{
		super(comp.getX()-comp.getWidth()/2.0,  comp.getY()-comp.getHeight()/2.0, comp.getWidth()*2, comp.getHeight()*2 );
		endtime=System.currentTimeMillis()+5000;
	}
	
	public boolean stillAlive()
	{
		return endtime > System.currentTimeMillis();
	}
}
