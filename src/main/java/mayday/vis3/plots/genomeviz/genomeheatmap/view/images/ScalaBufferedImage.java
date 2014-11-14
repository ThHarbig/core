package mayday.vis3.plots.genomeviz.genomeheatmap.view.images;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.TreeSet;

import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.ScaleImageModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.GetWholeChromePosition_Delegate;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;

public class ScalaBufferedImage {

	protected BufferedImage bufferedImage;
	protected GenomeHeatMapTableModel model;
	protected GetWholeChromePosition_Delegate gwcp = null;
	protected ScaleImageModel scalamodel = null;
	
	int rowWidth = 0;
	int rowHeight = 0;
	
	int row = -1;
	int lastRow = -1;
	
	int firstCell;
	int lastCell;

	
    private int left_margin = 0;		// left space
    private int right_margin = 0;		// right space
    private TreeSet<Double> tickmarks;
    private double v_big = 0.;
    private double v_small = 0.;
    protected Font displayFont = null;

	public ScalaBufferedImage(GenomeHeatMapTableModel Model, int FirstCell,int LastCell, int Row, int LastRow, ScaleImageModel Scalamodel, TreeSet<Double> Tickmarks) {
		model = Model;
		gwcp = new GetWholeChromePosition_Delegate();
		scalamodel = Scalamodel;
		
		int boxSizeX = model.getTableSettings().getBoxSizeX();
		firstCell = FirstCell;
		lastCell = LastCell;
		row = Row;
		lastRow = LastRow;
		
		left_margin = boxSizeX;
		right_margin = boxSizeX;
		
		rowWidth = boxSizeX*(model.getTableSettings().getNumberOfBoxesEachRow() + model.getNumberOfUnusedColumns());
		
		rowHeight = model.getTableSettings().getBoxSizeY();
		tickmarks = Tickmarks;
		
		v_big = scalamodel.getBig();
		v_small = scalamodel.getSmall();
		
		int val = rowHeight-5;
		displayFont = new Font("Sans", Font.PLAIN, val);
		
		updateBufferedImage(rowWidth, rowHeight);
	}

	

	protected void updateBufferedImage(int rowWidth, int rowHeight){
		bufferedImage = new BufferedImage(rowWidth,rowHeight,BufferedImage.TYPE_3BYTE_BGR);
		paint(bufferedImage.createGraphics(), rowWidth, rowHeight);
	}
	
	public BufferedImage getScalaImage() {
		return bufferedImage;
	}

	
	public Dimension getPreferredSize() {
	
		return null;
	}

	
	protected int posLine = 2;
	
	public void paint(Graphics g, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(displayFont);

		int hbt = 4;
		int hbts = 2;
		switch(model.getStyle()){
		case CLASSIC:
			g2d.setBackground(Color.BLACK);
			g2d.setColor(Color.white);
			break;
		case MODERN:
			g2d.setBackground(Color.WHITE);
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, width,height);
			g2d.setColor(Color.BLACK);
			break;
		}

		int first_bp = compute_bpOfCell_first(firstCell);
		int last_bp = compute_bpOfCell_last(lastCell);
		int actwidth = 1;


		if(row != lastRow){
			actwidth = (model.getTableSettings().getNumberOfBoxesEachRow())* model.getTableSettings().getBoxSizeX();			
		} else{
			last_bp = compute_bpOfCell_last(computeLastCell());
			int actualLastCell = computeLastCell();
			actwidth = (actualLastCell - firstCell + 1) * model.getTableSettings().getBoxSizeX();
		}
//		System.out.println("actwidth " + actwidth + " model.getNumberOfBoxesEachRow() " + model.getNumberOfBoxesEachRow() +
//				" boxSizeX " + model.getBoxSizeX());
		if(height>=5){
			String tick = "";
	        double xF = left_margin;
	        double x1 = 0.;
	        double x2 = 0.;
	        double xL = left_margin+actwidth-1;
	        double diff = left_margin-1;
        g2d.drawLine((int)xF, posLine, (int)xL, posLine);

        double div = Math.ceil((double)first_bp/v_small);
        if(div == 0){
        	div = v_small;
        }

        double k = div * v_small;
        	 // draw pos 1
//            if(row == 2){
//            	if(Math.abs((div+1)*v_small-first_bp) >= v_small ){
//            		x1 = diff + gg.getPosition_x(first_bp, actwidth, first_bp, last_bp);
//            		tick = Integer.toString((int)first_bp);
//            		//g2d.drawLine((int) xF, posLine, (int) xF, posLine + hbt);
//            	}
//            	if(height>=10){
//        			g2d.drawString(tick,(int) (x1 - (Math.min(g2d.getFontMetrics().stringWidth(
//    						tick) >> 1, g2d.getFontMetrics()
//    						.stringWidth(tick)
//    						+ Math.abs(width - x1) - 1))),
//    				height-1);
//        		}
//            }
            
            while(k<=last_bp){
            	if(first_bp<=k && k<=last_bp){
            		if(k%v_small==0){
            			if(k%v_big==0){
                        	x1 = diff + DataMapper.getXPosition(k, actwidth, first_bp, last_bp);
                        	if(x1 <= actwidth - right_margin + 1){
                        		tick = Integer.toString((int)k);
                        		g2d.drawLine((int) x1, posLine, (int) x1, posLine + hbt);
                        		if(height>=10){
                        			g2d.drawString(tick,(int) (x1 - (k != tickmarks.size() - 1 ? g2d.getFontMetrics().stringWidth(tick) >> 1:
                    					Math.min(g2d.getFontMetrics().stringWidth(tick) >> 1, g2d.getFontMetrics().stringWidth(tick)
                    										+ Math.abs(width - x1) - 1))),height-1);
                        		}
                        	} else {
                        		break;
                        	}
            			} else{
            				x2 = diff + DataMapper.getXPosition(k, actwidth, first_bp, last_bp) +left_margin;
                			if(x2 <= actwidth - right_margin + 1){
                				g2d.drawLine((int) x2, posLine, (int) x2, posLine + hbts);
                			} else {
                				break;
                			}
            			}
            		}
            	}
            	div++;
            	k = div * v_small;
            }
        }
      }
	 
	protected int computeLastCell(){

		int numberOfCells = (int)model.getTableSettings().getNumberOfNecessaryCells();
		int computedLastCell = lastCell;
		if(lastCell > numberOfCells){
			computedLastCell = numberOfCells;
		}
		return computedLastCell;
	}

	
	protected int compute_bpOfCell_first(int cellNumber){
		int bp_pos = gwcp.execFirstPos(cellNumber, model);
		return bp_pos;
	}
	
	protected int compute_bpOfCell_last(int cellNumber){
		int bp_pos = gwcp.execLastPos(cellNumber, model);
		return bp_pos;
	}
}