package mayday.core.gui.components;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicLabelUI;

@SuppressWarnings("serial")
public class VerticalLabel extends JLabel {

	public VerticalLabel(String text, Icon icon, int horizontalAlignment, boolean clockwise) {
		super(text, icon, horizontalAlignment);
		setUI(new VerticalLabelUI(clockwise));		 
	}

	public VerticalLabel(String text, Icon icon, int horizontalAlignment) {
		this(text, icon, horizontalAlignment, false);
	}

	public VerticalLabel(String text, int horizontalAlignment) {
		this(text, null, horizontalAlignment);
	}

	public VerticalLabel(String text) {
		this(text, null, LEADING);
	}


	public VerticalLabel(Icon image, int horizontalAlignment) {
		this(null, image, horizontalAlignment);
	}


	public VerticalLabel(Icon image) {
		this(null, image, CENTER);
	}


	public VerticalLabel() {
		this("", null, LEADING);
	}


	public VerticalLabel(boolean clockwise) {
		this("", null, LEADING, clockwise);
	}

	
	public static class VerticalLabelUI extends BasicLabelUI {

		private static Rectangle paintIconR = new Rectangle();
		private static Rectangle paintTextR = new Rectangle();
		private static Rectangle paintViewR = new Rectangle();
		private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

		protected boolean clockwise;

		public VerticalLabelUI( boolean clockwise )
		{
			super();
			this.clockwise = clockwise;
		}


		public Dimension getPreferredSize(JComponent c) 
		{
			Dimension dim = super.getPreferredSize(c);
			return new Dimension( dim.height, dim.width+1 ); // one pixel more to ensure round-UP when scaling
		}	

		public void paint(Graphics g, JComponent c) 
		{


			JLabel label = (JLabel)c;
			String text = label.getText();
			Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

			if ((icon == null) && (text == null)) {
				return;
			}

			FontMetrics fm = g.getFontMetrics();
			paintViewInsets = c.getInsets(paintViewInsets);

			paintViewR.x = paintViewInsets.left;
			paintViewR.y = paintViewInsets.top;

			// Use inverted height & width
			paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
			paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

			paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
			paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

			String clippedText = 
				layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

			Graphics2D g2 = (Graphics2D) g;
			AffineTransform tr = g2.getTransform();
			if( clockwise )
			{
				g2.rotate( Math.PI / 2 ); 
				g2.translate( 0, - c.getWidth() );
			}
			else
			{
				g2.rotate( - Math.PI / 2 ); 
				g2.translate( - c.getHeight(), 0 );
			}

			if (icon != null) {
				icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
			}

			if (text != null) {
				int textX = paintTextR.x;
				int textY = paintTextR.y + fm.getAscent();

				if (label.isEnabled()) {
					paintEnabledText(label, g, clippedText, textX, textY);
				}
				else {
					paintDisabledText(label, g, clippedText, textX, textY);
				}
			}


			g2.setTransform( tr );
		}
	}


}
