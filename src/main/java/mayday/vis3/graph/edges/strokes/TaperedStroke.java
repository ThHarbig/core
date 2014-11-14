package mayday.vis3.graph.edges.strokes;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;

public class TaperedStroke implements Stroke 
{
	private static final float FLATNESS = 1;

	private float startWidth=10;
	private float endWidth=1;
	private float step=0.2f;

	@Override
	public Shape createStrokedShape(Shape shape) 
	{
		PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
		float points[] = new float[6];
//		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
		float sofar=0;

		GeneralPath result = new GeneralPath();

		float length=measurePathLength(shape);
		float seg=length/((startWidth-endWidth)/step);

		float w=startWidth;
		Stroke stk=new BasicStroke(w);
		float next=sofar;

		while ( !it.isDone() ) 
		{
			type = it.currentSegment( points );
			switch( type )
			{
			case PathIterator.SEG_MOVETO:
				lastX=points[0];
				lastY=points[1];
				break;

			case PathIterator.SEG_CLOSE:
//				moveX=points[0];
//				moveY=points[1] ;
				//$FALL-THROUGH$
			case PathIterator.SEG_LINETO:
				thisX = points[0];
				thisY = points[1];

				float dx = thisX-lastX;
				float dy = thisY-lastY;
				float distance = (float)Math.sqrt( dx*dx + dy*dy );

				float od=distance;
				
//				result.append(new Line2D.Double(lastX, lastY, thisX, thisY), true);
				
				while(sofar+distance > next)
				{	
					float d=next-sofar;
					float dp=d/od;
					float x= lastX+dp*dx;
					float y= lastY+dp*dy;
					
					Line2D l=new Line2D.Float(lastX, lastY, x, y);
					result.append(stk.createStrokedShape(l), true);
//					result.append(new Ellipse2D.Double(x, y, 5, 5),false);
					sofar=next;
					next+=seg;
					w-=step;
//					System.out.println(w+ "\t" + lastX+ "\t" + lastY+ "\t" + x+ "\t" + y );
					
					lastX = x;
					lastY = y;
					distance-=d;	
					
					if(w <=step )
						break;					
					stk=new BasicStroke(w);		
				}
				System.out.println(w+ "\t" + lastX+ "\t" + lastY+ "\t" + thisX+ "\t" + thisY );
				Line2D l=new Line2D.Float(lastX, lastY, thisX, thisY);
				
				result.append(stk.createStrokedShape(l), false);
				sofar+=distance;

				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}
//		result.moveTo(lastX,lastY);
//		result.closePath();


		return result;
	}



	public float measurePathLength( Shape shape ) {
		PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
		float total = 0;

		while ( !it.isDone() ) {
			type = it.currentSegment( points );
			switch( type ){
			case PathIterator.SEG_MOVETO:
				moveX = lastX = points[0];
				moveY = lastY = points[1];
				break;

			case PathIterator.SEG_CLOSE:
				points[0] = moveX;
				points[1] = moveY;
				// Fall into....

				//$FALL-THROUGH$
			case PathIterator.SEG_LINETO:
				thisX = points[0];
				thisY = points[1];
				float dx = thisX-lastX;
				float dy = thisY-lastY;
				total += (float)Math.sqrt( dx*dx + dy*dy );
				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return total;
	}
	
	@Override
	public String toString() 
	{
		return "Tapered (2.0)";
	}

}
