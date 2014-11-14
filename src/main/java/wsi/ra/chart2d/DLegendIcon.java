package wsi.ra.chart2d;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * This class represents a LegendIcon which is just a rectangle with the given 
 * height and width in the given color. This class is used to add an Icon to a JLabel
 * @author Nastasja Trunk
 *
 */
public class DLegendIcon implements Icon{

	Color color;
	int   width;
	int   height;
	
	
	public DLegendIcon(Color color, int width, int height){
		this.color = color;
		this.width = width;
		this.height=height;
	}
	/* (non-Javadoc)
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon( Component c, Graphics g, int x, int y){
		g.setColor(color);
		g.fillRect(x, y, getIconWidth(), getIconHeight());		
	}
	

	/**
	 * Sets the height of the DLegendIcon
	 * @param height
	 */
	public void setIconHeight(int height) {
		this.height = height;
	}


	/**
	 * Sets the width of the DLegendIcon element
	 * @param width
	 */
	public void setIconWidth(int width) {
		this.width = width;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth(){
		return width;		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight(){
		return height;
	}
	
}
