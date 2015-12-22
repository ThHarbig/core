package mayday.vis3.components;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;

@SuppressWarnings("serial")
/** A button that is not visible during export */
public class PlotButton extends JButton {

   public PlotButton() {
       this(null, null);
   }
   
   public PlotButton(Icon icon) {
       this(null, icon);
   }
   
   public PlotButton(String text) {
       this(text, null);
   }
   
   public PlotButton(Action a) {
       this();
	setAction(a);
   }

   public PlotButton(String text, Icon icon) {
       // Create the model
       setModel(new DefaultButtonModel());

       // initialize
       init(text, icon);
   }

	
	public void paint(Graphics g) {
		if (isShowing()) { 
			super.paint(g);
		}
	}

}
