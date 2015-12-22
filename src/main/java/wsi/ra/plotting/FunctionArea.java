package wsi.ra.plotting;


/*
 * Title:        JavaEvA
 * Description:
 * Copyright:    Copyright (c) 2003
 * Company:      University of Tuebingen, Computer Architecture
 * @author Holger Ulmer, Felix Streichert, Hannes Planatscher
 * @version:  $Revision: 1.8 $
 *            $Date: 2010/12/02 14:53:24 $
 *            $Author: battke $
 */
/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import wsi.ra.chart2d.DArea;
import wsi.ra.chart2d.DPoint;
import wsi.ra.chart2d.DPointSet;
import wsi.ra.chart2d.ScaledBorder;


/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/
/**
 */
/**
 *
 */
@SuppressWarnings("serial")
public class FunctionArea extends DArea implements Serializable {
	// private InterfaceRefPointListener       m_RefPointListener;
	private JFileChooser                    m_FileChooser;
	private ArrayList<GraphPointSet>                       m_PointSetContainer;
	private ScaledBorder                    m_Border;
	private boolean                         m_log = false;
//	private int                             m_x;
//	private int                             m_y;

//	private DPointIcon                      m_CurrentPointIcon;
	/**
	 *
	 */
	public FunctionArea() {}

	/**
	 *
	 */
	public FunctionArea(String xname, String yname) {
		super();
		setPreferredSize(new Dimension(600, 500));
		setVisibleRectangle(1, 1, 100000, 1000);
		setAutoFocus(true);
		setMinRectangle(0, 0, 1, 1);
		//setAutoFocus(true);
		m_Border = new ScaledBorder();
		m_Border.x_label = xname; //"App. " + Name + " func. calls";
		m_Border.y_label = yname; //"fitness";
		setBorder(m_Border);
		setAutoGrid(true);
		setGridVisible(true);
		m_PointSetContainer = new ArrayList<GraphPointSet>(20);
		//new DMouseZoom( this );
		setBackground(Color.WHITE);
		//addPopup();
		repaint();
	}

	/**
	 *
	 */
	public String getGraphInfo(int x, int y) {
		String ret = "";
		if ((m_PointSetContainer == null) || (m_PointSetContainer.size() == 0))
			return ret;
		int minindex = getNearestGraphIndex(x, y);
		ret = ((GraphPointSet) (m_PointSetContainer.get(minindex))).getInfoString();
		return ret;
	}

	public void setAxisTitle(String xtitle, String ytitle)
	{
		m_Border.x_label = xtitle;
		m_Border.y_label = ytitle;
	}

	public String getAxisTitleX() {
		return m_Border.x_label;
	}
	
	public String getAxisTitleY() {
		return m_Border.y_label;
	}
	
	public ScaledBorder getBorder() {
		return m_Border;
	}

	/**
	 * This funtion returns the graph info associated with the graph which is identified
	 * by the Graphlabel
	 * @author Nastasja Trunk
	 * @param Graphlabel
	 * @return
	 */
	public String getGraphInfo(int Graphlabel){
		String ret = "";
		if((m_PointSetContainer == null) || (m_PointSetContainer.size() ==0) 
				|| m_PointSetContainer.size() <= Graphlabel)
			return ret;
		else{ 

			if(((GraphPointSet) (m_PointSetContainer.get((Graphlabel)))).getGraphLabel() == Graphlabel)
			{
				ret =((GraphPointSet) (m_PointSetContainer.get((Graphlabel)))).getInfoString();
			}
			else{
				//This is necessary to get the legend to work
				//there is a duplication of the GraphPointSets in the m_PointSetContainer
				GraphPointSet tmp =((GraphPointSet) (m_PointSetContainer.get((Graphlabel))));
				for(int i = tmp.getGraphLabel(); i < m_PointSetContainer.size(); ++i){
					if(((GraphPointSet) (m_PointSetContainer.get((i)))).getGraphLabel() == Graphlabel){
						ret = ((GraphPointSet) (m_PointSetContainer.get((i)))).getInfoString();
						break;
					}

				}
			}
		}

		return ret; 
	}

	/**
	 * Returns the number of graphs currently added to the functionarea
	 * @return
	 */
	public int getNumberOfGraphs(){
		HashSet<Integer> tmp = new HashSet<Integer>();

		for(int i =0; i != m_PointSetContainer.size(); ++i){
			//Debug 
			System.out.println("FunctionArea Number of Graphs:  " + "GraphLabel " + ((GraphPointSet) m_PointSetContainer.get(i)).getGraphLabel() + " Name:" + getGraphInfo(((GraphPointSet) m_PointSetContainer.get(i)).getGraphLabel()) );
			if(m_PointSetContainer.get(i) instanceof GraphPointSet) tmp.add(((GraphPointSet) m_PointSetContainer.get(i)).getGraphLabel());
		}
		return tmp.size();
	}

	public HashSet<Integer> getGraphLabels(){
		HashSet<Integer> ret = new HashSet<Integer>();

		for(int i =0; i != m_PointSetContainer.size(); ++i){
			if(m_PointSetContainer.get(i) instanceof GraphPointSet){
				ret.add(((GraphPointSet) m_PointSetContainer.get(i)).getGraphLabel());
				//Debugy
				System.out.println("Graphlabel functionarea: " +((GraphPointSet) m_PointSetContainer.get(i)).getGraphLabel());
			}
		}
		return ret;
	}


	/**
	 *
	 */
	public boolean isStatisticsGraph(int x, int y) {
		boolean ret = false;
		if ((m_PointSetContainer == null) || (m_PointSetContainer.size() == 0))
			return ret;
		int minindex = getNearestGraphIndex(x, y);
		ret = ((GraphPointSet) (m_PointSetContainer.get(minindex))).isStatisticsGraph();
		return ret;
	}


	/**
	 *
	 */
	public int getNearestGraphIndex(int x, int y) {
		// get index of nearest Graph
		double distmin = 10000000;
		int minindex = -1;
		DPoint point1 = getDMeasures().getDPoint(x, y);
		DPoint point2 = null;
		double dist = 0;
		for (int i = 0; i < m_PointSetContainer.size(); i++) {
			if (m_PointSetContainer.get(i)instanceof GraphPointSet) {
				GraphPointSet pointset = (GraphPointSet) (m_PointSetContainer.get(i));
				point2 = pointset.getNearestDPoint(point1);
				if (point2 == null)
					continue;
				if (point1 == null) {
					System.out.println("point1 == null");
					continue;
				}
				

				dist = (point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y);
				//System.out.println("dist="+dist+"i="+i);
				if (dist < distmin) {
					distmin = dist;
					minindex = i;
				}
			}
		}
		return minindex;
	}

	/**
	 *
	 */
	public DPoint getNearestDPoint(int x, int y) {
		// get index of nearest Graph
		double distmin = 10000000;
		DPoint ret = null;
		DPoint point1 = getDMeasures().getDPoint(x, y);
		DPoint point2 = null;
		double dist = 0;
		for (int i = 0; i < m_PointSetContainer.size(); i++) {
			if (m_PointSetContainer.get(i)instanceof GraphPointSet) {
				GraphPointSet pointset = (GraphPointSet) (m_PointSetContainer.get(i));
				point2 = pointset.getNearestDPoint(point1);
				if (point2 == null)
					continue;
				dist = (point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y);
				//System.out.println("dist="+dist+"i="+i);
				if (dist < distmin) {
					distmin = dist;
					ret = point2;
				}

			}
		}
		return ret;
	}

	/**
	 *
	 */
	public void exportToAscii() {
		String[] s = null;
		for (int i = 0; i < m_PointSetContainer.size(); i++) {
			if (m_PointSetContainer.get(i)instanceof GraphPointSet) {
				GraphPointSet set = (GraphPointSet) m_PointSetContainer.get(i);
				String info = set.getInfoString();
				DPointSet pset = set.getConnectedPointSet();
				if (s == null) {
					s = new String[pset.getSize() + 1];
					s[0] = "calls";
				}
				s[0] = s[0] + " " + info;
				for (int j = 1; j < s.length; j++) {
					if (i == 0)
						s[j] = "" + pset.getDPoint(j - 1).x;
					try {
						s[j] = s[j] + " " + pset.getDPoint(j - 1).y;
					} catch (Exception e) {
						s[j] += " ";
					}
				}
			}
		}
		if (s!=null) {
			for (int j = 0; j < s.length; j++) {
				System.out.println("s=" + s[j]);
			}
		}
		SimpleDateFormat formatter = new SimpleDateFormat("E'_'yyyy.MM.dd'_'HH.mm.ss");
		String fname = "PlotExport_"+formatter.format(new Date());
		//    System.out.println("Filename ??");
		//    String fname = null;
		//    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		//    try {
		//      fname = in.readLine();
		//    } catch (Exception e) {
		//      System.out.println("" + e.getMessage());
		//    }
		try {
			File f = new File(fname + ".txt");
			f.createNewFile();
			PrintWriter Out = new PrintWriter(new FileOutputStream(f));
			if (s!=null)
				for (int j = 0; j < s.length; j++)
					Out.println(s[j]);
			Out.flush();
			Out.close();
		} catch (Exception e) {
			System.out.println("Error:" + e.getMessage());
		}

	}

	/**
	 *
	 */
	public void setConnectedPoint(double x, double y, int GraphLabel) {
		if (m_log == true && y <= 0.0) {
			System.out.println("Warning !!!y value <0 and log !! y" + y);
			y = 1;
		}
		//if (x==0) System.out.println("x="+x+" y "+y);
		getGraphPointSet(GraphLabel).addDPoint(x, y);
	}



	public void addGraphPointSet(GraphPointSet d) {
		this.m_PointSetContainer.add(d);
	}


	/**
	 *
	 */
	public void addGraph(int GraphLabel_1, int GraphLabel_2) {
		getGraphPointSet(GraphLabel_1).addGraph(getGraphPointSet(GraphLabel_2), this.getDMeasures());
	}

	/**
	 *
	 */
	/**
	 * The Graph identified with GraphLabel is erased from the plot
	 * @param GraphLabel
	 */
	public void clearGraph(int GraphLabel) {
		getGraphPointSet(GraphLabel).removeAllPoints();
		m_PointSetContainer.remove(getGraphPointSet(GraphLabel));
		repaint();
	}

	/**
	 *
	 */
	public void changeColorGraph(int GraphLabel) {
		Color col = getGraphPointSet(GraphLabel).getColor();
		if (col == Color.black)
			col = Color.red;
		else
			if (col == Color.red)
				col = Color.blue;
			else
				if (col == Color.blue)
					col = Color.red;
				else
					if (col == Color.red)
						col = Color.black;
		getGraphPointSet(GraphLabel).setColor(col);
		repaint();
	}


	/**
	 * @author Nastasja Trunk
	 * Function to change the Color of a graph identified by GraphLabel to the given color
	 * @param GraphLabel
	 * @param color
	 */
	public void changeColorGraph(int GraphLabel, Color color){
		getGraphPointSet(GraphLabel).setColor(color);
		repaint();
	}

	/**
	 * Function to return the color of a Graph intended for the creation of a legend
	 * @author Nastasja Trunk
	 * @param GraphLabel
	 * @return
	 */
	public Color getColorGraph(int GraphLabel){
		return getGraphPointSet(GraphLabel).getColor();	
	}

	/**
	 *
	 */
	public void clearGraph(int x, int y) {
		int index = getNearestGraphIndex(x, y);
		if (index == -1)
			return;
		int GraphLabel = ((GraphPointSet) (this.m_PointSetContainer.get(index))).getGraphLabel();
		clearGraph(GraphLabel);
	}

	/**
	 *
	 */
	public void changeColorGraph(int x, int y) {
		int index = getNearestGraphIndex(x, y);
		if (index == -1)
			return;
		int GraphLabel = ((GraphPointSet) (this.m_PointSetContainer.get(index))).getGraphLabel();
		changeColorGraph(GraphLabel);
	}

	/**
	 * @author Nastasja Trunk
	 * @param x
	 * @param y
	 * @param color
	 * This function was added to enable the color change of a plot if the color was choosen by the user
	 */
	public void changeColorGraph(int x, int y, Color color){
		int index = getNearestGraphIndex(x, y);
		if (index == -1)
			return;
		int GraphLabel = ((GraphPointSet) (this.m_PointSetContainer.get(index))).getGraphLabel();
		changeColorGraph(GraphLabel, color);
	}

	/**
	 *
	 */
	public void removePoint(int x, int y) {
		DPoint point = getNearestDPoint(x, y);
		int index = getNearestGraphIndex(x, y);
		if (index == -1 || point == null)
			return;
		GraphPointSet pointset = (GraphPointSet) (this.m_PointSetContainer.get(index));
		pointset.removePoint(point);
	}

	/**
	 *
	 */
	public void setInfoString(int GraphLabel, String Info, float stroke) {
		getGraphPointSet(GraphLabel).setInfoString(Info, stroke);

	}

	/**
	 *
	 */
	public void clearAll() {
		this.removeAllDElements();
		for (int i = 0; i < m_PointSetContainer.size(); i++)
			((GraphPointSet) (m_PointSetContainer.get(i))).removeAllPoints();
		m_PointSetContainer.clear();
	}

	/**
	 *
	 */
	private GraphPointSet getGraphPointSet(int GraphLabel) {
		for (int i = 0; i < m_PointSetContainer.size(); i++) {
			if (m_PointSetContainer.get(i)instanceof GraphPointSet) {
				GraphPointSet xx = (GraphPointSet) (m_PointSetContainer.get(i));
				if (xx.getGraphLabel() == GraphLabel)
					return xx;
			}
		}
		// create new GraphPointSet
		GraphPointSet NewPointSet = new GraphPointSet(GraphLabel, this);
		//NewPointSet.setStroke(new BasicStroke( (float)1.0 ));
		m_PointSetContainer.add(NewPointSet);
		return NewPointSet;
	}
	int size = 6;
	/**
	 *
	 */
	public void setUnconnectedPoint(double x, double y, int GraphLabel) {
		if (m_log == true && y <= 0.0) {
			System.out.println("Warning !!!y value <0 and log !! y " + y);
			y = 1;
		}
		this.getGraphPointSet(GraphLabel).addDPoint(x, y);
		this.getGraphPointSet(GraphLabel).setConnectedMode(false);
		repaint();
	}

	Color[] Colors = new Color[] {Color.black, Color.red, Color.blue, Color.green,Color.magenta, Color.orange, Color.pink, Color.yellow};

	public void setGraphColor(int GraphLabel,int colorindex) {
		this.getGraphPointSet(GraphLabel).setColor(Colors[colorindex%Colors.length]);
	}


	/**
	 * function to change the color of a graph just using a specific color
	 * @param GraphLabel
	 * @param color
	 * @author Nastasja Trunk
	 */
	public void setGraphColor(int GraphLabel, Color color){
		this.getGraphPointSet(GraphLabel).setColor(color);
	}
	/**
	 *
	 */
	public int getContainerSize() {
		return m_PointSetContainer.size();
	}

	/**
	 *
	 */
	public DPointSet[] printPoints() {
		DPointSet[] ret = new DPointSet[m_PointSetContainer.size()];
		for (int i = 0; i < m_PointSetContainer.size(); i++) {
			System.out.println("");
			System.out.println("GraphPointSet No " + i);

			ret[i] = ((GraphPointSet) m_PointSetContainer.get(i)).printPoints();
		}
		return ret;

	}

	public DPointSet printPoints(int i) {
		//for (int i = 0; i < m_PointSetContainer.size();i++) {
		System.out.println("");
		System.out.println("GraphPointSet No " + i);

		return ((GraphPointSet) m_PointSetContainer.get(i)).printPoints();
		//}
	}

	/**
	 *
	 */
	public void toggleLog() {
		//System.out.println("ToggleLog log was: "+m_log);
		if (m_log == false) {
			setMinRectangle(0.001, 0.001, 1, 1);
			//setVisibleRectangle(  0.001, 0.001, 100000, 1000 );
			setYScale(new Exp());
			m_Border.setSrcdY(Math.log(10));
			((java.text.DecimalFormat) m_Border.format_y).applyPattern("0.###E0");
			m_log = true;
		} else {
			m_log = false;
			setYScale(null);
			ScaledBorder buffer = m_Border;
			m_Border = new ScaledBorder();
			m_Border.x_label = buffer.x_label; //"App. " + Name + " func. calls";
			m_Border.y_label = buffer.y_label; //"fitness";
			setBorder(m_Border);
		}
		repaint();
	}

	/**
	 * Causes all PointSets to interupt the connected painting at the
	 * current position.
	 */
	public void jump() {
		for (int i = 0; i < m_PointSetContainer.size(); i++)
			((GraphPointSet) (m_PointSetContainer.get(i))).jump();
	}

	/**
	 */
	public Object openObject() {
		if (m_FileChooser == null)
			createFileChooser();
		int returnVal = m_FileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selected = m_FileChooser.getSelectedFile();
			try {
				ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(new FileInputStream(selected)));
				Object obj = oi.readObject();
				oi.close();

				Object[] objects = (Object[]) obj;
				for (int i = 0; i < objects.length; i++) {
					GraphPointSet xx = ((GraphPointSet.SerPointSet) objects[i]).getGraphPointSet();
					xx.initGraph(this);
					this.m_PointSetContainer.add(xx);
				}
				repaint();
				return obj;
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"Couldn't read object: "
						+ selected.getName()
						+ "\n" + ex.getMessage(),
						"Open object file",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return null;
	}

	public void setAntalias(boolean a)
	{
		this.getDMeasures().setAntialias(a);
		((ScaledBorder)this.getBorder()).setAntialias(a);
	}



	public boolean getAntialias()
	{
		return this.getDMeasures().getAntialias();
	}

	public void setAlpha(float f)
	{
		this.getDMeasures().setAlpha(f);
	}

	public float getAlpha()
	{
		return this.getDMeasures().getAlpha();
	}

	/**
	 *
	 */
	 public void saveObject() {
		 Object[] object = new Object[m_PointSetContainer.size()];
		 for (int i = 0; i < m_PointSetContainer.size(); i++) {
			 object[i] = ((GraphPointSet) m_PointSetContainer.get(i)).getSerPointSet();
		 }
		 if (m_FileChooser == null)
			 createFileChooser();
		 int returnVal = m_FileChooser.showSaveDialog(this);
		 if (returnVal == JFileChooser.APPROVE_OPTION) {
			 File sFile = m_FileChooser.getSelectedFile();
			 try {
				 ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(sFile)));
				 oo.writeObject(object);
				 oo.close();
			 } catch (IOException ex) {
				 JOptionPane.showMessageDialog(this,
						 "Couldn't write to file: "
						 + sFile.getName()
						 + "\n" + ex.getMessage(),
						 "Save object",
						 JOptionPane.ERROR_MESSAGE);
			 }
		 }
	 }

	 /**
	  *
	  */
	 protected void createFileChooser() {
		 m_FileChooser = new JFileChooser(new File("/resources"));
		 m_FileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	 }

	 /** Add a popup menu for displaying certain information.
	  */
//	 
//	 private void addPopup() {
//		 addMouseListener(new MouseAdapter() {
//			 @Override
//			 public void mouseClicked(MouseEvent e) {
//				 if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
//					 // do nothing
//				 } else {
//					 JPopupMenu GraphMenu = new JPopupMenu();
//					 m_x = e.getX();
//					 m_y = e.getY();
//
//					 // The first info element
//					 JMenuItem Info = new JMenuItem("Graph Info: " + getGraphInfo(e.getX(), e.getY()));
//					 Info.addActionListener(new ActionListener() {
//						 //
//						 public void actionPerformed(ActionEvent ee) {
//							 DPoint temp = FunctionArea.this.getDMeasures().getDPoint(FunctionArea.this.m_x, FunctionArea.this.m_y);
//							 DPointIcon icon1 = new DPointIcon() {
//								 public void paint(Graphics g) {
//									 g.drawLine( -2, 0, 2, 0);
//									 g.drawLine(0, 0, 0, 4);
//								 }
//
//								 public DBorder getDBorder() {
//									 return new DBorder(4, 4, 4, 4);
//								 }
//							 };
//							 temp.setIcon(icon1);
//							 FunctionArea.this.addDElement(temp);
//						 }
//					 });
//					 GraphMenu.add(Info);
//
//
//					 // darn this point is an empty copy !!
//					 DPoint point = getNearestDPoint(e.getX(), e.getY());
//
//					 if (point != null) {
//						 JMenuItem InfoXY = new JMenuItem("(" + point.x + "/" + point.y+")");
//						 Info.addActionListener(new ActionListener() {
//							 public void actionPerformed(ActionEvent ee) {
//							 }
//						 });
//						 GraphMenu.add(InfoXY);  
//
//
//
//					 }
//					 if (FunctionArea.this.m_PointSetContainer.size() > 0) {
//						 JMenuItem removeGraph = new JMenuItem("Remove graph");
//						 removeGraph.addActionListener(new ActionListener() {
//							 public void actionPerformed(ActionEvent ee) {
//								 clearGraph(FunctionArea.this.m_x, FunctionArea.this.m_y);
//							 }
//						 });
//						 GraphMenu.add(removeGraph);
//					 }
//					 if (FunctionArea.this.m_PointSetContainer.size() > 0) {
//						 JMenuItem changecolorGraph = new JMenuItem("Change color");
//						 changecolorGraph.addActionListener(new ActionListener() {
//							 public void actionPerformed(ActionEvent ee) {
//								 Color col = JColorChooser.showDialog(FunctionArea.this, "Choose a new graph color", Color.WHITE);
//
//								 changeColorGraph(FunctionArea.this.m_x, FunctionArea.this.m_y, col);
//							 }
//						 });
//						 GraphMenu.add(changecolorGraph);
//					 }
//					 if (FunctionArea.this.m_PointSetContainer.size() > 0) {
//						 JMenuItem removePoint = new JMenuItem("Remove point");
//						 removePoint.addActionListener(new ActionListener() {
//							 public void actionPerformed(ActionEvent ee) {
//								 removePoint(FunctionArea.this.m_x, FunctionArea.this.m_y);
//							 }
//						 });
//						 GraphMenu.add(removePoint);
//					 }
//					 //            if (isStatisticsGraph(e.getX(),e.getY())==true) {
//					 //              if (getVar(e.getX(),e.getY())==false) {
//					 //                JMenuItem showVar = new JMenuItem("Show varianz ");
//					 //	        showVar.addActionListener(new ActionListener() {
//					 //                //
//					 //		public void actionPerformed(ActionEvent ee) {
//					 //		  setVar(m_x,m_y,true);
//					 //		}
//					 //	      });
//					 //	      GraphMenu.add(showVar);
//					 //
//					 //              }
//					 //              else {
//					 //                JMenuItem hideVar = new JMenuItem("Hide varianz ");
//					 //	        hideVar.addActionListener(new ActionListener() {
//					 //                  //
//					 //		  public void actionPerformed(ActionEvent ee) {
//					 //		    setVar(m_x,m_y,false);
//					 //		  }
//					 //                });
//					 //                GraphMenu.add(hideVar);
//					 //              }
//					 //            }
//					 GraphMenu.show(FunctionArea.this, e.getX(), e.getY());
//				 }
//			 }
//		 });
//	 }



}
