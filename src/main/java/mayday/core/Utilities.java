/*
 * Created on 21.06.2005
 */
package mayday.core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import mayday.core.pluma.PluginInfo;

public final class Utilities
{
    public static String htmlColorString(Color c)
    {
        int i = (c.getRed()<<16)+(c.getGreen()<<8)+(c.getBlue());        
        return String.format("#%06X",i);
    }
    
    
    private static ImageIcon WARNING_IMAGE;
    public static Icon getWarningIcon()
    {
        return WARNING_IMAGE==null?
        		PluginInfo.getIcon("mayday/util/images/warning12.gif")
                : 
                WARNING_IMAGE;
    }
    
    public static byte[] fetchBytes(InputStream is)
    {
        try
        {
            ByteArrayOutputStream bis = new ByteArrayOutputStream();
            int x=0;
            while((x=is.read())>=0) bis.write(x);
            bis.close();
            return bis.toByteArray();
            
        }catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    
    public static BufferedImage loadImage(String path, BufferedImage defaultImage, ClassLoader loader)
    {
        try
        {
            defaultImage = ImageIO.read(
                loader.getResourceAsStream( path ));
            
        }catch(Exception ex)
        {
            defaultImage=new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);
        }   
        return defaultImage;
    }
    
    public static ImageIcon createIcon(BufferedImage img)
    {
        ImageIcon icon = null;
        try
        {
            icon = new ImageIcon(img);
        }catch(Exception ex)
        {}
        
        return icon;
    }
    
    public static String debugVersionInfo="No build info found";
       
}
