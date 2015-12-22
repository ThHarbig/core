package mayday.core.structures.trees.layout;


import java.awt.Color;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.structures.trees.painter.IEdgePainter;
import mayday.core.structures.trees.painter.edge.DirectEdgePainter;


/**
 * the Layout representation of an Edge
 * containing its color, line width and the Painter used to paint it
 * @param color A Color object representing the Edge color
 * @param width width in pixels of this Edge
 * @param painter A painter object to paint this individual Edge
 * @author Michael Borner, Andreas Friedrich
 * @see IEdgePainter
 */
public class EdgeLayout implements ILayoutValue{
	
	private Color color;
	private int width;
	private IEdgePainter painter;
	protected boolean showLabel = true;

	/**
	 * Creates a new EdgeLayout
	 * @param color the Edges color
	 * @param width width of the line representing the Edge 
	 * @param painter A painter object to paint this individual Edge 
	 */
	public EdgeLayout(Color color, int width, IEdgePainter painter) {
		this.color = color;
		this.width = width;
		this.painter = painter;
	}
	
	public EdgeLayout(String s) {
		parse(s);
	}


	public IEdgePainter getPainter() {
		return this.painter;
	}

	public void setPainter(IEdgePainter ep) {
		this.painter = ep;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public EdgeLayout clone() {
		return new EdgeLayout(this.color,this.width,this.painter);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void parse(String s) {
		String[] parts = s.split(";");
		color = Color.decode(parts[0]);
		width = Integer.parseInt(parts[1]);
		PluginInfo pli = PluginManager.getInstance().getPluginFromID(parts[2]);
		if (pli!=null)
			painter = (IEdgePainter)pli.newInstance();
		else
			painter = new DirectEdgePainter();
	}

	@SuppressWarnings("unchecked")
	public String serialize() {
		return color.getRGB()+";"+width+";"+PluginManager.getInstance().getPluginFromClass((Class)painter.getClass()).getIdentifier();
	}

	
	public void setLabelVisible(boolean vis) {
		showLabel = vis;
	}

	
	public boolean labelVisible() {
		return showLabel;
	}
	
}
