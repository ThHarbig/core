package mayday.core.settings;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class HelpButton extends JLabel implements MouseListener {
	
	String text, name;
	Color c;
	Color defaultC = new Color(150,150,255);
	
	public HelpButton(String name, String helptext) {
		super("  ?  ");
		setForeground(Color.white);
		setOpaque(false);
		text=helptext;
		this.name=name;
		c = defaultC;
		addMouseListener(this);
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;		
		g2.setColor(c);
		g2.fillOval(0, 0, getWidth(), getHeight());
		super.paint(g);
	}

	public void mouseClicked(MouseEvent e) {
		JOptionPane.showMessageDialog(null, text, name, JOptionPane.INFORMATION_MESSAGE, null);
	}

	public void mouseEntered(MouseEvent e) {
		c = Color.BLUE;
		repaint();		
	}

	public void mouseExited(MouseEvent e) {
		c = defaultC;
		repaint();
	}

	public void mousePressed(MouseEvent e) {
		c = Color.BLACK;
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		c = defaultC;
		repaint();
	}

}
