package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.vis3.graph.renderer.RendererTools;

public class NoteRenderer extends DefaultComponentRenderer 
{

	@Override
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value, String label, boolean selected) 
	{
		RendererTools.fill(g, bounds, Color.LIGHT_GRAY);
		RendererTools.drawBox(g, bounds, selected);

		Font font=g.getFont();
		String text=node.getName();
		if(node instanceof DefaultNode )
		{
			DefaultNode dn=(DefaultNode)node;
			String name=font.getName();
			int style=font.getStyle();
			int size=font.getSize();
			if(dn.hasProperty(Nodes.NOTE_FONT_NAME))
				name=dn.getPropertyValue(Nodes.NOTE_FONT_NAME);			
			if(dn.hasProperty(Nodes.NOTE_FONT_SIZE))
				size=Integer.parseInt(dn.getPropertyValue(Nodes.NOTE_FONT_SIZE));			
			if(dn.hasProperty(Nodes.NOTE_FONT_STYLE))
				style=Integer.parseInt(dn.getPropertyValue(Nodes.NOTE_FONT_STYLE));
			if(dn.hasProperty(Nodes.NOTE_TEXT) && dn.getPropertyValue(Nodes.NOTE_TEXT)!=null)
				text=dn.getPropertyValue(Nodes.NOTE_TEXT);

			try {
				if(dn.hasProperty(Nodes.NOTE_BACKGROUND_COLOR) )
				{
					Color c=Color.decode("0x"+dn.getPropertyValue(Nodes.NOTE_BACKGROUND_COLOR));
					RendererTools.fill(g, bounds, c);
				}
			} catch (NumberFormatException e) {}
			try {
				if(dn.hasProperty(Nodes.NOTE_TEXT_COLOR) )
				{
					Color c=Color.decode("0x"+dn.getPropertyValue(Nodes.NOTE_TEXT_COLOR));
					g.setColor(c);
				}
			} catch (NumberFormatException e) {}
			font=new Font(name, style, size);
		}
		if(text==null)
		{
			DefaultComponentRenderer.getDefaultRenderer().drawString(g, bounds, selected, "?");
			return;
		}
		RendererTools.drawBreakingString(g, font, text, bounds.width-10, 5, 5);
	}
	
	

	@Override
	public Dimension getSuggestedSize() 
	{
		return new Dimension(160,120);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Note",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Renderer for Notes",
				"Note renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}

}
