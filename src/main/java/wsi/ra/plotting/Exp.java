package wsi.ra.plotting;

/*
 * Title:        JavaEvA
 * Description:
 * Copyright:    Copyright (c) 2003
 * Company:      University of Tuebingen, Computer Architecture
 * @author Holger Ulmer, Felix Streichert, Hannes Planatscher
 * @version:  $Revision: 1.1 $
 *            $Date: 2008/12/02 15:27:09 $
 *            $Author: battke $
 */
/*==========================================================================*
 * IMPORTS
 *==========================================================================*/
import wsi.ra.chart2d.DFunction;

/**
 *
 */
public class Exp extends DFunction{
  @Override
public boolean isDefinedAt( double source ){ return true; }
  @Override
public boolean isInvertibleAt( double image ){ return image > 0; }
  @Override
public double getImageOf( double source ){ return Math.exp( source ); }
  @Override
public double getSourceOf( double target ){
    if( target <= 0 )  { throw
      new IllegalArgumentException(
        "Can not calculate log on values smaller than or equal 0 --> target = "+target
      );
    }
    return Math.log( target );
  }
}

