
package mayday.core.pluma.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;

public class ProgressSplash  {
	
	private JProgressBar status;
	private JLabel l_label;
	private boolean imageOK = false;
	private JWindow jwin = null;
	
	public ProgressSplash() {
		try {
			l_label = new JLabel();
			jwin = new JWindow();
		} catch (HeadlessException e) {
	    	System.err.println("No GUI (Linux without X11)?");
	    	return;
	    } catch (Throwable t) {
	    	System.err.println("Could not show splash screen - no GUI libraries??");
	    	return;
	    }
	}
	
	private void loadImage() {
		if (imageOK) return;
		FMFile splash = PluginManager.getInstance().getFilemanager().getFile(MaydayDefaults.SPLASH_SCREEN_IMAGE);
		BufferedImage bfr=null;
		InputStream splash_is;
		if (splash!=null) {
			splash_is = splash.getStream();
		} else {
			//nur ich darf das!
			splash_is = ProgressSplash.class.getClassLoader().getResourceAsStream(MaydayDefaults.SPLASH_SCREEN_IMAGE);
		}
		if (splash_is!=null) {
			try {
				bfr = ImageIO.read(splash_is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    if ( bfr != null ) 	    {
	    	ImageIcon ico = new ImageIcon(bfr);
	    	l_label.setIcon(ico);	    	
	    	l_label.setVerticalAlignment( JLabel.TOP );
	    	l_label.setHorizontalAlignment( JLabel.CENTER);
	    	MaydayDefaults.SPLASH_SCREEN_IMAGEICON = ico;
	    	imageOK=true;
	    } else {
		    l_label.setText("<html>Mayday<br>Microarray Data Analysis");
	    	l_label.setVerticalAlignment( JLabel.CENTER);
	    	l_label.setHorizontalAlignment( JLabel.CENTER);
	    }
	}
	
	@SuppressWarnings("deprecation")
	public void setVisible(boolean vis) {
		
		if (jwin==null)
			return;
		
		if (vis) { // build splash
					
			loadImage();			
			
			JPanel content = (JPanel)(jwin.getContentPane());
		    
		    status = new JProgressBar(0,100);
		    status.setAlignmentX(JProgressBar.CENTER_ALIGNMENT);		
		    
		    int l_height = 322 + status.getHeight() + 15;
		    int l_width = 240;
		    
		    // compute window coordinates (center window on screen)
		    try {
		    	jwin.setSize(l_width, l_height);
		        MaydayDefaults.centerWindowOnScreen(jwin);

		    	content.add( l_label, BorderLayout.CENTER );
		    	content.add( status, BorderLayout.SOUTH );
		    	content.setBorder( BorderFactory.createLineBorder( Color.black, 0 ) );
		    
		    	status.setIndeterminate(true);
		    		
		    } catch (HeadlessException e) {
		    	System.err.println("No GUI (Linux without X11)?");
		    }
		}
		jwin.setVisible(vis);
	}
	
	public void setProgress(int progress) {
		loadImage();
		if (status!=null) {
			status.setIndeterminate(false);
			status.setValue(progress);
		}
	}
	
}
