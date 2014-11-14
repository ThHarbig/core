/*
 * Created on 19.07.2004
 *
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * Loads plots via the ImageIO-API. 
 * 
 * 
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class RPlotPanel extends JComponent
{
    public static final int INITIAL_HEIGHT=400;
    public static final int INITIAL_WIDTH=400;
    
    private Image image; //Image with the size from the file
    private File imageFile;
    private boolean updated;
    //private double zoomFactor=1;
    //private boolean antiAliasing=true;
    
    
    public RPlotPanel(File imageFile) throws Exception
    {
        this.imageFile=imageFile;
        this.setSize(INITIAL_WIDTH,INITIAL_HEIGHT);
        setPreferredSize(new Dimension(INITIAL_WIDTH,INITIAL_HEIGHT));
        this.updated=false;
        
        init();
    }
    
    protected void init() throws Exception
    {
        try
        {
        //Load the plot
        this.image=ImageIO.read(this.imageFile);
        int h=this.image.getHeight(this);
        int w=this.image.getWidth(this);
        //setSize(w,h);
        setPreferredSize(new Dimension(w,h));
        }catch(Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    
    public void paint(Graphics g)
    {
          	Image buf=null;
            Dimension sz=this.getSize();
            
            
            if(this.image!=null)
            {
                if(sz.height<=sz.width)
                {
                    sz.width=-1;
                }else
                {
                    sz.height=-1;
                }
                if(isUpdated())
                {
                    buf=this.image.getScaledInstance(sz.width,sz.height,Image.SCALE_DEFAULT);
                    setUpdated(false);
                }
                buf=this.image;
                g.drawImage(buf,0,0,this);
            }else
            {
                //buf=RDefaults.RSPLASH.getImage().getScaledInstance(sz.width,sz.height,Image.SCALE_DEFAULT);
                buf=this.createImage(sz.width,sz.height);
                String message="Could not create this plot!";
                int strWidth=getFontMetrics(getFont()).stringWidth(message);
                g.drawImage(buf,0,0,this);
                g.drawString(message,(sz.width-strWidth)/2,(sz.height)/2);
            }
        
    }
    
        
 
    /**
     * @return Returns the imageFile.
     */
    public File getImageFile()
    {
        return imageFile;
    }
    /**
     * @param imageFile The imageFile to set.
     */
    public void setImageFile(File imageFile)
    {
        this.imageFile = imageFile;
    }
 
    public Image getBuffer()
    {
        return this.image;
    }
    public void setBuffer(BufferedImage image)
    {
        this.image=image;
    }
    

    /**
     * @return Returns the image.
     */
    public Image getImage()
    {
        return image;
    }
    
    protected void setImage(Image image)
    {
        this.image=image;
    }
    /**
     * @return Returns the updated.
     */
    public boolean isUpdated()
    {
        return updated;
    }
    /**
     * @param updated The updated to set.
     */
    public void setUpdated(boolean updated)
    {
        this.updated = updated;
    }
}
