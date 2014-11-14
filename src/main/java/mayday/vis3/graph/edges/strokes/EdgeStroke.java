package mayday.vis3.graph.edges.strokes;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;


public class EdgeStroke implements Stroke
{
	protected Stroke stroke;
	protected String name;
	
	private static final float[] dashedPattern ={8.0f,8.0f};
	private static final float[] dottedPattern ={2.0f, 2.0f};
	
	
	@Override
	public Shape createStrokedShape(Shape p) 
	{
		return stroke.createStrokedShape(p);
	}
	
	@Override
	public String toString() 
	{
		return name;
	}
	
	public static EdgeStroke createEdgeStroke(float width)
	{
		EdgeStroke res=new EdgeStroke();
		res.stroke=new BasicStroke(width);
		res.name="Solid ("+width+")";
		return res;
		
	}
	
	public static EdgeStroke createDashedEdgeStroke(float width)
	{
		EdgeStroke res=new EdgeStroke();
		res.stroke=new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,dashedPattern,0.0f);
		res.name="Dashed ("+width+")";
		return res;
	}
	
	public static EdgeStroke createDottedEdgeStroke(float width)
	{
		EdgeStroke res=new EdgeStroke();
		res.stroke=new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,dottedPattern,0.0f);
		res.name="Dotted ("+width+")";
		return res;
	}
}
