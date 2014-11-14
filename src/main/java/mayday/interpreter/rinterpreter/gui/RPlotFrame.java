/*
 * Created on 16.07.2004
 *
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mayday.core.gui.MaydayFrame;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.RSettings;

/**
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class RPlotFrame extends MaydayFrame
{    
    private RSettings settings;
    private JTabbedPane tabbedPane;
//    private double aspectRatio=1.0;
    private int exceptionCounter=0;
    private StringBuffer exceptionMessages;
    private int plotCounter=0;
    
    
    
    public RPlotFrame(RSettings settings)
    {
        setTitle(RDefaults.Titles.RPLOTS_FRAME);
        this.settings=settings;
        this.exceptionMessages=new StringBuffer();
        this.tabbedPane=new JTabbedPane();
        
        init();
        
        if(this.plotCounter==0) this.setVisible(false);
    }
    
    private void init()
    {
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //prepare SVG plot files:
        SVGFileFilter svgFilter=new SVGFileFilter(this.settings);
        File[] svgFiles=new File(settings.getWorkingDir()).listFiles(svgFilter);
        prepareSVG(svgFiles);  //IMPROVEMENT: encapsulate this in something like an SVGImageReaderSpi; see ImageIO-API
        
        
        //get all currently created image-files:
        File[] files=new File(settings.getWorkingDir()).listFiles(new GraphicsFileFilter(this.settings));
 
        //if no plots found ignore the rest
        if(files==null || files.length==0) return;
        
        //for each File create the Image:
        for(int i=0; i!=files.length; ++i)
        {
            File imgfile=files[i];
            try
            {
                JPanel panel=new JPanel();
                panel.setBorder(
                    BorderFactory.createEmptyBorder(5,5,5,5)
                );
                
                if(svgFilter.accept(imgfile))
                {
                    panel.add(new SVGPlotPanel(imgfile));
                }else
                {
                    panel.add(new RPlotPanel(imgfile));
                }
                
                this.tabbedPane.add(
                    imgfile.getName(),	//title
                    panel
                );
                ++this.plotCounter;
            }catch(Exception ex)
            {
                ++this.exceptionCounter;
                this.exceptionMessages.append(
                    ex.getMessage()+
                    " File: " +
                    files[i].getName()+
                   	"\n");
                continue;
            }
        }
        
        this.getContentPane().add(this.tabbedPane);
        //this.tabbedPane.validate();  //recomputes the sizes of the sub-components
        
        pack();
        
        //set size and locate;
        Dimension sz=this.tabbedPane.getPreferredSize();
        sz.height+=30;
        this.setSize(sz);
        
        
        //this.aspectRatio=(double)sz.width/(double)sz.height;
        
        //this.addComponentListener(new ResizeListener());
        
        this.setVisible(true);
        
        //this.setResizable(false);
    }
    
    private void prepareSVG(File[] svgfiles)
    {
        int counter=0;
        for(int i=0; i!=svgfiles.length;++i)
        {     
            //Read the SVG File
            File svg=svgfiles[i];
            String RPLOT="Rplot";
        
            //get the XML-documents contained in there
            //IMPROVEMENT: make this more common
            try
            {
                String line;
//                StringBuffer buf=new StringBuffer();

                BufferedReader rd=new BufferedReader(new FileReader(svg));
                PrintWriter wr=null;
                while((line=rd.readLine())!=null)
                {
                    if(line.trim().matches(".*<\\?xml.*\\?>"))
                    {
                        //cleanly close the PrintWriter
                        if(wr!=null)
                        {
                            wr.flush();
                            wr.close();
                        }
                        
                        //create a new File 
                        File f;
                        do 
                        {
                            ++counter;
                            String num=RDefaults.fillFront(""+counter,3,'0');
                            f=new File(settings.getWorkingDir(),RPLOT+num+".svg");
                        }while(f.exists());
                        
                        f.createNewFile();
                        
                        //initialize new PrintWriter
                        try
                        {
                            wr=new PrintWriter(new FileWriter(f));
                        }catch(IOException ex)
                        {
                            wr=null;
                        }
                    }
                    
                    //write the line to the created file
                    if(wr!=null)
                    {
                        wr.println(line);
                    }
                }
                if(wr!=null)
                {
                    wr.close();
                }
                
                rd.close();
                svgfiles[i].delete();
            }catch(Exception ex)
            {
                ++this.exceptionCounter;
                this.exceptionMessages.append(ex.getMessage()+"\n");
                continue;
            }
        }       
    }
    
    
    
    
    
    public class NoGraphicsException extends RuntimeException
    {
        public NoGraphicsException(String message)
        {
            super(message);
        }
        public NoGraphicsException(String message, Throwable cause)
        {
            super(message, cause);
        }
        public NoGraphicsException(Throwable cause)
        {
            super(cause);
        }
        public NoGraphicsException()
        {
            super();
        }
    }
    
    public class ResizeListener implements ComponentListener
    {
         /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
         */
        public void componentHidden(ComponentEvent e)
        {
            //nothing to do
        }
        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
         */
        public void componentMoved(ComponentEvent e)
        {
            //nothing to do
        }
        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
         */
        public void componentResized(ComponentEvent e)
        {
//            Dimension sz=getSize();
//            
//            if(Math.abs(sz.height-this.lastSz.height)
//                    >
//               Math.abs(sz.width-this.lastSz.width))
//            {
//                sz.width=(int)(sz.height*aspectRatio);
//            }else
//            {
//                sz.height=(int)(sz.width/aspectRatio);
//            }
//            this.lastSz=sz;
//            setSize(sz.width,sz.height);
            pack();
            //repaint();
        }
        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
         */
        public void componentShown(ComponentEvent e)
        {
            //nothing to do

        }
    }
    /**
     * @return Returns the exceptionCounter.
     */
    public int getExceptionCounter()
    {
        return exceptionCounter;
    }
    
    public String getExceptionMessages()
    {
        return
        	"RPlotFrame - Exceptions:\n"+        
        	this.exceptionMessages.toString();
    }
}
