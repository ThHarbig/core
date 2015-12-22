package mayday.core.structures.trees.layout;


import java.awt.Color;
import java.awt.Font;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.structures.trees.painter.INodePainter;
import mayday.core.structures.trees.painter.node.LabelInside;

/**
 * the Layout representation of a Node
 * containing its color, height, width and the Painter used to paint it
 * @param color A Color object representing the Node color
 * @param height height in pixels of this Node
 * @param width width in pixels of this Node
 * @param painter A painter object to paint this individual Node
 * @author Michael Borner, Andreas Friedrich
 * @see INodePainter
 */
public class NodeLayout implements ILayoutValue{
	
	protected Color color;
	protected int height;
	protected int width;
	protected Font font;
	protected INodePainter painter;
	protected boolean showLabel = true;

	
	/**
	 * Creates a new NodeLayout
	 * @param color the Nodes color
	 * @param width width of the line representing the Node 
	 * @param painter A painter object to paint this individual Node 
	 */
	public NodeLayout (Color color, int height, int width, Font font, INodePainter painter){
		this.color = color;
		this.height = height;
		this.width = width;
		this.font = font;
		this.painter = painter;
	}
	
	public NodeLayout(String s) {
		parse(s);
	}
	
	public NodeLayout clone() {
		return new NodeLayout(this.color,this.height,this.width,this.font,this.painter);
	}
	
	public INodePainter getPainter() {
		return this.painter;
	}

	public void setPainter(INodePainter np) {
		this.painter = np;
	}

	public Color getColor() {
		return color;
	}

	public int getHeight() {		
		return height;
	}

	public int getWidth() {
		return width;
	}

	public Font getFont() {
		return font;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	
	public void parse(String s) {
		String[] parts = s.split(";");
		color = Color.decode(parts[0]);
		height = Integer.parseInt(parts[1]);
		width = Integer.parseInt(parts[2]);
		font = Font.decode(parts[3]);
		showLabel = Boolean.parseBoolean(parts[4]);
		PluginInfo pli = PluginManager.getInstance().getPluginFromID(parts[5]);
		if (pli!=null)
			painter = (INodePainter)pli.newInstance();
		else
			painter = new LabelInside();
	}

	@SuppressWarnings("unchecked")
	public String serialize() {
		String fontS = font.getFontName()+"-"+font.getStyle()+"-"+font.getSize();
		return color.getRGB()+";"+height+";"+width+";"+fontS+";"+labelVisible()+";"
				+PluginManager.getInstance().getPluginFromClass((Class)painter.getClass()).getIdentifier();
	}
	
	public void setLabelVisible(boolean vis) {
		showLabel = vis;
	}
	
	public boolean labelVisible() {
		return showLabel;
	}
}
