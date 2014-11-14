package mayday.core.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

@SuppressWarnings("serial")
public class AboutDialog extends MaydayFrame {

	public AboutDialog() {
		super("About Mayday");
    	ImageIcon l_splashScreen = MaydayDefaults.SPLASH_SCREEN_IMAGEICON;

	    setLayout(new BorderLayout());
	    JLabel image = new JLabel(l_splashScreen);
	    image.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	    getContentPane().setBackground(Color.white);
	    add(image, BorderLayout.WEST);
		
	    /* Build Plugin Authors String */
	    String Authors_Plugins = "";
	    TreeSet<String> names = new TreeSet<String>();
	    for (String mc : PluginManager.getInstance().getMasterComponents()) {
	    	if (!mc.equals("Libraries")) {
		    	for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(mc)) {
		    		String a = pli.getAuthor().replace(" and ", ",");
		    		if (a.contains(",")) {
		    			String[] b = a.split(",");
		    			for (String c : b)
		    				names.add(c.trim());
		    		} else {
		    			names.add(a.trim());
		    		}
		    	}		    		
	    	}
	    }
	    int count=0;
	    for (String name : names) {
	    	if (!name.equals("()")) {	
	    		Authors_Plugins += (count==0?"":", ")+name;
	    		++count; 
	    		if (count==3) {
	    			Authors_Plugins+=", <br>";
	    			count=0;
	    		}
	    	}
	    }
	    		    
	    if (Authors_Plugins.endsWith(", <br>"))
	    	Authors_Plugins = Authors_Plugins.substring(0, Authors_Plugins.length()-6);
	    
	    String Authors = "<html><font face='Arial'><center><h3>"+MaydayDefaults.PROGRAM_FULL_NAME+"</h3><br>"+MaydayDefaults.PROGRAM_DESCRIPTION+"<br>"
	    			   + MaydayDefaults.AUTHORS + "<br><br><hr>" 
	    			   + "Mayday is free software licensed under GPLv2"		    			   
	    			   + "<br><br>"
	    			   + "<b>Core developers</b><br>" 
	    			   + MaydayDefaults.AUTHORS_CORE + "<br><br>"
	    			   + "<b>Plugins contributed by</b><br>"
	    			   + Authors_Plugins + "<br><br>"
	    			   + "<font size=-2>"+MaydayDefaults.AUTHORS_INSTITUTION+"</font></font></html>";
	    
	    JEditorPane jep = new JEditorPane();
	    jep.setContentType("text/html");
	    jep.setText(Authors);
	    jep.setEditable(false);
	    jep.setBorder(BorderFactory.createEmptyBorder(20, 20,20,20));
	    		    
	    
	    add(jep, BorderLayout.CENTER);
	    
	    JButton lbutton = new JButton(new AbstractAction("Show license information") {

			public void actionPerformed(ActionEvent e) {
				new LicenseDialog().setVisible(true);
			
			}
	    	
	    });
	    JButton okbutton = new JButton(new AbstractAction("OK") {

			public void actionPerformed(ActionEvent arg0) {
				dispose();					
			}
	    	
	    });
	    
	    Box bbox = Box.createHorizontalBox();
	    bbox.add(Box.createHorizontalGlue());
	    bbox.add(lbutton);
	    bbox.add(Box.createHorizontalStrut(15));
	    bbox.add(okbutton);
	    add(bbox, BorderLayout.SOUTH);
	    
	    pack();
	    setMaximumSize(new Dimension(800,800));
	    setResizable(false);
	    setVisible(true);
	    
	}
}
