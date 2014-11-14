/**
 *  Filename: $RCSfile: DElement.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.2 $
 *            $Date: 2010/01/28 16:47:28 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.Color;
import java.awt.Graphics;

/*==========================================================================*
 * INTERFACE DECLARATION
 *==========================================================================*/

/**
 */
/**
 * some useful methods for objects which should be paintable in a scaled area
 */
public interface DElement
{
	Color DEFAULT_COLOR = Color.black;
	DRectangle getRectangle();

	/**
	 */
	void setDParent( DParent parent );
	DParent getDParent();

	void paint( Graphics g, DMeasures m );
	void repaint();

	/**
	 */
	void setVisible( boolean aFlag );
	/**
	 */
	boolean isVisible();

	/**
	 */
	void setColor( Color color );
	/**
	 */
	Color getColor();

	/**
	 */
	void setDBorder( DBorder b );
	DBorder getDBorder();
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
