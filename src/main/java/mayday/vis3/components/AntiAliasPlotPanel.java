package mayday.vis3.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.RepaintManager;

import mayday.core.gui.MaydayWindowManager;
import mayday.vis3.gui.PlotComponent;


/** 
 * This is the container for all Graphics2D-based plots. It adapts our plots 
 * to the Java Swing Framework for inclusion in layouted components such as
 * JPanels, JSplitPanes etc.
 * Subclasses must implement setup() and, if needed, can add KeyListeners,
 * MouseListeners, MouseMotionListeners and MouseWheelListeners directly to the
 * AntiAliasPlotPanel (themselves, so to speak) to provide user interaction.
 * Menu items such as Enhancements and Plot-specific settings can be added
 * via the plotContainer reference in setup().
 * Anti-Aliasing is handled automatically in a second thread.
 */

@SuppressWarnings("serial")
public abstract class AntiAliasPlotPanel extends BasicPlotPanel implements PlotComponent {

	
	protected final static boolean USE_DELAYED_ANTIALIASING = false; // currently disabled due to problems with the plotting library
	protected final static boolean SHOW_DEBUG_INFO = false; 
	
	protected BufferedImage buffer;
	protected BufferedImage antialias_buffer;
	
	protected DelayedPaintTask basicRenderTask;
	protected DelayedPaintTask antialiasRenderTask;
	
	public AntiAliasPlotPanel() {
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.white);
		g2.clearRect(0,0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		if (isExporting() || isTooLarge()) {
			paint_directly(g2, true);
		} else {			
			// use valid buffers
			// first look for antialiased image
			if (isValidBuffer(antialias_buffer)) {
				if (SHOW_DEBUG_INFO)
					System.out.println("Painting AA Buffer");
				paint_from_buffer(g2, antialias_buffer);
			} else {
				// secondly, try default buffer
				antialias_buffer = null;

				if (isValidBuffer(buffer)) {
					if (SHOW_DEBUG_INFO)
						System.out.println("Painting Buffer");
					paint_from_buffer(g2, buffer);
				} else {
					// try painting a stretched version of the old buffer
					if (buffer!=null) {
						try {
							if (SHOW_DEBUG_INFO)
								System.out.println("Painting stretched");
							paint_from_buffer(g2, buffer.getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_FAST));
						} catch (Exception e) {
							// apparently we can't use the buffer for the current size
							buffer = null;
							repaint();
						}
					}
					// else nothing is painted for now, but we return as soon as the new bufefr is done					
					triggerBasicBufferUpdate();
				}
				
			}
		}
	}
	
	protected void markUpdating(boolean updating) {
		Window w = getOutermostJWindow();
		if (w!=null) {
			String oldTitle = MaydayWindowManager.getTitle(w);
			oldTitle = oldTitle.replace("(updating...) ", "");
			if (updating)
				oldTitle = "(updating...) "+oldTitle;
			MaydayWindowManager.setTitle(w, oldTitle);
		}

	}
	
	protected void triggerBasicBufferUpdate() {
		if (basicRenderTask == null)
			basicRenderTask = new DelayedPaintTask(this, 50);
		if (!isTooLarge()) {
			markUpdating(true);
			basicRenderTask.trigger(false, getWidth(), getHeight(), false);
		}
	}
	
	protected void triggerAntiAliasBufferUpdate() {
//		if (!USE_DELAYED_ANTIALIASING)
//			return; 
//		if (antialiasRenderTask == null)
//			antialiasRenderTask = new DelayedPaintTask(this,1000);
//		if (!isTooLarge())
//			antialiasRenderTask.trigger(antialias_buffer==null, getWidth(), getHeight(), true);
	}
	
	public void updateBuffer(DelayedPaintTask task) {
		if (task == basicRenderTask) {
			buffer = task.getBuffer();	
			triggerAntiAliasBufferUpdate();
		}
		else if (task == antialiasRenderTask)
			antialias_buffer = task.getBuffer();
		repaint();
		markUpdating(false);
	}

	protected boolean isTooLarge() {
		// BufferedImage crashes around 20k x 20k. So we limit the maximal buffer size
		return (getWidth()>50000) || (getHeight()>50000) || 
			((long)getWidth()*(long)getHeight() > (long)(20000*20000));
	}
	
	protected boolean isExporting() {
		RepaintManager rema = RepaintManager.currentManager(this);
		return !rema.isDoubleBufferingEnabled();
	}
	
	protected boolean isValidBuffer(BufferedImage buffer) {
		return buffer!=null && buffer.getWidth()==getWidth() && buffer.getHeight()==getHeight();
	}
		
	public void updatePlot() {
		antialias_buffer=null;
//		buffer=null;
		revalidate();
		if (basicRenderTask!=null) {
			markUpdating(true);
			basicRenderTask.trigger(true, getWidth(), getHeight(), false);			
		}
		repaint();
	}
	
	protected void paint_directly(Graphics2D g, boolean overrideTask) {
//		if (USE_DELAYED_ANTIALIASING && overrideTask) {
//			aaRenderTask.killIfRunning();
//		}
		if (SHOW_DEBUG_INFO)
			System.out.println("Paint direct");
		paintPlot(g);
	}
	
//	protected void paint_antialiased(Graphics2D g) {
//		
//		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);		
//		try {
//			paint_directly(g, false);
//		} catch (Exception e) {
//			antialias_buffer = null;
//			if (USE_DELAYED_ANTIALIASING) {
//				aaRenderTask.trigger();
//			}
//		}
//	}
	
	protected void paint_from_buffer(Graphics2D g, Image buffer) {
		g.drawImage(buffer,0,0,getWidth(),getHeight(),null);
	}

	
	public abstract void paintPlot(Graphics2D g);
	

//	public interface LocklessPainter {}; 
		
}
