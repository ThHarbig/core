package mayday.vis3.plots.profilelogo.renderer;

import java.awt.Color;

import mayday.core.gui.GUIUtilities;
import mayday.vis3.gradient.ColorGradient;

public class BinningRendererTool 
{
	public static ProfileLogoRenderer[] suggestRenderer(int binCount)
	{
		ProfileLogoRenderer[] res=new ProfileLogoRenderer[binCount];
		switch(binCount)
		{
			case 1: 
				res[0]= new DefaultProfileLogoRenderer(); 
				break;
			case 2: 
				res[1]= new ArrowProfileLogoRenderer(ArrowType.DOWN, Color.green); 
				res[0]= new ArrowProfileLogoRenderer(ArrowType.UP, Color.red); 
				break;
			case 3: 	
				res[2]= new ArrowProfileLogoRenderer(ArrowType.DOWN, Color.green); 
				res[1]= new ArrowProfileLogoRenderer(ArrowType.MID, Color.black); 
				res[0]= new ArrowProfileLogoRenderer(ArrowType.UP, Color.red); 
				break;
			default:	
				
				Color[] colors=GUIUtilities.rainbow(binCount, 1.0);
				for(int i=0; i!=binCount;++i)
				{
					res[i]=new DefaultProfileLogoRenderer(colors[i]);
				}
				break;		
		}		
		return res;
	}
	
	public static ProfileLogoRenderer[] suggestRenderer(int binCount, ColorGradient grad)
	{
		ProfileLogoRenderer[] res=new ProfileLogoRenderer[binCount];
		switch(binCount)
		{
			case 1: 
				res[0]= new DefaultProfileLogoRenderer(); 
				break;
			case 2: 
				res[1]= new ArrowProfileLogoRenderer(ArrowType.DOWN,grad.mapValueToColor(grad.getMax())); 
				res[0]= new ArrowProfileLogoRenderer(ArrowType.UP, grad.mapValueToColor(grad.getMin())); 
				break;
			case 3: 	
				res[2]= new ArrowProfileLogoRenderer(ArrowType.DOWN, grad.mapValueToColor(grad.getMax())); 
				res[1]= new ArrowProfileLogoRenderer(ArrowType.MID, grad.mapValueToColor(grad.getMidpoint())); 
				res[0]= new ArrowProfileLogoRenderer(ArrowType.UP, grad.mapValueToColor(grad.getMin())); 
				break;
			default:	
				
				
				for(int i=0; i!=binCount; ++i)
				{
					res[i]=new DefaultProfileLogoRenderer(grad.mapValueToColor(i));
				}
				break;		
		}		
		return res;
	}
}
