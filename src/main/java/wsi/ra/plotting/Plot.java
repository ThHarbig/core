package wsi.ra.plotting;

/*
 * Title:        JavaEvA
 * Description:
 * Copyright:    Copyright (c) 2003
 * Company:      University of Tuebingen, Computer Architecture
 * @author Holger Ulmer, Felix Streichert, Hannes Planatscher
 * @version:  $Revision: 1.3 $
 *            $Date: 2008/12/17 14:37:45 $
 *            $Author: battke $
 */
/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.AWTException;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import wsi.ra.chart2d.DLegend;
import wsi.ra.chart2d.DPointSet;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/
/**
 */
/**
 *
 */
@SuppressWarnings("serial")
public class Plot implements PlotInterface, Serializable {

  public static boolean TRACE = false;
  private JFileChooser m_FileChooser;
  private JPanel m_ButtonPanel;
  private String m_PlotName;
  private String m_xname;
  private String m_yname;
  public FunctionArea m_PlotArea;
  protected JFrame m_Frame;
  
  /**
 * Boolean to toggle the display of a legend of the plot
 */
//  private boolean legend;
  private JPanel m_LegendPanel; 
  
  /**
 * Panel which includes the m_LegendPanel and the m_ButtonPanel
 */
private JPanel m_InformationPanel;
  /**
   *
   */
  public Plot(String PlotName,String xname,String yname,double[] x,double[] y) {
    if (TRACE) System.out.println("Constructor Plot "+PlotName);
    m_xname = xname;
    m_yname = yname;
    m_PlotName = PlotName;
    init();
    DPointSet points = new DPointSet();
    for (int i=0;i<x.length;i++) {
      points.addDPoint(x[i],y[i]);
    }
    m_PlotArea.addDElement(points);
  }
  /**
   *
   */
  public Plot(String PlotName,String xname,String yname, boolean init) {
    if (TRACE) System.out.println("Constructor Plot "+PlotName);
    m_xname = xname;
    m_yname = yname;
    m_PlotName = PlotName;
    if (init)
      init();
  }
  /**
   *
   */
  public Plot(String PlotName,String xname,String yname) {
  if (TRACE) System.out.println("Constructor Plot "+PlotName);
    m_xname = xname;
    m_yname = yname;
    m_PlotName = PlotName;
    init();
  }
  
  public void enableClosing(boolean enable) {
	  if (enable == true) {
		  m_Frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	  } else {
		  m_Frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);		  
	  }
	  
  }
  /**
   *
   */
  public void init() {
    m_Frame = new JFrame("Plot: "+m_PlotName);   

    m_ButtonPanel = new JPanel();
    m_PlotArea = new FunctionArea(m_xname,m_yname);
    
    m_LegendPanel = new JPanel();
    m_ButtonPanel.setLayout( new FlowLayout(FlowLayout.LEFT, 10,10));
    JButton ClearButton = new JButton ("Clear");
    ClearButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  clearAll();
	}
      });
    JButton LOGButton = new JButton ("Log/Lin");
      LOGButton.setToolTipText("Toggle between a linear and a log scale on the y-axis.");
    LOGButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_PlotArea.toggleLog();
	}
      });
    JButton ExportButton = new JButton ("Export");
      ExportButton.setToolTipText("Exports the graph data to a simple ascii file.");
    ExportButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_PlotArea.exportToAscii();
	}
      });
     // Test Thomas start

    // Test Thomas end
    JButton PrintButton = new JButton ("Print");
    PrintButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
          try {
            Robot robot = new Robot();
            // Capture a particular area on the screen
              int x = 100;
              int y = 100;
              int width = 200;
              int height = 200;
              Rectangle area = new Rectangle(x, y, width, height);
              BufferedImage bufferedImage = robot.createScreenCapture(area);

              // Capture the whole screen
              area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
              bufferedImage = robot.createScreenCapture(area);
              try {
         
              FileImageOutputStream out = new FileImageOutputStream(new File("test.jpeg"));
              ImageWriter encoder = (ImageWriter)ImageIO.getImageWritersByFormatName("JPEG").next();
              JPEGImageWriteParam param = new JPEGImageWriteParam(null);

              //param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
              //param.setCompressionQuality(compression);

              encoder.setOutput(out);
              encoder.write((IIOMetadata) null, new IIOImage(bufferedImage,null,null), param);
              out.close();
              } catch (Exception eee) {}


          } catch (AWTException ee) {
          ee.printStackTrace();
          }



          PrinterJob job = PrinterJob.getPrinterJob();
//          PageFormat format = job.defaultPage();
//          job.setPrintable(m_PlotArea, format);
//          if (job.printDialog()) {
//	    // If not cancelled, start printing!  This will call the print()
//	    // method defined by the Printable interface.
//	    try { job.print(); }
//	    catch (PrinterException ee) {
//              System.out.println(ee);
//              ee.printStackTrace();
//            }
//	  }

           ///////////////////////////////////////////////
          //PagePrinter pp = new PagePrinter(m_PlotArea,m_PlotArea.getGraphics(),job.defaultPage());
          //pp.print();
          //  public int print( Graphics g, PageFormat pf, int pi ){
//	  m_PlotArea.print(m_PlotArea.getGraphics(), new PageFormat(),0);
          // Obtain a java.awt.print.PrinterJob  (not java.awt.PrintJob)
	  //PrinterJob job = PrinterJob.getPrinterJob();
          // Tell the PrinterJob to print us (since we implement Printable)
	  // using the default page layout
          PageFormat page = job.defaultPage();

	  job.setPrintable(m_PlotArea, page);
          // Display the print dialog that allows the user to set options.
	  // The method returns false if the user cancelled the print request
	  if (job.printDialog()) {
	    // If not cancelled, start printing!  This will call the print()
	    // method defined by the Printable interface.
	    try { job.print(); }
	    catch (PrinterException ee) {
              System.out.println(ee);
              ee.printStackTrace();
            }
	  }
	}
      });
    JButton OpenButton = new JButton ("Open..");
    OpenButton.setToolTipText("Load a old plot");
    OpenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        m_PlotArea.openObject();
      }
    });
    JButton SaveButton = new JButton ("Save..");
    SaveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         m_PlotArea.saveObject();
      }
    });
    JButton SaveJPGButton = new JButton ("Save as JPG");
    SaveJPGButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
        String outfile ="";
        try {
            Robot       robot = new Robot();
            Rectangle   area;
            area        = m_Frame.getBounds();
            
            BufferedImage   bufferedImage   = robot.createScreenCapture(area);
            JFileChooser    fc              = new JFileChooser();
            if (fc.showSaveDialog(m_Frame) != JFileChooser.APPROVE_OPTION) return;
            System.out.println("Name " + outfile);
              try {
//                FileOutputStream fos = new FileOutputStream(fc.getSelectedFile().getAbsolutePath()+".jpeg");
//                BufferedOutputStream bos = new BufferedOutputStream(fos);
//                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
//                encoder.encode(bufferedImage);
//                bos.close();
                
            	  // Use the better, non proprietary API (see http://developer.classpath.org/mediation/ClasspathMigration#head-d4ee9efe53a641e29ffdcd96e985bf38bbc671c1)
            	  
                FileImageOutputStream out = new FileImageOutputStream(new File(fc.getSelectedFile().getAbsolutePath()+".jpeg"));
                ImageWriter encoder = (ImageWriter)ImageIO.getImageWritersByFormatName("JPEG").next();
                JPEGImageWriteParam param = new JPEGImageWriteParam(null);

//                param.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
//                param.setCompressionQuality(compression);

                encoder.setOutput(out);
                encoder.write((IIOMetadata) null, new IIOImage(bufferedImage,null,null), param);

                out.close();


                
              } catch (Exception eee) {}


          } catch (AWTException ee) {
            ee.printStackTrace();
          }
	   }
      });

    m_ButtonPanel.add(ClearButton);
    m_ButtonPanel.add(LOGButton);
    m_ButtonPanel.add(ExportButton);
    m_ButtonPanel.add(PrintButton);
    m_ButtonPanel.add(OpenButton);
    m_ButtonPanel.add(SaveButton);
    m_ButtonPanel.add(SaveJPGButton);
    //  getContentPane().smultetLayout( new GridLayout(1, 4) );
    
    //m_Frame.getContentPane().add(m_LegendPanel, "South");
    //m_Frame.getContentPane().add(m_ButtonPanel,"South");
    //test des panels
    //Change made by Nastasja Trunk 10.10.2007
    //the legendPanel was added to hold the legend of the plot and the InformationPanel is just there to get a nice layout
     m_LegendPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
     
     //This panel is created to hold the m_LegendPanel and the m_ButtonPanel to get everything nicely arranged
     m_InformationPanel = new JPanel();
     m_InformationPanel.setLayout(new BoxLayout(m_InformationPanel, BoxLayout.Y_AXIS));
     m_InformationPanel.add(m_LegendPanel, "North");
     m_InformationPanel.add(m_ButtonPanel, "South");
     m_Frame.getContentPane().add(m_InformationPanel, "South");
    //Change of this line on 13.09.2007 by Nastasja Trunk
    // the layout was changed from "North" to "Center" to get
    //the plotarea to adjust to windowsize changes not only in the 
    //width but also in height
    m_Frame.getContentPane().add(m_PlotArea,"Center");
    
    m_Frame.addWindowListener(new WindowAdapter() {
      @Override
	public void windowClosing(WindowEvent e) {
        m_Frame.dispose();
    	  //System.out.println("Plot closed");
      }
    });
    m_Frame.pack();
    m_Frame.setVisible(true);
  }

  /**
   * @author Nastasja Trunk
   * Adding  a legend to the plot
 * @param toggle
 */
public void setLegend(boolean toggle){
	m_LegendPanel.removeAll();
	if(toggle){
		DLegend tmp = new DLegend(m_PlotArea);
	    JLabel[] legendicons = tmp.getLegend();
	    for(int i = 0; i < legendicons.length; ++i){
	    	m_LegendPanel.add(legendicons[i]);
	    }
	   		
	}
	m_LegendPanel.add(new JLabel(" "));
		
	m_Frame.pack();
	
  }
  
  /**
   * Function to add multiple Points at one to the specified graph
   * @author Nastasja Trunk
 * @param x
 * @param y
 * @param GraphLabel
 */
public void setConnectedPoints(double[]x, double[] y, int GraphLabel){
	
	  for(int i = 0; i!= x.length; ++i){
		  m_PlotArea.setConnectedPoint(x[i], y[i], GraphLabel);
	  }
  }
  /**
   *
   */
  public void setConnectedPoint (double x,double y,int func) {
    m_PlotArea.setConnectedPoint(x,y,func);

  }


  /**
   *
   */
  public void addGraph (int g1,int g2) {
    m_PlotArea.addGraph(g1,g2);
  }
  /**
   *
   */
  public void setUnconnectedPoint (double x, double y,int GraphLabel) {
    m_PlotArea.setUnconnectedPoint(x,y,GraphLabel);
  }
  
  /**
   * @author Nastasja Trunk
 * @param x x_values of the added Points
 * @param y y_values of the added Points
 * @param GraphLabel Label of the graph the points belong to
 */
public void setUnconnectedPoints(double[] x, double[] y, int GraphLabel){
	  for(int i = 0; i!= x.length; ++i){
		  m_PlotArea.setUnconnectedPoint(x[i], y[i], GraphLabel);
	  }
  }

  /**
   *
   */
  public void clearAll () {
    m_PlotArea.clearAll();
    m_PlotArea.removeAllDElements();
    m_Frame.repaint();
  }
  /**
   *
   */
  public void clearGraph (int GraphNumber) {
    m_PlotArea.clearGraph(GraphNumber);
  }
   /**
   *
   */
  public void setInfoString (int GraphLabel, String Info, float stroke) {
    m_PlotArea.setInfoString(GraphLabel,Info,stroke);
    
  }
  /**
   *
   */
  public void jump () {
    m_PlotArea.jump();
  }
  /**
  */
  @SuppressWarnings("unchecked")
protected Object openObject() {
    if (m_FileChooser == null)
      createFileChooser();
    int returnVal = m_FileChooser.showOpenDialog(m_Frame);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File selected = m_FileChooser.getSelectedFile();
      try {
        ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(new FileInputStream(selected)));
        Object obj = oi.readObject();
        oi.close();
        Class ClassType = Class.forName("FunctionArea");
        if (!ClassType.isAssignableFrom(obj.getClass()))
            throw new Exception("Object not of type: " + ClassType.getName());
        return obj;
      } catch (Exception ex) {
	  JOptionPane.showMessageDialog(m_Frame,
					"Couldn't read object: "
					+ selected.getName()
					+ "\n" + ex.getMessage(),
					"Open object file",
					JOptionPane.ERROR_MESSAGE);
	}
    }
    return null;
  }
 /**
  *
  */
  protected void saveObject(Object object) {
    if (m_FileChooser == null)
      createFileChooser();
    int returnVal = m_FileChooser.showSaveDialog(m_Frame);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File sFile = m_FileChooser.getSelectedFile();
      try {
        ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(sFile)));
        oo.writeObject(object);
        oo.close();
      } catch (IOException ex) {
	  JOptionPane.showMessageDialog(m_Frame,
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
  /**
   *
   */
   public String getName() {
    return this.m_PlotName;
   }
  /**
  *
  */
  public FunctionArea getFunctionArea () {
    return m_PlotArea;
  }
  /**
   *
   */
  public void dispose() {
    m_Frame.dispose();
  }

  /**
   * Just for testing the Plot class.
   */
   public static void main( String[] args ){
    Plot plot = new Plot("Plot-Test","x-value","y-value");
   //plot.init();
    double[] x = new double[100000];
    double[] y = new double[100000];
    for(int i = 0; i != x.length; ++i){
    	x[i] = i;
    	y[i] = i+1;
    }
    
    plot.setConnectedPoints(x, y, 0);
    plot.getFunctionArea().changeColorGraph(0, Color.CYAN);
    plot.setInfoString(0, "blubb", 1);
    
   plot.setLegend(true);
    //plot.addGraph(1,2);
  }
}

