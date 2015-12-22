package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.scale;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import mayday.vis3.plots.genomeviz.delegates.HelperClass;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.Spaces;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller_ppc;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.ChromosomeMarker;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.ScaleFunctions;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.ChromosomeMarker.TPU;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.PaintingPanel;

@SuppressWarnings("serial")
public class ScaleTrackPanel extends PaintingPanel {

	private int marker_diff = -1; // space between markers
	private int left_margin = 5; // left space
	private int right_margin = 5; // right space
	protected Spaces spaces = null;
	protected boolean pressedflag = false;
//	protected TreeMap<Integer,Double> val_range;
	protected Controller_ppc cpp;
	protected boolean drawline = false;
	protected Color transparentColor_red = new Color(250, 191, 191, 127);
	protected int[] range = new int[2];
	
	public ScaleTrackPanel(GenomeOverviewModel chromeModel,AbstractTrackPlugin tp) {
		super(chromeModel, tp);
		spaces = new Spaces();
		setBackground(Color.white);
		cpp = chromeModel.getController().getController_pp();
		resizeInternalPaintingPanel();	
//		val_range = new TreeMap<Integer, Double>();
	}
	
	public void setRange(){}

	protected void resizeInternalPaintingPanel() {
		super.resizeInternalPaintingPanel();
		
		if (spaces==null) {
			return;
		}
		
		double lowestChromePos = model.getViewStart();
		double highestChromePos = model.getViewEnd();
		marker_diff = ScaleFunctions.computeMarkerDiff();

		spaces.setUserSpace(getDefaultWidth() - left_margin - right_margin);
		spaces.setValueSpace(highestChromePos - lowestChromePos);

	}
	
	protected int getDefaultWidth() {
		return model.getWidth_scala_pp();
	}
	
	protected int getDefaultHeight() {
		return model.getHeight_scalapaintingpanel();
	}

	protected void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		
		Rectangle r = model.getViewRectOfJLayeredPane();
//		r.x += model.getWidth_userpanel();
		((Graphics2D)g).setClip(r.intersection(((Graphics2D)g).getClipBounds()));
		
//		if (!isShowing())
//			paintComplete(g2);
//		else 
			paintRange(g2);
		
		if(drawline){
        	g2.setColor(transparentColor_red);
        	
        	int s = cpp.getStartDragged();
        	int e = cpp.getEndDragged();
        	if(s!=e){
        		if(cpp.getStartDragged()>cpp.getEndDragged()){
            		s = cpp.getEndDragged();
            		e = cpp.getStartDragged();
            	}
        		g2.fillRect(s, 0, (e-s+1), getHeight()-2);
        	}
        }
	}

//	private void paintComplete(Graphics2D g2) {
//		int width = model.getWidth_scala_pp();
//		int height = model.getHeight_scalapaintingpanel();
//		int startX = HelperClass.getXReducedStart(left_margin);  //only paint visible range except when exporting, then paint all
//		int endX = HelperClass.getXReducedEnd(width, right_margin);
//		Double left_bp = Double.NaN;
//		Double right_bp = Double.NaN;
//		int decimal_place = 2;
//		double start_bp = model.getViewStart();
//		double offset=(start_bp-1);
//		
//		
//		double tickMin = (double)model.getViewStart();
//		double tickMax = (double)model.getViewEnd();
//		int tickCount = Math.max((getDefaultWidth() - left_margin - right_margin) / marker_diff,2); 
//		 
//		g2.drawLine(startX, height - 5, endX,	height - 5);
//		 
//		TPU tick0 = ChromosomeMarker.tickmark_power_unit(0,tickMin, tickMax, tickCount);
//        tickCount = tick0.tickCount; //update to adapt to prettification
//        
//		ScaleFunctions.computeScaleValues(range, (double)left_bp, (double)right_bp, tickMin, tickMax, tickCount);
//		int digits = ScaleFunctions.computeDigits(range, tickMin, tickMax, tickCount);
//
//		
//		double x0 = left_margin + spaces.getFunctionValueOf(tick0.tick-offset);
//		decimal_place = computeDecimalPlace(x0, digits);
//		
//		String tick = String.format("%."+decimal_place+"f " + tick0.unit,(tick0.tick * tick0.power));
//
//		g2.drawLine((int) x0, height - 15, (int) x0, height - 5);
//		g2.drawString(tick, (int) x0 - 2 + 1, height - 18);
//		
//		for(int i=1; i!= tickCount; i++){
//			TPU tickI = ChromosomeMarker.tickmark_power_unit(i,tickMin, tickMax, tickCount);
//			double d = tickI.tick;
//			decimal_place = computeDecimalPlace(d, digits);
//			double x1 = left_margin + spaces.getFunctionValueOf(d-offset);
//			double x2 = 0;
//			
//			if (x1 <= endX){			
//				
//				g2.drawLine((int) x1, height - 15, (int) x1, height - 5);
//
//				//-- draw the tick text --//
//				double value = (tickI.tick * tickI.power);
//				tick = String.format("%."+decimal_place+"f " + tickI.unit, value);
//				
//				int strWidth= g2.getFontMetrics().stringWidth(tick);
//				int tPos = (int) (x1 - 
//						(i != tickCount - 1 ? 
//								(strWidth >> 1) : 
//								(Math.min(strWidth >> 1, strWidth + Math.abs(width - x1) - 1))));
//				
//				g2.drawString(tick, tPos, height - 18);
//
//				//-- draw the large tick --//
//				x2 = ((int) (x0 + x1)) >> 1;
//				g2.drawLine((int) x2, height - 10, (int) x2, height - 5);
//
//				if (i == tickCount - 1) {
//					x2 = ((int) Math.abs(x0 - x1)) >> 1;
//					if ((int) (x1 + x2) > width - right_margin)
//						break;
//					g2.drawLine((int) (x1 + x2), height - 10, (int) (x1 + x2),
//							height - 5);
//				}
//				x0 = x1;
//			} else{
//				if (i == tickCount - 1) {
//					x2 = ((int) Math.abs(x0 - x1)) >> 1;
//					if ((int) (x1 + x2) > width - right_margin)
//						break;
//					g2.drawLine((int) (x1 + x2), height - 10, (int) (x1 + x2), height - 5);
//				}
//				break;
//			}
//		}
//	}

	private void paintRange(Graphics2D g2) {
		int width = model.getWidth_scala_pp();
		int height = model.getHeight_scalapaintingpanel();
		int leftpos = model.getVis_leftPos_x();  //only paint visible range except when exporting, then paint all
		int rightpos = model.getVis_rightPos_x();
		Long left_bp = model.getVisPos_low_bp();
		Long right_bp = model.getVisPos_high_bp();
		int decimal_place = 0;
		int startX = HelperClass.getXReducedStart(left_margin);
		int endX = HelperClass.getXReducedEnd(width, right_margin);
		double start_bp = model.getViewStart();
		double offset=(start_bp-1);
		
		int tickCount = Math.max((getDefaultWidth() - left_margin - right_margin) / marker_diff,2);
		double tickMin = (double)model.getViewStart();
		double tickMax = (double)model.getViewEnd();
		 


        if (!isShowing()) {
        	left_bp = 0l;
        	right_bp = model.getChromosomeEnd();
        }
        	

		
		g2.drawLine(Math.max(leftpos, startX), height - 5, Math.max((endX), rightpos),
				height - 5);
		double x0=0;
		String tick = "";
		
		TPU tick0 = ChromosomeMarker.tickmark_power_unit(0,tickMin,tickMax, tickCount);
        tickCount = tick0.tickCount; //update to adapt to prettification

       	ScaleFunctions.computeScaleValues(range, (double)left_bp, (double)right_bp, tickMin, tickMax, tickCount);
       	int digits = ScaleFunctions.computeDigits(range, tickMin, tickMax, tickCount);

		x0 = startX + spaces.getFunctionValueOf(tick0.tick-offset);
		if(x0>=leftpos){
			decimal_place = computeDecimalPlace(x0, digits);
			
			tick = String.format("%."+decimal_place+"f " + tick0.unit,(tick0.tick * tick0.power));
			g2.drawLine((int) x0, height - 15, (int) x0, height - 5);
			g2.drawString(tick, (int) x0 - 2 + 1, height - 18);
		}
		
		if(range[0]==0)
			range[0]=1;
		
		double lastTick = -Double.MIN_VALUE;
		
		Rectangle clipRect = g2.getClipBounds();
		
		range[1]++;
		for(int i = range[0]; i <= range[1]; i++){
			TPU tickI = ChromosomeMarker.tickmark_power_unit(i,tickMin,tickMax, tickCount);
			double d = tickI.tick;
			
			decimal_place = computeDecimalPlace(d, digits);
			
			double x1 = startX + spaces.getFunctionValueOf(d-offset);
			
			// ----- draw LARGE tick
			if (clipRect.contains(x1, height-10)) {
				g2.drawLine((int) x1, height - 15, (int) x1, height - 5);

				double value = (tickI.tick * tickI.power);
				tick = String.format("%."+decimal_place+"f " + tickI.unit, value);
				int strWidth= g2.getFontMetrics().stringWidth(tick);
				int tPos = (int) (x1 - 
						(i != tickCount - 1 ? 
								(strWidth >> 1) : 
									(Math.min(strWidth >> 1, strWidth + Math.abs(width - x1) - 1))));

				g2.drawString(tick, tPos, height - 18);
			}

			// ----- draw SMALL tick
			double x2 = ((int) (x0 + x1)) >> 1;

			if (clipRect.contains(x2, height-10)) {
				if(x2>=leftpos){
					g2.drawLine((int) x2, height - 10, (int) x2, height - 5);
				}

				if (tickI.tick-lastTick<11) { // single base view
					// draw single-base ticks
					double ticks = tickI.tick-lastTick;
					g2.setColor(Color.gray);
					double baseXdelta = ((x1-x0)/ticks);
					for (int bI=1; bI<ticks; ++bI) {
						if (ticks==10 && bI==5)
							continue;
						int baseX=(int)(x0+bI*baseXdelta);
						g2.drawLine((int)baseX , height - 7, (int) baseX, height - 5);
					}
					g2.setColor(Color.black);
				}

				if (i == tickCount - 1) {
					x2 = ((int) Math.abs(x0 - x1)) >> 1;
					if ((int) (x1 + x2) > endX)
						break;
					g2.drawLine((int) (x1 + x2), height - 10, (int) (x1 + x2), height - 5);
				}
			}
			
			x0 = x1;
			lastTick = tickI.tick;
				 
		}
	}

	private int computeDecimalPlace(double d, double digits) {
		if(d<1000){
			return 0;
		}else{
			double b = Math.floor(Math.log10(d));
			return (int)(Math.abs(b - digits));
		}
	}

	protected void createChromeLocationAtMousePosition(Point point) {
		double x = point.getX();
		if(x >= left_margin
				&& x <= model.getWidth_scala_pp()-right_margin){
			FromToPosition ftp = new FromToPosition();
			DataMapper.getBpOfView(getWidth()- left_margin - right_margin, model,
					point.getX()- left_margin, ftp);
			String toolBoxText = "";
			if (ftp.getFrom() == ftp.getTo()) {
				toolBoxText = "loc: " + ftp.getFrom()
						+ " (" + 1 + "bp)";
			} else {
				toolBoxText = "loc: " + ftp.getFrom() + "-" + ftp.getTo()
						+ " (" + ((ftp.getTo() - ftp.getFrom() + 1)) + "bp)";
			}
			this.setToolTipText(toolBoxText);
		} else{
			this.setToolTipText(null);
		}
	}

	public int getLeft_margin() {
		return left_margin;
	}

	public int getRight_margin() {
		return right_margin;
	}
	
	/**
	 * set true if the selected range should be drawn in scale-component, remember that the selected range is only drawn if
	 * CTRL is not been pressed (CTRL is been pressed for the movement of the tracks).
	 * @param v : true if selected range should be drawn
	 * @param ctrl_pressed : is zero if CRTL is not been pressed
	 */
	public void drawLine(boolean v, int ctrl_pressed) {
		drawline = v;
		
		//draw selected range only if ctrl not pressed
		if(ctrl_pressed!=0 && drawline == true)drawline = false;
		repaint();
	}
}
