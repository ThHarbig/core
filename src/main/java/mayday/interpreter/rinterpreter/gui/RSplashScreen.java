package mayday.interpreter.rinterpreter.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.core.gui.MaydayDialog;
import mayday.interpreter.rinterpreter.RDefaults;

/**
 * RSplashScreen shows an initial SplashScreen when dialogs
 * come up before the RSettings dialog is not prepared yet.
 * 
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class RSplashScreen extends MaydayDialog
{
	public RSplashScreen()
	{
		super(RDefaults.MAYDAY_FRAME(),"R for Mayday");
		this.setUndecorated(true);
	}
	
	public void showSplash()
	{
		JLabel label=new JLabel(
			RDefaults.RSPLASH
		);
		label.setVerticalAlignment(JLabel.TOP);
		
		int w=300,h=300;
		this.setSize(w,h);
		//center on Page:
		
		this.getContentPane().add(label,BorderLayout.CENTER);
		((JPanel)this.getContentPane()).setBorder( 
			BorderFactory.createLineBorder( 
				Color.black, 
				1
			 ) 
		);
		
		pack();	
		setVisible(true);	
		toFront();
	}
}
