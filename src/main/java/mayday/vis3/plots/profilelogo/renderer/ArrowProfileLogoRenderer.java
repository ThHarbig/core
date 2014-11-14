package mayday.vis3.plots.profilelogo.renderer;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;


public class ArrowProfileLogoRenderer implements ProfileLogoRenderer
{
	private Color color;
	private ArrowType arrowType;
	private boolean fillArrows;
	
	public ArrowProfileLogoRenderer()
	{
		arrowType=ArrowType.STABLE;
		color=Color.black;	
		fillArrows=true;		
	}
	
	public ArrowProfileLogoRenderer(ArrowType arrow)
	{
		arrowType=arrow;
		color=Color.black;
		fillArrows=true;
	}
	
	public ArrowProfileLogoRenderer(ArrowType arrow, Color col)
	{
		arrowType=arrow;
		color=col;	
		fillArrows=true;
	}
	
	public Path2D renderToShape(Rectangle2D rect) 
	{		
		switch(arrowType)
		{
			case UP: return getUpArrow(rect); 
			case DOWN: return getDownArrow(rect); 
			case STABLE: return getStableArrow(rect); 
			case MID: return getMidArrow(rect); 
			
			default: return getStableArrow(rect); 
		}
	}
		
	public Path2D getDownArrow(Rectangle2D rect)
	{
		Path2D p=new Path2D.Double();
		p.moveTo(rect.getX()+rect.getWidth()/4.0, rect.getY());
		p.lineTo(rect.getX()+3.0*rect.getWidth()/4.0, rect.getY());
		p.lineTo(rect.getX()+3.0*rect.getWidth()/4.0, rect.getY()+rect.getHeight()/2.0);
		p.lineTo(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight()/2.0);
		p.lineTo(rect.getX()+rect.getWidth()/2.0, rect.getY()+rect.getHeight());
		p.lineTo(rect.getX(), rect.getY()+rect.getHeight()/2.0);
		p.lineTo(rect.getX()+rect.getWidth()/4.0, rect.getY()+rect.getHeight()/2.0);
		p.lineTo(rect.getX()+rect.getWidth()/4.0, rect.getY());
		return p;
	}
	
	public Path2D getUpArrow(Rectangle2D rect)
	{
		Path2D p=new Path2D.Double();
		p.moveTo(rect.getX()+rect.getWidth()/4.0, rect.getY()+rect.getHeight());
		p.lineTo(rect.getX()+3.0*rect.getWidth()/4.0, rect.getY()+rect.getHeight());
		p.lineTo(rect.getX()+3.0*rect.getWidth()/4.0, rect.getY()+rect.getHeight()/2.0);
		p.lineTo(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight()/2.0);
		p.lineTo(rect.getX()+rect.getWidth()/2.0, rect.getY());
		p.lineTo(rect.getX(), rect.getY()+rect.getHeight()/2.0);
		p.lineTo(rect.getX()+rect.getWidth()/4.0, rect.getY()+rect.getHeight()/2.0);
		return p;
	}	

	public Path2D getStableArrow(Rectangle2D rect)
	{
		Path2D p=new Path2D.Double();
		p.moveTo(rect.getX(), rect.getY()+rect.getHeight()/2.0);
		p.lineTo(rect.getX()+rect.getWidth()/2.0, rect.getY());		
		p.lineTo(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight()/2.0);
		p.lineTo(rect.getX()+rect.getWidth()/2.0, rect.getY()+rect.getHeight());
		p.lineTo(rect.getX(), rect.getY()+rect.getHeight()/2.0);
		return p;
	}	
	
	public Path2D getMidArrow(Rectangle2D rect)
	{
		Path2D p=new Path2D.Double();
		p.moveTo(rect.getMinX()+rect.getWidth()/4.0, rect.getMinY());
		p.lineTo(rect.getMaxX()-rect.getWidth()/4.0, rect.getMinY());
		p.lineTo(rect.getMaxX()-rect.getWidth()/4.0, rect.getMaxY());
		p.lineTo(rect.getMinX()+rect.getWidth()/4.0, rect.getMaxY());
		p.lineTo(rect.getMinX()+rect.getWidth()/4.0, rect.getMaxY());
		return p;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public ArrowType getArrowType() {
		return arrowType;
	}

	public void setArrowType(ArrowType arrowType) {
		this.arrowType = arrowType;
	}

	public boolean isFillArrows() {
		return fillArrows;
	}

	public void setFillArrows(boolean fillArrows) {
		this.fillArrows = fillArrows;
	}
	
	

	
}
