/**
 *  Filename: $RCSfile: DBorder.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.2 $
 *            $Date: 2009/03/25 22:44:17 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */
package wsi.ra.chart2d;

import java.awt.Insets;

@SuppressWarnings("serial")
public class DBorder extends Insets{

  public DBorder(){
    this( 0, 0, 0, 0 );
  }

  public DBorder(int top, int left, int bottom, int right ){
    super( top, left, bottom, right );
  }

  public boolean insert( DBorder b ){
    boolean changed = false;
    if( b.top    > top    ){ top    = b.top;    changed = true; }
    if( b.bottom > bottom ){ bottom = b.bottom; changed = true; }
    if( b.left   > left   ){ left   = b.left;   changed = true; }
    if( b.right  > right  ){ right  = b.right;  changed = true; }
    return changed;
  }

  @Override
public boolean equals( Object o ){
    if( o instanceof DBorder )
      return super.equals( o );
    return false;
  }

  @Override
public String toString(){
    return "wsi.ra.chart2d.DBorder[top="+top+",left="+left
           +",bottom="+bottom+",right="+right+"]";
  }
}