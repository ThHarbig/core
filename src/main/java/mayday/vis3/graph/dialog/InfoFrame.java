package mayday.vis3.graph.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;

import mayday.core.MaydayDefaults;

@SuppressWarnings("serial")
public class InfoFrame extends JWindow implements MouseListener, ActionListener
{
	//	private GraphCanvas parentCanvas;
	//	
	private JLabel messageLabel;
	private Font messageFont;	
	private Timer timer;

	
	public InfoFrame(Window parent, String message, ImageIcon img, int milliseconds) 
	{
		super(parent);
		System.out.println(parent.getBounds());
		setLayout(new BorderLayout(5,5));
		messageFont=new Font(Font.SANS_SERIF,Font.BOLD,60);		
		messageLabel=new JLabel(message);
		messageLabel.setFont(messageFont);

		setLayout(new BorderLayout(5,5));
		add(messageLabel,BorderLayout.SOUTH);
		add(new JLabel(img),BorderLayout.CENTER);
		timer = new Timer(milliseconds, this);
		pack();
		setBackground(new Color(10, 10, 10, 60));	
		setLocation(parent.getX()+parent.getWidth()/2-getWidth()/2, parent.getY()+parent.getHeight()/2-getHeight()/2);
	}
	
	@SuppressWarnings("deprecation")
	public InfoFrame(Window parent, ImageIcon img, int milliseconds) 
	{
		super(parent);
		System.out.println(parent.getBounds());
		setLayout(new BorderLayout(5,5));
		messageLabel=new JLabel(img);
		setLayout(new BorderLayout(5,5));
		add(messageLabel,BorderLayout.CENTER);
		timer = new Timer(milliseconds, this);
		pack();
		setBackground(Color.white);	
		
		MaydayDefaults.centerWindowOnScreen(this);
		
//		Dimension scr= Toolkit.getDefaultToolkit().getScreenSize();
//		setLocation(scr.width/2-getWidth()/2, scr.height/2-getHeight()/2);
	}
	
	public InfoFrame(Window parent, String message, int milliseconds) 
	{
		super(parent);
		messageFont=new Font(Font.SANS_SERIF,Font.BOLD,60);		
		messageLabel=new JLabel(message);
		messageLabel.setFont(messageFont);

		setLayout(new BorderLayout(5,5));
		add(messageLabel,BorderLayout.CENTER);

		timer = new Timer(milliseconds, this);
		pack();
		setBackground(new Color(10, 10, 10, 60));	
		setLocation(parent.getX()+parent.getWidth()/2-getWidth()/2, parent.getY()+parent.getHeight()/2-getHeight()/2);
	}

	@Override
	public void setVisible(boolean b) 
	{		
		super.setVisible(b);				
		timer.setInitialDelay(1000);
		timer.start(); 		
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		timer.stop();
		dispose();		
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		dispose();		
	}

	@Override
	public void paint(Graphics g) 
	{
		super.paint(g);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}


}
