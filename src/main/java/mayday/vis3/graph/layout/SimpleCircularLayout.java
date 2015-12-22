package mayday.vis3.graph.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.DepthFirstIterator;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class SimpleCircularLayout extends CanvasLayouterPlugin
{
	private RestrictedStringSetting methodSetting=new RestrictedStringSetting("Method", null, 0, new String[]{"Equal Angle","Equal Height"});
	private IntSetting vSpace=new IntSetting("Vertical Spacer",null,30);
	
	public SimpleCircularLayout() 
	{
		initSetting();
	}
	
	@Override
	protected void initSetting() {}
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		if(methodSetting.getSelectedIndex()==0)
		{
			layoutEqualAngle(container, bounds, model);
		}
		if(methodSetting.getSelectedIndex()==1)
		{
			layoutEqualHeight(container, bounds, model);
		}		
	}
	
	public void layoutEqualHeight(Container container, Rectangle bounds, GraphModel model) 
	{
		CanvasComponent n0=model.getComponents().get(0);
		int inDeg=model.getNode(n0).getInDegree();
		
		for(CanvasComponent comp:model.getComponents())
		{
			if(model.getNode(comp).getInDegree() < inDeg)
			{
				n0=comp;
			}
		}
		DepthFirstIterator dfs=new DepthFirstIterator(model.getGraph(),model.getNode(n0));
				
		// group in a,b
		List<Node> A=new ArrayList<Node>();
		List<Node> B=new ArrayList<Node>();
		int i=0;
		while(dfs.hasNext())
		{
			Node n=dfs.next();	
			if(i < model.getGraph().nodeCount()/2)
				A.add(n);
			else
				B.add(n);
			++i;
		}
		// calculate radius
		int sumA=0;
		for(Node n: A)
			sumA+=model.getComponent(n).getHeight()+vSpace.getIntValue();
		int sumB=0;
		for(Node n: B)
			sumB+=model.getComponent(n).getHeight()+vSpace.getIntValue();	
		
		final int D=Math.max(sumA, sumB);
		final double r=D/2.0;
		//place		
		i=0; 
		for(Node n:A)
		{
			double yp=(1.0*i/(1.0*A.size())*D);
			yp-=r;
			double xp=Math.sqrt(r*r-yp*yp);
//			xp/=2;
			model.getComponent(n).setLocation((int)(bounds.getCenterX()+xp+vSpace.getIntValue()-model.getComponent(n).getWidth()/2), (int)(bounds.getCenterY()+yp));
			++i;
		}	
		i=0; 
		Collections.reverse(B);
		for(Node n:B)
		{
			double yp=(1.0*i/(1.0*B.size())*D);
			yp-=r;
			double xp=Math.sqrt(r*r-yp*yp);
			model.getComponent(n).setLocation((int)(bounds.getCenterX()-xp-vSpace.getIntValue()), (int)(bounds.getCenterY()+yp));
			++i;
		}			
	}
	
	private void layoutEqualAngle(Container container, Rectangle bounds, GraphModel model) 
	{
		int rad=(Math.min(bounds.width, bounds.height)/2)-50;
		CanvasComponent n0=model.getComponents().get(0);
		int inDeg=model.getNode(n0).getInDegree();
		for(CanvasComponent comp:model.getComponents())
		{
			if(model.getNode(comp).getInDegree() < inDeg)
			{
				n0=comp;
			}
		}
		double cc=model.componentCount();
		DepthFirstIterator dfs=new DepthFirstIterator(model.getGraph(),model.getNode(n0));
		double u=0;
		double twopi=2.0*Math.PI;
		
		while(dfs.hasNext())
		{
			Node n=dfs.next();
			LayoutUtilities.placeOnAngleRadius(model.getComponent(n), rad, u*(twopi/cc), new Point2D.Double(bounds.getCenterX(), bounds.getCenterY()));
			u++;
		}
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.SimpleCircular",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Arrange components on a circle",
				"Circular"				
		);
		return pli;	
	}	
}
