/**
 *  Filename: $RCSfile: DParent.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.1 $
 *            $Date: 2008/12/02 15:27:25 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;


/*==========================================================================*
 * INTERFACE DECLARATION
 *==========================================================================*/

public interface DParent
{
  void addDElement( DElement e );
  boolean removeDElement( DElement e );
  void repaint( DRectangle r );
  DElement[] getDElements();
  boolean contains( DElement e );
  void addDBorder( DBorder b );
  void restoreBorder();
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
