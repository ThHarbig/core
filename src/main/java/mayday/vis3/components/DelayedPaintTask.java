package mayday.vis3.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;

import mayday.core.DelayedUpdateTask;
import mayday.core.MaydayDefaults;
import mayday.core.gui.MaydayWindowManager;

public class DelayedPaintTask extends DelayedUpdateTask {

	protected AntiAliasPlotPanel pnl;
	protected int requestID = 0;
	protected int lastStartedID = -1;
	protected int lastFinishedID = -1;
	
	protected BufferedImage buffer;
	protected int w, h;
	protected boolean aa = false;
	
	public DelayedPaintTask(AntiAliasPlotPanel tp, long interval) {
		super("Plot updater", interval);
		pnl = tp;
	}
	
	protected final synchronized boolean needsUpdating() {
		if (lastFinishedID == requestID)
			return false; // all done
		if (lastStartedID == requestID)
			return false; // currently working on the request
		return true;
	}
	
	protected final void performUpdate() {
		synchronized (this) {
			lastStartedID = requestID;
			if (MaydayDefaults.isDebugMode()) {
				Window win = pnl.getOutermostJWindow();			
				System.out.println("Starting request "+lastStartedID+" "+w+"x"+h+(aa?"(antialias)":"           ")+" "+(win!=null?MaydayWindowManager.getTitle(win):""));
			}
		}
		
		if (w>0 && h>0) {
			try {
			// 	render
				BufferedImage _buffer = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
				Graphics2D g2 = _buffer.createGraphics();
				g2.setBackground(Color.white);
				g2.clearRect(0,0, w, h);
				g2.setClip(0,0, w,h);
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				if (aa) {
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				}
				pnl.paintPlot(g2);
				buffer = _buffer;
			} catch (Exception e) {
				lastStartedID = -1;			
				trigger(); // try again later, or with new request ID
				e.printStackTrace();
				return; // no further processing here
			}
		}
		
		synchronized (this) {
			lastFinishedID = lastStartedID;
			if (MaydayDefaults.isDebugMode()) {
				Window win = pnl.getOutermostJWindow();			
				System.out.println("Finished request "+lastStartedID+" "+w+"x"+h+(aa?"(antialias)":"           ")+" "+(win!=null?MaydayWindowManager.getTitle(win):""));
			}
		}
		
		// now update the plot area on screen
		if (lastFinishedID == requestID) // no new request in the meantime
			pnl.updateBuffer(this);
	}
	
	public void trigger(boolean force, int w, int h, boolean antialias) {
		synchronized (this) {
			if (force || this.w!=w || this.h!=h || this.aa != antialias) 
				requestID=lastStartedID+1;
			this.w = w;
			this.h = h;
			this.aa = antialias;
		}
		super.trigger();
	}
	
	public BufferedImage getBuffer() {
		return buffer;
	}
	
}