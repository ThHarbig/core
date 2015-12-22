package mayday.clustering.dbscan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mayday.core.gui.MaydayDialog;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.typed.DoubleSetting;

@SuppressWarnings("serial")
public class PlotEps extends MaydayDialog {
	double[] data;
	int WIDTH;
	int HEIGHT;
	double eps = Double.NaN;
	
	DoubleSetting d = new DoubleSetting("Maximal distance (Epsilon):",
			"You should pick the distance that corresponds to a visible 'kink' in the plot, i.e.\n" +
			"the distance at which the slope of the curve changes dramatically."
			,0.2,0.0,null,false,false);
	SettingComponent sc;
	
	public PlotEps(String s, double[] d, int width, int height){
		setTitle(s);
		data=d;
		WIDTH=width;
		HEIGHT=height;
	}
	
	public void initiate(){
		setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new PlotEpsPanel(), BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout());
        south.add((sc = d.getGUIElement()).getEditorComponent(), BorderLayout.CENTER);
        south.add(new JButton(new AbstractAction("Start clustering") {

			public void actionPerformed(ActionEvent e) {
				if (sc.updateSettingFromEditor(false)) {
					eps = d.getDoubleValue();
					dispose();
				}
				
			}
        	
        }), BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);
        setModal(true);
        setVisible(true);
        repaint();
	}
	
	public double getMaxValue(){
		double max=Double.NEGATIVE_INFINITY;
		for(int i=0; i<data.length; i++){
			if(data[i]>max)
				max=data[i];
		}
		return max;
	}
	
	public double getMinValue(){
		double min=Double.POSITIVE_INFINITY;
		for(int i=0; i<data.length; i++){
			if(data[i]<min)
				min=data[i];
		}
		return min;
	} 
	
	class PlotEpsPanel extends JPanel {
	
		public void paint(Graphics g){
			int w = getWidth();
			int h = getHeight();

			int leftborder=40;
			// solve transparent jframe problem (new in java 6?)
			g.setColor(Color.white);
			g.fillRect(0, 0, w, h);

			//draw coordinate system
			int xmax=(int)Math.round(data.length);
			g.setColor(Color.black);
			g.setFont(new Font("Arial",Font.PLAIN,9));
			g.drawLine(0,h-20,w,h-20);
			g.drawLine(leftborder,h,leftborder,0);
			double max = getMaxValue();
			double min = getMinValue();
			double range=max-min;

			NumberFormat nf = NumberFormat.getInstance();
			//nf.setMaximumFractionDigits(2);
			//draw y axis
			double step = range/20.0;
			for(double i=min; i<max; i+=step) {
				int k = (int)Math.round(h-20-(h-60)*(i-min)/range);
				String label = nf.format(i);
				g.drawString(label,15,k+5);
				g.drawLine(leftborder-2,k,leftborder+2,k);
			}

			if(data.length<2)
				return;

			int lastyPos  =(int)Math.round(h-20-(h-60)*(data[0]-min)/range);
			int lastxPos = leftborder;
			for(int i=1; i<data.length; i++){
				int ypos =(int)Math.round(h-20-(h-60)*(data[i]-min)/range);
				int xpos = (int) Math.round((w-(leftborder+10))*i/(xmax-1)+leftborder);
				g.drawLine(lastxPos,lastyPos,xpos,ypos);
				lastyPos=ypos;
				lastxPos=xpos;
			}
		}

	}
}
