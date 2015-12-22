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
public interface PlotInterface {
  public void setConnectedPoint (double x,double y,int GraphLabel);
  public void addGraph (int g1,int g2);
  public void setUnconnectedPoint (double x, double y,int GraphLabel);
  public void clearAll ();
  public void clearGraph (int GraphNumber);
  public void setInfoString (int GraphLabel, String Info, float stroke);
  public void jump ();
  public String getName();
  public FunctionArea getFunctionArea ();
  public void init();
}

