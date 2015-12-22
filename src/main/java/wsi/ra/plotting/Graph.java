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
/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/
/**
 */
public class Graph {
  private PlotInterface m_Plotter;
  private int m_GraphLabel;
  private String m_Info;
  /**
   *
   */
  public Graph(String Info, PlotInterface Plotter,int x) {
      m_Info = Info;
    m_Plotter = Plotter;
    m_GraphLabel = x;
    if (m_Plotter==null)
      System.out.println("In constructor m_Plotter == null");
    m_Plotter.setInfoString(m_GraphLabel,Info, (float) 1.0 );
  }
  /**
   *
   * @param Info
   * @param stroke
   */
  public String getInfo() {return m_Info;}
   /**
   *
   */
  public void setInfoString(String Info,float stroke) {
    m_Plotter.setInfoString(m_GraphLabel, Info,stroke);
  }
  /**
   *
   */
  public int getGraphLabel () {
    return m_GraphLabel;
  }
  /**
   *
   */
  public void setConnectedPoint(double x,double y) {
      m_Plotter.setConnectedPoint(x,y,m_GraphLabel);
  }
  /**
   *
   */
  public void clear() {
    m_Plotter.clearGraph(m_GraphLabel);
  }
  /**
   *
   */
  public void setUnconnectedPoint(double x,double y) {
      m_Plotter.setUnconnectedPoint(x,y,m_GraphLabel);
  }

  /**
   *
   */
  public void addGraph(Graph x) {
    m_Plotter.jump();
    m_Plotter.addGraph(m_GraphLabel, x.getGraphLabel());
  }
  
   /**
   * Causes the PlotInterface to interupt the connected painting at the
   * current position.
   */
  public void jump() {
    m_Plotter.jump();
  }
}