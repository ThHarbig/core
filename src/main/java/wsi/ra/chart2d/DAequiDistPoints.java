/**
 *  Filename: $RCSfile: DAequiDistPoints.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.1 $
 *            $Date: 2008/12/02 15:27:26 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/


/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 * DAequiDistPoints represents a special kind of <code>DPointSet</code> with
 * constant x-distance between the points
 */
public class DAequiDistPoints extends DPointSet{

  /**
   * minX is the start-x-value for the set,
   * distance the constant x-distance
   */
  public DAequiDistPoints( double minX, double distance ){
    this( minX, distance, 10 );
  }

  /**
   * minX is the start-x-value for the set,
   * distance the constant x-distance,
   * initial-capacity of the array
   */
  public DAequiDistPoints( double minX, double distance, int initial_capacity ){
    super(new AffineMap(minX,distance), new DArray(initial_capacity));
  }

  /**
   * this method adds some new values to the currently stored values
   * it enlarges the array and repaints the set
   *
   * @param v double array of new values
   */
  public void addValues(double[] v){
    for( int i=0; i<v.length; i++ )
      addDPoint(0, v[i]);
    restore();
    repaint();
  }

  /**
   * @deprecated @see #getSize()
   */
  @Deprecated
public int size(){ return getSize(); }

}

/**
 */
class AffineMap implements DIntDoubleMap{
  double c;
  double m;
  int size;

  public AffineMap(double c, double m){
    this.c = c;
    this.m = m;
  }

  public boolean setImage(int index, double v){
    return false;
  }

  public double getImage(int index){
    if( index >= size ) throw
      new ArrayIndexOutOfBoundsException(index);
    return c + index * m;
  }

  public boolean addImage(double v){
    size++;
    return (m != 0);
  }

  /**
 */
public int getSize(){
    return size;
  }

  public double getMaxImageValue(){
    if( size == 0 ) throw
      new IllegalArgumentException("AffineMap is empty. No maximal value exists");
    if( m < 0 ) return c;
    else return c + m * (size-1);
  }

  public double getMinImageValue(){
    if( size == 0 ) throw
      new IllegalArgumentException("AffineMap is empty. No minimal value exists");
    if( m < 0 ) return c + m * (size-1);
    else return c;
  }

  public boolean restore(){
    return false;
  }

  public void reset(){
    size = 0;
  }
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
