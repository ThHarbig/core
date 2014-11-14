package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;

@PluginManager.IGNORE_PLUGIN
public class ImageDecorator extends RendererDecorator  {

	private Image image;
	
	public ImageDecorator(ComponentRenderer renderer, Image image) 
	{
		super(renderer);
		this.image=image;
	}

	@Override
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,
			String label, boolean selected) 
	{
		getRenderer().draw(g, node, bounds, value, label, selected);
		if(image!=null)
		{	
			if( ((BufferedImage)image).getWidth() > bounds.width || ((BufferedImage)image).getHeight() > bounds.height)
				return;
			
			g.setColor(Color.white);
			g.fillRect(1, 1, ((BufferedImage)image).getWidth(),((BufferedImage)image).getHeight());
			g.drawImage(image, 0, 0, null);			
		}
				
	}

	public Dimension getSuggestedSize(Node node, Object value) 
	{
		Dimension d=new Dimension(
				Math.max( ((BufferedImage)image).getWidth(),getRenderer().getSuggestedSize(node,value).width),
				Math.max( ((BufferedImage)image).getHeight(),getRenderer().getSuggestedSize(node,value).height));
		return d;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.RendererDecorator.Image",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Adds an image to a component",
				"Image"				
		);
		pli.addCategory(GROUP_DECORATORS);
		return pli;	
	}
	
//	@Override
//	public Setting getSetting() 
//	{
//		return new ;
//	}

}
