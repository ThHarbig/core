/**
 *  Filename: $RCSfile: DImage.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.3 $
 *            $Date: 2010/01/28 16:47:28 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;

/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 */
public class DImage extends DRectangle
{
	Image image;
	ImageObserver observer;

	public DImage( double x, double y, double width, double height, ImageObserver observer ){
		super( x, y, width, height );
		this.observer = observer;
	}

	@Override
	public void paint( Graphics g, DMeasures m ){
		//    Graphics g = m.getGraphics();
		@SuppressWarnings("unused")
		DParent parent = getDParent();
		Point p1 = m.getPoint( x, y ),
		p2 = m.getPoint( x + width, y + height );
		if( image == null ) g.drawRect( p1.x, p2.y, p2.x - p1.x, p1.y - p2.y );
		else g.drawImage( image, p1.x, p2.y, p2.x - p1.x, p1.y - p2.y, observer );
	}

	/**
	 */
	public void setImage( Image img ){
		if( img.equals( image ) ) return;
		image = img;
		repaint();
	}
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
