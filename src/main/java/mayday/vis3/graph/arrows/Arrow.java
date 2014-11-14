package mayday.vis3.graph.arrows;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import mayday.vis3.graph.edges.ArrowSetting;

public class Arrow 
{
	protected ArrowSettings settings;	
	private  ArrowPainter sourcePainter;	
	private ArrowPainter targetPainter;
	
	public Arrow() 
	{
		settings=new ArrowSettings();
		sourcePainter=ArrowPainter.painterForStyle(settings.getSourceStyle());
		targetPainter=ArrowPainter.painterForStyle(settings.getTargetStyle());
	}
	
	
	public Arrow(ArrowSettings settings) 
	{
		settings=new ArrowSettings();
		sourcePainter=ArrowPainter.painterForStyle(settings.getSourceStyle());
		targetPainter=ArrowPainter.painterForStyle(settings.getTargetStyle());
	}
	
	public static Shape paint(Point2D source, Point2D target, ArrowSettings settings)
	{
		Path2D p=new Path2D.Double();
		if(settings.isRenderSource())
		{
			p.append(paintSource(source, target,settings),false);
		}
		if(settings.isRenderTarget())
		{
			p.append(paintTarget(source, target,settings),false);
		}
		return p;
	}
	
	public static Shape paint(Point2D source, Point2D support, Point2D target, ArrowSettings settings)
	{
		Path2D p=new Path2D.Double();
		if(settings.isRenderSource())
		{
			p.append(paintSource(source, support,settings),false);
		}
		if(settings.isRenderTarget())
		{
			p.append(paintTarget(support, target,settings),false);
		}
		return p;
	}
		
//	public void paint(Graphics g, Line2D line)
//	{
//		Point source=new Point((int)line.getX1(),(int)line.getY1());
//		Point target=new Point((int)line.getX2(),(int)line.getY2());
//		if(settings.isRenderSource())
//		{
//			paintSource( source, target);
//		}
//		if(settings.isRenderTarget())
//		{
//			paintTarget(source, target);
//		}
//	}
//	
//	public void paint(Graphics g, QuadCurve2D line)
//	{
//		Point source=new Point((int)line.getX1(),(int)line.getY1());
//		Point target=new Point((int)line.getX2(),(int)line.getY2());
//		
//		Point c1=new Point((int)line.getCtrlX(),(int)line.getCtrlY());
//		
//		
//		if(settings.isRenderSource())
//		{
//			paintSource( source, c1);
//		}
//		if(settings.isRenderTarget())
//		{
//			paintTarget(c1, target);
//		}
//	}
//	
//	public void paint(Graphics g, CubicCurve2D line)
//	{
//		Point source=new Point((int)line.getX1(),(int)line.getY1());
//		Point target=new Point((int)line.getX2(),(int)line.getY2());
//		
//		Point c1=new Point((int)line.getCtrlX1(),(int)line.getCtrlY1());
//		Point c2=new Point((int)line.getCtrlX2(),(int)line.getCtrlY2());
//		
//		if(settings.isRenderSource())
//		{
//			paintSource(source, c1);
//		}
//		if(settings.isRenderTarget())
//		{
//			paintTarget(c2, target);
//		}
//	}
	

	private static Shape paintSource(Point2D source, Point2D target, ArrowSettings settings)
	{
		return ArrowPainter.painterForStyle(settings.getSourceStyle()).paintArrow(target, source, settings.getSourceLength(), settings.getSourceAngle());
	}
	
	private static Shape paintTarget(Point2D source, Point2D target, ArrowSettings settings)
	{
		return ArrowPainter.painterForStyle(settings.getTargetStyle()).paintArrow( source, target, settings.getTargetLength(), settings.getTargetAngle());
	}
	
	public static Shape paintSource(Point2D source, Point2D target, ArrowSetting setting)
	{
		return setting.getArrowPainter().paintArrow(target, source, setting.getSize(), setting.getAngle());
	}
	
	public static Shape paintTarget(Point2D source, Point2D target, ArrowSetting setting)
	{
		return setting.getArrowPainter().paintArrow(source, target, setting.getSize(), setting.getAngle());
	}

	/**
	 * @return the settings
	 */
	public ArrowSettings getSettings() {
		return settings;
	}


	/**
	 * @param settings the settings to set
	 */
	public void setSettings(ArrowSettings settings) 
	{
		this.settings = settings;
		sourcePainter=ArrowPainter.painterForStyle(settings.getSourceStyle());
		targetPainter=ArrowPainter.painterForStyle(settings.getTargetStyle());
	}


	/**
	 * @return the sourcePainter
	 */
	public ArrowPainter getSourcePainter() {
		return sourcePainter;
	}


	/**
	 * @return the targetPainter
	 */
	public ArrowPainter getTargetPainter() {
		return targetPainter;
	}
	
}
