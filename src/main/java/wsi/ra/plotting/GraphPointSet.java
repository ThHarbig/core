package wsi.ra.plotting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import wsi.ra.chart2d.DArea;
import wsi.ra.chart2d.DMeasures;
import wsi.ra.chart2d.DPoint;
import wsi.ra.chart2d.DPointIcon;
import wsi.ra.chart2d.DPointSet;
/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/
/**
 */
/**
 *
 */
public class GraphPointSet {
	private String m_InfoString = "InfoString";
	private int m_GraphLabel;
	@SuppressWarnings("unchecked")
	private ArrayList m_PointSetContainer = new ArrayList();
	private float m_Stroke = (float) 1.0;
	private boolean m_isStatisticeGraph = false;
	//  private DPointSet m_PointSet_1;
	//  private DPointSet m_PointSet_2;
	//  private DPointSet m_PointSet_3;
	private DPointSetMultiIcon m_ConnectedPointSet;
	private DArea m_Area;
	private Color m_Color;
//	private DPointIcon m_Icon;
	private int m_CacheIndex = 0;
	private int m_CacheSize = 1;
	private double [] m_cachex;
	private double [] m_cachey;
	/**
	 *
	 */
	public GraphPointSet (/*int size*/int GraphLabel, DArea Area) {
		//System.out.println("Constructor GraphPointSet "+ GraphLabel);
		m_cachex = new double[m_CacheSize];
		m_cachey = new double[m_CacheSize];
		m_Area = Area;
		m_GraphLabel = GraphLabel;
		m_ConnectedPointSet = new DPointSetMultiIcon(100);
		//    m_PointSet_1 = new DPointSet(100);
		//    m_PointSet_2 = new DPointSet(100);
		//    m_PointSet_3 = new DPointSet(100);
		//
		//    DPointIcon icon1 = new DPointIcon(){
		//            public void paint( Graphics g ){
		//              g.drawLine(-2, 0, 2, 0);
		//              g.drawLine(0, 0, 0, 4);
		//            }
		//          public DBorder getDBorder(){ return new DBorder(4, 4, 4, 4); }
		//          };
		//    DPointIcon icon2 = new DPointIcon(){
		//            public void paint( Graphics g ){
		//              g.drawLine(-2, 0, 2, 0);
		//              g.drawLine(0, 0, 0, -4);
		//            }
		//            public DBorder getDBorder(){ return new DBorder(4, 4, 4, 4); }
		//        };
		//
		m_ConnectedPointSet.setStroke(new BasicStroke( m_Stroke ));
		m_ConnectedPointSet.setConnected(true);
		m_Color = Color.black;
		//    int colors = 5;
		//    int b = GraphLabel*(int)(255/10)%255;
		//    int r = (GraphLabel-colors)*(int)(255/10)%255;
		//    int g = (GraphLabel-2*colors)*(int)(255/10)%255;
		//    if (r<0) r = 0;
		//    if (g<0) g = 0;
		//    m_Color = new Color(r,g,b);

		if (GraphLabel == 0) m_Color = (Color.black);
		else
			if (GraphLabel == 1) m_Color = (Color.red);
			else
				if (GraphLabel == 2) m_Color = (Color.blue);
				else
					if (GraphLabel == 3) m_Color = (Color.red);
					else
						if (GraphLabel == 4) m_Color = (Color.black);
						else
							if (GraphLabel == 5) m_Color = (Color.red);
							else
								if (GraphLabel == 6) m_Color = (Color.blue);
								else
									if (GraphLabel == 7) m_Color = (Color.red);
									else
										if (GraphLabel == 8) m_Color = (Color.blue);
										else
											if (GraphLabel == 9) m_Color = (Color.red);
											else
												if (GraphLabel == 10) m_Color = (Color.black);

		m_ConnectedPointSet.setColor(m_Color);
		//    m_PointSet_1.setColor(m_Color);
		//    m_PointSet_2.setColor(m_Color);
		//    m_PointSet_3.setColor(m_Color);
		initGraph(Area);
	}
	/**
	 *
	 */
	private GraphPointSet (int size,int GraphLabel) {
		m_GraphLabel = GraphLabel;
		m_cachex = new double[m_CacheSize];
		m_cachey = new double[m_CacheSize];
		m_ConnectedPointSet = new DPointSetMultiIcon(100);
		//    m_PointSet_1 = new DPointSet(100);
		//    m_PointSet_2 = new DPointSet(100);
		//    m_PointSet_3 = new DPointSet(100);
		//
		//     DPointIcon icon1 = new DPointIcon(){
		//            public void paint( Graphics g ){
		//              g.drawLine(-2, 0, 2, 0);
		//              g.drawLine(0, 0, 0, 4);
		//            }
		//          public DBorder getDBorder(){ return new DBorder(4, 4, 4, 4); }
		//          };
		//    DPointIcon icon2 = new DPointIcon(){
		//            public void paint( Graphics g ){
		//              g.drawLine(-2, 0, 2, 0);
		//              g.drawLine(0, 0, 0, -4);
		//            }
		//            public DBorder getDBorder(){ return new DBorder(4, 4, 4, 4); }
		//        };
		//    m_PointSet_2.setIcon(icon1);
		//    m_PointSet_3.setIcon(icon1);
		//
		m_ConnectedPointSet.setStroke(new BasicStroke( m_Stroke ));

		m_ConnectedPointSet.setConnected(true);
		m_Color = Color.black;
		//    int colors = 5;
		//    int b = GraphLabel*(int)(255/10)%255;
		//    int r = (GraphLabel-colors)*(int)(255/10)%255;
		//    int g = (GraphLabel-2*colors)*(int)(255/10)%255;
		//    if (r<0) r = 0;
		//    if (g<0) g = 0;
		//    m_Color = new Color(r,g,b);

		if (GraphLabel == 0) m_Color = (Color.black);
		else
			if (GraphLabel == 1) m_Color = (Color.red);
			else
				if (GraphLabel == 2) m_Color = (Color.blue);
				else
					if (GraphLabel == 3) m_Color = (Color.red);
					else
						if (GraphLabel == 4) m_Color = (Color.black);
						else
							if (GraphLabel == 5) m_Color = (Color.red);
							else
								if (GraphLabel == 6) m_Color = (Color.blue);
								else
									if (GraphLabel == 7) m_Color = (Color.red);
									else
										if (GraphLabel == 8) m_Color = (Color.blue);
										else
											if (GraphLabel == 9) m_Color = (Color.red);
											else
												if (GraphLabel == 10) m_Color = (Color.black);

		m_ConnectedPointSet.setColor(m_Color);
		//    m_PointSet_1.setColor(m_Color);
		//    m_PointSet_2.setColor(m_Color);
		//    m_PointSet_3.setColor(m_Color);
	}
	/**
	 *
	 */
	public DPointSet printPoints() {
		//   for (int i = 0; i < m_ConnectedPointSet.getSize();i++) {
		//      DPoint p = m_ConnectedPointSet.getDPoint(i);
		//      double x = p.x;
		//      double y = p.y;
		//      //System.out.println("point "+i+ " x= "+x+"y= "+y);
		//    }
		return m_ConnectedPointSet.getDPointSet();
	}
	/**
	 *
	 */
	public Color getColor()  {
		return m_ConnectedPointSet.getColor();
	}
	/**
	 *
	 */
	public void setColor(Color x)  {
		m_ConnectedPointSet.setColor(x);
		//    m_PointSet_1.setColor(x);
		//    m_PointSet_2.setColor(x);
		//    m_PointSet_3.setColor(x);
	}
	/**
	 *
	 */
	public DPointSet getConnectedPointSet()  {
		return m_ConnectedPointSet.getDPointSet();
	}
	public DPointSetMultiIcon getReference2ConnectedPointSet()  {
		return m_ConnectedPointSet;
	}

	/**
	 *
	 */
	public void initGraph (DArea Area) {
		m_Area = Area;
		m_Area.addDElement(m_ConnectedPointSet);
		((FunctionArea)m_Area).addGraphPointSet(this);
		//    m_Area.addDElement(m_PointSet_1);
		//    m_Area.addDElement(m_PointSet_2);
		//    m_Area.addDElement(m_PointSet_3);
		//     DPointIcon icon1 = new DPointIcon(){
		//            public void paint( Graphics g ){
		//              g.drawLine(-2, 0, 2, 0);
		//              g.drawLine(0, 0, 0, 4);
		//            }
		//          public DBorder getDBorder(){ return new DBorder(4, 4, 4, 4); }
		//          };
		//    DPointIcon icon2 = new DPointIcon(){
		//            public void paint( Graphics g ){
		//              g.drawLine(-2, 0, 2, 0);
		//              g.drawLine(0, 0, 0, -4);
		//            }
		//            public DBorder getDBorder(){ return new DBorder(4, 4, 4, 4); }
		//        };
	}
	/**
	 *
	 */
	public DPoint getNearestDPoint(DPoint p) {
		return m_ConnectedPointSet.getNearestDPoint(p);
	}

	/**
	 * Causes the PointSet to interupt the connected painting at the
	 * current position.
	 */
	public void jump() {
		m_ConnectedPointSet.jump();
	}
	/**
	 *
	 */
	public void removeAllPoints() {
		m_ConnectedPointSet.removeAllPoints();
		//    m_PointSet_1.removeAllPoints();
		//    m_PointSet_2.removeAllPoints();
		//    m_PointSet_3.removeAllPoints();
	}
	/**
	 *
	 */
	public void addDPoint(double x,double y) {
		//System.out.println(" "+x+" "+y);
		if (m_CacheIndex==m_CacheSize) {
			for (int i=0;i<m_CacheSize;i++) {
				m_ConnectedPointSet.addDPoint(m_cachex[i],m_cachey[i]);
			}
			m_ConnectedPointSet.addDPoint(x,y);
			m_CacheIndex=0;
		}
		else {
			m_cachex[m_CacheIndex]=x;
			m_cachey[m_CacheIndex]=y;
			m_CacheIndex++;
		}
	}
	/**
	 *
	 */
	public void addDPoint(DPoint p)  {
		m_ConnectedPointSet.addDPoint(p);
	}

	/**
	 *
	 */
	public void setIcon(DPointIcon p)  {
//		this.m_Icon = p;
		this.m_ConnectedPointSet.setIcon(p);
	}
	/**
	 *
	 */
	public void setConnectedMode(boolean p)  {
		m_ConnectedPointSet.setConnected(p);
	}
	/**
	 *
	 */
	public boolean isStatisticsGraph() {
		return m_isStatisticeGraph;
	}

	/**
	 *
	 */
	public PointSet getPointSet() {
		return new PointSet (this.m_ConnectedPointSet.getDPointSet());
	}
	/**
	 *
	 */
	public void removePoint(DPoint x) {
		System.out.println("removePoint "+x.x+ " "+ x.y);
		DPoint[] buf = new DPoint[m_ConnectedPointSet.getSize()];
		for (int i=0; i<m_ConnectedPointSet.getSize();i++) {
			buf[i] = m_ConnectedPointSet.getDPoint(i);
		}
		m_ConnectedPointSet.removeAllPoints();
		for (int i=0; i<buf.length;i++) {
			if(buf[i].x == x.x && buf[i].y == x.y)
				System.out.println("point found");
			else
				m_ConnectedPointSet.addDPoint(buf[i]);

		}
	}
	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public void addGraph (GraphPointSet set,DMeasures measures) {
		if (set.m_ConnectedPointSet.getSize()!=m_ConnectedPointSet.getSize() &&
				m_ConnectedPointSet.getSize()!=0) {
			System.out.println("WARNING addGraph not possible losrt last graph");
			System.out.println(" m_ConnectedPointSet.getSize() "+ m_ConnectedPointSet.getSize());
			return;
		}
		m_isStatisticeGraph = true;
		removeAllPoints();
		m_ConnectedPointSet.setColor(set.getColor());
		//    m_PointSet_1.setColor(m_ConnectedPointSet.getColor());
		//    m_PointSet_2.setColor(m_ConnectedPointSet.getColor());
		//    m_PointSet_3.setColor(m_ConnectedPointSet.getColor());
		m_PointSetContainer.add(set.getPointSet());
		int[] index = new int[m_PointSetContainer.size()];
		int[] GraphSize = new int[m_PointSetContainer.size()];
		for (int i=0;i<m_PointSetContainer.size();i++)
			GraphSize[i] = ((PointSet) m_PointSetContainer.get(i)).getSize();
		boolean doit = true;
		double minx =0;
		while ( doit ) {
			minx = ((PointSet) m_PointSetContainer.get(0)).m_X[index[0]];
//			int minindex = 0;
			//System.out.println("m_PointSetContainer.size()"+m_PointSetContainer.size());
			for (int i = 1;i<m_PointSetContainer.size();i++) {
				//System.out.println("i="+i);
				if (minx > ((PointSet) m_PointSetContainer.get(i)).m_X[index[i]]) {
//					minindex = i;
					minx =  ((PointSet) m_PointSetContainer.get(i)).m_X[index[i]];
				}
			}
			// Stelle minx wird gezeichnet. jetzt alle y werte dazu finden
			int numberofpoints =0;
			for (int i = 0;i<m_PointSetContainer.size();i++) {
				if (minx == ((PointSet) m_PointSetContainer.get(i)).m_X[index[i]])
					numberofpoints++;
			}
			double[] y = new double[numberofpoints];
			int c = 0;
			for (int i = 0;i<m_PointSetContainer.size();i++) {
				if (minx == ((PointSet) m_PointSetContainer.get(i)).m_X[index[i]]) {
					y[c]= ((PointSet) m_PointSetContainer.get(i)).m_Y[index[i]];
					c++;
					index[i]++;
				}
			}
			double ymean =0;
			for (int i = 0;i<y.length;i++)
				ymean = ymean + y[i];
			ymean = ymean / y.length;
			// compute median double median = getMedian(y);
			addDPoint(minx,ymean);// System.out.println("ymean "+ymean+"  y.length "+ y.length);
			//addDPoint(minx,median);// System.out.println("ymean "+ymean+"  y.length "+ y.length);
			doit = false;
			for (int i=0;i<m_PointSetContainer.size();i++) {
				if (GraphSize[i] > index[i] ) {
					doit = true;
					break;
				}
			}
		}
		//    m_PointSet_2.removeAllPoints();
	}
	/**
	 *
	 */
	//  private double getMedian(double[] in) {
	//    double[] x = in.clone();
	//    double ret = 0;
	//    Arrays.sort(x);
	//    int m = (int)( x.length/2.0);
	//    return x[m];
	//  }
	/**
	 *
	 */
	public int getGraphLabel () {
		return m_GraphLabel;
	}
	//  /**
	//   *
	//   */
	//  public void setUnconnectedPoint (double x, double y) {
	//    m_PointSet_1.addDPoint(x,y);
	//  }
	/**
	 *
	 */
	public String getInfoString () {
		System.out.println("Reading in "+ this.m_GraphLabel +"...: "+this.m_InfoString);
		return m_InfoString;
	}
	/**
	 *
	 */
	public void setInfoString (String x, float stroke ) {
		m_InfoString=x;
		m_Stroke = stroke;
		System.out.println("GraphPointSet: setInfoString " + x + " " + m_GraphLabel);
		//setStroke(new BasicStroke( m_Stroke ));
	}
	/**
	 *
	 */
	public SerPointSet getSerPointSet () {
		SerPointSet ret= new SerPointSet(this);
		return ret;
	}
	/**
	 */
	/**
	 *
	 */
	@SuppressWarnings("serial")
	class SerPointSet implements Serializable {
		private String m_InfoString;
		private int m_GraphLabel;
		private Color m_Color;
		private float m_Stroke;
		//    private PointSet m_PointSet_1;
		//    private PointSet m_PointSet_2;
		//    private PointSet m_PointSet_3;
//		private PointSet m_ConnectedPointSet;
		//    private PointSet m_VarPointSetPlus;
		//    private PointSet m_VarPointSetMinus;
		private boolean m_isStatisticeGraph;
		//    private boolean m_showVarianz;
		/**
		 *
		 */
		public SerPointSet (GraphPointSet Source) {
			m_InfoString = Source.m_InfoString;
			m_GraphLabel = Source.m_GraphLabel;
			m_Color = Source.m_Color;
			m_Stroke = Source.m_Stroke;
			m_isStatisticeGraph = Source.m_isStatisticeGraph;

			// save the connected points
//			m_ConnectedPointSet = new PointSet(Source.getConnectedPointSet());
			//      m_PointSet_1 = new PointSet (Source.m_PointSet_1);
			//      m_PointSet_2 = new PointSet (Source.m_PointSet_2);
			//      m_PointSet_3 = new PointSet (Source.m_PointSet_3);
		}
		/**
		 *
		 */
		public GraphPointSet getGraphPointSet () {
			GraphPointSet ret = new GraphPointSet(10,m_GraphLabel);
			ret.setInfoString(this.m_InfoString,this.m_Stroke);
			ret.setColor(this.m_Color);
			ret.m_Color = m_Color;
			ret.m_Stroke = m_Stroke;
			ret.m_isStatisticeGraph = m_isStatisticeGraph;
			ret.m_ConnectedPointSet.setConnected(true);
			return ret;
		}
	}
	/**
	 *
	 */
	@SuppressWarnings("serial")
	class PointSet implements Serializable {
		private double[] m_X;
		private double[] m_Y;
		private Color m_Color;
		/**
		 *
		 */
		public PointSet (DPointSet pointset) {
			m_Color = pointset.getColor();
			m_X = new double[pointset.getSize()];
			m_Y = new double[pointset.getSize()];
			for (int i=0;i<pointset.getSize();i++) {
				DPoint point = pointset.getDPoint(i);
				m_X[i] = point.x;
				m_Y[i] = point.y;
			}
		}
		/**
		 *
		 */
		public DPointSet getDPointSet () {
			DPointSet ret = new DPointSet(100);
			ret.setColor(m_Color);
			for (int i=0;i<m_X.length;i++)
				ret.addDPoint(m_X[i],m_Y[i]);
			return ret;
		}
		/**
		 *
		 */
		public int getSize() {
			return m_X.length;
		}
		/**
		 *
		 */
		//    public DPointSet printPoints() {
		//     for (int i = 0; i < m_ConnectedPointSet.getSize();i++) {
		//        DPoint p = m_ConnectedPointSet.getDPoint(i);
		//        double x = p.x;
		//        double y = p.y;
		//        //System.out.println("point "+i+ " x= "+x+"y= "+y);
		//      }
		//		return m_ConnectedPointSet.getDPointSet();
		//	}

}
}
