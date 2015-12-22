/*
 * Created on 16.07.2004
 *
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JPanel;

import mayday.core.MaydayDefaults;

/**
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class ImagePane extends  JPanel //Canvas
{
    private Image image;
    
    public ImagePane(Image image)
    {
        this.image=image;
        
        
        Toolkit.getDefaultToolkit().prepareImage(image,100,100,this);
        
        this.setBackground(MaydayDefaults.Colors.PLOTTING_AREA);
        repaint();
    }
    
    
    /* (non-Javadoc)
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paintComponent(Graphics g)
    {
        ((Graphics2D)g).addRenderingHints(
                        new RenderingHints(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON)
         );
        g.drawImage(this.image,0,0,this);
    }
    
    
    public Dimension getPreferredSize()
    {
        return new Dimension(image.getWidth(this), image.getHeight(this));
    }
    
    
//    /* (non-Javadoc)
//     * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension)
//     */
//    public void setPreferredSize(Dimension preferredSize)
//    {
//        this.image=this.image.getScaledInstance(
//            preferredSize.width,
//            preferredSize.height,
//            Image.SCALE_DEFAULT
//            );
//    }
//    /* (non-Javadoc)
//     * @see java.awt.Component#setSize(java.awt.Dimension)
//     */
//    public void setSize(Dimension d)
//    {
//        this.setPreferredSize(d);
//    }
//    /* (non-Javadoc)
//     * @see java.awt.Component#setSize(int, int)
//     */
//    public void setSize(int width, int height)
//    {
//        this.setSize(new Dimension(width,height));
//    }
}
















