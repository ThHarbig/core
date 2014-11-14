package mayday.vis3.plots.genomeviz.genomeoverview.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import mayday.vis3.plots.genomeviz.delegates.HelperClass;
import mayday.vis3.plots.genomeviz.genomeoverview.ConstantData;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.MyPlotScrollPane;
import mayday.vis3.plots.genomeviz.genomeoverview.Spaces;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller_header;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.ChromosomeMarker;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.ScaleFunctions;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.ChromosomeMarker.TPU;

@SuppressWarnings("serial")
public class ChromosomeHeaderPanel extends JPanel{

	protected GenomeOverviewModel model = null;
	
	private int marker_diff;		// space between markers
    private int left_margin;		// left space
    private int right_margin;		// right space
    
    protected Spaces spaces = new Spaces(); 
    protected Color transparentColor_gray=ConstantData.TRANSPARENT_COLOR_GREY;
    protected Color transparentColor_red=ConstantData.TRANSPARENT_COLOR_RED;
    
	protected MyPlotScrollPane myPlotScrollPane;
	protected Controller c;
	protected Controller_header ch;
	protected FromToPosition ftp;

	private boolean drawline = false;
	
	public ChromosomeHeaderPanel(GenomeOverviewModel ChromeModel, Controller controller, 
			MyPlotScrollPane MyPlotScrollPane, int LeftMargin, int RightMargin, int MarkerDiff){
		model = ChromeModel;
		c = controller;
		left_margin=LeftMargin;
		right_margin=RightMargin;
		marker_diff =MarkerDiff;
		if(c!=null){
			ch = controller.getController_chrh();
			addMouseListener(ch);
			addMouseMotionListener(ch);
		}
		
		myPlotScrollPane = MyPlotScrollPane;
//		this.setBorder(BorderFactory.createLineBorder(Color.blue,1));
		if(myPlotScrollPane != null){
			int width = myPlotScrollPane.getViewport().getWidth();
			int height = model.getHeight_chromePanel();
			setSize(new Dimension(width,height));
			setPreferredSize(new Dimension(width,height));
			setBackground(Color.white);
		}
		ftp = new FromToPosition();
	}	
	
//	protected UsablePanObj getUsableObj(long s_bp, long end_bp){
//		return compUsabParams(s_bp, end_bp, HelperClass.getWidthReduced(getWidth(), getLeft_margin(), getRight_margin()));
//	}
	
	protected void paintComponent(Graphics g) {
		
		if (!isShowing()) {
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(Color.LIGHT_GRAY);
        g2.setColor(Color.BLACK);
        
        int h = getHeight();
        int w = getWidth();
        int widthReduced=HelperClass.getWidthReduced(getWidth(), getLeft_margin(), getRight_margin());
        int x_r_s=HelperClass.getXReducedStart(getLeft_margin());
        int x_r_e=HelperClass.getXReducedEnd(getWidth(), getRight_margin());

		int tickCount = Math.max(widthReduced/ marker_diff,2);
		double tickMin = (double)model.getChromosomeStart();
		double tickMax = (double)model.getChromosomeEnd();
        int digits = 2;
        
        
        spaces.setUserSpace(widthReduced);
        spaces.setValueSpace(tickMax-tickMin);
        
    	
		 
		
        
//        double[] tickmarks = ChromosomeMarker.tickmarks(lowestChromePos,highestChromePos,  
//                Math.max((widthReduced)/marker_diff,2)); 

        g2.drawLine(x_r_s, h-5, x_r_e, h-5);
        g2.drawLine(x_r_s, h-4, x_r_e, h-4);


//        double[] powers = new double[tickmarks.length];
//        String[] units = ChromosomeMarker.units(tickmarks, powers,false,true);

        TPU tick0 = ChromosomeMarker.tickmark_power_unit(0,tickMin,tickMax, tickCount);
        tickCount = tick0.tickCount; //update to adapt to prettification
        
        double x0 = x_r_s + spaces.getFunctionValueOf(tick0.tick);
        
        digits = computeNecessaryDigits(x0, digits);
        
        
        String tick = String.format("%."+digits+"f "+tick0.unit,(tick0.tick*tick0.power));

        g2.drawLine((int)x0,h-15,(int)x0,h-5);
        g2.drawLine((int)x0+1,h-15,(int)x0+1,h-5);
        g2.drawString(tick,(int)x0-2+1,h-18);

//        int digits_diff = computeDiffDigits(tickmarks);
    	int digits_diff = ScaleFunctions.computeDigits(new int[]{0, tickCount-1}, tickMin, tickMax, tickCount);


        
        for(int i=1; i!=tickCount; ++i)
        {
            TPU tickI = ChromosomeMarker.tickmark_power_unit(i,tickMin,tickMax, tickCount);

            double d=tickI.tick;
            digits = computeNecessaryDigits(d, digits_diff);
            
            double x1 = x_r_s + spaces.getFunctionValueOf(d);
//            g2.setStroke(new BasicStroke(3f / w, BasicStroke.CAP_ROUND,
//    				BasicStroke.JOIN_ROUND));
            g2.drawLine((int)x1,h-15,(int)x1,h-5);
            g2.drawLine((int)x1+1,h-15,(int)x1+1,h-5);
            
            double value  = (tickI.tick*tickI.power);
            tick = String.format("%."+digits+"f "+tickI.unit, value);
            g2.drawString(
                    tick, 
                    (int)(x1- (  i!=tickCount-1 ?
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
            
            if(i==tickCount-1)
            {
                x2 = ((int)Math.abs(x0-x1)) >>1;
                if((int)(x1+x2)>x_r_e) break;
                g2.drawLine((int)(x1+x2), h-10, (int)(x1+x2), h-5);
            }
            
            if(x1>x_r_e) break;
            x0 = x1;
        }
        /* draw last line*/
        g2.drawLine((int)x_r_e-1,h-15,(int)x_r_e-1,h-5);
        g2.drawLine((int)x_r_e,h-15,(int)x_r_e,h-5);
        // draw rect on actual visible range
        drawVisibleRange(g2, h, w, tickMax);
        
        if(drawline){
        	g2.setColor(transparentColor_red);
        	
        	int s = ch.getStartDragged();
        	int e = ch.getEndDragged();
        	
        	if(s!=e){
        		if(ch.getStartDragged()>ch.getEndDragged()){
            		s = ch.getEndDragged();
            		e = ch.getStartDragged();
            	}
            	
            	g2.fillRect(s, 0, (e-s+1), getHeight());
        	}
        }
	}
	
	private int computeNecessaryDigits(double d, double digits) {
		if(d<1000){
			return 0;
		}else{
			double b = Math.floor(Math.log10(d));
			return (int)(Math.abs(b - digits));
		}
	}


//	 private static double computeMinAbsDifference(double data[])
//	    {
//		 double minAbsDifference = Double.MAX_VALUE;
//			Double prev = null;
//			
//
//			for (int i = 0; i < data.length; i++) {
//				if(prev!=null){
//					minAbsDifference = Math.min(minAbsDifference, Math.abs(data[i]- prev));
//				}else{
//					prev = data[i];
//				}
//			}
//			
//			return minAbsDifference;
//	    }
//
//	    private static int computeDiffDigits(double data[])
//	    {
//	    	double minAbsDifference = computeMinAbsDifference(data);
//
//	        double log = Math.log10(minAbsDifference);
//	        return (int)Math.floor(log);
//	    }
	/**
	 * draw rect on actual visible range.
	 * @param g2
	 * @param h
	 * @param w
	 * @param highestChromePos
	 */
	private void drawVisibleRange(Graphics2D g2, int h, int w,
			double highestChromePos) {	
		
		
		double reducedWidth_hp = HelperClass.getWidthReduced(getWidth(), getLeft_margin(), getRight_margin());
		int x_r_s = HelperClass.getXReducedStart(getLeft_margin());
		double visPos_low_bp = model.getVisPos_low_bp();
        double visPos_high_bp = model.getVisPos_high_bp();
//        System.out.println("visPos " + visPos_low_bp + " - " + visPos_high_bp);

		double visPos_from_x = DataMapper.getLeftmostPositionX(visPos_low_bp, reducedWidth_hp, model.getChromosomeStart(), model.getChromosomeEnd(), model, x_r_s);
		double visPos_to_x = DataMapper.getRightmostPositionX(visPos_high_bp, reducedWidth_hp, model.getChromosomeStart(), model.getChromosomeEnd(), model, x_r_s);
		
        g2.setColor(transparentColor_gray);
        // +1 because width is positions in panel
        g2.fillRect((int)visPos_from_x, 0, (int)HelperClass.getWidth((int)visPos_from_x, (int)visPos_to_x), h);
	}


//	private UsablePanObj compUsabParams(double from_bp, double to_bp, double widthReduced) {
//		
//		int from_x=Integer.MAX_VALUE;
//		int to_x=-Integer.MAX_VALUE;
//		
//		for(int i = 0; i < widthReduced; i++){
//			ftp.clear();
//			DataMapper.getBpOfChromosome((int)widthReduced,model,i,ftp);
//			
//			if(from_bp >=ftp.getFrom() && from_bp <= ftp.getTo()){
//				from_x=i;
//				break;
//			}
//		}
//		
//		for(int i = 0; i < widthReduced; i++){
//			ftp.clear();
//			DataMapper.getBpOfChromosome((int)widthReduced,model,i,ftp);
//			
//			if(to_bp >=ftp.getFrom() && to_bp <= ftp.getTo()){
//				to_x=i;
//				break;
//			}
//		}
//		return new UsablePanObj(HelperClass.getWidth(from_x, to_x), from_x, to_x);
//	}

	public static void main(String[] args) {
		System.out.println("Tester");
	}

	public int getLeft_margin() {
		return left_margin;
	}

	public int getRight_margin() {
		return right_margin;
	}


	public void drawLine(boolean v) {
		drawline = v;
		repaint();
	}
}
