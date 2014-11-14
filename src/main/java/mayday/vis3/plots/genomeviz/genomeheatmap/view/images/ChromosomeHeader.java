package mayday.vis3.plots.genomeviz.genomeheatmap.view.images;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.vis3.components.CenteredMiddleLayout;
import mayday.vis3.components.PlotButton;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.delegates.HelperClass;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.ChromosomeMarker;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.Spaces;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeheatmap.scrollpane.ChromosomeScrollPane;
import mayday.vis3.plots.genomeviz.genomeheatmap.usergestures.UserGestures;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;

@SuppressWarnings("serial")
public class ChromosomeHeader extends JPanel{
	
	protected GenomeHeatMapTableModel model = null;
	protected ChromosomeScrollPane chromosomeScrollPane;
	protected Controller c = null;
	
	private int marker_diff = 70;		// space between markers
    private int left_margin = 5;		// left space
    private int right_margin = 5;		// right space
    protected Spaces spaces = new Spaces(); 
    protected Color transparentColor = new Color(191, 191, 191, 127);
	
    protected LeftJButton buttonLeft;
    protected RightJButton buttonRight;
	
    protected final int HEIGHT_BUTTONS = 10;
    protected final int WIDTH_BUTTONS = 30;
    protected FromToPosition ftp;
    protected JLabel label = null;
    
	public ChromosomeHeader(GenomeHeatMapTableModel Model, Controller Controller, ChromosomeScrollPane ChromosomeScrollPane){
		super(new CenteredMiddleLayout());
		model = Model;
		c = Controller;
		chromosomeScrollPane = ChromosomeScrollPane;
		ftp = new FromToPosition();
		if(chromosomeScrollPane != null){
			int width = chromosomeScrollPane.getViewport().getWidth();
			int height = model.getHeight_chromePanel();
			this.setSize(new Dimension(width,height));
			this.setPreferredSize(new Dimension(width,height));
			this.setBackground(Color.white);
		}
		label = new JLabel("");
		label.setFont(new Font("Sans", Font.PLAIN, 18));
		label.setForeground(new Color(170, 170, 170, 0));
		add(label,"Middle");
		

		buttonLeft = new LeftJButton();
		buttonLeft.addActionListener(this.c.getC_dt());
		buttonLeft.setActionCommand(UserGestures.SCROLL_LEFT);

		buttonRight = new RightJButton();
		buttonRight.addActionListener(this.c.getC_dt());
		buttonRight.setActionCommand(UserGestures.SCROLL_RIGHT);
		
		this.add(buttonLeft);
		this.add(buttonRight);
		this.addMouseListener(c.getC_hp());
		
		if(model != null && model.getActualChromeData() != null){
			repositionButtons();
		}
	}
	
	public void repositionButtons(){
		if(!this.model.getActualChromeData().getKindOfData().equals(KindOfData.STANDARD)){
			buttonLeft.setBounds(5, 2, WIDTH_BUTTONS, HEIGHT_BUTTONS);
			buttonRight.setBounds(this.getWidth()-right_margin - WIDTH_BUTTONS, 2, WIDTH_BUTTONS, HEIGHT_BUTTONS);
		}
	}
    
	
	protected void paintComponent(Graphics g) {
		
//		if(label!=null){
//		label.setText("<html>"+model.getActualSpecies().getName() + " chromosome: " + model.getActualChrome().getId());
//		}
		
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(Color.LIGHT_GRAY);
        g2.setColor(Color.BLACK);
        
        int h = getHeight();
        int w = getWidth();
        
        double lowestChromePos = model.getChromosomeStart();
        double highestChromePos = model.getChromosomeEnd();
        
        if(highestChromePos>lowestChromePos){
        	spaces.setUserSpace(w-left_margin-right_margin);
            spaces.setValueSpace(highestChromePos-lowestChromePos);
            
            double[] tickmarks = ChromosomeMarker.tickmarks(lowestChromePos, 
            		highestChromePos,  
                    Math.max((w-left_margin-right_margin)/marker_diff,2)); 
           	//g2.fillRect(left_margin, h-4, w-right_margin, 2);
            g2.drawLine(left_margin, h-5, w-right_margin-1, h-5);
            g2.drawLine(left_margin, h-4, w-right_margin-1, h-4);

            double[] powers = new double[tickmarks.length];
            String[] units = ChromosomeMarker.units(tickmarks, powers,false,true);

            double x0 = left_margin + spaces.getFunctionValueOf(tickmarks[0]);
            String tick = String.format("%.2f "+units[0],(tickmarks[0]*powers[0]));

            g2.drawLine((int)x0,h-15,(int)x0,h-5);
            g2.drawLine((int)x0+1,h-15,(int)x0+1,h-5);
            g2.drawString(tick,(int)x0-2+1,h-18);

            for(int i=1; i!=tickmarks.length; ++i)
            {
                double d=tickmarks[i];
                
                double x1 = left_margin + spaces.getFunctionValueOf(d);
                if(x1>w-right_margin) break;
//                g2.setStroke(new BasicStroke(3f / w, BasicStroke.CAP_ROUND,
//        				BasicStroke.JOIN_ROUND));
                g2.drawLine((int)x1,h-15,(int)x1,h-5);
                g2.drawLine((int)x1+1,h-15,(int)x1+1,h-5);
                
                double value  = (tickmarks[i]*powers[i]);
                tick = String.format("%.2f "+units[i], value);
                g2.drawString(
                        tick, 
                        (int)(x1- (  i!=tickmarks.length-1 ?
                                g2.getFontMetrics().stringWidth(tick)>>1 :
                                Math.min(
                                    g2.getFontMetrics().stringWidth(tick)>>1,
                                    g2.getFontMetrics().stringWidth(tick)
                                    + Math.abs(getWidth()-x1) - 1
                                )
                        )), 
                        h-18);
                
                double x2 = ((int)(x0+x1)) >> 1;
                g2.drawLine((int)x2, h-10, (int)x2, h-5);
                g2.drawLine((int)x2+1, h-10, (int)x2+1, h-5);
                
                if(i==tickmarks.length-1)
                {
                    x2 = ((int)Math.abs(x0-x1)) >>1;
                    if((int)(x1+x2)>w-right_margin) break;
                    g2.drawLine((int)(x1+x2), h-10, (int)(x1+x2), h-5);
                }
                
                
                x0 = x1;
            }
            
            
            // draw rect on actual visible range
            drawVisibleRange(g2, h, w, highestChromePos);
        }
	}

	/**
	 * draw rect on actual visible range.
	 * @param g2
	 * @param h
	 * @param w
	 * @param highestChromePos
	 */
	private void drawVisibleRange(Graphics2D g2, int h, int w,
			double highestChromePos) {
		double reducedWidth_hp = getWidth()-getLeft_margin()-getRight_margin();
		double visPos_low_bp = model.getFirstVisiblePosition();
        double visPos_high_bp = model.getLastVisiblePosition();
		double xpos = -1.;
		double visPos_from_x = 0.;
		double visPos_to_x = 0.;
		
		for(int xPos_original = getLeft_margin(); xPos_original < (getWidth()-getRight_margin()); xPos_original++){
			xpos = HelperClass.getTranslatedX(getWidth(), getLeft_margin(), getRight_margin(), xPos_original);
			//new Mapper().translateXPosition(xPos_original, getLeft_margin(), getRight_margin(), getWidth());
			if(xpos>=0.){
				FromToPosition ftp = new FromToPosition();
				DataMapper.getDataForMousePos((int)reducedWidth_hp,model,xpos,ftp);
					if(ftp.getFrom()< visPos_low_bp){
						visPos_from_x = xPos_original;
					} else if(ftp.getFrom()== visPos_low_bp){
						visPos_from_x = xPos_original;
						break;
					} else {
						break;
					}
			}
		}
		
		for(int xPos_original = getLeft_margin(); xPos_original < (getWidth()-getRight_margin()); xPos_original++){
			xpos = HelperClass.getTranslatedX(getWidth(), getLeft_margin(), getRight_margin(), xPos_original);
//				new Mapper().translateXPosition(xPos_original, getLeft_margin(),getRight_margin(), getWidth());
			ftp.clear();
			DataMapper.getDataForMousePos((int)reducedWidth_hp,model,xpos,ftp);
				if(ftp.getFrom()<= visPos_high_bp){
					visPos_to_x = xPos_original;
				} else{
					break;
				}
		}
        
        if(visPos_from_x < left_margin){
        	visPos_from_x = left_margin;
        }
        
        if(visPos_to_x >= w-right_margin){
        	visPos_to_x = w-right_margin-1;
        }

        g2.setColor(transparentColor);
        // +1 because width is positions in panel
        g2.fillRect((int)visPos_from_x, 0, (int)(visPos_to_x-visPos_from_x+1), h);

	}

	public int getLeft_margin() {
		return left_margin;
	}

	public int getRight_margin() {
		return right_margin;
	}
	
	protected class LeftJButton extends PlotButton{

		public LeftJButton() {
			this.setBackground(Color.white);
			this.setBorder(null);
		}

		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.BLACK);

			g2d.fillRect(5, 3, getWidth(), 4);
			int[] xcoords = {0, 5, 5}; 
		    int[] ycoords = {getHeight()/2, 0, getHeight()}; 
		    g2d.fillPolygon (xcoords, ycoords, xcoords.length); 
		}
	}
	
	protected class RightJButton extends PlotButton{
		
		Insets ins;
		public RightJButton() {
			this.setBackground(Color.white);
			this.setBorder(null);
			ins = this.getInsets();
		}
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.BLACK);
			
			g2d.fillRect(0, 3, getWidth()-5, 4);
			int[] xcoords = {getWidth(), getWidth()-5, getWidth()-5}; 
		    int[] ycoords = {getHeight()/2, 0, getHeight()}; 
		    g2d.fillPolygon (xcoords, ycoords, xcoords.length); 
		}
	}
}
