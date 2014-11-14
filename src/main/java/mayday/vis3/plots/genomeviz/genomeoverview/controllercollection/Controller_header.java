package mayday.vis3.plots.genomeviz.genomeoverview.controllercollection;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import mayday.vis3.plots.genomeviz.EnumManagerGO.Dragged;
import mayday.vis3.plots.genomeviz.EnumManagerGO.Fixed;
import mayday.vis3.plots.genomeviz.EnumManagerGO.SizeMode;
import mayday.vis3.plots.genomeviz.EnumManagerGO.Zoom;
import mayday.vis3.plots.genomeviz.delegates.HelperClass;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.LayeredPane_Operations;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.OperationsForScalaSelection;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.ChromosomeHeaderPanel;

public class Controller_header extends AbstractController implements MouseListener, MouseMotionListener {

	protected int visLowPos_new_x = 0;
	protected int visHighPos_new_x = 0;
	
	protected double visLowPos_new_bp = 0.;
	protected double visHighPos_new_bp = 0.;
	
	protected boolean draggedflag = false;
	double newWidthLayeredPane = 0;
	protected Dragged dragged = null;
	protected Fixed fixed = null;
	
	public Controller_header(GenomeOverviewModel chromeModel,Controller c){
		super(chromeModel, c);
	}

	
	public void mouseClicked(MouseEvent e) {
		
		if(e.getSource() instanceof ChromosomeHeaderPanel){
			
			boolean changeNecessary = false;

			// get actual width of visible range
			double visLowPos_bp = model.getVisPos_low_bp();
			double visHighPos_bp = model.getVisPos_high_bp();
			
			// get actual range
			double range_bp = OperationsForScalaSelection.getRangeOfVisible_BP(visLowPos_bp, visHighPos_bp);
			
			getBPPosition(e, ftp);
			// get the middle position where clicked
			double clickedPos_bp = OperationsForScalaSelection.getClickedPositionInHeader(ftp);
			
			if(ftp.isValid()){
				
				if(!e.isControlDown()){
					computeNewVisibleRange_bp(clickedPos_bp, range_bp);
					model.getSelectionModel().setWantedVisiblePositions(visLowPos_new_bp,visHighPos_new_bp);
					model.getSelectionModel().setZooming(Zoom.ZOOM_NOT);
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							model.repositionVisibleRect_mouseClicked(null);
						}
					});
					
				} else if(e.isControlDown()){
					double leftDist_bp = clickedPos_bp - visLowPos_bp;
					double rightDist_bp = clickedPos_bp - visHighPos_bp;
					// change the right border
					if(Math.abs(leftDist_bp) >  Math.abs(rightDist_bp)){
						changeNecessary = true;
						
						visLowPos_new_bp = visLowPos_bp;
						visHighPos_new_bp = clickedPos_bp;
						if(visHighPos_new_bp > visHighPos_bp){
							fixed = Fixed.LEFT_FIXED_RIGHT_BIGGER;
						} else if(visHighPos_new_bp < visHighPos_bp){
							fixed = Fixed.LEFT_FIXED_RIGHT_SMALLER;
						}
					} 
					// change the left border
					else if(Math.abs(leftDist_bp) <  Math.abs(rightDist_bp)){
						changeNecessary = true;
						
						visLowPos_new_bp = clickedPos_bp;
						visHighPos_new_bp = visHighPos_bp;
						if(visLowPos_new_bp > visLowPos_bp){
							fixed = Fixed.RIGHT_FIXED_LEFT_SMALLER;
						} else if(visLowPos_new_bp < visLowPos_bp){
							fixed = Fixed.RIGHT_FIXED_LEFT_BIGGER;
						}
					}

					if(fixed!=null){
						newWidthLayeredPane = LayeredPane_Operations.computeNewWidthOfLayeredPane(visLowPos_new_bp, visHighPos_new_bp, model);
						
						if(changeNecessary && newWidthLayeredPane>0.){
							model.resizeLayeredPane((int)visLowPos_new_bp,(int)visHighPos_new_bp,
									(int)newWidthLayeredPane,SizeMode.SIZE_ZOOM);
						}
						
						SwingUtilities.invokeLater(new Runnable(){
							public void run(){
								model.repositionVisibleRect_mouseClicked(fixed);
							}
						});
					}		
					changeNecessary = false;
				}
			}
		}
	}
	
	/**
	 * compute new visible wanted range of bp.
	 * @param selMiddlePos_bp
	 * @param range_bp
	 */
	private void computeNewVisibleRange_bp(double selMiddlePos_bp,
			double range_bp) {

		// compute new visible range and visible positions
		if(range_bp%2==0){
			visLowPos_new_bp = selMiddlePos_bp - (range_bp/2.)+1;
			visHighPos_new_bp = selMiddlePos_bp + (range_bp/2.);
			
		} else{
			double halfrange = Math.floor(range_bp/2.);
			visLowPos_new_bp = selMiddlePos_bp - halfrange;
			visHighPos_new_bp = selMiddlePos_bp + halfrange;
		}
		
		if(visLowPos_new_bp < model.getChromosomeStart()){
			visLowPos_new_bp = model.getChromosomeStart();
			visHighPos_new_bp = visLowPos_new_bp + range_bp -1; // -1 because of range
		} else if(visHighPos_new_bp > model.getChromosomeEnd()){
			visHighPos_new_bp = model.getChromosomeEnd();
			visLowPos_new_bp = visHighPos_new_bp - range_bp +1; // +1 because of range
		}
		
		if((visHighPos_new_bp - visLowPos_new_bp +1) != range_bp){
			System.err.println("Range not valid");
		}
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	
	public void mouseExited(MouseEvent e) {
		
	}

	
	public void mousePressed(MouseEvent e) {
		if(e.getSource() instanceof ChromosomeHeaderPanel){
			visLowPos_new_x = visHighPos_new_x = e.getX();
			getBPPosition(e, first);
			ChromosomeHeaderPanel chp = (ChromosomeHeaderPanel)e.getSource();
			chp.drawLine(true);
		}	
	}

	
	public void mouseReleased(MouseEvent e) {

		if (e.getSource() instanceof ChromosomeHeaderPanel) {
			ChromosomeHeaderPanel chp = (ChromosomeHeaderPanel) e.getSource();
			chp.drawLine(false);

			if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0
					&& (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0) {
				if (draggedflag) {

					getBPPosition(e, last);

					HelperClass.getRangeOfClickedPosition(result_arry_dbl, first, last, model);

					visLowPos_new_bp = result_arry_dbl[0];
					visHighPos_new_bp = result_arry_dbl[1];
					
					newWidthLayeredPane = LayeredPane_Operations
							.computeNewWidthOfLayeredPane(visLowPos_new_bp,
									visHighPos_new_bp, model);

					if (newWidthLayeredPane > 0.) {
						model.resizeLayeredPane((int) visLowPos_new_bp,
								(int) visHighPos_new_bp,
								(int) newWidthLayeredPane, SizeMode.SIZE_ZOOM);
					} else {
						System.out.println("Width not valid");
					}

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							model.repositionVisibleRect_mouseClicked(null);
						}
					});
					//					} 
					visHighPos_new_x = e.getX();
				}
				draggedflag = false;
			}
		}
	}


	// Mouse motion listener
	
	public void mouseDragged(MouseEvent e) {
		if(e.getSource() instanceof ChromosomeHeaderPanel){
			
			ChromosomeHeaderPanel chp = (ChromosomeHeaderPanel)e.getSource();
			chp.drawLine(true);
			
			if((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0 
					&& (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == 0){
				visHighPos_new_x = e.getX();
				draggedflag = true;
			}
		}		
	}


	
	public void mouseMoved(MouseEvent e) {
		if(e.getSource() instanceof ChromosomeHeaderPanel){
			ChromosomeHeaderPanel panel = (ChromosomeHeaderPanel)e.getSource();
			
			getBPPosition(e, ftp);
			
			if(ftp.isValid())panel.setToolTipText("location: " + ftp.getTooltiptext_bp());
			else panel.setToolTipText(null);
		}
	}

	
	/**
	 * computes the bp (from-to) of clicked- or mouse over position.
	 * @param e
	 */
	protected void getBPPosition(MouseEvent e, FromToPosition ftp){
		ChromosomeHeaderPanel panel = (ChromosomeHeaderPanel)e.getSource();
		DataMapper.getSelectedPosition(panel.getWidth(), panel.getLeft_margin(), panel.getRight_margin(), e.getX(), ftp, model);
	}

	public boolean isDraggedflag() {
		return draggedflag;
	}


	public int getEndDragged() {
		return visHighPos_new_x;
	}


	public int getStartDragged() {
		return visLowPos_new_x;
	}
}
